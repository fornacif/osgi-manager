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

import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;

import com.fornacif.osgi.manager.services.JMXService;

@Component(name = "JMXService", configurationPolicy = ConfigurationPolicy.require)
public class JMXServiceImpl implements JMXService {

	private final String JMX_SERVICE_URL = "jmx.service.url";
	private final String BUNDLESTATE_BEAN_NAME = "bundestate.mbean.name";
	private final String FRAMEWORK_BEAN_NAME = "framework.mbean.name";
	
	private JMXServiceURL jmxServiceURL;
	private JMXConnector jmxConnector;
	private MBeanServerConnection mbeanServerConnection;

	private ObjectName bundleStateObjectName;
	private ObjectName frameworkObjectName;
	
	@Activate
	public void activate(Map<String, ?> properties) throws IOException, MalformedObjectNameException {
		this.jmxServiceURL = new JMXServiceURL((String) properties.get(JMX_SERVICE_URL));
		this.bundleStateObjectName = new ObjectName((String) properties.get(BUNDLESTATE_BEAN_NAME));
		this.frameworkObjectName = new ObjectName((String) properties.get(FRAMEWORK_BEAN_NAME));
		
		connect();
	}
	
	@Deactivate
	public void deactivate() throws IOException {
		close();
	}

	@Override
	public void connect() throws IOException {
		jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);
		mbeanServerConnection = jmxConnector.getMBeanServerConnection();
	}

	@Override
	public void close() throws IOException {
		jmxConnector.close();
	}

	@Override
	public BundleStateMBean getBundleStateMBean() {
		return JMX.newMBeanProxy(mbeanServerConnection, bundleStateObjectName, BundleStateMBean.class, true);
	}

	@Override
	public FrameworkMBean getFrameworkMBean() {
		return JMX.newMBeanProxy(mbeanServerConnection, frameworkObjectName, FrameworkMBean.class, true);
	}

}
