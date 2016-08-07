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

import static nz.co.fortytwo.signalk.util.ConfigConstants.STATIC_DIR;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
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
	private static Logger logger = LogManager.getLogger(GitHandler.class);
	private File staticDir = null;
	
	private static String github = "https://github.com/SignalK/";
	
	
	public GitHandler() {
		super();
		staticDir = new File(Util.getConfigProperty(STATIC_DIR));
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
	public Object processInstall(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// use Restlet API to create the response
		String path = request.getPathInfo();
		// String path = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
		if (logger.isDebugEnabled())
			logger.debug("We are processing the path = " + path);

		// check valid request.
		if (path.length() < SignalKConstants.SIGNALK_INSTALL.length() || !path.startsWith(SignalKConstants.SIGNALK_INSTALL)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		path = path.substring(SignalKConstants.SIGNALK_INSTALL.length());
		if (path.startsWith(SLASH))
			path = path.substring(1);
		if (logger.isDebugEnabled())
			logger.debug("We are processing the extension:" + path);
		// now we should have a valid github project name
		try {
			String fileName = install(path);
			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect("/config/logs.html");
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
		if (path.length() < SignalKConstants.SIGNALK_UPGRADE.length() || !path.startsWith(SignalKConstants.SIGNALK_UPGRADE)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		path = path.substring(SignalKConstants.SIGNALK_UPGRADE.length());
		if (path.startsWith(SLASH))
			path = path.substring(1);
		if (logger.isDebugEnabled())
			logger.debug("We are processing the extension:" + path);
		// now we should have a valid github project name
		try {
			String fileName = upgrade(path);
			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect("/config/logs.html");
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
			File destDir = new File(staticDir,SLASH+path);
			destDir.mkdirs();
			String gitPath = github+path+".git";
			logger.debug("Cloning from " + gitPath + " to " + destDir.getAbsolutePath());
			FileUtils.writeStringToFile(output, "Updating from " + gitPath + " to " + destDir.getAbsolutePath()+"\n",false);
			try{
				result = Git.cloneRepository().setURI(gitPath).setDirectory(destDir).call();
				result.fetch().setRemote(gitPath);
				logger.debug("Cloned "+gitPath+" repository: " + result.getRepository().getDirectory());
				FileUtils.writeStringToFile(output, "DONE: Cloned "+gitPath+" repository: " + result.getRepository().getDirectory(), true);
			}catch(Exception e){
				FileUtils.writeStringToFile(output, e.getMessage(),true);
				FileUtils.writeStringToFile(output, e.getStackTrace().toString(),true);
				logger.debug("Error updating "+gitPath+" repository: " + e.getMessage(),e);
			}
			/*try{
				//now run npm install
				runNpmInstall(output, destDir);
			}catch(Exception e){
				FileUtils.writeStringToFile(output, e.getMessage(),true);
				FileUtils.writeStringToFile(output, e.getStackTrace().toString(),true);
				logger.debug("Error updating "+gitPath+" repository: " + e.getMessage(),e);
			}*/
			return logFile;
			
		
		} finally {
			if(result!=null)result.close();
		}
		
	}
	private void runNpmInstall(final File output, File destDir)  throws Exception{
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
		final Process p = pb.start();
		Thread t = new Thread(){

			@Override
			public void run() {
				try {
					p.waitFor();
					FileUtils.writeStringToFile(output, "\nDONE: Npm ended sucessfully" ,true);
				} catch (Exception e) {
					try {
						logger.error(e);
						FileUtils.writeStringToFile(output, "\nNpm ended badly:"+e.getMessage() ,true);
						FileUtils.writeStringToFile(output, "\n"+e.getStackTrace() ,true);
					} catch (IOException e1) {
						logger.error(e1);
					}
				}
			}
			
		};
		t.start();
	}
	protected String upgrade(String path) throws Exception{
		//staticDir.mkdirs();
		Repository repository = null;
		try {
			String logFile = "output.log";
			File installLogDir = new File(staticDir,"logs");
			installLogDir.mkdirs();
			//
			File destDir = new File(staticDir,SLASH+path);
			destDir.mkdirs();
			File output = new File(installLogDir,logFile);
			String gitPath = github+path+".git";
			logger.debug("Updating from " + gitPath + " to " + destDir.getAbsolutePath());
			FileUtils.writeStringToFile(output, "Updating from " + gitPath + " to " + destDir.getAbsolutePath()+"\n",false);
			Git git = null;
			try{
				FileRepositoryBuilder builder = new FileRepositoryBuilder();
				repository = builder.setGitDir(new File(destDir,"/.git"))
					.readEnvironment() // scan environment GIT_* variables
					.findGitDir() // scan up the file system tree
					.build();
				git = new Git(repository);
				PullResult result = git.pull().call();
				FileUtils.writeStringToFile(output, result.getMergeResult().toString(),true);
				logger.debug("DONE: Updated "+gitPath+" repository: " + result.getMergeResult().toString());
				
				//now run npm install
				//runNpmInstall(output, destDir);
			}catch(Exception e){
				FileUtils.writeStringToFile(output, e.getMessage(),true);
				FileUtils.writeStringToFile(output, e.getStackTrace().toString(),true);
				logger.debug("Error updating "+gitPath+" repository: " + e.getMessage(),e);
			}finally{
				if(git!=null) git.close();
				if(repository!=null)repository.close();
			}
			return logFile;
		
		} finally {
			if(repository!=null)repository.close();
		}
		
	}
	
	
}
