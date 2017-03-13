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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package nz.co.fortytwo.signalk.handler;

import nz.co.fortytwo.signalk.util.SignalKConstants;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_belowTransducer;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_belowKeel;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_surfaceToTransducer;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_transducerToKeel;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_belowSurface;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_meta_displayUnit;
import static nz.co.fortytwo.signalk.util.SignalKConstants.self;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Calculates the depth below keel and depth from surface from the depth below
 * transducer.
 *
 * @author Ron
 *
 */
public class DepthHandler {

    private static Logger logger = LogManager.getLogger(DepthHandler.class);

    /**
     * Updates the depthBelowSurface and depthBelowKeel if the
     * surfaceToTranducer and transducerToKeel parameters are present provided
     * signalKModel.
     *
     * @param signalkModel
     */
    public void handle(SignalKModel signalkModel) {
        double depthBelowTransducer = 0;
        double depthBelowSurface = 0;
        double surfaceToTransducer = 0;
        double transducerToKeel = 0;
        double depthBelowKeel = 0;
        StringBuffer sb = new StringBuffer();
        try {
            depthBelowTransducer = (double) signalkModel.getValue(vessels_dot_self_dot + env_depth_belowTransducer);
            if (logger.isDebugEnabled()) {
                sb.append("\n\tdepthBelowTransducer: " + depthBelowTransducer);
            } else {
                logger.info("depthBelowTransducer: " + depthBelowTransducer);
            }
        } catch (NullPointerException e) {
            // No depth data available
            return;
        }
        try {
            surfaceToTransducer = (double) signalkModel.get(vessels_dot_self_dot + env_depth_surfaceToTransducer);
            depthBelowSurface = surfaceToTransducer + depthBelowTransducer;
            if (logger.isDebugEnabled()) {
                sb.append("\n\tsurfaceToTransducer: " + surfaceToTransducer);
                sb.append("\n\tdepthBelowSurface: " + depthBelowSurface);
            }
            signalkModel.put(vessels_dot_self_dot + env_depth_belowSurface, depthBelowSurface, self, Util.getIsoTimeString());
        } catch (NullPointerException e) {
            //no suface to transducer data available
        }
        try {
            transducerToKeel = (double) signalkModel.get(vessels_dot_self_dot + env_depth_transducerToKeel);
            depthBelowKeel = depthBelowTransducer - transducerToKeel;
            if (logger.isDebugEnabled()) {
                sb.append("\n\ttransducerToKeel: " + transducerToKeel);
                sb.append("\n\tdepthBelowKeel: " + depthBelowKeel);
            }
            signalkModel.put(vessels_dot_self_dot + env_depth_belowKeel, depthBelowKeel, self, Util.getIsoTimeString());
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug(sb.toString());
    }
}
