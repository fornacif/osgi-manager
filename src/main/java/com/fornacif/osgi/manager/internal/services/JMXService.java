package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;

@Component(name = "JMXService", configurationPolicy = ConfigurationPolicy.require)
public class JMXService {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	public static final String JMX_SERVICE_URL = "jmx.service.url";
	private static final String BUNDLESTATE_BEAN_NAME = "bundestate.mbean.name";
	private static final String FRAMEWORK_BEAN_NAME = "framework.mbean.name";
	
	private JMXServiceURL jmxServiceURL;
	private JMXConnector jmxConnector;
	private MBeanServerConnection mbeanServerConnection;

	private ObjectName bundleStateObjectName;
	private ObjectName frameworkObjectName;

	private ServiceRegistration<JMXService> serviceRegistration;
	
	@Activate
	private void activate(BundleContext bundleContext, Map<String, ?> properties) throws IOException, MalformedObjectNameException {
		this.jmxServiceURL = new JMXServiceURL((String) properties.get(JMX_SERVICE_URL));
		this.bundleStateObjectName = new ObjectName((String) properties.get(BUNDLESTATE_BEAN_NAME));
		this.frameworkObjectName = new ObjectName((String) properties.get(FRAMEWORK_BEAN_NAME));
		
		jmxConnector = JMXConnectorFactory.connect(jmxServiceURL);
		mbeanServerConnection = jmxConnector.getMBeanServerConnection();
		
		if (mbeanServerConnection.isRegistered(frameworkObjectName)) {
			serviceRegistration = bundleContext.registerService(JMXService.class, this, null);
		} else {
			LOGGER.info("Connection not open due to missing OSGi MBeans");
		}
	}
	
	@Deactivate
	private void deactivate() throws IOException {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
		jmxConnector.close();
	}
	
	public BundleStateMBean getBundleStateMBean() {
		return JMX.newMBeanProxy(mbeanServerConnection, bundleStateObjectName, BundleStateMBean.class, true);
	}

	public FrameworkMBean getFrameworkMBean() {
		return JMX.newMBeanProxy(mbeanServerConnection, frameworkObjectName, FrameworkMBean.class, true);
	}

}
