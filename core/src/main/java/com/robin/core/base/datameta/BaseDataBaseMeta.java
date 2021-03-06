/*
 * Copyright (c) 2015,robinjim(robinjim@126.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.robin.core.base.datameta;

import com.robin.core.convert.util.ConvertUtil;
import com.robin.core.sql.util.BaseSqlGen;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
@Slf4j
public abstract class BaseDataBaseMeta implements DataBaseInterface, Serializable {
	//static fields
	public static final String TYPE_MYSQL="MySql";
	public static final String TYPE_ORACLE="Oracle";
	public static final String TYPE_ORACLERAC="OracleRac";
	public static final String TYPE_DB2="DB2";
	public static final String TYPE_SYBASE="Sybase";
	public static final String TYPE_SQLSERVER="SqlServer";
	public static final String TYPE_H2="H2";
	public static final String TYPE_DEBRY="Debry";
	public static final String TYPE_PGSQL="PostgreSql";
	public static final String TYPE_PHONEIX="Phoenix4";
	public static final String TYPE_HIVE="Hive";
	public static final String TYPE_HIVE2="Hive2";
	public static final String TYPE_IMPALA ="Impala";
	protected DataBaseParam param;
	protected String dbType;
	//Enum type of all support DB
	public static final String[] DB_TYPE_ENMU ={"Oracle","MySql","DB2","SqlServer","Sybase","PostgreSql","Phoenix4","Hive","Hive2","OracleRac"};
	//jdbc Url Template like jdbc:mysql://[hostName]:[port]/[databaseName]?useUnicode=true&characterEncoding=[encode]
	public static final Pattern PATTERN_TEMPLATE_PARAM = Pattern.compile("\\[.*?\\]");
	@Override
	public List<DataBaseTableMeta> listAllTable(String schema) throws Exception {
		DataBaseUtil util=new DataBaseUtil();
		util.connect(this);
		List<DataBaseTableMeta> list=util.getAllTable(schema, this);
		util.closeConnection();
		return list;
	}
	@Override
	public String getUrl(){

		return param.getUrlByMeta(this);
	}
	protected void processParam(Map<String,String> map) throws Exception{
		ConvertUtil.objectToMap(map, param);
	}
	
	public BaseDataBaseMeta(DataBaseParam param){
		if(param!=null){
			this.param=param;
			if(param.getUrlTemplate()==null || param.getUrlTemplate().isEmpty()){
				param.setUrlTemplate(getUrlTemplate());
			}
		}
	}
	@Override
	public String getSQLNextSequenceValue(String sequenceName) {
		return null;
	}

	@Override
	public String getSQLCurrentSequenceValue(String sequenceName) {
		return null;
	}
	@Override
	public DataBaseParam getParam(){
		return this.param;
	}

	@Override
	public String getSQLSequenceExists(String sequenceName) {
		return null;
	}
	public abstract BaseSqlGen getSqlGen();
	@Override
	public boolean equals(Object obj) {
		boolean isequal=false;
		if(obj instanceof BaseDataBaseMeta){
			BaseDataBaseMeta compareObj=(BaseDataBaseMeta) obj;
			if(param.getDriverClassName().equals(param.getDriverClassName()) && this.getParam().equals(compareObj.getParam())){
				isequal=true;
			}
		}
		return isequal;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String getCatalog(String schema) {
		return null;
	}
	public String getDbType(){
		return dbType;
	}
	public void setDbType(String dbType){
		this.dbType=dbType;
	}
	public String getCreateExtension(){
		return "";
	}
}
