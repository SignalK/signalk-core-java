package nz.co.fortytwo.signalk.util;

import static nz.co.fortytwo.signalk.util.JsonConstants.SELF;
import static nz.co.fortytwo.signalk.util.SignalKConstants.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.NavigableMap;

import mjson.Json;
import net.minidev.json.JSONArray;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.model.impl.SignalKModelImpl;
import nz.co.fortytwo.signalk.model.impl.SignalKModelImplTest;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JsonSerializerTest {

	private static Logger logger = Logger.getLogger(JsonSerializerTest.class);
	@BeforeClass
	public static void setUp() throws Exception {
		Util.getConfig();
		Util.setSelf("motu");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldCreateConfigModel() throws IOException {
		
		SignalKModel signalk = new SignalKModelImpl( );
		File jsonFile = new File("src/test/resources/samples/signalk-config-test.json");
		Json temp = Json.read(jsonFile.toURI().toURL());
		JsonSerializer ser = new JsonSerializer();
		signalk.putAll(ser.read(temp));
		logger.debug(signalk.getFullData());
		assertNotNull(signalk.get("config.server.security.config.ip"));
		
		String jsonOut = ser.write(signalk);
		logger.debug(jsonOut);
		
	}
	
	@Test
	public void shouldHandleNullArray() throws IOException {
		
		SignalKModel signalk = new SignalKModelImpl( );
		File jsonFile = new File("src/test/resources/samples/signalk-config-test.json");
		Json temp = Json.read(jsonFile.toURI().toURL());
		JsonSerializer ser = new JsonSerializer();
		signalk.putAll(ser.read(temp));
		logger.debug(signalk.getFullData());
		assertNotNull(signalk.get("config.server.security.config.ip"));
		assertEquals(null,signalk.get("config.server.security.deny.ip"));
		String jsonOut = ser.write(signalk);
		
		logger.debug(jsonOut);
		signalk = new SignalKModelImpl( );
		NavigableMap<String, Object> jsonMap = ser.read(jsonOut);
		signalk.putAll(jsonMap);
		String jsonMapOut=ser.write(signalk);
		logger.debug(jsonMapOut);
		Json json = Json.read(jsonMapOut);
		Json deny = json.at("config").at("server").at("security").at("deny").at("ip");
		assertTrue(deny.isArray());
		
	}
	

	@Test
	public void shouldCreateSignalkModel() throws IOException {
		
		JsonSerializer ser = new JsonSerializer();
		Object obj = ser.read("{\"vessels\":{\""+SELF+"\":{\"navigation\":{\"courseOverGroundTrue\": {\"value\":11.9600000381},\"courseOverGroundMagnetic\": {\"value\":93.0000000000},\"headingMagnetic\": {\"value\":0.0000000000},\"magneticVariation\": {\"value\":0.0000000000},\"headingTrue\": {\"value\":0.0000000000},\"pitch\": {\"value\":0.0000000000},\"rateOfTurn\": {\"value\":0.0000000000},\"roll\": {\"value\":0.0000000000},\"speedOverGround\": {\"value\":0.0399999980},\"speedThroughWater\": {\"value\":0.0000000000},\"state\": {\"value\":\"Not defined (example)\"},\"anchor\":{\"alarmRadius\": {\"value\":0.0000000000},\"maxRadius\": {\"value\":0.0000000000},\"position\":{\"latitude\": {\"value\":-41.2936935424},\"longitude\": {\"value\":173.2470855712},\"altitude\": {\"value\":0.0000000000}}},\"position\":{\"latitude\": {\"value\":-41.2936935424},\"longitude\": {\"value\":173.2470855712},\"altitude\": {\"value\":0.0000000000}}},\"alarm\":{\"anchorAlarmMethod\": {\"value\":\"sound\"},\"anchorAlarmState\": {\"value\":\"disabled\"},\"autopilotAlarmMethod\": {\"value\":\"sound\"},\"autopilotAlarmState\": {\"value\":\"disabled\"},\"engineAlarmMethod\": {\"value\":\"sound\"},\"engineAlarmState\": {\"value\":\"disabled\"},\"fireAlarmMethod\": {\"value\":\"sound\"},\"fireAlarmState\": {\"value\":\"disabled\"},\"gasAlarmMethod\": {\"value\":\"sound\"},\"gasAlarmState\": {\"value\":\"disabled\"},\"gpsAlarmMethod\": {\"value\":\"sound\"},\"gpsAlarmState\": {\"value\":\"disabled\"},\"maydayAlarmMethod\": {\"value\":\"sound\"},\"maydayAlarmState\": {\"value\":\"disabled\"},\"panpanAlarmMethod\": {\"value\":\"sound\"},\"panpanAlarmState\": {\"value\":\"disabled\"},\"powerAlarmMethod\": {\"value\":\"sound\"},\"powerAlarmState\": {\"value\":\"disabled\"},\"silentInterval\": {\"value\":300},\"windAlarmMethod\": {\"value\":\"sound\"},\"windAlarmState\": {\"value\":\"disabled\"},\"genericAlarmMethod\": {\"value\":\"sound\"},\"genericAlarmState\": {\"value\":\"disabled\"},\"radarAlarmMethod\": {\"value\":\"sound\"},\"radarAlarmState\": {\"value\":\"disabled\"},\"mobAlarmMethod\": {\"value\":\"sound\"},\"mobAlarmState\": {\"value\":\"disabled\"}},\"steering\":{\"rudderAngle\": {\"value\":0.0000000000},\"rudderAngleTarget\": {\"value\":0.0000000000},\"autopilot\":{\"state\": {\"value\":\"off\"},\"mode\": {\"value\":\"powersave\"},\"targetHeadingNorth\": {\"value\":0.0000000000},\"targetHeadingMagnetic\": {\"value\":0.0000000000},\"alarmHeadingXte\": {\"value\":0.0000000000},\"headingSource\": {\"value\":\"compass\"},\"deadZone\": {\"value\":0.0000000000},\"backlash\": {\"value\":0.0000000000},\"gain\": {\"value\":0},\"maxDriveAmps\": {\"value\":0.0000000000},\"maxDriveRate\": {\"value\":0.0000000000},\"portLock\": {\"value\":0.0000000000},\"starboardLock\": {\"value\":0.0000000000}}},\"environment\":{\"airPressureChangeRateAlarm\": {\"value\":0.0000000000},\"airPressure\": {\"value\":1024.0000000000},\"waterTemp\": {\"value\":0.0000000000},\"wind\":{\"speedAlarm\": {\"value\":0.0000000000},\"directionChangeAlarm\": {\"value\":0.0000000000},\"angleApparent\": {\"value\":0.0000000000},\"directionTrue\": {\"value\":0.0000000000},\"speedApparent\": {\"value\":0.0000000000},\"speedTrue\": {\"value\":7.68}}}}}}");
		SignalKModel signalk = new SignalKModelImpl( (NavigableMap<String, Object>) obj);
		
		logger.debug(signalk);
		//logger.debug(signalk.get(vessels));
		double cogM = (double) signalk.getValue(vessels_dot_self_dot+nav_courseOverGroundMagnetic);
		assertEquals(cogM,93.0,0.01);
		double lat = (double) signalk.getValue(vessels_dot_self_dot+nav_position_latitude);
		assertEquals(lat,-41.29,0.01);
		double wind = (double)signalk.getValue(vessels_dot_self_dot+env_wind_speedTrue);
		assertEquals(wind,7.68,0.000001);
	}
	
	@Test
	public void shouldCreateJsonModel() throws IOException {
		String jsonStr = "{\"vessels\":{\""+self+"\":{\"environment\":{\"airPressure\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.110Z\",\"value\":1024.0},\"airPressureChangeRateAlarm\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.324Z\",\"value\":0.0},\"waterTemp\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.324Z\",\"value\":0.0},\"wind\":{\"angleApparent\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.325Z\",\"value\":0.0},\"directionChangeAlarm\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.326Z\",\"value\":0.0},\"directionTrue\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.327Z\",\"value\":256.3},\"speedAlarm\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.327Z\",\"value\":0.0},\"speedApparent\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.328Z\",\"value\":0.0},\"speedTrue\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.329Z\",\"value\":7.68}}},\"navigation\":{\"anchor\":{\"alarmRadius\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.330Z\",\"value\":0.0},\"maxRadius\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.330Z\",\"value\":0.0},\"position\":{\"altitude\":0.0,\"latitude\":-41.29369354,\"longitude\":173.24708557}},\"courseOverGroundMagnetic\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.331Z\",\"value\":93.0},\"courseOverGroundTrue\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.332Z\",\"value\":11.96000004},\"headingMagnetic\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.333Z\",\"value\":0.0},\"headingTrue\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.334Z\",\"value\":0.0},\"magneticVariation\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.335Z\",\"value\":0.0},\"pitch\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.335Z\",\"value\":0.0},\"position\":{\"altitude\":0.0,\"latitude\":-41.29369354,\"longitude\":173.24708557},\"rateOfTurn\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.336Z\",\"value\":0.0},\"roll\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.341Z\",\"value\":0.0},\"speedOverGround\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.342Z\",\"value\":0.04},\"speedThroughWater\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.343Z\",\"value\":0.0},\"state\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.343Z\",\"value\":\"Notdefined(example)\"}},\"steering\":{\"autopilot\":{\"alarmHeadingXte\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.344Z\",\"value\":0.0},\"backlash\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.345Z\",\"value\":0.0},\"deadZone\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.346Z\",\"value\":0.0},\"gain\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.347Z\",\"value\":0.0},\"headingSource\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.348Z\",\"value\":\"compass\"},\"maxDriveAmps\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.349Z\",\"value\":0.0},\"maxDriveRate\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.356Z\",\"value\":0.0},\"mode\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.359Z\",\"value\":\"powersave\"},\"portLock\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.360Z\",\"value\":0.0},\"starboardLock\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.363Z\",\"value\":0.0},\"state\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.366Z\",\"value\":\"off\"},\"targetHeadingMagnetic\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.369Z\",\"value\":0.0},\"targetHeadingNorth\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.371Z\",\"value\":0.0}},\"rudderAngle\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.374Z\",\"value\":0.0},\"rudderAngleTarget\":{\"source\":\"unknown\",\"timestamp\":\"2015-03-16T03:31:22.376Z\",\"value\":\"0}\"}}}}}";
		
		SignalKModel signalk = new SignalKModelImpl( );
		Util.populateModel(signalk, new File("src/test/resources/samples/basicModel.txt"));
		logger.debug(signalk);
		
		JsonSerializer ser = new JsonSerializer();
		
		String jsonOut = ser.write(signalk);
		logger.debug(jsonOut);
		assertEquals(jsonStr, jsonOut);
	}
}
