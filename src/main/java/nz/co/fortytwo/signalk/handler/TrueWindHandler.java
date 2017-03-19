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

import static nz.co.fortytwo.signalk.util.SignalKConstants.*;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_directionTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedOverGround;
import static nz.co.fortytwo.signalk.util.SignalKConstants.self;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Calculates the true wind from apparent wind and vessel speed/heading
 *
 * @author robert
 *
 */
public class TrueWindHandler {

    private static Logger logger = LogManager.getLogger(TrueWindHandler.class);

    /**
     * Updates the true wind direction from the apparent wind direction in the
     * provided signalKModel.
     *
     * @param signalkModel
     */
    public void handle(SignalKModel signalkModel) {
        try {

            Double vesselSpeed = (Double) signalkModel.getValue(vessels_dot_self_dot + nav_speedOverGround);
            Double apparentDirection = (Double) signalkModel.getValue(vessels_dot_self_dot + env_wind_angleApparent);
            Double apparentWindSpeed = (Double) signalkModel.getValue(vessels_dot_self_dot + env_wind_speedApparent);
            if (apparentWindSpeed != null && apparentDirection != null && vesselSpeed != null) {
                // now calc and add to body
                // 0-360 from bow clockwise

                double[] windCalc = calcTrueWindDirection(apparentWindSpeed, apparentDirection, vesselSpeed);
                logger.info(String.format("App speed, dir, vesselSpeed: %3.1f %4f %2.1f",Util.msToKnts(apparentWindSpeed), apparentDirection*360./TWO_PI, Util.msToKnts(vesselSpeed)));
                logger.info(String.format("windCalc: %3.1f %4f",Util.msToKnts(windCalc[0]), windCalc[1]*360./TWO_PI));
                if (windCalc != null) {

                    if (!Double.isNaN(windCalc[1])) {
                        //map.put(Constants.WIND_DIR_TRUE, round(trueDirection, 2));
                        signalkModel.put(vessels_dot_self_dot + env_wind_directionTrue, windCalc[1], self, Util.getIsoTimeString());
                    }
                    if (!Double.isNaN(windCalc[0])) {
                        //map.put(Constants.WIND_SPEED_TRUE, round(trueWindSpeed, 2));
                        signalkModel.put(vessels_dot_self_dot + env_wind_speedTrue, windCalc[0], self, Util.getIsoTimeString());
                    }
                }

            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * Calculates the true wind direction from apparent wind on vessel Result is
     * relative to bow
     *
     * @param apparentWnd in m/s
     * @param apparentDir 0 to 360 deg to the bow in radians
     * @param vesselSpd in m/s
     *
     * @return array of [true speed in m/s, trueDirection 0 to 360 deg to the
     * bow, in radians ]
     */
    public double[] calcTrueWindDirection(double apparentWnd, double apparentDir, double vesselSpd) {
        double trueDirection = 0.0;
        double trueWindSpeed = 0.0;
        double windCalc[] = {trueWindSpeed, trueDirection};
        /*
		 * Y = 90 - D
		 * a = AW * ( cos Y )
		 * bb = AW * ( sin Y )
		 * b = bb - BS
		 * True-Wind Speed = (( a * a ) + ( b * b )) 1/2
		 * True-Wind Angle = 90-arctangent ( b / a )
         */

        apparentDir = apparentDir % TWO_PI;
        boolean port = apparentDir > Math.PI;
        if (port) {
            apparentDir = TWO_PI - apparentDir;
        }

        /*
		 * // Calculate true heading diff and true wind speed - JAVASCRIPT
		 * tan_alpha = (Math.sin(angle) / (aspeed - Math.cos(angle)));
		 * alpha = Math.atan(tan_alpha);
		 * 
		 * tdiff = rad2deg(angle + alpha);
		 * tspeed = Math.sin(angle)/Math.sin(alpha);
         */
        double aspeed = Math.max(apparentWnd, vesselSpd);
        if (apparentWnd > 0 && vesselSpd > 0.0) {
            aspeed = apparentWnd / vesselSpd;
        }
        double angle = apparentDir;
        double tan_alpha = (Math.sin(angle) / (aspeed - Math.cos(angle)));
        double alpha = Math.atan(tan_alpha);
        double tAngle = alpha + angle;
        if (Double.valueOf(tAngle).isNaN() || Double.isInfinite(tAngle)) {
            return windCalc;
        }
        if (port) {
            trueDirection = (TWO_PI - tAngle);
        } else {
            trueDirection = tAngle;
        }
        windCalc[1] = trueDirection % TWO_PI;
//        windCalc[1] = tAngle % TWO_PI;

        if (apparentWnd < 0.1 || vesselSpd < 0.1) {
            trueWindSpeed = Math.max(apparentWnd, vesselSpd);
            windCalc[0] = trueWindSpeed;
            return windCalc;
        }
        double tspeed = Math.sin(angle) / Math.sin(alpha);
        if (Double.valueOf(tspeed).isNaN() || Double.isInfinite(tspeed)) {
            return windCalc;
        }
        trueWindSpeed = Math.abs(tspeed * vesselSpd);
        windCalc[0] = trueWindSpeed;
        return windCalc;
    }
}
