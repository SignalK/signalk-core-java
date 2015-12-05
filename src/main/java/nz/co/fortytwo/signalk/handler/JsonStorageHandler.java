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
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nz.co.fortytwo.signalk.handler;

import static nz.co.fortytwo.signalk.util.ConfigConstants.MIME_TYPE;
import static nz.co.fortytwo.signalk.util.ConfigConstants.PAYLOAD;
import static nz.co.fortytwo.signalk.util.ConfigConstants.STORAGE_ROOT;
import static nz.co.fortytwo.signalk.util.ConfigConstants.STORAGE_URI;
import static nz.co.fortytwo.signalk.util.SignalKConstants.CONTEXT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.PATH;
import static nz.co.fortytwo.signalk.util.SignalKConstants.PUT;
import static nz.co.fortytwo.signalk.util.SignalKConstants.UPDATES;
import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.value;
import static nz.co.fortytwo.signalk.util.SignalKConstants.values;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mjson.Json;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Handles json messages with large blobs 
 * 
 * @author robert
 * 
 */
public class JsonStorageHandler {

	private static Logger logger = Logger.getLogger(JsonStorageHandler.class);
	private File storageDir = new File(Util.getConfigProperty(STORAGE_ROOT));
	private Map<String, String> mimeMap = new HashMap<String, String>();

	/**
	 * Process the message and on ingoing and extract the payload to storage, so we dont pass big blobs into the model
	 * Extracts them back into the message again on outgoing. Uses the default mimetypes mapping file
	 * @throws IOException
	 */
	public JsonStorageHandler() throws IOException {
		init("./src/main/resources/mime.types");
		
	}
	private void init(String mimeTypes) throws IOException {
		@SuppressWarnings("unchecked")
		List<String> lines = FileUtils.readLines(new File(mimeTypes));
		for (String line : lines) {
			String[] parts = line.split("=");
			mimeMap.put(parts[0], parts[1]);
		}
		
	}
	/**
	 * Process the message and on ingoing and extract the payload to storage, so we dont pass big blobs into the model
	 * Extracts them back into the message again on outgoing
	 * @param mimeTypes - location of the mimetypes mapping file
	 * @throws IOException
	 */
	public JsonStorageHandler(String mimeTypes) throws IOException {
		init(mimeTypes);
	}

	public Json handle(Json node) throws Exception {
		// avoid full signalk syntax
		if (node.has(vessels))
			return null;
		// deal with diff format
		if (node.has(CONTEXT)) {
			if (logger.isDebugEnabled())
				logger.debug("processing put  " + node);

			// go to context
			String ctx = node.at(CONTEXT).asString();
			// Json pathNode = temp.addNode(path);

			Json puts = node.at(PUT);
			if (puts == null)
				return node;
			if (puts.isArray()) {
				for (Json put : puts.asJsonList()) {
					parseUpdate( put, ctx);
				}
			} else {
				parseUpdate( puts.at(UPDATES), ctx);
			}

			if (logger.isDebugEnabled())
				logger.debug("JsonPutHandler processed put  ");
			return node;
		}
		return null;

	}

	protected void parseUpdate( Json update, String ctx) throws Exception {

		// DateTime timestamp = DateTime.parse(ts,fmt);

		// grab values and add
		Json array = update.at(values);
		for (Json e : array.asJsonList()) {
			String key = e.at(PATH).asString();
			// temp.put(ctx+"."+key, e.at(value).getValue());
			process( ctx + dot + key, e.at(value));

		}

	}

	protected void process( String ctx, Json j) throws IOException {
		// capture and store any embedded content
		if (j.isObject()) {
			if (j.has(PAYLOAD)) {

				String ext = getExtension(j);
				String filePath = ctx.replace('.', '/') + SignalKConstants.dot + ext;

				String payload = null;
				if (j.at(PAYLOAD).isString()) {
					// save it separately and add a storage url
					payload = j.at(PAYLOAD).asString();
				}
				if (j.at(PAYLOAD).isObject()) {
					// save it separately and add a storage url
					payload = j.at(PAYLOAD).toString().trim();
				}
				File save = new File(storageDir, filePath);
				logger.debug("Save to from "+save.getAbsolutePath());
				FileUtils.writeStringToFile(save, payload);
				j.set(STORAGE_URI, filePath);
				j.delAt(PAYLOAD);
			} else if (j.has(STORAGE_URI)) {
				String filePath = j.at(STORAGE_URI).asString();
				File save = new File(storageDir, filePath);
				logger.debug("Retrieve from "+save.getAbsolutePath());
				String payload = FileUtils.readFileToString(save);
				if (payload.startsWith("{") && payload.endsWith("}")) {
					j.set(PAYLOAD, Json.read(payload));
				} else {
					j.set(PAYLOAD, payload);
				}
				j.delAt(STORAGE_URI);
			} else {
				for (Json child : j.asJsonMap().values()) {
					process( ctx + dot + j.getParentKey(), child);
				}
			}
		}
	}

	private String getExtension(Json j) {
		String ext = null;
		if (j.has(MIME_TYPE)) {
			ext = mimeMap.get(j.at(MIME_TYPE).asString());
		}
		if (ext == null) {
			ext = "txt";
		}
		return ext;
	}

}
