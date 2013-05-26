package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.models.ServiceModel;
import com.fornacif.osgi.manager.internal.models.SummaryModel;

@Component(name="SummaryService", provide=SummaryService.class)
public class SummaryService {
	
	public enum CATEGORY {STANDARD, IN_USE};

	private JMXService jmxService;
	
	private ServicesService servicesService;

	@Reference
	private void bindJmxService(JMXService jmxService) {
		this.jmxService = jmxService;
	}
	
	@Reference
	private void bindServicesService(ServicesService servicesService) {
		this.servicesService = servicesService;
	}

	public SummaryModel getSummary() throws Exception {
		String arch = jmxService.getOperatingSystemMXBean().getArch();
		String name = jmxService.getOperatingSystemMXBean().getName();
		int availableProcessors = jmxService.getOperatingSystemMXBean().getAvailableProcessors();
		long upTime = jmxService.getRuntimeMXBean().getUptime();
		long[] bundleIds = jmxService.getBundleStateMBean().getBundleIds();
		List<ServiceModel> services = servicesService.listServices();
		Map<CATEGORY, Integer> aggregation = aggregate(services);

		SummaryModel summaryModel = new SummaryModel();
		summaryModel.setBundlesCount(bundleIds.length);
		summaryModel.setServicesCount(services.size());
		summaryModel.setStandardServicesCount(aggregation.get(CATEGORY.STANDARD));
		summaryModel.setInUseServicesCount(aggregation.get(CATEGORY.IN_USE));
		summaryModel.setName(name);
		summaryModel.setArch(arch);
		summaryModel.setAvailableProcessors(availableProcessors);
		summaryModel.setUpTime(upTime);
		
		return summaryModel;
	}
	
	private Map<CATEGORY, Integer> aggregate(List<ServiceModel> services) {
		Map<CATEGORY, Integer> categories = new HashMap<>();
		categories.put(CATEGORY.STANDARD, 0);
		categories.put(CATEGORY.IN_USE, 0);
		
		for (ServiceModel serviceModel : services) {
			String[] objectClass = serviceModel.getObjectClass();
			for (String clazz : objectClass) {
				if (clazz.startsWith("org.osgi.service")) {
					categories.put(CATEGORY.STANDARD, categories.get(CATEGORY.STANDARD) + 1);
				}
			}
			if (serviceModel.isInUse()) {
				categories.put(CATEGORY.IN_USE, categories.get(CATEGORY.IN_USE) + 1);
			}
		}
		
		return categories;
	}


}
