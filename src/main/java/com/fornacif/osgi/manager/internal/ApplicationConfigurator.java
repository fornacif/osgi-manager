package com.fornacif.osgi.manager.internal;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(name = "ApplicationConfigurator")
public class ApplicationConfigurator {
	private final static String CONFIGURATION_NAME = "ApplicationLauncher";
	private final static String CONFIGURATION_FILENAME = "/ApplicationLauncher.properties";
	
	private ConfigurationAdmin configurationAdmin;
	
	@Reference
	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}
	
	@Activate
	public void activate() throws IOException {
		Configuration configuration = this.configurationAdmin.getConfiguration(CONFIGURATION_NAME, null);
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream(CONFIGURATION_FILENAME));
		configuration.update(new Hashtable(properties));
	}
}
