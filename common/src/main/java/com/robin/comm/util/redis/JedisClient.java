package com.robin.comm.util.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robin.comm.util.json.GsonUtil;
import com.robin.core.base.reflect.ReflectUtils;
import com.robin.core.fileaccess.util.AvroUtils;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.Map.Entry;

public class JedisClient {

    // private static Jedis jedis=null;
    private JedisPool pool = null;
    private static JedisClient client;
    private int dbindex = 0;
    private Gson gson = GsonUtil.getGson();

    public JedisClient() {


    }

    public static JedisClient getInstance(String initName) {
        if (client == null) {
            synchronized (JedisClient.class) {
                if (client == null) {
                    client = new JedisClient();
                    String initPropName = (initName == null || initName.equals("")) ? "jedisconfig" : initName;
                    client.init(initPropName);
                }
            }
        }
        return client;
    }


    public void init(String propertiesName) {
        ResourceBundle bundle = ResourceBundle.getBundle(propertiesName);
        String ip = bundle.getString("IPADDRESS");
        int port = Integer.parseInt(bundle.getString("PORT"));
        String passwd = null;
		/*if(bundle.containsKey("PASSWORD"))
			passwd=bundle.getString("PASSWORD");
		if(bundle.containsKey("DBINDEX")){
			dbindex=Integer.parseInt(bundle.getString("DBINDEX"));
		}*/
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(20);
        config.setMaxWaitMillis(100000l);
        if (passwd == null || passwd.equals(""))
            pool = new JedisPool(config, ip, port, 100000);
        else {
            pool = new JedisPool(config, ip, port, 100000, passwd);
        }
    }

    public void putValue(String key, Object obj, Integer expireSecond) {
        Jedis jedis = pool.getResource();
        if (dbindex != 0)
            jedis.select(dbindex);
        putValue(jedis, key, obj, expireSecond);
    }

    private void putValue(Jedis jedis, String key, Object obj, Integer expireSecond) {
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            Map<String, String> map1 = new HashMap<String, String>();
            Iterator<String> iter = map.keySet().iterator();
            while (iter.hasNext()) {
                String keycol = iter.next();
                Object tobj = map.get(keycol);
                String valstr = "";
                if (null != tobj) {
                    valstr = gson.toJson(tobj);
                }
                map1.put(keycol, valstr);
            }

            jedis.hmset(key, map1);
        } else if (obj instanceof List) {
            List list = (List) obj;
            for (Object str : list) {
                if (str instanceof String) {
                    jedis.lpush(key, str.toString());
                } else {
                    String valstr = gson.toJson(str);
                    jedis.lpush(key, valstr);
                }
            }
        } else if (obj instanceof String) {
            jedis.set(key, obj.toString());
        } else {
            jedis.set(key, obj.toString());
        }
        if (expireSecond != null && !expireSecond.equals(-1))
            jedis.expire(key, expireSecond);
        close(jedis);
    }

    public void putValue(String key, Object obj, int dbIndex, Integer expireSecond) {
        Jedis jedis = pool.getResource();
        jedis.select(dbIndex);
        putValue(jedis, key, obj, expireSecond);
    }


    public void putSerializableObject(String key, Object value,
                                      Integer expireSecond) {
        ByteArrayOutputStream arrayOutputStream = null;
        ObjectOutputStream outputStream = null;
        try {
            arrayOutputStream = new ByteArrayOutputStream();
            outputStream = new ObjectOutputStream(arrayOutputStream);
            outputStream.writeObject(value);
            Jedis jedis = getJedis();
            jedis.set(key.getBytes(), arrayOutputStream.toByteArray());
            if (expireSecond != null && !expireSecond.equals(-1))
                jedis.expire(key, expireSecond);
            close(jedis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                arrayOutputStream.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Object getSerializableObject(String key, Class<?> clazz) {
        ObjectInputStream arrayin = null;
        ByteArrayInputStream in = null;
        Object obj = null;
        try {
            Jedis jedis = getJedis();
            in = new ByteArrayInputStream(jedis.get(key.getBytes()));
            arrayin = new ObjectInputStream(in);
            Object tmpobj = arrayin.readObject();
            close(jedis);
            obj = clazz.cast(tmpobj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void putPlainSet(String key, String... value) {
        Jedis jedis = getJedis();
        jedis.sadd(key, value);
        close(jedis);
    }
    public void putSetWithSchema(String key, List<? extends Serializable> valueObject){
        Assert.notEmpty(valueObject,"array is null");
        Map<String,Method> getMethods= ReflectUtils.returnGetMethods(valueObject.get(0).getClass());
        Schema schema=AvroUtils.getSchemaFromModel(valueObject.get(0).getClass());

        SchemaBuilder.FieldAssembler<Schema> assembler= SchemaBuilder.record("Nested").fields();
        assembler.name("list").type().nullable().array().items(schema).noDefault();
        Schema nestedSchema=assembler.endRecord();
        try {
            GenericRecord rd=new GenericData.Record(nestedSchema);
            List<GenericRecord> retList=new ArrayList<>();
            for (Serializable obj : valueObject) {
                GenericRecord genericRecord = new GenericData.Record(schema);
                Iterator<Map.Entry<String, Method>> iter = getMethods.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Method> entry = iter.next();
                    genericRecord.put(entry.getKey(), retriveModelData(entry.getKey(),entry.getValue().invoke(obj, null),schema));
                }
                retList.add(genericRecord);
            }
            rd.put("list",retList);
            byte[] bytes=AvroUtils.dataToByteArray(nestedSchema,rd);
            byte[] bytes1=AvroUtils.dataToByteWithBijection(nestedSchema,rd);
            System.out.println(bytes.length);
            System.out.println(bytes1.length);
            GenericRecord record=AvroUtils.parse(nestedSchema,bytes);
            System.out.println(record);
            //getJedis().sadd(key.getBytes(),AvroUtils.dataToByteWithBijection(nestedSchema,))
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private Object retriveModelData(String key,Object value,Schema schema){
        try {
            if(value!=null) {
                if (value.getClass().isAssignableFrom(Serializable.class)) {
                    GenericRecord record = new GenericData.Record(schema.getField(key).schema());
                    Schema modelschema = schema.getField(key).schema();
                    Map<String, Method> getMethods = ReflectUtils.returnGetMethods(value.getClass());
                    Iterator<Map.Entry<String, Method>> iter = getMethods.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, Method> entry = iter.next();
                        if (modelschema.getField(entry.getKey()) != null) {
                            record.put(entry.getKey(), entry.getValue().invoke(value, null));
                        }
                    }
                    return record;
                }else if(value instanceof List){
                    List<GenericRecord> records=new ArrayList<>();
                    List<?> list=(List<?>) value;
                    if(CollectionUtils.isEmpty(list)){
                        return null;
                    }
                    Map<String, Method> getMethods = ReflectUtils.returnGetMethods(list.get(0).getClass());
                    Schema eleType=schema.getField(key).schema().getTypes().get(0).getElementType();
                    for(Object t:list){
                        GenericRecord record=new GenericData.Record(eleType);
                        Iterator<Map.Entry<String, Method>> iter = getMethods.entrySet().iterator();
                        while(iter.hasNext()){
                            Map.Entry<String, Method> entry = iter.next();
                            record.put(entry.getKey(), retriveModelData(entry.getKey(),entry.getValue().invoke(t, null),eleType));
                        }
                        records.add(record);
                    }
                    return records;
                }
                else if(value.getClass().isAssignableFrom(Date.class)){
                    return ((Date) value).getTime();
                }else if(value.getClass().isAssignableFrom(Timestamp.class)){
                    return ((Timestamp) value).getTime();
                }else if(value.getClass().isAssignableFrom(LocalDateTime.class)){
                    return ((LocalDateTime) value).toInstant(ZoneOffset.of("+8")).toEpochMilli();
                }
                else {
                    return value;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    private Jedis getJedis(){
        Jedis jedis = pool.getResource();
        if (dbindex != 0)
            jedis.select(dbindex);
        return jedis;
    }

    public void putPlainSet(String key, List<?> valueList) {
        Jedis jedis = pool.getResource();
        if (dbindex != 0)
            jedis.select(dbindex);
        byte[][] byteArr = new byte[valueList.size()][];
        for (int i = 0; i < valueList.size(); i++) {
            try {
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream outputStream = new ObjectOutputStream(
                        arrayOutputStream);
                outputStream.writeObject(valueList.get(i));
                byteArr[i] = arrayOutputStream.toByteArray();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        jedis.sadd(key.getBytes(), byteArr);
        close(jedis);
    }

    public void rmPlainSet(String key, String value) {
        Jedis jedis = pool.getResource();
        if (dbindex != 0)
            jedis.select(dbindex);
        jedis.srem(key, value);
        close(jedis);
    }

    public Set<String> getPlainSet(String key) {
        Jedis jedis = pool.getResource();
        if (dbindex != 0)
            jedis.select(dbindex);
        Set<String> retSet = jedis.smembers(key);
        close(jedis);
        return retSet;
    }

    public List getPlainSetWithObj(String key, Class<?> clazz) {
        List list = new ArrayList();
        Jedis jedis = pool.getResource();
        if (dbindex != 0)
            jedis.select(dbindex);
        Set<byte[]> retSet = jedis.smembers(key.getBytes());
        Iterator<byte[]> it = retSet.iterator();
        while (it.hasNext()) {
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(jedis.get(key.getBytes()));
                ObjectInputStream arrayin = new ObjectInputStream(in);
                Object tmpobj = arrayin.readObject();
                list.add(clazz.cast(tmpobj));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        close(jedis);
        return list;
    }

    public boolean isKeyExists(String key) {
        Jedis jedis = pool.getResource();
        if (dbindex != 0)
            jedis.select(dbindex);
        boolean isExists = jedis.exists(key);
        String val = jedis.get(key);
        if (val == null || "".equals(val))
            isExists = false;
        close(jedis);
        return isExists;
    }

    public void clearValue(String key) {
        Jedis jedis = pool.getResource();
        if (dbindex != 0)
            jedis.select(dbindex);
        jedis.del(key);
        close(jedis);
    }

    public String getPlainValue(String key) {
        Jedis jedis = pool.getResource();
        if (dbindex != 0)
            jedis.select(dbindex);
        String str = jedis.get(key);
        close(jedis);
        return str;
    }

    public String getPlainValue(String key, int dbIndex) {
        Jedis jedis = pool.getResource();
        jedis.select(dbIndex);
        String str = jedis.get(key);
        close(jedis);
        return str;
    }

    public String flushDB(int dbIndex) {
        Jedis jedis = pool.getResource();
        jedis.select(dbIndex);
        String str = jedis.flushDB();
        close(jedis);
        return str;
    }

    public String flushAll() {
        Jedis jedis = pool.getResource();
        String str = jedis.flushAll();
        close(jedis);
        return str;
    }


    public void lpush(String key, String value) {
        Jedis jedis = pool.getResource();
        jedis.lpush(key, value);
        close(jedis);
    }

    public void rpush(String key, String value) {
        Jedis jedis = pool.getResource();
        jedis.rpush(key, value);
        close(jedis);
    }

    public void hmset(String key, Map<String, ?> map) {
        Jedis jedis = pool.getResource();
        try {
            Iterator<?> iterator = map.entrySet().iterator();
            Object tmpobj = null;
            if (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                tmpobj = entry.getValue();
            }
            if (tmpobj instanceof String) {
                jedis.hmset(key, (Map<String, String>) map);
            } else if (tmpobj != null && isWrapClass(tmpobj.getClass())) {
                Iterator<String> it = map.keySet().iterator();
                Map<String, String> map1 = new HashMap<String, String>();
                while (it.hasNext()) {
                    String key1 = it.next();
                    map1.put(key1, map.get(key1).toString());
                }
                jedis.hmset(key, map1);
            } else {
                Iterator<String> it = map.keySet().iterator();
                Map<byte[], byte[]> tmpmap = new HashMap<byte[], byte[]>();
                while (it.hasNext()) {
                    String key1 = it.next();
                    byte[] tmpbyte = SerializeObject(map.get(key1));
                    if (tmpbyte != null) {
                        tmpmap.put(key1.getBytes(), tmpbyte);
                    }
                }
                jedis.hmset(key.getBytes(), tmpmap);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            close(jedis);
        }
    }

    public List<?> hmget(String key, String[] fields, Class targetClass) {
        Jedis jedis = pool.getResource();
        try {
            Object tmpobj = targetClass.newInstance();

            if (tmpobj instanceof String) {
                List<String> list1 = jedis.hmget(key, fields);
                return list1;
            } else if (isWrapClass(targetClass)) {
                List<String> list1 = jedis.hmget(key, fields);
                List retList = new ArrayList();
                Method method = targetClass.getDeclaredMethod("valueOf", String.class);
                for (int i = 0; i < list1.size(); i++) {
                    retList.add(method.invoke(tmpobj, list1.get(i)));
                }
                return retList;
            } else {
                List retList = new ArrayList();
                byte[][] bt1 = new byte[fields.length][];
                for (int i = 0; i < fields.length; i++) {
                    bt1[i] = fields[i].getBytes();
                }
                List<byte[]> list2 = jedis.hmget(key.getBytes(), bt1);
                for (int i = 0; i < list2.size(); i++) {
                    retList.add(DeSerializeObject(list2.get(i)));
                }
                return list2;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void lpush(String key, List<?> valueList) {
        Jedis jedis = pool.getResource();
        for (Object value : valueList) {
            if (value instanceof String) {
                jedis.lpush(key, value.toString());
            } else if (isWrapClass(value.getClass())) {
                jedis.lpush(key, value.toString());
            } else {
                try {
                    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream outputStream = new ObjectOutputStream(
                            arrayOutputStream);
                    outputStream.writeObject(value);
                    jedis.lpush(key.getBytes(), arrayOutputStream.toByteArray());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        close(jedis);
    }

    public void rpush(String key, List<?> valueList) {
        Jedis jedis = pool.getResource();
        for (Object value : valueList) {
            if (value instanceof String)
                jedis.lpush(key, value.toString());
            else if (isWrapClass(value.getClass())) {
                jedis.lpush(key, value.toString());
            } else {
                try {
                    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream outputStream = new ObjectOutputStream(
                            arrayOutputStream);
                    outputStream.writeObject(value);
                    jedis.rpush(key.getBytes(), arrayOutputStream.toByteArray());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        close(jedis);
    }

    public List<String> lrange(String key, int start, int end) {
        Jedis jedis = pool.getResource();
        if (end == 0)
            end = -1;
        List<String> retList = jedis.lrange(key, start, end);
        close(jedis);
        return retList;
    }

    public List<Object> lrangeEntity(String key, int start, int end) {
        Jedis jedis = pool.getResource();
        List<Object> list = new ArrayList<Object>();
        if (end == 0)
            end = -1;
        List<byte[]> retList = jedis.lrange(key.getBytes(), start, end);
        for (byte[] str : retList) {
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(str);
                ObjectInputStream arrayin = new ObjectInputStream(in);
                Object tmpobj = arrayin.readObject();
                list.add(tmpobj);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        close(jedis);
        return list;
    }

    public String lpop(String key) {
        Jedis jedis = pool.getResource();
        String retStr = jedis.lpop(key);
        close(jedis);
        return retStr;
    }

    public void incr(String key) {
        Jedis jedis = pool.getResource();
        jedis.incr(key);
        close(jedis);
    }

    public void incrBy(String key, Long value) {
        Jedis jedis = pool.getResource();
        jedis.incrBy(key, value);
        close(jedis);
    }

    public void decr(String key) {
        Jedis jedis = pool.getResource();
        jedis.decr(key);
        close(jedis);
    }

    public void decrBy(String key, Long value) {
        Jedis jedis = pool.getResource();
        jedis.decrBy(key, value);
        close(jedis);
    }



    public Object getValue(String key,
                           Class clazz,Class... targetClassArr) {
        Object retObj = null;
        Jedis jedis = pool.getResource();
        try {
            if (clazz.getClass().isAssignableFrom(Map.class)) {
                Map<String, String> map = jedis.hgetAll(key);
                retObj = map;
                Map<String, Object> map1 = new HashMap<String, Object>();
                Iterator<String> iter = map.keySet().iterator();
                while (iter.hasNext()) {
                    String keycol = iter.next();
                    String valStr = map.get(keycol);
                    if (valStr.startsWith("[")) {
                        // list
                        List<Map<String, Object>> tlist = gson.fromJson(valStr, new TypeToken<List<Map<String, Object>>>() {
                        }.getType());
                        map1.put(keycol, tlist);
                    } else if (valStr.startsWith("{")) {
                        // Object
                        Map<String, Object> tmap = gson.fromJson(valStr, new TypeToken<Map<String, Object>>() {
                        }.getType());
                        map1.put(keycol, tmap);
                    }
                    retObj = map1;
                }

            } else if (clazz.isAssignableFrom(List.class)) {
                List<Object> objList = new ArrayList<Object>();
                List<String> listobj = jedis.lrange(key, 0, -1);
                for(String str:listobj){
                    if(targetClassArr.length>0) {
                        if (str.startsWith("[")) {
                            List<?> list = gson.fromJson(str, TypeToken.getParameterized(ArrayList.class, targetClassArr[0]).getType());
                            retObj = list;
                        } else if (str.startsWith("{")) {
                            retObj = gson.fromJson(str, TypeToken.get(targetClassArr[0]).getType());
                        }
                    }else{
                        if (str.startsWith("[")) {
                            List<Map<String,Object>> list = gson.fromJson(str, new TypeToken<List<Map<String,Object>>>(){}.getType());
                            retObj = list;
                        } else if (str.startsWith("{")) {
                            retObj = gson.fromJson(str, new TypeToken<Map<String,Object>>(){}.getType());
                        }
                    }
                }
            } else if (clazz.isAssignableFrom(String.class))
                retObj = jedis.get(key);
            else {
                String val = jedis.get(key);
                retObj=gson.fromJson(val,TypeToken.get(clazz).getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        close(jedis);
        return retObj;
    }

    
    private byte[] SerializeObject(Object obj) {
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    arrayOutputStream);
            outputStream.writeObject(obj);
            byte[] byteArr = arrayOutputStream.toByteArray();
            return byteArr;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Object DeSerializeObject(byte[] bytes) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream arrayin = new ObjectInputStream(in);
            Object tmpobj = arrayin.readObject();
            return tmpobj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    private void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
