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
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav;
import static nz.co.fortytwo.signalk.util.SignalKConstants.source;
import static nz.co.fortytwo.signalk.util.SignalKConstants.sourceRef;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;
import static nz.co.fortytwo.signalk.util.SignalKConstants.type;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

import mjson.Json;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.dma.ais.message.NavigationalStatus;

public class GenerateSignalkData {

	private Map<String, Json> defMap;
	private boolean skipMeta=true;
	private boolean skipAttr=true;
	private String filter=nav;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	//@Ignore
	public void test() throws Exception {

		File definitionsFile = new File("./../specification/schemas/definitions.json");
		String definitionsString = FileUtils.readFileToString(definitionsFile);
		Json definitionsJson = Json.read(definitionsString);
		defMap = definitionsJson.at("definitions").asJsonMap();
		
		File elecFile = new File("./../specification/schemas/groups/electrical_dc.json");
		String elecString = FileUtils.readFileToString(elecFile);
		Json elecJson = Json.read(elecString);
		defMap.putAll(elecJson.at("definitions").asJsonMap());
		
		
		File schemaFile = new File("./../specification/schemas/signalk.json");
		String schemaString = FileUtils.readFileToString(schemaFile);
		//System.out.println(schemaString);
		Json schemaJson = Json.read(schemaString);
		//System.out.println("OK");
		ConcurrentSkipListMap<String, Object> keyList = new ConcurrentSkipListMap<String,Object>();
		recurse(schemaJson, "", schemaFile, keyList);
		
		for(Entry<String, Object> entry: keyList.entrySet()){
			if(entry.getKey().contains(filter)){
				String mark = (entry.getValue() instanceof String)?"\"":"";
				System.out.println("model.getFullData().put(\""+entry.getKey()+"\","+mark+entry.getValue()+mark+");");
			}
		}
		System.out.println("total:"+keyList.size());
	}


	private void recurse(Json schemaJson, String pad, File schemaFile, Map<String, Object>  keyList) throws IOException {
		
	
		if(schemaJson.at("$ref")!=null){
			String src = schemaJson.at("$ref").asString();
			//System.out.println(pad + "ref:" + src);
			if(src.contains("definitions.json#")||src.contains("#/definitions/")){
				//System.out.println("   Getting "+src);
				//../definitions.json#/definitions/timestamp
				recurseDefs(src,pad,schemaFile,keyList);
				return;
			}
			src=src.replace('#',' ').trim();
			File next = new File(schemaFile.getParentFile(),src);
			
			if(next.exists()){
				Json srcJson = Json.read(FileUtils.readFileToString(next));
				recurse(srcJson, pad, schemaFile, keyList);
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
				recurse(j,pad,schemaFile,keyList);
			}
			
			
		}
		
		if(schemaJson.at("anyOf")!=null){
			List<Json> list = schemaJson.at("anyOf").asJsonList();
			for(Json j:list){
				//System.out.println("     Recurse j:"+j);
				recurse(j,pad,schemaFile,keyList);
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
				if (e.equals(timestamp)){
					keyList.put(pad +  sanitiseKey(e), Util.getIsoTimeString());
					continue;
				}
				if (e.equals(sourceRef) || e.equals(source)){
					keyList.put(pad +  source+dot+type, "testType");
					keyList.put(pad +  source+".label", "testLabel");
					continue;
				}
				//System.out.println("properties key="+pad +  e);
				try{
					Object value = getValueForType(map.get(e).asJsonMap());
					if(value!=null){
						keyList.put(pad +  sanitiseKey(e), value);
						//System.out.println("    key="+pad +  sanitiseKey(e)+"="+value); 
						
					}
				}catch(UnsupportedOperationException use){
					System.out.println("   No type in map="+e+" : "+map);
				}
				if (props.at(e).isObject()) {
					//System.out.println("   Recurse:"+e);
					recurse(props.at(e), pad +sanitiseKey(e)+"." , schemaFile, keyList);
				}

			}
		}
		/*Json addProps = schemaJson.at("additionalProperties");
		if (addProps != null) {

			Map<String, Json> map = addProps.asJsonMap();
			for (String e : map.keySet()) {
				if (e.equals("timestamp"))
					continue;
				if (e.equals("source"))
					continue;
				
				keyList.add(pad +  e);
				if (addProps.at(e).isObject()) {
					recurse(addProps.at(e), pad +e+"." , schemaFile, keyList);
				}

			}
		}*/
		Json patternProps = schemaJson.at("patternProperties");
		if (patternProps != null) {
			Map<String, Json> map = patternProps.asJsonMap();
			for (String e : map.keySet()) {
				if (e.equals("timestamp")){
					keyList.put(pad +  sanitiseKey(e), Util.getIsoTimeString());
					continue;
				}
				if (e.equals("source")){
					keyList.put(pad +  sanitiseKey(e)+dot+type, "testType");
					keyList.put(pad +  sanitiseKey(e)+".label", "testLabel");
					continue;
				}
				
				//System.out.println("patternProperties key="+pad +  sanitiseKey(e));
				Object value = getValueForType(map.get(e).asJsonMap());
				if(value!=null){
					keyList.put(pad +  sanitiseKey(e), value);
					//System.out.println("    key="+pad +  sanitiseKey(e)+"="+value);
				}
				if (patternProps.at(e).isObject()) {
					recurse(patternProps.at(e), pad +sanitiseKey(e)+".", schemaFile, keyList);
				}

			}
		}
		
	}

	private String sanitiseKey(String e) {
		String uuid = UUID.randomUUID().toString();
		if(e.equals("(^urn:mrn:(imo|signalk):(mmsi:[2-7][0-9]{8,8}|uuid:[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-4[0-9A-Fa-f]{3}-[89ABab][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}))|^(http(s?):.*|mailto:.*|tel:(\\+?)[0-9]{4,})$"))return "urn:mrn:signalk:uuid:"+uuid;
		if(e.equals("^urn:mrn:signalk:uuid:[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-4[0-9A-Fa-f]{3}-[89ABab][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}$"))return "urn:mrn:signalk:uuid:"+uuid;
		if(e.equals("(^source$)"))return "source";
		if(e.equals("(^message$)"))return "message";
		if(e.equals("(^method$)"))return "method";
		if(e.equals("(^timestamp$)"))return "timestamp";
		if(e.equals("(^state$)"))return "state";
		if(e.equals("(^[A-Za-z0-9]+$)"))return uuid.substring(0,7);
		if(e.equals("(^[A-Za-z0-9_-]{8,}$)"))return uuid.substring(0,8);
		if(e.equals(".*"))return uuid.substring(0,7);
		if(e.equals("(^[A-Za-z0-9-]+$)"))return uuid.substring(0,7);
		if(e.equals("(^[A-Za-z0-9_-]+$)"))return uuid.substring(0,7);
		return e;
	}

	private void recurseDefs(String src, String pad, File schemaFile,
			Map<String, Object> keyList) throws IOException {
		int pos = src.lastIndexOf("/")+1;
		//System.out.println("   Lookup "+src.substring(pos)+" from "+defMap);
		Json def = defMap.get(src.substring(pos));
		//System.out.println("   Found "+def);
		try{
			recurse(def, pad, schemaFile, keyList);
		}catch(NullPointerException ne){
			System.out.println("   Null Error on  "+pad+"/"+src);
		}
		return;
		
	}

	private Object getValueForType(Map<String, Json> map) {
		
		//if(map.containsKey("example"))return map.get("example").getValue();
		//System.out.println("   map="+map);
		
		String type = null;
		try{
			if(map.containsKey("type")){
				Json json = map.get("type");
				if(json.isArray()){
					type=json.asJsonList().get(0).asString();
				}else{
					type=json.asString();
				}
			}
		}catch(UnsupportedOperationException use){
			System.out.println("   No type in map="+map);
		}
		if("object".equals(type))return null;
		if("string".equals(type)){
			if(map.containsKey("enum")){
				Json eNum = map.get("enum");
				return eNum.asJsonList().get(0).asString();
			}
			return "ipsum";
		}
		if("boolean".equals(type))return true;
		if("integer".equals(type))return Math.round(Math.random()*100);
		if("number".equals(type))return Math.random()*100;
		if("mmsi".equals(type))return "398765432";
		if("array".equals(type)){
			//map={minItems=2, description="A single position, in x,y order (Lon, Lat)", items=[{"type":"number"},{"type":"number"}], additionalItems=false, type="array"}
			Json items = map.get("items");
			if(items==null)return "[]";
			StringBuffer val = new StringBuffer("[");
			if(items.isArray()){
				for(Json item:items.asJsonList()){
					val.append(getValueForType(item.asJsonMap()));
					val.append(",");
				}
				val.delete(val.length()-1, val.length());
			}else{
				val.append(getValueForType(items.asJsonMap()));
			}
			
			return val.append("]").toString();
		}
		
		return type;
	}

	

}
