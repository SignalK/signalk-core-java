package nz.co.fortytwo.signalk.handler;

import static nz.co.fortytwo.signalk.util.SignalKConstants.env_depth_belowTransducer;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_angleApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_angleTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_directionTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedApparent;
import static nz.co.fortytwo.signalk.util.SignalKConstants.env_wind_speedTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundMagnetic;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_courseOverGroundTrue;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_magneticVariation;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_latitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_position_longitude;
import static nz.co.fortytwo.signalk.util.SignalKConstants.nav_speedOverGround;
import static nz.co.fortytwo.signalk.util.SignalKConstants.vessels_dot_self_dot;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.DBTSentence;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.HDMSentence;
import net.sf.marineapi.nmea.sentence.HDTSentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VHWSentence;
import net.sf.marineapi.nmea.util.CompassPoint;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.FaaMode;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Units;
import nz.co.fortytwo.signalk.model.SignalKModel;

public class NMEA0183Producer {
	private SentenceFactory sf = SentenceFactory.getInstance();

	//RMC,GLL, GGA = position
	public String createRMC(SignalKModel model) {
		if (model.get(vessels_dot_self_dot + nav_position_latitude) != null && model.get(vessels_dot_self_dot + nav_position_longitude) != null) {
			RMCSentence rmc = (RMCSentence) sf.createParser(TalkerId.GP, SentenceId.RMC);
			rmc.setDate(new Date());
			rmc.setStatus(DataStatus.ACTIVE);
			rmc.setMode(FaaMode.AUTOMATIC);
			rmc.setPosition(new Position((double) model.get(vessels_dot_self_dot + nav_position_latitude), (double) model.get(vessels_dot_self_dot
					+ nav_position_longitude)));
			if (model.getValue(vessels_dot_self_dot + nav_speedOverGround) != null) {
				rmc.setSpeed((double) model.getValue(vessels_dot_self_dot + nav_speedOverGround)*1.94384);
			}
			if (model.get(vessels_dot_self_dot + nav_courseOverGroundTrue) != null) {
				rmc.setCourse((double) model.getValue(vessels_dot_self_dot + nav_courseOverGroundTrue));
			}

			Double variation = (Double) model.getValue(vessels_dot_self_dot + nav_magneticVariation);
			if (variation != null) {
				rmc.setVariation(Math.abs(variation));
				rmc.setDirectionOfVariation((variation < 0) ? CompassPoint.WEST : CompassPoint.EAST);
			}
			return rmc.toSentence();
		}
		return null;
	}
	
	//Heading HDG, HDM, HDT, VHW
	public String createVHW(SignalKModel model) {
		if (model.getValue(vessels_dot_self_dot + nav_courseOverGroundTrue) != null 
				|| model.getValue(vessels_dot_self_dot + nav_speedOverGround) != null
				|| model.getValue(vessels_dot_self_dot + nav_courseOverGroundMagnetic) != null) {
			VHWSentence sen = (VHWSentence) sf.createParser(TalkerId.II, SentenceId.VHW);
			sen.setHeading((double) model.getValue(vessels_dot_self_dot + nav_courseOverGroundTrue));
			sen.setMagneticHeading((double) model.getValue(vessels_dot_self_dot + nav_courseOverGroundMagnetic));
			sen.setSpeedKnots((double) model.getValue(vessels_dot_self_dot + nav_speedOverGround)*1.94384);
			return sen.toSentence();
		}
		return null;
	}
	
	public String createHDT(SignalKModel model) {
		if (model.getValue(vessels_dot_self_dot + nav_courseOverGroundTrue) != null ) {
			HDTSentence sen = (HDTSentence) sf.createParser(TalkerId.II, SentenceId.HDT);
			sen.setHeading((double) model.getValue(vessels_dot_self_dot + nav_courseOverGroundTrue));
			
			return sen.toSentence();
		}
		return null;
	}
	
	public String createHDM(SignalKModel model) {
		if (model.getValue(vessels_dot_self_dot + nav_courseOverGroundMagnetic) != null ) {
			HDMSentence sen = (HDMSentence) sf.createParser(TalkerId.II, SentenceId.HDM);
			sen.setHeading((double) model.getValue(vessels_dot_self_dot + nav_courseOverGroundMagnetic));
			
			return sen.toSentence();
		}
		return null;
	}
	public String createHDG(SignalKModel model) {
		if (model.getValue(vessels_dot_self_dot + nav_courseOverGroundMagnetic) != null ) {
			HDGSentence sen = (HDGSentence) sf.createParser(TalkerId.II, SentenceId.HDG);
			sen.setHeading((double) model.getValue(vessels_dot_self_dot + nav_courseOverGroundMagnetic));
			if(model.getValue(vessels_dot_self_dot + nav_magneticVariation)!=null)
				sen.setVariation((double) model.getValue(vessels_dot_self_dot + nav_magneticVariation));
			return sen.toSentence();
		}
		return null;
	}
	
	//MWV - Wind Speed and Angle
	public String createMWVApparent(SignalKModel model) {
		if (model.getValue(vessels_dot_self_dot + env_wind_angleApparent) != null
				|| model.getValue(vessels_dot_self_dot + env_wind_speedApparent)!=null) {
			MWVSentence sen = (MWVSentence) sf.createParser(TalkerId.II, SentenceId.MWV);
			sen.setStatus(DataStatus.ACTIVE);
			if(model.getValue(vessels_dot_self_dot + env_wind_angleApparent)!=null)
				sen.setAngle((double) model.getValue(vessels_dot_self_dot + env_wind_angleApparent));
			sen.setTrue(false);
			if(model.getValue(vessels_dot_self_dot + env_wind_speedApparent)!=null)
				sen.setSpeed((double) model.getValue(vessels_dot_self_dot + env_wind_speedApparent)*1.94384);
			sen.setSpeedUnit(Units.KNOT);
			return sen.toSentence();
		}
		return null;
	}
	public String createMWVTrue(SignalKModel model) {
		if (model.getValue(vessels_dot_self_dot + env_wind_directionTrue) != null
				|| model.getValue(vessels_dot_self_dot + env_wind_speedTrue)!=null) {
			MWVSentence sen = (MWVSentence) sf.createParser(TalkerId.II, SentenceId.MWV);
			sen.setStatus(DataStatus.ACTIVE);
			if(model.getValue(vessels_dot_self_dot + env_wind_directionTrue)!=null)
				sen.setAngle((double) model.getValue(vessels_dot_self_dot + env_wind_directionTrue));
			sen.setTrue(true);
			if(model.getValue(vessels_dot_self_dot + env_wind_speedTrue)!=null){
				double speed = (double) model.getValue(vessels_dot_self_dot + env_wind_speedTrue)*1.94384;
				if(speed<0.0)speed=0.0;
				sen.setSpeed((double) model.getValue(vessels_dot_self_dot + env_wind_speedTrue)*1.94384);
			}
			sen.setSpeedUnit(Units.KNOT);
			return sen.toSentence();
		}
		return null;
	}
	
	//depth below Transducer
	public String createDBT(SignalKModel model) {
		if (model.getValue(vessels_dot_self_dot + env_depth_belowTransducer) != null) {
			DBTSentence sen = (DBTSentence) sf.createParser(TalkerId.II, SentenceId.DBT);
			if(model.getValue(vessels_dot_self_dot + env_depth_belowTransducer)!=null){
				sen.setDepth((double) model.getValue(vessels_dot_self_dot + env_depth_belowTransducer));
				sen.setFeet((double) model.getValue(vessels_dot_self_dot + env_depth_belowTransducer)*3.28084);
				sen.setFathoms((double) model.getValue(vessels_dot_self_dot + env_depth_belowTransducer)*0.546806649);
			}
			
			return sen.toSentence();
		}
		return null;
	}
}
