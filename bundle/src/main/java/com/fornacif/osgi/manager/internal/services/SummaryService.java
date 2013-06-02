package com.fornacif.osgi.manager.internal.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.models.BundleModel;
import com.fornacif.osgi.manager.internal.models.PackageModel;
import com.fornacif.osgi.manager.internal.models.ServiceModel;
import com.fornacif.osgi.manager.internal.models.SummaryModel;

@Component(name = "SummaryService", provide = SummaryService.class)
public class SummaryService {

	public enum BundleCategory {
		INSTALLED, RESOLVED, ACTIVE, REMOVAL_PENDING;
	};

	public enum ServiceCategory {
		STANDARD, IN_USE
	};
	
	public enum PackageCategory {
		IN_USE, REMOVAL_PENDING
	};

	private JMXService jmxService;

	@Reference
	private void bindJmxService(JMXService jmxService) {
		this.jmxService = jmxService;
	}

	public SummaryModel getSummary(List<BundleModel> bundles, List<ServiceModel> services, List<PackageModel> packages) throws Exception {
		long uptime = jmxService.getRuntimeMXBean().getUptime();

		Map<BundleCategory, Integer> aggregatedBundles = aggregateBundles(bundles);
		Map<ServiceCategory, Integer> aggregatedServices = aggregateServices(services);
		Map<PackageCategory, Integer> aggregatedPackages = aggregatePackages(packages);

		SummaryModel summaryModel = new SummaryModel();
		
		summaryModel.setUptime(uptime);
		
		summaryModel.setBundlesCount(bundles.size());
		summaryModel.setInstalledBundlesCount(aggregatedBundles.get(BundleCategory.INSTALLED));
		summaryModel.setResolvedBundlesCount(aggregatedBundles.get(BundleCategory.RESOLVED));
		summaryModel.setActiveBundlesCount(aggregatedBundles.get(BundleCategory.ACTIVE));
		summaryModel.setRemovalPendingBundlesCount(aggregatedBundles.get(BundleCategory.REMOVAL_PENDING));
		
		summaryModel.setServicesCount(services.size());
		summaryModel.setStandardServicesCount(aggregatedServices.get(ServiceCategory.STANDARD));
		summaryModel.setInUseServicesCount(aggregatedServices.get(ServiceCategory.IN_USE));
		
		summaryModel.setPackagesCount(packages.size());
		summaryModel.setInUsePackagesCount(aggregatedPackages.get(PackageCategory.IN_USE));
		summaryModel.setRemovalPendingPackagesCount(aggregatedPackages.get(PackageCategory.REMOVAL_PENDING));

		return summaryModel;
	}


	private Map<BundleCategory, Integer> aggregateBundles(List<BundleModel> bundles) {
		Map<BundleCategory, Integer> aggregatedBundles = new HashMap<>();
		aggregatedBundles.put(BundleCategory.INSTALLED, 0);
		aggregatedBundles.put(BundleCategory.RESOLVED, 0);
		aggregatedBundles.put(BundleCategory.ACTIVE, 0);
		aggregatedBundles.put(BundleCategory.REMOVAL_PENDING, 0);

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
			
			if (bundleModel.isRemovalPending()) {
				aggregatedBundles.put(BundleCategory.REMOVAL_PENDING, aggregatedBundles.get(BundleCategory.REMOVAL_PENDING) + 1);
			}
		}

		return aggregatedBundles;
	}

	private Map<ServiceCategory, Integer> aggregateServices(List<ServiceModel> services) {
		Map<ServiceCategory, Integer> aggregatedServices = new HashMap<>();
		aggregatedServices.put(ServiceCategory.STANDARD, 0);
		aggregatedServices.put(ServiceCategory.IN_USE, 0);

		for (ServiceModel serviceModel : services) {
			List<String> objectClass = serviceModel.getObjectClass();
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

	private Map<PackageCategory, Integer> aggregatePackages(List<PackageModel> packages) {
		Map<PackageCategory, Integer> aggregatedPackages = new HashMap<>();
		aggregatedPackages.put(PackageCategory.IN_USE, 0);
		aggregatedPackages.put(PackageCategory.REMOVAL_PENDING, 0);
		
		for (PackageModel packageModel : packages) {
			if (packageModel.getImportingBundles().size() > 0) {
				aggregatedPackages.put(PackageCategory.IN_USE, aggregatedPackages.get(PackageCategory.IN_USE) + 1);
			}
			if (packageModel.isRemovalPending()) {
				aggregatedPackages.put(PackageCategory.REMOVAL_PENDING, aggregatedPackages.get(PackageCategory.REMOVAL_PENDING) + 1);
			}
		}
		
		return aggregatedPackages;
	}
	
}
