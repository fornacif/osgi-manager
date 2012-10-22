package com.fornacif.osgi.manager.internal.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
import com.fornacif.osgi.manager.services.ResultCallable;
import com.fornacif.osgi.manager.services.ServiceCaller;

@Component(name = "ConnectionController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class ConnectionController extends VBox implements Initializable {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private ServiceCaller serviceCaller;

	private ConnectionService connectionService;

	private List<ConnectionModel> virtualMachines;

	private ConnectionModel lastConnection;

	private ConfigurationService configurationService;

	@FXML
	private TableView<ConnectionModel> virtualMachinesTableView;

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
		lastConnection.setConnected(true);
	}

	public void unbindJmxService(JMXService jmxService) {
		lastConnection.setConnected(false);
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		listVirtualMachines();
	}

	@FXML
	protected void executeAction(final ConnectionActionEvent connectionActionEvent) throws IOException {
		ConnectionModel connection = connectionActionEvent.getConnection();
		if (connectionActionEvent.getAction() == Action.CONNECT) {
			lastConnection = connection;
			serviceCaller.execute(connectionService.configureConnection(connection.getVirtualMachine()), null, null);
		} else {
			this.configurationService.removeConfiguration("JMXService");
		}

	}

	private void listVirtualMachines() {
		serviceCaller.execute(connectionService.listVirtualMachines(), listVirtualMachinesResult(), null);
	}

	private ResultCallable<List<ConnectionModel>> listVirtualMachinesResult() {
		return new ResultCallable<List<ConnectionModel>>() {
			@Override
			public Void call() throws Exception {
				virtualMachines = getResult();
				fillVirtualMachinesListView();
				return null;
			}
		};
	}

	private void fillVirtualMachinesListView() {
		virtualMachinesTableView.setItems(FXCollections.observableArrayList(virtualMachines));
		updateSort();
	}

	private void updateSort() {
		ObservableList<TableColumn<ConnectionModel, ?>> sortOrder = virtualMachinesTableView.getSortOrder();
		if (sortOrder.size() > 0) {
			TableColumn<ConnectionModel, ?> sortedTableColumn = sortOrder.get(0);
			sortOrder.clear();
			sortOrder.add(sortedTableColumn);
		}

	}

}
