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
package nz.co.fortytwo.signalk.model.impl;

import static nz.co.fortytwo.signalk.util.SignalKConstants.mmsi;
import static nz.co.fortytwo.signalk.util.SignalKConstants.name;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NavigableMap;
import java.util.UUID;

import mjson.Json;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.ConfigConstants;
import nz.co.fortytwo.signalk.util.JsonSerializer;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory to get signalKModel singleton
 *
 * @author robert
 *
 */
public class SignalKModelFactory {

    private static final String SIGNALK_MODEL_SAVE_FILE = "./conf/self.json";
    private static final String SIGNALK_CFG_SAVE_FILE = "./conf/signalk-config.json";
    private static Logger logger = LogManager.getLogger(SignalKModelFactory.class);
    private static SignalKModel signalKModel;
    private static String rootPath = "";

    static {
        rootPath = Util.getRootPath();
        if (signalKModel == null) {
            signalKModel = new SignalKModelImpl();
        }
        Util.setDefaults(signalKModel);
        try {
            SignalKModelFactory.loadConfig(signalKModel);
            AttrMapFactory.setAttrDefaults(AttrMapFactory.getInstance());
            AttrMapFactory.loadConfig(AttrMapFactory.getInstance());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Get the signalKModel singleton
     *
     * @return
     */
    public static synchronized SignalKModel getInstance() {
        // if(signalKModel==null){
        // signalKModel=new SignalKModelImpl();
        // }
        return signalKModel;
    }

    /**
     * Returns a different clean instance - used for incoming message creation
     * and testing! The model returned does not handle multiple values, if
     * required this must be done manually
     *
     * @return
     */
    public static synchronized SignalKModel getCleanInstance() {
        return new SignalKModelImpl(true);
    }

    /**
     * Returns the signalk instance cleaned out and configured with self=motu -
     * only needed for testing!
     *
     * @return
     */
    public static synchronized SignalKModel getMotuTestInstance() {
        signalKModel.getFullData().clear();
        Util.setDefaults(signalKModel);
        loadConfig(signalKModel, "motu");
        AttrMapFactory.setAttrDefaults(AttrMapFactory.getInstance());
        try {
            AttrMapFactory.loadConfig(AttrMapFactory.getInstance());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        signalKModel.getFullData().put(ConfigConstants.DEMO, false);
        return signalKModel;
    }

    /**
     * Returns a different clean instance - only needed for testing!
     *
     * @return
     */
    public static synchronized SignalKModel getWrappedInstance(NavigableMap<String, Object> root) {
        return new SignalKModelImpl(root);
    }

    public static void load(SignalKModel model) {
        File jsonFile = new File(rootPath + SIGNALK_MODEL_SAVE_FILE);
        logger.info("Checking for previous state: " + jsonFile.getAbsolutePath());
        if (jsonFile.exists()) {
            try {
                Json temp = Json.read(jsonFile.toURI().toURL());
                JsonSerializer ser = new JsonSerializer();
                model.putAll(ser.read(temp));
                logger.info("   Saved state loaded from " + rootPath + SIGNALK_MODEL_SAVE_FILE);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        } else {
            logger.info("   Saved state not found");
        }
    }

    public static void loadConfig(SignalKModel model) throws IOException {
        File jsonFile = new File(rootPath + SIGNALK_CFG_SAVE_FILE);
        logger.info("Checking for previous config: " + jsonFile.getAbsolutePath());
        if (jsonFile.exists()) {
            try {
                Json temp = Json.read(jsonFile.toURI().toURL());
                JsonSerializer ser = new JsonSerializer();
                model.putAll(ser.read(temp));
                String self = (String) model.get(ConfigConstants.UUID);

                Util.setSelf(self);
                model.getFullData().put(SignalKConstants.vessels_dot_self_dot + "uuid", self);
                //load other vessel specifics.
                if (model.get(ConfigConstants.MMSI) != null) {
                    model.getFullData().put(SignalKConstants.vessels_dot_self_dot + mmsi, model.get(ConfigConstants.MMSI));
                }
                if (model.get(ConfigConstants.NAME) != null) {
                    model.getFullData().put(SignalKConstants.vessels_dot_self_dot + name, model.get(ConfigConstants.NAME));
                }
                if (model.get(ConfigConstants.FLAG) != null) {
                    model.getFullData().put(SignalKConstants.vessels_dot_self_dot + "flag", model.get(ConfigConstants.FLAG));
                }
                if (model.get(ConfigConstants.PORT) != null) {
                    model.getFullData().put(SignalKConstants.vessels_dot_self_dot + "port", model.get(ConfigConstants.PORT));
                }
                if (model.get(ConfigConstants.DEPTH_TRANSDUCER_OFFSET) != null) {
                    model.getFullData().put(SignalKConstants.vessels_dot_self_dot + ConfigConstants.DEPTH_TRANSDUCER_OFFSET, model.get(ConfigConstants.DEPTH_TRANSDUCER_OFFSET));
                }
                if (model.get(ConfigConstants.ALARM_DEPTH) != null) {
                    // construct the meta object
                    StringBuilder metaKey = new StringBuilder(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_depth_belowTransducer);
                    metaKey.append(".meta");
                    model.getFullData().put(metaKey + ".displayName", "Depth Display");
                    model.getFullData().put(metaKey + ".shortName", "Depth");
                    model.getFullData().put(metaKey + ".warningMethod", "visual");
                    model.getFullData().put(metaKey + ".warningMessage", "");
                    model.getFullData().put(metaKey + ".alarmMethod", "visual");
                    model.getFullData().put(metaKey + ".alarmMessage", "");
                    metaKey.append(".zones");
                    JSONObject jo = new JSONObject();
                    JSONArray ja = new JSONArray();
                    jo.put("lower", "0");
                    jo.put("upper", model.get(ConfigConstants.ALARM_DEPTH));
                    jo.put("state", "alarm");
                    jo.put("message", "Danger");
                    ja.add(jo);
                    jo = new JSONObject();
                    jo.put("lower", model.get(ConfigConstants.ALARM_DEPTH));
                    double warnDepth = 1.2*(((Double)model.get(ConfigConstants.ALARM_DEPTH)).doubleValue());
                    jo.put("upper", warnDepth);
                    jo.put("state", "warn");
                    jo.put("message", "Shallow Water");
                    ja.add(jo);
                    model.getFullData().put(metaKey.toString(), ja.toJSONString());
                }
                logger.info("   Saved config loaded from " + rootPath + SIGNALK_CFG_SAVE_FILE);
             } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } else {
            logger.info("   Saved config not found, creating default");
            //write a new one for next time
            //create a uuid
            String self = SignalKConstants.URN_UUID + UUID.randomUUID().toString();
            model.getFullData().put(ConfigConstants.UUID, self);
            saveConfig(model);
            Util.setSelf(SignalKConstants.self);
            model.getFullData().put(SignalKConstants.vessels_dot_self_dot + "uuid", self);
        }
    }

    private static void loadConfig(SignalKModel model, String self) {
        File jsonFile = new File(rootPath + SIGNALK_CFG_SAVE_FILE);
        logger.info("Checking for previous config: " + jsonFile.getAbsolutePath());
        if (jsonFile.exists()) {
            try {
                Json temp = Json.read(jsonFile.toURI().toURL());
                JsonSerializer ser = new JsonSerializer();
                model.putAll(ser.read(temp));
                model.getFullData().put(ConfigConstants.UUID, self);
                Util.setSelf(self);
                logger.info("   Saved config loaded from " + rootPath + SIGNALK_CFG_SAVE_FILE);
                logger.info("   self set to: " + self);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } else {
            logger.info("   Saved config not found");
        }
    }

    /**
     * Save the current state of the signalk model
     *
     * @throws IOException
     */
    public static void save(SignalKModel model) throws IOException {
        if (model != null) {
            File jsonFile = new File(rootPath + SIGNALK_MODEL_SAVE_FILE);
            JsonSerializer ser = new JsonSerializer();
            ser.setPretty(3);
            Json modelJson = ser.writeJson(model);
            //remove config
            if (modelJson.has(SignalKConstants.CONFIG)) {
                modelJson = modelJson.delAt(SignalKConstants.CONFIG);
            }
            FileUtils.writeStringToFile(jsonFile, modelJson.toString(), StandardCharsets.UTF_8);
            logger.debug("   Saved model state to " + rootPath + SIGNALK_MODEL_SAVE_FILE);
        }
    }

    /**
     * Save the current state of the signalk config
     *
     * @throws IOException
     */
    public static void saveConfig(SignalKModel model) throws IOException {
        saveConfig(model, new File(rootPath + SIGNALK_CFG_SAVE_FILE));
    }

    public static void saveConfig(SignalKModel model, File jsonFile) throws IOException {
        if (model != null) {
            NavigableMap<String, Object> config = model.getSubMap(SignalKConstants.CONFIG);
            JsonSerializer ser = new JsonSerializer();
            ser.setPretty(3);
            StringBuilder buffer = new StringBuilder();
            if (config != null && config.size() > 0) {
                ser.write(config.entrySet().iterator(), '.', buffer);
            } else {
                buffer.append("{}");
            }
            FileUtils.writeStringToFile(jsonFile, buffer.toString(), StandardCharsets.UTF_8);
            logger.debug("   Saved model state to " + rootPath + SIGNALK_CFG_SAVE_FILE);
        }

    }
}
