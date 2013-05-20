package com.fornacif.osgi.manager.services;

public interface ServiceCaller {
	<T> void execute(AsynchService<T> asynchService, boolean showProgressIndicator, boolean blockConcurrentExecutions);
}
