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

import static nz.co.fortytwo.signalk.util.SignalKConstants.dot;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_belowTransducer;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_angleApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundMagnetic;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedOverGround;
import static nz.co.fortytwo.signalk.util.SignalKConstants.source;
import static nz.co.fortytwo.signalk.util.SignalKConstants.timestamp;
import static nz.co.fortytwo.signalk.util.SignalKConstants.value;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.DepthSentence;
import net.sf.marineapi.nmea.sentence.HeadingSentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.PositionSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.VHWSentence;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.ConfigConstants;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;



/**
 * Processes NMEA sentences in the body of a message, firing events to interested listeners
 * Converts the NMEA messages to signalk
 * 
 * @author robert
 * 
 */
public class NMEAHandler{

	private static Logger logger = Logger.getLogger(NMEAHandler.class);
	private static final String DISPATCH_ALL = "DISPATCH_ALL";

	// map of sentence listeners
	private ConcurrentMap<String, List<SentenceListener>> listeners = new ConcurrentHashMap<String, List<SentenceListener>>();
	private boolean rmcClock=false;

	public NMEAHandler() {
		super();
		// register BVE
		//SentenceFactory.getInstance().registerParser("BVE", net.sf.marineapi.nmea.parser.BVEParser.class);
		//SentenceFactory.getInstance().registerParser("XDR", net.sf.marineapi.nmea.parser.CruzproXDRParser.class);
		try {
			if("rmc".equals(Util.getConfigProperty(ConfigConstants.CLOCK_source))){
				rmcClock=true;
			}
		} catch (Exception e) {
			logger.error(e);
		} 
		setNmeaListeners();
	}



	/**
	 * Convert an NMEA string to a signalk json object
	 * @param bodyStr
	 * @return
	 */
	public SignalKModel handle(String bodyStr) {
		return handle(bodyStr, null);
	}
	/**
	 * Convert an NMEA string to a signalk json object
	 * @param bodyStr
	 * @return
	 */
	public SignalKModel handle(String bodyStr, String source) {
		SignalKModel model = null;
		if (StringUtils.isNotBlank(bodyStr)&& bodyStr.startsWith("$")) {
			try {
				if(logger.isDebugEnabled())logger.debug("Processing NMEA:[" + bodyStr+"]");
				Sentence sentence = SentenceFactory.getInstance().createParser(bodyStr);
				model = SignalKModelFactory.getCleanInstance();
				fireSentenceEvent(model, sentence, source);
				return model;
			}catch (IllegalArgumentException e) {
				logger.debug(e.getMessage(), e);
				logger.info(e.getMessage() + ":" + bodyStr);
				logger.info("   in hexidecimal : " + Hex.encodeHexString(bodyStr.getBytes()));
			}catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * Adds a {@link SentenceListener} that wants to receive all sentences read
	 * by the reader.
	 * 
	 * @param listener
	 *            {@link SentenceListener} to be registered.
	 * @see net.sf.marineapi.nmea.event.SentenceListener
	 */
	public void addSentenceListener(SentenceListener listener) {
		registerListener(DISPATCH_ALL, listener);
	}

	/**
	 * Adds a {@link SentenceListener} that is interested in receiving only
	 * sentences of certain type.
	 * 
	 * @param sl
	 *            SentenceListener to add
	 * @param type
	 *            Sentence type for which the listener is registered.
	 * @see net.sf.marineapi.nmea.event.SentenceListener
	 */
	public void addSentenceListener(SentenceListener sl, SentenceId type) {
		registerListener(type.toString(), sl);
	}

	/**
	 * Adds a {@link SentenceListener} that is interested in receiving only
	 * sentences of certain type.
	 * 
	 * @param sl
	 *            SentenceListener to add
	 * @param type
	 *            Sentence type for which the listener is registered.
	 * @see net.sf.marineapi.nmea.event.SentenceListener
	 */
	public void addSentenceListener(SentenceListener sl, String type) {
		registerListener(type, sl);
	}

	/**
	 * Remove a listener from reader. When removed, listener will not receive
	 * any events from the reader.
	 * 
	 * @param sl
	 *            {@link SentenceListener} to be removed.
	 */
	public void removeSentenceListener(SentenceListener sl) {
		for (List<SentenceListener> list : listeners.values()) {
			if (list.contains(sl)) {
				list.remove(sl);
			}
		}
	}

	/**
	 * Dispatch data to all listeners.
	 * Puts the nmea string into SignalKConstants.self.sources.nmea.0183.[sentenceid]
	 * Processes the nmea into signalk position, heading, etc.
	 * 
	 * @param map
	 * 
	 * @param sentence
	 *            sentence string.
	 */
	private void fireSentenceEvent(SignalKModel model, Sentence sentence, String device) {
		if (!sentence.isValid()) {
			logger.warn("NMEA Sentence is invalid:" + sentence.toSentence());
			return;
		}
		String now = Util.getIsoTimeString();
		model.put(vessels_dot_self_dot+"sources.nmea.0183"+dot+sentence.getSentenceId()+dot+value,sentence.toSentence());
		model.put(vessels_dot_self_dot+"sources.nmea.0183"+dot+sentence.getSentenceId()+dot+timestamp,now);
		if(StringUtils.isBlank(device))device = "unknown";
		model.put(vessels_dot_self_dot+"sources.nmea.0183"+dot+sentence.getSentenceId()+dot+source,device);
		// TODO: Why am I creating all these lists?
		String type = sentence.getSentenceId();
		Set<SentenceListener> list = new HashSet<SentenceListener>();

		if (listeners.containsKey(type)) {
			list.addAll(listeners.get(type));
		}
		if (listeners.containsKey(DISPATCH_ALL)) {
			list.addAll(listeners.get(DISPATCH_ALL));
		}

		for (SentenceListener sl : list) {
			try {
				SentenceEvent se = new SentenceEvent(model, sentence);
				sl.sentenceRead(se);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	/**
	 * Registers a SentenceListener to hash map with given key.
	 * 
	 * @param type
	 *            Sentence type to register for
	 * @param sl
	 *            SentenceListener to register
	 */
	private void registerListener(String type, SentenceListener sl) {
		if (listeners.containsKey(type)) {
			listeners.get(type).add(sl);
		} else {
			List<SentenceListener> list = new Vector<SentenceListener>();
			list.add(sl);
			listeners.put(type, list);
		}
	}

	/**
	 * Adds NMEA sentence listeners to process NMEA to simple output
	 * 
	 * @param processor
	 */
	private void setNmeaListeners() {

		addSentenceListener(new SentenceListener() {

			private boolean startLat = true;
			private boolean startLon = true;
			double previousLat = 0;
			double previousLon = 0;
			double previousSpeed = 0;
			static final double ALPHA = 1 - 1.0 / 6;

			public void sentenceRead(SentenceEvent evt) {
				SignalKModel sk = (SignalKModel) evt.getSource();
				String now = (String) sk.get(vessels_dot_self_dot+"sources.nmea.0183"+dot+evt.getSentence().getSentenceId()+dot+timestamp);
				try{
				
					if (evt.getSentence() instanceof PositionSentence) {
						PositionSentence sen = (PositionSentence) evt.getSentence();
	
						if (startLat) {
							previousLat = sen.getPosition().getLatitude();
							startLat = false;
						}
						previousLat = Util.movingAverage(ALPHA, previousLat, sen.getPosition().getLatitude());
						if(logger.isDebugEnabled())logger.debug("lat position:" + sen.getPosition().getLatitude() + ", hemi=" + sen.getPosition().getLatitudeHemisphere());
						sk.put(vessels_dot_self_dot + nav_position_latitude , previousLat);
	
						if (startLon) {
							previousLon = sen.getPosition().getLongitude();
							startLon = false;
						}
						previousLon = Util.movingAverage(ALPHA, previousLon, sen.getPosition().getLongitude());
						sk.put(vessels_dot_self_dot + nav_position_longitude , previousLon);
						sk.put(vessels_dot_self_dot + nav_position +dot+ source , vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId());
						sk.put(vessels_dot_self_dot + nav_position + dot+timestamp , now);
					}
	
					if (evt.getSentence() instanceof HeadingSentence) {
						
						if (!(evt.getSentence() instanceof VHWSentence)) {
							
							HeadingSentence sen = (HeadingSentence) evt.getSentence();

							if (sen.isTrue()) {
								try {
									sk.put(vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId()+dot+source+dot+"src",sen.toSentence());
									sk.put(vessels_dot_self_dot + nav_courseOverGroundTrue , sen.getHeading(),vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId(),now);
								} catch (Exception e) {
									logger.error(e.getMessage());
								}
							} else {
								sk.put(vessels_dot_self_dot + nav_courseOverGroundMagnetic , sen.getHeading(),vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId(),now);
							}
						}
					}
					
					if (evt.getSentence() instanceof RMCSentence) {
						RMCSentence sen = (RMCSentence) evt.getSentence();
						if(rmcClock)Util.checkTime(sen);
						previousSpeed = Util.movingAverage(ALPHA, previousSpeed, Util.kntToMs(sen.getSpeed()));
						sk.put(vessels_dot_self_dot + nav_speedOverGround , previousSpeed, vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId(), now);
					}
					if (evt.getSentence() instanceof VHWSentence) {
						VHWSentence sen = (VHWSentence) evt.getSentence();
						//VHW sentence types have both, but true can be empty
						try {
							sk.put(vessels_dot_self_dot + nav_courseOverGroundMagnetic , sen.getMagneticHeading(), vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId(), now);
							sk.put(vessels_dot_self_dot + nav_courseOverGroundTrue , sen.getHeading(), vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId(), now);
							
						} catch (DataNotAvailableException e) {
							logger.error(e.getMessage());
						}
						previousSpeed = Util.movingAverage(ALPHA, previousSpeed, Util.kntToMs(sen.getSpeedKnots()));
						sk.put(vessels_dot_self_dot + nav_speedOverGround , previousSpeed, vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId(), now);
					}
	
					// MWV wind
					// Mega sends $IIMVW with 0-360d clockwise from bow, (relative to bow)
					// Mega value is int+'.0'
					if (evt.getSentence() instanceof MWVSentence) {
						MWVSentence sen = (MWVSentence) evt.getSentence();
						//TODO: check relative to bow or compass + sen.getSpeedUnit()
						// relative to bow
						double angle = sen.getAngle();
						sk.put(vessels_dot_self_dot + env_wind_angleApparent , angle, vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId(), now);
						sk.put(vessels_dot_self_dot + env_wind_speedApparent , Util.kntToMs(sen.getSpeed()), vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId(), now);
						//sk.put(vessels_dot_self_dot + env_wind+dot+source,vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId());
						//sk.put(vessels_dot_self_dot + env_wind + dot+timestamp , now);
					}
					
					// Cruzpro BVE sentence
					// TODO: how to deal with multiple engines??
					/*if (evt.getSentence() instanceof BVESentence) {
						BVESentence sen = (BVESentence) evt.getSentence();
						if (sen.isFuelGuage()) {
							sk.put(json, tanks_id_level , sen.getFuelRemaining(), "output");
							sk.put(json, propulsion_id_fuelUsageRate , sen.getFuelUseRateUnitsPerHour(), "output");
							
							// map.put(Constants.FUEL_USED, sen.getFuelUsedOnTrip());
							// sk.put(tempSignalKConstants.selfNode, SignalKConstants.tank_level, sen.getFuelRemaining(), "output");
						}
						if (sen.isEngineRpm()) {
							sk.put(json, propulsion_id_rpm , sen.getEngineRpm(), "output");
							// map.put(Constants.ENGINE_HOURS, sen.getEngineHours());
							//sk.put(tempSignalKConstants.selfNode, SignalKConstants.propulsion_hours, sen.getEngineHours(), "output");
							// map.put(Constants.ENGINE_MINUTES, sen.getEngineMinutes());
							//sk.put(tempSignalKConstants.selfNode, SignalKConstants.propulsion_minutes, sen.getEngineMinutes(), "output");
	
						}
						if (sen.isTempGuage()) {
							sk.put(json, propulsion_id_engineTemperature , sen.getEngineTemp(), "output");
							// map.put(Constants.ENGINE_VOLTS, sen.getVoltage());
							//sk.put(tempSignalKConstants.selfNode, SignalKConstants.propulsion_engineVolts, sen.getVoltage(), "output");
							// map.put(Constants.ENGINE_TEMP_HIGH_ALARM, sen.getHighTempAlarmValue());
							// map.put(Constants.ENGINE_TEMP_LOW_ALARM, sen.getLowTempAlarmValue());
	
						}
						if (sen.isPressureGuage()) {
							sk.put(json, propulsion_id_oilPressure , sen.getPressure(), "output");
							// map.put(Constants.ENGINE_PRESSURE_HIGH_ALARM, sen.getHighPressureAlarmValue());
							// map.put(Constants.ENGINE_PRESSURE_LOW_ALARM, sen.getLowPressureAlarmValue());
	
						}
	
					}*/
					if (evt.getSentence() instanceof DepthSentence) {
						DepthSentence sen = (DepthSentence) evt.getSentence();
						// in meters
						sk.put(vessels_dot_self_dot + env_depth_belowTransducer , sen.getDepth(), vessels_dot_self_dot+"sources.nmea.0183"+dot+sen.getSentenceId(), now);
					}
				}catch (DataNotAvailableException e){
					logger.error(e.getMessage()+":"+evt.getSentence().toSentence());
					//logger.debug(e.getMessage(),e);
				}
				
			}


			public void readingStopped() {
			}

			public void readingStarted() {
			}

			public void readingPaused() {
			}
		});
	}

}
