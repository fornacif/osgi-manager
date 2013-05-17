package com.fornacif.osgi.manager.internal.services;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;

public class JMXService {

	private MBeanServerConnection mbeanServerConnection;
	private ObjectName bundleStateObjectName;
	private ObjectName frameworkObjectName;

	public JMXService(MBeanServerConnection mbeanServerConnection) {
		this.mbeanServerConnection = mbeanServerConnection;
	}
	
	public void setFrameworkObjectName(ObjectName frameworkObjectName) {
		this.frameworkObjectName = frameworkObjectName;
	}
	
	public void setBundleStateObjectName(ObjectName bundleStateObjectName) {
		 this.bundleStateObjectName = bundleStateObjectName;
	}

	public BundleStateMBean getBundleStateMBean() {
		return JMX.newMBeanProxy(mbeanServerConnection, bundleStateObjectName, BundleStateMBean.class, true);
	}

	public FrameworkMBean getFrameworkMBean() {
		return JMX.newMBeanProxy(mbeanServerConnection, frameworkObjectName, FrameworkMBean.class, true);
	}

}
