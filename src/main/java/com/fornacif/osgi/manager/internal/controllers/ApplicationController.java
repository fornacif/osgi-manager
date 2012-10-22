package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.constants.EventAdminTopics;
import com.fornacif.osgi.manager.internal.application.FXMLLoaderDelegator;

@Component(name = "ApplicationController", provide = { Pane.class, EventHandler.class }, configurationPolicy = ConfigurationPolicy.require)
public class ApplicationController extends VBox implements Initializable, EventHandler {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private BundleContext bundleContext;
	
	private Pane connectionController;
	
	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private TabPane tabPane;
	
	@FXML
	private ToggleGroup toggleGroup;
	
	@FXML
	private StackPane console;
	
	@FXML
	private VBox connection;

	@Activate
	public void activate(BundleContext bundleContext) throws Exception {
		this.bundleContext = bundleContext;
	}
	
	@Reference(target="(component.name=ConnectionController)")
	public void bindConnectionController(Pane connectionController) {
		this.connectionController = connectionController;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		bundleContext.registerService(EventListenerHook.class, new FXMLLoaderDelegator(bundleContext), null);
		
		bundleContext.registerService(TabPane.class, tabPane, null);
		
		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle selectedToggle) {
            	String selectedId = ((ToggleButton) selectedToggle).getId();
            	if ("consoleToggleButton".equals(selectedId)) {
            		console.setVisible(true);
            		console.setManaged(true);
            		connection.setVisible(false);
            		connection.setManaged(false);
            	} else {
            		connection.setVisible(true);
            		connection.setManaged(true);
            		console.setVisible(false);
            		console.setManaged(false);
            	}
            }
        });
		
		connection.getChildren().add(connectionController);	
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
