package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;
import java.util.Collection;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.management.MalformedObjectNameException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.BundleRow;
import com.fornacif.osgi.manager.internal.models.BundlesModel;
import com.fornacif.osgi.manager.services.BundlesService;
import com.fornacif.osgi.manager.services.JMXService;

@Component
public class BundlesServiceImpl implements BundlesService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private JMXService jmxService;
	
	private BundlesModel bundlesModel;
	
	@Reference
	public void bindBundlesModel(BundlesModel bundlesModel) {
		this.bundlesModel = bundlesModel;
	}

	@Reference
	public void bindJmxConnectorService(JMXService jmxConnectorService) throws MalformedObjectNameException {
		this.jmxService = jmxConnectorService;
	}

	@Override
	public void loadBundles() throws IOException {
		BundleStateMBean bundleStateMBean = jmxService.getBundleStateMBean();
		TabularData bundles = bundleStateMBean.listBundles();
		final ObservableList<BundleRow> observableList = buildObservableList(bundles);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				bundlesModel.getBundles().set(observableList);
				bundlesModel.getBundlesCount().set(String.valueOf(observableList.size()));		
			}
		});	
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

	@SuppressWarnings("unchecked")
	private ObservableList<BundleRow> buildObservableList(TabularData bundleData) {
		ObservableList<BundleRow> bundles = FXCollections.observableArrayList();
		Collection<CompositeData> bundlesCompositeData = (Collection<CompositeData>) bundleData.values();
		for (CompositeData bundleCompositeData : bundlesCompositeData) {
			BundleRow bundleModel = new BundleRow();

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
