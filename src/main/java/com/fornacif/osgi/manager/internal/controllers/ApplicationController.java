package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
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

import com.fornacif.osgi.manager.internal.application.FXMLLoaderDelegator;
import com.fornacif.osgi.manager.internal.events.ProgressIndicatorEvent;
import com.fornacif.osgi.manager.internal.services.JMXService;

@Component(name = "ApplicationController", provide = { Pane.class, EventHandler.class }, configurationPolicy = ConfigurationPolicy.require)
public class ApplicationController extends VBox implements Initializable, EventHandler {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private BundleContext bundleContext;

	private Pane connectionController;

	private Pane notificationController;

	@FXML
	private HBox notificationBar;

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private TabPane tabPane;

	@FXML
	private StackPane connection;

	@FXML
	private ToggleButton consoleToggleButton;

	@FXML
	private ToggleButton connectionToggleButton;

	@Activate
	private void activate(BundleContext bundleContext) throws Exception {
		this.bundleContext = bundleContext;
	}

	@Reference(target = "(component.name=ConnectionController)")
	private void bindConnectionController(Pane connectionController) {
		this.connectionController = connectionController;
	}
	
	private void unbindConnectionController(final Pane connectionController) {
		if (connection != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					connection.getChildren().remove(connectionController);
				}
			});
		}
	}

	@Reference(target = "(component.name=NotificationController)")
	private void bindNotificationController(Pane notificationController) {
		this.notificationController = notificationController;
	}
	
	private void unbindNotificationController(final Pane notificationController) {
		if (notificationBar != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					notificationBar.getChildren().remove(notificationController);
				}
			});
		}	
	}

	@Reference(optional = true, dynamic = true)
	private void bindJmxConnectorService(JMXService jmxConnectorService) {
		consoleToggleButton.setDisable(false);
		consoleToggleButton.selectedProperty().set(true);
	}

	@SuppressWarnings("unused")
	private void unbindJmxConnectorService(JMXService jmxConnectorService) {
		consoleToggleButton.setDisable(true);
		connectionToggleButton.selectedProperty().set(true);
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		bundleContext.registerService(EventListenerHook.class, new FXMLLoaderDelegator(bundleContext), null);
		bundleContext.registerService(TabPane.class, tabPane, null);
		connection.getChildren().add(connectionController);
		notificationBar.getChildren().add(notificationController);
	}

	@Override
	public void handleEvent(final Event event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				switch (((ProgressIndicatorEvent) event).getType()) {
				case START:
					progressIndicator.setManaged(true);
					progressIndicator.setVisible(true);
					break;
				case STOP:
					progressIndicator.setManaged(false);
					progressIndicator.setVisible(false);
					break;
				}
				LOGGER.debug("Event {}[{}] received", event.getTopic(), ((ProgressIndicatorEvent) event).getType());
			}
		});
	}

}
