package nz.co.fortytwo.signalk.handler;

import static org.junit.Assert.*;

import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GitHandlerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldCloneFreeboardSk() throws Exception {
		GitHandler handler = new GitHandler();
		handler.install("freeboard-sk");
	}
	@Test
	public void shouldUpgradeFreeboardSk() throws Exception {
		GitHandler handler = new GitHandler();
		handler.upgrade("freeboard-sk");
	}
	
	
}
