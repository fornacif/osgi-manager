package com.fornacif.osgi.manager.services;

import java.io.IOException;

import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;

public interface BundlesService {
	void loadBundles() throws IOException;
	void executeAction(Action action, Long bundleId) throws IOException;
}
