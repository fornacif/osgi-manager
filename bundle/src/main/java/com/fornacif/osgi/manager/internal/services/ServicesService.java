package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.osgi.jmx.framework.ServiceStateMBean;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.models.ServiceModel;

@Component(name="ServicesService", provide=ServicesService.class)
public class ServicesService {

	private JMXService jmxService;

	@Reference
	private void bindJmxConnectorService(JMXService jmxConnectorService) {
		this.jmxService = jmxConnectorService;
	}

	public List<ServiceModel> listServices() throws IOException  {
		TabularData serviceData = jmxService.getServiceStateMBean().listServices();
		List<ServiceModel> services = listServices(serviceData);
		return services;
	}
	
	private List<ServiceModel> listServices(TabularData serviceData) {
		List<ServiceModel> services = new ArrayList<>();
		Collection<CompositeData> servicesCompositeData = (Collection<CompositeData>) serviceData.values();
		for (CompositeData serviceCompositeData : servicesCompositeData) {
			ServiceModel serviceModel = new ServiceModel();
			
			Long id = (Long) serviceCompositeData.get(ServiceStateMBean.IDENTIFIER);
			String[] objectClass = (String[]) serviceCompositeData.get(ServiceStateMBean.OBJECT_CLASS);
			Long[] usingBundles = (Long[]) serviceCompositeData.get(ServiceStateMBean.USING_BUNDLES);
			
			serviceModel.setId(id);
			serviceModel.setObjectClass(objectClass);
			serviceModel.setInUse(usingBundles.length > 0);
			
			services.add(serviceModel);
		}
		return services;
	}


}
