package com.fornacif.osgi.manager.internal.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BundleModel {

	private final LongProperty id = new SimpleLongProperty();
	private final StringProperty state = new SimpleStringProperty();
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty symbolicName = new SimpleStringProperty();
	private final StringProperty version = new SimpleStringProperty();
	private final IntegerProperty startLevel = new SimpleIntegerProperty();

	public Long getId() {
		return id.get();
	}

	public void setId(Long id) {
		this.id.set(id);
	}

	public String getState() {
		return state.get();
	}

	public void setState(String state) {
		this.state.set(state);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public String getSymbolicName() {
		return symbolicName.get();
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName.set(symbolicName);
	}

	public String getVersion() {
		return version.get();
	}

	public void setVersion(String version) {
		this.version.set(version);
	}

	public Integer getStartLevel() {
		return startLevel.get();
	}

	public void setStartLevel(Integer startLevel) {
		this.startLevel.set(startLevel);
	}
	
	public BundleModel getModel() {
		return this;
	}

}
