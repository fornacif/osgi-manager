package com.fornacif.osgi.manager.internal.events;

import com.fornacif.osgi.manager.internal.models.ConnectionModel;

import javafx.event.ActionEvent;

@SuppressWarnings("serial")
public class ConnectionActionEvent extends ActionEvent {
	public enum Action {
		CONNECT, DISCONNECT
	};
	
	final private Action action;
	final private ConnectionModel connection;
	
	public ConnectionActionEvent(Action action, ConnectionModel connection) {
		this.action = action;
		this.connection = connection;
	}

	public Action getAction() {
		return action;
	}
	
	public ConnectionModel getConnection() {
		return connection;
	}

}
