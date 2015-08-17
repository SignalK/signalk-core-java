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

public class Constants {

	public static final String VERSION = "signalk.version";
	
	public static final String SELF = "signalk.vessel.self";
	
	//camel output destination types
	public static final String OUTPUT_TYPE = "OUTPUT_TYPE";
	public static final String OUTPUT_MQTT = "OUTPUT_MQTT";
	public static final String OUTPUT_STOMP = "OUTPUT_STOMP";
	public static final String OUTPUT_TCP = "OUTPUT_TCP";
	public static final String OUTPUT_WS = "OUTPUT_WS";
	public static final String OUTPUT_NMEA = "OUTPUT_NMEA";
	public static final String OUTPUT_REST = "OUTPUT_REST";
	
	//misc camel header constants
	public static final String DESTINATION = "destination";
	public static final String REPLY_TO = "reply-to";
	public static final String CamelMQTTPublishTopic = "CamelMQTTPublishTopic";
	
	// attached device types
	public static final String UID = "UID";
	public static final String IMU = "IMU";
	public static final String MEGA = "MEGA";

	public static final String MERGE_MODEL = "MRG"; 
		
	// config constants
	public static final String HOSTNAME ="signalk.hostname";

	public static final String USBDRIVE = "signalk.files.usb.usbdrive";
	public static final String CLOCK_SOURCE = "signalk.clock.source";
	
	//serial ports
	public static final String SERIAL_PORTS = "signalk.serial.ports";
	public static final String SERIAL_PORT_BAUD = "signalk.serial.baud";

	//debug flags
	public static final String SEND_MESSAGE = "signalk.debug.sendMessage";
	
	//directories
	public static final String STATIC_DIR = "signalk.files.static.dir";
	public static final String MAP_DIR = "signalk.files.maps.dir";
	public static final String MAP_DIR_FILTER = "signalk.files.maps.filter";
	public static final String STORAGE_ROOT = "signalk.files.storage.root";
	public static final String CFG_DIR = "signalk.files.cfg.dir";
	public static final String CFG_FILE = "signalk.files.cfg.file";
	
	
	public static final String PAYLOAD = "payload";
	public static final String MIME_TYPE = "mimetype";
	public static final String MIME_TYPE_JSON = "application/vnd.geo+json";
	public static final String STORAGE_URI="uri";
	

	//demo
	public static final String DEMO = "signalk.demo.start";
	public static final String STREAM_URL = "signalk.demo.file";
//	public static final String SESSIONID = "signalk.session";
	
	//servers
	public static final String WEBSOCKET_PORT = "signalk.server.websocket.port";
	public static final String REST_PORT = "signalk.server.rest.port";
	public static final String TCP_PORT = "signalk.server.tcp.port";
	public static final String UDP_PORT = "signalk.server.udp.port";
	public static final String TCP_NMEA_PORT = "signalk.server.tcp.nmea.port";
	public static final String UDP_NMEA_PORT = "signalk.server.udp.nmea.port";
	public static final String GENERATE_NMEA0183 = "signalk.server.nmea.generate0183";
	//STOMP
	public static final String STOMP_PORT = "signalk.server.stomp.port";
	public static final String START_STOMP = "signalk.server.stomp.start";
	//MQTT 
	public static final String MQTT_PORT = "signalk.server.mqtt.port";
	public static final String START_MQTT = "signalk.server.mqtt.start";
	
	//clients
	public static final String CLIENT_TCP = "signalk.client.tcp.connect";
	public static final String CLIENT_MQTT = "signalk.client.mqtt.connect";
	public static final String CLIENT_STOMP = "signalk.client.stomp.connect";
	
	//apps
	public static final String ALLOW_INSTALL = "signalk.apps.install.allow";
	public static final String ALLOW_UPGRADE = "signalk.apps.upgrade.allow";
	
//Hawtio
	public static final String HAWTIO_PORT = "hawtio.port";
	public static final String HAWTIO_AUTHENTICATE = "hawtio.authenticationEnabled";
	public static final String HAWTIO_CONTEXT = "hawtio.context";
	public static final String HAWTIO_WAR = "hawtio.war";
	public static final String HAWTIO_START = "hawtio.start";

	
	
	
	
	public Constants() {
		// TODO Auto-generated constructor stub
	}

}
