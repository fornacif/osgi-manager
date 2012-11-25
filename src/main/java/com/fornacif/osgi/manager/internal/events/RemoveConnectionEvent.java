package com.fornacif.osgi.manager.internal.events;

import com.fornacif.osgi.manager.internal.models.ConnectionModel;

import javafx.event.ActionEvent;

@SuppressWarnings("serial")
public class RemoveConnectionEvent extends ActionEvent {
	final private ConnectionModel connection;
	
	public RemoveConnectionEvent(ConnectionModel connection) {
		this.connection = connection;
	}
	
	public ConnectionModel getConnection() {
		return connection;
	}

}
