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

import com.fornacif.osgi.manager.internal.configurations.LauncherConfiguration;

@Component(name = "Launcher", configurationPolicy = ConfigurationPolicy.require, designate = LauncherConfiguration.class)
public class Launcher extends Application {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private static String fxml;
	private static String css;
	private static String title;

	private static Pane applicationController;

	private static BundleContext bundleContext;

	@Activate
	private void activate(final BundleContext bundleContext, Map<String, ?> properties) throws Exception {
		Launcher.bundleContext = bundleContext;
		fxml = String.valueOf(properties.get(OSGiManagerConstants.FXML_PROPERTY));
		css = String.valueOf(properties.get(OSGiManagerConstants.CSS_PROPERTY));
		title = String.valueOf(properties.get(OSGiManagerConstants.TITLE_PROPERTY));	
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
		bundleContext.getBundle(0).stop();
	}

	@Reference(target = "(component.name=ApplicationController)")
	private void bindApplicationController(Pane applicationController) {
		Launcher.applicationController = applicationController;
		new Thread(new Runnable() {
			@Override
			public void run() {
				launch(Launcher.class);
			}
		}, "OSGi Manager Launcher").start();
		LOGGER.debug("Launching OSGi Manager");
	}

}
