package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.service.ControllerFXMLLoader;

@Component(name = "ServicesController", provide = { Pane.class } , configurationPolicy = ConfigurationPolicy.require)
public class ServicesController extends VBox implements Initializable {	
	private BundleContext bundleContext;
	
	private ControllerFXMLLoader controllerFXMLLoader;

	@Activate
	public void activate(BundleContext bundleContext, Map<String, ?> properties) {
		this.bundleContext = bundleContext;
		try {
			this.controllerFXMLLoader.loadFXML(this, properties);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Reference
	public void setControllerFXMLLoader(ControllerFXMLLoader controllerFXMLLoader) {
		this.controllerFXMLLoader = controllerFXMLLoader;
	}
	
	private void loadServices() {
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadServices();
	}
}
