package com.fornacif.osgi.manager.internal.configurations;

import com.fornacif.osgi.manager.internal.application.OSGiManagerConstants;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(name = "BundlesController")
public interface BundlesControllerConfiguration {
	@Meta.AD(name = OSGiManagerConstants.FXML_PROPERTY)
	String fxml();

	@Meta.AD(id = OSGiManagerConstants.TAB_POSITION_PROPERTY, name = OSGiManagerConstants.TAB_POSITION_PROPERTY)
	int tabPosition();

	@Meta.AD(id = OSGiManagerConstants.TAB_SELECTED_PROPERTY, name = OSGiManagerConstants.TAB_SELECTED_PROPERTY)
	boolean tabSelected();

	@Meta.AD(id = OSGiManagerConstants.TAB_TEXT_PROPERTY, name = OSGiManagerConstants.TAB_TEXT_PROPERTY)
	String tabText();
}
