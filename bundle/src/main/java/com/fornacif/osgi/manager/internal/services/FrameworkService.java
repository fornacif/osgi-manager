package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;

import org.osgi.jmx.framework.FrameworkMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;

@Component(name = "FrameworkService", provide = FrameworkService.class)
public class FrameworkService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private JMXService jmxService;

	@Reference
	private void bindJmxConnectorService(JMXService jmxConnectorService) {
		this.jmxService = jmxConnectorService;
	}

	public void refreshBundlesAndWait(long[] bundleIdentifiers) throws IOException {
		FrameworkMBean frameworkMBean = jmxService.getFrameworkMBean();
		frameworkMBean.refreshBundlesAndWait(bundleIdentifiers);
	}

	public void executeAction(final Action action, final Long bundleId) throws IOException {
		FrameworkMBean mbeanProxy = jmxService.getFrameworkMBean();
		LOGGER.debug("{} {}", action, bundleId);
		switch (action) {
		case START:
			mbeanProxy.startBundle(bundleId);
			break;
		case STOP:
			mbeanProxy.stopBundle(bundleId);
			break;
		case UPDATE:
			mbeanProxy.updateBundle(bundleId);
			break;
		case UNINSTALL:
			mbeanProxy.uninstallBundle(bundleId);
			break;
		}
	}

}
