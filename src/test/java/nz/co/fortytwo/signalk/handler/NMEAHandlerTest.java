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

package nz.co.fortytwo.signalk.handler;

import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_belowTransducer;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_angleApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedOverGround;
import static nz.co.fortytwo.signalk.util.SignalKConstants.propulsion_id_oilPressure;
import static nz.co.fortytwo.signalk.util.SignalKConstants.propulsion_id_revolutions;
import static nz.co.fortytwo.signalk.util.SignalKConstants.propulsion_id_temperature;
import static nz.co.fortytwo.signalk.util.SignalKConstants.self;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.io.IOException;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

public class NMEAHandlerTest {

	private static Logger logger = Logger.getLogger(NMEAHandlerTest.class);
	private SignalKModel signalkModel=SignalKModelFactory.getMotuTestInstance();
	

	@Test
	public void shouldPassJson(){
		 String jStr = "{\"vessels\":{\""+self+"\":{\"environment\":{\"wind\":{\"angleApparent\":0.0000000000,\"directionTrue\":0.0000000000,\"speedApparent\":0.0000000000,\"speedTrue\":20.0000000000}}}}}";
		 NMEAHandler processor = new NMEAHandler();
		
		 SignalKModel model = processor.handle(jStr);
		 logger.debug(model);
		 assertNull( model);
		 
	}
	//"$IIVPW,4.71,N,,*03",
	//"$IIVTG,224.44,T,224.44,M,5.81,N,,,D*68",
	//"$IIVWT,039,L,08.10,N,04.17,M,,*2B",
	//"$IIMWD,,,,,08.16,N,04.20,M*54"};
	@Test
	public void shouldHandleGPRMC(){
		 String nmea1 = "$GPRMC,144629.20,A,5156.91111,N,00434.80385,E,0.295,,011113,,,A*78";
		 NMEAHandler processor = new NMEAHandler();
		 SignalKModel model = processor.handle(nmea1);
		 logger.debug("Returned signalk tree:"+model);
		 assertEquals(51.9485185d,(double)model.get(vessels_dot_self_dot +nav_position_latitude),0.0001);
		 assertEquals(4.580064d,(double)model.get(vessels_dot_self_dot +nav_position_longitude),0.0001);
		 assertEquals(0.025293299999999994d,(double)model.getValue(vessels_dot_self_dot +nav_speedOverGround),0.0001);
		 //logger.debug("Lat :"+model.get(vessels_dot_self_dot +nav_position_latitude));
	}
	
	@Test
	public void shouldHandleMWV() throws IOException{
		String nmea1 = "$IIMWV,338,R,13.41,N,A*2C";
		 NMEAHandler processor = new NMEAHandler();
		 SignalKModel model = processor.handle(nmea1);
		 logger.debug("Returned signalk tree:"+model);
		 assertEquals(Math.toRadians(338.0),model.getValue(vessels_dot_self_dot +env_wind_angleApparent));
		 assertEquals(6.898640400000001,model.getValue(vessels_dot_self_dot +env_wind_speedApparent));
	}
	@Test
	public void shouldHandleDBT() throws IOException{
		String nmea1 = "$IIDBT,034.25,f,010.44,M,005.64,F*27";
		 NMEAHandler processor = new NMEAHandler();
		 SignalKModel model = processor.handle(nmea1);
		 logger.debug("Returned signalk tree:"+model);
		 assertEquals(10.44,model.getValue(vessels_dot_self_dot +env_depth_belowTransducer));
	}
	
	@Test
	public void shouldHandleGLL() throws IOException{
		String nmea1 = "$GPGLL,6005.071,N,02332.346,E,095559,A,D*43";
		 NMEAHandler processor = new NMEAHandler();
		 SignalKModel model = processor.handle(nmea1);
		 logger.debug("Returned signalk tree:"+model);
		 assertEquals(60.0845166d,(double)model.get(vessels_dot_self_dot +nav_position_latitude),0.0001);
		 assertEquals(23.5391d,(double)model.get(vessels_dot_self_dot +nav_position_longitude),0.0001);

	}
	@Test
	@Ignore
	public void shouldHandleCruzproXDR() throws FileNotFoundException, IOException {
		NMEAHandler processor = new NMEAHandler();
		Json json = (Json) processor.handle("$YXXDR,G,0004,,G,12.27,,G,,,G,003.3,,G,0012,,MaxVu110*4E");
		//RPM,EVV,DBT,EPP,ETT
		assertEquals(4.0,(double)signalkModel.getValue(vessels_dot_self_dot +propulsion_id_revolutions),0.0001);
		//assertEquals(12.27,signalkModel.getValue(vessels_dot_self_dot +propulsion_));
		assertEquals(null,(double)signalkModel.getValue(vessels_dot_self_dot +env_depth_belowTransducer));
		assertEquals(3.3,(double)signalkModel.getValue(vessels_dot_self_dot +propulsion_id_oilPressure),0.0001);
		assertEquals(12.0,(double)signalkModel.getValue(vessels_dot_self_dot +propulsion_id_temperature),0.0001);
	}
	
//	@Test
//	@Ignore
//	public void shouldHandleSkipValue() throws FileNotFoundException, IOException {
//		NMEAHandler processor = new NMEAHandler();
//	
//		//freeboard.nmea.YXXDR.MaxVu110=RPM,EVV,DBT,EPP,ETT
//		Util.setProperty("freeboard.nmea.YXXDR.MaxVu110", "RPM,EVV,SKIP,EPP,ETT");
//		
//		Json json = (Json) processor.handle("$YXXDR,G,0004,,G,12.27,,G,,,G,003.3,,G,0012,,MaxVu110*4E");
//		//RPM,EVV,DBT,EPP,ETT
//		assertEquals(4.0,(double)signalkModel.getValue(vessels_dot_self_dot +propulsion_id_rpm),0.0001);
//		//assertEquals(12.27,map.get(Constants.ENGINE_VOLTS));
//		assertTrue(signalkModel.getValue(vessels_dot_self_dot +env_depth_belowTransducer)==null);
//		assertEquals(3.3,(double)signalkModel.getValue(vessels_dot_self_dot +propulsion_id_oilPressure),0.0001);
//		assertEquals(12.0,(double)signalkModel.getValue(vessels_dot_self_dot +propulsion_id_engineTemperature),0.0001);
//	}
//	@Test
//	@Ignore
//	public void shouldRejectMismatchedValues() throws FileNotFoundException, IOException {
//		NMEAHandler processor = new NMEAHandler();
//		//HashMap<String, Object> map = new HashMap<String, Object>();
//		//freeboard.nmea.YXXDR.MaxVu110=RPM,EVV,DBT,EPP,ETT
//		Util.getConfig(null).setProperty("freeboard.nmea.YXXDR.MaxVu110", "RPM,EVV,SKIP,EPP");
//		//map.put(Constants.NMEA, "$YXXDR,G,0004,,G,12.27,,G,,,G,003.3,,G,0012,,MaxVu110*4E");
//		Json json = (Json) processor.handle("$YXXDR,G,0004,,G,12.27,,G,,,G,003.3,,G,0012,,MaxVu110*4E");
//		//RPM,EVV,DBT,EPP,ETT
//		assertTrue(signalkModel.getValue(vessels_dot_self_dot +propulsion_id_rpm)==null);
//		//assertTrue(signalkModel.getValue(vessels_dot_self_dot +propulsion_rpm)==null);
//		assertTrue(signalkModel.getValue(vessels_dot_self_dot +env_depth_belowTransducer)==null);
//		assertTrue(signalkModel.getValue(vessels_dot_self_dot +propulsion_id_oilPressure)==null);
//		assertTrue(signalkModel.getValue(vessels_dot_self_dot +propulsion_id_engineTemperature)==null);
//	}

}
