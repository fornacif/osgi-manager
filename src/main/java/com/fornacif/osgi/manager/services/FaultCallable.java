package com.fornacif.osgi.manager.services;

import java.util.concurrent.Callable;

public abstract class FaultCallable implements Callable<Void> {
	private Throwable exception;
	
	public Throwable getException() {
		return exception;
	}
	
	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public void setExeception(Throwable exception) {
		this.exception = exception;
	}
}
