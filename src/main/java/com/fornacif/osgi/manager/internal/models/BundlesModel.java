package com.fornacif.osgi.manager.internal.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import aQute.bnd.annotation.component.Component;

@Component(provide = BundlesModel.class)
public class BundlesModel {
	final private ObjectProperty<ObservableList<BundleRow>> bundles = new SimpleObjectProperty<>();
	final private StringProperty bundlesCount = new SimpleStringProperty();

	public ObjectProperty<ObservableList<BundleRow>> getBundles() {
		return bundles;
	}
	
	public StringProperty getBundlesCount() {
		return bundlesCount;
	}
}
