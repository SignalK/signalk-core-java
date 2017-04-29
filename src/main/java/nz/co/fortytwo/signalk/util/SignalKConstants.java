/*
 *
 * Copyright (C) 2012-2014 R T Huitema. All Rights Reserved.
 * Web: www.42.co.nz
 * Email: robert@42.co.nz
 * Author: R T Huitema
 *
 * This file is part of the signalk-server-java project
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
package nz.co.fortytwo.signalk.util;


public class SignalKConstants {

	//fixed
	public static final double MS_TO_KNOTS = 1.94384;
	public static final double MS_TO_KM = 3.6;
	public static final double KNOTS_TO_MS = 0.51444;
	public static final double MTR_TO_FEET = 3.28084;
	public static final double MTR_TO_FATHOM = 0.546806649;
	public static final double TWO_PI = 2*Math.PI;
	public static final String M = "m";             // meters
	public static final String F = "f";             // fathoms
	public static final String FT = "ft";           // feet
	public static final String NM = "n.m.";         // nautical miles
	public static final String MS = "m/s";          // meters/sec
	public static final String KM_PER_HR = "km/hr"; // km/hr
	public static final String MI_PER_HR = "mi/hr"; // mi/hr
	public static final String NM_PER_HR = "Kt";    // knots
        public static final String FAHR = "F";
        public static final String CENT = "C";


	public static final String dot = ".";
	public static final String vessels = "vessels";
	public static final String aircraft = "aircraft";
	public static final String sar = "sar";
	public static final String self_str =  "self";
	public static final String CONFIG = "config";
    public static final String SIGNALK = "signalk";

	public static String self = self_str;
 	public static String vessels_dot_self_dot=vessels+dot+self+dot;
	public static String vessels_dot_self=vessels+dot+self;

	public static final String version = "version";
	public static final String timestamp = "timestamp";
	public static final String sourceRef = "$source";
	public static final String source = "source";
	public static final String value = "value";
	public static final String values = "values";
	public static final String UNKNOWN = "unknown";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ALTITUDE = "altitude";



	public static final String meta = "meta";
	public static final String zones = "zones";
	public static final String attr = "_attr";
	public static final String alarm = "alarm";
	public static final String alarmState = "alarmState";
	public static final String alarmMessage = "alarmMessage";
	public static final String warnMessage = "warnMessage";
	public static final String message = "message";
	public static final String warn = "warn";
	public static final String normal = "normal";

	public static final String type = "type";
	public static final String label = "label";
	public static final String routes = "routes";
	public static final String key= "key";
	public static final String currentTrack = "currentTrack";


	public static final String websocketUrl="signalk-ws";
	public static final String restUrl="signalk-http";

	public static final String signalkTcpPort = "signalk-tcp";
	public static final String signalkUdpPort = "signalk-udp";
	public static final String nmeaTcpPort = "nmea-tcp";
	public static final String nmeaUdpPort = "nmea-udp";

	public static final String stompPort = "stomp";
	public static final String mqttPort = "mqtt";



	public static final String MSG_TYPE = "MSG_TYPE";
	public static final String SERIAL = "SERIAL";
	public static final String DEMO = "DEMO";
	public static final String EXTERNAL_IP = "EXTERNAL_IP";
	public static final String INTERNAL_IP = "INTERNAL_IP";

	public static final String MSG_SERIAL_PORT = "MSG_SERIAL_PORT";
	public static final String MSG_SRC_BUS = "MSG_SRC_BUS";
	public static final String MSG_SRC_IP = "MSG_SRC_IP";
	public static final String MSG_SRC_IP_PORT = "MSG_SRC_IP_PORT";
	public static final String MSG_APPROVAL = "MSG_APPROVAL";
	public static final String REQUIRED = "REQUIRED";
	public static final String CONFIG_ACTION = "CONFIG_ACTION";
	public static final String CONFIG_ACTION_READ = "CONFIG_ACTION_READ";
	public static final String CONFIG_ACTION_SAVE = "CONFIG_ACTION_SAVE";

	public static final String CONTEXT = "context";
	public static final String UPDATES = "updates";
	public static final String SUBSCRIBE = "subscribe";
	public static final String UNSUBSCRIBE = "unsubscribe";
	public static final String LIST = "list";
	public static final String GET = "get";
	public static final String PUT = "put";
	public static final String PATHLIST = "pathlist";
	//public static final String source = "source";
	//public static final String DEVICE = "device";
	//public static final String timestamp = "timestamp";
	//public static final String SRC = "src";
	//public static final String PGN = "pgn";
	//public static final String value = "value";

	public static final String PATH = "path";
	public static final String PERIOD = "period";
	public static final String MIN_PERIOD = "minPeriod";
	public static final String SIGNALK_FORMAT="SIGNALK_FORMAT";
	public static final String FORMAT="format";
	public static final String FORMAT_DELTA="delta";
	public static final String FORMAT_FULL="full";
	public static final String POLICY="policy";
	public static final String POLICY_FIXED = "fixed";
	public static final String POLICY_INSTANT = "instant";
	public static final String POLICY_IDEAL = "ideal";

	public static final String N2K_MESSAGE = "N2K_MESSAGE";

	public static final String URN_UUID = "urn:mrn:signalk:uuid:";
	public static final String SIGNALK_DISCOVERY = "/signalk";
	public static final String SIGNALK_AUTH = "/signalk/v1/auth";
	public static final String SIGNALK_API = "/signalk/v1/api";
	public static final String SIGNALK_WS = "/signalk/v1/stream";
	public static final String SIGNALK_INSTALL = "/signalk/v1/install";
	public static final String SIGNALK_UPGRADE = "/signalk/v1/upgrade";
	public static final String SIGNALK_CONFIG = "/signalk/v1/config";
	public static final String SIGNALK_LOGGER = "/signalk/v1/logger";
	public static final String SIGNALK_UPLOAD = "/signalk/v1/upload";
	public static final String SIGNALK_RESTART = "/signalk/v1/restart";

	public static final String _SIGNALK_HTTP_TCP_LOCAL = "_signalk-http._tcp.local.";
	public static final String _SIGNALK_WS_TCP_LOCAL = "_signalk-ws._tcp.local.";
	//public static final String name = "name";
	//public static final String mmsi = "mmsi";

	public static final String url="url";
	public static final String uuid="uuid";
	public static final String vessels_id = "vessels.*";
	public static final String pgn = "pgn";


	//temp
	public static final String env_time_time = "environment.time.time";
	public static final String env_time_date = "environment.time.date";
	public static final String env_time_utc = "environment.time.utc";
	public static final String env_timezone = "environment.time.timezone";

	//generated
	public static final String resources="resources";
	public static final String resources_charts="resources.charts";
	public static final String resources_charts_id_bounds="resources.charts.*.bounds";
	public static final String resources_charts_id_chartFormat="resources.charts.*.chartFormat";
	public static final String resources_charts_id_chartLayers="resources.charts.*.chartLayers";
	public static final String resources_charts_id_chartUrl="resources.charts.*.chartUrl";
	public static final String resources_charts_id_description="resources.charts.*.description";
	public static final String resources_charts_id_geohash="resources.charts.*.geohash";
	public static final String resources_charts_id_identifier="resources.charts.*.identifier";
	public static final String resources_charts_id_name="resources.charts.*.name";
	public static final String resources_charts_id_region="resources.charts.*.region";
	public static final String resources_charts_id_scale="resources.charts.*.scale";
	public static final String resources_charts_id_tilemapUrl="resources.charts.*.tilemapUrl";
	public static final String resources_notes="resources.notes";
	public static final String resources_notes_id_description="resources.notes.*.description";
	public static final String resources_notes_id_geohash="resources.notes.*.geohash";
	public static final String resources_notes_id_mimeType="resources.notes.*.mimeType";
	public static final String resources_notes_id_position="resources.notes.*.position";
	public static final String resources_notes_id_position_altitude="resources.notes.*.position.altitude";
	public static final String resources_notes_id_position_latitude="resources.notes.*.position.latitude";
	public static final String resources_notes_id_position_longitude="resources.notes.*.position.longitude";
	public static final String resources_notes_id_region="resources.notes.*.region";
	public static final String resources_notes_id_title="resources.notes.*.title";
	public static final String resources_notes_id_url="resources.notes.*.url";
	public static final String resources_regions="resources.regions";
	public static final String resources_regions_id_feature="resources.regions.*.feature";
	public static final String resources_regions_id_feature_geometry="resources.regions.*.feature.geometry";
	public static final String resources_regions_id_feature_id="resources.regions.*.feature.id";
	public static final String resources_regions_id_feature_properties="resources.regions.*.feature.properties";
	public static final String resources_regions_id_feature_type="resources.regions.*.feature.type";
	public static final String resources_regions_id_geohash="resources.regions.*.geohash";
	public static final String resources_routes="resources.routes";
	public static final String resources_routes_id_description="resources.routes.*.description";
	public static final String resources_routes_id_distance="resources.routes.*.distance";
	public static final String resources_routes_id_end="resources.routes.*.end";
	public static final String resources_routes_id_feature="resources.routes.*.feature";
	public static final String resources_routes_id_feature_geometry="resources.routes.*.feature.geometry";
	public static final String resources_routes_id_feature_geometry_coordinates="resources.routes.*.feature.geometry.coordinates";
	public static final String resources_routes_id_feature_geometry_type="resources.routes.*.feature.geometry.type";
	public static final String resources_routes_id_feature_id="resources.routes.*.feature.id";
	public static final String resources_routes_id_feature_properties="resources.routes.*.feature.properties";
	public static final String resources_routes_id_feature_type="resources.routes.*.feature.type";
	public static final String resources_routes_id_name="resources.routes.*.name";
	public static final String resources_routes_id_start="resources.routes.*.start";
	public static final String resources_waypoints="resources.waypoints";
	public static final String resources_waypoints_id_feature="resources.waypoints.*.feature";
	public static final String resources_waypoints_id_feature_geometry="resources.waypoints.*.feature.geometry";
	public static final String resources_waypoints_id_feature_geometry_coordinates="resources.waypoints.*.feature.geometry.coordinates";
	public static final String resources_waypoints_id_feature_geometry_type="resources.waypoints.*.feature.geometry.type";
	public static final String resources_waypoints_id_feature_id="resources.waypoints.*.feature.id";
	public static final String resources_waypoints_id_feature_properties="resources.waypoints.*.feature.properties";
	public static final String resources_waypoints_id_feature_type="resources.waypoints.*.feature.type";
	public static final String resources_waypoints_id_position="resources.waypoints.*.position";
	public static final String resources_waypoints_id_position_altitude="resources.waypoints.*.position.altitude";
	public static final String resources_waypoints_id_position_latitude="resources.waypoints.*.position.latitude";
	public static final String resources_waypoints_id_position_longitude="resources.waypoints.*.position.longitude";
	public static final String sources="sources";
	public static final String sources_id_hardwareVersion="sources.*.hardwareVersion";
	public static final String sources_id_id_n2k="sources.*.id.n2k";
	public static final String sources_id_id_n2k_deviceClass="sources.*.id.n2k.deviceClass";
	public static final String sources_id_id_n2k_deviceFunction="sources.*.id.n2k.deviceFunction";
	public static final String sources_id_id_n2k_manufacturerId="sources.*.id.n2k.manufacturerId";
	public static final String sources_id_id_n2k_pgns="sources.*.id.n2k.pgns";
	public static final String sources_id_id_n2k_productID="sources.*.id.n2k.productID";
	public static final String sources_id_id_n2k_src="sources.*.id.n2k.src";
	public static final String sources_id_id_n2k_uniqueId="sources.*.id.n2k.uniqueId";
	public static final String sources_id_id_sentences="sources.*.id.sentences";
	public static final String sources_id_id_talker="sources.*.id.talker";
	public static final String sources_id_installationNote1="sources.*.installationNote1";
	public static final String sources_id_installationNote2="sources.*.installationNote2";
	public static final String sources_id_label="sources.*.label";
	public static final String sources_id_manufacturerInfo="sources.*.manufacturerInfo";
	public static final String sources_id_manufacturerName="sources.*.manufacturerName";
	public static final String sources_id_productName="sources.*.productName";
	public static final String sources_id_serialNumber="sources.*.serialNumber";
	public static final String sources_id_softwareVersion="sources.*.softwareVersion";
	public static final String sources_id_type="sources.*.type";
	public static final String communication="communication";
	public static final String communication_callsignHf="communication.callsignHf";
	public static final String communication_callsignVhf="communication.callsignVhf";
	public static final String communication_crewNames="communication.crewNames";
	public static final String communication_email="communication.email";
	public static final String communication_emailHf="communication.emailHf";
	public static final String communication_phoneNumber="communication.phoneNumber";
	public static final String communication_satPhoneNumber="communication.satPhoneNumber";
	public static final String communication_skipperName="communication.skipperName";
	public static final String design="design";
	public static final String design_airHeight="design.airHeight";
	public static final String design_beam="design.beam";
	public static final String design_displacement="design.displacement";
	public static final String design_draft="design.draft";
	public static final String design_draft_canoe="design.draft.canoe";
	public static final String design_draft_maximum="design.draft.maximum";
	public static final String design_draft_minimum="design.draft.minimum";
	public static final String design_keel="design.keel";
	public static final String design_keel_angle="design.keel.angle";
	public static final String design_keel_lift="design.keel.lift";
	public static final String design_keel_type="design.keel.type";
	public static final String design_length="design.length";
	public static final String design_length_hull="design.length.hull";
	public static final String design_length_overall="design.length.overall";
	public static final String design_length_waterline="design.length.waterline";
	public static final String design_rigging="design.rigging";
	public static final String design_rigging_configuration="design.rigging.configuration";
	public static final String design_rigging_masts="design.rigging.masts";
	public static final String electrical="electrical";
	public static final String electrical_ac="electrical.ac";
	public static final String electrical_ac_id_phase="electrical.ac.*.phase";
	public static final String electrical_ac_id_phase_id_apparentPower="electrical.ac.*.phase.*.apparentPower";
	public static final String electrical_ac_id_phase_id_associatedBus="electrical.ac.*.phase.*.associatedBus";
	public static final String electrical_ac_id_phase_id_current="electrical.ac.*.phase.*.current";
	public static final String electrical_ac_id_phase_id_frequency="electrical.ac.*.phase.*.frequency";
	public static final String electrical_ac_id_phase_id_lineLineVoltage="electrical.ac.*.phase.*.lineLineVoltage";
	public static final String electrical_ac_id_phase_id_lineNeutralVoltage="electrical.ac.*.phase.*.lineNeutralVoltage";
	public static final String electrical_ac_id_phase_id_powerFactor="electrical.ac.*.phase.*.powerFactor";
	public static final String electrical_ac_id_phase_id_powerFactorLagging="electrical.ac.*.phase.*.powerFactorLagging";
	public static final String electrical_ac_id_phase_id_reactivePower="electrical.ac.*.phase.*.reactivePower";
	public static final String electrical_ac_id_phase_id_realPower="electrical.ac.*.phase.*.realPower";
	public static final String electrical_batteries="electrical.batteries";
	public static final String electrical_batteries_id_associatedBus="electrical.batteries.*.associatedBus";
	public static final String electrical_batteries_id_capacity="electrical.batteries.*.capacity";
	public static final String electrical_batteries_id_capacity_actual="electrical.batteries.*.capacity.actual";
	public static final String electrical_batteries_id_capacity_dischargeLimit="electrical.batteries.*.capacity.dischargeLimit";
	public static final String electrical_batteries_id_capacity_dischargeSinceFull="electrical.batteries.*.capacity.dischargeSinceFull";
	public static final String electrical_batteries_id_capacity_nominal="electrical.batteries.*.capacity.nominal";
	public static final String electrical_batteries_id_capacity_remaining="electrical.batteries.*.capacity.remaining";
	public static final String electrical_batteries_id_capacity_stateOfCharge="electrical.batteries.*.capacity.stateOfCharge";
	public static final String electrical_batteries_id_capacity_stateOfHealth="electrical.batteries.*.capacity.stateOfHealth";
	public static final String electrical_batteries_id_capacity_timeRemaining="electrical.batteries.*.capacity.timeRemaining";
	public static final String electrical_batteries_id_current="electrical.batteries.*.current";
	public static final String electrical_batteries_id_lifetimeDischarge="electrical.batteries.*.lifetimeDischarge";
	public static final String electrical_batteries_id_lifetimeRecharge="electrical.batteries.*.lifetimeRecharge";
	public static final String electrical_batteries_id_temperature="electrical.batteries.*.temperature";
	public static final String electrical_batteries_id_temperature_faultLower="electrical.batteries.*.temperature.faultLower";
	public static final String electrical_batteries_id_temperature_faultUpper="electrical.batteries.*.temperature.faultUpper";
	public static final String electrical_batteries_id_temperature_limitDischargeLower="electrical.batteries.*.temperature.limitDischargeLower";
	public static final String electrical_batteries_id_temperature_limitDischargeUpper="electrical.batteries.*.temperature.limitDischargeUpper";
	public static final String electrical_batteries_id_temperature_limitRechargeLower="electrical.batteries.*.temperature.limitRechargeLower";
	public static final String electrical_batteries_id_temperature_limitRechargeUpper="electrical.batteries.*.temperature.limitRechargeUpper";
	public static final String electrical_batteries_id_temperature_warnLower="electrical.batteries.*.temperature.warnLower";
	public static final String electrical_batteries_id_temperature_warnUpper="electrical.batteries.*.temperature.warnUpper";
	public static final String electrical_batteries_id_voltage="electrical.batteries.*.voltage";
	public static final String electrical_batteries_id_voltage_ripple="electrical.batteries.*.voltage.ripple";
	public static final String electrical_chargers="electrical.chargers";
	public static final String electrical_chargers_id_associatedBus="electrical.chargers.*.associatedBus";
	public static final String electrical_chargers_id_current="electrical.chargers.*.current";
	public static final String electrical_chargers_id_mode="electrical.chargers.*.mode";
	public static final String electrical_chargers_id_temperature="electrical.chargers.*.temperature";
	public static final String electrical_chargers_id_temperature_faultLower="electrical.chargers.*.temperature.faultLower";
	public static final String electrical_chargers_id_temperature_faultUpper="electrical.chargers.*.temperature.faultUpper";
	public static final String electrical_chargers_id_temperature_warnLower="electrical.chargers.*.temperature.warnLower";
	public static final String electrical_chargers_id_temperature_warnUpper="electrical.chargers.*.temperature.warnUpper";
	public static final String electrical_chargers_id_voltage="electrical.chargers.*.voltage";
	public static final String electrical_chargers_id_voltage_ripple="electrical.chargers.*.voltage.ripple";
	public static final String electrical_inverters="electrical.inverters";
	public static final String electrical_inverters_id_ac="electrical.inverters.*.ac";
	public static final String electrical_inverters_id_ac_apparentPower="electrical.inverters.*.ac.apparentPower";
	public static final String electrical_inverters_id_ac_associatedBus="electrical.inverters.*.ac.associatedBus";
	public static final String electrical_inverters_id_ac_current="electrical.inverters.*.ac.current";
	public static final String electrical_inverters_id_ac_frequency="electrical.inverters.*.ac.frequency";
	public static final String electrical_inverters_id_ac_lineLineVoltage="electrical.inverters.*.ac.lineLineVoltage";
	public static final String electrical_inverters_id_ac_lineNeutralVoltage="electrical.inverters.*.ac.lineNeutralVoltage";
	public static final String electrical_inverters_id_ac_powerFactor="electrical.inverters.*.ac.powerFactor";
	public static final String electrical_inverters_id_ac_powerFactorLagging="electrical.inverters.*.ac.powerFactorLagging";
	public static final String electrical_inverters_id_ac_reactivePower="electrical.inverters.*.ac.reactivePower";
	public static final String electrical_inverters_id_ac_realPower="electrical.inverters.*.ac.realPower";
	public static final String electrical_inverters_id_dc="electrical.inverters.*.dc";
	public static final String electrical_inverters_id_dc_associatedBus="electrical.inverters.*.dc.associatedBus";
	public static final String electrical_inverters_id_dc_current="electrical.inverters.*.dc.current";
	public static final String electrical_inverters_id_dc_temperature="electrical.inverters.*.dc.temperature";
	public static final String electrical_inverters_id_dc_temperature_faultLower="electrical.inverters.*.dc.temperature.faultLower";
	public static final String electrical_inverters_id_dc_temperature_faultUpper="electrical.inverters.*.dc.temperature.faultUpper";
	public static final String electrical_inverters_id_dc_temperature_warnLower="electrical.inverters.*.dc.temperature.warnLower";
	public static final String electrical_inverters_id_dc_temperature_warnUpper="electrical.inverters.*.dc.temperature.warnUpper";
	public static final String electrical_inverters_id_dc_voltage="electrical.inverters.*.dc.voltage";
	public static final String electrical_inverters_id_dc_voltage_ripple="electrical.inverters.*.dc.voltage.ripple";
	public static final String electrical_inverters_id_mode="electrical.inverters.*.mode";
	public static final String env="environment";
	public static final String env_current="environment.current";
	public static final String env_current_drift="environment.current.drift";
	public static final String env_current_setMagnetic="environment.current.setMagnetic";
	public static final String env_current_setTrue="environment.current.setTrue";
	public static final String env_depth="environment.depth";
	public static final String env_depth_belowKeel="environment.depth.belowKeel";

	public static final String env_depth_belowSurface="environment.depth.belowSurface";
	public static final String env_depth_belowTransducer="environment.depth.belowTransducer";
	public static final String env_depth_surfaceToTransducer="environment.depth.surfaceToTransducer";
	public static final String env_depth_transducerToKeel="environment.depth.transducerToKeel";
	public static final String env_depth_alarmMethod = "environment.depth.belowSurface.meta.alarmMethod";
	public static final String env_depth_warnMethod = "environment.depth.belowSurface.meta.warnMethod";
	public static final String env_depth_meta_userUnit = "environment.depth.meta.userUnit";
	public static final String env_heave="environment.heave";
	public static final String env_inside="environment.inside";
	public static final String env_inside_engineRoom="environment.inside.engineRoom";
	public static final String env_inside_engineRoom_temperature="environment.inside.engineRoom.temperature";
	public static final String env_inside_freezer="environment.inside.freezer";
	public static final String env_inside_freezer_temperature="environment.inside.freezer.temperature";
	public static final String env_inside_heating="environment.inside.heating";
	public static final String env_inside_heating_temperature="environment.inside.heating.temperature";
	public static final String env_inside_humidity="environment.inside.humidity";
	public static final String env_inside_mainCabin="environment.inside.mainCabin";
	public static final String env_inside_mainCabin_temperature="environment.inside.mainCabin.temperature";
	public static final String env_inside_refrigerator="environment.inside.refrigerator";
	public static final String env_inside_refrigerator_temperature="environment.inside.refrigerator.temperature";
	public static final String env_inside_temperature="environment.inside.temperature";
	public static final String env_mode="environment.mode";
	public static final String env_outside="environment.outside";
	public static final String env_outside_apparentWindChillTemperature="environment.outside.apparentWindChillTemperature";
	public static final String env_outside_dewPointTemperature="environment.outside.dewPointTemperature";
	public static final String env_outside_heatIndexTemperature="environment.outside.heatIndexTemperature";
	public static final String env_outside_humidity="environment.outside.humidity";
	public static final String env_outside_illuminance="environment.outside.illuminance";
	public static final String env_outside_pressure="environment.outside.pressure";
	public static final String env_outside_temperature="environment.outside.temperature";
	public static final String env_outside_theoreticalWindChillTemperature="environment.outside.theoreticalWindChillTemperature";
	public static final String env_tide="environment.tide";
	public static final String env_tide_heightHigh="environment.tide.heightHigh";
	public static final String env_tide_heightLow="environment.tide.heightLow";
	public static final String env_tide_heightNow="environment.tide.heightNow";
	public static final String env_tide_timeHigh="environment.tide.timeHigh";
	public static final String env_tide_timeLow="environment.tide.timeLow";
	public static final String env_time="environment.time";
	public static final String env_time_millis="environment.time.millis";
	public static final String env_time_timezoneOffset="environment.time.timezoneOffset";
	public static final String env_time_timezoneRegion="environment.time.timezoneRegion";
	public static final String env_water="environment.water";
	public static final String env_water_baitWell="environment.water.baitWell";
	public static final String env_water_baitWell_temperature="environment.water.baitWell.temperature";
	public static final String env_water_liveWell="environment.water.liveWell";
	public static final String env_water_liveWell_temperature="environment.water.liveWell.temperature";
	public static final String env_water_salinity="environment.water.salinity";
	public static final String env_water_temperature="environment.water.temperature";
	public static final String env_wind="environment.wind";
	public static final String env_wind_angleApparent="environment.wind.angleApparent";
	public static final String env_wind_angleTrueGround="environment.wind.angleTrueGround";
	public static final String env_wind_angleTrueWater="environment.wind.angleTrueWater";
	public static final String env_wind_directionChangeAlarm="environment.wind.directionChangeAlarm";
	public static final String env_wind_directionMagnetic="environment.wind.directionMagnetic";
	public static final String env_wind_directionTrue="environment.wind.directionTrue";
	public static final String env_wind_speedApparent="environment.wind.speedApparent";
	public static final String env_wind_speedOverGround="environment.wind.speedOverGround";
	public static final String env_wind_speedTrue="environment.wind.speedTrue";
	public static final String flag="flag";
	public static final String mmsi="mmsi";
	public static final String name="name";
	public static final String nav="navigation";
	public static final String nav_anchor="navigation.anchor";
	public static final String nav_anchor_currentRadius="navigation.anchor.currentRadius";
	public static final String nav_anchor_maxRadius="navigation.anchor.maxRadius";
	public static final String nav_anchor_position="navigation.anchor.position";
	public static final String nav_anchor_position_altitude="navigation.anchor.position.altitude";
	public static final String nav_anchor_position_latitude="navigation.anchor.position.latitude";
	public static final String nav_anchor_position_longitude="navigation.anchor.position.longitude";
	public static final String nav_attitude="navigation.attitude";
	public static final String nav_attitude_pitch="navigation.attitude.pitch";
	public static final String nav_attitude_roll="navigation.attitude.roll";
	public static final String nav_attitude_yaw="navigation.attitude.yaw";

	public static final String nav_course="navigation.course";
	public static final String nav_course_activeRoute="navigation.course.activeRoute";
	public static final String nav_courseGreatCircle="navigation.courseGreatCircle";
	public static final String nav_courseGreatCircle_activeRoute="navigation.courseGreatCircle.activeRoute";
	public static final String nav_courseGreatCircle_activeRoute_estimatedTimeOfArrival="navigation.courseGreatCircle.activeRoute.estimatedTimeOfArrival";
	public static final String nav_courseGreatCircle_activeRoute_href="navigation.courseGreatCircle.activeRoute.href";
	public static final String nav_courseGreatCircle_activeRoute_startTime="navigation.courseGreatCircle.activeRoute.startTime";
	public static final String nav_courseGreatCircle_bearingTrackMagnetic="navigation.courseGreatCircle.bearingTrackMagnetic";
	public static final String nav_courseGreatCircle_bearingTrackTrue="navigation.courseGreatCircle.bearingTrackTrue";
	public static final String nav_courseGreatCircle_crossTrackError="navigation.courseGreatCircle.crossTrackError";
	public static final String nav_courseGreatCircle_nextPoint="navigation.courseGreatCircle.nextPoint";
	public static final String nav_courseGreatCircle_nextPoint_bearingMagnetic="navigation.courseGreatCircle.nextPoint.bearingMagnetic";
	public static final String nav_courseGreatCircle_nextPoint_bearingTrue="navigation.courseGreatCircle.nextPoint.bearingTrue";
	public static final String nav_courseGreatCircle_nextPoint_distance="navigation.courseGreatCircle.nextPoint.distance";
	public static final String nav_courseGreatCircle_nextPoint_position="navigation.courseGreatCircle.nextPoint.position";
	public static final String nav_courseGreatCircle_nextPoint_position_latitude="navigation.courseGreatCircle.nextPoint.position.latitude";
	public static final String nav_courseGreatCircle_nextPoint_position_longitude="navigation.courseGreatCircle.nextPoint.position.longitude";
	public static final String nav_courseGreatCircle_nextPoint_timeToGo="navigation.courseGreatCircle.nextPoint.timeToGo";
	public static final String nav_courseGreatCircle_nextPoint_velocityMadeGood="navigation.courseGreatCircle.nextPoint.velocityMadeGood";
	public static final String nav_courseGreatCircle_previousPoint="navigation.courseGreatCircle.previousPoint";
	public static final String nav_courseGreatCircle_previousPoint_distance="navigation.courseGreatCircle.previousPoint.distance";
	public static final String nav_courseGreatCircle_previousPoint_position="navigation.courseGreatCircle.previousPoint.position";
	public static final String nav_courseGreatCircle_previousPoint_position_latitude="navigation.courseGreatCircle.previousPoint.position.latitude";
	public static final String nav_courseGreatCircle_previousPoint_position_longitude="navigation.courseGreatCircle.previousPoint.position.longitude";
	public static final String nav_courseOverGroundMagnetic="navigation.courseOverGroundMagnetic";
	public static final String nav_courseOverGroundTrue="navigation.courseOverGroundTrue";
	public static final String nav_courseRhumbline="navigation.courseRhumbline";
	public static final String nav_courseRhumbline_activeRoute="navigation.courseRhumbline.activeRoute";
	public static final String nav_courseRhumbline_activeRoute_estimatedTimeOfArrival="navigation.courseRhumbline.activeRoute.estimatedTimeOfArrival";
	public static final String nav_courseRhumbline_activeRoute_href="navigation.courseRhumbline.activeRoute.href";
	public static final String nav_courseRhumbline_activeRoute_startTime="navigation.courseRhumbline.activeRoute.startTime";
	public static final String nav_courseRhumbline_bearingTrackMagnetic="navigation.courseRhumbline.bearingTrackMagnetic";
	public static final String nav_courseRhumbline_bearingTrackTrue="navigation.courseRhumbline.bearingTrackTrue";
	public static final String nav_courseRhumbline_crossTrackError="navigation.courseRhumbline.crossTrackError";
	public static final String nav_courseRhumbline_nextPoint="navigation.courseRhumbline.nextPoint";
	public static final String nav_courseRhumbline_nextPoint_bearingMagnetic="navigation.courseRhumbline.nextPoint.bearingMagnetic";
	public static final String nav_courseRhumbline_nextPoint_bearingTrue="navigation.courseRhumbline.nextPoint.bearingTrue";
	public static final String nav_courseRhumbline_nextPoint_distance="navigation.courseRhumbline.nextPoint.distance";
	public static final String nav_courseRhumbline_nextPoint_position="navigation.courseRhumbline.nextPoint.position";
	public static final String nav_courseRhumbline_nextPoint_position_latitude="navigation.courseRhumbline.nextPoint.position.latitude";
	public static final String nav_courseRhumbline_nextPoint_position_longitude="navigation.courseRhumbline.nextPoint.position.longitude";
	public static final String nav_courseRhumbline_nextPoint_timeToGo="navigation.courseRhumbline.nextPoint.timeToGo";
	public static final String nav_courseRhumbline_nextPoint_velocityMadeGood="navigation.courseRhumbline.nextPoint.velocityMadeGood";
	public static final String nav_courseRhumbline_previousPoint="navigation.courseRhumbline.previousPoint";
	public static final String nav_courseRhumbline_previousPoint_distance="navigation.courseRhumbline.previousPoint.distance";
	public static final String nav_courseRhumbline_previousPoint_position="navigation.courseRhumbline.previousPoint.position";
	public static final String nav_courseRhumbline_previousPoint_position_latitude="navigation.courseRhumbline.previousPoint.position.latitude";
	public static final String nav_courseRhumbline_previousPoint_position_longitude="navigation.courseRhumbline.previousPoint.position.longitude";

	public static final String nav_datetime="navigation.datetime";
	public static final String nav_datetime_gnssTimeSource="navigation.datetime.gnssTimeSource";
	public static final String nav_destination="navigation.destination";
	public static final String nav_destination_eta="navigation.destination.eta";
	public static final String nav_destination_waypoint="navigation.destination.waypoint";
	public static final String nav_gnss="navigation.gnss";
	public static final String nav_gnss_antennaAltitude="navigation.gnss.antennaAltitude";
	public static final String nav_gnss_differentialAge="navigation.gnss.differentialAge";
	public static final String nav_gnss_differentialReference="navigation.gnss.differentialReference";
	public static final String nav_gnss_geoidalSeparation="navigation.gnss.geoidalSeparation";
	public static final String nav_gnss_horizontalDilution="navigation.gnss.horizontalDilution";
	public static final String nav_gnss_integrity="navigation.gnss.integrity";
	public static final String nav_gnss_methodQuality="navigation.gnss.methodQuality";
	public static final String nav_gnss_positionDilution="navigation.gnss.positionDilution";
	public static final String nav_gnss_satellites="navigation.gnss.satellites";
	public static final String nav_headingMagnetic="navigation.headingMagnetic";
	public static final String nav_headingTrue="navigation.headingTrue";
	public static final String nav_leewayAngle="navigation.leewayAngle";
	public static final String nav_lights="navigation.lights";
	public static final String nav_log="navigation.log";
	public static final String nav_logTrip="navigation.logTrip";
	public static final String nav_magneticVariation="navigation.magneticVariation";
	public static final String nav_magneticVariationAgeOfService="navigation.magneticVariationAgeOfService";
	public static final String nav_position="navigation.position";
	public static final String nav_position_altitude="navigation.position.altitude";
	public static final String nav_position_latitude="navigation.position.latitude";
	public static final String nav_position_longitude="navigation.position.longitude";
	public static final String nav_racing="navigation.racing";
	public static final String nav_racing_distanceLayline="navigation.racing.distanceLayline";
	public static final String nav_racing_distanceStartline="navigation.racing.distanceStartline";
	public static final String nav_racing_startLinePort="navigation.racing.startLinePort";
	public static final String nav_racing_startLineStb="navigation.racing.startLineStb";
	public static final String nav_racing_timePortDown="navigation.racing.timePortDown";
	public static final String nav_racing_timePortUp="navigation.racing.timePortUp";
	public static final String nav_racing_timeStbdDown="navigation.racing.timeStbdDown";
	public static final String nav_racing_timeStbdUp="navigation.racing.timeStbdUp";
	public static final String nav_racing_timeToStart="navigation.racing.timeToStart";
	public static final String nav_rateOfTurn="navigation.rateOfTurn";
	public static final String nav_speedOverGround="navigation.speedOverGround";
	public static final String nav_sogDisplayUnit="navigation.speedOverGround.meta.unit";
	public static final String nav_speedThroughWater="navigation.speedThroughWater";
	public static final String nav_speedThroughWaterLongitudinal="navigation.speedThroughWaterLongitudinal";
	public static final String nav_speedThroughWaterTransverse="navigation.speedThroughWaterTransverse";
	public static final String nav_stwDisplayUnit="navigation.speedThroughWater.meta.unit";

	public static final String nav_state="navigation.state";
	public static final String notifications="notifications";
	public static final String notifications_abandon="notifications.abandon";
	public static final String notifications_adrift="notifications.adrift";
	public static final String notifications_collision="notifications.collision";
	public static final String notifications_fire="notifications.fire";
	public static final String notifications_flooding="notifications.flooding";
	public static final String notifications_grounding="notifications.grounding";
	public static final String notifications_listing="notifications.listing";
	public static final String notifications_mob="notifications.mob";
	public static final String notifications_piracy="notifications.piracy";
	public static final String notifications_sinking="notifications.sinking";
	public static final String performance="performance";
	public static final String performance_beatAngle="performance.beatAngle";
	public static final String performance_beatAngleTargetSpeed="performance.beatAngleTargetSpeed";
	public static final String performance_beatAngleVelocityMadeGood="performance.beatAngleVelocityMadeGood";
	public static final String performance_gybeAngle="performance.gybeAngle";
	public static final String performance_gybeAngleTargetSpeed="performance.gybeAngleTargetSpeed";
	public static final String performance_gybeAngleVelocityMadeGood="performance.gybeAngleVelocityMadeGood";
	public static final String performance_leeway="performance.leeway";
	public static final String performance_polarSpeed="performance.polarSpeed";
	public static final String performance_polarSpeedRatio="performance.polarSpeedRatio";
	public static final String performance_tackMagnetic="performance.tackMagnetic";
	public static final String performance_tackTrue="performance.tackTrue";
	public static final String performance_targetAngle="performance.targetAngle";
	public static final String performance_targetSpeed="performance.targetSpeed";
	public static final String performance_velocityMadeGood="performance.velocityMadeGood";
	public static final String performance_velocityMadeGoodToWaypoint="performance.velocityMadeGoodToWaypoint";
	public static final String port="port";
	public static final String propulsion="propulsion";
	public static final String propulsion_id_alternatorVoltage="propulsion.*.alternatorVoltage";
	public static final String propulsion_id_boostPressure="propulsion.*.boostPressure";
	public static final String propulsion_id_coolantPressure="propulsion.*.coolantPressure";
	public static final String propulsion_id_coolantTemperature="propulsion.*.coolantTemperature";
	public static final String propulsion_engine_coolantTemperature="propulsion.engine.coolantTemperature";
	public static final String propulsion_engine_coolantTemperature_meta_unit="propulsion.engine.coolantTemperature.meta.unit";
	public static final String propulsion_engine_coolantTemperature_meta_alarmMethod = "propulsion.engine.coolantTemperature.meta.alarmMethod";
	public static final String propulsion_engine_coolantTemperature_meta_warnMethod = "propulsion.engine.coolantTemperature.meta.warnMethod";

	public static final String propulsion_id_drive="propulsion.*.drive";
	public static final String propulsion_id_drive_propeller="propulsion.*.drive.propeller";
	public static final String propulsion_id_drive_thrustAngle="propulsion.*.drive.thrustAngle";
	public static final String propulsion_id_drive_trimState="propulsion.*.drive.trimState";
	public static final String propulsion_id_drive_type="propulsion.*.drive.type";
	public static final String propulsion_id_engineLoad="propulsion.*.engineLoad";
	public static final String propulsion_id_engineTorque="propulsion.*.engineTorque";
	public static final String propulsion_id_exhaustTemperature="propulsion.*.exhaustTemperature";
	public static final String propulsion_id_fuel="propulsion.*.fuel";
	public static final String propulsion_id_fuel_averageRate="propulsion.*.fuel.averageRate";
	public static final String propulsion_id_fuel_economyRate="propulsion.*.fuel.economyRate";
	public static final String propulsion_id_fuel_pressure="propulsion.*.fuel.pressure";
	public static final String propulsion_id_fuel_rate="propulsion.*.fuel.rate";
	public static final String propulsion_id_fuel_type="propulsion.*.fuel.type";
	public static final String propulsion_id_fuel_used="propulsion.*.fuel.used";
	public static final String propulsion_id_label="propulsion.*.label";
	public static final String propulsion_id_oilPressure="propulsion.*.oilPressure";
	public static final String propulsion_id_oilTemperature="propulsion.*.oilTemperature";
	public static final String propulsion_id_revolutions="propulsion.*.revolutions";
	public static final String propulsion_id_runTime="propulsion.*.runTime";
	public static final String propulsion_id_state="propulsion.*.state";
	public static final String propulsion_id_temperature="propulsion.*.temperature";
	public static final String propulsion_id_transmission="propulsion.*.transmission";
	public static final String propulsion_id_transmission_gear="propulsion.*.transmission.gear";
	public static final String propulsion_id_transmission_gearRatio="propulsion.*.transmission.gearRatio";
	public static final String propulsion_id_transmission_oilPressure="propulsion.*.transmission.oilPressure";
	public static final String propulsion_id_transmission_oilTemperature="propulsion.*.transmission.oilTemperature";
	public static final String registrations="registrations";
	public static final String registrations_imo="registrations.imo";
	public static final String registrations_local="registrations.local";
	public static final String registrations_local_id_description="registrations.local.*.description";
	public static final String registrations_local_id_registration="registrations.local.*.registration";
	public static final String registrations_national="registrations.national";
	public static final String registrations_national_id_country="registrations.national.*.country";
	public static final String registrations_national_id_description="registrations.national.*.description";
	public static final String registrations_national_id_registration="registrations.national.*.registration";
	public static final String registrations_other="registrations.other";
	public static final String registrations_other_id_description="registrations.other.*.description";
	public static final String registrations_other_id_registration="registrations.other.*.registration";
	public static final String sails="sails";
	public static final String sails_area="sails.area";
	public static final String sails_area_active="sails.area.active";
	public static final String sails_area_total="sails.area.total";
	public static final String sails_inventory="sails.inventory";
	public static final String sails_inventory_id_active="sails.inventory.*.active";
	public static final String sails_inventory_id_area="sails.inventory.*.area";
	public static final String sails_inventory_id_brand="sails.inventory.*.brand";
	public static final String sails_inventory_id_material="sails.inventory.*.material";
	public static final String sails_inventory_id_maximumWind="sails.inventory.*.maximumWind";
	public static final String sails_inventory_id_minimumWind="sails.inventory.*.minimumWind";
	public static final String sails_inventory_id_name="sails.inventory.*.name";
	public static final String sails_inventory_id_type="sails.inventory.*.type";
	public static final String sensors="sensors";
	public static final String sensors_id_fromBow="sensors.*.fromBow";
	public static final String sensors_id_fromCenter="sensors.*.fromCenter";
	public static final String sensors_id_name="sensors.*.name";
	public static final String sensors_id_sensorData="sensors.*.sensorData";
	public static final String sensors_id_sensorType="sensors.*.sensorType";
	public static final String steering="steering";
	public static final String steering_autopilot="steering.autopilot";
	public static final String steering_autopilot_backlash="steering.autopilot.backlash";
	public static final String steering_autopilot_deadZone="steering.autopilot.deadZone";
	public static final String steering_autopilot_headingSource="steering_autopilot_headingSource";
	public static final String steering_autopilot_gain="steering.autopilot.gain";
	public static final String steering_autopilot_maxDriveCurrent="steering.autopilot.maxDriveCurrent";
	public static final String steering_autopilot_maxDriveRate="steering.autopilot.maxDriveRate";
	public static final String steering_autopilot_mode="steering.autopilot.mode";
	public static final String steering_autopilot_portLock="steering.autopilot.portLock";
	public static final String steering_autopilot_starboardLock="steering.autopilot.starboardLock";
	public static final String steering_autopilot_state="steering.autopilot.state";
	public static final String steering_autopilot_target="steering.autopilot.target";
	public static final String steering_autopilot_target_headingMagnetic="steering.autopilot.target.headingMagnetic";
	public static final String steering_autopilot_target_headingTrue="steering.autopilot.target.headingTrue";
	public static final String steering_autopilot_target_windAngleApparent="steering.autopilot.target.windAngleApparent";
	public static final String steering_rudderAngle="steering.rudderAngle";
	public static final String steering_rudderAngleTarget="steering.rudderAngleTarget";
	public static final String tanks="tanks";
	public static final String tanks_blackWater="tanks.blackWater";
	public static final String tanks_blackWater_id_capacity="tanks.blackWater.*.capacity";
	public static final String tanks_blackWater_id_currentLevel="tanks.blackWater.*.currentLevel";
	public static final String tanks_blackWater_id_currentVolume="tanks.blackWater.*.currentVolume";
	public static final String tanks_blackWater_id_name="tanks.blackWater.*.name";
	public static final String tanks_blackWater_id_type="tanks.blackWater.*.type";
	public static final String tanks_freshWater="tanks.freshWater";
	public static final String tanks_freshWater_id_capacity="tanks.freshWater.*.capacity";
	public static final String tanks_freshWater_id_currentLevel="tanks.freshWater.*.currentLevel";
	public static final String tanks_freshWater_id_currentVolume="tanks.freshWater.*.currentVolume";
	public static final String tanks_freshWater_id_name="tanks.freshWater.*.name";
	public static final String tanks_freshWater_id_type="tanks.freshWater.*.type";
	public static final String tanks_fuel="tanks.fuel";
	public static final String tanks_fuel_id_capacity="tanks.fuel.*.capacity";
	public static final String tanks_fuel_id_currentLevel="tanks.fuel.*.currentLevel";
	public static final String tanks_fuel_id_currentVolume="tanks.fuel.*.currentVolume";
	public static final String tanks_fuel_id_name="tanks.fuel.*.name";
	public static final String tanks_fuel_id_type="tanks.fuel.*.type";
	public static final String tanks_liveWell="tanks.liveWell";
	public static final String tanks_liveWell_id_capacity="tanks.liveWell.*.capacity";
	public static final String tanks_liveWell_id_currentLevel="tanks.liveWell.*.currentLevel";
	public static final String tanks_liveWell_id_currentVolume="tanks.liveWell.*.currentVolume";
	public static final String tanks_liveWell_id_name="tanks.liveWell.*.name";
	public static final String tanks_liveWell_id_type="tanks.liveWell.*.type";
	public static final String tanks_lubrication="tanks.lubrication";
	public static final String tanks_lubrication_id_capacity="tanks.lubrication.*.capacity";
	public static final String tanks_lubrication_id_currentLevel="tanks.lubrication.*.currentLevel";
	public static final String tanks_lubrication_id_currentVolume="tanks.lubrication.*.currentVolume";
	public static final String tanks_lubrication_id_name="tanks.lubrication.*.name";
	public static final String tanks_lubrication_id_type="tanks.lubrication.*.type";
	public static final String tanks_wasteWater="tanks.wasteWater";
	public static final String tanks_wasteWater_id_capacity="tanks.wasteWater.*.capacity";
	public static final String tanks_wasteWater_id_currentLevel="tanks.wasteWater.*.currentLevel";
	public static final String tanks_wasteWater_id_currentVolume="tanks.wasteWater.*.currentVolume";
	public static final String tanks_wasteWater_id_name="tanks.wasteWater.*.name";
	public static final String tanks_wasteWater_id_type="tanks.wasteWater.*.type";


	public SignalKConstants() {
		super();
	}

}