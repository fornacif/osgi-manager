package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;

import com.fornacif.osgi.manager.constants.EventAdminTopics;

@Component(name = "ApplicationController", provide = { VBox.class, EventHandler.class }, configurationPolicy = ConfigurationPolicy.require)
public class ApplicationController extends VBox implements Initializable, EventHandler {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private BundleContext bundleContext;
	
	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private TabPane tabPane;

	@Activate
	public void activate(BundleContext bundleContext) throws Exception {
		this.bundleContext = bundleContext;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		bundleContext.registerService(TabPane.class, tabPane, null);
	}

	@Override
	public void handleEvent(final Event event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String topic = event.getTopic();
				LOGGER.debug("Event {} received", topic);
				
				switch (topic) {
				case EventAdminTopics.PROGRESS_INDICATOR_START:
					progressIndicator.setManaged(true);
					progressIndicator.setVisible(true);
					break;
				case EventAdminTopics.PROGRESS_INDICATOR_STOP:
					progressIndicator.setManaged(false);
					progressIndicator.setVisible(false);
					break;
				}		
			}
		});	
	}

}
