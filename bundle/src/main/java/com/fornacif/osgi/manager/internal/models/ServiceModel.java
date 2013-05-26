package com.fornacif.osgi.manager.internal.models;

public class ServiceModel {
	
	public enum ServiceType {
		STANDARD, USER
	};

	private long id;
	private String[] objectClass;
	private boolean isInUse;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String[] getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String[] objectClass) {
		this.objectClass = objectClass;
	}

	public boolean isInUse() {
		return isInUse;
	}

	public void setInUse(boolean isInUse) {
		this.isInUse = isInUse;
	}

}
