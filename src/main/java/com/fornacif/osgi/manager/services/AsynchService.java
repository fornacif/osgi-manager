package com.fornacif.osgi.manager.services;

public abstract class AsynchService<T> {
	
	public abstract T call() throws Exception;
	
	public void succeeded(T result) {
		// Not implemented
	}
	
	public void failed(Throwable exception) {
		// Not implemented
	}
	
	@Override
	public int hashCode() {
		return getClass().getGenericSuperclass().hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		return object.getClass().getGenericSuperclass().equals(getClass().getGenericSuperclass());
	}
}
