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
	
	private final String APPLICATION_FXML = "/fxml/application.fxml";
	private final String TITLE_PROPERTIES = "title";

	private static String title;
	
	private static Pane applicationController;

	@Activate
	private void activate(final BundleContext bundleContext, Map<String, ?> properties) throws Exception {
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
		applicationLoader.setController(applicationController);
		applicationLoader.setRoot(applicationController);
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
