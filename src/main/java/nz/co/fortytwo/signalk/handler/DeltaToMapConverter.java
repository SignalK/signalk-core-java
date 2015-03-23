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

import static nz.co.fortytwo.signalk.util.JsonConstants.CONTEXT;
import static nz.co.fortytwo.signalk.util.JsonConstants.DEVICE;
import static nz.co.fortytwo.signalk.util.JsonConstants.PATH;
import static nz.co.fortytwo.signalk.util.JsonConstants.PGN;
import static nz.co.fortytwo.signalk.util.JsonConstants.SOURCE;
import static nz.co.fortytwo.signalk.util.JsonConstants.SRC;
import static nz.co.fortytwo.signalk.util.JsonConstants.TIMESTAMP;
import static nz.co.fortytwo.signalk.util.JsonConstants.UPDATES;
import static nz.co.fortytwo.signalk.util.JsonConstants.VALUE;
import static nz.co.fortytwo.signalk.util.JsonConstants.*;
import static nz.co.fortytwo.signalk.util.SignalKConstants.*;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Converts SignalK delta format to map format
 * 
 * @author robert
 * 
 */
public class DeltaToMapConverter {

	private static Logger logger = Logger.getLogger(DeltaToMapConverter.class);
	private static DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
	
	

	/*
	 *  {
    "context": "vessels.motu.navigation",
    "updates":[
	    	{
		    "source": {
		        "device" : "/dev/actisense",
		        "timestamp":"2014-08-15-16:00:00.081",
		        "src":"115",
		         "pgn":"128267"
		    },
		    "values": [
		         { "path": "courseOverGroundTrue","value": 172.9 },
		         { "path": "speedOverGround","value": 3.85 }
		      ]
		    },
		     {
		      "source": {
		        "device" : "/dev/actisense",
		        "timestamp":"2014-08-15-16:00:00.081",
		        "src":"115",
		         "pgn":"128267"
		    },
		    "values": [
		         { "path": "courseOverGroundTrue","value": 172.9 },
		         { "path": "speedOverGround","value": 3.85 }
		      ]
		    }
	    ]
	      
	}
*/
	 
	
	/**
	 * Convert Delta JSON to full tree map.
	 * Returns null if the json is not an update, otherwise return a SignalKModel
	 * @param node
	 * @return
	 */
	public SignalKModel  handle(Json node) {
		//avoid full signalk syntax
		if(node.has(VESSELS))return null;
		//deal with diff format
		if(node.has(CONTEXT)){
			if(logger.isDebugEnabled())logger.debug("processing delta  "+node );
			//process it
			SignalKModel temp =  SignalKModelFactory.getCleanInstance();
			
			//go to context
			String ctx = node.at(CONTEXT).asString();
			//Json pathNode = temp.addNode(path);
			Json updates = node.at(UPDATES);
			if(updates==null)return temp;
			if(updates.isArray()){
				for(Json update: updates.asJsonList()){
					parseUpdate(temp, update, ctx);
				}
			}else{
				parseUpdate(temp, updates.at(UPDATES), ctx);
			}
			
			if(logger.isDebugEnabled())logger.debug("SignalkModelProcessor processed diff  "+temp );
			return  temp;
		}
		return null;
		
	}

	private void parseUpdate(SignalKModel temp, Json update, String ctx) {
		
		
		//DateTime timestamp = DateTime.parse(ts,fmt);
		
		
	//grab values and add
		Json array = update.at(VALUES);
		for(Json e : array.asJsonList()){
			String key = e.at(PATH).asString();
			//temp.put(ctx+"."+key, e.at(VALUE).getValue());
			addRecursively(temp, ctx+dot+key, e.at(VALUE));
			
			if(update.has(SOURCE)){
				//TODO:generate a proper src ref.
				addRecursively(temp, ctx+dot+key, update.at(SOURCE));
			}
			
			if(update.has(TIMESTAMP)){
				String ts = update.at(TIMESTAMP).asString();
				//TODO: should validate the timestamp
				temp.put(ctx+dot+key+dot+timestamp, ts);
			}
		}
		
	}

	private void addRecursively(SignalKModel temp, String ctx, Json j) {
		if(j==null||j.isNull())return;
		logger.debug("Object is: "+j );
		if(j.isPrimitive()){
			temp.put(ctx+dot+j.getParentKey(), j.getValue());
		}else{
			for(Json child: j.asJsonMap().values()){
			 addRecursively(temp, ctx+dot+j.getParentKey(), child);
			}
		}
		
	}

	
}
