package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.configurations.BundlesControllerConfiguration;
import com.fornacif.osgi.manager.internal.events.BundleActionEvent;
import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.BundleModel;
import com.fornacif.osgi.manager.internal.models.Models;
import com.fornacif.osgi.manager.internal.services.BundlesService;
import com.fornacif.osgi.manager.internal.services.ModelsService;
import com.fornacif.osgi.manager.services.AsynchService;
import com.fornacif.osgi.manager.services.ServiceCaller;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@Component(name = "BundlesController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require, designate = BundlesControllerConfiguration.class)
public class BundlesController extends VBox implements Initializable {
	@FXML
	private TableView<BundleModel> bundlesTableView;

	@FXML
	private TextField bundleNameFilterTextField;

	private BundlesService bundlesService;

	private ServiceCaller serviceCaller;

	private ModelsService modelsService;

	public ModelsService getModelsService() {
		return modelsService;
	}

	@Reference
	private void bindServiceCaller(ServiceCaller serviceCaller) {
		this.serviceCaller = serviceCaller;
	}
	
	@Reference
	private void bindBundlesService(BundlesService bundlesService) {
		this.bundlesService = bundlesService;
	}
	
	@Reference
	private void bindModelsService (ModelsService modelsService) {
		this.modelsService = modelsService;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		modelsService.modelsProperty().addListener(new ChangeListener<Models>(){
			@Override
			public void changed(ObservableValue<? extends Models> observable, Models oldValue, Models newValue) {
				bundlesTableView.setItems(newValue.getBundles());
				handleBundleNameFilter();
			}});
	}

	@FXML
	protected void executeBundleAction(final BundleActionEvent bundleActionEvent) {
		serviceCaller.execute(new AsynchService<Void>() {
			@Override
			public Void call() throws Exception {
				Action action = bundleActionEvent.getAction();
				Long bundleId = bundleActionEvent.getBundleId();
				bundlesService.executeAction(action, bundleId);
				return null;
			}

			@Override
			public void succeeded(Void result) {
				listBundles();
			}
		}, true, true);
	}

	private void listBundles() {
		serviceCaller.execute(new AsynchService<Models>() {
			@Override
			public Models call() throws Exception {
				return modelsService.buildModels();
			}

			@Override
			public void succeeded(Models result) {
				modelsService.setModels(result);
				applyBundleNameFilter();
			}
		}, true, true);
	}

	private void applyBundleNameFilter() {
		Predicate<BundleModel> predicate = new Predicate<BundleModel>() {
			public boolean apply(BundleModel bundleModel) {
				if (!bundleNameFilterTextField.getText().equals("")) {
					return bundleModel.getName().toUpperCase().contains(bundleNameFilterTextField.getText().toUpperCase());
				}
				return true;
			}
		};
		ObservableList<BundleModel> filteredBundles = FXCollections.observableArrayList(Collections2.filter(modelsService.getModels().getBundles(), predicate));
		bundlesTableView.setItems(filteredBundles);
		updateSort();
	}

	private void updateSort() {
		ObservableList<TableColumn<BundleModel, ?>> sortOrder = bundlesTableView.getSortOrder();
		if (sortOrder.size() > 0) {
			TableColumn<BundleModel, ?> sortedTableColumn = sortOrder.get(0);
			sortOrder.clear();
			sortOrder.add(sortedTableColumn);
		}
	}

	private void handleBundleNameFilter() {
		bundleNameFilterTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				applyBundleNameFilter();
			}
		});
	}
}
