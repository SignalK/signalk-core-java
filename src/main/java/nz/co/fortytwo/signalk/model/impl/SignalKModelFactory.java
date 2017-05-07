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

import static nz.co.fortytwo.signalk.util.SignalKConstants.*;
import static nz.co.fortytwo.signalk.util.SignalKConstants.mmsi;
import static nz.co.fortytwo.signalk.util.SignalKConstants.name;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NavigableMap;
import java.util.UUID;
import java.util.logging.Level;

import mjson.Json;
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
    private static final String SIGNALK_RESOURCES_SAVE_FILE = "./conf/resources.json";

    private static Logger logger = LogManager.getLogger(SignalKModelFactory.class);
    private static SignalKModel signalKModel;
    private static String rootPath = "";

    static {
        // lolgger is not initialized until after these statements are
        // executedSystem.out.println("SignalKModelFactory static block");
        rootPath = Util.getRootPath();
        if (signalKModel == null) {
            signalKModel = new SignalKModelImpl();
        }
        System.out.println("SignalKModelFactory static block Util.setDefaults()");
        Util.setDefaults(signalKModel);
        try {
            System.out.println("SignalKModelFactory static block SignalKModelFactory.loadConfig(signalKModel)");
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
     * @param root
     * @return
     */
    public static synchronized SignalKModel getWrappedInstance(NavigableMap<String, Object> root) {
        return new SignalKModelImpl(root);
    }

    /**
     *
     * @param model
     */
    public static void load(SignalKModel model) {
        File jsonFile = new File(rootPath + SIGNALK_MODEL_SAVE_FILE);
        File resourceFile = new File(rootPath + SIGNALK_RESOURCES_SAVE_FILE);
        logger.info("load(signalkModel) Checking for previous state: " + jsonFile.getAbsolutePath());
        if (jsonFile.exists()) {
            try {
                Json temp = Json.read(jsonFile.toURI().toURL());
                JsonSerializer ser = new JsonSerializer();
                model.putAll(ser.read(temp));
                removeOtherVessels(model);

                logger.info("   Saved state loaded from " + rootPath + SIGNALK_MODEL_SAVE_FILE);
                // loadConfig(model);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        } else {
            logger.info("   Saved state not found");
        }
        // now load resources
        if (resourceFile.exists()) {
            try {
                Json temp = Json.read(resourceFile.toURI().toURL());
                JsonSerializer ser = new JsonSerializer();
                model.putAll(ser.read(temp));
                // removeOtherVessels(model);

                logger.info("   Saved resources loaded from " + rootPath + SIGNALK_RESOURCES_SAVE_FILE);
                // loadConfig(model);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        } else {
            logger.info("   Saved resources not found");
        }
        insertMetaToModel(model);
    }

    public static void removeOtherVessels(SignalKModel model) throws IOException {
        String self = (String) model.get(ConfigConstants.UUID);
        Util.setSelf(self);
        for (String key : model.getData().keySet()) {
            if (key.startsWith(vessels)) {
                if (key.startsWith(vessels + dot + self) || key.startsWith(vessels + dot + "self")) {
                    continue;
                }
                model.getFullData().remove(key);
            }
        }
        save(model);

    }

    /**
     * Extracts the values for selected ConfigConstants keys and inserts them
     * into the SignalK model using the appropriate SignalKConstants keys.
     *
     * @param model the SignalK model
     */
    public static void insertMetaToModel(SignalKModel model) {

        String self = (String) model.get(ConfigConstants.UUID);

        Util.setSelf(self);
        model.getFullData().put(SignalKConstants.vessels_dot_self_dot + "uuid", self);
        // load other vessel specifics.
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

        if (model.get(ConfigConstants.DEPTH_ALARM_METHOD) != null) {
            model.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_depth_alarmMethod,
                model.get(ConfigConstants.DEPTH_ALARM_METHOD));
        }

        if (model.get(ConfigConstants.DEPTH_WARN_METHOD) != null) {
            model.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_depth_warnMethod,
                model.get(ConfigConstants.DEPTH_WARN_METHOD));
        }

        if (model.get(ConfigConstants.ENGINE_TEMP_ALARM_METHOD) != null) {
            model.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.propulsion_engine_coolantTemperature_meta_alarmMethod,
                model.get(ConfigConstants.ENGINE_TEMP_ALARM_METHOD));
        }

        if (model.get(ConfigConstants.ENGINE_TEMP_WARN_METHOD) != null) {
            model.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.propulsion_engine_coolantTemperature_meta_warnMethod,
                model.get(ConfigConstants.ENGINE_TEMP_WARN_METHOD));
        }

        if (model.get(ConfigConstants.ENGINE_TEMP_USER_UNIT) != null) {
            String engineTempUserUnit = (String) model.get(ConfigConstants.ENGINE_TEMP_USER_UNIT);
            model.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.propulsion_engine_coolantTemperature_meta_unit,
                model.get(ConfigConstants.ENGINE_TEMP_USER_UNIT));
        

        Double upper = new Double(0);
        Double lower = new Double(0);
            if (model.get(ConfigConstants.ENGINE_TEMP_ALARM_ZONES) != null) {
                Json zones = Json.read(model.get(ConfigConstants.ENGINE_TEMP_ALARM_ZONES).toString());
                Json jArrayElem;
                // Set the zones for alarm (0) and warn (1)
                for (int i = 0; i < zones.asList().size(); i++) {
                    jArrayElem = zones.at(i);
                    
                    // switch from user units to SignalK units
                    switch (engineTempUserUnit) {
                        case SignalKConstants.CENT:
                            break;
                        case SignalKConstants.FAHR:
                            upper = Util.fahrToC(jArrayElem.at("upper").asDouble());
                            lower = Util.fahrToC(jArrayElem.at("lower").asDouble());
                            break;
                    }
                    jArrayElem.set("upper", upper);
                    jArrayElem.set("lower", lower);
                    zones.set(i, jArrayElem);
                }

                // Set the normal zone using the settings for the warning zone
                Json normalZone = Json.object("lower", 0., "upper", zones.at(1).at("lower"), "state", "normal", "message", "");
                Json newZones = Json.array(zones.at(0), zones.at(1), normalZone);

                // put the alarm zones in the proper position in the model
                model.getFullData().put(SignalKConstants.vessels_dot_self_dot
                    + SignalKConstants.propulsion_engine_coolantTemperature
                    + SignalKConstants.dot
                    + SignalKConstants.meta
                    + SignalKConstants.dot
                    + SignalKConstants.zones,
                    newZones);
            }
        }

        // insert surfaceToTranducer, transducerToKeel and depth zones  into model
        // after converting from user units to SignalK units.

        if (model.get(ConfigConstants.DEPTH_USER_UNIT) != null) {
            String depthUserUnit = (String) model.get(ConfigConstants.DEPTH_USER_UNIT);
            model.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_depth_meta_userUnit,
                model.get(ConfigConstants.DEPTH_USER_UNIT));

            if (model.get(ConfigConstants.SURFACE_TO_TRANSDUCER) != null) {
                double offset = (double) model.get(ConfigConstants.SURFACE_TO_TRANSDUCER);
                switch (depthUserUnit) {
                    case SignalKConstants.M:
                        break;
                    case SignalKConstants.F:
                        offset /= SignalKConstants.MTR_TO_FATHOM;
                        break;
                    case SignalKConstants.FT:
                        offset /= SignalKConstants.MTR_TO_FEET;
                        break;
                }
                model.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_depth_surfaceToTransducer, offset,
                    self);
            }

            if (model.get(ConfigConstants.TRANSDUCER_TO_KEEL) != null) {
                double offset = (double) model.get(ConfigConstants.TRANSDUCER_TO_KEEL);
                switch (depthUserUnit) {
                    case SignalKConstants.M:
                        break;
                    case SignalKConstants.F:
                        offset /= SignalKConstants.MTR_TO_FATHOM;
                        break;
                    case SignalKConstants.FT:
                        offset /= SignalKConstants.MTR_TO_FEET;
                        break;
                }
                model.put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.env_depth_transducerToKeel, offset,
                    self);
            }

            Double upper = new Double(0);
            Double lower = new Double(0);
            if (model.get(ConfigConstants.DEPTH_ALARM_ZONES) != null) {
                Json zones = Json.read(model.get(ConfigConstants.DEPTH_ALARM_ZONES).toString());
                Json jArrayElem;
                // Set the zones for alarm (0) and warn (1)
                for (int i = 0; i < zones.asList().size(); i++) {
                    jArrayElem = zones.at(i);
                    switch (depthUserUnit) {
                        case SignalKConstants.M:
                            break;
                        case SignalKConstants.F:
                            upper = jArrayElem.at("upper").asDouble() / SignalKConstants.MTR_TO_FATHOM;
                            lower = jArrayElem.at("lower").asDouble() / SignalKConstants.MTR_TO_FATHOM;
                            break;
                        case SignalKConstants.FT:
                            upper = jArrayElem.at("upper").asDouble() / SignalKConstants.MTR_TO_FEET;
                            lower = jArrayElem.at("lower").asDouble() / SignalKConstants.MTR_TO_FEET;
                            break;
                    }
                    jArrayElem.set("upper", upper);
                    jArrayElem.set("lower", lower);
                    zones.set(i, jArrayElem);
                }

                // Set the normal zone using the settings for the warning zone
                Json normalZone = Json.object("lower", zones.at(1).at("upper"), "upper", new Double(9999), "state", "normal", "message", "");
                Json newZones = Json.array(zones.at(0), zones.at(1), normalZone);

                // put the alarm zones in the proper position in the model
                model.getFullData().put(SignalKConstants.vessels_dot_self_dot
                    + SignalKConstants.env_depth_belowSurface
                    + SignalKConstants.dot
                    + SignalKConstants.meta
                    + SignalKConstants.dot
                    + SignalKConstants.zones,
                    newZones);
            }
        }

        if (model.get(ConfigConstants.SOG_DISPLAY_UNIT) != null) {
            model.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_sogDisplayUnit,
                model.get(ConfigConstants.SOG_DISPLAY_UNIT));
        }

        if (model.get(ConfigConstants.STW_DISPLAY_UNIT) != null) {
            model.getFullData().put(SignalKConstants.vessels_dot_self_dot + SignalKConstants.nav_stwDisplayUnit,
                model.get(ConfigConstants.STW_DISPLAY_UNIT));
        }
        logger.info("   Inserted config values into SignalK model");

    }

    /**
     * Loads the previous signalk-config.json file if it exists. If it does not
     * exist, it generates a new random uuid for self and inserts this into the
     * model.
     *
     * @param model the SignalK model.
     * @throws IOException
     */
    public static void loadConfig(SignalKModel model) throws IOException {
        File jsonFile = new File(rootPath + SIGNALK_CFG_SAVE_FILE);
        logger.info("loadConfig(signalkModel) Checking for previous config: " + jsonFile.getAbsolutePath());
        if (jsonFile.exists()) {
            try {
                Json temp = Json.read(jsonFile.toURI().toURL());
                JsonSerializer ser = new JsonSerializer();
                model.putAll(ser.read(temp));
                String self = (String) model.getFullData().get(ConfigConstants.UUID);
                Util.setSelf(self);
                logger.info("   Saved config loaded from " + rootPath + SIGNALK_CFG_SAVE_FILE);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } else {
            logger.info("   Saved config not found, creating default");
            // write a new one for next time
            // Get the set of reasonabl defaults - they go in with the
            // ConfigConstants keys
            Util.setDefaults(model);
            // create a uuid
            String self = SignalKConstants.URN_UUID + UUID.randomUUID().toString();
            // put the new random uuid into the model;
            model.getFullData().put(ConfigConstants.UUID, self);
            saveConfig(model);
            Util.setSelf(self);
            model.getFullData().put(SignalKConstants.vessels_dot_self_dot + "uuid", self);
            // insert the configuration parameters into the model with the
            // SignalKConstants keys
            // save(model);
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
     * @param model
     * @throws IOException
     */
    public static void save(SignalKModel model) throws IOException {
        if (model != null) {
            File jsonFile = new File(rootPath + SIGNALK_MODEL_SAVE_FILE);
            JsonSerializer ser = new JsonSerializer();
            ser.setPretty(3);
            Json modelJson = ser.writeJson(model);
            // remove config
            if (modelJson.has(SignalKConstants.CONFIG)) {
                modelJson = modelJson.delAt(SignalKConstants.CONFIG);
            }
            FileUtils.writeStringToFile(jsonFile, modelJson.toString(), StandardCharsets.UTF_8);
            logger.info("   Saved model state to " + rootPath + SIGNALK_MODEL_SAVE_FILE);

            // now save resources separately
            if (modelJson.has(vessels)) {
                modelJson = modelJson.delAt(vessels);
            }
            if (modelJson.has(aircraft)) {
                modelJson = modelJson.delAt(aircraft);
            }
            if (modelJson.has(sar)) {
                modelJson = modelJson.delAt(sar);
            }
            // keep atons
            File resourceFile = new File(rootPath + SIGNALK_RESOURCES_SAVE_FILE);
            FileUtils.writeStringToFile(resourceFile, modelJson.toString(), StandardCharsets.UTF_8);
            logger.info("   Saved model resources to " + rootPath + SIGNALK_RESOURCES_SAVE_FILE);
        }
    }

    /**
     * Save the current state of the signalk config
     *
     * @param model
     * @throws IOException
     */
    public static void saveConfig(SignalKModel model) throws IOException {
        saveConfig(model, new File(rootPath + SIGNALK_CFG_SAVE_FILE));
    }

    /**
     *
     * @param model
     * @param jsonFile
     * @throws IOException
     */
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

    public static String writePretty(SignalKModel model) {

        StringBuilder buffer = null;
        if (model != null) {
            JsonSerializer ser = new JsonSerializer();
            ser.setPretty(3);
            buffer = new StringBuilder();
            try {
                buffer.append(ser.write(model));
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(SignalKModelFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return buffer.toString();
    }

}
