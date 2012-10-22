package com.fornacif.osgi.manager.internal.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.sun.tools.attach.VirtualMachine;

public class ConnectionModel {
	private final StringProperty id = new SimpleStringProperty();
	private final StringProperty name = new SimpleStringProperty();
	private final ObjectProperty<VirtualMachine> virtualMachine = new SimpleObjectProperty<VirtualMachine>();
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
	
	public VirtualMachine getVirtualMachine() {
		return this.virtualMachine.get();
	}
	
	public void setVirtualMachine(VirtualMachine virtualMachine) {
		this.virtualMachine.set(virtualMachine);
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

}
