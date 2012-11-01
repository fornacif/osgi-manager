package com.fornacif.osgi.manager.internal.services;

import java.util.HashMap;
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
import com.fornacif.osgi.manager.services.AsynchService;
import com.fornacif.osgi.manager.services.ServiceCaller;

@Component(name="ServiceCaller")
public class ServiceCallerImpl implements ServiceCaller {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private EventAdmin eventAdmin;
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	@Reference
	private void bindEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	@Override
	public <T> void execute(final AsynchService<T> asynchService) {
		Service<T> service = new Service<T>() {
			@Override
			protected Task<T> createTask() {
				return new Task<T>() {
					@Override
					protected T call() throws Exception {
						try {
							eventAdmin.sendEvent(new Event(EventAdminTopics.PROGRESS_INDICATOR_START, new HashMap<String, Object>()));
							return asynchService.call();
						} finally {
							eventAdmin.sendEvent(new Event(EventAdminTopics.PROGRESS_INDICATOR_STOP, new HashMap<String, Object>()));
						}
					}
				};
			}
			
			@Override
			protected void succeeded() {
				asynchService.succeeded(getValue());
			}
			
			@Override
			protected void failed() {
				LOGGER.error("Error during service call", getException());
				asynchService.failed(getException());
			}
		};	
		service.setExecutor(executorService);
		service.start();
	}

}
