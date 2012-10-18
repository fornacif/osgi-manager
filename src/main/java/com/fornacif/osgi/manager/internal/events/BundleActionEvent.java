package com.fornacif.osgi.manager.internal.events;

import javafx.event.ActionEvent;

@SuppressWarnings("serial")
public class BundleActionEvent extends ActionEvent {
	public enum Action {
		START, STOP, UPDATE, UNINSTALL
	};
	
	final private Action action;
	final private Long bundleId;
	
	public BundleActionEvent(Action action, Long bundleId) {
		this.action = action;
		this.bundleId = bundleId;
	}

	public Action getAction() {
		return action;
	}

	public Long getBundleId() {
		return bundleId;
	}

}
