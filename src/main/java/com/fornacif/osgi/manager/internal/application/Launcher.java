package com.fornacif.osgi.manager.internal.application;

import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

@Component(name = "Launcher", configurationPolicy = ConfigurationPolicy.require)
public class Launcher extends Application {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private final String FXML = "fxml";
	private final String TITLE = "title";
	private final String CSS = "css";

	private static String fxml;
	private static String css;
	private static String title;
	
	private static Pane applicationController;

	@Activate
	private void activate(final BundleContext bundleContext, Map<String, ?> properties) throws Exception {
		fxml = (String) properties.get(FXML);
		css = (String) properties.get(CSS);
		title = (String) properties.get(TITLE);
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
		FXMLLoader applicationLoader = new FXMLLoader(getClass().getResource(fxml));
		applicationLoader.setClassLoader(getClass().getClassLoader());
		applicationLoader.setController(applicationController);
		applicationLoader.setRoot(applicationController);
		applicationController.getStylesheets().add(getClass().getResource(css).toExternalForm());
		applicationLoader.load();
		Scene scene = new Scene(applicationController);
		stage.setScene(scene);
		stage.setTitle(title);
		stage.show();
	}

	
	@Override
	public void stop() throws Exception {
		System.exit(0);
	}
	
	@Reference(target="(component.name=ApplicationController)")
	private void bindApplicationController(Pane applicationController) {
		Launcher.applicationController = applicationController;
	}

}
