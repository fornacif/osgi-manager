package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;

@Component(name = "ServicesController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class ServicesController extends VBox implements Initializable {

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		loadServices();
	}
	
	private void loadServices() {

	}

	
}
