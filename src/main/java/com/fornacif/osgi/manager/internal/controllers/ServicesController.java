package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.services.ServicesService;

@Component(name = "ServicesController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class ServicesController extends VBox implements Initializable {
	
	private ServicesService servicesService;
	
	@Reference
	private void bindServicesService(ServicesService servicesService) {
		this.servicesService = servicesService;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		loadServices();
	}
	
	private void loadServices() {

	}

	
}
