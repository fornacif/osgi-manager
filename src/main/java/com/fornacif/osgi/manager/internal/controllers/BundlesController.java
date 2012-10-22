package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.events.BundleActionEvent;
import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.BundleModel;
import com.fornacif.osgi.manager.internal.services.BundlesService;
import com.fornacif.osgi.manager.services.ResultCallable;
import com.fornacif.osgi.manager.services.ServiceCaller;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@Component(name = "BundlesController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class BundlesController extends VBox implements Initializable {
	
	@FXML
	private Label bundlesCountLabel;

	@FXML
	private TableView<BundleModel> bundlesTableView;
	
	@FXML
	private TextField bundleNameFilterTextField;
	
	private BundlesService bundlesService;
	
	private List<BundleModel> bundles = new ArrayList<>();
	
	private ServiceCaller serviceCaller;
	
	@Reference
	private void bindServiceCaller(ServiceCaller serviceCaller) {
		this.serviceCaller = serviceCaller;
	}

	@Reference
	private void bindBundlesService(BundlesService bundlesService) {
		this.bundlesService = bundlesService;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {			
		handleBundleNameFilter();
		listBundles();
	}

	@FXML
	protected void executeBundleAction(final BundleActionEvent bundleActionEvent) {
		Action action = bundleActionEvent.getAction();
		Long bundleId = bundleActionEvent.getBundleId();
		serviceCaller.execute(bundlesService.executeAction(action, bundleId), executeActionResult(), null);
	}

	private void listBundles() {
		serviceCaller.execute(bundlesService.listBundles(), listBundlesResult(), null);	
	}
	
	private ResultCallable<Void> executeActionResult() {
		return new ResultCallable<Void>() {
			@Override
			public Void call() throws Exception {
				listBundles();
				return null;
			}
		};
	}
	
	private ResultCallable<List<BundleModel>> listBundlesResult() {
		return new ResultCallable<List<BundleModel>>() {
			@Override
			public Void call() throws Exception {
				bundles = getResult();
				applyBundleNameFilter();
				return null;
			}
		};
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
		ObservableList<BundleModel> filteredBundles = FXCollections.observableArrayList(Collections2.filter(bundles, predicate));
		bundlesTableView.setItems(filteredBundles);
		bundlesCountLabel.setText("Bundles: "+ String.valueOf(filteredBundles.size()));
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
            @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	applyBundleNameFilter();
            }
        });
	}
}
