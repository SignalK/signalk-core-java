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

import static nz.co.fortytwo.signalk.util.SignalKConstants.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mjson.Json;
import nz.co.fortytwo.signalk.ais.AisVesselInfo;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.Util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage18;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.ais.message.AisStaticCommon;
import dk.dma.ais.packet.AisPacket;
import dk.dma.ais.packet.AisPacketParser;
import dk.dma.ais.sentence.Abk;
import dk.dma.ais.sentence.SentenceException;

/**
 * Accepts AIVDM messages and translates the VDMs into AisMessages and sends the AisPositionMessages on to the browser.
 * Mostly we need 1,2,3,5, 18,19
 * 
 * @author robert
 * 
 */
public class AISHandler {

	private static Logger logger = Logger.getLogger(AISHandler.class);

	/**
	 * Reader to parse lines and deliver complete AIS packets.
	 * Updates them into model, and removes the key from the map.
	 */
	private AisPacketParser packetParser = new AisPacketParser();

	private Map<Integer, String> navStatusMap = new HashMap<>();

	public AISHandler() {
		navStatusMap.put(0, "Under way using engine");
		navStatusMap.put(1, "At anchor");
		navStatusMap.put(2, "Not under command");
		navStatusMap.put(3, "Restricted manoeuverability");
		navStatusMap.put(4, "Constrained by her draught");
		navStatusMap.put(5, "Moored");
		navStatusMap.put(6, "Aground");
		navStatusMap.put(7, "Engaged in Fishing");
		navStatusMap.put(8, "Under way sailing");
		navStatusMap.put(9, "Reserved for future amendment of Navigational Status for HSC");
		navStatusMap.put(10, "Reserved for future amendment of Navigational Status for WIG");
		navStatusMap.put(11, "Reserved for future use");
		navStatusMap.put(12, "Reserved for future use");
		navStatusMap.put(13, "Reserved for future use");
		navStatusMap.put(14, "Reserved for future use");
		navStatusMap.put(15, "Not defined (default)");
	}

	// https://github.com/dma-ais/AisLib

	/**
	 * Converts an AIS NMEA string to a signalK JSON object
	 * See https://github.com/dma-ais/AisLib
	 * 
	 * HD-SF. Free raw AIS data feed for non-commercial use.
	 * hd-sf.com:9009
	 * 
	 * @param bodyStr
	 * @return
	 * @throws Exception 
	 */
	public SignalKModel handle(String bodyStr) throws Exception {

		if (logger.isDebugEnabled())
			logger.debug("Processing AIS:" + bodyStr);

		if (StringUtils.isBlank(bodyStr) || !bodyStr.startsWith("!AIVDM")) {
			return null;
		}

		try {
			List<AisPacket> packets = handleLine(bodyStr);
			AisVesselInfo vInfo = null;
			SignalKModel model = SignalKModelFactory.getCleanInstance();
			for (AisPacket packet : packets) {
				if (packet != null && packet.isValidMessage()) {
					// process message here
					AisMessage message = packet.getAisMessage();
					if (logger.isDebugEnabled())
						logger.debug("AisMessage:" + message.getClass() + ":" + message.toString());
					
					// 1,2,3
					if (message instanceof AisPositionMessage) {
						vInfo = new AisVesselInfo((AisPositionMessage) message);
					}
					// 5,19,24
					if (message instanceof AisStaticCommon) {
						vInfo = new AisVesselInfo((AisStaticCommon) message);
					}
					if (message instanceof AisMessage18) {
						vInfo = new AisVesselInfo((AisMessage18) message);
					}
					if (vInfo != null) {
						String ts = Util.getIsoTimeString(packet.getBestTimestamp());
						String aisVessel = vessels + dot + String.valueOf(vInfo.getUserId())+dot;

						model.put(aisVessel+name, vInfo.getName());
						model.put(aisVessel+mmsi, String.valueOf(vInfo.getUserId()), "AIS", ts);
						model.put(aisVessel+ nav_state, navStatusMap.get(vInfo.getNavStatus()), "AIS", ts);
						if (vInfo.getPosition() != null) {
							model.put(aisVessel+ nav_position+dot+timestamp, ts);
							model.put(aisVessel+ nav_position_source, "AIS");
							model.put(aisVessel+ nav_position_latitude, vInfo.getPosition().getLatitude());
							model.put(aisVessel+ nav_position_longitude, vInfo.getPosition().getLongitude());
						}
						model.put(aisVessel+ nav_courseOverGroundTrue, ((double) vInfo.getCog()) / 10, "AIS", ts);
						model.put(aisVessel+ nav_speedOverGround, Util.kntToMs(((double) vInfo.getSog()) / 10), "AIS", ts);
						model.put(aisVessel+ nav_headingTrue, ((double) vInfo.getTrueHeading()) / 10, "AIS");
						if (vInfo.getCallsign() != null) model.put(aisVessel+ communication_callsignVhf, vInfo.getCallsign(), "AIS", ts);
					}
				}
			}
			return model;

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			logger.error(e.getMessage() + " : " + bodyStr);
			throw e;
		}

	}

	/**
	 * Handle a String message, returning a list of AisPackets
	 * 
	 * @param line
	 * @return
	 */
	public List<AisPacket> handleLine(String messageString) throws IOException {
		if (logger.isDebugEnabled())
			logger.debug("AIS Received : " + messageString);
		// Check for ABK
		if (Abk.isAbk(messageString)) {
			if (logger.isDebugEnabled())
				logger.debug("AIS Received ABK: " + messageString);
			return null;
		}

		try {
			List<AisPacket> packets = new ArrayList<AisPacket>();
			String[] lines = messageString.split("\\r?\\n");

			for (String line : lines) {
				packets.add(packetParser.readLine(line));
			}
			return packets;
		} catch (SentenceException se) {
			if (logger.isDebugEnabled())
				logger.info("AIS Sentence error: " + se.getMessage() + " line: " + messageString);
			throw new IOException(se);

		}
	}

}
