package com.fornacif.osgi.manager.internal.services;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(name = "ConfigurationService", provide=ConfigurationService.class, immediate=true)
public class ConfigurationService {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private final String CONFIGURATIONS_DIR = "/configurations/load";
	private final String CONFIGURATION_EXTENSION = ".properties";
	private ConfigurationAdmin configurationAdmin;

	@Reference
	public void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	@Activate
	public void activate(final BundleContext bundleContext) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					configure(bundleContext);
				} catch (IOException e) {
					LOGGER.error("Error loading configuration", e);
				}		
			}
		}, "OSGi Manager Configurator").start();
		
	}
	
	public void configure(String configurationPath) throws IOException {
		configure(configurationPath, null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void configure(String configurationPath, Map<String, ?> additionalProperties) throws IOException {
		String configurationFileName = new File(configurationPath).getName();
		String configurationName = configurationFileName.substring(0, configurationFileName.length() - CONFIGURATION_EXTENSION.length());
		Configuration configuration = this.configurationAdmin.getConfiguration(configurationName, null);
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/" + configurationPath));
		if (additionalProperties != null) {			
			properties.putAll(additionalProperties);
		}
		configuration.update(new Hashtable(properties));
		LOGGER.debug("Configuration " + configurationName + " created");
	}
	
	private void configure(BundleContext bundleContext) throws IOException {
		BundleWiring bundleWiring = bundleContext.getBundle().adapt(BundleWiring.class);
		Collection<String> configurations = bundleWiring.listResources(CONFIGURATIONS_DIR, "*" + CONFIGURATION_EXTENSION, BundleWiring.LISTRESOURCES_LOCAL);
		for (String configuration : configurations) {
			configure(configuration);
		}
	}

	public void removeConfiguration(String configurationName) throws IOException {
		Configuration configuration = this.configurationAdmin.getConfiguration(configurationName);
		configuration.delete();
	}
	

}
