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
	public void execute(final Callable<Void> callable, final Callable<Void> resultHandler) {
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						try {
							eventAdmin.postEvent(new Event(EventAdminTopics.PROGRESS_INDICATOR_START, new HashMap<String, Object>()));
							return callable.call();
						} finally {
							eventAdmin.postEvent(new Event(EventAdminTopics.PROGRESS_INDICATOR_STOP, new HashMap<String, Object>()));
						}
					}
				};
			}
			
			@Override
			protected void succeeded() {
				if (resultHandler != null) {
					try {
						resultHandler.call();
					} catch (Exception e) {
						LOGGER.error("Error calling the result handler", e);
					}
				}
			}
			
			@Override
			protected void failed() {
				LOGGER.error("Error during service call", getException());
			}
		};	
		service.setExecutor(executorService);
		service.start();
	}

}
