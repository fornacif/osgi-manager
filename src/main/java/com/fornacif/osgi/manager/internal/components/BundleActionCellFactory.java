package com.fornacif.osgi.manager.internal.components;

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

import com.fornacif.osgi.manager.internal.events.BundleActionEvent;
import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.BundleModel;

public class BundleActionCellFactory implements Callback<TableColumn<BundleModel, BundleModel>, TableCell<BundleModel, BundleModel>> {
	
	private final Image STOP_ICON_16 = new Image(BundleActionCellFactory.class.getResourceAsStream("/icons/stop-16x16.png"));
	private final Image START_ICON_16 = new Image(BundleActionCellFactory.class.getResourceAsStream("/icons/start-16x16.png"));
	private final Image UPDATE_ICON_16 = new Image(BundleActionCellFactory.class.getResourceAsStream("/icons/update-16x16.png"));
	private final Image UNINSTALL_ICON_16 = new Image(BundleActionCellFactory.class.getResourceAsStream("/icons/uninstall-16x16.png"));

	private ObjectProperty<EventHandler<BundleActionEvent>> propertyOnAction = new SimpleObjectProperty<EventHandler<BundleActionEvent>>();

	@Override
	public TableCell<BundleModel, BundleModel> call(TableColumn<BundleModel, BundleModel> tableColumn) {
		TableCell<BundleModel, BundleModel> cell = new TableCell<BundleModel, BundleModel>() {
			@Override
			protected void updateItem(final BundleModel bundle, final boolean empty) {
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
				
				Button startStopButton = new Button();
				startStopButton.setCursor(Cursor.HAND);
				if (bundle.getState().equals("ACTIVE")) {
					startStopButton.setTooltip(new Tooltip("Stop"));
					startStopButton.setGraphic(new ImageView(STOP_ICON_16));
				} else {
					startStopButton.setTooltip(new Tooltip("Start"));
					startStopButton.setGraphic(new ImageView(START_ICON_16));
				}

				startStopButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						if (bundle.getState().equals("ACTIVE")) {
							onActionProperty().get().handle(new BundleActionEvent(Action.STOP, bundle.getId()));
						} else {
							onActionProperty().get().handle(new BundleActionEvent(Action.START, bundle.getId()));
						}
					}
				});
				
				Button updateButton = ButtonBuilder.create().tooltip(new Tooltip("Update")).cursor(Cursor.HAND).graphic(new ImageView(UPDATE_ICON_16)).onAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						System.out.println(actionEvent.getTarget());
						onActionProperty().get().handle(new BundleActionEvent(Action.UPDATE, bundle.getId()));
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
						confirmationHBox.setVisible(false);
						confirmationHBox.setManaged(false);
						actionsHBox.setDisable(false);
						onActionProperty().get().handle(new BundleActionEvent(Action.UNINSTALL, bundle.getId()));
					}
				}).build();
				
				Button cancelUninstallButton = ButtonBuilder.create().text("No").tooltip(new Tooltip("Cancel")).cursor(Cursor.HAND).onAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						confirmationHBox.setVisible(false);
						confirmationHBox.setManaged(false);
						actionsHBox.setDisable(false);
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
	
	public final ObjectProperty<EventHandler<BundleActionEvent>> onActionProperty() {
		return propertyOnAction;
	}

	public final void setOnAction(EventHandler<BundleActionEvent> handler) {
		propertyOnAction.set(handler);
	}

	public final EventHandler<BundleActionEvent> getOnAction() {
		return propertyOnAction.get();
	}


}
