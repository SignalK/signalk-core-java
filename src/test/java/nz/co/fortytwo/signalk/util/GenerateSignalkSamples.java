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
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nz.co.fortytwo.signalk.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import mjson.Json;
import nz.co.fortytwo.signalk.handler.FullToDeltaConverter;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GenerateSignalkSamples {

	Map<String, Object> keyList = new HashMap<String, Object>();

	@Before
	public void setUp() throws Exception {
		File schemaFile = new File("./../specification/schemas/signalk.json");
		String schemaString = FileUtils.readFileToString(schemaFile);
		// System.out.println(schemaString);
		Json schemaJson = Json.read(schemaString);
		// System.out.println("OK");

		recurse(schemaJson, "", schemaFile, keyList);
		// populate default values
		populateDefault(keyList);
	}

	private void populateDefault(Map<String, Object> keyMap) {
		/*
		 * vessels.[ID].navigation.courseOverGroundTrue
		 * vessels.[ID].navigation.position
		 * vessels.[ID].navigation.position.altitude
		 * vessels.[ID].navigation.position.latitude
		 * vessels.[ID].navigation.position.longitude
		 * vessels.[ID].navigation.position.source
		 * vessels.[ID].navigation.position.timestamp
		 */

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		Json data = Json.object();
		String[] keys = keyList.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		for (String k : keys) {
			if (k.contains("navigation.position") || k.contains("navigation.courseOverGroundTrue")|| k.contains("navigation.speedOverGround")) {
				k = k.replace("(^[2-7][0-9]{8,8}$|^[A-F0-9]{8,8}$)", "366982330");
				k = k.replace("(^[A-Za-z0-9]+$)", "366982330");
				print(k);
				Json tmp = data;
				Json attr = Json.object("_mode", 644,"_owner","self", "_group", "self");
				String dt = DateTime.now().toDateTimeISO().toString();
				tmp.set("sources",Json.object("_attr", attr, "gps_0183_RMC", Json.object("bus", "/dev/ttyUSB1", "src",
						"$GPRMC,033025.000,A,4115.6426,S,17316.9300,E,0.05,245.69,090113,,*15", "timestamp", dt)));
				
				for (String x : k.split("\\.")) {
					if (!tmp.has(x)) {
						if ("speedOverGround".equals(x)) {
							Json meta = Json.object("displayName", "SOG (Knots)","shortName","SOG");
							tmp.set(x, Json.object( "timestamp", dt,"value", Util.kntToMs(0.05) , "source", "sources.gps_0183_RMC","meta",meta, "_attr", attr));
						} else if ("courseOverGroundTrue".equals(x)) {
							Json zoneArray = Json.array().add(new Object[]{250,260,"warn"}).add(new Object[]{260,360,"alarm"}).add(new Object[]{220,230,"warn"}).add(new Object[]{0,220,"alarm"}).add(new Object[]{220,260,"normal"});
							Json meta = Json.object("displayName", "COG (True)","shortName","COG", "warnMethod", "visual", "alarmMethod","sound","zones",zoneArray);
							tmp.set(x, Json.object( "timestamp", dt,"value", 245.69, "source", "sources.gps_0183_RMC","meta",meta, "_attr", attr));
						} else if ("timestamp".equals(x)) {
							tmp.set(x, dt);
						} else if ("source".equals(x)) {
							tmp.set(x, "sources.gps_0183_RMC");
							tmp.set("_attr", attr);
						} else if ("latitude".equals(x)) {
							tmp.set(x, -41.156426);
						} else if ("longitude".equals(x)) {
							tmp.set(x, 173.169300);
						} else if ("altitude".equals(x)) {
							tmp.set(x, 0.0);
						} else
							tmp.set(x, Json.object());
					}

					tmp = tmp.at(x);
				}
			}
		}
		System.out.println(data);
		//make delta format
		FullToDeltaConverter f2d = new FullToDeltaConverter();
		
		System.out.println(f2d.handle(data));
	}
	/*
	 * [250,260,"warn"],
                            [260,360,"alarm"],
                            [220,230,"warn"],
                            [0,220,"alarm" ],
                            [220,260,"normal"]
                            
	 */


	private void recurse(Json schemaJson, String pad, File schemaFile, Map<String, Object> keyList) throws IOException {
		if (schemaJson.at("$ref") != null) {
			String src = schemaJson.at("$ref").asString();
			// System.out.println(pad + "ref:" + src);
			if (src.contains("definitions.json#"))
				return;
			src = src.replace('#', ' ').trim();
			File next = new File(schemaFile.getParentFile(), src);
			if (next.exists()) {
				Json srcJson = Json.read(FileUtils.readFileToString(next));
				recurse(srcJson, pad, schemaFile, keyList);
			} else {
				System.out.println("   err:Cant find " + next.getAbsolutePath());
			}
			return;
		}
		Json props = schemaJson.at("properties");
		if (props != null) {
			Map<String, Json> map = props.asJsonMap();
			for (String e : map.keySet()) {
				// if (e.equals("timestamp"))
				// continue;
				// if (e.equals("source"))
				// continue;

				keyList.put(pad + e, null);
				if (props.at(e).isObject()) {
					recurse(props.at(e), pad + e + ".", schemaFile, keyList);
				}

			}
		}
		/*
		 * Json addProps = schemaJson.at("additionalProperties");
		 * if (addProps != null) {
		 * 
		 * Map<String, Json> map = addProps.asJsonMap();
		 * for (String e : map.keySet()) {
		 * if (e.equals("timestamp"))
		 * continue;
		 * if (e.equals("source"))
		 * continue;
		 * 
		 * keyList.add(pad + e);
		 * if (addProps.at(e).isObject()) {
		 * recurse(addProps.at(e), pad +e+"." , schemaFile, keyList);
		 * }
		 * 
		 * }
		 * }
		 */
		Json patternProps = schemaJson.at("patternProperties");
		if (patternProps != null) {
			Map<String, Json> map = patternProps.asJsonMap();
			for (String e : map.keySet()) {
				// if (e.equals("timestamp"))
				// continue;
				// if (e.equals("source"))
				// continue;
				keyList.put(pad + e, null);
				if (patternProps.at(e).isObject()) {
					recurse(patternProps.at(e), pad + e + ".", schemaFile, keyList);
				}

			}
		}

	}

	private void print(String string) {
		string = string.replace("(^[2-7][0-9]{8,8}$|^[A-F0-9]{8,8}$)", "[ID]");
		string = string.replace("(^[A-Za-z0-9]+$)", "[ID]");
		System.out.println(string);

	}

}
