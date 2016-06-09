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
package nz.co.fortytwo.signalk.model.impl;

import static nz.co.fortytwo.signalk.util.SignalKConstants.UNKNOWN;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_angleApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_directionChangeAlarm;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_directionTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_altitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.self;
import static nz.co.fortytwo.signalk.util.SignalKConstants.sourceRef;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;
import static nz.co.fortytwo.signalk.util.SignalKConstants.value;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.NavigableMap;
import java.util.SortedMap;

import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.TestHelper;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class SignalKModelImplTest {

	private static Logger logger = LogManager.getLogger(SignalKModelImplTest.class);
	@BeforeClass
	public static void setUp() throws Exception {
		Util.getConfig();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void shouldSubstituteSelf() throws IOException {
		
		SignalKModel signalk = SignalKModelFactory.getMotuTestInstance();
		signalk.putAll(TestHelper.getBasicModel().getFullData());
		
		logger.debug(signalk);
		
		signalk.putValue("vessels.self.environment.wind.angleApparent", 256.0d);
		
		assertEquals(256.0, signalk.getValue(vessels+dot+self+dot+env_wind_angleApparent));
		
		assertEquals(256.0, signalk.getValue(vessels+dot+"self"+dot+env_wind_angleApparent));
		
		assertEquals(15, signalk.getSubMap(vessels+dot+"self"+dot+env_wind).size());
		assertEquals(105, signalk.getSubMap(vessels+dot+"self").size());
	}

	@Test
	public void shouldReturnBranch() throws IOException {
		
		SignalKModel signalk = SignalKModelFactory.getMotuTestInstance();
		signalk.putAll(TestHelper.getBasicModel().getFullData());
		
		logger.debug(signalk);
		
		//String wind = "{vessels."+self+".environment.wind.angleApparent.source=unknown, vessels."+self+".environment.wind.angleApparent.timestamp=2015-03-16T03:31:22.325Z, vessels."+self+".environment.wind.angleApparent.value=0.0, vessels."+self+".environment.wind.directionChangeAlarm.source=unknown, vessels."+self+".environment.wind.directionChangeAlarm.timestamp=2015-03-16T03:31:22.326Z, vessels."+self+".environment.wind.directionChangeAlarm.value=0.0, vessels."+self+".environment.wind.directionTrue.source=unknown, vessels."+self+".environment.wind.directionTrue.timestamp=2015-03-16T03:31:22.327Z, vessels."+self+".environment.wind.directionTrue.value=256.3, vessels."+self+".environment.wind.speedAlarm.source=unknown, vessels."+self+".environment.wind.speedAlarm.timestamp=2015-03-16T03:31:22.327Z, vessels."+self+".environment.wind.speedAlarm.value=0.0, vessels."+self+".environment.wind.speedApparent.source=unknown, vessels."+self+".environment.wind.speedApparent.timestamp=2015-03-16T03:31:22.328Z, vessels."+self+".environment.wind.speedApparent.value=0.0, vessels."+self+".environment.wind.speedTrue.source=unknown, vessels."+self+".environment.wind.speedTrue.timestamp=2015-03-16T03:31:22.329Z, vessels."+self+".environment.wind.speedTrue.value=7.68}";
		logger.debug("Submap for: "+vessels_dot_self_dot+env_wind);
		NavigableMap<String, Object> branch = signalk.getSubMap(vessels_dot_self_dot+env_wind);
		logger.debug(branch);
		
		assertEquals(branch.get(vessels_dot_self_dot+env_wind_angleApparent+dot+value),0d);
		assertEquals(branch.get(vessels_dot_self_dot+env_wind_directionChangeAlarm+dot+value),0d);
		assertEquals(branch.get(vessels_dot_self_dot+env_wind_directionTrue+dot+value),0d);
		
		assertEquals(branch.get(vessels_dot_self_dot+env_wind_speedApparent+dot+value),0d);
		assertEquals(branch.get(vessels_dot_self_dot+env_wind_speedTrue+dot+value),7.68d);
		
	}
	@Test
	public void shouldFailAltitudeValue() throws IOException{
		SignalKModel signalk = SignalKModelFactory.getMotuTestInstance();
		signalk.putAll(TestHelper.getBasicModel().getFullData());
		
		logger.debug(signalk);
		try{
		signalk.put(vessels_dot_self_dot+nav_position_altitude, 90,"dummy",Util.getIsoTimeString());
		}catch(IllegalArgumentException e){
			return;
		}
		fail();
	}
	@Test
	public void shouldReturnLeaf() throws IOException {
		SignalKModel signalk = SignalKModelFactory.getMotuTestInstance();
		signalk.putAll(TestHelper.getBasicModel().getFullData());
	
		logger.debug(signalk);
		
		Double dirTrue = (Double) signalk.getValue(vessels_dot_self_dot+ env_wind_speedTrue);
		assertEquals(7.68,dirTrue,0.000001);
	}
	
	@Test
	public void shouldMergeBranch() throws IOException {
		
		SignalKModel signalk = SignalKModelFactory.getMotuTestInstance();
		signalk.putAll(TestHelper.getBasicModel().getFullData());
		
		logger.debug(signalk);
		assertNull(signalk.get(vessels_dot_self_dot+ env_wind));
		
		JsonSerializer ser = new JsonSerializer();
		Object wind = ser.read("{\"vessels\":{\""+self+"\":{\"environment\":{\"airPressureChangeRateAlarm\": {\"value\":0.0000000000},\"airPressure\": {\"value\":1024.0000000000},\"waterTemp\": {\"value\":0.0000000000},\"wind\":{\"speedAlarm\": {\"value\":0.0000000000},\"directionChangeAlarm\": {\"value\":0.0000000000},\"angleApparent\": {\"value\":0.0000000000},\"directionTrue\": {\"value\":256.3},\"speedApparent\": {\"value\":0.0000000000},\"speedTrue\": {\"value\":7.68}}}}}}");
		signalk.putAll((SortedMap<String, Object>) wind);
		assertTrue(signalk.getTree(vessels_dot_self_dot+ env_wind).size()>0);
		Double dirTrue = (Double) signalk.getValue(vessels_dot_self_dot+ env_wind_directionTrue);
		logger.debug(dirTrue);
		assertEquals(dirTrue,256.3,0.000001);
	}
	
	
	@Test
	public void shouldSetLeaf() {
		SignalKModel signalk = SignalKModelFactory.getMotuTestInstance();
		
		//signalk = Util.populateModel(signalk, new File("src/test/resources/samples/basicModel.txt"));
		signalk.put(vessels_dot_self_dot+ env_wind_directionTrue,256.3,"dummy",Util.getIsoTimeString());
		logger.debug(signalk);
		
		Double dirTrue = (Double) signalk.getValue(vessels_dot_self_dot+ env_wind_directionTrue);
		assertEquals(256.3,dirTrue,0.000001);
	}
	@Test
	public void shouldSetLeafValue() {
		SignalKModel signalk = SignalKModelFactory.getMotuTestInstance();
	
		//signalk = Util.populateModel(signalk, new File("src/test/resources/samples/basicModel.txt"));
		signalk.putValue(vessels_dot_self_dot+ env_wind_directionTrue,256.3);
		logger.debug(signalk);
		
		Double dirTrue = (Double) signalk.getValue(vessels_dot_self_dot+ env_wind_directionTrue);
		assertEquals(256.3,dirTrue,0.000001);
	}
	@Test
	public void shouldSetLeafAll() {
		SignalKModel signalk = SignalKModelFactory.getMotuTestInstance();
		
		//signalk = Util.populateModel(signalk, new File("src/test/resources/samples/basicModel.txt"));
		String ts = Util.getIsoTimeString();
		signalk.put(vessels_dot_self_dot+ env_wind_directionTrue,256.3, UNKNOWN,ts);
		logger.debug(signalk);
		
		Double dirTrue = (Double) signalk.getValue(vessels_dot_self_dot+ env_wind_directionTrue);
		assertEquals(256.3,dirTrue,0.000001);
		assertEquals(ts, signalk.get(vessels_dot_self_dot+ env_wind_directionTrue+dot+timestamp));
		assertEquals(UNKNOWN, signalk.get(vessels_dot_self_dot+ env_wind_directionTrue+dot+sourceRef));
		
	}
	
	@Test
	public void shouldHandleMultipleValues(){
		/*
		"navigation": {
		    "courseOverGround": {
		        "value": 123,
		        "$source": "nmea1.RMC",
		        "timestamp": "2099-0-01",
		        "values": {
		            "nmea1.RMC": {
		                "value": 123,
		                "timestamp": "2099-0-01",
		            },
		            "nmea2.RMC": {
		                "value": 120,
		                "timestamp": "2099-0-01",
		            }
		        }
		    }
		}
		*/
		SignalKModel signalk = SignalKModelFactory.getMotuTestInstance();
		
		String ts = Util.getIsoTimeString();
		signalk.put(vessels_dot_self_dot+ env_wind_directionTrue,256.3, "masthead",ts);
		logger.debug(signalk);
		
		Double dirTrue = (Double) signalk.getValue(vessels_dot_self_dot+ env_wind_directionTrue);
		assertEquals(256.3,dirTrue,0.000001);
		assertEquals(ts, signalk.get(vessels_dot_self_dot+ env_wind_directionTrue+dot+timestamp));
		assertEquals("masthead", signalk.get(vessels_dot_self_dot+ env_wind_directionTrue+dot+sourceRef));
		//second source
		signalk.put(vessels_dot_self_dot+ env_wind_directionTrue,250.0, "bow",ts);
		logger.debug(signalk);
		dirTrue = (Double) signalk.getValue(vessels_dot_self_dot+ env_wind_directionTrue);
		assertEquals(256.3,dirTrue,0.000001);
		assertEquals(ts, signalk.get(vessels_dot_self_dot+ env_wind_directionTrue+dot+timestamp));
		assertEquals("masthead", signalk.get(vessels_dot_self_dot+ env_wind_directionTrue+dot+sourceRef));
		
		//values
		NavigableMap<String, Object> values = signalk.getValues(vessels_dot_self_dot+ env_wind_directionTrue);
		logger.debug(signalk);
		//Double altTrue = (Double)values.get("a").get(value); 
		//assertEquals(250,dirTrue,0.000001);
		//assertEquals(ts, signalk.get(vessels_dot_self_dot+ env_wind_directionTrue+dot+timestamp));
		//assertEquals("masthead", signalk.get(vessels_dot_self_dot+ env_wind_directionTrue+dot+source));
	}
	@Test
	public void shouldDeleteBranch() throws IOException {
		SignalKModel signalk = SignalKModelFactory.getMotuTestInstance();
		signalk.putAll(TestHelper.getBasicModel().getFullData());
		
		logger.debug(signalk);
		assertTrue(signalk.getTree(vessels_dot_self_dot+ nav_position).size()>0);
		signalk.put(vessels_dot_self_dot+nav_position,null,"dummy",Util.getIsoTimeString());
		assertTrue(signalk.getTree(vessels_dot_self_dot+ nav_position).size()==0);
		
	}
	@Test
	public void shouldNotOverwriteWithEmptyBranch() {
		//TODO: should this fail?
		
	}

	
}
