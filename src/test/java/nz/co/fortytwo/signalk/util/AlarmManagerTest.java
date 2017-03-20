package nz.co.fortytwo.signalk.util;

import static nz.co.fortytwo.signalk.util.SignalKConstants.alarm;
import static nz.co.fortytwo.signalk.util.SignalKConstants.alarmState;
import static nz.co.fortytwo.signalk.util.SignalKConstants.notifications;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.message;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundMagnetic;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class AlarmManagerTest {
	private static Logger logger = LogManager.getLogger(AlarmManagerTest.class);
	@BeforeClass
	public static void setUp() throws Exception {
		Util.getConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldNotBeAlarm() {
		Json zones = Json.read("[{\"lower\":0,\"upper\":45,\"state\":\"normal\"},{\"lower\":45,\"upper\":50,\"state\":\"warn\"},{\"lower\":50,\"upper\":99,\"state\":\"alarm\"}]");
		AlarmManager mgr = new AlarmManager(zones);
		assertFalse(mgr.isAlarm(30));
		assertFalse(mgr.isAlarm(0));
		assertFalse(mgr.isAlarm(46.7));
		assertFalse(mgr.isAlarm(100.1));
	}
	
	@Test
	public void shouldBeAlarm() {
		Json zones = Json.read("[{\"lower\":0,\"upper\":45,\"state\":\"normal\"},{\"lower\":45,\"upper\":50,\"state\":\"warn\"},{\"lower\":50,\"upper\":99,\"state\":\"alarm\"}]");
		AlarmManager mgr = new AlarmManager(zones);
		assertTrue(mgr.isAlarm(55));
		assertTrue(mgr.isAlarm(50.001));
	}

	@Test
	public void shouldNotBeWarn() {
		Json zones = Json.read("[{\"lower\":0,\"upper\":45,\"state\":\"normal\"},{\"lower\":45,\"upper\":50,\"state\":\"warn\"},{\"lower\":50,\"upper\":99,\"state\":\"alarm\"}]");
		AlarmManager mgr = new AlarmManager(zones);
		assertFalse(mgr.isWarn(30));
		assertFalse(mgr.isWarn(0));
		assertFalse(mgr.isWarn(55));
		assertFalse(mgr.isWarn(50.001));
		assertFalse(mgr.isWarn(100.1));
	}
	
	@Test
	public void shouldBeWarn() {
		Json zones = Json.read("[{\"lower\":0,\"upper\":45,\"state\":\"normal\"},{\"lower\":45,\"upper\":50,\"state\":\"warn\"},{\"lower\":50,\"upper\":99,\"state\":\"alarm\"}]");
		AlarmManager mgr = new AlarmManager(zones);
		assertTrue(mgr.isWarn(46.7));
		
	}
	
	@Test
	public void shouldNotBeNormal() {
		Json zones = Json.read("[{\"lower\":0,\"upper\":45,\"state\":\"normal\"},{\"lower\":45,\"upper\":50,\"state\":\"warn\"},{\"lower\":50,\"upper\":99,\"state\":\"alarm\"}]");
		AlarmManager mgr = new AlarmManager(zones);
		assertFalse(mgr.isNormal(-1));
		assertFalse(mgr.isNormal(0));
		assertFalse(mgr.isNormal(45));
		assertFalse(mgr.isNormal(50.001));
		assertFalse(mgr.isNormal(100.1));
	}
	
	@Test
	public void shouldBeNormal() {
		Json zones = Json.read("[{\"lower\":0,\"upper\":45,\"state\":\"normal\"},{\"lower\":45,\"upper\":50,\"state\":\"warn\"},{\"lower\":50,\"upper\":99,\"state\":\"alarm\"}]");
		AlarmManager mgr = new AlarmManager(zones);
		assertTrue(mgr.isNormal(0.001));
		assertTrue(mgr.isNormal(3.56));
		assertTrue(mgr.isNormal(44.999));
		
	}
	
	@Test
	public void shouldSetAlarm(){
		Json zones = Json.read("[{\"lower\":0,\"upper\":45,\"state\":\"normal\"},{\"lower\":45,\"upper\":50,\"state\":\"warn\"},{\"lower\":50,\"upper\":99,\"state\":\"alarm\"}]");
		AlarmManager mgr = new AlarmManager(zones);
		SignalKModel model = SignalKModelFactory.getCleanInstance();
		Util.setSelf("motu");
		mgr.setAlarm(model, vessels_dot_self_dot+nav_courseOverGroundMagnetic, alarm, "Test msg");
		logger.debug(model);
		assertEquals(alarm, model.get(vessels_dot_self_dot+notifications+dot+nav_courseOverGroundMagnetic+dot+alarmState));
		assertEquals("Test msg", model.get(vessels_dot_self_dot+notifications+dot+nav_courseOverGroundMagnetic+dot+message));
	}
	@Test
	public void shouldSetAlarmWithNullMessage(){
		Json zones = Json.read("[{\"lower\":0,\"upper\":45,\"state\":\"normal\"},{\"lower\":45,\"upper\":50,\"state\":\"warn\"},{\"lower\":50,\"upper\":99,\"state\":\"alarm\"}]");
		AlarmManager mgr = new AlarmManager(zones);
		SignalKModel model = SignalKModelFactory.getCleanInstance();
		Util.setSelf("motu");
		mgr.setAlarm(model, vessels_dot_self_dot+nav_courseOverGroundTrue, alarm, null);
		logger.debug(model);
		assertEquals(alarm, model.get(vessels_dot_self_dot+notifications+dot+nav_courseOverGroundTrue+dot+alarmState));
		assertEquals("", model.get(vessels_dot_self_dot+notifications+dot+nav_courseOverGroundTrue+dot+message));
	}

}
