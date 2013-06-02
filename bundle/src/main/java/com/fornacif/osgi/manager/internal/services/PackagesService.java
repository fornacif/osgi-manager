package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.osgi.jmx.framework.PackageStateMBean;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.models.PackageModel;

@Component(name = "PackagesService", provide = PackagesService.class)
public class PackagesService {

	private JMXService jmxService;

	@Reference
	private void bindJmxConnectorService(JMXService jmxConnectorService) {
		this.jmxService = jmxConnectorService;
	}

	public List<PackageModel> listPackages() throws IOException {
		TabularData packageData = jmxService.getPackageStateMBean().listPackages();
		List<PackageModel> packages = listPackages(packageData);
		return packages;
	}

	private List<PackageModel> listPackages(TabularData packageData) {
		List<PackageModel> packages = new ArrayList<>();
		Collection<CompositeData> packagesCompositeData = (Collection<CompositeData>) packageData.values();
		for (CompositeData packageCompositeData : packagesCompositeData) {
			PackageModel packageModel = new PackageModel();

			List<Long> exportingBundles = Arrays.asList((Long[]) packageCompositeData.get(PackageStateMBean.EXPORTING_BUNDLES));
			List<Long> importingBundles = Arrays.asList((Long[]) packageCompositeData.get(PackageStateMBean.IMPORTING_BUNDLES));
			String name = (String) packageCompositeData.get(PackageStateMBean.NAME);
			boolean isRemovalPending = (boolean) packageCompositeData.get(PackageStateMBean.REMOVAL_PENDING);
			String version = (String) packageCompositeData.get(PackageStateMBean.VERSION);

			packageModel.setExportingBundles(exportingBundles);
			packageModel.setImportingBundles(importingBundles);
			packageModel.setName(name);
			packageModel.setRemovalPending(isRemovalPending);
			packageModel.setVersion(version);

			packages.add(packageModel);
		}
		return packages;
	}

}
