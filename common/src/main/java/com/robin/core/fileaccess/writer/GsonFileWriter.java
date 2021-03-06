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
package com.robin.core.fileaccess.writer;

import com.google.gson.stream.JsonWriter;
import com.robin.core.fileaccess.meta.DataCollectionMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GsonFileWriter extends WriterBasedFileWriter{
	private JsonWriter jwriter=null;
	private Logger logger=LoggerFactory.getLogger(getClass());
	public GsonFileWriter(DataCollectionMeta colmeta) {
		super(colmeta);
	}

	@Override
	public void beginWrite() throws IOException{
		jwriter=new JsonWriter(writer);
		jwriter.beginArray();
	}

	@Override
	public void writeRecord(Map<String, ?> map) throws IOException, OperationNotSupportedException {
		try{
			jwriter.beginObject();
			for (int i = 0; i < colmeta.getColumnList().size(); i++) {
				String name = colmeta.getColumnList().get(i).getColumnName();
				String value=getOutputStringByType(map,name);
				if(value!=null){
					jwriter.name(name).value(value);
				}
			}
			jwriter.endObject();
		}catch(Exception ex){
			logger.error("",ex);
		}
	}


	@Override
	public void finishWrite() throws IOException{
		jwriter.endArray();
		jwriter.close();
	}

	@Override
	public void flush() throws IOException{
		jwriter.flush();
	}
	@Override
    public void close() throws IOException{
		writer.close();
	}
	
}
