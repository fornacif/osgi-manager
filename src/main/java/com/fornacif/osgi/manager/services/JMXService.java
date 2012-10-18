package com.fornacif.osgi.manager.services;

import java.io.IOException;

import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;

public interface JMXService {
	void connect() throws IOException;
	void close() throws IOException;
	
	BundleStateMBean getBundleStateMBean();
	FrameworkMBean getFrameworkMBean();
}
