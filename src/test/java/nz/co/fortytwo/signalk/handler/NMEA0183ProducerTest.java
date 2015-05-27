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

import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_angleApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_magneticVariation;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;

import org.junit.After;
import org.junit.Test;

public class NMEA0183ProducerTest {

	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldGetRMC() {
		NMEA0183Producer p = new NMEA0183Producer();
		SignalKModel model = SignalKModelFactory.getInstance();
		model.put(vessels_dot_self_dot+ nav_position_latitude, -41.5);
		model.put(vessels_dot_self_dot+ nav_position_longitude, 172.5);
		String nmea = p.createRMC(model);
		
		assertEquals("$GPRMC,,A,4130.000,S,17230.000,E,,,270515,,,A*68",nmea);
	}
	
	@Test
	public void shouldGetMWVApparent() {
		NMEA0183Producer p = new NMEA0183Producer();
		SignalKModel model = SignalKModelFactory.getInstance();
		model.putValue(vessels_dot_self_dot+ env_wind_angleApparent, 41.5);
		model.putValue(vessels_dot_self_dot+ env_wind_speedApparent, 12.5);
		String nmea = p.createMWVApparent(model);
		
		assertEquals("$IIMWV,041.5,R,24.3,N,A*08",nmea);
	}

}