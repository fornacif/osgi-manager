package com.fornacif.osgi.manager.internal.services;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.models.BundleModel;
import com.fornacif.osgi.manager.internal.models.Models;
import com.fornacif.osgi.manager.internal.models.ServiceModel;
import com.fornacif.osgi.manager.internal.models.SummaryModel;

@Component(name = "ModelsService", provide = { ModelsService.class })
public class ModelsService {
	
private BundlesService bundlesService;
	
	private ServicesService servicesService;
	
	private SummaryService summaryService;
	
	private final ObjectProperty<Models> models = new SimpleObjectProperty<>();
	
	@Reference
	private void bindBundlesService(BundlesService bundlesService) {
		this.bundlesService = bundlesService;
	}
	
	@Reference
	private void bindServicesService(ServicesService servicesService) {
		this.servicesService = servicesService;
	}
	
	@Reference
	private void bindSummaryService(SummaryService summaryService) {
		this.summaryService = summaryService;
	}

	public Models getModels() {
		return models.get();
	}

	public void setModels(Models models) {
		this.models.set(models);
	}
	
	public ObjectProperty<Models> modelsProperty() {
		return models;
	}
	
	public Models loadModels() throws Exception {	
		List<BundleModel> bundles = bundlesService.listBundles();
		List<ServiceModel> services = servicesService.listServices();
		SummaryModel summaryModel = summaryService.getSummary(bundles, services);
		
		Models models = new Models();
		models.setBundles(bundles);
		models.setServices(services);
		models.setSummaryModel(summaryModel);
		
		return models;
	}

}
