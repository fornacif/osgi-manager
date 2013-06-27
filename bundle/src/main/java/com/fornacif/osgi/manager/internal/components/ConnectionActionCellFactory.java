package com.fornacif.osgi.manager.internal.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import com.fornacif.osgi.manager.internal.events.ConnectionActionEvent;
import com.fornacif.osgi.manager.internal.events.ConnectionActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.ConnectionModel;

public class ConnectionActionCellFactory implements Callback<TableColumn<ConnectionModel, ConnectionModel>, TableCell<ConnectionModel, ConnectionModel>> {

	private ObjectProperty<EventHandler<ConnectionActionEvent>> propertyOnAction = new SimpleObjectProperty<EventHandler<ConnectionActionEvent>>();

	@Override
	public TableCell<ConnectionModel, ConnectionModel> call(TableColumn<ConnectionModel, ConnectionModel> tableColumn) {
		TableCell<ConnectionModel, ConnectionModel> cell = new TableCell<ConnectionModel, ConnectionModel>() {
			@Override
			protected void updateItem(final ConnectionModel connectionModel, final boolean empty) {
				if (connectionModel == null) {
					return;
				}

				final HBox connectionHBox = new HBox();
				connectionHBox.setAlignment(Pos.BASELINE_CENTER);

				Button connectionButton = new Button();
				connectionButton.setId("connectionButton");
				connectionButton.setCursor(Cursor.HAND);
				if (connectionModel.isConnected()) {
					connectionButton.setText("Disconnect");
					connectionButton.setTooltip(new Tooltip("Disconnect"));
				} else {
					connectionButton.setText("Connect");
					connectionButton.setTooltip(new Tooltip("Connect"));
				}
				connectionButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						if (connectionModel.isConnected()) {
							onActionProperty().get().handle(new ConnectionActionEvent(Action.DISCONNECT, connectionModel));
						} else {
							onActionProperty().get().handle(new ConnectionActionEvent(Action.CONNECT, connectionModel));
						}

					}
				});

				connectionHBox.getChildren().add(connectionButton);

				setGraphic(connectionHBox);
			}
		};
		return cell;
	}

	public final ObjectProperty<EventHandler<ConnectionActionEvent>> onActionProperty() {
		return propertyOnAction;
	}

	public final void setOnAction(EventHandler<ConnectionActionEvent> handler) {
		propertyOnAction.set(handler);
	}

	public final EventHandler<ConnectionActionEvent> getOnAction() {
		return propertyOnAction.get();
	}

}
