package com.fornacif.osgi.manager.internal.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.events.BundleActionEvent;
import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.BundleModel;
import com.fornacif.osgi.manager.services.BundlesService;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@Component(name = "BundlesController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class BundlesController extends VBox implements Initializable {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private BundlesService bundlesService;

	@FXML
	private Label bundlesCountLabel;

	@FXML
	private TableView<BundleModel> bundlesTableView;
	
	@FXML
	private TextField bundleNameFilterTextField;
	
	private List<BundleModel> unfilteredBundles = new ArrayList<>();

	@Reference
	public void bindBundlesService(BundlesService bundlesService) {
		this.bundlesService = bundlesService;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		listBundles();
		handleBundleNameFilter();	
	}

	@FXML
	protected void executeBundleAction(BundleActionEvent bundleActionEvent) throws IOException {
		Action action = bundleActionEvent.getAction();
		Long bundleId = bundleActionEvent.getBundleId();
		bundlesService.executeAction(action, bundleId);
		listBundles();
	}
	
	private void listBundles() {
		try {
			List<BundleModel> bundles = bundlesService.listBundles();
			unfilteredBundles.clear();
			unfilteredBundles.addAll(bundles);
			filterBundleName();
			bundlesCountLabel.setText(String.valueOf(bundles.size()));
		} catch (IOException e) {
			LOGGER.error("Error during loading bundles", e);
		}		
	}

	private void filterBundleName() {
		Predicate<BundleModel> predicate = new Predicate<BundleModel>() {
			public boolean apply(BundleModel bundleModel) {
				if (!bundleNameFilterTextField.getText().equals("")) {
					return bundleModel.getName().toUpperCase().contains(bundleNameFilterTextField.getText().toUpperCase());
				} 
				return true;	
			}
		};
		Collection<BundleModel> filteredBundles = Collections2.filter(unfilteredBundles, predicate);
		updateTableViewContent(filteredBundles);
	}
	
	private void updateTableViewContent(Collection<BundleModel> filteredBundles) {
		bundlesTableView.getItems().setAll(filteredBundles);
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
            	filterBundleName();
            }
        });
	}
}
