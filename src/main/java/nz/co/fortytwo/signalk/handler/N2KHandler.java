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

import static nz.co.fortytwo.signalk.util.SignalKConstants.UNKNOWN;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.source;
import static nz.co.fortytwo.signalk.util.SignalKConstants.sourceRef;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;
import static nz.co.fortytwo.signalk.util.SignalKConstants.value;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.regex.Pattern;

import mjson.Json;
import net.minidev.json.JSONArray;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

/**
 * Updates the convert n2k json to signalk tree
 * 
 * @author robert
 * 
 */
public class N2KHandler {

	private static final String FILTER = "filter";
	private static final String NODE = "node";
	private static final String AIS_PGN_129038 = "129038";
	private static final String AIS_PGN_129039 = "129039";
	private static final String AIS_PGN_129794 = "129794";
	private static final String AIS_PGN_129809 = "129809";
	
	private static final String TYPE = "type";
	private static final CharSequence STRING = "string";
	private static final String VALUE = "value";

	private static Logger logger = Logger.getLogger(N2KHandler.class);

	private NumberFormat numberFormat = DecimalFormat.getInstance();
	private Json mappings = null;
	private JsonPath pgnPath = JsonPath.compile("$.pgn");
	private JsonPath srcPath = JsonPath.compile("$.src");
	private JsonPath userId = JsonPath.compile("$.fields.User_ID");
	private Multimap<String, N2KHolder> nodeMap = HashMultimap.create();
	private Pattern pattern = Pattern.compile("^[-+]?\\d+(\\.\\d+)?$");

	public N2KHandler() {
		File mappingFile = new File("./conf/n2kMappings.json");
		try {
			mappings = Json.read(FileUtils.readFileToString(mappingFile));
			for (String pgn : mappings.asJsonMap().keySet()) {
				Json mappingArray = mappings.at(pgn);
				logger.debug("Array="+mappingArray);
				for (Json j : mappingArray.asJsonList()) {
					logger.debug("Json="+j);
					String filter = "$.fields";
					if(j.at(FILTER)!=null && !j.at(FILTER).isNull()){
						filter=j.at(FILTER).asString();
						//replace spaces in words only
						filter=filter.replaceAll("([A-Za-z)]) ([(A-Za-z])", "$1_$2");
						logger.debug("Converted filter:"+filter);
					}
					JsonPath compiledPath = JsonPath.compile(filter + "." + j.at(source).getValue().toString().replaceAll(" ", "_"));
					String node = j.at(NODE).asString();
					String type = null;
					if(j.has(TYPE)){
						type = j.at(TYPE).asString();
					}
					boolean val=false;
					if(j.has(VALUE)){
						val = j.at(VALUE).asBoolean();
					}
					nodeMap.put(pgn, new N2KHolder(node, compiledPath, type, val));
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	

	/*
	 * {
	 * "timestamp": "2013-10-08-15:47:28.264",
	 * "prio": "2",
	 * "src": "2",
	 * "dst": "255",
	 * "pgn": "129025",
	 * "description": "Position, Rapid Update",
	 * "fields": {
	 * "Latitude": "60.1445540",
	 * "Longitude": "24.7921348"
	 * }
	 * }
	 */

	// @Override
	/**
	 * 
	 * Converts a Json n2k message (from CANboat analyser) to a signalK json message
	 * @param n2kmsg
	 * @return
	 */
	public SignalKModel handle(String n2kmsg) {
		return handle(n2kmsg, null);
	}
	
	/**
	 * 
	 * Converts a Json n2k message (from CANboat analyser) to a signalK json message
	 * @param n2kmsg
	 * @return
	 */
	public SignalKModel handle(String n2kmsg, String device) {
		// get the pgn value
		DocumentContext n2k = JsonPath.parse(n2kmsg.replaceAll(" ", "_"));
		String pgn = n2k.read(pgnPath);
		if (logger.isDebugEnabled())
			logger.debug("processing n2k pgn " + pgn);
		if (nodeMap.containsKey(pgn)) {
			// process it, mappings is n2kMapping.json as a json object
			Collection<N2KHolder> entries = nodeMap.get(pgn);
			//check AIS pgns
			String target = null;
			if(AIS_PGN_129038.equals(pgn)||AIS_PGN_129039.equals(pgn)||AIS_PGN_129794.equals(pgn)||AIS_PGN_129809.equals(pgn)){
				target = vessels+dot+n2k.read(userId)+dot;
			}else{
				target = vessels_dot_self_dot;
			}
			// make a dummy signalk object
			SignalKModel temp = SignalKModelFactory.getCleanInstance();
			String sourceRef = target+"sources.n2k."+pgn+dot+n2k.read(srcPath);
			
			String ts = Util.getIsoTimeString();
			if(StringUtils.isBlank(device))device = UNKNOWN;
			//add the actual source
			temp.put(sourceRef, n2kmsg,device, ts);
			
			
			// mapping contains an array
			for (N2KHolder entry : entries) {
				try{
					Object var = n2k.read(entry.path);
					Object val = resolve(var,entry.type);
					logger.debug(" evaluating " + entry + " = "+val.getClass()+ " : "+val);
					
					if(val instanceof JSONArray){
						if(!((JSONArray)val).isEmpty()){
							if(entry.value){
								temp.getFullData().put(target + entry.node, resolve(((JSONArray)val).get(0),entry.type));
								if(entry.parent!=null){
									temp.getFullData().put(target + entry.parent+dot+sourceRef, sourceRef);
									temp.getFullData().put(target + entry.parent+dot+timestamp, ts);
								}
							}else{
								temp.put(target + entry.node, resolve(((JSONArray)val).get(0),entry.type),sourceRef,ts);
							}
						}
						continue;
					}
					if(entry.value){
						temp.getFullData().put(target + entry.node, val);
						if(entry.parent!=null){
							temp.getFullData().put(target + entry.parent+dot+sourceRef, sourceRef);
							temp.getFullData().put(target + entry.parent+dot+timestamp, ts);
						}
					}else{
						temp.put(target + entry.node, val,sourceRef,ts);
					}
					
				}catch(PathNotFoundException p){
					logger.error(p);
				}
					
			}
			if (logger.isDebugEnabled())
				logger.debug("N2KHandler output  " + temp);
			return temp;
		}
		return null;

	}

	private Object resolve(Object var, String type) {
		if (var == null || !(var instanceof String))
			return var;
		try {
			if(StringUtils.equals(STRING, type))return ((String)var).replace('_', ' ');
			//check if numeric (-+0..9(.)0...9)
			if(!pattern.matcher((String)var).find()){
				return ((String)var).replace('_', ' ');
			}
			return numberFormat.parse((String)var).doubleValue();
		} catch (ParseException e) {
			return var;
		}

	}

	class N2KHolder {
		String parent = "";
		String node = null;
		JsonPath path = null;
		String type = null;
		boolean value = false;

		public N2KHolder(String node, JsonPath path, String type, boolean value) {
			this.node = node;
			this.path = path;
			this.type = type;
			this.value=value;
			int p = node.lastIndexOf(dot);
			if(p>0)parent=node.substring(0,p);
		}
	}

}
