package com.fornacif.osgi.manager.internal.services;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.osgi.jmx.framework.PackageStateMBean;
import org.osgi.jmx.framework.ServiceStateMBean;

import com.fornacif.osgi.manager.internal.models.ConnectionModel;

public class JMXService {

	private final ConnectionModel connectionModel;

	private final BundleStateMBean bundleStateMBean;
	private final ServiceStateMBean serviceStateMBean;
	private final FrameworkMBean frameworkMBean;
	private PackageStateMBean packageStateMBean;
	private final RuntimeMXBean runtimeMXBean;

	public JMXService(MBeanServerConnection mbeanServerConnection, ConnectionModel connectionModel, ObjectName frameworkObjectName, ObjectName bundleStateObjectName,
			ObjectName serviceStateObjectName, ObjectName packageStateObjectName) throws Exception {
		this.connectionModel = connectionModel;
		this.bundleStateMBean = JMX.newMBeanProxy(mbeanServerConnection, bundleStateObjectName, BundleStateMBean.class, true);
		this.serviceStateMBean = JMX.newMBeanProxy(mbeanServerConnection, serviceStateObjectName, ServiceStateMBean.class, true);
		this.frameworkMBean = JMX.newMBeanProxy(mbeanServerConnection, frameworkObjectName, FrameworkMBean.class, true);
		this.packageStateMBean = JMX.newMBeanProxy(mbeanServerConnection, packageStateObjectName, PackageStateMBean.class, true);
		this.runtimeMXBean = JMX.newMBeanProxy(mbeanServerConnection, new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME), RuntimeMXBean.class, true);
	}

	public ConnectionModel getConnectionModel() {
		return connectionModel;
	}
	
	public FrameworkMBean getFrameworkMBean() {
		return frameworkMBean;
	}

	public BundleStateMBean getBundleStateMBean() {
		return bundleStateMBean;
	}

	public ServiceStateMBean getServiceStateMBean() {
		return serviceStateMBean;
	}

	public PackageStateMBean getPackageStateMBean() {
		return packageStateMBean;
	}

	public RuntimeMXBean getRuntimeMXBean() {
		return runtimeMXBean;
	}

}
