package com.fornacif.osgi.manager.internal.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(name="ServicesService", provide=ServicesService.class)
public class ServicesService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private JMXService jmxService;

	@Reference
	private void bindJmxConnectorService(JMXService jmxConnectorService) {
		this.jmxService = jmxConnectorService;
	}

}
