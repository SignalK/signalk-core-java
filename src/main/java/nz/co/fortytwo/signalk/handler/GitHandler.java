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

import static nz.co.fortytwo.signalk.util.Constants.STATIC_DIR;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.co.fortytwo.signalk.util.JsonConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;

/*
 * Processes REST requests for Signal K installs from Github
 * 
 * 
 * @author robert
 */
public class GitHandler {

	private static final String SLASH = "/";
	private static Logger logger = Logger.getLogger(GitHandler.class);
	private File staticDir = new File(Util.getConfigProperty(STATIC_DIR));
	
	private static String github = "https://github.com/SignalK/";
	/**
	 * Process a signalk install
	 * 
	 * @param request
	 * @param response
	 * @param signalkModel
	 * @return
	 * @throws IOException
	 */
	public Object processInstall(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// use Restlet API to create the response
		String path = request.getPathInfo();
		// String path = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
		if (logger.isDebugEnabled())
			logger.debug("We are processing the path = " + path);

		// check valid request.
		if (path.length() < JsonConstants.SIGNALK_INSTALL.length() || !path.startsWith(JsonConstants.SIGNALK_INSTALL)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		path = path.substring(JsonConstants.SIGNALK_INSTALL.length());
		if (path.startsWith(SLASH))
			path = path.substring(1);
		if (logger.isDebugEnabled())
			logger.debug("We are processing the extension:" + path);
		// now we should have a valid github project name
		try {
			String fileName = install(path);
			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect("/logs.html");
			return fileName;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return e.getMessage();
		} 
		
	}
	/**
	 * Process a signalk install
	 * 
	 * @param request
	 * @param response
	 * @param signalkModel
	 * @return
	 * @throws IOException
	 */
	public Object processUpgrade(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// use Restlet API to create the response
		String path = request.getPathInfo();
		// String path = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
		if (logger.isDebugEnabled())
			logger.debug("We are processing the path = " + path);

		// check valid request.
		if (path.length() < JsonConstants.SIGNALK_UPGRADE.length() || !path.startsWith(JsonConstants.SIGNALK_UPGRADE)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		path = path.substring(JsonConstants.SIGNALK_UPGRADE.length());
		if (path.startsWith(SLASH))
			path = path.substring(1);
		if (logger.isDebugEnabled())
			logger.debug("We are processing the extension:" + path);
		// now we should have a valid github project name
		try {
			String fileName = install(path);
			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect("/logs.html");
			return fileName;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return e.getMessage();
		} 
		
	}

	protected String install(String path) throws Exception{
		//staticDir.mkdirs();
		Git result = null;
		try {
			File installLogDir = new File(staticDir,"logs");
			installLogDir.mkdirs();
			//make log name
			String logFile = "output.log";
			File output = new File(installLogDir,logFile);
			
			String gitPath = github+path+".git";
			logger.debug("Cloning from " + gitPath + " to " + staticDir);
			FileUtils.writeStringToFile(output, "Updating from " + gitPath + " to " + staticDir+"\n",false);
			File destDir = new File(staticDir,SLASH+path);
			destDir.mkdirs();
			result = Git.cloneRepository().setURI(gitPath).setDirectory(destDir).call();
			result.fetch().setRemote(gitPath);
			logger.debug("Cloned "+gitPath+" repository: " + result.getRepository().getDirectory());
			FileUtils.writeStringToFile(output, "Cloned "+gitPath+" repository: " + result.getRepository().getDirectory(), true);
			//now run npm install
			runNpmInstall(output, destDir);
			
			return logFile;
			
		
		} finally {
			result.close();
		}
		
	}
	private void runNpmInstall(File output, File destDir)  throws Exception{
		FileUtils.writeStringToFile(output, "\nBeginning npm install" ,true);
		ProcessBuilder pb = new ProcessBuilder("npm", "install");
		Map<String, String> env = System.getenv();
		if(env.containsKey("PATH")){
			pb.environment().put("PATH", env.get("PATH"));
		}
		if(env.containsKey("Path")){
			pb.environment().put("Path", env.get("Path"));
		}
		if(env.containsKey("path")){
			pb.environment().put("path", env.get("path"));
		}
		pb.directory(destDir);
		pb.redirectErrorStream(true);
		pb.redirectOutput(output);
		Process p = pb.start();
		
	}
	protected String upgrade(String path) throws Exception{
		//staticDir.mkdirs();
		Repository repository = null;
		try {
			String logFile = "output.log";
			File installLogDir = new File(staticDir,"logs");
			installLogDir.mkdirs();
			//make log name
			
			File output = new File(installLogDir,logFile);
			String gitPath = github+path+".git";
			logger.debug("Cloning from " + gitPath + " to " + staticDir);
			FileUtils.writeStringToFile(output, "Updating from " + gitPath + " to " + staticDir+"\n",false);
			File destDir = new File(staticDir,SLASH+path);
			destDir.mkdirs();
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			repository = builder.setGitDir(destDir)
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
			
			FetchResult result = new Git(repository).fetch().setRemote(gitPath).setCheckFetchedObjects(true).call();
			FileUtils.writeStringToFile(output, result.getMessages(),true);
			logger.debug("Updated "+gitPath+" repository: " + result.getMessages());
			
			//now run npm install
			runNpmInstall(output, destDir);
			
			return logFile;
			
		
		} finally {
			repository.close();
		}
		
	}
	
	
}
