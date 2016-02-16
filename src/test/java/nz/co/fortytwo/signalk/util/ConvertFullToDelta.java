package nz.co.fortytwo.signalk.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import mjson.Json;
import nz.co.fortytwo.signalk.handler.FullToDeltaConverter;
import nz.co.fortytwo.signalk.handler.FullToDeltaConverterTest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

public class ConvertFullToDelta {
	private static Logger logger = Logger.getLogger(FullToDeltaConverterTest.class);
	@Test
	public void shouldCreateDelta() throws IOException {
		File f = new File("./src/test/resources/samples/full.json");
		logger.debug(f.getAbsolutePath());
		String contents = FileUtils.readFileToString(f);
		Json data = Json
				.read(contents);
		
		FullToDeltaConverter processor = new FullToDeltaConverter();
		Json out = processor.handle(data).get(0);
		logger.debug(out);
		
	}

}
