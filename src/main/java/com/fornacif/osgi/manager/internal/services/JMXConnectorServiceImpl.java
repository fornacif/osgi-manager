package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;

import com.fornacif.osgi.manager.services.JMXConnectorService;

@Component(name = "JMXConnectorService", configurationPolicy = ConfigurationPolicy.require)
public class JMXConnectorServiceImpl implements JMXConnectorService {

	private final String JMX_SERVICE_URL = "jmx.service.url";
	private JMXServiceURL jmxServiceURL;
	private JMXConnector jmxConnector;
	
	@Activate
	public void activate(Map<String, ?> properties) throws MalformedURLException {
		jmxServiceURL = new JMXServiceURL((String) properties.get(JMX_SERVICE_URL));
	}

	@Override
	public MBeanServerConnection connect() throws IOException {
		jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);
		return jmxConnector.getMBeanServerConnection();
	}

	@Override
	public void close() throws IOException {
		jmxConnector.close();
	}

}
