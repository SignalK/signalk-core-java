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

import static nz.co.fortytwo.signalk.util.SignalKConstants.CONFIG;
import static nz.co.fortytwo.signalk.util.SignalKConstants.CONTEXT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.resources;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Converts SignalK full format to map format
 * 
 * @author robert
 * 
 */
public class FullToMapConverter {

	private static Logger logger = LogManager.getLogger(FullToMapConverter.class);
	private static DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
	 
	private JsonSerializer ser = new JsonSerializer();
	/**
	 * Convert full JSON to full tree map.
	 * Returns null if the json is not an full format, otherwise return a SignalKModel
	 * @param node
	 * @return
	 */
	/**
	 * @param node
	 * @return
	 */
	public SignalKModel  handle(Json node) {
		//avoid diff signalk syntax
		if(node.has(CONTEXT))return null;
		//deal with full format
		if(node.has(vessels) || node.has(CONFIG)|| node.has(resources)){
			if(logger.isDebugEnabled())logger.debug("processing full  "+node );
			//process it
			SignalKModel temp =  SignalKModelFactory.getCleanInstance();
			temp.putAll(ser.read(node));
			if(logger.isDebugEnabled())logger.debug("SignalkModelProcessor processed diff  "+temp );
			return  temp;
		}
		return null;
		
	}


	
}
