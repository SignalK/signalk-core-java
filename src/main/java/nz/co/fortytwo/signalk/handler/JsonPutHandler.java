/*
 * 
 * Copyright (C) 2012-2014 R T Huitema. All Rights Reserved.
 * Web: www.42.co.nz
 * Email: robert@42.co.nz
 * Author: R T Huitema
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nz.co.fortytwo.signalk.handler;

import static nz.co.fortytwo.signalk.util.Constants.*;
import static nz.co.fortytwo.signalk.util.JsonConstants.*;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeTypeParameterList;
import javax.activation.MimetypesFileTypeMap;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Handles json messages with 'get' requests
 * 
 * @author robert
 * 
 */
public class JsonPutHandler extends DeltaToMapConverter{

	private static Logger logger = Logger.getLogger(JsonPutHandler.class);
	private File storageDir = new File(Util.getConfigProperty(STORAGE_ROOT));
	private Map<String, String> mimeMap = new HashMap<String, String>();
	/**
	 * Processes the putNode, saving into the signalKModel
	 * Returns null if the json is not an update, otherwise return a SignalKModel
	 * 
	 * @param signalkModel
	 * @param putNode
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 * 
	 */
	public JsonPutHandler() throws IOException{
		@SuppressWarnings("unchecked")
		List<String> lines = FileUtils.readLines(new File("./src/main/resources/mime.types"));
		for(String line: lines){
			String[] parts=line.split("=");
			mimeMap.put(parts[0], parts[1]);
		}
	}
	@Override
	public SignalKModel  handle(Json node) throws Exception {
		//avoid full signalk syntax
		if(node.has(VESSELS))return null;
		//deal with diff format
		if(node.has(CONTEXT)){
			if(logger.isDebugEnabled())logger.debug("processing put  "+node );
			//process it
			SignalKModel temp =  SignalKModelFactory.getCleanInstance();
			
			//go to context
			String ctx = node.at(CONTEXT).asString();
			//Json pathNode = temp.addNode(path);
			
			Json puts = node.at(PUT);
			if(puts==null)return temp;
			if(puts.isArray()){
				for(Json put: puts.asJsonList()){
					parseUpdate(temp, put, ctx);
				}
			}else{
				parseUpdate(temp, puts.at(UPDATES), ctx);
			}
			
			if(logger.isDebugEnabled())logger.debug("JsonPutHandler processed put  "+temp );
			return  temp;
		}
		return null;
		
	}

	@Override
	protected void preProcess(SignalKModel temp, String ctx, Json j) throws IOException {
		//capture and store any embedded content
		if(j.isObject() && j.has(PAYLOAD)){
			
			String ext = null;
			if(j.has(MIME_TYPE)){
				ext=mimeMap.get(j.at(MIME_TYPE).asString());
			}
			if(ext==null){
				ext="txt";
			}
			String filePath = ctx.replace('.', '/')+DOT+ext;
			//MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(mimeType);
			String payload = null;
			if(j.at(PAYLOAD).isString()){
				//save it separately and add a storage url
				payload = j.at(PAYLOAD).asString();
			}
			if(j.at(PAYLOAD).isObject()){
				//save it separately and add a storage url
				payload = j.toString();
			}
			File save = new File(storageDir, filePath);
			FileUtils.writeStringToFile(save, payload);
			j.set(STORAGE_URI, filePath);
			j.delAt(PAYLOAD);
		}else{
			if(j.isObject() && j.has(STORAGE_URI)){
				
			}
		}
	}

}
