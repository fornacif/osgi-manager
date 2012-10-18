package com.fornacif.osgi.manager.services;

import java.io.IOException;
import java.util.List;

import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.BundleModel;

public interface BundlesService {
	List<BundleModel> listBundles() throws IOException;
	void executeAction(Action action, Long bundleId) throws IOException;
}
