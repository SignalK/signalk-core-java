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

import static nz.co.fortytwo.signalk.util.SignalKConstants.CONTEXT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.PATH;
import static nz.co.fortytwo.signalk.util.SignalKConstants.UPDATES;
import static nz.co.fortytwo.signalk.util.SignalKConstants.source;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;
import static nz.co.fortytwo.signalk.util.SignalKConstants.value;
import static nz.co.fortytwo.signalk.util.SignalKConstants.values;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.ConfigConstants;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FullToDeltaConverterTest {
	private static Logger logger = LogManager.getLogger(FullToDeltaConverterTest.class);
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void shouldCreateDelta() {
		Json data = Json
				.read("{\"vessels\":{\"motu\":{\"navigation\":{\"courseOverGroundTrue\":{\"timestamp\":\"2015-03-18T04:53:26.367Z\",\"source\":{\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"src\":\"115\",\"device\":\"/dev/actisense\",\"pgn\":\"128267\"},\"value\":3.0176},\"speedOverGround\":{\"timestamp\":\"2015-03-18T04:53:26.462Z\",\"source\":{\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"src\":\"115\",\"device\":\"/dev/actisense\",\"pgn\":\"128267\"},\"value\":3.85}}}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json out = processor.handle(data).get(0);
		logger.debug(out);
		
	}
	
	@Test
	public void shouldCreateDelta1() {
		Json data = Json
				.read("{\"vessels\":{\"motu\":{\"navigation\":{\"position\":{\"timestamp\":\"2015-04-02T00:13:05.329Z\",\"longitude\":23.52916584,\"latitude\":60.07603504,\"source\":\"sources.nmea.0183.GLL\"}}}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		logger.debug("Input:"+data);
		Json out = processor.handle(data).get(0);
		logger.debug("Output:"+out);
		assertNotNull(out);
		
	}
	
	
	
	@Test
	public void shouldGetContext() {
		
		Json data = Json.read("{\"vessels\":{\"366982330\":{\"navigation\":{\"position\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"longitude\":173.1693,\"latitude\":-41.156426,\"source\":\"sources.gps_0183_RMC\",\"altitude\":0.0,\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"}},\"courseOverGroundTrue\":{\"timestamp\":\"2015-03-07T11:51:57.723+13:00\",\"meta\":{\"zones\":[[250,260,\"warn\"],[260,360,\"alarm\"],[220,230,\"warn\"],[0,220,\"alarm\"],[220,260,\"normal\"]],\"shortName\":\"COG\",\"alarmMethod\":\"sound\",\"warnMethod\":\"visual\",\"displayName\":\"COG (True)\"},\"source\":\"sources.gps_0183_RMC\",\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"},\"value\":245.69}}}},\"sources\":{\"gps_0183_RMC\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"src\":\"$GPRMC,033025.000,A,4115.6426,S,17316.9300,E,0.05,245.69,090113,,*15\",\"bus\":\"/dev/ttyUSB1\"},\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json context = processor.getContext(data.at(SignalKConstants.vessels));
		
		logger.debug(context);
		logger.debug(context.getPath());
		assertEquals("vessels.366982330.navigation", context.getPath());

	}
	
	@Test
	public void shouldHandleArray() {
		
		Json data = Json.read("{\"vessels\":{\"366982330\":{\"navigation\":{\"position\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"longitude\":173.1693,\"latitude\":-41.156426,\"source\":\"sources.gps_0183_RMC\",\"altitude\":0.0,\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"}},\"courseOverGroundTrue\":{\"timestamp\":\"2015-03-07T11:51:57.723+13:00\",\"meta\":{\"zones\":[[250,260,\"warn\"],[260,360,\"alarm\"],[220,230,\"warn\"],[0,220,\"alarm\"],[220,260,\"normal\"]],\"shortName\":\"COG\",\"alarmMethod\":\"sound\",\"warnMethod\":\"visual\",\"displayName\":\"COG (True)\"},\"source\":\"sources.gps_0183_RMC\",\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"},\"value\":245.69}}}},\"sources\":{\"gps_0183_RMC\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"src\":\"$GPRMC,033025.000,A,4115.6426,S,17316.9300,E,0.05,245.69,090113,,*15\",\"bus\":\"/dev/ttyUSB1\"},\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json context = processor.handle(data).get(0);
		
		logger.debug(context);
		assertTrue(context.toString().indexOf("zones")>0);
		
	}
	
	@Test
	public void shouldAggregateBySource() {
		
		Json data = Json.read("{\"vessels\":{\"366982330\":{\"navigation\":{\"position\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"longitude\":173.1693,\"latitude\":-41.156426,\"source\":\"sources.gps_0183_RMC\",\"altitude\":0.0,\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"}},\"courseOverGroundTrue\":{\"timestamp\":\"2015-03-07T11:51:57.723+13:00\",\"meta\":{\"zones\":[[250,260,\"warn\"],[260,360,\"alarm\"],[220,230,\"warn\"],[0,220,\"alarm\"],[220,260,\"normal\"]],\"shortName\":\"COG\",\"alarmMethod\":\"sound\",\"warnMethod\":\"visual\",\"displayName\":\"COG (True)\"},\"source\":\"sources.gps_0183_RMC\",\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"},\"value\":245.69}}}},\"sources\":{\"gps_0183_RMC\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"src\":\"$GPRMC,033025.000,A,4115.6426,S,17316.9300,E,0.05,245.69,090113,,*15\",\"bus\":\"/dev/ttyUSB1\"},\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json context = processor.handle(data).get(0);
		
		logger.debug(context);
		assertTrue(context.toString().indexOf("sources.gps_0183_RMC")>0);
		assertFalse(context.toString().indexOf("sources.gps_0183_RMC_1")>0);
		
	}
	
	@Test
	public void shouldNotAggregateBySource() {
		
		Json data = Json.read("{\"vessels\":{\"366982330\":{\"navigation\":{\"position\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"longitude\":173.1693,\"latitude\":-41.156426,\"source\":\"sources.gps_0183_RMC_1\",\"altitude\":0.0,\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"}},\"courseOverGroundTrue\":{\"timestamp\":\"2015-03-07T11:51:57.723+13:00\",\"meta\":{\"zones\":[[250,260,\"warn\"],[260,360,\"alarm\"],[220,230,\"warn\"],[0,220,\"alarm\"],[220,260,\"normal\"]],\"shortName\":\"COG\",\"alarmMethod\":\"sound\",\"warnMethod\":\"visual\",\"displayName\":\"COG (True)\"},\"source\":\"sources.gps_0183_RMC\",\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"},\"value\":245.69}}}},\"sources\":{\"gps_0183_RMC\":{\"timestamp\":\"2015-03-07T11:51:57.904+13:00\",\"src\":\"$GPRMC,033025.000,A,4115.6426,S,17316.9300,E,0.05,245.69,090113,,*15\",\"bus\":\"/dev/ttyUSB1\"},\"_attr\":{\"_mode\":644,\"_owner\":\"SignalKConstants.self\",\"_group\":\"SignalKConstants.self\"}}}");
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json context = processor.handle(data).get(0);
		
		logger.debug(context);
		assertTrue(context.toString().indexOf("sources.gps_0183_RMC")>0);
		assertTrue(context.toString().indexOf("sources.gps_0183_RMC_1")>0);
		
	}
	
	@Test
	public void shouldConvertNotifications() throws IOException{
		SignalKModel model = SignalKModelFactory.getCleanInstance();
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.notifications.navigation.anchor.currentRadius.alarmState","normal");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.notifications.navigation.anchor.currentRadius.message","");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.notifications.navigation.anchor.currentRadius.alarmState","normal");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.altitude",0.0);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.latitude",56.08612787724117);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.longitude",21.891184134073562);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.pgn",123456);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.timestamp","2016-03-14T08:15:40.418Z");
		//get full json
		JsonSerializer ser = new JsonSerializer();
		Json json = ser.writeJson(model);
		//to Delta
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json delta = processor.handle(json).get(0);
		logger.debug(delta);
		
		//now check
		assertEquals("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b",delta.at("context").asString());
		Json updates = delta.at("updates");
		assertEquals(2, updates.asList().size());
		assertEquals("2016-03-14T08:15:40.418Z",updates.asJsonList().get(0).at("timestamp").asString());
		assertEquals("testLabel",updates.asJsonList().get(0).at("source").at("label").asString());
		List<Json> valuesList = updates.asJsonList().get(0).at("values").asJsonList();
		assertEquals("navigation.position", valuesList.get(0).at("path").asString());
		Json val = valuesList.get(0).at("value");
		assertEquals(21.89118413d , val.at("longitude").asDouble(), 0.00001);
		assertEquals(56.08612788d , val.at("latitude").asDouble(), 0.00001);
		assertEquals(0.0d , val.at("altitude").asDouble(), 0.00001);
	}
	
	
	@Test
	public void shouldConvertPosition() throws IOException{
		SignalKModel model = SignalKModelFactory.getCleanInstance();
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.altitude",0.0);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.latitude",56.08612787724117);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.longitude",21.891184134073562);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.pgn",123456);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b.navigation.position.timestamp","2016-03-14T08:15:40.418Z");
		//get full json
		JsonSerializer ser = new JsonSerializer();
		Json json = ser.writeJson(model);
		//to Delta
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json delta = processor.handle(json).get(0);
		logger.debug(delta);
		//now check
		assertEquals("vessels.urn:mrn:signalk:uuid:c42e5095-d3c3-49b6-a317-c633464a4f2b",delta.at("context").asString());
		Json updates = delta.at("updates");
		assertEquals("2016-03-14T08:15:40.418Z",updates.asJsonList().get(0).at("timestamp").asString());
		assertEquals("testLabel",updates.asJsonList().get(0).at("source").at("label").asString());
		List<Json> valuesList = updates.asJsonList().get(0).at("values").asJsonList();
		assertEquals("navigation.position", valuesList.get(0).at("path").asString());
		Json val = valuesList.get(0).at("value");
		assertEquals(21.89118413d , val.at("longitude").asDouble(), 0.00001);
		assertEquals(56.08612788d , val.at("latitude").asDouble(), 0.00001);
		assertEquals(0.0d , val.at("altitude").asDouble(), 0.00001);
	}
	
	@Test
	public void shouldConvertPosition2() throws IOException{
		SignalKModel model = SignalKModelFactory.getCleanInstance();
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.courseOverGroundMagnetic.$source","nmea.0183.VHW");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.courseOverGroundMagnetic.timestamp","2016-03-30T08:05:46.983Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.courseOverGroundMagnetic.value",5.28834763);
		//model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.courseOverGroundMagnetic.values.nmea.0183.HDM.$source","nmea.0183.HDM");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.courseOverGroundMagnetic.values.nmea.0183.HDM.timestamp","2016-03-30T08:05:38.546Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.courseOverGroundMagnetic.values.nmea.0183.HDM.value",5.28834763);
		//model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.courseOverGroundMagnetic.values.nmea.0183.VHW.$source","nmea.0183.VHW");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.courseOverGroundMagnetic.values.nmea.0183.VHW.timestamp","2016-03-30T08:05:46.983Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.courseOverGroundMagnetic.values.nmea.0183.VHW.value",5.28834763);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.position.$source","nmea.0183.RMC");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.position.altitude",0.0);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.position.latitude",37.81306667);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.position.longitude",-122.44718333);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.position.timestamp","2016-03-30T08:06:18.556Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.speedOverGround.$source","nmea.0183.RMC");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.speedOverGround.timestamp","2016-03-30T08:06:18.546Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.speedOverGround.value",1.61298375);
		//model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.speedOverGround.values.nmea.0183.RMC.$source","nmea.0183.RMC");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.speedOverGround.values.nmea.0183.RMC.timestamp","2016-03-30T08:06:18.546Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.speedOverGround.values.nmea.0183.RMC.value",1.61298375);
		//model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.speedOverGround.values.nmea.0183.VHW.$source","nmea.0183.VHW");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.speedOverGround.values.nmea.0183.VHW.timestamp","2016-03-30T08:05:46.983Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5.navigation.speedOverGround.values.nmea.0183.VHW.value",1.1124765);
		//get full json
		JsonSerializer ser = new JsonSerializer();
		Json json = ser.writeJson(model);
		logger.debug(json);
		//to Delta
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json delta = processor.handle(json).get(0);
		logger.debug(delta);
		//now check
		assertEquals("vessels.urn:mrn:signalk:uuid:6b0e776f-811a-4b35-980e-b93405371bc5",delta.at("context").asString());
		Json updates = delta.at("updates");
		assertEquals("2016-03-30T08:06:18.556Z",updates.asJsonList().get(2).at("timestamp").asString());
		//assertEquals("testLabel",updates.asJsonList().get(0).at("source").at("label").asString());
		List<Json> valuesList = updates.asJsonList().get(2).at("values").asJsonList();
		assertEquals("navigation.position", valuesList.get(0).at("path").asString());
		Json val = valuesList.get(0).at("value");
		assertEquals(-122.44718333d , val.at("longitude").asDouble(), 0.00001);
		assertEquals(37.81306667d , val.at("latitude").asDouble(), 0.00001);
		assertEquals(0.0d , val.at("altitude").asDouble(), 0.00001);
	}
	@Test
	public void shouldConvertNavigation() throws IOException{
		SignalKModel model = SignalKModelFactory.getCleanInstance();

		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundMagnetic.pgn",21.406661494994307);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundMagnetic.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundMagnetic.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundMagnetic.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundMagnetic.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundMagnetic.value",24.99888661232309);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundMagnetic.values.b5d1104.pgn",10.98299199970798);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundMagnetic.values.b5d1104.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundMagnetic.values.b5d1104.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundMagnetic.values.b5d1104.value",20.523181765927777);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundTrue.pgn",47.961135869778914);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundTrue.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundTrue.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundTrue.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundTrue.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundTrue.value",21.577254828573665);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundTrue.values.c116f44.pgn",49.98039875805297);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundTrue.values.c116f44.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundTrue.values.c116f44.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.courseOverGroundTrue.values.c116f44.value",29.234137464073527);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingMagnetic.pgn",65.95645592408168);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingMagnetic.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingMagnetic.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingMagnetic.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingMagnetic.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingMagnetic.value",13.918538822712634);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingMagnetic.values.ec87399.pgn",0.22110212431679654);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingMagnetic.values.ec87399.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingMagnetic.values.ec87399.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingMagnetic.values.ec87399.value",78.7534528174313);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingTrue.pgn",50.846242235996556);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingTrue.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingTrue.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingTrue.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingTrue.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingTrue.value",99.96161913730467);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingTrue.values.b8defda.pgn",38.61428260398605);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingTrue.values.b8defda.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingTrue.values.b8defda.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.headingTrue.values.b8defda.value",64.08349014224515);
		
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.position.altitude",0.0);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.position.latitude",77.65327885982339);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.position.longitude",96.98441049156695);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.position.pgn",81.58427978544627);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.position.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.position.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.position.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.position.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedOverGround.pgn",86.45359814901583);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedOverGround.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedOverGround.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedOverGround.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedOverGround.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedOverGround.value",57.448476349323705);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedOverGround.values.c327297.pgn",90.60234199140433);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedOverGround.values.c327297.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedOverGround.values.c327297.timestamp","2016-03-14T08:51:56.744Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedOverGround.values.c327297.value",46.77694243864312);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedThroughWater.pgn",56.62465022638501);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedThroughWater.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedThroughWater.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedThroughWater.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedThroughWater.timestamp","2016-03-14T08:51:57.727Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedThroughWater.value",74.59723282465691);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedThroughWater.values.d0267df.pgn",49.711687770743886);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedThroughWater.values.d0267df.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedThroughWater.values.d0267df.timestamp","2016-03-14T08:51:57.727Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2.navigation.speedThroughWater.values.d0267df.value",41.95402030596676);
		
		//get full json
		JsonSerializer ser = new JsonSerializer();
		Json json = ser.writeJson(model);
		//to Delta
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json delta = processor.handle(json).get(0);
		logger.debug(delta);
		//now check
		assertEquals("vessels.urn:mrn:signalk:uuid:9119b97a-19ee-4f45-a27f-a9a99ce0d0c2",delta.at("context").asString());
		Json updates = delta.at("updates");
		assertEquals("2016-03-14T08:51:56.744Z",updates.asJsonList().get(0).at("timestamp").asString());
		assertEquals("testLabel",updates.asJsonList().get(2).at("source").at("label").asString());
		List<Json> valuesList = updates.asJsonList().get(2).at("values").asJsonList();
		assertEquals("navigation.position", valuesList.get(2).at("path").asString());
		Json val = valuesList.get(2).at("value");
		assertEquals(96.98441049156695d , val.at("longitude").asDouble(), 0.00001);
		assertEquals(77.65327885982339d , val.at("latitude").asDouble(), 0.00001);
		assertEquals(0.0d , val.at("altitude").asDouble(), 0.00001);
		//should be 2 updates
		assertEquals("2016-03-14T08:51:57.727Z",updates.asJsonList().get(1).at("timestamp").asString());
	}
	
	@Test
	public void shouldConvertToDelta() throws IOException {
		//first get the full version
		SignalKModel model = getNavSample();
		JsonSerializer ser = new JsonSerializer();
		Json fullJson = ser.writeJson(model);
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		List<Json> context = processor.handle(fullJson);
		
		logger.debug("Output:"+context);
		assertTrue(context.get(0).has(CONTEXT)&&context.get(0).has(UPDATES));
		
		List<Json> updates = context.get(0).at(UPDATES).asJsonList();
		for(Json entry:updates){
			assertTrue(entry.has(timestamp));
			assertTrue(entry.has(source));
			for(Json j:entry.at(values).asJsonList()){
				assertTrue(j.has(PATH));
				assertTrue(j.has(value));
			}
		}
		//assertTrue(context.get(0).has(values)||context.get(1).has(values));
		//assertTrue(context.toString().indexOf("sources.gps_0183_RMC_1")>0);
		
	}
	
	private SignalKModel getNavSample(){
		SignalKModel model = SignalKModelFactory.getMotuTestInstance();
		model.getFullData().clear();
		

		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundMagnetic.pgn",13.195956857064918);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundMagnetic.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundMagnetic.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundMagnetic.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundMagnetic.timestamp","2016-03-02T08:37:22.660Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundMagnetic.value",48.610374899944574);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundMagnetic.values.bfd943b.pgn",18.685895890396388);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundMagnetic.values.bfd943b.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundMagnetic.values.bfd943b.timestamp","2016-03-02T08:37:22.662Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundMagnetic.values.bfd943b.value",75.38115243763873);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundTrue.pgn",72.50817331074792);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundTrue.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundTrue.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundTrue.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundTrue.timestamp","2016-03-02T08:37:22.663Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundTrue.value",5.580066902373082);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundTrue.values.6c1018a.pgn",54.010689127775294);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundTrue.values.6c1018a.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundTrue.values.6c1018a.timestamp","2016-03-02T08:37:22.665Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.courseOverGroundTrue.values.6c1018a.value",67.45841514818161);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingMagnetic.pgn",66.17690205054507);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingMagnetic.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingMagnetic.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingMagnetic.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingMagnetic.timestamp","2016-03-02T08:37:22.653Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingMagnetic.value",62.982190136888626);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingMagnetic.values.d91ba64.pgn",1.8837455222973865);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingMagnetic.values.d91ba64.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingMagnetic.values.d91ba64.timestamp","2016-03-02T08:37:22.654Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingMagnetic.values.d91ba64.value",59.144773525036385);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingTrue.pgn",63.65455526177374);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingTrue.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingTrue.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingTrue.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingTrue.timestamp","2016-03-02T08:37:22.646Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingTrue.value",42.596333126174414);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingTrue.values.4dc1ffe.pgn",40.51442614595232);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingTrue.values.4dc1ffe.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingTrue.values.4dc1ffe.timestamp","2016-03-02T08:37:22.647Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.headingTrue.values.4dc1ffe.value",2.922123462346582);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.position.altitude",9.85701886187792);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.position.latitude",31.848983568975385);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.position.longitude",54.66022943898835);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.position.pgn",80.83392213858801);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.position.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.position.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.position.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.position.timestamp","2016-03-02T08:37:22.637Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedOverGround.pgn",58.604728414050754);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedOverGround.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedOverGround.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedOverGround.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedOverGround.timestamp","2016-03-02T08:37:22.670Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedOverGround.value",71.92208143118758);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedOverGround.values.7c8dcde.pgn",82.07203567331553);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedOverGround.values.7c8dcde.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedOverGround.values.7c8dcde.timestamp","2016-03-02T08:37:22.672Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedOverGround.values.7c8dcde.value",47.822729862734946);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedThroughWater.pgn",14.662963206277023);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedThroughWater.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedThroughWater.source.label","testLabel");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedThroughWater.source.type","testType");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedThroughWater.timestamp","2016-03-02T08:37:22.643Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedThroughWater.value",21.144652173398836);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedThroughWater.values.a4e7c86.pgn",75.996691910829);
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedThroughWater.values.a4e7c86.sentence","ipsum");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedThroughWater.values.a4e7c86.timestamp","2016-03-02T08:37:22.644Z");
		model.getFullData().put("vessels.urn:mrn:signalk:uuid:2ad6e75a-41bb-4c50-afbf-9c260f77b1d6.navigation.speedThroughWater.values.a4e7c86.value",7.672131079336775);
				
		 return model;
	}
	
}
