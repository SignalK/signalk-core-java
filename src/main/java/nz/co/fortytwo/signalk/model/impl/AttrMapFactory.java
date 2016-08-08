package nz.co.fortytwo.signalk.model.impl;

import static nz.co.fortytwo.signalk.util.SignalKConstants.CONFIG;
import static nz.co.fortytwo.signalk.util.SignalKConstants.communication_crewNames;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_anchor;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_course;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_lights;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_racing;
import static nz.co.fortytwo.signalk.util.SignalKConstants.resources;
import static nz.co.fortytwo.signalk.util.SignalKConstants.sources;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nz.co.fortytwo.signalk.model.Attr;
import nz.co.fortytwo.signalk.util.Util;

public class AttrMapFactory {

	private static final String SIGNALK_ATTR_SAVE_FILE = "./conf/signalk-attr.json";
	private static Logger logger = LogManager.getLogger(AttrMapFactory.class);
	private static ConcurrentSkipListMap<String, Attr> attrMap;
	private static String rootPath="";

	static {
		rootPath=Util.getRootPath();
		if (attrMap == null)
			attrMap = new ConcurrentSkipListMap<String,Attr>();
		//setAttrDefaults(attrMap);
		//try {
		//	AttrMapFactory.loadConfig(attrMap);
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//	System.exit(1);
		//}
	}
	public static NavigableMap<String, Attr> getInstance() {
		return attrMap;
	}
	
	protected static void loadConfig(NavigableMap<String, Attr> navigableMap) throws IOException {
		File attrFile = new File(rootPath+SIGNALK_ATTR_SAVE_FILE);
		logger.info("Checking for previous attr: "+attrFile.getAbsolutePath());
		if(attrFile.exists()){
			for(String line: FileUtils.readLines(attrFile)){
				try{
					String[] data = line.split("=");
					navigableMap.put(data[0].trim(), new Attr(data[1].split(",")));
					
				}catch(Exception ex){
					logger.error(ex.getMessage(),ex);
				}
			}
			logger.info("   Saved attr's loaded from "+rootPath+SIGNALK_ATTR_SAVE_FILE);
		}else{
			logger.info("   Saved attr not found, creating default");
			//write a new one for next time
			saveConfig(navigableMap);
			
		}
		
	}
	
	/**
	 * Save the current state of the signalk attr
	 * 
	 * @throws IOException
	 */
	public static void saveConfig(NavigableMap<String, Attr> navigableMap) throws IOException {
		saveConfig(navigableMap,new File(rootPath+SIGNALK_ATTR_SAVE_FILE));
	}
	
	public static void saveConfig(NavigableMap<String, Attr> model, File attrFile ) throws IOException {
		StringBuffer buffer = new StringBuffer();
		if (model != null && model.size()>0) {
			for( String key: model.keySet()){
				buffer.append(key+"="+model.get(key).asString()+"\n");
	    	}
			FileUtils.writeStringToFile(attrFile, buffer.toString(), StandardCharsets.UTF_8);
			logger.debug("   Saved attr state to "+rootPath+attrFile.getAbsolutePath());
		}

	}
	protected static void setAttrDefaults(NavigableMap<String, Attr> navigableMap) {
		//config, private to vessel
		navigableMap.put(CONFIG, new Attr(750,"self","self"));
		//own vessel, public with these private to vessel
		navigableMap.put(vessels_dot_self, new Attr());
		navigableMap.put(vessels_dot_self_dot+nav_course, new Attr(750,"self","self"));
		navigableMap.put(vessels_dot_self_dot+nav_anchor, new Attr(750,"self","self"));
		navigableMap.put(vessels_dot_self_dot+nav_lights, new Attr(750,"self","self"));
		navigableMap.put(vessels_dot_self_dot+nav_racing, new Attr(750,"self","self"));
		navigableMap.put(vessels_dot_self_dot+communication_crewNames, new Attr(750,"self","self"));
		//resources, private to vessel
		navigableMap.put(resources, new Attr(750,"self","self"));
		navigableMap.put(sources, new Attr(750,"self","self"));
		
		//default
		navigableMap.put(vessels, new Attr());
		
		
	}
}
