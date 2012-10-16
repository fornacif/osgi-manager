package com.fornacif.osgi.manager.internal.application;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

@Component(name = "TabPaneManager", provide = {})
public class TabPaneManager {
	
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
				Tab tab = addTab(paneController, (String) reference.getProperty(TAB_TEXT_SERVICE_PROPERTY));
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
	
	private Tab addTab(Pane controller, String text) {
		final Tab tab = new Tab();
		tab.setText(text);
		tab.setContent(controller);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				tabPane.getTabs().add(tab);
			}
		});
		
		return tab;
	}
	
	private void removeTab(final Tab tab) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				tabPane.getTabs().remove(tab);
			}
		});	
	}

}
