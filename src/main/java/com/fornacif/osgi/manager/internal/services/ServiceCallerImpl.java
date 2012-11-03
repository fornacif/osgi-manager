package com.fornacif.osgi.manager.internal.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

@Component(name = "ServiceCaller")
public class ServiceCallerImpl implements ServiceCaller {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private EventAdmin eventAdmin;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	private Map<AsynchService<?>, Service<?>> services = Collections.synchronizedMap(new HashMap<AsynchService<?>, Service<?>>());

	@Reference
	private void bindEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	@Override
	public <T> void execute(final AsynchService<T> asynchService) {
		if (services.containsKey(asynchService)) {
			HashMap<String, String> properties = new HashMap<>();
			properties.put("TYPE", "INFO");
			properties.put("MESSAGE", "Waiting for a task to be completed");
			eventAdmin.sendEvent(new Event(EventAdminTopics.NOTIFICATION, properties));
		} else {
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
					services.remove(asynchService);
					asynchService.succeeded(getValue());
				}
	
				@Override
				protected void failed() {
					services.remove(asynchService);
					LOGGER.error("Error during service call", getException());
					asynchService.failed(getException());
					HashMap<String, String> properties = new HashMap<>();
					properties.put("TYPE", "ERROR");
					properties.put("MESSAGE", "An error occured");
					eventAdmin.sendEvent(new Event(EventAdminTopics.NOTIFICATION, properties));
				}
			};
			service.setExecutor(executorService);
			service.start();
			services.put(asynchService, service);
		}
	}

}
