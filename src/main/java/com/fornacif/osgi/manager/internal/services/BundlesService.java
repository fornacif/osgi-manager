package com.fornacif.osgi.manager.internal.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.BundleModel;

@Component(name="BundlesService", provide=BundlesService.class)
public class BundlesService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private JMXService jmxService;

	@Reference
	private void bindJmxConnectorService(JMXService jmxConnectorService) {
		this.jmxService = jmxConnectorService;
	}

	public Callable<List<BundleModel>> listBundles() {
		return new Callable<List<BundleModel>>() {
			@Override
			public List<BundleModel> call() throws Exception {
				BundleStateMBean bundleStateMBean = jmxService.getBundleStateMBean();
				TabularData bundles = bundleStateMBean.listBundles();
				return listBundles(bundles);
			}
		};
	}

	public Callable<Void> executeAction(final Action action, final Long bundleId) {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
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
				return null;
			}
		};
		
	}

	@SuppressWarnings("unchecked")
	private List<BundleModel> listBundles(TabularData bundleData) {
		List<BundleModel> bundles = new ArrayList<>();
		Collection<CompositeData> bundlesCompositeData = (Collection<CompositeData>) bundleData.values();
		for (CompositeData bundleCompositeData : bundlesCompositeData) {
			BundleModel bundleModel = new BundleModel();

			Long id = (Long) bundleCompositeData.get(BundleStateMBean.IDENTIFIER);
			String symbolicName = (String) bundleCompositeData.get(BundleStateMBean.SYMBOLIC_NAME);
			String version = (String) bundleCompositeData.get(BundleStateMBean.VERSION);
			String state = (String) bundleCompositeData.get(BundleStateMBean.STATE);
			Integer startLevel = (Integer) bundleCompositeData.get(BundleStateMBean.START_LEVEL);
			TabularData headers = (TabularData) bundleCompositeData.get(BundleStateMBean.HEADERS);

			bundleModel.setId(id);
			bundleModel.setSymbolicName(symbolicName);
			bundleModel.setVersion(version);
			bundleModel.setState(state);
			bundleModel.setStartLevel(startLevel);

			Collection<CompositeData> headersCompositeData = (Collection<CompositeData>) headers.values();
			for (CompositeData headerCompositeData : headersCompositeData) {
				if (headerCompositeData.containsValue("Bundle-Name")) {
					String name = (String) headerCompositeData.get("Value");
					bundleModel.setName(name);
				}
			}

			bundles.add(bundleModel);
		}
		return bundles;
	}

}