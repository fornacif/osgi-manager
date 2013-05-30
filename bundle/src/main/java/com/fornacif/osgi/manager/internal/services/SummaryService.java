package com.fornacif.osgi.manager.internal.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.models.BundleModel;
import com.fornacif.osgi.manager.internal.models.ServiceModel;
import com.fornacif.osgi.manager.internal.models.SummaryModel;

@Component(name = "SummaryService", provide = SummaryService.class)
public class SummaryService {

	public enum BundleCategory {
		INSTALLED, RESOLVED, ACTIVE;
	};

	public enum ServiceCategory {
		STANDARD, IN_USE
	};

	private JMXService jmxService;

	@Reference
	private void bindJmxService(JMXService jmxService) {
		this.jmxService = jmxService;
	}

	public SummaryModel getSummary(List<BundleModel> bundles, List<ServiceModel> services) throws Exception {
		String arch = jmxService.getOperatingSystemMXBean().getArch();
		String name = jmxService.getOperatingSystemMXBean().getName();
		int availableProcessors = jmxService.getOperatingSystemMXBean().getAvailableProcessors();
		long upTime = jmxService.getRuntimeMXBean().getUptime();

		Map<BundleCategory, Integer> aggregatedBundles = aggregateBundles(bundles);
		Map<ServiceCategory, Integer> aggregatedServices = aggregateServices(services);

		SummaryModel summaryModel = new SummaryModel();
		summaryModel.setBundlesCount(bundles.size());
		summaryModel.setInstalledBundlesCount(aggregatedBundles.get(BundleCategory.INSTALLED));
		summaryModel.setResolvedBundlesCount(aggregatedBundles.get(BundleCategory.RESOLVED));
		summaryModel.setActiveBundlesCount(aggregatedBundles.get(BundleCategory.ACTIVE));
		summaryModel.setServicesCount(services.size());
		summaryModel.setStandardServicesCount(aggregatedServices.get(ServiceCategory.STANDARD));
		summaryModel.setInUseServicesCount(aggregatedServices.get(ServiceCategory.IN_USE));
		summaryModel.setName(name);
		summaryModel.setArch(arch);
		summaryModel.setAvailableProcessors(availableProcessors);
		summaryModel.setUpTime(upTime);

		return summaryModel;
	}

	private Map<BundleCategory, Integer> aggregateBundles(List<BundleModel> bundles) {
		Map<BundleCategory, Integer> aggregatedBundles = new HashMap<>();
		aggregatedBundles.put(BundleCategory.INSTALLED, 0);
		aggregatedBundles.put(BundleCategory.RESOLVED, 0);
		aggregatedBundles.put(BundleCategory.ACTIVE, 0);

		for (BundleModel bundleModel : bundles) {
			switch (BundleCategory.valueOf(bundleModel.getState())) {
			case INSTALLED:
				aggregatedBundles.put(BundleCategory.INSTALLED, aggregatedBundles.get(BundleCategory.INSTALLED) + 1);
				break;
			case RESOLVED:
				aggregatedBundles.put(BundleCategory.RESOLVED, aggregatedBundles.get(BundleCategory.RESOLVED) + 1);
				break;
			case ACTIVE:
				aggregatedBundles.put(BundleCategory.ACTIVE, aggregatedBundles.get(BundleCategory.ACTIVE) + 1);
				break;
			}
		}

		return aggregatedBundles;
	}

	private Map<ServiceCategory, Integer> aggregateServices(List<ServiceModel> services) {
		Map<ServiceCategory, Integer> aggregatedServices = new HashMap<>();
		aggregatedServices.put(ServiceCategory.STANDARD, 0);
		aggregatedServices.put(ServiceCategory.IN_USE, 0);

		for (ServiceModel serviceModel : services) {
			String[] objectClass = serviceModel.getObjectClass();
			for (String clazz : objectClass) {
				if (clazz.startsWith("org.osgi.service")) {
					aggregatedServices.put(ServiceCategory.STANDARD, aggregatedServices.get(ServiceCategory.STANDARD) + 1);
				}
			}
			if (serviceModel.isInUse()) {
				aggregatedServices.put(ServiceCategory.IN_USE, aggregatedServices.get(ServiceCategory.IN_USE) + 1);
			}
		}

		return aggregatedServices;
	}

}
