package com.fornacif.osgi.manager.internal.application;

import java.util.concurrent.Callable;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.services.AsynchronousCallerService;

@Component(name = "TabPaneManager", provide = {})
public class TabPaneManager {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private final String TAB_FXML_SERVICE_PROPERTY = "tab.fxml";
	private final String TAB_POSITION_SERVICE_PROPERTY = "tab.position";
	private final String TAB_SELECT_SERVICE_PROPERTY = "tab.select";
	private final String TAB_TEXT_SERVICE_PROPERTY = "tab.text";

	private TabPane tabPane;
	private ServiceTracker<Pane, Tab> paneTracker;
	private AsynchronousCallerService asynchronousCallerService;

	@Reference
	public void bindTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}
	
	@Reference
	public void bindAsynchronousCallerService(AsynchronousCallerService asynchronousCallerService) {
		this.asynchronousCallerService = asynchronousCallerService;
	}

	@Activate
	public void activate(final BundleContext bundleContext) {
		paneTracker = new ServiceTracker<Pane, Tab>(bundleContext, Pane.class, null) {
			@Override
			public Tab addingService(ServiceReference<Pane> reference) {
				Pane paneController = bundleContext.getService(reference);
				String fxml = (String) reference.getProperty(TAB_FXML_SERVICE_PROPERTY);
				String position = (String) reference.getProperty(TAB_POSITION_SERVICE_PROPERTY);
				String text = (String) reference.getProperty(TAB_TEXT_SERVICE_PROPERTY);
				String select = (String) reference.getProperty(TAB_SELECT_SERVICE_PROPERTY);
				Bundle bundle = reference.getBundle();
				Tab tab = addTab(paneController, bundle, fxml, position, select, text);
				return tab;
			}

			@Override
			public void modifiedService(ServiceReference<Pane> reference, Tab tab) {
				removedService(reference, tab);
				addingService(reference);
			}

			@Override
			public void removedService(ServiceReference<Pane> reference, Tab tab) {
				removeTab(tab);
			}
		};
		
		try {
			asynchronousCallerService.callAsynchronously(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					paneTracker.open();
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Deactivate
	public void deactivate() {
		paneTracker.close();
	}

	private Tab addTab(final Pane controller, final Bundle bundle, final String fxml, final String position, final String select, final String text) {
		try {
			loadFXML(controller, bundle, fxml);
		} catch (Exception e) {
			LOGGER.error("Error when loading FXML", e);
		}
		
		final Tab tab = new Tab();
		tab.setText(text);
		tab.setContent(controller);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ObservableList<Tab> tabs = tabPane.getTabs();

				Integer positionValue = Integer.valueOf(0);
				if (position != null && tabs.size() >= Integer.valueOf(position)) {
					positionValue = Integer.valueOf(position);
				}

				tabs.add(Integer.valueOf(positionValue), tab);

				if (select != null && Boolean.valueOf(select)) {
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

	private void loadFXML(final Pane controller, Bundle bundle, String fxml) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(bundle.getResource(fxml));
		fxmlLoader.setRoot(controller);
		fxmlLoader.setController(controller);
		fxmlLoader.load();
	}

}
