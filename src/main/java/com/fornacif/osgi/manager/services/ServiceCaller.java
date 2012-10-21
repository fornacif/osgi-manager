package com.fornacif.osgi.manager.services;

import java.util.concurrent.Callable;

public interface ServiceCaller {
	<T> void execute(final Callable<T> callable, final ResultCallable<T> resultHandler, final FaultCallable faultHandler);
}
