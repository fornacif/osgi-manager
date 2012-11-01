package com.fornacif.osgi.manager.internal.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.events.ConnectionActionEvent;
import com.fornacif.osgi.manager.internal.events.ConnectionActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.ConnectionModel;
import com.fornacif.osgi.manager.internal.services.ConfigurationService;
import com.fornacif.osgi.manager.internal.services.ConnectionService;
import com.fornacif.osgi.manager.internal.services.JMXService;
import com.fornacif.osgi.manager.services.AsynchService;
import com.fornacif.osgi.manager.services.ServiceCaller;

@Component(name = "ConnectionController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class ConnectionController extends VBox implements Initializable {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private ServiceCaller serviceCaller;

	private ConnectionService connectionService;

	private List<ConnectionModel> connections;

	private ConnectionModel selectedConnection;

	private ConfigurationService configurationService;

	@FXML
	private TableView<ConnectionModel> localConnectionsTableView;

	@Reference
	private void bindServiceCaller(ServiceCaller serviceCaller) {
		this.serviceCaller = serviceCaller;
	}

	@Reference
	private void bindConnectionService(ConnectionService connectionService) {
		this.connectionService = connectionService;
	}

	@Reference
	private void bindConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@Reference(optional = true, dynamic = true)
	public void bindJmxService(JMXService jmxService) {
		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				selectedConnection.setConnected(true);
				fillVirtualMachinesListView();
			}
		});
		
	}

	public void unbindJmxService(JMXService jmxService) {
		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				selectedConnection.setConnected(false);
				fillVirtualMachinesListView();
			}
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		listVirtualMachines();
	}

	@FXML
	protected void executeAction(final ConnectionActionEvent connectionActionEvent) throws IOException {
		final ConnectionModel connection = connectionActionEvent.getConnection();
		if (connectionActionEvent.getAction() == Action.CONNECT) {
			if (selectedConnection != null && connection != selectedConnection) {
				selectedConnection.setConnected(false);
			}
			serviceCaller.execute(new AsynchService<Void>() {
				@Override
				public Void call() throws Exception {
					connectionService.configureConnection(connection.getVirtualMachine());
					return null;
				}
			});
		} else {
			this.configurationService.removeConfiguration("JMXService");
		}	
		selectedConnection = connection;
	}

	private void listVirtualMachines() {
		serviceCaller.execute(new AsynchService<List<ConnectionModel>>() {
			@Override
			public List<ConnectionModel> call() throws Exception {
				return connectionService.listConnections();
			}
			@Override
			public void succeeded(List<ConnectionModel> result) {
				connections = result;
				fillVirtualMachinesListView();
			}
		});
	}

	private void fillVirtualMachinesListView() {
		localConnectionsTableView.setItems(null); 
		localConnectionsTableView.layout();
		localConnectionsTableView.setItems(FXCollections.observableArrayList(connections));
		updateSort();
	}

	private void updateSort() {
		ObservableList<TableColumn<ConnectionModel, ?>> sortOrder = localConnectionsTableView.getSortOrder();
		if (sortOrder.size() > 0) {
			TableColumn<ConnectionModel, ?> sortedTableColumn = sortOrder.get(0);
			sortOrder.clear();
			sortOrder.add(sortedTableColumn);
		}

	}

}
