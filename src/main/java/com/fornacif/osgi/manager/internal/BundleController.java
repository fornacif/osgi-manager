package com.fornacif.osgi.manager.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

@Component(name = "BundleController", provide = { Controller.class })
public class BundleController implements Initializable, Controller {
	private BundleContext bundleContext;
	
	@FXML
	private TableView<BundleRow> bundleTableView;
	
	@FXML
	private Label bundleLabel;
	
	@Activate
	public void activate(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
	
	private void loadBundles() {
		Bundle[] bundles = bundleContext.getBundles();
		Collection<BundleRow> bundleRows = new ArrayList<>();
		for (Bundle bundle : bundles) {
			bundleRows.add(new BundleRow(bundle));
		}	
		bundleTableView.getItems().setAll(bundleRows);
		ObservableList<TableColumn<BundleRow, ?>> sortOrder = bundleTableView.getSortOrder();
		if (sortOrder.size() > 0) {
			TableColumn<BundleRow, ?> sortedTableColumn = sortOrder.get(0);
			sortOrder.clear();
			sortOrder.add(sortedTableColumn);
		}
		
		bundleLabel.setText("Informations: " + bundles.length + " bundles");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadBundles();
	}
}
