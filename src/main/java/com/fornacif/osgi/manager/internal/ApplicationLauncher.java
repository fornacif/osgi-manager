package com.fornacif.osgi.manager.internal;

import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

@Component(name = "ApplicationLauncher", provide = {}, configurationPolicy = ConfigurationPolicy.require)
public class ApplicationLauncher extends Application implements Runnable {

	private static ControllerFactory componentControllerFactory;
	
	private final static String APPLICATION_FXML = "/fxml/application.fxml";
	private final static String TITLE_PROPERTIES = "title";

	private static String title;

	@Activate
	public void activate(Map<String, ?> properties) throws Exception {
		title = (String) properties.get(TITLE_PROPERTIES);
		new Thread(this).start();
	}
	
	@Reference
	public void bindComponentControllerFactory(ControllerFactory componentControllerFactory) {
		ApplicationLauncher.componentControllerFactory = componentControllerFactory;
	}
	
	@Override
	public void start(Stage stage) throws Exception {      
		FXMLLoader applicationLoader = new FXMLLoader(getClass().getResource(APPLICATION_FXML));
		applicationLoader.setClassLoader(getClass().getClassLoader());
		applicationLoader.setControllerFactory(ApplicationLauncher.componentControllerFactory);
		Scene scene = (Scene) applicationLoader.load();
        stage.setScene(scene);
        stage.setTitle(title);
		stage.show();
	}
	
	@Override
	public void stop() throws Exception {
		System.exit(0);
	}

	@Override
	public void run() {
		launch(ApplicationLauncher.class);
	}

}
