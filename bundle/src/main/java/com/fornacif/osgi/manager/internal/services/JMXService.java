package com.fornacif.osgi.manager.internal.services;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.osgi.jmx.framework.ServiceStateMBean;

import com.fornacif.osgi.manager.internal.models.ConnectionModel;

public class JMXService {

	private MBeanServerConnection mbeanServerConnection;
	private ConnectionModel connectionModel;
	private ObjectName bundleStateObjectName;
	private ObjectName serviceStateObjectName;
	private ObjectName frameworkObjectName;

	public JMXService(MBeanServerConnection mbeanServerConnection, ConnectionModel connectionModel) {
		this.mbeanServerConnection = mbeanServerConnection;
		this.connectionModel = connectionModel;
	}
	
	public ConnectionModel getConnectionModel() {
		return connectionModel;
	}
	
	public void setFrameworkObjectName(ObjectName frameworkObjectName) {
		this.frameworkObjectName = frameworkObjectName;
	}
	
	public void setBundleStateObjectName(ObjectName bundleStateObjectName) {
		 this.bundleStateObjectName = bundleStateObjectName;
	}
	
	public void setServiceStateObjectName(ObjectName serviceStateObjectName) {
		this.serviceStateObjectName = serviceStateObjectName;
	}

	public BundleStateMBean getBundleStateMBean() {
		return JMX.newMBeanProxy(mbeanServerConnection, bundleStateObjectName, BundleStateMBean.class, true);
	}
	
	public ServiceStateMBean getServiceStateMBean() {
		return JMX.newMBeanProxy(mbeanServerConnection, serviceStateObjectName, ServiceStateMBean.class, true);
	}

	public FrameworkMBean getFrameworkMBean() {
		return JMX.newMBeanProxy(mbeanServerConnection, frameworkObjectName, FrameworkMBean.class, true);
	}
	
	public OperatingSystemMXBean getOperatingSystemMXBean() throws MalformedObjectNameException {
		return JMX.newMBeanProxy(mbeanServerConnection, new ObjectName(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME), OperatingSystemMXBean.class, true);
	}
	
	public RuntimeMXBean getRuntimeMXBean() throws MalformedObjectNameException {
		return JMX.newMBeanProxy(mbeanServerConnection, new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME), RuntimeMXBean.class, true);
	}

}
