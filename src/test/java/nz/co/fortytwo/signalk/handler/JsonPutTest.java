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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.Constants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JsonPutTest {

	String jsonDiff = "{\"context\":\"resources\",\"put\":[{\"timestamp\":\"2015-03-23T01:57:01.856Z\",\"values\":[{\"path\":\"routes.self.currentTrack\",\"value\":{\"name\":\"CurrentTrack\",\"description\":\"Thecurrentvesseltrack\",\"mimetype\":\"application/vnd.geo+json\",\"payload\":{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[23.366832680000005,59.85969252999999],[23.367125929999997,59.85961481000001],[23.367415009999995,59.85953943999999]]},\"properties\":{\"name\":\"CurrentTrack\",\"description\":\"Thecurrentvesseltrack\"}}}}],\"source\":\"vessels.motu\"}]}";
	
	private static Logger logger = Logger.getLogger(JsonPutTest.class);
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldProcessDiff() throws Exception{
		Json diff = Json.read(jsonDiff);
		File dataRoot = new File(Util.getConfigProperty(Constants.STORAGE_ROOT));
		//empty it
		FileUtils.deleteDirectory(dataRoot);
		
		JsonPutHandler processor = new JsonPutHandler();
		SignalKModel output = processor.handle(diff);
		logger.debug(output);
		String filePath = (String) output.get("resources.routes.motu.currentTrack.value.uri");
		logger.debug(filePath);
		assertNotNull(filePath);
		File data = new File(Util.getConfigProperty(Constants.STORAGE_ROOT)+filePath);
		assertTrue(data.exists());
		//logger.debug(filePath);
		data.delete();
	}
	
}
