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

import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_magneticVariation;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.util.TSAGeoMag;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 * When provided with a LAT and LON, it calculates declination.
 * 
 * @author robert
 * 
 */
public class DeclinationHandler {

	private static Logger logger = LogManager.getLogger(DeclinationHandler.class);
	
	private TSAGeoMag geoMag = new TSAGeoMag();

	public void handle(SignalKModel signalkModel ) {
		logger.debug("Declination  calculation fired " );
		Double lat = (Double) signalkModel.get(vessels_dot_self_dot+nav_position_latitude);
		Double lon = (Double) signalkModel.get(vessels_dot_self_dot+nav_position_longitude);
		
		if (lat!=null && lon!=null) {
			if(logger.isDebugEnabled())logger.debug("Declination  for "+lat+", "+lon );
			
			double declination = Math.toRadians(geoMag.getDeclination(lat, lon, DateTime.now().getYear(), 0.0d));
			
			declination = Util.round(declination, 6);
			if(logger.isDebugEnabled()){
				logger.debug("Declination (rad)= " + declination);
				logger.debug("Declination (deg)= " + Math.toDegrees(declination));
			}
			signalkModel.putValue(vessels_dot_self_dot+nav_magneticVariation, declination);	
		}
		
	}
	
	/**
	 * Calculates the declination for a given lat, lon and year.
	 * @param lat
	 * @param lon
	 * @param year
	 * @return
	 */
	public double handle(double lat, double lon, double year) {
		logger.debug("Declination  calculation fired " );
		
		double declination = geoMag.getDeclination(lat, lon, year, 0.0d);
		
		declination = Util.round(declination, 1);
		if(logger.isDebugEnabled())logger.debug("Declination = " + declination);
		return declination;	
	
		
	}

}
