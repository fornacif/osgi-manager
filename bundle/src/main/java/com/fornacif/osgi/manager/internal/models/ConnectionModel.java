package com.fornacif.osgi.manager.internal.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class ConnectionModel {
	private final IntegerProperty pid = new SimpleIntegerProperty();
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty url = new SimpleStringProperty();
	private final BooleanProperty connected = new SimpleBooleanProperty(false);
	
	public Integer getPid() {
		return pid.get();
	}

	public void setPid(Integer id) {
		this.pid.set(id);
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

	@JsonIgnore
	public Boolean isConnected() {
		return connected.get();
	}

	public void setConnected(Boolean connected) {
		this.connected.set(connected);
	}
	
	@JsonIgnore
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
		return ((ConnectionModel) object).getPid().equals(getPid());
	}

}
