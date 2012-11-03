package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
public class NotificationController extends HBox implements EventHandler, Initializable {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private final BlockingQueue<Event> queue = new ArrayBlockingQueue<>(100);

	private final Semaphore semaphore = new Semaphore(1);

	@FXML
	private Label notificationMessage;

	@FXML
	private ImageView image;

	private SequentialTransition sequence;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		TranslateTransition showNotification = new TranslateTransition(Duration.millis(500), NotificationController.this);
		showNotification.setFromY(30.0);
		showNotification.setToY(0.0);
		
		TranslateTransition hideNotification = new TranslateTransition(Duration.millis(500), NotificationController.this);
		hideNotification.setFromY(0.0);
		hideNotification.setToY(30.0);

		sequence = new SequentialTransition(showNotification, new PauseTransition(Duration.seconds(2)), hideNotification);

		sequence.setOnFinished(new javafx.event.EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				setManaged(false);
				setVisible(false);
				semaphore.release();
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						final Event event = queue.take();
						semaphore.acquire();

						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								String type = (String) event.getProperty("TYPE");
								switch (type) {
								case "ERROR":
									image.setImage(new Image("/icons/error-16x16.png"));
									break;
								case "INFO":
									image.setImage(new Image("/icons/info-16x16.png"));
									break;
								}

								String message = (String) event.getProperty("MESSAGE");
								notificationMessage.setText(message);

								setManaged(true);
								setVisible(true);

								sequence.play();
							}
						});
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();
	}

	@Override
	public void handleEvent(final Event event) {
		try {
			queue.put(event);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOGGER.debug("Event {} received", event.getTopic());
	}

}
