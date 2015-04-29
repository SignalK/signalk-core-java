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
	public static final String DEMO = "signalk.demo";
	public static final String WEBSOCKET_PORT = "signalk.websocket.port";
	public static final String REST_PORT = "signalk.rest.port";
	public static final String CFG_DIR = "signalk.cfg.dir";
	public static final String CFG_FILE = "signalk.cfg.file";
	public static final String STREAM_URL = "signalk.stream.demo.file";

	public static final String USBDRIVE = "signalk.usb.usbdrive";

	public static final String SERIAL_PORTS = "signalk.serial.ports";
	public static final String SERIAL_PORT_BAUD = "signalk.serial.port.baud";

	//debug flags
	public static final String SEND_MESSAGE = "signalk.debug.sendMessage";
	public static final String STATIC_DIR = "signalk.static.files.dir";
	public static final String MAP_DIR = "signalk.maps.dir";
	public static final String MAP_DIR_FILTER = "signalk.maps.dir.filter";
	
	public static final String SELF = "signalk.vessel.self";
//	public static final String SESSIONID = "signalk.session";
	
	public static final String TCP_PORT = "signalk.tcp.port";
	public static final String UDP_PORT = "signalk.udp.port";
	public static final String TCP_NMEA_PORT = "signalk.tcp.nmea.port";
	public static final String UDP_NMEA_PORT = "signalk.udp.nmea.port";
	public static final String CLOCK_SOURCE = "signalk.clock.source";
	public static final String HAWTIO_PORT = "hawtio.port";
	public static final String HAWTIO_AUTHENTICATE = "hawtio.authenticationEnabled";
	public static final String HAWTIO_CONTEXT = "hawtio.context";
	public static final String HAWTIO_WAR = "hawtio.war";
	public static final String HAWTIO_START = "hawtio.start";

	public static final String STOMP_PORT = "signalk.stomp.port";
	public static final String MQTT_PORT = "signalk.mqtt.port";

	public static final String STORAGE_ROOT = "signalk.storage.root";
	public static final String PAYLOAD = "payload";
	public static final String MIME_TYPE = "mimetype";
	public static final String STORAGE_URI="uri";

	
	
	
	
	public Constants() {
		// TODO Auto-generated constructor stub
	}

}
