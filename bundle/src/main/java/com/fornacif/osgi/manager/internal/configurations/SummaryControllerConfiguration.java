package com.fornacif.osgi.manager.internal.configurations;

import com.fornacif.osgi.manager.internal.application.OSGiManagerConstants;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(name = "SummaryController")
public interface SummaryControllerConfiguration {
	@Meta.AD(name = OSGiManagerConstants.FXML_PROPERTY)
	String fxml();
	
	@Meta.AD(name = OSGiManagerConstants.CSS_PROPERTY)
	String css();

	@Meta.AD(id = OSGiManagerConstants.TAB_POSITION_PROPERTY, name = OSGiManagerConstants.TAB_POSITION_PROPERTY)
	int tabPosition();

	@Meta.AD(id = OSGiManagerConstants.TAB_TEXT_PROPERTY, name = OSGiManagerConstants.TAB_TEXT_PROPERTY)
	String tabText();
}
