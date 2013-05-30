package com.fornacif.osgi.manager.internal.models;

import java.util.concurrent.TimeUnit;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SummaryModel {
	
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty arch = new SimpleStringProperty();
	private final IntegerProperty availableProcessors = new SimpleIntegerProperty();
	private final LongProperty uptime = new SimpleLongProperty();
	private final StringProperty formattedUptime = new SimpleStringProperty();
	private final IntegerProperty bundlesCount = new SimpleIntegerProperty();
	private final IntegerProperty installedBundlesCount = new SimpleIntegerProperty();
	private final IntegerProperty resolvedBundlesCount = new SimpleIntegerProperty();
	private final IntegerProperty activeBundlesCount = new SimpleIntegerProperty();
	private final IntegerProperty servicesCount = new SimpleIntegerProperty();
	private final IntegerProperty standardServicesCount = new SimpleIntegerProperty();
	private final IntegerProperty inUseServicesCount = new SimpleIntegerProperty();

	public String getName() {
		return name.get();
	}
	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public String getArch() {
		return arch.get();
	}

	public void setArch(String arch) {
		this.arch.set(arch);
	}
	
	public Integer getAvailableProcessors() {
		return availableProcessors.get();
	}

	public void setAvailableProcessors(Integer availableProcessors) {
		this.availableProcessors.set(availableProcessors);
	}
	
	public long getUptime() {
		return uptime.get();
	}

	public void setUptime(Long upTime) {
		this.uptime.set(upTime);
		
		long hours = TimeUnit.MILLISECONDS.toHours(upTime);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(upTime) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(upTime) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
		setFormattedUptime(String.format("%d hours %d minutes %d seconds", hours, minutes, seconds));
	}
	
	public String getFormattedUptime() {
		return formattedUptime.get();
	}
	
	public void setFormattedUptime(String formattedUptime) {
		this.formattedUptime.set(formattedUptime);
	}
	
	public StringProperty formattedUptimeProperty() {
		return formattedUptime;
	}
	
	public Integer getBundlesCount() {
		return bundlesCount.get();
	}

	public void setBundlesCount(Integer bundlesCount) {
		this.bundlesCount.set(bundlesCount);
	}
	
	public Integer getInstalledBundlesCount() {
		return installedBundlesCount.get();
	}

	public void setInstalledBundlesCount(Integer installedBundlesCount) {
		this.installedBundlesCount.set(installedBundlesCount);
	}
	
	public Integer getResolvedBundlesCount() {
		return resolvedBundlesCount.get();
	}

	public void setResolvedBundlesCount(Integer resolvedBundlesCount) {
		this.resolvedBundlesCount.set(resolvedBundlesCount);
	}
	
	public Integer getActiveBundlesCount() {
		return activeBundlesCount.get();
	}

	public void setActiveBundlesCount(Integer activeBundlesCount) {
		this.activeBundlesCount.set(activeBundlesCount);
	}

	public Integer getServicesCount() {
		return servicesCount.get();
	}

	public void setServicesCount(Integer servicesCount) {
		this.servicesCount.set(servicesCount);
	}
	
	public Integer getStandardServicesCount() {
		return standardServicesCount.get();
	}

	public void setStandardServicesCount(Integer standardServicesCount) {
		this.standardServicesCount.set(standardServicesCount);
	}
	
	public Integer getInUseServicesCount() {
		return inUseServicesCount.get();
	}

	public void setInUseServicesCount(Integer inUseServicesCount) {
		this.inUseServicesCount.set(inUseServicesCount);
	}

}
