package com.fornacif.osgi.manager.services;

import java.util.concurrent.Callable;

public abstract class ResultCallable<T> implements Callable<Void> {

	private T result;
	
	public T getResult() {
		return result;
	}
	
	public void setResult(T result) {
		this.result = result;
	}
	
	
}
