package com.fornacif.osgi.manager.services;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import com.fornacif.osgi.manager.internal.events.BundleActionEvent.Action;
import com.fornacif.osgi.manager.internal.models.BundleModel;

public interface BundlesService {
	Callable<List<BundleModel>> listBundles();
	Callable<Void> executeAction(Action action, Long bundleId);
}
