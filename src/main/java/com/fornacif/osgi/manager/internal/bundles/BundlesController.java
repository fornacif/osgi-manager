package com.fornacif.osgi.manager.internal.bundles;

import java.io.IOException;
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

import com.fornacif.osgi.manager.service.ControllerFXMLLoader;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

@Component(name = "BundlesController", provide = { Pane.class } , configurationPolicy = ConfigurationPolicy.require)
public class BundlesController extends VBox implements Initializable {	
	private BundleContext bundleContext;
	
	private ControllerFXMLLoader controllerFXMLLoader;
	
	@FXML
	private TableView<BundleRow> bundlesTableView;
	
	@FXML
	private Label bundlesLabel;
	
	@Activate
	public void activate(BundleContext bundleContext, Map<String, ?> properties) {
		this.bundleContext = bundleContext;
		try {
			this.controllerFXMLLoader.loadFXML(this, properties);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Reference
	public void setControllerFXMLLoader(ControllerFXMLLoader controllerFXMLLoader) {
		this.controllerFXMLLoader = controllerFXMLLoader;
	}
	
	private void loadBundles() {
		Bundle[] bundles = bundleContext.getBundles();
		Collection<BundleRow> bundleRows = new ArrayList<>();
		for (Bundle bundle : bundles) {
			bundleRows.add(new BundleRow(bundle));
		}	
		bundlesTableView.getItems().setAll(bundleRows);
		ObservableList<TableColumn<BundleRow, ?>> sortOrder = bundlesTableView.getSortOrder();
		if (sortOrder.size() > 0) {
			TableColumn<BundleRow, ?> sortedTableColumn = sortOrder.get(0);
			sortOrder.clear();
			sortOrder.add(sortedTableColumn);
		}
		
		bundlesLabel.setText("Informations: " + bundles.length + " bundles");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadBundles();
	}
}
