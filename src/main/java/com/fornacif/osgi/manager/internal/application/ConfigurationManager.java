package com.fornacif.osgi.manager.internal.application;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationManager implements BundleActivator {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private final String CONFIGURATIONS_DIR = "/configurations";
	private final String CONFIGURATION_EXTENSION = ".properties";

	private BundleContext bundleContext;

	private ServiceTracker<ConfigurationAdmin, ConfigurationAdmin> configurationAdminTracker;

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		this.bundleContext = bundleContext;

		configurationAdminTracker = new ServiceTracker<ConfigurationAdmin, ConfigurationAdmin>(bundleContext, ConfigurationAdmin.class, null) {
			@Override
			public ConfigurationAdmin addingService(ServiceReference<ConfigurationAdmin> reference) {
				ConfigurationAdmin configurationAdmin = super.addingService(reference);
				try {
					configure(configurationAdmin);
				} catch (IOException e) {
					LOGGER.error("Error updating configurations", e);
				}
				return configurationAdmin;
			}
		};

		configurationAdminTracker.open();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		configurationAdminTracker.close();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void configure(ConfigurationAdmin configurationAdmin) throws IOException {
		Map<String, String> configurationNames = configurationNames();
		for (String configurationName : configurationNames.keySet()) {
			Configuration configuration = configurationAdmin.getConfiguration(configurationName);
			Properties properties = new Properties();
			properties.load(getClass().getResourceAsStream("/" + configurationNames.get(configurationName)));
			configuration.update(new Hashtable(properties));
			LOGGER.debug("Configuration " + configurationName + " created");
		}
	}

	private Map<String, String> configurationNames() {
		Map<String, String> configurationNames = new HashMap<>();
		BundleWiring bundleWiring = bundleContext.getBundle().adapt(BundleWiring.class);
		Collection<String> configurationPaths = bundleWiring.listResources(CONFIGURATIONS_DIR, "*" + CONFIGURATION_EXTENSION, BundleWiring.LISTRESOURCES_LOCAL);
		for (String configurationPath : configurationPaths) {
			String configurationFileName = new File(configurationPath).getName();
			String configurationName = configurationFileName.substring(0, configurationFileName.length() - CONFIGURATION_EXTENSION.length());
			configurationNames.put(configurationName, configurationPath);
		}
		return configurationNames;
	}

}
