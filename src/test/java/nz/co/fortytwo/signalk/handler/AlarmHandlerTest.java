package nz.co.fortytwo.signalk.handler;

import static nz.co.fortytwo.signalk.util.SignalKConstants.alarm;
import static nz.co.fortytwo.signalk.util.SignalKConstants.alarmState;
import static nz.co.fortytwo.signalk.util.SignalKConstants.alarms;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.meta;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundMagnetic;
import static nz.co.fortytwo.signalk.util.SignalKConstants.normal;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.zones;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.TestHelper;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

public class AlarmHandlerTest {
	private static Logger logger = Logger.getLogger(AlarmHandlerTest.class);
	

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldSetAlarm() throws IOException {
		SignalKModel model = SignalKModelFactory.getMotuTestInstance();
		model.putAll(TestHelper.getBasicModel().getFullData());

		assertEquals(Math.toRadians(93d), model.getValue(vessels_dot_self_dot+nav_courseOverGroundMagnetic));
		//now set an alarm
		model.put(vessels_dot_self_dot+nav_courseOverGroundMagnetic+dot+meta+dot+zones, Json.read("[[0,91,\"alarm\"],[93,95,\"normal\"],[95,360,\"alarm\"]]"));
		AlarmHandler handler = new AlarmHandler();
		handler.handle(model);
		assertNull(model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic));
		
		model.putValue(vessels_dot_self_dot+nav_courseOverGroundMagnetic, 97.0d);
		handler.handle(model);
		logger.debug(model);
		logger.debug(model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+alarmState));
		assertEquals(alarm, model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+alarmState));
		
		model.putValue(vessels_dot_self_dot+nav_courseOverGroundMagnetic, 94.0d);
		handler.handle(model);
		logger.debug(model);
		logger.debug(model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+alarmState));
		assertEquals(normal, model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+alarmState));
		
		model.putValue(vessels_dot_self_dot+nav_courseOverGroundMagnetic, 97d);
		handler.handle(model);
		assertEquals(alarm, model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+alarmState));
		
		model.put(vessels_dot_self_dot+nav_courseOverGroundMagnetic+dot+meta+dot+zones, Json.read("[]"));
		handler.handle(model);
		logger.debug(model);
		logger.debug(model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+alarmState));
		assertEquals(normal, model.get(vessels_dot_self_dot+alarms+dot+nav_courseOverGroundMagnetic+dot+alarmState));
	}

}
