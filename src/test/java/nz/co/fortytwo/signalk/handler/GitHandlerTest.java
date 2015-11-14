package nz.co.fortytwo.signalk.handler;

import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.Util;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class GitHandlerTest {

	@BeforeClass
	public static void setUp() throws Exception {
		Util.getConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldCloneFreeboardSk() throws Exception {
		SignalKModelFactory.getMotuTestInstance();
		GitHandler handler = new GitHandler();
		handler.install("freeboard-sk");
	}
	@Test
	public void shouldUpgradeFreeboardSk() throws Exception {
		GitHandler handler = new GitHandler();
		handler.upgrade("freeboard-sk");
	}
	
	
}
