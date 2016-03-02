package nz.co.fortytwo.signalk.handler;

import static nz.co.fortytwo.signalk.util.SignalKConstants.nav;
import static nz.co.fortytwo.signalk.util.SignalKConstants.self;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.TestHelper;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class RestApiHandlerTest {

	private static Logger logger = Logger.getLogger(RestApiHandlerTest.class);
	
	@BeforeClass
	public static void setUp() throws Exception {
		Util.getConfig();
		Util.setSelf("motu");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldGetSelfPosition() throws Exception {
		SignalKModel model = SignalKModelFactory.getMotuTestInstance();
		RestApiHandler api = new RestApiHandler();
		
		model.putAll(TestHelper.getBasicModel().getFullData());
		model.putAll(TestHelper.getOtherModel().getFullData());
		
		HttpServletRequest  mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getPathInfo()).thenReturn(SignalKConstants.SIGNALK_API+vessels+"/"+self+"/"+nav+"/position");

		HttpServletResponse  mockedResponse = Mockito.mock(HttpServletResponse.class);
		Json reply = (Json)api.processGet(mockedRequest, mockedResponse, model);
		logger.debug("Repy="+reply);
		assertEquals(-41.2936935424d,reply.at(vessels).at(self).at(nav).at("position").at("latitude").asDouble(),0.0001);
		assertEquals(173.2470855712d,reply.at(vessels).at(self).at(nav).at("position").at("longitude").asDouble(),0.0001);
		//assertTrue(reply.getData().size()==3); 
		verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
		verify(mockedResponse).setContentType("application/json");
		
		//assertEquals(200, mockedResponse.getStatus());
	}
	
	@Test
	public void shouldGetOtherPosition() throws Exception {
		RestApiHandler api = new RestApiHandler();
		SignalKModel model = SignalKModelFactory.getMotuTestInstance();
		model.putAll(TestHelper.getBasicModel().getFullData());
		model.putAll(TestHelper.getOtherModel().getFullData());
		
		HttpServletRequest  mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getPathInfo()).thenReturn(SignalKConstants.SIGNALK_API+vessels+"/other/"+nav+"/position");

		HttpServletResponse  mockedResponse = Mockito.mock(HttpServletResponse.class);
		Json reply = (Json)api.processGet(mockedRequest, mockedResponse, model);
		logger.debug("Repy="+reply);
		assertEquals(-41.2936935424,reply.at(vessels).at("other").at(nav).at("position").at("latitude").asDouble(),0.0001);
		assertEquals(173.2470855712,reply.at(vessels).at("other").at(nav).at("position").at("longitude").asDouble(),0.0001);
		//assertTrue(reply.getData().size()==3); 
		verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
		verify(mockedResponse).setContentType("application/json");
	}
	
	@Test
	public void shouldGetNotFound() throws Exception {
		RestApiHandler api = new RestApiHandler();
		SignalKModel model = SignalKModelFactory.getMotuTestInstance();
		model.putAll(TestHelper.getBasicModel().getFullData());
		model.putAll(TestHelper.getOtherModel().getFullData());
		
		HttpServletRequest  mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getPathInfo()).thenReturn(SignalKConstants.SIGNALK_API+vessels+"/"+self+"/"+nav+"/not_here");

		HttpServletResponse  mockedResponse = Mockito.mock(HttpServletResponse.class);
		Json reply = (Json)api.processGet(mockedRequest, mockedResponse, model);
		logger.debug("Repy="+reply);
		//assertEquals(-41.2936935424,reply.get(vessels).at(SignalKConstants.self_dot+nav_position_latitude));
		//assertEquals(173.2470855712,reply.get(vessels).at(SignalKConstants.self_dot+nav_position_longitude));
		assertNull(reply); 
		verify(mockedResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
		//assertEquals(200, mockedResponse.getStatus());
	}
	@Test
	public void shouldGetNotFound1() throws Exception {
		RestApiHandler api = new RestApiHandler();
		SignalKModel model = SignalKModelFactory.getMotuTestInstance();
		model.putAll(TestHelper.getBasicModel().getFullData());
		model.putAll(TestHelper.getOtherModel().getFullData());
		
		HttpServletRequest  mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getPathInfo()).thenReturn(SignalKConstants.SIGNALK_API+"vess/"+self+"/"+nav+"/");

		HttpServletResponse  mockedResponse = Mockito.mock(HttpServletResponse.class);
		Json reply = (Json)api.processGet(mockedRequest, mockedResponse, model);
		logger.debug("Repy="+reply);
		//assertEquals(-41.2936935424,reply.get(vessels).at(SignalKConstants.self_dot+nav_position_latitude));
		//assertEquals(173.2470855712,reply.get(vessels).at(SignalKConstants.self_dot+nav_position_longitude));
		assertNull(reply); 
		verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		//assertEquals(200, mockedResponse.getStatus());
	}
	
	@Test
	public void shouldGetBadRequest1() throws Exception {
		RestApiHandler api = new RestApiHandler();
		SignalKModel model = SignalKModelFactory.getMotuTestInstance();
		model.putAll(TestHelper.getBasicModel().getFullData());
		model.putAll(TestHelper.getOtherModel().getFullData());
		
		HttpServletRequest  mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getPathInfo()).thenReturn(SignalKConstants.SIGNALK_API.substring(0,11));

		HttpServletResponse  mockedResponse = Mockito.mock(HttpServletResponse.class);
		Json reply = (Json)api.processGet(mockedRequest, mockedResponse, model);
		logger.debug("Repy="+reply);
		//assertEquals(-41.2936935424,reply.get(vessels).at(SignalKConstants.self_dot+nav_position_latitude));
		//assertEquals(173.2470855712,reply.get(vessels).at(SignalKConstants.self_dot+nav_position_longitude));
		assertNull(reply); 
		verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		//assertEquals(200, mockedResponse.getStatus());
	}

}
