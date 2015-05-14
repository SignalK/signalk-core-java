package nz.co.fortytwo.signalk.util;

import static nz.co.fortytwo.signalk.util.SignalKConstants.*;
import static org.junit.Assert.*;
import mjson.Json;
import nz.co.fortytwo.signalk.handler.AlarmHandler;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AlarmManagerTest {
	private static Logger logger = Logger.getLogger(AlarmManagerTest.class);
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldNotBeAlarm() {
		Json zones = Json.read("[[0,45,\"normal\"],[45,50,\"warn\"],[50,99,\"alarm\"]]");
		AlarmManager mgr = new AlarmManager(zones);
		assertFalse(mgr.isAlarm(30));
		assertFalse(mgr.isAlarm(0));
		assertFalse(mgr.isAlarm(46.7));
		assertFalse(mgr.isAlarm(100.1));
	}
	
	@Test
	public void shouldBeAlarm() {
		Json zones = Json.read("[[0,45,\"normal\"],[45,50,\"warn\"],[50,99,\"alarm\"]]");
		AlarmManager mgr = new AlarmManager(zones);
		assertTrue(mgr.isAlarm(55));
		assertTrue(mgr.isAlarm(50.001));
	}

	@Test
	public void shouldNotBeWarn() {
		Json zones = Json.read("[[0,45,\"normal\"],[45,50,\"warn\"],[50,99,\"alarm\"]]");
		AlarmManager mgr = new AlarmManager(zones);
		assertFalse(mgr.isWarn(30));
		assertFalse(mgr.isWarn(0));
		assertFalse(mgr.isWarn(55));
		assertFalse(mgr.isWarn(50.001));
		assertFalse(mgr.isWarn(100.1));
	}
	
	@Test
	public void shouldBeWarn() {
		Json zones = Json.read("[[0,45,\"normal\"],[45,50,\"warn\"],[50,99,\"alarm\"]]");
		AlarmManager mgr = new AlarmManager(zones);
		assertTrue(mgr.isWarn(46.7));
		
	}
	
	@Test
	public void shouldNotBeNormal() {
		Json zones = Json.read("[[0,45,\"normal\"],[45,50,\"warn\"],[50,99,\"alarm\"]]");
		AlarmManager mgr = new AlarmManager(zones);
		assertFalse(mgr.isNormal(-1));
		assertFalse(mgr.isNormal(0));
		assertFalse(mgr.isNormal(45));
		assertFalse(mgr.isNormal(50.001));
		assertFalse(mgr.isNormal(100.1));
	}
	
	@Test
	public void shouldBeNormal() {
		Json zones = Json.read("[[0,45,\"normal\"],[45,50,\"warn\"],[50,99,\"alarm\"]]");
		AlarmManager mgr = new AlarmManager(zones);
		assertTrue(mgr.isNormal(0.001));
		assertTrue(mgr.isNormal(3.56));
		assertTrue(mgr.isNormal(44.999));
		
	}
	
	@Test
	public void shouldSetAlarm(){
		Json zones = Json.read("[[0,45,\"normal\"],[45,50,\"warn\"],[50,99,\"alarm\"]]");
		AlarmManager mgr = new AlarmManager(zones);
		SignalKModel model = SignalKModelFactory.getCleanInstance();
		mgr.setAlarm(model, vessels_dot_self_dot+nav_courseOverGroundMagnetic, alarm, "Test msg");
		logger.debug(model);
		assertEquals(alarm, model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+alarmState));
		assertEquals("Test msg", model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+message));
	}
	@Test
	public void shouldSetAlarmWithNullMessage(){
		Json zones = Json.read("[[0,45,\"normal\"],[45,50,\"warn\"],[50,99,\"alarm\"]]");
		AlarmManager mgr = new AlarmManager(zones);
		SignalKModel model = SignalKModelFactory.getCleanInstance();
		mgr.setAlarm(model, vessels_dot_self_dot+nav_courseOverGroundMagnetic, alarm, null);
		logger.debug(model);
		assertEquals(alarm, model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+alarmState));
		assertEquals("", model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+message));
	}

}
