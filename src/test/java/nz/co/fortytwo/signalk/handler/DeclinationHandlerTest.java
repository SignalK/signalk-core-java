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

public class DeclinationHandlerTest {

	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldGetDeclination() {
		DeclinationHandler p = new DeclinationHandler();
		SignalKModel model = SignalKModelFactory.getMotuTestInstance();
		model.put(vessels_dot_self_dot+ nav_position_latitude, -41.5);
		model.put(vessels_dot_self_dot+ nav_position_longitude, 172.5);
		p.handle(model);
		double decl = (double) model.getValue(vessels_dot_self_dot+nav_magneticVariation);
		assertEquals(22.1, decl, 001);
	}
	
	@Test
	public void shouldNotGetDeclination() {
		DeclinationHandler p = new DeclinationHandler();
		SignalKModel model = SignalKModelFactory.getMotuTestInstance();
		model.put(vessels_dot_self_dot+ nav_position_latitude, -41.5);
		//model.putWith(model.SignalKConstants.self(), JsonConstants.nav_position_longitude, 172.5);
		p.handle(model);
		Object decl = model.getValue(vessels_dot_self_dot+nav_magneticVariation);
		assertNull( decl);
	}

}
