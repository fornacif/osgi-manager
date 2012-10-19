package com.fornacif.osgi.manager.internal.services;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.constants.EventAdminTopics;
import com.fornacif.osgi.manager.services.AsynchronousCallerService;

@Component
public class AsynchronousCallerServiceImpl implements AsynchronousCallerService {
	
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private EventAdmin eventAdmin;
	
	@Reference
	public void bindEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	@Override
	public <T> void callAsynchronously(final Callable<T> callable) throws Exception {
		executorService.submit(new Callable<T>() {
			public T call() throws Exception {
				try {
					eventAdmin.postEvent(new Event(EventAdminTopics.PROGRESS_INDICATOR_START, new HashMap<String, Object>()));
					return callable.call();
				} finally {
					eventAdmin.postEvent(new Event(EventAdminTopics.PROGRESS_INDICATOR_STOP, new HashMap<String, Object>()));
				}
			};
		});	
	}

}
