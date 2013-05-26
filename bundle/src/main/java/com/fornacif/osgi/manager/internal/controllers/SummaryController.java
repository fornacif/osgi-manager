package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.configurations.SummaryControllerConfiguration;
import com.fornacif.osgi.manager.internal.models.SummaryModel;
import com.fornacif.osgi.manager.internal.services.SummaryService;
import com.fornacif.osgi.manager.services.AsynchService;
import com.fornacif.osgi.manager.services.ServiceCaller;

@Component(name = "SummaryController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require, designate = SummaryControllerConfiguration.class)
public class SummaryController extends VBox implements Initializable {

	private ServiceCaller serviceCaller;

	private SummaryService summaryService;
	
	private ObjectProperty<SummaryModel> summaryModel = new SimpleObjectProperty<SummaryModel>();
	
	public final SummaryModel getSummaryModel() {
		return summaryModel.get();
	}

	public final void setSummaryModel(SummaryModel value) {
		summaryModel.set(value);
	}

	public final ObjectProperty<SummaryModel> summaryModelProperty() {
		return summaryModel;
	}

	@Reference
	private void bindServiceCaller(ServiceCaller serviceCaller) {
		this.serviceCaller = serviceCaller;
	}

	@Reference
	private void bindJmxConnectorService(SummaryService summaryService) {
		this.summaryService = summaryService;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		serviceCaller.execute(new AsynchService<SummaryModel>() {
			@Override
			public SummaryModel call() throws Exception {
				return summaryService.getSummary();
			}

			@Override
			public void succeeded(SummaryModel result) {
				setSummaryModel(result);
			}
		}, true, true);
	}

}
