package com.fornacif.osgi.manager.internal.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

@Component(name = "BundlesController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class BundlesController extends VBox implements Initializable {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private BundlesService bundlesService;

	@FXML
	private TableView<BundleModel> bundlesTableView;

	@FXML
	private Label bundlesLabel;
	
	@Reference
	public void bindBundlesService(BundlesService bundlesService) {
		this.bundlesService = bundlesService;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		loadBundles();
	}

	protected void loadBundles() {
		List<BundleModel> bundles = new ArrayList<>();
		try {
			bundles = bundlesService.listBundles();
		} catch (IOException e) {
			LOGGER.error("Error during loading bundles", e);
		}
		
		bundlesTableView.getItems().setAll(bundles);

		ObservableList<TableColumn<BundleModel, ?>> sortOrder = bundlesTableView.getSortOrder();
		if (sortOrder.size() > 0) {
			TableColumn<BundleModel, ?> sortedTableColumn = sortOrder.get(0);
			sortOrder.clear();
			sortOrder.add(sortedTableColumn);
		}

		bundlesLabel.setText("Informations: " + bundles.size() + " bundles");
	}

	@FXML
	protected void handleBundleAction(BundleActionEvent bundleActionEvent) throws IOException {
		Action action = bundleActionEvent.getAction();
		Long bundleId = bundleActionEvent.getBundleId();
		bundlesService.executeAction(action, bundleId);
		loadBundles();
	}
}
