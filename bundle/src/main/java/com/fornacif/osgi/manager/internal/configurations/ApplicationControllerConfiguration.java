package com.fornacif.osgi.manager.internal.configurations;

import com.fornacif.osgi.manager.internal.application.OSGiManagerConstants;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(name = "ApplicationController")
public interface ApplicationControllerConfiguration {
	@Meta.AD(id = OSGiManagerConstants.EVENT_TOPICS_PROPERTY, name = OSGiManagerConstants.EVENT_TOPICS_PROPERTY)
	String eventTopics();
}
