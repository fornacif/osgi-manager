package com.fornacif.osgi.manager.services;

import java.io.IOException;

import javax.management.MBeanServerConnection;

public interface JMXConnectorService {
	MBeanServerConnection connect() throws IOException;
	void close() throws IOException;
}
