package nz.co.fortytwo.signalk.util;

import static nz.co.fortytwo.signalk.util.SignalKConstants.alarm;
import static nz.co.fortytwo.signalk.util.SignalKConstants.alarmState;
import static nz.co.fortytwo.signalk.util.SignalKConstants.notifications;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.message;
import static nz.co.fortytwo.signalk.util.SignalKConstants.normal;
import static nz.co.fortytwo.signalk.util.SignalKConstants.warn;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

public class AlarmManager {
	private static Logger logger = LogManager.getLogger(AlarmManager.class);
	private Json zones;

	public AlarmManager(Json zones){
		if(!zones.isArray()) throw new IllegalArgumentException("Zones must be a Json array:"+zones);
		this.zones=zones;
	}

	public boolean isNormal(Number value){
		Double val = value.doubleValue();
		for(Json zone : zones.asJsonList()){
			if(normal.equals(zone.at("state").asString())){
				double low = zone.at("lower").asDouble();
				double high = zone.at("upper").asDouble();
				if(Math.min(low, val)==val)return false;
				if(Math.max(high, val)==val)return false;
				if(logger.isDebugEnabled())logger.debug("In normal zone:"+value);
				//its between 
				return true;
			}
		}
		return false;
	}
	public boolean isWarn(Number value){
		Double val = value.doubleValue();
		for(Json zone : zones.asJsonList()){
			if(warn.equals(zone.at("state").asString())){
				double low = zone.at("lower").asDouble();
				double high = zone.at("upper").asDouble();
				if(Math.min(low, val)==val)return false;
				if(Math.max(high, val)==val)return false;
				//its between 
				if(logger.isDebugEnabled())logger.debug("In warn zone:"+value);
				return true;
			}
		}
		return false;
	}
	public boolean isAlarm(Number value){
		Double val = value.doubleValue();
		for(Json zone : zones.asJsonList()){
			if(alarm.equals(zone.at("state").asString())){
				if(logger.isDebugEnabled())logger.debug("checking zone:"+zone);
				double low = zone.at("lower").asDouble();
				double high = zone.at("upper").asDouble();
				if(Math.min(low, val)==low && Math.max(high, val)==high){
					//its between 
					if(logger.isDebugEnabled())logger.debug("In alarm zone:"+value);
					return true;
				}
				
			}
		}
		return false;
	}
	
	/**
	 * Set the alarm key and value for key. Only resets it if its changed.
	 * key is the real key, it will be inserted into vessels.*.notifications.* automatically
	 * @param signalkModel
	 * @param alarmKey
	 * @param value
	 */
	public void setAlarm(SignalKModel signalkModel,String key, String value, String msg) {
		int pos = key.indexOf(dot);
		pos=key.indexOf(dot,pos+1)+1;
		String alarmKey = key.substring(0,pos)+notifications+dot+key.substring(pos);
		Object obj = signalkModel.get(alarmKey);
		if(obj!=null && obj instanceof Json){
			Json json = ((Json)obj).at(alarmState);
			if(json!=null && value.equals(json.asString())){
				//already set return
				if(logger.isDebugEnabled())logger.debug("Alarm already set for:"+key);
				return;
			}
		}
		signalkModel.getFullData().put(alarmKey+dot+alarmState, value);
		if (msg==null)msg="";
		signalkModel.getFullData().put(alarmKey+dot+message, msg);
		if(logger.isDebugEnabled())logger.debug("Alarm set for:"+key+"="+value);
	}
}
