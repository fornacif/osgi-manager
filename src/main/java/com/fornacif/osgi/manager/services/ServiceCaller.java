package com.fornacif.osgi.manager.services;

import java.util.concurrent.Callable;

public interface ServiceCaller {
	void execute(Callable<Void> service, Callable<Void> resultHandler);
}
