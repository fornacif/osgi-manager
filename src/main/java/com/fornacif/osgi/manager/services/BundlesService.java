package com.fornacif.osgi.manager.services;

import java.util.List;

import com.fornacif.osgi.manager.internal.models.BundleModel;

public interface BundlesService {
	List<BundleModel> loadBundles();
}
