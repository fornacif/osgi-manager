package com.fornacif.osgi.manager.internal.models;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Models {

	private final ObservableList<BundleModel> bundles = FXCollections.observableArrayList();
	
	private final ObservableList<ServiceModel> services = FXCollections.observableArrayList();
	
	private final ObjectProperty<SummaryModel> summaryModel = new SimpleObjectProperty<>();
	
	public ObservableList<BundleModel> getBundles() {
		return bundles;
	}

	public void setBundles(List<BundleModel> bundles) {
		this.bundles.clear();
		this.bundles.addAll(bundles);
	}
	
	public ObservableList<ServiceModel> getServices() {
		return services;
	}

	public void setServices(List<ServiceModel> services) {
		this.services.clear();
		this.services.addAll(services);
	}

	public SummaryModel getSummaryModel() {
		return summaryModel.get();
	}

	public void setSummaryModel(SummaryModel summaryModel) {
		this.summaryModel.set(summaryModel);
	}

}
