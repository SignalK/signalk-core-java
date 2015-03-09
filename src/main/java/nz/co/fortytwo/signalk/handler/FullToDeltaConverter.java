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

import static nz.co.fortytwo.signalk.util.JsonConstants.CONTEXT;
import static nz.co.fortytwo.signalk.util.JsonConstants.PATH;
import static nz.co.fortytwo.signalk.util.JsonConstants.SOURCE;
import static nz.co.fortytwo.signalk.util.JsonConstants.UPDATES;
import static nz.co.fortytwo.signalk.util.JsonConstants.VALUE;
import static nz.co.fortytwo.signalk.util.JsonConstants.VALUES;
import static nz.co.fortytwo.signalk.util.JsonConstants.VESSELS;

import java.util.HashMap;

import mjson.Json;

import org.apache.log4j.Logger;

/**
 * Convert the full format to delta format
 * 
 * @author robert
 * 
 */
public class FullToDeltaConverter {

	private static Logger logger = Logger.getLogger(FullToDeltaConverter.class);


	/*
	 * {
	 * "context": "vessels.motu.navigation",
	 * "updates":[
	 * {
	 * "source": {
	 * "device" : "/dev/actisense",
	 * "timestamp":"2014-08-15-16:00:00.081",
	 * "src":"115",
	 * "pgn":"128267"
	 * },
	 * "values": [
	 * { "path": "courseOverGroundTrue","value": 172.9 },
	 * { "path": "speedOverGround","value": 3.85 }
	 * ]
	 * },
	 * {
	 * "source": {
	 * "device" : "/dev/actisense",
	 * "timestamp":"2014-08-15-16:00:00.081",
	 * "src":"115",
	 * "pgn":"128267"
	 * },
	 * "values": [
	 * { "path": "courseOverGroundTrue","value": 172.9 },
	 * { "path": "speedOverGround","value": 3.85 }
	 * ]
	 * }
	 * ]
	 * 
	 * }
	 */

	
	/**
	 * Converts the full tree format to the signalk delta format
	 * 
	 * @param node
	 * @return
	 */
	public Json handle(Json node) {
		// avoid full signalk syntax
		if (node.has(CONTEXT))
			return node;
		// deal with diff format
		if (node.has(VESSELS)) {
			if(logger.isDebugEnabled())logger.debug("processing full format  " + node);
			// find the first branch that splits
			Json ctx = getContext(node.at(VESSELS));
		
			String context = ctx.getPath();
			// process it

			// add values
			Json updates = Json.array();
			getEntries(updates, null, null, ctx, context.length() + 1);

			if (updates.asList().size() == 0)
				return null;

			Json delta = Json.object();
			delta.set(CONTEXT, context);
			delta.set(UPDATES, updates);

			return delta;
		}
		// misc types
		return node;
	}

	/**
	 * Find the first node with more than one child.
	 * 
	 * @param node
	 * @return
	 */
	protected Json getContext(Json node) {
		// look down the tree until we get more than one branch, thats the context
		if (node.asJsonMap().size() > 1){
			logger.debug("Context is :"+node.asJsonMap().size()+", "+ node);
			return node;
		}
		for (Json j : node.asJsonMap().values()) {
			logger.debug("Context recurse :"+ j);
			return getContext(j);
		}
		return node;
	}

	private void getEntries(Json updates, Json values, String jsSrcRef, Json j, int prefix) {
		if (!j.isObject())
			return;
		
		Json entry = null;
		
		for (Json js : j.asJsonMap().values()) {
			logger.debug("Process : "+js+", is primitive:"+js.isPrimitive());
			if (js == null)
				continue;
			
			if (js.isObject() && js.has(SOURCE)) {
				logger.debug("Process source : "+js);
				Json jsSrc = js.at(SOURCE);
				//has it changed
				if(jsSrcRef==null || !jsSrcRef.equals(jsSrc.toString())){
						
					//existing entry
					if(entry!=null){
						updates.add(entry);
					}
					jsSrcRef=jsSrc.toString();
					//new entry
					entry=Json.object();
					values = Json.array();
					entry.set(VALUES, values);
					entry.set(SOURCE, jsSrc.getValue());
				}
				
			}
			
			if (js.isArray()){
				logger.debug("Process array : "+js);
				String path = js.getPath().substring(prefix);
				Json value = Json.object();
				value.set(PATH, path);
				value.set(VALUE, js);

				values.add(value);
				continue;
			}
			
			if ( js.isPrimitive() || js.has(VALUE)) {
				if(js.getParentKey().equals(SOURCE))continue;
				logger.debug("Process value: "+js);
				String path = js.getPath().substring(prefix);

				Json value = Json.object();
				value.set(PATH, path);
				if(js.isPrimitive()){
					value.set(VALUE, js.getValue());
				}else{
					value.set(VALUE, js.at(VALUE).getValue());
				}
				values.add(value);
				//continue;
			} 
			if (js.isObject()) {
				logger.debug("Recurse : "+js);
				getEntries(updates, values, jsSrcRef, js, prefix);
			}
		}
		if(entry!=null){
			updates.add(entry);
		}

	}

}
