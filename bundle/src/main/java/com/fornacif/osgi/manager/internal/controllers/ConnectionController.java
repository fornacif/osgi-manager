package com.fornacif.osgi.manager.internal.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.codehaus.jackson.type.TypeReference;
import org.osgi.service.event.EventAdmin;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.events.ConnectionActionEvent;
import com.fornacif.osgi.manager.internal.events.ConnectionActionEvent.Action;
import com.fornacif.osgi.manager.internal.events.NotificationEvent;
import com.fornacif.osgi.manager.internal.events.RemoveConnectionEvent;
import com.fornacif.osgi.manager.internal.models.ConnectionModel;
import com.fornacif.osgi.manager.internal.services.ConnectionService;
import com.fornacif.osgi.manager.internal.services.JMXService;
import com.fornacif.osgi.manager.internal.services.SystemPreferencesService;
import com.fornacif.osgi.manager.services.AsynchService;
import com.fornacif.osgi.manager.services.ServiceCaller;

@Component(name = "ConnectionController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class ConnectionController extends VBox implements Initializable {

	private ServiceCaller serviceCaller;

	private ConnectionService connectionService;

	private List<ConnectionModel> localConnections;

	private List<ConnectionModel> remoteConnections = new ArrayList<>();

	private ConnectionModel selectedConnection;

	private EventAdmin eventAdmin;
	
	private SystemPreferencesService systemPreferencesService;

	@FXML
	private TableView<ConnectionModel> localConnectionsTableView;

	@FXML
	private TableView<ConnectionModel> remoteConnectionsTableView;

	@FXML
	private TextField remoteConnectionName;

	@FXML
	private TextField remoteServiceURLTextField;
	
	@Activate
	private void loadPreferences() {
		try {
			remoteConnections = systemPreferencesService.load("remoteConnections", new TypeReference<List<ConnectionModel>>() {});
			if (remoteConnections == null) {
				remoteConnections = new ArrayList<>();
			}
		} catch (IOException e) {
			eventAdmin.sendEvent(new NotificationEvent(NotificationEvent.Level.ERROR, "Error during loading remote connections"));
		}
	}
	
	@Deactivate
	private void disconnect() throws IOException {
		connectionService.disconnect();
	}
	
	private void saveRemoteConnections() {
		try {
			systemPreferencesService.save("remoteConnections", remoteConnections);
		} catch (IOException e) {
			eventAdmin.sendEvent(new NotificationEvent(NotificationEvent.Level.ERROR, "Error during saving remote connections"));
		}
	}
	
	@Reference
	private void bindEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	@Reference
	private void bindServiceCaller(ServiceCaller serviceCaller) {
		this.serviceCaller = serviceCaller;
	}

	@Reference
	private void bindConnectionService(ConnectionService connectionService) {
		this.connectionService = connectionService;
	}
	
	@Reference
	public void bindSystemPreferencesService(SystemPreferencesService systemPreferencesService) {
		this.systemPreferencesService = systemPreferencesService;
	}

	@Reference(optional = true, dynamic = true)
	public void bindJmxService(JMXService jmxService) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				selectedConnection.setConnected(true);
				fillConnectionsTableViews();
			}
		});

	}

	public void unbindJmxService(JMXService jmxService) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				selectedConnection.setConnected(false);
				fillConnectionsTableViews();
			}
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		listLocalConnections();
	}

	@FXML
	private void executeAction(final ConnectionActionEvent connectionActionEvent) throws IOException {
		final ConnectionModel connection = connectionActionEvent.getConnection();
		if (connectionActionEvent.getAction() == Action.CONNECT) {
			if (selectedConnection != null && connection != selectedConnection) {
				selectedConnection.setConnected(false);
			}
			serviceCaller.execute(new AsynchService<Void>() {
				@Override
				public Void call() throws Exception {
					connectionService.connect(connection);
					return null;
				}
			});
		} else {
			serviceCaller.execute(new AsynchService<Void>() {
				@Override
				public Void call() throws Exception {
					connectionService.disconnect();
					return null;
				}
			});
		}
		selectedConnection = connection;
	}
	
	@FXML
	private void removeConnection(final RemoveConnectionEvent removeConnectionEvent) throws IOException {
		if (selectedConnection != null && selectedConnection.equals(removeConnectionEvent.getConnection())) {
			serviceCaller.execute(new AsynchService<Void>() {
				@Override
				public Void call() throws Exception {
					connectionService.disconnect();
					return null;
				}
			});
		}
		
		remoteConnections.remove(removeConnectionEvent.getConnection());
		saveRemoteConnections();
		fillConnectionsTableViews();
	}

	@FXML
	private void addRemoteConnection() {
		if (remoteConnectionName.getText().isEmpty()) {
			eventAdmin.sendEvent(new NotificationEvent(NotificationEvent.Level.ERROR, "Connection name cannot be empty"));
			return;
		}
		if (remoteServiceURLTextField.getText().isEmpty()) {
			eventAdmin.sendEvent(new NotificationEvent(NotificationEvent.Level.ERROR, "Connection JMX URL cannot be empty"));
			return;
		}
		
		ConnectionModel connectionModel = new ConnectionModel();
		connectionModel.setName(remoteConnectionName.getText());
		connectionModel.setUrl(remoteServiceURLTextField.getText());
		if (!remoteConnections.contains(connectionModel)) {
			remoteConnections.add(connectionModel);
			fillConnectionsTableViews();
			resetRemoteConnectionForm();
			saveRemoteConnections();
		} else {
			eventAdmin.sendEvent(new NotificationEvent(NotificationEvent.Level.ERROR, "Remote connection with the same name already exists"));
		}
	}

	private void resetRemoteConnectionForm() {
		remoteConnectionName.setText("");
	}

	private void listLocalConnections() {
		serviceCaller.execute(new AsynchService<List<ConnectionModel>>() {
			@Override
			public List<ConnectionModel> call() throws Exception {
				return connectionService.listLocalConnections();
			}

			@Override
			public void succeeded(List<ConnectionModel> result) {
				localConnections = result;
				fillConnectionsTableViews();
			}
		});
	}

	private void fillConnectionsTableViews() {
		fillConnectionsTableView(localConnectionsTableView, localConnections);
		fillConnectionsTableView(remoteConnectionsTableView, remoteConnections);
	}

	private void fillConnectionsTableView(TableView<ConnectionModel> connectionsTableView, List<ConnectionModel> connections) {
		connectionsTableView.setItems(null);
		connectionsTableView.layout();
		connectionsTableView.setItems(FXCollections.observableArrayList(connections));
		updateSort(connectionsTableView);
	}

	private void updateSort(TableView<ConnectionModel> connectionsTableView) {
		ObservableList<TableColumn<ConnectionModel, ?>> sortOrder = connectionsTableView.getSortOrder();
		if (sortOrder.size() > 0) {
			TableColumn<ConnectionModel, ?> sortedTableColumn = sortOrder.get(0);
			sortOrder.clear();
			sortOrder.add(sortedTableColumn);
		}
	}

}