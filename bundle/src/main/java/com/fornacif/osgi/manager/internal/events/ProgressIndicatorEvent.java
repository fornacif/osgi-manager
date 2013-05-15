package com.fornacif.osgi.manager.internal.events;

import java.util.Map;

import org.osgi.service.event.Event;

public final class ProgressIndicatorEvent extends Event {
	
	private final static String TOPIC = "PROGRESS_INDICATOR";
	
	private final Type type;

	public enum Type {
		START, STOP
	};
	
	public ProgressIndicatorEvent(Type type) {
		super(TOPIC, (Map<String, ?>) null);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

}
