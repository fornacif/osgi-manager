package com.fornacif.osgi.manager.internal.application;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(name = "Configurator")
public class Configurator {
	private final static String LAUNCHER_CONFIGURATION_NAME = "Launcher";
	private final static String LAUNCHER_CONFIGURATION_FILENAME = "/configurations/Launcher.properties";

	private final static String BUNDLES_CONTROLLER_CONFIGURATION_NAME = "BundlesController";
	private final static String BUNDLES_CONTROLLER_CONFIGURATION_FILENAME = "/configurations/BundlesController.properties";

	private final static String SERVICES_CONTROLLER_CONFIGURATION_NAME = "ServicesController";
	private final static String SERVICES_CONTROLLER_CONFIGURATION_FILENAME = "/configurations/ServicesController.properties";

	private ConfigurationAdmin configurationAdmin;

	@Reference
	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	@Activate
	public void activate() throws IOException {
		configure(LAUNCHER_CONFIGURATION_NAME, LAUNCHER_CONFIGURATION_FILENAME);
		configure(BUNDLES_CONTROLLER_CONFIGURATION_NAME, BUNDLES_CONTROLLER_CONFIGURATION_FILENAME);
		configure(SERVICES_CONTROLLER_CONFIGURATION_NAME, SERVICES_CONTROLLER_CONFIGURATION_FILENAME);
	}

	private void configure(String fileName, String configurationName) throws IOException {
		Configuration configuration = this.configurationAdmin.getConfiguration(fileName, null);
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream(configurationName));
		configuration.update(new Hashtable(properties));
	}
}
