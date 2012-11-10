package com.fornacif.osgi.manager.internal.events;

import java.util.Map;

import org.osgi.service.event.Event;

public final class NotificationEvent extends Event {
	
	private final static String TOPIC = "NOTIFICATION";
	
	private final Level level;
	
	private final String message;

	public enum Level {
		INFO, ERROR
	};
	
	public NotificationEvent(Level level, String message) {
		super(TOPIC, (Map<String, ?>) null);
		this.level = level;
		this.message = message;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public String getMessage() {
		return message;
	}

}
