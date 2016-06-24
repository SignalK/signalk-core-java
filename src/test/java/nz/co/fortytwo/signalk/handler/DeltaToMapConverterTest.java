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
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nz.co.fortytwo.signalk.handler;

import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundMagnetic;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedOverGround;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedThroughWater;
import static nz.co.fortytwo.signalk.util.SignalKConstants.self;
import static nz.co.fortytwo.signalk.util.SignalKConstants.source;
import static nz.co.fortytwo.signalk.util.SignalKConstants.sourceRef;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;
import static nz.co.fortytwo.signalk.util.SignalKConstants.value;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeltaToMapConverterTest {

	String jsonDiff = "{\"context\": \"vessels."
			+ self
			+ ".navigation\",\"updates\":[{\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"source\": {\"type\" : \"n2k\",\"device\" : \"/dev/actisense\",\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"src\":\"115\",\"pgn\":\"128267\"},\"values\": [{ \"path\": \"courseOverGroundTrue\",\"value\": 3.0176 },{ \"path\": \"speedOverGround\",\"value\": 3.85 }]}]}";
	String jsonDiff1 = "{\"context\": \"vessels."
			+ self
			+ "\",\"updates\":[{\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"source\": {\"type\" : \"n2k\",\"device\" : \"/dev/actisense\", \"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"src\":\"115\",\"pgn\":\"128267\"},\"values\": [{ \"path\": \"navigation.courseOverGroundTrue\",\"value\": 3.0176 },{ \"path\": \"navigation.speedOverGround\",\"value\": 3.85 }]}]}";
	String jsonDiff2 = "{\"context\": \"vessels."
			+ self
			+ ".navigation\",\"updates\":[{\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"source\": {\"type\" : \"n2k\",\"device\" : \"/dev/actisense\",\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"src\":\"115\",\"pgn\":\"128267\"},\"values\": [{ \"path\": \"courseOverGroundTrue\",\"value\": 3.0176 },{ \"path\": \"speedOverGround\",\"value\": 3.85 }]},{\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"source\": {\"type\" : \"n2k\",\"device\" : \"/dev/ttyUSB0\",\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"src\":\"115\",\"pgn\":\"128267\"},\"values\": [{ \"path\": \"courseOverGroundMagnetic\",\"value\": 152.9 },{ \"path\": \"speedThroughWater\",\"value\": 2.85 }]}]}";
	String jsonDiff3 = "{\"updates\":[{\"values\":[{\"value\":3.0176,\"path\":\"courseOverGroundTrue\"},{\"value\":3.85,\"path\":\"speedOverGround\"}],\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"source\":{\"type\" : \"n2k\",\"timestamp\":\"2014-08-15T16:00:00.081+00:00\",\"device\":\"/dev/actisense\",\"pgn\":\"128267\",\"src\":\"115\"}}],\"context\":\"vessels."
			+ self + ".navigation\"}";
	private static Logger logger = LogManager.getLogger(DeltaToMapConverterTest.class);

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldSetAnchorWatch() throws Exception {
		Json watch = Json.read(FileUtils.readFileToString(new File("src/test/resources/samples/anchorWatchSet.json")));
		DeltaToMapConverter processor = new DeltaToMapConverter();
		SignalKModel output = processor.handle(watch);
		logger.debug(output);
		assertEquals("sound", output.get("vessels.self.navigation.anchor.currentRadius.meta.alarmMethod"));
		assertEquals("Anchor Watch", output.get("vessels.self.navigation.anchor.currentRadius.meta.displayName"));
		assertEquals("Anchor Watch", output.get("vessels.self.navigation.anchor.currentRadius.meta.shortName"));
		assertEquals("vessels.self", output.get("vessels.self.navigation.anchor.currentRadius.meta.source"));
		assertEquals("2015-05-08T06:29:26.455Z", output.get("vessels.self.navigation.anchor.currentRadius.meta.timestamp"));
		assertEquals("visual", output.get("vessels.self.navigation.anchor.currentRadius.meta.warnMethod"));
		assertEquals(Json.read("[[0,50,\"normal\"],[50,999999,\"alarm\"]]"), output.get("vessels.self.navigation.anchor.currentRadius.meta.zones"));
		assertEquals(50l, output.getValue("vessels.self.navigation.anchor.maxRadius"));

		assertEquals(60.08087031, output.get("vessels.self.navigation.anchor.position.latitude"));
		assertEquals(23.53406503, output.get("vessels.self.navigation.anchor.position.longitude"));

	}

	@Test
	public void shouldReadRMCDelta() throws Exception {
		Json watch = Json.read(FileUtils.readFileToString(new File("src/test/resources/samples/rmcDeltaFormat.json")));
		DeltaToMapConverter processor = new DeltaToMapConverter();
		SignalKModel output = processor.handle(watch);
		logger.debug(output);
		assertEquals(51.9485185, output.get("vessels.motu.navigation.position.latitude"));
		assertEquals(4.58006417, output.get("vessels.motu.navigation.position.longitude"));
		assertEquals("vessels.motu.sources.nmea.0183.RMC", output.get("vessels.motu.navigation.position.source"));
		assertEquals("2015-03-23T01:57:02.256Z", output.get("vessels.motu.navigation.position.timestamp"));

		assertEquals(0.1517598, output.get("vessels.motu.navigation.speedOverGround.value"));

	}

	@Test
	public void shouldProcessDiff() throws Exception {
		Json diff = Json.read(jsonDiff);
		DeltaToMapConverter processor = new DeltaToMapConverter();
		SignalKModel output = processor.handle(diff);
		logger.debug(output);
		assertEquals(3.0176, (double) output.getValue(vessels_dot_self_dot + nav_courseOverGroundTrue), 001);
		assertEquals("2014-08-15T16:00:00.081+00:00", output.get(vessels_dot_self_dot + nav_courseOverGroundTrue + ".timestamp"));
		assertEquals("/dev/actisense", output.get(vessels_dot_self_dot + nav_courseOverGroundTrue + "." + source + ".device"));

		assertEquals(3.85, (double) output.get(vessels_dot_self_dot + nav_speedOverGround + "." + value), 001);
		assertEquals("2014-08-15T16:00:00.081+00:00", output.get(vessels_dot_self_dot + nav_speedOverGround +  ".timestamp"));
		assertEquals("/dev/actisense", output.get(vessels_dot_self_dot + nav_speedOverGround + "." + source + ".device"));
	}

	@Test
	public void shouldProcessDiff3() throws Exception {
		Json diff = Json.read(jsonDiff3);
		DeltaToMapConverter processor = new DeltaToMapConverter();
		SignalKModel output = processor.handle(diff);
		logger.debug(output);
		assertEquals(3.0176, (double) output.get(vessels_dot_self_dot + nav_courseOverGroundTrue + "." + value), 001);
		assertEquals("2014-08-15T16:00:00.081+00:00", output.get(vessels_dot_self_dot + nav_courseOverGroundTrue +".timestamp"));
		assertEquals("/dev/actisense", output.get(vessels_dot_self_dot + nav_courseOverGroundTrue + "." + source + ".device"));

		assertEquals(3.85, (double) output.get(vessels_dot_self_dot + nav_speedOverGround + "." + value), 001);
		assertEquals("2014-08-15T16:00:00.081+00:00", output.get(vessels_dot_self_dot + nav_speedOverGround + ".timestamp"));
		assertEquals("/dev/actisense", output.get(vessels_dot_self_dot + nav_speedOverGround + "." + source + ".device"));
	}

	@Test
	public void shouldIgnoreSignalKJson() throws Exception {
		Json diff = Json
				.read("{\"vessels\":{\""
						+ self
						+ "\":{\"navigation\":{\"courseOverGroundTrue\": {\"value\":11.9600000381},\"courseOverGroundMagnetic\": {\"value\":93.0000000000},\"headingMagnetic\": {\"value\":0.0000000000},\"magneticVariation\": {\"value\":0.0000000000},\"headingTrue\": {\"value\":0.0000000000},\"pitch\": {\"value\":0.0000000000},\"rateOfTurn\": {\"value\":0.0000000000},\"roll\": {\"value\":0.0000000000},\"speedOverGround\": {\"value\":0.0399999980},\"speedThroughWater\": {\"value\":0.0000000000},\"state\": {\"value\":\"Not defined (example)\"},\"anchor\":{\"alarmRadius\": {\"value\":0.0000000000},\"maxRadius\": {\"value\":0.0000000000},\"position\":{\"latitude\": {\"value\":-41.2936935424},\"longitude\": {\"value\":173.2470855712},\"altitude\": {\"value\":0.0000000000}}},\"position\":{\"latitude\": {\"value\":-41.2936935424},\"longitude\": {\"value\":173.2470855712},\"altitude\": {\"value\":0.0000000000}}},\"alarm\":{\"anchorAlarmMethod\": {\"value\":\"sound\"},\"anchorAlarmState\": {\"value\":\"disabled\"},\"autopilotAlarmMethod\": {\"value\":\"sound\"},\"autopilotAlarmState\": {\"value\":\"disabled\"},\"engineAlarmMethod\": {\"value\":\"sound\"},\"engineAlarmState\": {\"value\":\"disabled\"},\"fireAlarmMethod\": {\"value\":\"sound\"},\"fireAlarmState\": {\"value\":\"disabled\"},\"gasAlarmMethod\": {\"value\":\"sound\"},\"gasAlarmState\": {\"value\":\"disabled\"},\"gpsAlarmMethod\": {\"value\":\"sound\"},\"gpsAlarmState\": {\"value\":\"disabled\"},\"maydayAlarmMethod\": {\"value\":\"sound\"},\"maydayAlarmState\": {\"value\":\"disabled\"},\"panpanAlarmMethod\": {\"value\":\"sound\"},\"panpanAlarmState\": {\"value\":\"disabled\"},\"powerAlarmMethod\": {\"value\":\"sound\"},\"powerAlarmState\": {\"value\":\"disabled\"},\"silentInterval\": {\"value\":300},\"windAlarmMethod\": {\"value\":\"sound\"},\"windAlarmState\": {\"value\":\"disabled\"},\"genericAlarmMethod\": {\"value\":\"sound\"},\"genericAlarmState\": {\"value\":\"disabled\"},\"radarAlarmMethod\": {\"value\":\"sound\"},\"radarAlarmState\": {\"value\":\"disabled\"},\"mobAlarmMethod\": {\"value\":\"sound\"},\"mobAlarmState\": {\"value\":\"disabled\"}},\"steering\":{\"rudderAngle\": {\"value\":0.0000000000},\"rudderAngleTarget\": {\"value\":0.0000000000},\"autopilot\":{\"state\": {\"value\":\"off\"},\"mode\": {\"value\":\"powersave\"},\"targetHeadingNorth\": {\"value\":0.0000000000},\"targetHeadingMagnetic\": {\"value\":0.0000000000},\"alarmHeadingXte\": {\"value\":0.0000000000},\"headingSource\": {\"value\":\"compass\"},\"dead+00:00one\": {\"value\":0.0000000000},\"backlash\": {\"value\":0.0000000000},\"gain\": {\"value\":0},\"maxDriveAmps\": {\"value\":0.0000000000},\"maxDriveRate\": {\"value\":0.0000000000},\"portLock\": {\"value\":0.0000000000},\"starboardLock\": {\"value\":0.0000000000}}},\"environment\":{\"airPressureChangeRateAlarm\": {\"value\":0.0000000000},\"airPressure\": {\"value\":1024.0000000000},\"waterTemp\": {\"value\":0.0000000000},\"wind\":{\"speedAlarm\": {\"value\":0.0000000000},\"directionChangeAlarm\": {\"value\":0.0000000000},\"angleApparent\": {\"value\":0.0000000000},\"directionTrue\": {\"value\":256.3},\"speedApparent\": {\"value\":0.0000000000},\"speedTrue\": {\"value\":7.68}}}}}}");
		DeltaToMapConverter processor = new DeltaToMapConverter();
		SignalKModel output = processor.handle(diff);
		logger.debug(output);
		assertNull(output);
	}

	@Test
	public void shouldIgnoreRandomJson() throws Exception {
		Json diff = Json.read("{\"headingTrue\": {\"value\": 23,\"source\": \"" + self + "\",\"timestamp\": \"2014-03-24T00: 15: 41+00:00\" }}");
		DeltaToMapConverter processor = new DeltaToMapConverter();
		SignalKModel output = processor.handle(diff);
		logger.debug(output);
		assertNull(output);
	}

	@Test
	public void shouldProcessComplexDiff() throws Exception {
		Json diff = Json.read(jsonDiff1);
		DeltaToMapConverter processor = new DeltaToMapConverter();
		SignalKModel output = processor.handle(diff);
		logger.debug(output);
		assertEquals(3.0176, (double) output.get(vessels_dot_self_dot + nav_courseOverGroundTrue + "." + value), 001);
		assertEquals("2014-08-15T16:00:00.081+00:00", output.get(vessels_dot_self_dot + nav_courseOverGroundTrue + ".timestamp"));
		assertEquals("/dev/actisense", output.get(vessels_dot_self_dot + nav_courseOverGroundTrue + "." + source + ".device"));

		assertEquals(3.85, (double) output.get(vessels_dot_self_dot + nav_speedOverGround + "." + value), 001);
		assertEquals("2014-08-15T16:00:00.081+00:00", output.get(vessels_dot_self_dot + nav_speedOverGround +  ".timestamp"));
		assertEquals("/dev/actisense", output.get(vessels_dot_self_dot + nav_speedOverGround + "." + source + ".device"));
	}

	@Test
	public void shouldProcessDiffArray() throws Exception {
		Json diff = Json.read(jsonDiff2);
		DeltaToMapConverter processor = new DeltaToMapConverter();
		SignalKModel output = processor.handle(diff);
		logger.debug(output);
		assertEquals(3.0176, (double) output.get(vessels_dot_self_dot + nav_courseOverGroundTrue + "." + value), 001);
		assertEquals("2014-08-15T16:00:00.081+00:00", output.get(vessels_dot_self_dot + nav_courseOverGroundTrue + "." + timestamp) );
		assertEquals("/dev/actisense", output.get(vessels_dot_self_dot + nav_courseOverGroundTrue + "." + source + ".device"));

		assertEquals(3.85, (double) output.get(vessels_dot_self_dot + nav_speedOverGround + "." + value), 001);
		assertEquals("2014-08-15T16:00:00.081+00:00", output.get(vessels_dot_self_dot + nav_speedOverGround +  ".timestamp"));
		assertEquals("/dev/actisense", output.get(vessels_dot_self_dot + nav_speedOverGround + "." + source + ".device"));

		assertEquals(152.9, (double) output.get(vessels_dot_self_dot + nav_courseOverGroundMagnetic + "." + value), 001);
		assertEquals("2014-08-15T16:00:00.081+00:00", output.get(vessels_dot_self_dot + nav_courseOverGroundMagnetic  + ".timestamp"));
		assertEquals("/dev/ttyUSB0", output.get(vessels_dot_self_dot + nav_courseOverGroundMagnetic + "." + source + ".device"));

		assertEquals(2.85, (double) output.get(vessels_dot_self_dot + nav_speedThroughWater + "." + value), 001);
		assertEquals("2014-08-15T16:00:00.081+00:00", output.get(vessels_dot_self_dot + nav_speedThroughWater + ".timestamp"));
		assertEquals("/dev/ttyUSB0", output.get(vessels_dot_self_dot + nav_speedThroughWater + "." + source + ".device"));
	}
}
