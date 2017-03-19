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

public class ConfigConstants {

	public static final String VERSION = "config.server.version";
	
	public static final String UUID = "config.server.vessel.uuid";
	public static final String FLAG = "config.server.vessel.flag";
	public static final String MMSI = "config.server.vessel.mmsi";
	public static final String NAME = "config.server.vessel.name";
	public static final String PORT = "config.server.vessel.port";
	
	//camel output destination types
	public static final String OUTPUT_TYPE = "OUTPUT_TYPE";
	public static final String OUTPUT_MQTT = "OUTPUT_MQTT";
	public static final String OUTPUT_STOMP = "OUTPUT_STOMP";
	public static final String OUTPUT_TCP = "OUTPUT_TCP";
	public static final String OUTPUT_WS = "OUTPUT_WS";
	public static final String OUTPUT_NMEA = "OUTPUT_NMEA";
	public static final String OUTPUT_REST = "OUTPUT_REST";
	public static final String OUTPUT_XMPP = "OUTPUT_XMPP";
	
	//misc camel header constants
	public static final String DESTINATION = "destination";
	public static final String REPLY_TO = "reply-to";
	public static final String CamelMQTTPublishTopic = "CamelMQTTPublishTopic";
	
	// attached device types
	public static final String UID = "UID";
	public static final String IMU = "IMU";
	public static final String MEGA = "MEGA";
 
		
	// config constants
	public static final String HOSTNAME ="config.server.hostname";

	public static final String USBDRIVE = "config.server.files.usb.usbdrive";
	public static final String CLOCK_source = "config.server.clock.src";
	
	//serial ports
	public static final String SERIAL_PORTS = "config.server.serial.ports";
	public static final String SERIAL_PORT_BAUD = "config.server.serial.baud";

	//debug flags
	public static final String ENABLE_SERIAL = "config.server.serial.enable";
	
	//directories
	public static final String STATIC_DIR = "config.server.files.static.dir";
	public static final String MAP_DIR = "config.server.files.maps.dir";
	public static final String MAP_DIR_FILTER = "config.server.files.maps.filter";
	public static final String STORAGE_ROOT = "config.server.files.storage.root";
	public static final String CFG_DIR = "config.server.files.cfg.dir";
	public static final String CFG_FILE = "config.server.files.cfg.file";
	
	
	public static final String PAYLOAD = "payload";
	public static final String MIME_TYPE = "mimetype";
	public static final String MIME_TYPE_JSON = "application/vnd.geo+json";
	public static final String STORAGE_URI="uri";
	

	//demo
	public static final String DEMO = "config.server.demo.start";
	public static final String STREAM_URL = "config.server.demo.file";
	public static final String XMPP = "config.server.xmpp";
//	public static final String SESSIONID = "signalk.session";
	
	//servers
	public static final String WEBSOCKET_PORT = "config.server.server.websocket.port";
	public static final String REST_PORT = "config.server.server.rest.port";
	public static final String TCP_PORT = "config.server.server.tcp.port";
	public static final String UDP_PORT = "config.server.server.udp.port";
	public static final String TCP_NMEA_PORT = "config.server.server.tcp.nmea.port";
	public static final String UDP_NMEA_PORT = "config.server.server.udp.nmea.port";
	public static final String GENERATE_NMEA0183 = "config.server.server.nmea.generate0183";
	public static final String ZEROCONF_AUTO = "config.server.server.zeroconf.auto";
	//STOMP
	public static final String STOMP_PORT = "config.server.server.stomp.port";
	public static final String START_STOMP = "config.server.server.stomp.start";
	//MQTT 
	public static final String MQTT_PORT = "config.server.server.mqtt.port";
	public static final String START_MQTT = "config.server.server.mqtt.start";
	
	//clients
	public static final String CLIENT_WS = "config.server.client.ws.connect";
	public static final String CLIENT_TCP = "config.server.client.tcp.connect";
	public static final String CLIENT_MQTT = "config.server.client.mqtt.connect";
	public static final String CLIENT_STOMP = "config.server.client.stomp.connect";
	
	//apps
	public static final String ALLOW_INSTALL = "config.server.apps.install.allow";
	public static final String ALLOW_UPGRADE = "config.server.apps.upgrade.allow";
	
	//security
	public static final String SECURITY_DENY = "config.server.security.deny.ip";
	public static final String SECURITY_CONFIG = "config.server.security.config.ip";
	public static final String SECURITY_WHITE = "config.server.security.white.ip";
//Hawtio
	public static final String HAWTIO_PORT = "config.hawtio.port";
	public static final String HAWTIO_AUTHENTICATE = "config.hawtio.authenticationEnabled";
	public static final String HAWTIO_CONTEXT = "config.hawtio.context";
	public static final String HAWTIO_WAR = "config.hawtio.war";
	public static final String HAWTIO_START = "config.hawtio.start";
	
	//Jolokia
	public static final String JOLOKIA_PORT = "config.jolokia.port";
	public static final String JOLOKIA_AUTHENTICATE = "config.jolokia.authenticationEnabled";
	public static final String JOLOKIA_CONTEXT = "config.jolokia.context";
	public static final String JOLOKIA_WAR = "config.jolokia.war";
        
        //Instrument Offsets, Display Units, Adjustments
        public static final String SURFACE_TO_TRANSDUCER = "config.environment.depth.surfaceToTransducer";
        public static final String TRANSDUCER_TO_KEEL = "config.environment.depth.transducerToKeel";
        public static final String SOG_DISPLAY_UNIT = "config.navigation.meta.sogDisplayUnit";
        public static final String DEPTH_DISPLAY_UNIT = "config.environment.depth.meta.displayUnit";
        public static final String STW_DISPLAY_UNIT = "config.navigation.meta.stwDisplayUnit";
        public static final String DEPTH_ALARM_ZONES = "config.environment.depth.meta.zones";
        public static final String DEPTH_WARN_METHOD = "config.environment.depth.meta.warnMethod";
        public static final String DEPTH_ALARM_METHOD = "config.environment.depth.meta.alarmMethod";
        
        // This is currently unimplemented as the MEGA compensages for wind ofsets
        // A command must be issued in terminal mode with the MEGA to set the apparent
        //  wind offset.     
        // public static final String WIND_OFFSET = "config.instruments.wind.apparentWindOffset";
	

	
	public ConfigConstants() {
		// TODO Auto-generated constructor stub
	}

}
