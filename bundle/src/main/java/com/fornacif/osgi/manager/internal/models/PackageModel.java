package com.fornacif.osgi.manager.internal.models;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class PackageModel {

	private final ListProperty<Long> exportingBundles = new SimpleListProperty<>();
	private final ListProperty<Long> importingBundles = new SimpleListProperty<>();
	private final StringProperty name = new SimpleStringProperty();
	private final BooleanProperty isRemovalPending = new SimpleBooleanProperty();
	private final StringProperty version = new SimpleStringProperty();

	public List<Long> getExportingBundles() {
		return exportingBundles.get();
	}
	
	public void setExportingBundles(List<Long> exportingBundles) {
		this.exportingBundles.set(FXCollections.observableArrayList(exportingBundles));
	}

	public List<Long> getImportingBundles() {
		return importingBundles.get();
	}
	
	public void setImportingBundles(List<Long> importingBundles) {
		this.importingBundles.set(FXCollections.observableArrayList(importingBundles));
	}

	public String getName() {
		return name.get();
	}
	
	public void setName(String name) {
		this.name.set(name);
	}

	public Boolean isRemovalPending() {
		return isRemovalPending.get();
	}
	
	public void setRemovalPending(boolean isRemovalPending) {
		this.isRemovalPending.set(isRemovalPending);
	}

	public String getVersion() {
		return version.get();
	}
	
	public void setVersion(String version) {
		this.version.set(version);
	}

}
