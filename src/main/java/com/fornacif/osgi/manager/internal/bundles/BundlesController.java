package com.fornacif.osgi.manager.internal.bundles;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;

@Component(name = "BundlesController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class BundlesController extends VBox implements Initializable {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private BundleContext bundleContext;

	@FXML
	private TableView<BundleTableRow> bundlesTableView;

	@FXML
	private Label bundlesLabel;

	@Activate
	public void activate(BundleContext bundleContext, Map<String, ?> properties) {
		this.bundleContext = bundleContext;
	}

	protected void loadBundles() {
		Bundle[] bundles = bundleContext.getBundles();
		Collection<BundleTableRow> bundleRows = new ArrayList<>();
		for (Bundle bundle : bundles) {
			bundleRows.add(new BundleTableRow(bundle));
		}
		bundlesTableView.getItems().setAll(bundleRows);
		
		ObservableList<TableColumn<BundleTableRow, ?>> sortOrder = bundlesTableView.getSortOrder();
		if (sortOrder.size() > 0) {
			TableColumn<BundleTableRow, ?> sortedTableColumn = sortOrder.get(0);
			sortOrder.clear();
			sortOrder.add(sortedTableColumn);
		}

		bundlesLabel.setText("Informations: " + bundles.length + " bundles");
	}
	
	@FXML
	protected void handleBundleAction(BundleActionEvent bundleActionEvent) {
		LOGGER.debug("{} {}", bundleActionEvent.getAction(), bundleActionEvent.getBundleId());
	}

	@Override
	public void initialize(URL arg0, ResourceBundle resourceBundle) {
		loadBundles();
	}
}
