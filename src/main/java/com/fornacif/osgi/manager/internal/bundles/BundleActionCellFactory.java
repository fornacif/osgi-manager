package com.fornacif.osgi.manager.internal.bundles;

import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleActionCellFactory implements Callback<TableColumn<BundleTableRow, Bundle>, TableCell<BundleTableRow, Bundle>> {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private static final Image STOP_ICON_16 = new Image(BundleActionCellFactory.class.getResourceAsStream("/icons/stop-16x16.png"));
	private static final Image START_ICON_16 = new Image(BundleActionCellFactory.class.getResourceAsStream("/icons/start-16x16.png"));
	private static final Image UPDATE_ICON_16 = new Image(BundleActionCellFactory.class.getResourceAsStream("/icons/update-16x16.png"));
	private static final Image UNINSTALL_ICON_16 = new Image(BundleActionCellFactory.class.getResourceAsStream("/icons/uninstall-16x16.png"));

	private ObjectProperty<EventHandler<ActionEvent>> propertyOnAction = new SimpleObjectProperty<EventHandler<ActionEvent>>();

	@Override
	public TableCell<BundleTableRow, Bundle> call(TableColumn<BundleTableRow, Bundle> tableColumn) {
		TableCell<BundleTableRow, Bundle> cell = new TableCell<BundleTableRow, Bundle>() {
			@Override
			protected void updateItem(final Bundle bundle, final boolean empty) {
				if (bundle == null) {
					return;
				}
				
				final VBox vBox = new VBox();
				vBox.setSpacing(10);
				
				final HBox actionsHBox = new HBox();
				actionsHBox.setSpacing(5);
				
				final HBox confirmationHBox = new HBox();
				confirmationHBox.setSpacing(5);
				confirmationHBox.setVisible(false);
				confirmationHBox.setManaged(false);
				confirmationHBox.setAlignment(Pos.BOTTOM_LEFT);
				
				final FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), confirmationHBox);
				fadeInTransition.setFromValue(0.0);
				fadeInTransition.setToValue(1.0);

				final FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(100), confirmationHBox);
				fadeOutTransition.setFromValue(1.0);
				fadeOutTransition.setToValue(0.0);
				fadeOutTransition.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						confirmationHBox.setVisible(false);
						confirmationHBox.setManaged(false);
						actionsHBox.setDisable(false);
						onActionProperty().get().handle(actionEvent);
					}
		
				});
				
				Button startStopButton = new Button();
				startStopButton.setCursor(Cursor.HAND);
				if (bundle.getState() == Bundle.ACTIVE) {
					startStopButton.setTooltip(new Tooltip("Stop"));
					startStopButton.setGraphic(new ImageView(STOP_ICON_16));
				} else {
					startStopButton.setTooltip(new Tooltip("Start"));
					startStopButton.setGraphic(new ImageView(START_ICON_16));
				}

				startStopButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						try {
							if (bundle.getState() == Bundle.ACTIVE) {
								bundle.stop();
							} else {
								bundle.start();
							}
						} catch (BundleException e) {
							LOGGER.error("Error during bundle startup or stop", e);
						}
						onActionProperty().get().handle(actionEvent);
					}
				});
				
				Button updateButton = ButtonBuilder.create().tooltip(new Tooltip("Update")).cursor(Cursor.HAND).graphic(new ImageView(UPDATE_ICON_16)).onAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						try {
							bundle.update();
						} catch (BundleException e) {
							LOGGER.error("Error during bundle update", e);
						}
						onActionProperty().get().handle(actionEvent);
					}
				}).build();
	
				Button uninstallButton = ButtonBuilder.create().tooltip(new Tooltip("Uninstall")).cursor(Cursor.HAND).graphic(new ImageView(UNINSTALL_ICON_16)).onAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						confirmationHBox.setManaged(true);
						confirmationHBox.setVisible(true);
						actionsHBox.setDisable(true);	
						fadeInTransition.play();
					}
				}).build();

				Label confirmationLabel = new Label("Confirm?");
				
				Button validUninstallButton = ButtonBuilder.create().defaultButton(true).text("Yes").tooltip(new Tooltip("Uninstall")).cursor(Cursor.HAND).onAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						try {
							fadeOutTransition.play();
							bundle.uninstall();
						} catch (BundleException e) {
							LOGGER.error("Error during bundle uninstall", e);
						}
					}
				}).build();
				
				Button cancelUninstallButton = ButtonBuilder.create().text("No").tooltip(new Tooltip("Cancel")).cursor(Cursor.HAND).onAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						fadeOutTransition.play();
					}
				}).build();
				
				actionsHBox.getChildren().addAll(startStopButton, updateButton, uninstallButton);
				confirmationHBox.getChildren().addAll(confirmationLabel, validUninstallButton, cancelUninstallButton);
				
				vBox.getChildren().addAll(actionsHBox, confirmationHBox);
				setGraphic(vBox);
			}
		};
		return cell;
	}
	
	public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
		return propertyOnAction;
	}

	public final void setOnAction(EventHandler<ActionEvent> handler) {
		propertyOnAction.set(handler);
	}

	public final EventHandler<ActionEvent> getOnAction() {
		return propertyOnAction.get();
	}


}
