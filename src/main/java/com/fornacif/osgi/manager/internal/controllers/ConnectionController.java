package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fornacif.osgi.manager.internal.application.FXMLLoaderDelegator;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;

@Component(name = "ConnectionController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class ConnectionController extends VBox implements Initializable {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Activate
	public void activate(BundleContext bundleContext) {
		
	}


	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		
	}

}
