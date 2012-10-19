package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.events.BundleActionEvent;
import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.BundleRow;
import com.fornacif.osgi.manager.internal.models.BundlesModel;
import com.fornacif.osgi.manager.services.AsynchronousCallerService;
import com.fornacif.osgi.manager.services.BundlesService;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@Component(name = "BundlesController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require)
public class BundlesController extends VBox implements Initializable {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@FXML
	private Label bundlesCountLabel;

	@FXML
	private TableView<BundleRow> bundlesTableView;
	
	@FXML
	private TextField bundleNameFilterTextField;
	
	private BundlesService bundlesService;
	
	private BundlesModel bundlesModel;
	
	private ObjectProperty<ObservableList<BundleRow>> filteredBundlesProperty = new SimpleObjectProperty<>();
	
	private AsynchronousCallerService asynchronousCallerService;

	@Reference
	public void bindBundlesModel(BundlesModel bundlesModel) {
		this.bundlesModel = bundlesModel;
	}

	@Reference
	public void bindBundlesService(BundlesService bundlesService) {
		this.bundlesService = bundlesService;
	}
	
	
	@Reference
	public void bindAsynchronousCallerService(AsynchronousCallerService asynchronousCallerService) {
		this.asynchronousCallerService = asynchronousCallerService;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {	
		bundlesModel.getBundles().addListener(new ChangeListener<ObservableList<BundleRow>>() {
			@Override
			public void changed(ObservableValue<? extends ObservableList<BundleRow>> arg0, ObservableList<BundleRow> arg1, ObservableList<BundleRow> arg2) {
				LOGGER.debug("Bundle list changed");
				applyBundleNameFilter();
			}
		});
		
		bundlesTableView.itemsProperty().bind(filteredBundlesProperty);
		bundlesCountLabel.textProperty().bind(bundlesModel.getBundlesCount());
		handleBundleNameFilter();
		listBundles();
	}

	@FXML
	protected void executeBundleAction(final BundleActionEvent bundleActionEvent) throws Exception {
		asynchronousCallerService.callAsynchronously(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Action action = bundleActionEvent.getAction();
				Long bundleId = bundleActionEvent.getBundleId();
				bundlesService.executeAction(action, bundleId);
				listBundles();
				return null;
			}
		});
	}

	private void listBundles() {
		try {
			bundlesService.loadBundles();
		} catch (Exception e) {
			LOGGER.error("Error during loading bundles", e);
		}
	}

	private void applyBundleNameFilter() {
		Predicate<BundleRow> predicate = new Predicate<BundleRow>() {
			public boolean apply(BundleRow bundleModel) {
				if (!bundleNameFilterTextField.getText().equals("")) {
					return bundleModel.getName().toUpperCase().contains(bundleNameFilterTextField.getText().toUpperCase());
				} 
				return true;	
			}
		};
		Collection<BundleRow> filteredBundles = Collections2.filter(bundlesModel.getBundles().get(), predicate);
		filteredBundlesProperty.set(FXCollections.observableArrayList(filteredBundles));
		updateSort();
	}
	
	private void updateSort() {
		ObservableList<TableColumn<BundleRow, ?>> sortOrder = bundlesTableView.getSortOrder();
		if (sortOrder.size() > 0) {
			TableColumn<BundleRow, ?> sortedTableColumn = sortOrder.get(0);
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
