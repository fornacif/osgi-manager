package com.fornacif.osgi.manager.internal.services;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.constants.EventAdminTopics;
import com.fornacif.osgi.manager.services.FaultCallable;
import com.fornacif.osgi.manager.services.ResultCallable;
import com.fornacif.osgi.manager.services.ServiceCaller;

@Component
public class ServiceCallerImpl implements ServiceCaller {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private EventAdmin eventAdmin;
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	@Reference
	public void bindEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	@Override
	public <T> void execute(final Callable<T> callable, final ResultCallable<T> resultHandler, final FaultCallable faultHandler) {
		Service<T> service = new Service<T>() {
			@Override
			protected Task<T> createTask() {
				return new Task<T>() {
					@Override
					protected T call() throws Exception {
						try {
							eventAdmin.sendEvent(new Event(EventAdminTopics.PROGRESS_INDICATOR_START, new HashMap<String, Object>()));
							return callable.call();
						} finally {
							eventAdmin.sendEvent(new Event(EventAdminTopics.PROGRESS_INDICATOR_STOP, new HashMap<String, Object>()));
						}
					}
				};
			}
			
			@Override
			protected void succeeded() {
				if (resultHandler != null) {
					try {
						resultHandler.setResult(getValue());
						resultHandler.call();
					} catch (Exception e) {
						LOGGER.error("Error on result handler", e);
					}
				}
			}
			
			@Override
			protected void failed() {
				LOGGER.error("Error during service call", getException());
				if (faultHandler != null) {
					try {
						faultHandler.setExeception(getException());
						faultHandler.call();
					} catch (Exception e) {
						LOGGER.error("Error on fault handler", e);
					}
				}
			}
		};	
		service.setExecutor(executorService);
		service.start();
	}

}
