/*
 *
 * Copyright (C) 2012-2014 R T Huitema. All Rights Reserved.
 * Web: www.42.co.nz
 * Email: robert@42.co.nz
 * Author: R T Huitema
 *
 * This file is part of the signalk-server-java project
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

import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.self;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import static org.junit.Assert.*;

import java.io.File;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JsonGetHandlerTest {

	private static Logger logger = Logger.getLogger(JsonGetHandlerTest.class);
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldFindFullFormat(){
		String json = "{\"context\":\"vessels.motu\",\"get\":[{\"path\":\"environment.wind.*\",\"format\":\"full\"}]}";
		JsonGetHandler processor = new JsonGetHandler();
		String format = processor.getFormat(Json.read(json));
		assertEquals("full",format);
	}
	@Test
	public void shouldFindDeltaFormat(){
		String json = "{\"context\":\"vessels.motu\",\"get\":[{\"path\":\"environment.wind.*\",\"format\":\"delta\"}]}";
		JsonGetHandler processor = new JsonGetHandler();
		String format = processor.getFormat(Json.read(json));
		assertEquals("delta",format);
	}
	@Test
	public void shouldGetPaths() throws Exception {
		SignalKModel model = SignalKModelFactory.getInstance();
		model.getData().clear();
		model = Util.populateModel(model, new File("src/test/resources/samples/basicModel.txt"));
		model = Util.populateModel(model, new File("src/test/resources/samples/otherModel.txt"));
		String request = "{\"context\":\"vessels.*\",\"get\":[{\"path\":\"navigation.*\"}]}";
		Json json = Json.read(request);
		JsonGetHandler processor = new JsonGetHandler();
		SignalKModel reply = processor.handle(model,json);
		logger.debug(reply);
		assertNotNull(reply);
		//self
		assertEquals(-41.2936935424,reply.get(vessels_dot_self_dot+nav_position_latitude));
		assertEquals(173.2470855712,reply.get(vessels_dot_self_dot+nav_position_longitude));
		//other
		assertEquals(-41.2936935424,reply.get(vessels+".other."+nav_position_latitude));
		assertEquals(173.2470855712,reply.get(vessels+".other."+nav_position_longitude));
		assertTrue(reply.getData().size()>89); 
	}
	@Test
	public void shouldIgnoreListRequest() throws Exception {
		SignalKModel model = SignalKModelFactory.getInstance();
		model.getData().clear();
		model = Util.populateModel(model, new File("src/test/resources/samples/basicModel.txt"));
		model = Util.populateModel(model, new File("src/test/resources/samples/otherModel.txt"));
		String request = "{\"context\":\"vessels."+self+"\",\"list\":[{\"path\":\"navigation.position.*\"},{\"path\":\"navigation.course*\"}]}";
		Json json = Json.read(request);
		JsonGetHandler processor = new JsonGetHandler();
		SignalKModel reply = processor.handle(model,json);
		logger.debug(reply);
		assertNotNull(reply);
		assertTrue(reply.getData().size()==0); 
	}
	
	@Test
	public void shouldProduceSingleVesselGet() throws Exception {
		SignalKModel model = SignalKModelFactory.getInstance();
		model.getData().clear();
		model = Util.populateModel(model, new File("src/test/resources/samples/basicModel.txt"));
		model = Util.populateModel(model, new File("src/test/resources/samples/otherModel.txt"));
		String request = "{\"context\":\"vessels."+self+"\",\"get\":[{\"path\":\"navigation.position.*\"},{\"path\":\"navigation.course*\"}]}";
		Json json = Json.read(request);
		JsonGetHandler processor = new JsonGetHandler();
		SignalKModel reply = processor.handle(model,json);
		logger.debug(reply);
		assertNotNull(reply);
		//self
		assertEquals(-41.2936935424,reply.get(vessels_dot_self_dot+nav_position_latitude));
		assertEquals(173.2470855712,reply.get(vessels_dot_self_dot+nav_position_longitude));
		//other
		assertNull(reply.get(vessels+".other."+nav_position_latitude));
		assertNull(reply.get(vessels+".other."+nav_position_longitude));
		assertTrue(reply.getData().size()==9); 
		 
	}
	
	@Test
	public void shouldProduceMultipleVesselGet() throws Exception {
		SignalKModel model = SignalKModelFactory.getInstance();
		model.getData().clear();
		model = Util.populateModel(model, new File("src/test/resources/samples/basicModel.txt"));
		model = Util.populateModel(model, new File("src/test/resources/samples/otherModel.txt"));
		String request = "{\"context\":\"vessels.*\",\"get\":[{\"path\":\"navigation.position.*\"},{\"path\":\"navigation.course*\"}]}";
		Json json = Json.read(request);
		JsonGetHandler processor = new JsonGetHandler();
		SignalKModel reply = processor.handle(model,json);
		logger.debug(reply);
		assertNotNull(reply);
		//self
		assertEquals(-41.2936935424,reply.get(vessels_dot_self_dot+nav_position_latitude));
		assertEquals(173.2470855712,reply.get(vessels_dot_self_dot+nav_position_longitude));
		//other
		assertEquals(-41.2936935424,reply.get(vessels+".other."+nav_position_latitude));
		assertEquals(173.2470855712,reply.get(vessels+".other."+nav_position_longitude));
		assertTrue(reply.getData().size()==18); 
		 
	}
	@Test
	public void shouldProduceSpecificPathList() throws Exception {
		SignalKModel model = SignalKModelFactory.getInstance();
		model.getData().clear();
		model = Util.populateModel(model, new File("src/test/resources/samples/basicModel.txt"));
		model = Util.populateModel(model, new File("src/test/resources/samples/otherModel.txt"));
		//test ? works
		String request = "{\"context\":\"vessels.*\",\"get\":[{\"path\":\"navigation.position.l?t*\"}]}";
		Json json = Json.read(request);
		JsonGetHandler processor = new JsonGetHandler();
		SignalKModel reply = processor.handle(model,json);
		assertNotNull(reply);
		logger.debug(reply);
		assertEquals(-41.2936935424,reply.get(vessels_dot_self_dot+nav_position_latitude));
		assertNull(reply.get(vessels_dot_self_dot+nav_position_longitude));
		assertEquals(-41.2936935424,reply.get(vessels+".other."+nav_position_latitude));
		assertNull(reply.get(vessels+".other."+nav_position_longitude));
		assertTrue(reply.getData().size()==2);
	}

}
