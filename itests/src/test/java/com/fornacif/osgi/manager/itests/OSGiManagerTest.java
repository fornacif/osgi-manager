package com.fornacif.osgi.manager.itests;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import static org.ops4j.pax.exam.CoreOptions.vmOption;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;


@RunWith(PaxExam.class)
public class OSGiManagerTest {

	@Inject
	private BundleContext bundleContext;
	
	@Inject @Filter("(component.name=BundlesController)")
	private Object bundlesController;
	
	private final File equinoxFile = new File("target/dependency/osgi.manager.assembly-0.1.0-SNAPSHOT/");

	@Configuration
	public Option[] configuration() throws Exception {
		List<Option> options = new ArrayList<>();
		options.add(junitBundles());
		options.add(vmOption("-Xmx512m"));
		options.addAll(properties());
		
		return options.toArray(new Option[options.size()]);
	}

	public List<Option> properties() throws Exception {
		File configFile = new File(equinoxFile, "/configuration/config.ini");
		Assert.assertTrue(configFile.exists());
		
		Properties properties = new Properties();
		properties.load(new FileInputStream(configFile));
		Assert.assertTrue(properties.size() > 0);
		
		List<Option> options = new ArrayList<>();
		options.addAll(addPackages((String) properties.get(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA)));
		options.addAll(addBundles((String) properties.get("osgi.bundles")));
		
		return options;
	}
	
	private List<Option> addPackages(String packagesProperty) {
		List<Option> packages = new ArrayList<>();
		
		String[] packagesDeclaration = packagesProperty.split(",");
		for (String packageDeclaration : packagesDeclaration) {
			packages.add(systemPackage(packageDeclaration));	
		}
		return packages;
	}
	
	private List<Option> addBundles(String bundlesProperty) {
		List<Option> bundles = new ArrayList<>();
		
		String[] bundlesDeclaration = bundlesProperty.split(",");
		for (String bundleDeclaration : bundlesDeclaration) {
			String bundleFilename = bundleDeclaration.replace("@start", ".jar");
			File bundleFile = new File(equinoxFile, bundleFilename);
			if (bundleFile.exists()) {
				bundles.add(bundle("file:" + bundleFile.getAbsolutePath()));
			}
			
		}
		return bundles;
	}

	/**
	 * Verify that the BundleContext is available.
	 */
	@Test
	public void testBundleContextAvailability() {
		Assert.assertNotNull(bundleContext);
	}

}
