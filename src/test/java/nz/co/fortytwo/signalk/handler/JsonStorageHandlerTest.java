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
import nz.co.fortytwo.signalk.util.ConfigConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class JsonStorageHandlerTest {

	String jsonDiff = "{\"context\":\"resources\",\"put\":[{\"timestamp\":\"2015-03-23T01:57:01.856Z\",\"values\":[{\"path\":\"routes.SignalKConstants.self.currentTrack\",\"value\":{\"name\":\"CurrentTrack\",\"description\":\"Thecurrentvesseltrack\",\"mimetype\":\"application/vnd.geo+json\",\"payload\":{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[23.366832680000005,59.85969252999999],[23.367125929999997,59.85961481000001],[23.367415009999995,59.85953943999999]]},\"properties\":{\"name\":\"CurrentTrack\",\"description\":\"Thecurrentvesseltrack\"}}}}],\"source\":\"vessels.motu\"}]}";
	
	private static Logger logger = Logger.getLogger(JsonStorageHandlerTest.class);
	@BeforeClass
	public static void setUp() throws Exception {
		Util.getConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldProcessPut() throws Exception{
		Json diff = Json.read(jsonDiff);
		File dataRoot = new File(Util.getConfigProperty(ConfigConstants.STORAGE_ROOT));
		//empty it
		FileUtils.deleteDirectory(dataRoot);
		
		JsonStorageHandler processor = new JsonStorageHandler();
		Json output = processor.handle(diff);
		logger.debug(output);
		assertTrue(!output.at("put").at(0).at("values").at(0).at("value").has("payload"));
		String filePath = output.at("put").at(0).at("values").at(0).at("value").at("uri").asString();
		logger.debug(filePath);
		assertNotNull(filePath);
		File data = new File(Util.getConfigProperty(ConfigConstants.STORAGE_ROOT)+filePath);
		assertTrue(data.exists());
		//logger.debug(filePath);
		//now retrieve it again
		output = processor.handle(output);
		assertTrue(output.at("put").at(0).at("values").at(0).at("value").has("payload"));
		assertTrue(!output.at("put").at(0).at("values").at(0).at("value").has("uri"));
		data.delete();
	}
	
}
