package com.fornacif.osgi.manager.internal.application;

import java.io.IOException;

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

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

@Component(name = "TabPaneManager", provide = {})
public class TabPaneManager {
	
	private final static String TAB_FXML_SERVICE_PROPERTY = "tab.fxml";
	private final static String TAB_POSITION_SERVICE_PROPERTY = "tab.position";
	private final static String TAB_SELECT_SERVICE_PROPERTY = "tab.select";
	private final static String TAB_TEXT_SERVICE_PROPERTY = "tab.text";
	
	private TabPane tabPane;
	private ServiceTracker<Pane, Tab> controllerTracker;
	
	@Reference
	public void bindTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}
	
	@Activate
	public void activate(final BundleContext bundleContext) {
		controllerTracker = new ServiceTracker<Pane, Tab>(bundleContext, Pane.class, null) {
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
		controllerTracker.open();
	}
	
	@Deactivate
	public void deactivate() {
		controllerTracker.close();
	}
	
	private Tab addTab(final Pane controller, final Bundle bundle, final String fxml, final String position, final String select, final String text) {
		try {
			loadFXML(controller, bundle, fxml);
		} catch (IOException e) {
			e.printStackTrace();
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
	
	private void loadFXML(final Pane controller, Bundle bundle, String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(bundle.getResource(fxml));
		fxmlLoader.setRoot(controller);
		fxmlLoader.setController(controller);
		fxmlLoader.load();
	}

}
