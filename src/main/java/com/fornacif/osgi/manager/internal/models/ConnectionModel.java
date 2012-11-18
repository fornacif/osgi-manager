package com.fornacif.osgi.manager.internal.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConnectionModel {
	private final StringProperty id = new SimpleStringProperty();
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty url = new SimpleStringProperty();
	private final BooleanProperty connected = new SimpleBooleanProperty(false);
	
	public String getId() {
		return id.get();
	}

	public void setId(String id) {
		this.id.set(id);
	}
	
	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public String getUrl() {
		return url.get();
	}

	public void setUrl(String url) {
		this.url.set(url);
	}

	public Boolean isConnected() {
		return connected.get();
	}

	public void setConnected(Boolean connected) {
		this.connected.set(connected);
	}
	
	public ConnectionModel getModel() {
		return this;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (!(object instanceof ConnectionModel)) {
			return false;
		}
		return ((ConnectionModel) object).getName().equals(getName());
	}
	
	public void toJson() {
		
	}
	
	private void fromJson(String json) {
		
	}

}
