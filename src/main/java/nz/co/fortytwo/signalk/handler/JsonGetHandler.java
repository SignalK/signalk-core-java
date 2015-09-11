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

import static nz.co.fortytwo.signalk.util.JsonConstants.CONTEXT;
import static nz.co.fortytwo.signalk.util.JsonConstants.FORMAT;
import static nz.co.fortytwo.signalk.util.JsonConstants.FORMAT_DELTA;
import static nz.co.fortytwo.signalk.util.JsonConstants.GET;
import static nz.co.fortytwo.signalk.util.JsonConstants.PATH;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import mjson.Json;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;

/**
 * Handles json messages with 'get' requests
 * 
 * @author robert
 * 
 */
public class JsonGetHandler {

	private static Logger logger = Logger.getLogger(JsonGetHandler.class);

	/**
	 * Processes the getNode against the signalKModel and returns a temporary signalkModel with the matching paths
	 * Supports * and ? wildcards.
	 * 
	 * @param signalkModel
	 * @param getNode
	 * @return
	 * @throws Exception
	 */
	public SignalKModel handle(SignalKModel signalkModel, Json getNode) throws Exception {

		if (logger.isDebugEnabled())
			logger.debug("Checking for get  " + getNode);

		// go to context
		String context = getNode.at(CONTEXT).asString();
		// TODO: is the context and path valid? A DOS attack is possible if we allow numerous crap/bad paths?

		Json paths = getNode.at(GET);
		SignalKModel tree = SignalKModelFactory.getCleanInstance();
		if (paths != null) {
			if (paths.isArray()) {

				for (Json path : paths.asJsonList()) {
					parseGet(signalkModel, context, path, tree);
				}
			}
			if (logger.isDebugEnabled())
				logger.debug("Processed get  " + getNode);
		}

		return tree;

	}

	/**
	 * Looks for a format key, and returns the first one found
	 * Defaults to FORMAT_DELTA
	 * 
	 * @param node
	 * @return
	 */
	public String getFormat(Json node) {
		Json paths = node.at(GET);
		if (paths != null) {
			if (paths.isArray()) {
				for (Json path : paths.asJsonList()) {
					if (path.has(FORMAT)) {
						return path.at(FORMAT).asString();
					}
				}
			}else{
				if (paths.has(FORMAT)) {
					return paths.at(FORMAT).asString();
				}
			}

		}
		return FORMAT_DELTA;
	}

	/**
	 * Copies a context and path from the signalkModel to the temp model.
	 * Supports * and ? wildcards.
	 * 
	 * @param signalkModel
	 * @param context
	 * @param path
	 * @param tree
	 * @throws Exception
	 */
	public void parseGet(SignalKModel signalkModel, String context, Json path, SignalKModel tree) throws Exception {
		// get values
		String regexKey = context + JsonConstants.DOT + path.at(PATH).asString();
		if (logger.isDebugEnabled())
			logger.debug("Parsing get  " + regexKey);

		List<String> rslt = getMatchingPaths(signalkModel, regexKey);
		// add to tree
		for (String p : rslt) {
			//TODO: this may be calling recursive paths, need to do each path only once
			if (logger.isTraceEnabled())
				logger.trace("Parsing key  " + p);
			Util.populateTree(signalkModel, tree, p);
		}

	}

	/**
	 * Returns a get of paths that this implementation is currently providing.
	 * The get is filtered by the key if it is not null or empty in which case a full get is returned,
	 * supports * and ? wildcards.
	 * 
	 * @param regex
	 * @return
	 */
	public List<String> getMatchingPaths(SignalKModel signalkModel, String regex) {
		if (StringUtils.isBlank(regex)) {
			return ImmutableList.copyOf(signalkModel.getKeys());
		}
		regex = Util.sanitizePath(regex);
		Pattern pattern = Util.regexPath(regex);
		List<String> paths = new ArrayList<String>();
		for (String p : signalkModel.getKeys()) {
			if (pattern.matcher(p).matches()) {
				if (logger.isTraceEnabled())
					logger.trace("Adding path:" + p);
				paths.add(p);
			}
		}
		return paths;
	}

}
