package com.fornacif.osgi.manager.services;

public abstract class AsynchService<T> {
	public abstract T call() throws Exception;
	public void succeeded(T result) {
		// Not implemented
	}
	public void failed(Throwable exception) {
		// Not implemented
	}
}
