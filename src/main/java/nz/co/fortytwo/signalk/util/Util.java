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

package nz.co.fortytwo.signalk.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import mjson.Json;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Place for all the left over bits that are used across Signalk
 * @author robert
 *
 */
public class Util {
	
	private static Logger logger = Logger.getLogger(Util.class);
	private static Properties props;
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");
	public static File cfg = null;
	private static boolean timeSet=false;
	public static final double R = 6372800; // In meters
	
	/**
	 * Smooth the data a bit
	 * @param prev
	 * @param current
	 * @return
	 */
	public static  double movingAverage(double ALPHA, double prev, double current) {
	    prev = ALPHA * prev + (1-ALPHA) * current;
	    return prev;
	}

	/**
	 * Load the config from the named dir, or if the named dir is null, from the default location
	 * The config is cached, subsequent calls get the same object 
	 * @param dir
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Properties getConfig(String dir) throws FileNotFoundException, IOException{
		if(props==null){
			//we do a quick override so we get nice sorted output :-)
			props = new Properties() {
			    /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
			    public Set<Object> keySet(){
			        return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
			    }

			    @Override
			    public synchronized Enumeration<Object> keys() {
			        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			    }
			};
			Util.setDefaults(props);
			if(StringUtils.isNotBlank(dir)){
				//we provided a config dir, so we use it
				props.setProperty(Constants.CFG_DIR, dir);
				cfg = new File(props.getProperty(Constants.CFG_DIR)+props.getProperty(Constants.CFG_FILE));
			}else if(Util.getUSBFile()!=null){
				//nothing provided, but we have a usb config dir, so use it
				cfg = new File(Util.getUSBFile(),props.getProperty(Constants.CFG_DIR)+props.getProperty(Constants.CFG_FILE));
			}else{
				//use the default config
				cfg = new File(props.getProperty(Constants.CFG_DIR)+props.getProperty(Constants.CFG_FILE));
			}
			
			if(cfg.exists()){
				props.load(new FileReader(cfg));
			}
		}
		return props;
	}
	
	/**
	 * Save the current config to disk.
	 * @throws IOException
	 */
	public static void saveConfig() throws IOException{
		if(props==null)return;
		props.store(new FileWriter(cfg), null);
		
	}

	/**
	 * Config defaults
	 * 
	 * @param props
	 */
	public static void setDefaults(Properties props) {
		//populate sensible defaults here
		props.setProperty(Constants.SELF,"self");
		props.setProperty(Constants.WEBSOCKET_PORT,"3000");
		props.setProperty(Constants.REST_PORT,"8080");
		props.setProperty(Constants.CFG_DIR,"./conf/");
		props.setProperty(Constants.CFG_FILE,"signalk.cfg");
		props.setProperty(Constants.STORAGE_ROOT,"./storage/");
		props.setProperty(Constants.DEMO,"false");
		props.setProperty(Constants.STREAM_URL,"./src/test/resources/motu.log&scanStream=true&scanStreamDelay=500");
		props.setProperty(Constants.USBDRIVE,"/media/usb0");
		props.setProperty(Constants.SERIAL_PORTS,"/dev/ttyUSB0,/dev/ttyUSB1,/dev/ttyUSB2,/dev/ttyACM0,/dev/ttyACM1,/dev/ttyACM2");
		if(SystemUtils.IS_OS_WINDOWS){
			props.setProperty(Constants.SERIAL_PORTS,"COM1,COM2,COM3,COM4");
		}
		props.setProperty(Constants.SERIAL_PORT_BAUD,"38400");
		props.setProperty(Constants.TCP_PORT,"5555");
		props.setProperty(Constants.UDP_PORT,"5554");
		props.setProperty(Constants.TCP_NMEA_PORT,"5557");
		props.setProperty(Constants.UDP_NMEA_PORT,"5556");
		props.setProperty(Constants.STOMP_PORT,"61613");
		props.setProperty(Constants.MQTT_PORT,"1883");
		props.setProperty(Constants.CLOCK_SOURCE,"system");
		props.setProperty(Constants.HAWTIO_PORT, "8000");
		props.setProperty(Constants.HAWTIO_AUTHENTICATE,"false");
		props.setProperty(Constants.HAWTIO_CONTEXT,"/hawtio");
		props.setProperty(Constants.HAWTIO_WAR ,"./hawtio/hawtio-default-offline-1.4.48.war");
		props.setProperty(Constants.HAWTIO_START,"true");
		props.setProperty(Constants.VERSION, "0.1");
	}
	

	public static Json getAddressesMsg(String hostname) throws UnknownHostException{
		Json msg = Json.object();
		String cfgHostname = getConfigProperty(Constants.HOSTNAME);
		if(StringUtils.isNotBlank(cfgHostname))
			hostname=cfgHostname;
		msg.set(SignalKConstants.websocketUrl, "ws://"+hostname+":"+getConfigProperty(Constants.WEBSOCKET_PORT)+JsonConstants.SIGNALK_WS);
		msg.set(SignalKConstants.signalkTcpPort,hostname+":"+getConfigProperty(Constants.TCP_PORT));
		msg.set(SignalKConstants.signalkUdpPort,hostname+":"+getConfigProperty(Constants.UDP_PORT));
		msg.set(SignalKConstants.nmeaTcpPort,hostname+":"+getConfigProperty(Constants.TCP_NMEA_PORT));
		msg.set(SignalKConstants.nmeaUdpPort,hostname+":"+getConfigProperty(Constants.UDP_NMEA_PORT));
		msg.set(SignalKConstants.stompPort,hostname+":"+getConfigProperty(Constants.STOMP_PORT));
		msg.set(SignalKConstants.mqttPort,hostname+":"+getConfigProperty(Constants.MQTT_PORT));
		
		return msg;
	}
	
	public static Json getWelcomeMsg(){
		Json msg = Json.object();
		msg.set(SignalKConstants.version, getVersion());
		msg.set(SignalKConstants.timestamp, getIsoTimeString());
		msg.set(SignalKConstants.self_str, JsonConstants.SELF);
		return msg;
	}
	
	public static String getVersion() {
		return getConfigProperty(Constants.VERSION);
	}

	/**
	 * Round to specified decimals
	 * @param val
	 * @param places
	 * @return
	 */
	public static double round(double val, int places){
		double scale = Math.pow(10, places);
		long iVal = Math.round (val*scale);
		return iVal/scale;
	}
	
	/**
	 * Updates and saves the scaling values for instruments
	 * @param scaleKey
	 * @param amount
	 * @param scaleValue
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static double updateScale(String scaleKey, double amount, double scaleValue) throws FileNotFoundException, IOException {
			scaleValue = scaleValue*amount;
			scaleValue= Util.round(scaleValue, 2);
			//logger.debug(" scale now = "+scale);
			
			//write out to config
			Util.getConfig(null).setProperty(scaleKey, String.valueOf(scaleValue));
			Util.saveConfig();
			
		return scaleValue;
	}

	/**
	 * Checks if a usb drive is inserted, and returns the root dir.
	 * Returns null if its not there
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static File getUSBFile() throws FileNotFoundException, IOException {
		File usbDrive = new File(Util.getConfig(null).getProperty(Constants.USBDRIVE));
		if(usbDrive.exists() && usbDrive.list().length>0){
			//we return it
			return usbDrive;
		}
		return null;
	}

	/**
	 * Attempt to set the system time using the GPS time
	 * @param sen
	 */
	@SuppressWarnings("deprecation")
	public static void checkTime(RMCSentence sen) {
			if(timeSet)return;
			try {
				net.sf.marineapi.nmea.util.Date dayNow = sen.getDate();
				//if we need to set the time, we will be WAAYYY out
				//we only try once, so we dont get lots of native processes spawning if we fail
				timeSet=true;
				Date date = new Date();
				if((date.getYear()+1900)==dayNow.getYear()){
					if(logger.isDebugEnabled())logger.debug("Current date is " + date);
					return;
				}
				//so we need to set the date and time
				net.sf.marineapi.nmea.util.Time timeNow = sen.getTime();
				String yy = String.valueOf(dayNow.getYear());
				String MM = pad(2,String.valueOf(dayNow.getMonth()));
				String dd = pad(2,String.valueOf(dayNow.getDay()));
				String hh = pad(2,String.valueOf(timeNow.getHour()));
				String mm = pad(2,String.valueOf(timeNow.getMinutes()));
				String ss = pad(2,String.valueOf(timeNow.getSeconds()));
				if(logger.isDebugEnabled())logger.debug("Setting current date to " + dayNow + " "+timeNow);
				String cmd = "sudo date --utc " + MM+dd+hh+mm+yy+"."+ss;
				Runtime.getRuntime().exec(cmd.split(" "));// MMddhhmm[[yy]yy]
				if(logger.isDebugEnabled())logger.debug("Executed date setting command:"+cmd);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			} 
			
		}

	/**
	 * pad the value to i places, eg 2 >> 02
	 * @param i
	 * @param valueOf
	 * @return
	 */
	private static String pad(int i, String value) {
		while(value.length()<i){
			value="0"+value;
		}
		return value;
	}
	

	

	public static double kntToMs(double speed) {
		return speed*0.51444;
	}
	public static double msToKnts(double speed) {
		return speed*1.943844492;
	}

	public static String getConfigProperty(String prop) {
		try {
			return getConfig(null).getProperty(prop);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	public static Integer getConfigPropertyInt(String prop) {
		try {
			return Integer.valueOf(getConfig(null).getProperty(prop));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	

	public static Pattern regexPath(String newPath) {
		// regex it
		String regex = newPath.replaceAll(".", "[$0]").replace("[*]", ".*").replace("[?]", ".");
		return Pattern.compile(regex);
	}


	public static String sanitizePath(String newPath) {
		newPath = newPath.replace('/', '.');
		if (newPath.startsWith(JsonConstants.DOT))
			newPath = newPath.substring(1);
		
		return newPath;
	}
	

	public static void populateTree(SignalKModel signalkModel, SignalKModel temp, String p) {
		NavigableSet<String> node = signalkModel.getTree(p);
		if(logger.isDebugEnabled())logger.debug("Found node:" + p + " = " + node);
		if (node != null && node.size()>0) {
			addNodeToTemp(temp, node);
		}else{
			temp.put(p, signalkModel.get(p));
		}
		
	}
	
	public static SignalKModel populateModel(SignalKModel model, String mapDump) throws IOException{
		Properties props = new Properties();
		props.load(new StringReader(mapDump.substring(1, mapDump.length() - 1).replace(", ", "\n")));       
		for (Map.Entry<Object, Object> e : props.entrySet()) {
			if(e.getValue().equals("true")|| e.getValue().equals("false")){
				model.put((String)e.getKey(), Boolean.getBoolean((String) e.getValue()));
			}else if(NumberUtils.isNumber((String)e.getValue())){
				model.put((String)e.getKey(),NumberUtils.createDouble((String)e.getValue()));
			}else{
				model.put((String)e.getKey(), e.getValue());
			}
		}
		return model;
	}

	public static SignalKModel populateModel(SignalKModel signalk, File file)throws IOException {
		 return populateModel(signalk, FileUtils.readFileToString(file));
	}
	
	/**
	 * Recursive findNode()
	 * @param node
	 * @param fullPath
	 * @return
	 */
	public static Json findNode(Json node, String fullPath) {
		String[] paths = fullPath.split("\\.");
		//Json endNode = null;
		for(String path : paths){
			logger.debug("findNode:"+path);
			node = node.at(path);
			if(node==null)return null;
		}
		return node;
	}
	
	public static void addNodeToTemp(SignalKModel temp, NavigableSet<String> node) {
		SignalKModel model = SignalKModelFactory.getInstance();
		for(String key:node){
			temp.put(key, model.get(key));
		}
	}

	public static String getIsoTimeString() {
		// TODO Auto-generated method stub
		return DateTime.now(DateTimeZone.UTC).toDateTimeISO().toString();
		//return ISO8601DateFormat.getDateInstance().format(new Date());
	}

	public static String getIsoTimeString(DateTime now) {
		return now.toDateTimeISO().toString();
	}

	public static String getIsoTimeString(long timestamp) {
		return new DateTime(timestamp,DateTimeZone.UTC).toDateTimeISO().toString();
	}

	public static double haversineMeters(double lat, double lon, double anchorLat, double anchorLon) {
		double dLat = Math.toRadians(anchorLat - lat);
        double dLon = Math.toRadians(anchorLon - lon);
        lat = Math.toRadians(lat);
        anchorLat = Math.toRadians(anchorLat);
 
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat) * Math.cos(anchorLat);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
	}



}
