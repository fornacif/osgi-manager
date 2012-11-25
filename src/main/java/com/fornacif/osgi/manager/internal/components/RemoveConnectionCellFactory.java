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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import com.fornacif.osgi.manager.internal.events.RemoveConnectionEvent;
import com.fornacif.osgi.manager.internal.models.ConnectionModel;

public class RemoveConnectionCellFactory implements Callback<TableColumn<ConnectionModel, ConnectionModel>, TableCell<ConnectionModel, ConnectionModel>> {

	private final Image DELETE_ICON_16 = new Image(BundleActionCellFactory.class.getResourceAsStream("/icons/delete-16x16.png"));

	private ObjectProperty<EventHandler<RemoveConnectionEvent>> propertyOnAction = new SimpleObjectProperty<EventHandler<RemoveConnectionEvent>>();

	@Override
	public TableCell<ConnectionModel, ConnectionModel> call(TableColumn<ConnectionModel, ConnectionModel> tableColumn) {
		TableCell<ConnectionModel, ConnectionModel> cell = new TableCell<ConnectionModel, ConnectionModel>() {
			@Override
			protected void updateItem(final ConnectionModel connectionModel, final boolean empty) {
				if (connectionModel == null) {
					return;
				}

				final HBox hBox = new HBox();
				hBox.setAlignment(Pos.CENTER);

				Button deleteButton = new Button();
				deleteButton.setCursor(Cursor.HAND);
				deleteButton.setTooltip(new Tooltip("Remove"));
				deleteButton.setGraphic(new ImageView(DELETE_ICON_16));

				deleteButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						onActionProperty().get().handle(new RemoveConnectionEvent(connectionModel));
					}
				});

				hBox.getChildren().addAll(deleteButton);
				setGraphic(hBox);
			}
		};
		return cell;
	}

	public final ObjectProperty<EventHandler<RemoveConnectionEvent>> onActionProperty() {
		return propertyOnAction;
	}

	public final void setOnAction(EventHandler<RemoveConnectionEvent> handler) {
		propertyOnAction.set(handler);
	}

	public final EventHandler<RemoveConnectionEvent> getOnAction() {
		return propertyOnAction.get();
	}

}
