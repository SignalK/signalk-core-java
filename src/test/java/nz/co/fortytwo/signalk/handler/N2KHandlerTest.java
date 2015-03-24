package nz.co.fortytwo.signalk.handler;

import static nz.co.fortytwo.signalk.util.SignalKConstants.communication_callsignVhf;
import static nz.co.fortytwo.signalk.util.SignalKConstants.design_beam;
import static nz.co.fortytwo.signalk.util.SignalKConstants.design_loa;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_date;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_belowTransducer;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_time;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_angleApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_angleTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_directionTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedOverGround;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.mmsi;
import static nz.co.fortytwo.signalk.util.SignalKConstants.name;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_current_drift;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_current_setTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_destination;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_headingMagnetic;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_headingTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_log;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_logTrip;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_magneticVariation;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_pitch;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_rateOfTurn;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_roll;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedOverGround;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedThroughWater;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_state;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_yaw;
import static nz.co.fortytwo.signalk.util.SignalKConstants.steering_rudderAngle;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import static org.junit.Assert.*;

import java.io.IOException;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKExamplesGenerator;
import nz.co.fortytwo.signalk.util.JsonSerializer;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class N2KHandlerTest {
	private static Logger logger = Logger.getLogger(N2KHandlerTest.class);
	

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldConvertN2K_126992() throws IOException{
		String json = "{\"timestamp\":\"2013-10-08-16:04:06.044\",\"prio\":\"3\",\"src\":\"1\",\"dst\":\"255\",\"pgn\":\"126992\",\"description\":\"System Time\",\"fields\":{\"SID\":\"222\",\"Date\":\"2013.10.08\",\"Time\":\"16:04:00\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		logger.debug("Model:"+model);
		assertEquals("16:04:00", model.getValue(vessels_dot_self_dot+env_time));
		assertEquals("2013.10.08", model.getValue(vessels_dot_self_dot+env_date));
	}
	
	@Test
	public void shouldConvertN2K_127245() throws IOException{
		
		String json = "{\"timestamp\":\"2013-10-08-15:47:28.263\",\"prio\":\"2\",\"src\":\"204\",\"dst\":\"255\",\"pgn\":\"127245\",\"description\":\"Rudder\",\"fields\":{\"Instance\":\"0\",\"Position\":\"-0.7\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		logger.debug("Model:"+model);
		assertEquals(-0.7, model.getValue(vessels_dot_self_dot+steering_rudderAngle));
	}
	
	
	@Test
	public void shouldConvertN2K_129026() throws IOException{
		String json = "{\"timestamp\":\"2014-08-15-18:00:10.005\",\"prio\":\"2\",\"src\":\"160\",\"dst\":\"255\",\"pgn\":\"129026\",\"description\":\"COG & SOG, Rapid Update\",\"fields\":{\"COG_Reference\":\"True\",\"COG\":\"206.1\",\"SOG\":\"3.65\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		logger.debug("Model:"+model);
		assertEquals(206.1, model.getValue(vessels_dot_self_dot+nav_courseOverGroundTrue));
		assertEquals(3.65, model.getValue(vessels_dot_self_dot+nav_speedOverGround));
	}
	
	@Test
	public void shouldConvertN2K_127250() throws IOException{
		String json = "{\"timestamp\":\"2013-10-08-15:47:28.263\",\"prio\":\"2\",\"src\":\"204\",\"dst\":\"255\",\"pgn\":\"127250\",\"description\":\"Vessel Heading\",\"fields\":{\"Heading\":\"129.7\",\"Reference\":\"Magnetic\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		assertEquals(129.7, model.getValue(vessels_dot_self_dot+nav_headingMagnetic));
		json = "{\"timestamp\":\"2013-10-08-15:47:28.264\",\"prio\":\"2\",\"src\":\"1\",\"dst\":\"255\",\"pgn\":\"127250\",\"description\":\"Vessel Heading\",\"fields\":{\"SID\":\"68\",\"Variation\":\"8.0\",\"Reference\":\"True\"}}";
		model = handler.handle(json);
		assertEquals(8.0, model.getValue(vessels_dot_self_dot+nav_magneticVariation));
	}
	
	@Test
	public void shouldConvertN2K_127257() throws IOException{
		String json = "{\"timestamp\":\"2013-10-08-15:47:28.263\",\"prio\":\"2\",\"src\":\"204\",\"dst\":\"255\",\"pgn\":\"127257\",\"description\":\"Attitude\",\"fields\":{\"Yaw\":\"37.190\",\"Pitch\":\"0.464\",\"Roll\":\"-2.496\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		assertEquals(37.190, model.getValue(vessels_dot_self_dot+nav_yaw));
		assertEquals(0.464, model.getValue(vessels_dot_self_dot+nav_pitch));
		assertEquals(-2.496, model.getValue(vessels_dot_self_dot+nav_roll));
	}
	
	@Test
	public void shouldConvertN2K_128259() throws IOException{
		String json = "{\"timestamp\":\"2014-08-15-18:00:30.175\",\"prio\":\"2\",\"src\":\"115\",\"dst\":\"255\",\"pgn\":\"128259\",\"description\":\"Speed\",\"fields\":{\"SID\":\"0\",\"Speed Water Referenced\":\"3.47\",\"Speed Water Referenced Type\":\"-0\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		assertEquals(3.47, model.getValue(vessels_dot_self_dot+nav_speedThroughWater));
	}
	
	@Test
	public void shouldConvertN2K_128267() throws IOException{
		String json = "{\"timestamp\":\"2013-10-08-15:47:28.280\",\"prio\":\"3\",\"src\":\"1\",\"dst\":\"255\",\"pgn\":\"128267\",\"description\":\"Water Depth\",\"fields\":{\"SID\":\"91\",\"Depth\":\"8.20\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		assertEquals(8.20, model.getValue(vessels_dot_self_dot+env_depth_belowTransducer));
	}
	
	@Test
	public void shouldConvertN2K_128275() throws IOException{
		String json = "{\"timestamp\":\"2013-10-08-16:04:06.060\",\"prio\":\"6\",\"src\":\"1\",\"dst\":\"255\",\"pgn\":\"128275\",\"description\":\"Distance Log\",\"fields\":{\"Log\":\"2229808\",\"Trip Log\":\"4074\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		assertEquals(4074.0, model.getValue(vessels_dot_self_dot+nav_logTrip));
		assertEquals(2229808.0, model.getValue(vessels_dot_self_dot+nav_log));
	}
	
	@Test
	public void shouldConvertN2K_129025() throws IOException{
		String json = "{\"timestamp\":\"2013-10-08-15:47:28.264\",\"prio\":\"2\",\"src\":\"2\",\"dst\":\"255\",\"pgn\":\"129025\",\"description\":\"Position, Rapid Update\",\"fields\":{\"Latitude\":\"60.1445540\",\"Longitude\":\"24.7921348\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		assertEquals(24.7921348, model.get(vessels_dot_self_dot+nav_position_longitude));
		assertEquals(60.144554, model.get(vessels_dot_self_dot+nav_position_latitude));
	}
	
	@Test
	public void shouldConvertN2K_129038() throws IOException{
		String json = "{\"timestamp\":\"2014-08-15-15:00:01.665\",\"prio\":\"4\",\"src\":\"43\",\"dst\":\"255\",\"pgn\":\"129038\",\"description\":\"AIS Class A Position Report\",\"fields\":{\"Message ID\":\"1\",\"Repeat Indicator\":\"Initial\",\"User ID\":\"230982000\",\"Longitude\":\"25.2026083\",\"Latitude\":\"60.2176150\",\"Position Accuracy\":\"High\",\"RAIM\":\"not in use\",\"Time Stamp\":\"0\",\"COG\":\"154.0\",\"SOG\":\"2.26\",\"Communication State\":\"2286\",\"AIS Transceiver information\":\"Channel B VDL reception\",\"Heading\":\"153.0\",\"Rate of Turn\":\"0.047\",\"Nav Status\":\"Under way using engine\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		String target = vessels+dot+"230982000"+dot;
		assertEquals(25.2026083, model.get(target+nav_position_longitude));
		assertEquals(60.2176150, model.get(target+nav_position_latitude));
		assertEquals(154.0, model.getValue(target+nav_courseOverGroundTrue));
		assertEquals(2.26, model.getValue(target+nav_speedOverGround));
		assertEquals("230982000", model.get(target+mmsi));
		assertEquals(0.047, model.getValue(target+nav_rateOfTurn));
		assertEquals("Under way using engine", model.getValue(target+nav_state));
		assertEquals(153.0, model.getValue(target+nav_headingTrue));
	}
	@Test
	public void shouldConvertN2K_129039() throws IOException{
		String json = "{\"timestamp\":\"2014-08-15-16:00:00.257\",\"prio\":\"4\",\"src\":\"43\",\"dst\":\"255\",\"pgn\":\"129039\",\"description\":\"AIS Class B Position Report\",\"fields\":{\"Message ID\":\"18\",\"Repeat Indicator\":\"Initial\",\"User ID\":\"230035780\",\"Longitude\":\"24.9024733\",\"Latitude\":\"60.0395100\",\"Position Accuracy\":\"High\",\"RAIM\":\"in use\",\"Time Stamp\":\"0\",\"COG\":\"167.7\",\"SOG\":\"3.75\",\"Communication State\":\"393222\",\"AIS Transceiver information\":\"Own information not broadcast\",\"Regional Application\":\"0\",\"Regional Application\":\"0\",\"Unit type\":\"CS\",\"Integrated Display\":\"No\",\"DSC\":\"Yes\",\"Band\":\"entire marine band\",\"Can handle Msg 22\":\"Yes\",\"AIS mode\":\"Autonomous\",\"AIS communication state\":\"ITDMA\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		String target = vessels+dot+"230035780"+dot;
		assertEquals(24.9024733, model.get(target+nav_position_longitude));
		assertEquals(60.0395100, model.get(target+nav_position_latitude));
		assertEquals(167.7, model.getValue(target+nav_courseOverGroundTrue));
		assertEquals(3.75, model.getValue(target+nav_speedOverGround));
		assertEquals("230035780", model.get(target+mmsi));
		//assertEquals(0.047, model.getValue(target+nav_rateOfTurn));
		//assertEquals("Under way using engine", model.getValue(target+nav_state));
		//assertEquals(153.0, model.getValue(target+nav_headingTrue));
	}
	
	@Test
	public void shouldConvertN2K_129794() throws IOException{
		String json = "{\"timestamp\":\"2014-08-15-15:01:01.881\",\"prio\":\"6\",\"src\":\"43\",\"dst\":\"255\",\"pgn\":\"129794\",\"description\":\"AIS Class A Static and Voyage Related Data\",\"fields\":{\"Message ID\":\"5\",\"Repeat indicator\":\"Initial\",\"User ID\":\"230939100\",\"IMO number\":\"0\",\"Callsign\":\"OJ7510\",\"Name\":\"RESCUE RAUTAUOMA\",\"Type of ship\":\"SAR\",\"Length\":\"16.0\",\"Beam\":\"4.0\",\"Position reference from Starboard\":\"2.0\",\"Position reference from Bow\":\"9.0\",\"ETA Date\":\"2014.11.30\", \"ETA Time\": \"25:00:00\",\"Draft\":\"1.00\",\"Destination\":\"HELSINKI LIFEBOAT\",\"AIS version indicator\":\"ITU-R M.1371-1\",\"GNSS type\":\"GPS\",\"DTE\":\"available\",\"Reserved\":\"0\",\"AIS Transceiver information\":\"Channel B VDL reception\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		String target = vessels+dot+"230939100"+dot;
		assertEquals("OJ7510", model.getValue(target+communication_callsignVhf));
		assertEquals("RESCUE RAUTAUOMA", model.get(target+name));
		assertEquals(16.0, model.getValue(target+design_loa));
		assertEquals(4.0, model.getValue(target+design_beam));
		assertEquals("230939100", model.get(target+mmsi));
		assertEquals("HELSINKI LIFEBOAT", model.getValue(target+nav_destination));
		//assertEquals("Under way using engine", model.getValue(target+nav_state));
		//assertEquals(153.0, model.getValue(target+nav_headingTrue));
	}
	
	@Test
	public void shouldConvertN2K_129809() throws IOException{
		String json = "{\"timestamp\":\"2014-08-15-15:00:04.655\",\"prio\":\"6\",\"src\":\"43\",\"dst\":\"255\",\"pgn\":\"129809\",\"description\":\"AIS Class B static data (msg 24 Part A)\",\"fields\":{\"Message ID\":\"24\",\"Repeat indicator\":\"Initial\",\"User ID\":\"230044160\",\"Name\":\"LAGUNA\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		String target = vessels+dot+"230044160"+dot;
		assertEquals("LAGUNA", model.get(target+name));
		assertEquals("230044160", model.get(target+mmsi));
		
	}
	
	
	
	@Test
	public void shouldConvertN2K_130306() throws IOException{
		String json = "{\"timestamp\":\"2013-10-08-15:47:28.263\",\"prio\":\"2\",\"src\":\"1\",\"dst\":\"255\",\"pgn\":\"130306\",\"description\":\"Wind Data\",\"fields\":{\"SID\":\"67\",\"Wind Speed\":\"6.22\",\"Wind Angle\":\"50.0\",\"Reference\":\"Apparent\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		assertEquals(50.0, model.get(vessels_dot_self_dot+env_wind_angleApparent));
		assertEquals(6.22, model.get(vessels_dot_self_dot+env_wind_speedApparent));
		
		json = "{\"timestamp\":\"2013-10-08-15:47:28.264\",\"prio\":\"2\",\"src\":\"1\",\"dst\":\"255\",\"pgn\":\"130306\",\"description\":\"Wind Data\",\"fields\":{\"SID\":\"68\",\"Wind Speed\":\"4.89\",\"Wind Angle\":\"86.0\",\"Reference\":\"True (boat referenced)\"}}";
		logger.debug("Converting "+Json.read(json));
		handler = new N2KHandler();
		model = handler.handle(json);
		assertEquals(86.0, model.get(vessels_dot_self_dot+env_wind_angleTrue));
		assertEquals(4.89, model.get(vessels_dot_self_dot+env_wind_speedTrue));
		
		json = "{\"timestamp\":\"2013-10-08-15:47:28.264\",\"prio\":\"2\",\"src\":\"3\",\"dst\":\"255\",\"pgn\":\"130306\",\"description\":\"Wind Data\",\"fields\":{\"SID\":\"94\",\"Wind Speed\":\"4.82\",\"Wind Angle\":\"218.6\",\"Reference\":\"True (ground referenced to North)\"}}";
		logger.debug("Converting "+Json.read(json));
		handler = new N2KHandler();
		model = handler.handle(json);
		assertEquals(4.82, model.get(vessels_dot_self_dot+env_wind_speedOverGround));
		assertEquals(218.6, model.get(vessels_dot_self_dot+env_wind_directionTrue));
	}
	
	@Test
	public void shouldConvertN2K_130577() throws IOException{
		String json = "{\"timestamp\":\"2014-08-15-18:00:00.755\",\"prio\":\"3\",\"src\":\"160\",\"dst\":\"255\",\"pgn\":\"130577\",\"description\":\"Direction Data\",\"fields\":{\"Data Mode\":\"Autonomous\",\"COG Reference\":\"True\",\"SID\":\"84\",\"COG\":\"206.9\",\"SOG\":\"3.51\",\"Set\":\"58.9\",\"Drift\":\"0.28\"}}";
		logger.debug("Converting "+Json.read(json));
		N2KHandler handler = new N2KHandler();
		SignalKModel model = handler.handle(json);
		
		assertEquals(206.9, model.getValue(vessels_dot_self_dot+nav_courseOverGroundTrue));
		assertEquals(3.51, model.getValue(vessels_dot_self_dot+nav_speedOverGround));
		assertEquals(58.9, model.get(vessels_dot_self_dot+nav_current_setTrue));
		assertEquals(0.28, model.get(vessels_dot_self_dot+nav_current_drift));
		
		json = "{\"timestamp\":\"2014-08-15-18:00:00.755\",\"prio\":\"3\",\"src\":\"160\",\"dst\":\"255\",\"pgn\":\"130577\",\"description\":\"Direction Data\",\"fields\":{\"Data Mode\":\"Autonomous\",\"COG Reference\":\"True\",\"SID\":\"84\",\"COG\":\"206.9\",\"SOG\":\"3.51\"}}";
		logger.debug("Converting "+Json.read(json));
		handler = new N2KHandler();
		model = handler.handle(json);
		
		assertEquals(206.9, model.getValue(vessels_dot_self_dot+nav_courseOverGroundTrue));
		assertEquals(3.51, model.getValue(vessels_dot_self_dot+nav_speedOverGround));
		assertNull( model.get(vessels_dot_self_dot+nav_current_setTrue));
		assertNull( model.get(vessels_dot_self_dot+nav_current_drift));
	}
	
}
