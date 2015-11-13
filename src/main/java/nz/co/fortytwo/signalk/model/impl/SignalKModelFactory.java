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
package nz.co.fortytwo.signalk.model.impl;

import java.io.File;
import java.io.IOException;
import java.util.NavigableMap;
import java.util.SortedMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.Constants;
import nz.co.fortytwo.signalk.util.JsonConstants;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.Util;

/**
 * Factory to get signalKModel singleton
 * 
 * @author robert
 * 
 */
public class SignalKModelFactory {
	private static final String SIGNALK_MODEL_SAVE_FILE = "./conf/self.json";
	private static final String SIGNALK_CFG_SAVE_FILE = "./conf/signalk-config.json";
	private static Logger logger = Logger.getLogger(SignalKModelFactory.class);
	private static SignalKModel signalKModel;
	static {
		if (signalKModel == null)
			signalKModel = new SignalKModelImpl();
	}

	/**
	 * Get the signalKModel singleton
	 * 
	 * @return
	 */
	public static synchronized SignalKModel getInstance() {
		// if(signalKModel==null){
		// signalKModel=new SignalKModelImpl();
		// }
		return signalKModel;
	}

	/**
	 * Returns a different clean instance - only needed for testing!
	 * 
	 * @return
	 */
	public static synchronized SignalKModel getCleanInstance() {
		return new SignalKModelImpl();
	}

	/**
	 * Returns a different clean instance - only needed for testing!
	 * 
	 * @return
	 */
	public static synchronized SignalKModel getWrappedInstance(NavigableMap<String, Object> root) {
		return new SignalKModelImpl(root);
	}

	public static void load(SignalKModel model){
		File jsonFile = new File(SIGNALK_MODEL_SAVE_FILE);
		logger.info("Checking for previous state: "+jsonFile.getAbsolutePath());
		if(jsonFile.exists()){
			try{
				Json temp = Json.read(jsonFile.toURI().toURL());
				JsonSerializer ser = new JsonSerializer();
				model.putAll(ser.read(temp));
				logger.info("   Saved state loaded from "+SIGNALK_MODEL_SAVE_FILE);
			}catch(Exception ex){
				logger.error(ex.getMessage());
			}
		}else{
			logger.info("   Saved state not found");
		}
	}
	public static void loadConfig(SignalKModel model){
		File jsonFile = new File(SIGNALK_CFG_SAVE_FILE);
		logger.info("Checking for previous config: "+jsonFile.getAbsolutePath());
		if(jsonFile.exists()){
			try{
				Json temp = Json.read(jsonFile.toURI().toURL());
				JsonSerializer ser = new JsonSerializer();
				model.putAll(ser.read(temp));
				String self = (String) model.get(Constants.SELF);
				Util.setSelf(self);
				logger.info("   Saved config loaded from "+SIGNALK_CFG_SAVE_FILE);
			}catch(Exception ex){
				logger.error(ex.getMessage(),ex);
			}
		}else{
			logger.info("   Saved config not found");
		}
	}
	/**
	 * Save the current state of the signalk model
	 * 
	 * @throws IOException
	 */
	public static void save(SignalKModel model) throws IOException {
		if (model != null) {
			File jsonFile = new File(SIGNALK_MODEL_SAVE_FILE);
			JsonSerializer ser = new JsonSerializer();
			Json modelJson = ser.writeJson(model);
			//remove config
			if(modelJson.has(JsonConstants.CONFIG)){
				modelJson = modelJson.delAt(JsonConstants.CONFIG);
			}
			FileUtils.writeStringToFile(jsonFile, modelJson.toString());
			logger.debug("   Saved model state to "+SIGNALK_MODEL_SAVE_FILE);
		}
	}
	
	/**
	 * Save the current state of the signalk config
	 * 
	 * @throws IOException
	 */
	public static void saveConfig(SignalKModel model) throws IOException {
		if (model != null) {
			File jsonFile = new File(SIGNALK_CFG_SAVE_FILE);
			NavigableMap<String, Object> config = model.getSubMap(JsonConstants.CONFIG);
			JsonSerializer ser = new JsonSerializer();
			ser.setPretty(3);
			StringBuffer buffer = new StringBuffer();
	    	if(config!=null && config.size()>0){
	    		ser.write(config.entrySet().iterator(),'.',buffer);
	    	}else{
	    		buffer.append("{}");
	    	}
			FileUtils.writeStringToFile(jsonFile, buffer.toString());
			logger.debug("   Saved model state to "+SIGNALK_CFG_SAVE_FILE);
		}

	}
}
