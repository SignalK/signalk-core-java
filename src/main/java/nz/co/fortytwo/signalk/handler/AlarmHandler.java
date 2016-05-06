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

import static nz.co.fortytwo.signalk.util.SignalKConstants.alarm;
import static nz.co.fortytwo.signalk.util.SignalKConstants.alarmMessage;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.meta;
import static nz.co.fortytwo.signalk.util.SignalKConstants.normal;
import static nz.co.fortytwo.signalk.util.SignalKConstants.value;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.warn;
import static nz.co.fortytwo.signalk.util.SignalKConstants.warnMessage;
import static nz.co.fortytwo.signalk.util.SignalKConstants.zones;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.AlarmManager;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;



/**
 * Scans the signal k model for alarm conditions and sets/unsets alarms
 * 
 * @author robert
 * 
 */
public class AlarmHandler {

	private static Logger logger = LogManager.getLogger(AlarmHandler.class);
	
	/**
	 * Scans the signal k model for alarm conditions and sets/unsets alarms
	 * Only looks at vessels.self
	 * @param signalkModel
	 */
	public  void handle(SignalKModel signalkModel) {
		try {
			for(String key : signalkModel.getKeys()){
				//we check if there is a key.meta key
				//only check SignalKConstants.self
				if(!key.startsWith(vessels_dot_self_dot))continue;
				if(logger.isDebugEnabled())logger.debug("Checking :"+key);
				//remove .value
				key=key.replace(dot+value,"");
				Object metaZones = signalkModel.get(key+dot+meta+dot+zones);
				
				if(metaZones!=null && metaZones instanceof Json && ((Json)metaZones).isArray()){
					if(logger.isDebugEnabled())logger.debug("Checking zones:"+metaZones);
					//if zones is empty clear the alarms
					//String alarmKey = vessels_dot_self_dot+alarms+dot+key.substring(vessels_dot_self_dot.length());
					//zones object
					AlarmManager alarmManager = new AlarmManager((Json) metaZones);
					
					if (((Json)metaZones).asJsonList().size()==0){
						//clear all alarms.
						alarmManager.setAlarm(signalkModel,key, normal,null);
					}
					Number val = (Number)signalkModel.getValue(key);
					if(logger.isDebugEnabled())logger.debug("Checking value:"+val+"="+alarmManager.isAlarm(val));
					//get key.value
					if(alarmManager.isAlarm(val)){
						//set the alarm in vessels.self.alarms.key
						String msg = (String) signalkModel.get(key+dot+meta+dot+alarmMessage);
						alarmManager.setAlarm(signalkModel,key, alarm, msg);
						
					}
					if(alarmManager.isWarn(val)){
						//set the alarm
						String msg = (String) signalkModel.get(key+dot+meta+dot+warnMessage);
						alarmManager.setAlarm(signalkModel,key, warn, msg);
						
					}
					if(alarmManager.isNormal(val)){
						//clear the alarms
						alarmManager.setAlarm(signalkModel,key, normal,null);
						
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	
	}

	

	
}
