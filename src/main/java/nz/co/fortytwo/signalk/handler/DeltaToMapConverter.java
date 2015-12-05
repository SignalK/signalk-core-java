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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package nz.co.fortytwo.signalk.handler;

import static nz.co.fortytwo.signalk.util.SignalKConstants.CONTEXT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.PATH;
import static nz.co.fortytwo.signalk.util.SignalKConstants.PUT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.UPDATES;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.source;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;
import static nz.co.fortytwo.signalk.util.SignalKConstants.value;
import static nz.co.fortytwo.signalk.util.SignalKConstants.values;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.log4j.Logger;


/**
 * Converts SignalK delta format to map format
 * 
 * @author robert
 * 
 */
public class DeltaToMapConverter {

	private static Logger logger = Logger.getLogger(DeltaToMapConverter.class);
	
	/**
	 * Convert Delta JSON to full tree map.
	 * Returns null if the json is not an update, otherwise return a SignalKModel
	 * @param node
	 * @return
	 * @throws Exception 
	 */
	public SignalKModel  handle(Json node) throws Exception {
		//avoid full signalk syntax
		if(node.has(vessels))return null;
		//deal with diff format
		if(node.has(CONTEXT) && (node.has(UPDATES) || node.has(PUT))){
			if(logger.isDebugEnabled())logger.debug("processing delta  "+node );
			//process it
			SignalKModel temp =  SignalKModelFactory.getCleanInstance();
			
			//go to context
			String ctx = node.at(CONTEXT).asString();
			ctx = Util.fixSelfKey(ctx);
			//Json pathNode = temp.addNode(path);
			Json updates = node.at(UPDATES);
			if(updates==null)updates = node.at(PUT);
			if(updates==null)return temp;
			
				for(Json update: updates.asJsonList()){
					parseUpdate(temp, update, ctx);
				}
			
			if(logger.isDebugEnabled())logger.debug("DeltaToMapConverter processed diff  "+temp );
			return  temp;
		}
		return null;
		
	}

	protected void parseUpdate(SignalKModel temp, Json update, String ctx) throws Exception {
		
		
		//DateTime timestamp = DateTime.parse(ts,fmt);
		
		
	//grab values and add
		Json array = update.at(values);
		for(Json e : array.asJsonList()){
			String key = e.at(PATH).asString();
			//temp.put(ctx+"."+key, e.at(value).getValue());
			addRecursively(temp, ctx+dot+key, e.at(value));
			
			if(update.has(source)){
				//TODO:generate a proper src ref.
				addRecursively(temp, ctx+dot+key, update.at(source));
			}
			
			if(update.has(timestamp)){
				String ts = update.at(timestamp).asString();
				//TODO: should validate the timestamp
				temp.put(ctx+dot+key+dot+timestamp, ts);
			}
		}
		
	}

	protected void addRecursively(SignalKModel temp, String ctx, Json j) throws Exception {
		if(j==null||j.isNull())return;
		if(logger.isDebugEnabled())logger.debug("Key:"+ctx+dot+j.getParentKey()+", Object: "+j );
		preProcess(temp,ctx,j);
		if(j.isPrimitive()){
			temp.put(ctx+dot+j.getParentKey(), j.getValue());
		}else if(j.isArray()){
			temp.put(ctx+dot+j.getParentKey(), j);
		}else {
			for(Json child: j.asJsonMap().values()){
				if(value.equals(j.getParentKey())){
					addRecursively(temp, ctx, child);
				}else{
					addRecursively(temp, ctx+dot+j.getParentKey(), child);
				}
			}
		}
		
	}

	/**
	 * Allows us to do pre-processing in sub-classes
	 * @param temp
	 * @param ctx
	 * @param j
	 */
	protected void preProcess(SignalKModel temp, String ctx, Json j) throws Exception{
		//do nothing
	}

	
}
