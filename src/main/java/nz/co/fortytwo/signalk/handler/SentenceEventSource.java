package nz.co.fortytwo.signalk.handler;

import nz.co.fortytwo.signalk.model.SignalKModel;

public class SentenceEventSource {

	private String now;
	private String sourceRef;
	private SignalKModel model;

	public SentenceEventSource(String device, String now, SignalKModel model) {
		this.now=now;
		this.sourceRef=device; 
		this.model=model;
	}

	public String getNow() {
		return now;
	}

	public String getSourceRef() {
		return sourceRef;
	}

	public SignalKModel getModel() {
		return model;
	}

}
