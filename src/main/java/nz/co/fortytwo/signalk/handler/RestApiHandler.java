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

import static nz.co.fortytwo.signalk.util.Constants.STORAGE_ROOT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonConstants;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/*
 * Processes REST requests for Signal K data
 * By the time we get here it safe to do whatever is requested
 * Its safe to return whatever is requested, its filtered later.
 * 
 * @author robert
 *
 */
public class RestApiHandler {

	private static final String SLASH = "/";
	private static final String LIST = "list";
	private static Logger logger = Logger.getLogger(RestApiHandler.class);
	private JsonSerializer ser = new JsonSerializer();
	private JsonListHandler listHandler = new JsonListHandler();
	private File storageDir = new File(Util.getConfigProperty(STORAGE_ROOT));
	private Map<String, String> mimeMap = new HashMap<String, String>();
	
	public RestApiHandler() throws IOException {
		@SuppressWarnings("unchecked")
		List<String> lines = FileUtils.readLines(new File("./src/main/resources/mime.types"));
		for (String line : lines) {
			String[] parts = line.split("=");
			mimeMap.put(parts[1], parts[0]);
		}
	}
	/**
	 * Process a signalk GET message. The method will recover the appropriate json object at the urls path from 
	 * the provided SignalKModel. 
	 * <b/>
	 * If the request is invalid or not found the response will have the appropriate HTTP error codes set, and null will be returned
	 * <b/>
	 * If found the response will have HTTP 200 set, and the json object will be returned. 
	 * 
	 * @param request
	 * @param response
	 * @param signalkModel
	 * @return
	 * @throws IOException 
	 */
	public Object processGet(HttpServletRequest request, HttpServletResponse response, SignalKModel signalkModel) throws IOException {
		// use Restlet API to create the response
		String path = request.getPathInfo();
		//String path =  exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
		if(logger.isDebugEnabled())logger.debug("We are processing the path = "+path);
        
        //check valid request.
        if(path.length()<JsonConstants.SIGNALK_API.length() || !path.startsWith(JsonConstants.SIGNALK_API)){
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	return null;
        }
        path=path.substring(JsonConstants.SIGNALK_API.length());
        if(path.startsWith(SLASH))path=path.substring(1);
        if(path.endsWith(SLASH))path=path.substring(path.length()-1);
        if(logger.isDebugEnabled())logger.debug("We are processing the extension:"+path);
        //list
        if(path.startsWith(LIST)){
        	
        	path=path.substring(LIST.length()).replace(SLASH, SignalKConstants.dot);
        	if(path.startsWith(SignalKConstants.dot))path=path.substring(1);
        	//find the 'vessels.* or as much as exists
        	int pos1=-1;
        	int pos2=-1;
        	String context="vessels.*";
        	if(path.length()>0){
        		pos1=path.indexOf(SignalKConstants.dot)+1;
        		pos2=path.indexOf(SignalKConstants.dot, pos1);
        	}
        	if(pos2>-1){
        		//we have a potential vessel name
        		context=path.substring(0,pos2);
        		path=path.substring(pos2+1);
        	}
        	path=path+"*";
        	
        	
        	List<String> rslt = listHandler.getMatchingPaths(path);
        	Json pathList = Json.array();
    		for(String p: rslt){
    			pathList.add(context+dot+p);
    		}
    		response.setContentType("application/json");
	        
	        // SEND RESPONSE
	        response.setStatus(HttpServletResponse.SC_OK);
        	return pathList;
        }
        //vessel params
        if(path.startsWith(vessels)){
        	//convert .self to .motu
        	path=path.replace(".self", dot+self);
	        NavigableMap<String, Object> keys = signalkModel.getSubMap(path.replace(SLASH, dot));
	        
	        if(keys.size()==0){
	        	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	        	return null;
	        }
	    
	        if(logger.isDebugEnabled())logger.debug("Returning:"+keys);
	        
	        response.setContentType("application/json");
	        
	        // SEND RESPONSE
	        response.setStatus(HttpServletResponse.SC_OK);
	        return ser.writeJson(SignalKModelFactory.getWrappedInstance(keys));
        }
        //storage dir
        if(path.startsWith(resources)){
        	path = path.substring(resources.length()+1);
	        if(logger.isDebugEnabled())logger.debug("Returning resource:"+path);
	        String ext = path.substring(path.lastIndexOf(".")+1);
	        response.setContentType(mimeMap.get(ext));
	        
	        // SEND RESPONSE
	        response.setStatus(HttpServletResponse.SC_OK);
	        return FileUtils.readFileToString(new File(storageDir,path));
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    	return null;
	}

}
