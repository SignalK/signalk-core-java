/*
 *
 * Copyright (C) 2012-2014 R T Huitema. All Rights Reserved.
 * Web: www.42.co.nz
 * Email: robert@42.co.nz
 * Author: R T Huitema
 *
 * This file is part of the signalk-server-java project
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

import mjson.Json;
import nz.co.fortytwo.signalk.model.impl.SignalKModelImpl;
import nz.co.fortytwo.signalk.util.JsonConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.log4j.Logger;
import org.junit.After;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FullToDeltaConverterTest {
	private static Logger logger = Logger.getLogger(FullToDeltaConverterTest.class);
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldCreateDelta() {
		Json data = Json
				.read("{\"vessels\":{\"motu\":{\"navigation\":{\"courseOverGroundTrue\":{\"timestamp\":\"2015-03-18T04:53:26.367Z\",\"source\":{\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"src\":\"115\",\"device\":\"/dev/actisense\",\"pgn\":\"128267\"},\"value\":172.9},\"speedOverGround\":{\"timestamp\":\"2015-03-18T04:53:26.462Z\",\"source\":{\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"src\":\"115\",\"device\":\"/dev/actisense\",\"pgn\":\"128267\"},\"value\":3.85}}}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json out = processor.handle(data);
		logger.debug(out);
		
	}
	
	@Test
	public void shouldCreateDelta1() {
		Json data = Json
				.read("{\"vessels\":{\"motu\":{\"navigation\":{\"position\":{\"timestamp\":\"2015-04-02T00:13:05.329Z\",\"longitude\":23.52916584,\"latitude\":60.07603504,\"source\":\"vessels.motu.sources.nmea.0183.GLL\"}}}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		logger.debug("Input:"+data);
		Json out = processor.handle(data);
		logger.debug("Output:"+out);
		assertNotNull(out);
		
	}
	
	
	
	@Test
	public void shouldGetContext() {
		
		Json data = Json.read("{\"vessels\":{\"366982330\":{\"navigation\":{\"position\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"longitude\":173.1693,\"latitude\":-41.156426,\"source\":\"sources.gps_0183_RMC\",\"altitude\":0.0,\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"}},\"courseOverGroundTrue\":{\"timestamp\":\"2015-03-07T11:51:57.723+13:00\",\"meta\":{\"zones\":[[250,260,\"warn\"],[260,360,\"alarm\"],[220,230,\"warn\"],[0,220,\"alarm\"],[220,260,\"normal\"]],\"shortName\":\"COG\",\"alarmMethod\":\"sound\",\"warnMethod\":\"visual\",\"displayName\":\"COG (True)\"},\"source\":\"sources.gps_0183_RMC\",\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"},\"value\":245.69}}}},\"sources\":{\"gps_0183_RMC\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"src\":\"$GPRMC,033025.000,A,4115.6426,S,17316.9300,E,0.05,245.69,090113,,*15\",\"bus\":\"/dev/ttyUSB1\"},\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json context = processor.getContext(data.at(JsonConstants.VESSELS));
		
		logger.debug(context);
		logger.debug(context.getPath());
		assertEquals("vessels.366982330.navigation", context.getPath());

	}
	
	@Test
	public void shouldHandleArray() {
		
		Json data = Json.read("{\"vessels\":{\"366982330\":{\"navigation\":{\"position\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"longitude\":173.1693,\"latitude\":-41.156426,\"source\":\"sources.gps_0183_RMC\",\"altitude\":0.0,\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"}},\"courseOverGroundTrue\":{\"timestamp\":\"2015-03-07T11:51:57.723+13:00\",\"meta\":{\"zones\":[[250,260,\"warn\"],[260,360,\"alarm\"],[220,230,\"warn\"],[0,220,\"alarm\"],[220,260,\"normal\"]],\"shortName\":\"COG\",\"alarmMethod\":\"sound\",\"warnMethod\":\"visual\",\"displayName\":\"COG (True)\"},\"source\":\"sources.gps_0183_RMC\",\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"},\"value\":245.69}}}},\"sources\":{\"gps_0183_RMC\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"src\":\"$GPRMC,033025.000,A,4115.6426,S,17316.9300,E,0.05,245.69,090113,,*15\",\"bus\":\"/dev/ttyUSB1\"},\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json context = processor.handle(data);
		
		logger.debug(context);
		assertTrue(context.toString().indexOf("zones")>0);
		
	}
	
	@Test
	public void shouldAggregateBySource() {
		
		Json data = Json.read("{\"vessels\":{\"366982330\":{\"navigation\":{\"position\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"longitude\":173.1693,\"latitude\":-41.156426,\"source\":\"sources.gps_0183_RMC\",\"altitude\":0.0,\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"}},\"courseOverGroundTrue\":{\"timestamp\":\"2015-03-07T11:51:57.723+13:00\",\"meta\":{\"zones\":[[250,260,\"warn\"],[260,360,\"alarm\"],[220,230,\"warn\"],[0,220,\"alarm\"],[220,260,\"normal\"]],\"shortName\":\"COG\",\"alarmMethod\":\"sound\",\"warnMethod\":\"visual\",\"displayName\":\"COG (True)\"},\"source\":\"sources.gps_0183_RMC\",\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"},\"value\":245.69}}}},\"sources\":{\"gps_0183_RMC\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"src\":\"$GPRMC,033025.000,A,4115.6426,S,17316.9300,E,0.05,245.69,090113,,*15\",\"bus\":\"/dev/ttyUSB1\"},\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json context = processor.handle(data);
		
		logger.debug(context);
		assertTrue(context.toString().indexOf("sources.gps_0183_RMC")>0);
		assertFalse(context.toString().indexOf("sources.gps_0183_RMC_1")>0);
		
	}
	
	@Test
	public void shouldNotAggregateBySource() {
		
		Json data = Json.read("{\"vessels\":{\"366982330\":{\"navigation\":{\"position\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"longitude\":173.1693,\"latitude\":-41.156426,\"source\":\"sources.gps_0183_RMC_1\",\"altitude\":0.0,\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"}},\"courseOverGroundTrue\":{\"timestamp\":\"2015-03-07T11:51:57.723+13:00\",\"meta\":{\"zones\":[[250,260,\"warn\"],[260,360,\"alarm\"],[220,230,\"warn\"],[0,220,\"alarm\"],[220,260,\"normal\"]],\"shortName\":\"COG\",\"alarmMethod\":\"sound\",\"warnMethod\":\"visual\",\"displayName\":\"COG (True)\"},\"source\":\"sources.gps_0183_RMC\",\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"},\"value\":245.69}}}},\"sources\":{\"gps_0183_RMC\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"src\":\"$GPRMC,033025.000,A,4115.6426,S,17316.9300,E,0.05,245.69,090113,,*15\",\"bus\":\"/dev/ttyUSB1\"},\"_attr\":{\"_mode\":644,\"_owner\":\"self\",\"_group\":\"self\"}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json context = processor.handle(data);
		
		logger.debug(context);
		assertTrue(context.toString().indexOf("sources.gps_0183_RMC")>0);
		assertTrue(context.toString().indexOf("sources.gps_0183_RMC_1")>0);
		
	}
	
	
	
}
