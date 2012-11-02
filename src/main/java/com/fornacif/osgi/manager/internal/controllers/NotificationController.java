package com.fornacif.osgi.manager.internal.controllers;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;

@Component(name = "NotificationController", provide = { Pane.class, EventHandler.class }, configurationPolicy = ConfigurationPolicy.require)
public class NotificationController extends HBox implements EventHandler {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@FXML
	private Label notificationMessage;

	@Override
	public void handleEvent(final Event event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				TranslateTransition showNotification = new TranslateTransition(Duration.seconds(1), NotificationController.this);
				showNotification.setFromY(30.0);
				showNotification.setToY(0.0);
				
				TranslateTransition hideNotification = new TranslateTransition(Duration.seconds(1), NotificationController.this);
				hideNotification.setFromY(0.0);
				hideNotification.setToY(30.0);

				SequentialTransition sequence = new SequentialTransition(showNotification, new PauseTransition(Duration.seconds(2)), hideNotification);

				String message = (String) event.getProperty("MESSAGE");
				notificationMessage.setText(message);

				setManaged(true);
				setVisible(true);
				sequence.play();
				
				sequence.setOnFinished(new javafx.event.EventHandler<ActionEvent>() {	
					@Override
					public void handle(ActionEvent arg0) {
						setManaged(false);
						setVisible(false);
					}
				});	
			}
		});	
	}
}
