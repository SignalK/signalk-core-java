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

import static nz.co.fortytwo.signalk.util.SignalKConstants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.regex.Pattern;

import mjson.Json;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Place for all the left over bits that are used across Signalk
 * 
 * @author robert
 * 
 */
public class Util {

	private static Logger logger = LogManager.getLogger(Util.class);
	// private static Properties props;
	private static SignalKModel model = null;
	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd_hh:mm:ss");
	//public static File cfg = null;
	private static boolean timeSet = false;
	public static final double R = 6372800; // In meters

	private static Pattern selfMatch = Pattern.compile("\\.self\\.");
	private static String dot_self_dot = dot + self + dot;

	private static Pattern selfEndMatch = Pattern.compile("\\.self$");
	private static String dot_self = dot + self;

	private static String rootPath="";


	/**
         * Set the root path
         *
         * @param rootPath
         */
        public static void setRootPath(String rootPath) {
                Util.rootPath=rootPath;
        }

	/**
         * Get the root path
         *
         * @return
         */
        public static String getRootPath() { return rootPath; }

	/**
	 * Smooth the data a bit
	 * 
	 * @param prev
	 * @param current
	 * @return
	 */
	public static double movingAverage(double ALPHA, double prev, double current) {
		prev = ALPHA * prev + (1 - ALPHA) * current;
		return prev;
	}

	/**
	 * Load the config from the
	 * default location The config is cached, subsequent calls get the same
	 * object
	 * 
	 * @param dir
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void getConfig() throws FileNotFoundException, IOException {
		model = SignalKModelFactory.getInstance();
		//Util.setDefaults(model);
		//SignalKModelFactory.loadConfig(model);
		//String mySelf = (String) model.get(ConfigConstants.UUID);
		//Util.setSelf(mySelf);
	}
	
	/**
	 * Load the provided model as the config. The config is cached, subsequent calls get the same
	 * object
	 * Useful in tests
	 * 
	 * @param dir
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void setConfig(SignalKModel model) throws FileNotFoundException, IOException {
		Util.model = model;
	}


	/**
	 * Config defaults
	 * 
	 * @param props
	 */
	public static void setDefaults(SignalKModel model) {
		// populate sensible defaults here
		model.getFullData().put(ConfigConstants.UUID, "self");
		model.getFullData().put(ConfigConstants.WEBSOCKET_PORT, 3000);
		model.getFullData().put(ConfigConstants.REST_PORT, 8080);
		model.getFullData().put(ConfigConstants.STORAGE_ROOT, "./storage/");
		model.getFullData().put(ConfigConstants.STATIC_DIR, "./signalk-static");
		model.getFullData().put(ConfigConstants.MAP_DIR, "./mapcache");
		model.getFullData().put(ConfigConstants.DEMO, false);
		model.getFullData().put(ConfigConstants.STREAM_URL,
				"motu.log");
		model.getFullData().put(ConfigConstants.USBDRIVE, "/media/usb0");
		model.getFullData().put(
				ConfigConstants.SERIAL_PORTS,
				"[\"/dev/ttyUSB0\",\"/dev/ttyUSB1\",\"/dev/ttyUSB2\",\"/dev/ttyACM0\",\"/dev/ttyACM1\",\"/dev/ttyACM2\"]");
		if (SystemUtils.IS_OS_WINDOWS) {
			model.getFullData().put(ConfigConstants.SERIAL_PORTS,
					"[\"COM1\",\"COM2\",\"COM3\",\"COM4\"]");
		}
		model.getFullData().put(ConfigConstants.SERIAL_PORT_BAUD, 38400);
		model.getFullData().put(ConfigConstants.ENABLE_SERIAL, true);
		model.getFullData().put(ConfigConstants.TCP_PORT, 55555);
		model.getFullData().put(ConfigConstants.UDP_PORT, 55554);
		model.getFullData().put(ConfigConstants.TCP_NMEA_PORT, 55557);
		model.getFullData().put(ConfigConstants.UDP_NMEA_PORT, 55556);
		model.getFullData().put(ConfigConstants.STOMP_PORT, 61613);
		model.getFullData().put(ConfigConstants.MQTT_PORT, 1883);
		model.getFullData().put(ConfigConstants.CLOCK_source, "system");
		model.getFullData().put(ConfigConstants.HAWTIO_PORT, 8000);
		model.getFullData().put(ConfigConstants.HAWTIO_AUTHENTICATE, false);
		model.getFullData().put(ConfigConstants.HAWTIO_CONTEXT, "/hawtio");
		model.getFullData().put(ConfigConstants.HAWTIO_WAR,
				"./hawtio/hawtio-default-offline-1.4.48.war");
		model.getFullData().put(ConfigConstants.HAWTIO_START, false);
		model.getFullData().put(ConfigConstants.VERSION, "v1.0.0");
		model.getFullData().put(ConfigConstants.ALLOW_INSTALL, true);
		model.getFullData().put(ConfigConstants.ALLOW_UPGRADE, true);
		model.getFullData().put(ConfigConstants.GENERATE_NMEA0183, true);
		model.getFullData().put(ConfigConstants.ZEROCONF_AUTO, true);
		model.getFullData().put(ConfigConstants.START_MQTT, true);
		model.getFullData().put(ConfigConstants.START_STOMP, true);
		//model.getFullData().put(ConfigConstants.CLIENT_WS, null);
		//model.getFullData().put(ConfigConstants.CLIENT_TCP, null);
		//model.getFullData().put(ConfigConstants.CLIENT_MQTT, null);
		//model.getFullData().put(ConfigConstants.CLIENT_STOMP, null);

	}

	public static Json getWelcomeMsg() {
		Json msg = Json.object();
		msg.set(version, getVersion());
		msg.set(timestamp, getIsoTimeString());
		msg.set(self_str, getConfigProperty(ConfigConstants.UUID));
		return msg;
	}

	public static String getVersion() {
		return getConfigProperty(ConfigConstants.VERSION);
	}

	/**
	 * Round to specified decimals
	 * 
	 * @param val
	 * @param places
	 * @return
	 */
	public static double round(double val, int places) {
		double scale = Math.pow(10, places);
		long iVal = Math.round(val * scale);
		return iVal / scale;
	}

	/**
	 * Attempt to set the system time using the GPS time
	 * 
	 * @param sen
	 */
	@SuppressWarnings("deprecation")
	public static void checkTime(RMCSentence sen) {
		if (timeSet)
			return;
		try {
			net.sf.marineapi.nmea.util.Date dayNow = sen.getDate();
			// if we need to set the time, we will be WAAYYY out
			// we only try once, so we dont get lots of native processes
			// spawning if we fail
			timeSet = true;
			Date date = new Date();
			if ((date.getYear() + 1900) == dayNow.getYear()) {
				if (logger.isDebugEnabled())
					logger.debug("Current date is " + date);
				return;
			}
			// so we need to set the date and time
			net.sf.marineapi.nmea.util.Time timeNow = sen.getTime();
			String yy = String.valueOf(dayNow.getYear());
			String MM = pad(2, String.valueOf(dayNow.getMonth()));
			String dd = pad(2, String.valueOf(dayNow.getDay()));
			String hh = pad(2, String.valueOf(timeNow.getHour()));
			String mm = pad(2, String.valueOf(timeNow.getMinutes()));
			String ss = pad(2, String.valueOf(timeNow.getSeconds()));
			if (logger.isDebugEnabled())
				logger.debug("Setting current date to " + dayNow + " "
						+ timeNow);
			String cmd = "sudo date --utc " + MM + dd + hh + mm + yy + "." + ss;
			Runtime.getRuntime().exec(cmd.split(" "));// MMddhhmm[[yy]yy]
			if (logger.isDebugEnabled())
				logger.debug("Executed date setting command:" + cmd);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * pad the value to i places, eg 2 >> 02
	 * 
	 * @param i
	 * @param valueOf
	 * @return
	 */
	private static String pad(int i, String value) {
		while (value.length() < i) {
			value = "0" + value;
		}
		return value;
	}

	public static double kntToMs(double speed) {
		return speed * KNOTS_TO_MS;
	}

	public static double msToKnts(double speed) {
		return speed * MS_TO_KNOTS;
	}

	public static String getConfigProperty(String prop) {
		try {
			return (String) model.get(prop);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Json getConfigJsonArray(String prop) {
		try {
			String arrayStr = (String) model.get(prop);
			if (StringUtils.isNotBlank(arrayStr) && arrayStr.length() > 2) {
				Json array = Json.read(arrayStr);
				return array;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Integer getConfigPropertyInt(String prop) {
		try {
			if (model.get(prop) instanceof String) {
				return (Integer.valueOf((String) model.get(prop)));
			}
			if (model.get(prop) instanceof Number) {
				return ((Number) model.get(prop)).intValue();
			}
			
			return (Integer) model.get(prop);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Double getConfigPropertyDouble(String prop) {
		try {
			if (model.get(prop) instanceof String) {
				return (Double.valueOf((String) model.get(prop)));
			}
			return (Double) model.get(prop);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Boolean getConfigPropertyBoolean(String prop) {
		try {
			if (model.get(prop) instanceof Boolean) {
				return ((Boolean) model.get(prop));
			}
			return new Boolean((String) model.get(prop));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Pattern regexPath(String newPath) {
		// regex it
		String regex = newPath.replaceAll(".", "[$0]").replace("[*]", ".*")
				.replace("[?]", ".");
		return Pattern.compile(regex);
	}

	public static String fixSelfKey(String key) {
		key = selfMatch.matcher(key).replaceAll(dot_self_dot);
		key = selfEndMatch.matcher(key).replaceAll(dot_self);
		return key;
	}

	public static String sanitizePath(String newPath) {
		newPath = newPath.replace('/', '.');
		if (newPath.startsWith(dot))
			newPath = newPath.substring(1);
		if (!newPath.endsWith("*") || !newPath.endsWith("?"))
			newPath = newPath + "*";

		return newPath;
	}

	public static void populateTree(SignalKModel signalkModel,
			SignalKModel temp, String p) {
		NavigableSet<String> node = signalkModel.getTree(p);
		if (logger.isDebugEnabled())
			logger.debug("Found node:" + p + " = " + node);
		if (node != null && node.size() > 0) {
			addNodeToTemp(signalkModel,temp, node);
		} else {
			temp.getFullData().put(p, signalkModel.get(p));
		}

	}

	public static SignalKModel populateModel(SignalKModel model, String mapDump)
			throws IOException {
		Properties props = new Properties();
		props.load(new StringReader(mapDump.substring(1, mapDump.length() - 1)
				.replace(", ", "\n")));
		for (Map.Entry<Object, Object> e : props.entrySet()) {
			if (e.getValue().equals("true") || e.getValue().equals("false")) {
				model.getFullData().put((String) e.getKey(),
						Boolean.getBoolean((String) e.getValue()));
			} else if (NumberUtils.isNumber((String) e.getValue())) {
				model.getFullData().put((String) e.getKey(),
						NumberUtils.createDouble((String) e.getValue()));
			} else {
				model.getFullData().put((String) e.getKey(), e.getValue());
			}
		}
		return model;
	}

	public static SignalKModel populateModel(SignalKModel signalk, File file)
			throws IOException {
		return populateModel(signalk, FileUtils.readFileToString(file));
	}

	/**
	 * Recursive findNode()
	 * 
	 * @param node
	 * @param fullPath
	 * @return
	 */
	public static Json findNode(Json node, String fullPath) {
		String[] paths = fullPath.split("\\.");
		// Json endNode = null;
		for (String path : paths) {
			logger.debug("findNode:" + path);
			node = node.at(path);
			if (node == null)
				return null;
		}
		return node;
	}

	public static void addNodeToTemp(SignalKModel temp,
			NavigableSet<String> node) {
		SignalKModel model = SignalKModelFactory.getInstance();
		addNodeToTemp(model, temp, node);
	}
	
	public static void addNodeToTemp(SignalKModel model, SignalKModel temp,
			NavigableSet<String> node) {
		for (String key : node) {
			temp.getFullData().put(key, model.get(key));
		}
	}

	public static String getIsoTimeString() {
		
		return getIsoTimeString(System.currentTimeMillis());
		// return ISO8601DateFormat.getDateInstance().format(new Date());
	}

	public static String getIsoTimeString(DateTime now) {
		return now.toDateTimeISO().toString();
	}

	public static String getIsoTimeString(long timestamp) {
		return new DateTime(timestamp, DateTimeZone.UTC).toDateTimeISO()
				.toString();
	}

	public static double haversineMeters(double lat, double lon,
			double anchorLat, double anchorLon) {
		double dLat = Math.toRadians(anchorLat - lat);
		double dLon = Math.toRadians(anchorLon - lon);
		lat = Math.toRadians(lat);
		anchorLat = Math.toRadians(anchorLat);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(lat) * Math.cos(anchorLat);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}

	public static String getContext(String path) {
		// return vessels.*
		// TODO; robustness for "signalk/api/v1/", and "vessels.*" and
		// "list/vessels"
		if (StringUtils.isBlank(path))
			return "";
		if (path.equals(resources)||path.startsWith(resources + dot)) {
			return path;
		}
		
		if (path.equals(sources)||path.startsWith(sources + dot)) {
			return path;
		}
		
		if (path.equals(CONFIG)) {
			return path;
		}
		if (path.startsWith(CONFIG + dot)) {
			int p1 = path.indexOf(CONFIG)
					+ CONFIG.length() + 1;

			int pos = path.indexOf(".", p1);
			if (pos < 0)
				return path;
			return path.substring(0, pos);
		}
		if (path.equals(vessels)) {
			return path;
		}
		if (path.startsWith(vessels + dot)
				|| path.startsWith(LIST + dot + vessels + dot)) {
			int p1 = path.indexOf(vessels) + vessels.length() + 1;

			int pos = path.indexOf(".", p1);
			if (pos < 0)
				return path;
			return path.substring(0, pos);
		}
		return "";
	}

	public static void setSelf(String self) {
		//self = self;
		dot_self_dot = dot + self + dot;
		dot_self = dot + self;
		SignalKConstants.self = self;
		vessels_dot_self_dot = vessels + dot + self + dot;
		vessels_dot_self = vessels + dot + self;
		logger.info("Setting self:"+self);
		logger.info("Setting vessels.self:"+vessels_dot_self);
	}

	public static boolean sameNetwork(String localAddress, String remoteAddress)
			throws Exception {
		InetAddress addr = InetAddress.getByName(localAddress);
		NetworkInterface networkInterface = NetworkInterface.getByInetAddress(addr);
		short netmask = -1;
		for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
			if(address.getAddress().equals(addr)){
				netmask = address.getNetworkPrefixLength();
			}
		}
		byte[] a1 = InetAddress.getByName(localAddress).getAddress();
		byte[] a2 = InetAddress.getByName(remoteAddress).getAddress();
		byte[] m = InetAddress.getByName(normalizeFromCIDR(netmask)).getAddress();
		if(logger.isDebugEnabled())logger.debug("sameNetwork?:"+localAddress+","+remoteAddress+","+netmask);
		for (int i = 0; i < a1.length; i++){
			if ((a1[i] & m[i]) != (a2[i] & m[i])){
				return false;
			}
		}

		return true;

	}
	
	/*
	 * RFC 1518, 1519 - Classless Inter-Domain Routing (CIDR)
	 * This converts from "prefix + prefix-length" format to
	 * "address + mask" format, e.g. from xxx.xxx.xxx.xxx/yy
	 * to xxx.xxx.xxx.xxx/yyy.yyy.yyy.yyy.
	 */
	public static String normalizeFromCIDR(short bits)
	{
	    final int mask = (bits == 32) ? 0 : 0xFFFFFFFF - ((1 << bits)-1); 

	    return  Integer.toString(mask >> 24 & 0xFF, 10) + "." +
	            Integer.toString(mask >> 16 & 0xFF, 10) + "." +
	            Integer.toString(mask >>  8 & 0xFF, 10) + "." +
	            Integer.toString(mask >>  0 & 0xFF, 10);
	}
}
