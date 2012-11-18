package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.osgi.service.prefs.PreferencesService;

import com.fornacif.osgi.manager.internal.models.ConnectionModel;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(name="PreferencesService", provide = { SystemPreferencesService.class })
public class SystemPreferencesService {
	private PreferencesService preferencesService;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	public <T> void save(String key, T value) throws IOException {
		String jsonValue = mapper.writeValueAsString(value);
		preferencesService.getSystemPreferences().put(key, jsonValue);
	}
	
	public <T> T load(String key, TypeReference<List<ConnectionModel>> typeReference) throws IOException {
		String jsonValue = preferencesService.getSystemPreferences().get(key, null);
		if (jsonValue != null) {
			return mapper.readValue(jsonValue, typeReference);	
		} else {
			return null;
		}
	}

	@Reference
	public void bindPreferencesService(PreferencesService preferencesService) {
		this.preferencesService = preferencesService;
	}
}
