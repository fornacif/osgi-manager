package com.fornacif.osgi.manager.internal.models;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;

public class ServiceModel {

	private final LongProperty id = new SimpleLongProperty();
	private final ListProperty<String> objectClass = new SimpleListProperty<>();
	private final BooleanProperty isInUse = new SimpleBooleanProperty();

	public long getId() {
		return id.get();
	}

	public void setId(long id) {
		this.id.set(id);
	}

	public List<String> getObjectClass() {
		return objectClass.get();
	}

	public void setObjectClass(List<String> objectClass) {
		this.objectClass.set(FXCollections.observableArrayList(objectClass));
	}

	public boolean isInUse() {
		return isInUse.get();
	}

	public void setInUse(boolean isInUse) {
		this.isInUse.set(isInUse);
	}

}
