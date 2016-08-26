package nz.co.fortytwo.signalk.util;

import java.io.File;
import java.nio.charset.StandardCharsets;

import mjson.Json;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GenerateMimeMap {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		String data = FileUtils.readFileToString(new File("./src/test/resources/samples/mimedb.json"));
		Json mimeTypes = Json.read(data);
		StringBuilder buffer = new StringBuilder();
		for(String key: mimeTypes.asJsonMap().keySet()){
			
			Json type = mimeTypes.at(key);
			if( type.has("extensions")){
				Json exts = type.at("extensions");
				for(Json ext: exts.asJsonList()){
					buffer.insert(0, key +"="+ext.asString()+"\n");
				}
			}
		}
		FileUtils.writeStringToFile(new File("./src/main/resources/mime.types"), buffer.toString(), StandardCharsets.UTF_8);
	}

}
