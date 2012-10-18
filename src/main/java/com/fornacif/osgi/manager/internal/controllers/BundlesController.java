package com.fornacif.osgi.manager.internal.controllers;

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
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
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

	private BundleContext bundleContext;
	
	private BundlesService bundlesService;

	@FXML
	private TableView<BundleModel> bundlesTableView;

	@FXML
	private Label bundlesLabel;

	@Activate
	public void activate(BundleContext bundleContext, Map<String, ?> properties) {
		this.bundleContext = bundleContext;
	}
	
	@Reference
	public void bindBundlesService(BundlesService bundlesService) {
		this.bundlesService = bundlesService;
	}

	protected void loadBundles() {
		Bundle[] bundles = bundleContext.getBundles();
		Collection<BundleModel> bundleRows = new ArrayList<>();
		for (Bundle bundle : bundles) {
			bundleRows.add(new BundleModel(bundle));
		}
		bundlesTableView.getItems().setAll(bundleRows);

		ObservableList<TableColumn<BundleModel, ?>> sortOrder = bundlesTableView.getSortOrder();
		if (sortOrder.size() > 0) {
			TableColumn<BundleModel, ?> sortedTableColumn = sortOrder.get(0);
			sortOrder.clear();
			sortOrder.add(sortedTableColumn);
		}

		bundlesLabel.setText("Informations: " + bundles.length + " bundles");
	}

	@FXML
	protected void handleBundleAction(BundleActionEvent bundleActionEvent) throws BundleException {
		Action action = bundleActionEvent.getAction();
		Long bundleId = bundleActionEvent.getBundleId();
		Bundle bundle = bundleContext.getBundle(bundleId);

		switch (action) {
		case START:
			bundle.start();
			break;
		case STOP:
			bundle.stop();
			break;
		case UPDATE:
			bundle.update();
			break;
		case UNINSTALL:
			bundle.uninstall();
			break;
		}

		LOGGER.debug("{} {}", action, bundleId);
		loadBundles();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle resourceBundle) {
		loadBundles();
	}
}
