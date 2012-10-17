package com.fornacif.osgi.manager.internal.application;

import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;

@Component(name = "Launcher", configurationPolicy = ConfigurationPolicy.require)
public class Launcher extends Application {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private final static String APPLICATION_FXML = "/fxml/application.fxml";
	private final static String TITLE_PROPERTIES = "title";

	private static String title;
	private static BundleContext bundleContext;

	@Activate
	public void activate(BundleContext bundleContext, Map<String, ?> properties) throws Exception {
		Launcher.bundleContext = bundleContext;
		title = (String) properties.get(TITLE_PROPERTIES);
		new Thread(new Runnable() {
			@Override
			public void run() {
				launch(Launcher.class);	
			}
		}, "OSGi Manager Launcher").start();
		LOGGER.debug("Launching OSGi Manager");
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader applicationLoader = new FXMLLoader(getClass().getResource(APPLICATION_FXML));
		Scene scene = (Scene) applicationLoader.load();
		scene.getStylesheets().addAll(getClass().getResource("/css/application.css").toExternalForm());
		TabPane tabPane = (TabPane) scene.getRoot();
		registerTabPane(tabPane);
		stage.setScene(scene);
		stage.setTitle(title);
		stage.show();
	}
	
	private void registerTabPane(TabPane tabPane) {
		Launcher.bundleContext.registerService(TabPane.class, tabPane, null);
	}
	
	@Override
	public void stop() throws Exception {
		System.exit(0);
	}

}
