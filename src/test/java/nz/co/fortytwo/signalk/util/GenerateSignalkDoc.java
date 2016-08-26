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


import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.pgn;
import static nz.co.fortytwo.signalk.util.SignalKConstants.source;
import static nz.co.fortytwo.signalk.util.SignalKConstants.sourceRef;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;
import static nz.co.fortytwo.signalk.util.SignalKConstants.type;
import static nz.co.fortytwo.signalk.util.SignalKConstants.value;
import static nz.co.fortytwo.signalk.util.SignalKConstants.values;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mjson.Json;

public class GenerateSignalkDoc {

	//private  defMap;


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	//@Ignore
	public void test() throws Exception {
		
		createSignalkData("./../", "motu", "", true,true);
	}
	
	public static void createSignalkData(String schemaDir, String uuid, String filter, boolean skipAttr, boolean skipMeta ) throws IOException{
		File schemaRoot = new File(schemaDir);
		createSignalkData(schemaRoot, uuid, filter, skipAttr, skipMeta);
	}
	public static void createSignalkData(File schemaRoot, String uuid, String filter, boolean skipAttr, boolean skipMeta ) throws IOException{
		
		//add definitions
		File[] definitions = new File[]{
				new File(schemaRoot,"specification/schemas/definitions.json"),
				//new File(schemaRoot,"specification/schemas/external/geojson/geometry.json"),
				new File(schemaRoot,"specification/schemas/groups/environment.json"),
				new File(schemaRoot,"specification/schemas/groups/tanks.json"),
				new File(schemaRoot, "specification/schemas/groups/electrical.json")};
		Map<String, Json> defMap=addDefinitions(definitions);
		
		//load schema
		File schemaFile = new File("./../specification/schemas/signalk.json");
		String schemaString = FileUtils.readFileToString(schemaFile);
		Json schemaJson = Json.read(schemaString);
		
		ConcurrentSkipListMap<String, Object> keyList = new ConcurrentSkipListMap<String,Object>();
		recurse(schemaJson, defMap,"", schemaFile, keyList, skipAttr, skipMeta);
		
		
		// output 
		
		System.out.println("\nDocs:");
		for(Entry<String, Object> entry: keyList.entrySet()){
			if(entry.getKey().contains(filter)){
				String key = entry.getKey();
				//key=key.replace("vessels.motu.","");
				String emphasis = "";
				//is this a branch or leaf
				if(keyList.subMap(key,true, key+".\uFFFD",true).size()>1){
					emphasis="**";
				}
				String val = key;
				val=val.replace(".id.", ".[id].");
				int pos = val.split("\\.").length-1;
				int pos1=val.lastIndexOf(".");
				String path = val.substring(0,pos1+1 );
				pos1=path.lastIndexOf(".",pos1-1);
				String parent = path.substring(pos1+1 );
				val=val.substring(val.lastIndexOf(".")+1);
				if(emphasis.length()>0){
					System.out.println("|"+path+emphasis+val+emphasis+" | "+entry.getValue()+"|");
				}else{
					System.out.println("|"+StringUtils.repeat("&nbsp;", pos*4)+".."+parent+val+" | "+entry.getValue()+"|");
				}
			}
		}
		
		
	}


	private static Map<String, Json> addDefinitions(File[] definitions) throws IOException {
		Map<String,Json> definitionsMap = new HashMap<>();
		for(File def: definitions){
			String definitionsString = FileUtils.readFileToString(def);
			Json definitionsJson = Json.read(definitionsString);
			definitionsMap.putAll(definitionsJson.at("definitions").asJsonMap());
		}
		return definitionsMap;
	}

	private static void recurse(Json schemaJson, Map<String, Json> defMap, String pad, File schemaFile, Map<String, Object>  keyList, boolean skipAttr, boolean skipMeta) throws IOException {
		
	
		if(schemaJson.at("$ref")!=null){
			String src = schemaJson.at("$ref").asString();
			//System.out.println(pad + "ref:" + src);
			if(src.contains("definitions.json#")||src.contains("#/definitions/")){
				//System.out.println("   Getting "+src);
				//../definitions.json#/definitions/timestamp
				recurseDefs(defMap, src,pad,schemaFile,keyList,skipAttr,skipMeta);
				return;
			}
			src=src.replace('#',' ').trim();
			File next = new File(schemaFile.getParentFile(),src);
			
			if(next.exists()){
				Json srcJson = Json.read(FileUtils.readFileToString(next));
				recurse(srcJson, defMap,pad, schemaFile, keyList,skipAttr,skipMeta);
			}else{
				System.out.println("   err:Cant find "+next.getAbsolutePath());
			}
			return;
		}
		if(schemaJson.at("allOf")!=null){
			List<Json> list = schemaJson.at("allOf").asJsonList();
			//System.out.println("   Recursing allOf:"+list);
			for(Json j:list){
				//System.out.println("     Recurse j:"+j);
				recurse(j,defMap,pad,schemaFile,keyList,skipAttr,skipMeta);
			}
			
			
		}
		
		if(schemaJson.at("anyOf")!=null){
			List<Json> list = schemaJson.at("anyOf").asJsonList();
			for(Json j:list){
				//System.out.println("     Recurse j:"+j);
				recurse(j,defMap,pad,schemaFile,keyList,skipAttr,skipMeta);
			}
			
			
		}
		Json props = schemaJson.at("properties");
		
		if (props != null) {
			Map<String, Json> map = props.asJsonMap();
			//System.out.println("properties="+map.keySet());
			for (String e : map.keySet()) {
				//skip meta and _attr?
				if (skipMeta && e.equals("meta")){
					continue;
				}
				if (skipAttr && e.equals("_attr")){
					continue;
				}
				if (e.equals(timestamp)
						||e.equals(sourceRef) 
						|| e.equals(source)
						||e.equals(pgn)
						||e.equals(value)
						||e.equals(values)
						||e.equals("sentence")
						||e.equals("(^\\$source$)")){
					//keyList.put(pad +  source+dot+type, "testType");
					//keyList.put(pad +  source+".label", "testLabel");
					continue;
				}
				//System.out.println("properties key="+pad +  e);
				String desc = "";
				String units = "";
				if(map.get(e).has("description")){
					desc = map.get(e).at("description").asString();
				}
				

				if(map.get(e).has("units")){
					units = map.get(e).at("units").asString();
				}
				
				keyList.put(pad +  sanitiseKey(e),units+" | "+ desc);
					
				if (props.at(e).isObject()) {
					//System.out.println("   Recurse:"+e);
					recurse(props.at(e), defMap,pad +sanitiseKey(e)+"." , schemaFile, keyList,skipAttr,skipMeta);
				}

			}
		}
		
		Json patternProps = schemaJson.at("patternProperties");
		if (patternProps != null) {
			Map<String, Json> map = patternProps.asJsonMap();
			for (String e : map.keySet()) {
				if (e.equals("timestamp")){
					//keyList.put(pad +  sanitiseKey(e), Util.getIsoTimeString());
					continue;
				}
				if (e.equals("source")
						||e.equals("(^\\$source$)")){
					continue;
				}
				
				//System.out.println("patternProperties key="+pad +  sanitiseKey(e));
				String desc = "";
				String units = "";
				
				if(map.get(e).has("description")){
					desc = map.get(e).at("description").asString();
				}
				
				if(map.get(e).has("units")){
					units = map.get(e).at("units").asString();
				}
				
				keyList.put(pad +  sanitiseKey(e),units+" | "+ desc);
				if (patternProps.at(e).isObject()) {
					recurse(patternProps.at(e),defMap, pad +sanitiseKey(e)+".", schemaFile, keyList,skipAttr,skipMeta);
				}

			}
		}
		
	}

	private static String sanitiseKey(String e) {
		//String uuid = UUID.randomUUID().toString();
		if(e.equals("(^urn:mrn:(imo|signalk):(mmsi:[2-7][0-9]{8,8}|uuid:[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-4[0-9A-Fa-f]{3}-[89ABab][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}))|^(http(s?):.*|mailto:.*|tel:(\\+?)[0-9]{4,})$"))return "id";
		if(e.equals("^urn:mrn:signalk:uuid:[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-4[0-9A-Fa-f]{3}-[89ABab][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}$"))return "id";
		if(e.equals("(^source$)"))return "source";
		if(e.equals("(^message$)"))return "message";
		if(e.equals("(^method$)"))return "method";
		if(e.equals("(^timestamp$)"))return "timestamp";
		if(e.equals("(^state$)"))return "state";
		if(e.equals("(^[A-Za-z0-9]+$)"))return "id";
		if(e.equals("(^[A-Za-z0-9_-]{8,}$)"))return "id";
		if(e.equals(".*"))return "id";
		if(e.equals("(^[A-Za-z0-9-]+$)"))return "id";
		if(e.equals("(^[A-Za-z0-9_-]+$)"))return "id";
		if(e.equals("(single)|([A-C])"))return "id";
		if(e.equals("(^[a-zA-Z0-9]+$)"))return "id";
		return e;
	}

	private static void recurseDefs(Map<String, Json> defMap, String src, String pad, File schemaFile,
			Map<String, Object> keyList, boolean skipAttr, boolean skipMeta) throws IOException {
		int pos = src.lastIndexOf("/")+1;
		//System.out.println("   Lookup "+src.substring(pos)+" from "+defMap);
		Json def = defMap.get(src.substring(pos));
		//System.out.println("   Found "+def);
		try{
			recurse(def, defMap, pad, schemaFile, keyList,skipAttr,skipMeta);
		}catch(NullPointerException ne){
			System.out.println("   Null Error on  "+pad+"/"+src);
		}
		return;
		
	}

	

	

}
