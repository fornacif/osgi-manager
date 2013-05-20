package com.fornacif.osgi.manager.services;

public interface ServiceCaller {
	<T> void execute(final AsynchService<T> asynchService, boolean showProgressIndicator);
}
