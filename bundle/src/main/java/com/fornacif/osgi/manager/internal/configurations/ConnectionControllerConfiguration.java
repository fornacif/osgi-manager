package com.fornacif.osgi.manager.internal.configurations;

import com.fornacif.osgi.manager.internal.application.OSGiManagerConstants;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(name = "ConnectionController")
public interface ConnectionControllerConfiguration {
	@Meta.AD(name = OSGiManagerConstants.FXML_PROPERTY)
	String fxml();
	
	@Meta.AD(name = OSGiManagerConstants.CSS_PROPERTY)
	String css();
}
