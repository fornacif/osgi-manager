package com.fornacif.osgi.manager.internal.application;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
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

@Component(name = "Configurator")
public class Configurator {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private final String CONFIGURATIONS_DIR = "/configurations";
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
	
	private void configure(BundleContext bundleContext) throws IOException {
		BundleWiring bundleWiring = bundleContext.getBundle().adapt(BundleWiring.class);
		Collection<String> configurations = bundleWiring.listResources(CONFIGURATIONS_DIR, "*" + CONFIGURATION_EXTENSION, BundleWiring.LISTRESOURCES_LOCAL);
		for (String configuration : configurations) {
			configure(configuration);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void configure(String configurationPath) throws IOException {
		String configurationFileName = new File(configurationPath).getName();
		String configurationName = configurationFileName.substring(0, configurationFileName.length() - CONFIGURATION_EXTENSION.length());
		Configuration configuration = this.configurationAdmin.getConfiguration(configurationName, null);
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/" + configurationPath));
		configuration.update(new Hashtable(properties));
		LOGGER.debug("Configuration " + configurationName + " created");
	}
}
