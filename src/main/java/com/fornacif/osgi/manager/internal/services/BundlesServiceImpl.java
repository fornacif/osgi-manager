package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularData;

import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.BundleModel;
import com.fornacif.osgi.manager.services.BundlesService;
import com.fornacif.osgi.manager.services.JMXService;

@Component
public class BundlesServiceImpl implements BundlesService {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private JMXService jmxService;

	@Reference
	public void bindJmxConnectorService(JMXService jmxConnectorService) throws MalformedObjectNameException {
		this.jmxService = jmxConnectorService;
	}

	@Override
	public List<BundleModel> listBundles() throws IOException {
		BundleStateMBean bundleStateMBean = jmxService.getBundleStateMBean();
		TabularData bundles = bundleStateMBean.listBundles();
		return buildModel(bundles);
	}

	@Override
	public void executeAction(Action action, Long bundleId) throws IOException {
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
	
	private List<BundleModel> buildModel(TabularData bundleData) {
		List<BundleModel> bundles = new ArrayList<>();
		Collection<CompositeDataSupport> values = (Collection<CompositeDataSupport>) bundleData.values();
		for (CompositeDataSupport compositeDataSupport : values) {
			Long id = (Long) compositeDataSupport.get(BundleStateMBean.IDENTIFIER);
			String symbolicName = (String) compositeDataSupport.get(BundleStateMBean.SYMBOLIC_NAME);
			String version = (String) compositeDataSupport.get(BundleStateMBean.VERSION);
			String state = (String) compositeDataSupport.get(BundleStateMBean.STATE);
			Integer startLevel = (Integer) compositeDataSupport.get(BundleStateMBean.START_LEVEL);
			
			BundleModel bundleModel = new BundleModel();
			bundleModel.setId(id);
			bundleModel.setSymbolicName(symbolicName);
			bundleModel.setVersion(version);
			bundleModel.setState(state);
			bundleModel.setStartLevel(startLevel);
			
			bundles.add(bundleModel);
		}
		return bundles;
	}

}
