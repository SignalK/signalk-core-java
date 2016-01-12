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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class UtilTest {

	private static Logger logger = Logger.getLogger(UtilTest.class);
	
	@BeforeClass
	public static void setUp() throws Exception {
		Util.getConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldGetWelcomeMsg(){
		Json msg = Util.getWelcomeMsg();
		logger.debug(msg);
		//{"timestamp":"2015-04-13T23:04:03.826Z","version":"0.1","SignalKConstants.self":"motu"}
		assertTrue(msg.has(SignalKConstants.timestamp));
		assertTrue(msg.has(SignalKConstants.version));
		assertTrue(msg.has(SignalKConstants.self_str));
	}
	
	@Test
	public void shouldGetAddressesMsg() throws UnknownHostException{
		Json msg = Util.getEndpoints("localhost");
		logger.debug(msg);
		//{"timestamp":"2015-04-13T23:04:03.826Z","version":"0.1","SignalKConstants.self":"motu"}
		assertTrue(msg.has(SignalKConstants.stompPort));
		assertTrue(msg.has(SignalKConstants.mqttPort));
		assertTrue(msg.has(SignalKConstants.websocketUrl));
		assertTrue(msg.has(SignalKConstants.signalkTcpPort));
		assertTrue(msg.has(SignalKConstants.signalkUdpPort));
		assertTrue(msg.has(SignalKConstants.nmeaUdpPort));
		assertTrue(msg.has(SignalKConstants.nmeaTcpPort));
	}

	@Test
	public void shouldCreateDefaultJson() {
		SignalKModel model = SignalKModelFactory.getCleanInstance();
		Util.setDefaults(model);
		try {
			File tmp = File.createTempFile("jUnit", "_cfg");
			SignalKModelFactory.saveConfig(model,tmp);
			String jsonCfg = FileUtils.readFileToString(tmp);
			logger.debug(jsonCfg);
			assertNotNull(model.get(ConfigConstants.UUID));
			assertNotNull(model.get(ConfigConstants.WEBSOCKET_PORT));
			assertNotNull(model.get(ConfigConstants.REST_PORT));
			assertNotNull(model.get(ConfigConstants.STORAGE_ROOT));
			assertNotNull(model.get(ConfigConstants.STATIC_DIR));
			assertNotNull(model.get(ConfigConstants.MAP_DIR));
			assertNotNull(model.get(ConfigConstants.DEMO));
			assertNotNull(model.get(ConfigConstants.STREAM_URL));
			assertNotNull(model.get(ConfigConstants.USBDRIVE));
			assertNotNull(model.get(ConfigConstants.SERIAL_PORTS));
			
			assertNotNull(model.get(ConfigConstants.SERIAL_PORT_BAUD));
			assertNotNull(model.get(ConfigConstants.ENABLE_SERIAL));
			assertNotNull(model.get(ConfigConstants.TCP_PORT));
			assertNotNull(model.get(ConfigConstants.UDP_PORT));
			assertNotNull(model.get(ConfigConstants.TCP_NMEA_PORT));
			assertNotNull(model.get(ConfigConstants.UDP_NMEA_PORT));
			assertNotNull(model.get(ConfigConstants.STOMP_PORT));
			assertNotNull(model.get(ConfigConstants.MQTT_PORT));
			assertNotNull(model.get(ConfigConstants.CLOCK_source));
			assertNotNull(model.get(ConfigConstants.HAWTIO_PORT));
			assertNotNull(model.get(ConfigConstants.HAWTIO_AUTHENTICATE));
			assertNotNull(model.get(ConfigConstants.HAWTIO_CONTEXT));
			assertNotNull(model.get(ConfigConstants.HAWTIO_WAR));
			assertNotNull(model.get(ConfigConstants.HAWTIO_START));
			assertNotNull(model.get(ConfigConstants.VERSION));
			assertNotNull(model.get(ConfigConstants.ALLOW_INSTALL));
			assertNotNull(model.get(ConfigConstants.ALLOW_UPGRADE));
			assertNotNull(model.get(ConfigConstants.GENERATE_NMEA0183));
			assertNotNull(model.get(ConfigConstants.START_MQTT));
			assertNotNull(model.get(ConfigConstants.START_STOMP));
			assertNull(model.get(ConfigConstants.CLIENT_TCP));
			assertNull(model.get(ConfigConstants.CLIENT_MQTT));
			assertNull(model.get(ConfigConstants.CLIENT_STOMP));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
