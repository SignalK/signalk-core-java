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

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.ConfigConstants;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.log4j.Logger;
import org.junit.After;
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
