package com.fornacif.osgi.manager.internal.application;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

@Component(name = "TabPaneManager")
public class TabPaneManager {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private final static String PANE_SERVICE_FILTER = "(&(" + Constants.OBJECTCLASS + "=" + Pane.class.getName() + ")(" + OSGiManagerConstants.TAB_TEXT_PROPERTY + "=*))";

	private TabPane tabPane;
	private ServiceTracker<Pane, Tab> paneTracker;

	@Reference
	private void bindTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}

	@Activate
	private void activate(final BundleContext bundleContext) throws InvalidSyntaxException {
		paneTracker = new ServiceTracker<Pane, Tab>(bundleContext, bundleContext.createFilter(PANE_SERVICE_FILTER), null) {
			@Override
			public Tab addingService(ServiceReference<Pane> serviceReference) {
				Pane controller = bundleContext.getService(serviceReference);
				Integer position = Integer.valueOf(String.valueOf(serviceReference.getProperty(OSGiManagerConstants.TAB_POSITION_PROPERTY)));
				String text = String.valueOf(serviceReference.getProperty(OSGiManagerConstants.TAB_TEXT_PROPERTY));
				Tab tab = addTab(controller, position, text);
				return tab;
			}

			@Override
			public void removedService(ServiceReference<Pane> reference, Tab tab) {
				removeTab(tab);
			}
		};
		paneTracker.open();
	}

	@Deactivate
	private void deactivate() {
		paneTracker.close();
	}

	private Tab addTab(final Pane controller, final Integer position, final String text) {
		final Tab tab = new Tab();
		tab.setText(text);
		tab.setContent(controller);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ObservableList<Tab> tabs = tabPane.getTabs();

				Integer positionValue = Integer.valueOf(0);
				if (position != null && tabs.size() >= position) {
					positionValue = Integer.valueOf(position);
				}

				tabs.add(Integer.valueOf(positionValue), tab);

				if (positionValue == 0) {
					tabPane.getSelectionModel().select(tab);
				}
			}
		});

		return tab;
	}

	private void removeTab(final Tab tab) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				tabPane.getTabs().remove(tab);
				tab.setContent(null);
			}
		});
	}

}
