package com.fornacif.osgi.manager.services;

import java.util.concurrent.Callable;

public interface AsynchronousCallerService {
	<T> void callAsynchronously(Callable<T> callable) throws Exception;
}
