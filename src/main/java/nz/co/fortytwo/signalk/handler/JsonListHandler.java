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
import static nz.co.fortytwo.signalk.util.JsonConstants.DOT;
import static nz.co.fortytwo.signalk.util.JsonConstants.LIST;
import static nz.co.fortytwo.signalk.util.JsonConstants.PATH;
import static nz.co.fortytwo.signalk.util.JsonConstants.VESSELS;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import mjson.Json;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;

/**
 * Handles json messages with 'list' requests
 * 
 * @author robert
 * 
 */
public class JsonListHandler {

	private static Logger logger = Logger.getLogger(JsonListHandler.class);
	//private static DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
	
	private List<String> keys = new ArrayList<String>();
	
	public JsonListHandler(){
		//get a chache all the signalk keys we know about
		for(Field f: SignalKConstants.class.getFields()){
			try {
				keys.add(VESSELS+DOT+"*"+DOT+f.get(null).toString());
				if(logger.isDebugEnabled())logger.debug("Added "+VESSELS+DOT+"*"+DOT+f.get(null).toString());
			} catch (IllegalArgumentException e) {
				logger.warn(e.getMessage());
			} catch (IllegalAccessException e) {
				logger.warn(e.getMessage());
			}
		}
	}

	public Json  handle(Json node) throws Exception {
		
		//deal with diff format
		
		if(logger.isDebugEnabled())logger.debug("Checking for list  "+node );
		
		//go to context
		String context = node.at(CONTEXT).asString();
		//TODO: is the context and path valid? A DOS attack is possible if we allow numerous crap/bad paths?
		
		Json paths = node.at(LIST);
		Json pathList = Json.array();
		if(paths!=null){
			if(paths.isArray()){
				
				for(Json path: paths.asJsonList()){
					parseList(context, path, pathList);
				}
				
			}
			if(logger.isDebugEnabled())logger.debug("Found list  "+pathList );
			if(logger.isDebugEnabled())logger.debug("Class type  "+pathList.getClass() );
			
		}
		return pathList;
		
	}

	/**
	 *  
	 *   <pre>{
                    "path": "navigation.speedThroughWater",
                  
                }
                </pre>
	 * @param context
	 * @param list
	 * @throws Exception 
	 */
	private void parseList( String context, Json path, Json pathList) throws Exception {
		//get values
		String regexKey = context+DOT+path.at(PATH).asString();
		if(logger.isDebugEnabled())logger.debug("Parsing list  "+regexKey );
		
		List<String> rslt = getMatchingPaths(regexKey);
		//add to pathList
		for(String p: rslt){
			pathList.add(p);
		}
		
	}
	
	
	/**
	 * Returns a list of paths that this implementation is currently providing.
	 * The list is filtered by the key if it is not null or empty in which case a full list is returned,
	 * supports * and ? wildcards.
	 * @param regex
	 * @return
	 */
	public List<String> getMatchingPaths(String regex) {
		if(StringUtils.isBlank(regex)){
			return ImmutableList.copyOf(keys);
		}
		regex=Util.sanitizePath(regex);
		//deal with vessels.motu, vessels.self
		int p1 = VESSELS.length()+1;
		int p2 = regex.indexOf(".",p1);
		if(p2>0){
			regex = VESSELS+DOT+"*"+regex.substring(p2);
		}else{
			regex = VESSELS+DOT+"*";
		}
		if(logger.isDebugEnabled())logger.debug("Regexing " + regex);
		Pattern pattern = Util.regexPath(regex);
		List<String> paths = new ArrayList<String>();
		for (String p : keys) {
			if (pattern.matcher(p).matches()) {
				if(logger.isDebugEnabled())logger.debug("Adding path:" + p);
				paths.add(p);
			}
		}
		return paths;
	}

	
}
