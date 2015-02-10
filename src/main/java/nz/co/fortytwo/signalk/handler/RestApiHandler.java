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

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.JsonConstants;

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

	private static Logger logger = Logger.getLogger(RestApiHandler.class);
	


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
	 */
	public Json processGet(HttpServletRequest request, HttpServletResponse response, SignalKModel signalkModel) {
		// use Restlet API to create the response
		String path = request.getPathInfo();
		//String path =  exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
		if(logger.isDebugEnabled())logger.debug("We are processing the path = "+path);
        
        //check valid request.
        if(path.length()<JsonConstants.SIGNALK_API.length()){
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	return null;
        }
        path=path.substring(JsonConstants.SIGNALK_API.length());
        if(logger.isDebugEnabled())logger.debug("We are processing the extension:"+Arrays.toString(path.split("/")));
        
        Json json = signalkModel.atPath(path.split("/"));
        if(json==null){
        	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        	return null;
        }
        
        if(logger.isDebugEnabled())logger.debug("Returning:"+json);
        
        response.setContentType("application/json");
        
        // SEND RESPONSE
        
        response.setStatus(HttpServletResponse.SC_OK);
        return json;
		
	}

}
