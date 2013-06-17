package com.fornacif.osgi.manager.itests;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.vmOption;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

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
import org.osgi.framework.BundleContext;

@RunWith(PaxExam.class)
public class OSGiManagerTest {

	@Inject
	private BundleContext bundleContext;

	@Configuration
	public Option[] configuration() throws Exception {
		List<Option> options = new ArrayList<>();
		options.add(junitBundles());
		options.add(vmOption("-Xmx512m"));
		options.addAll(properties());
		
		return options.toArray(new Option[options.size()]);
	}

	public List<Option> properties() throws Exception {
		File configFile = new File("target/dependency/osgi.manager.assembly-0.1.0-SNAPSHOT/configuration/config.ini");
		Assert.assertTrue(configFile.exists());
		
		Properties properties = new Properties();
		properties.load(new FileInputStream(configFile));
		Assert.assertTrue(properties.size() > 0);
		
		List<Option> options = new ArrayList<>();
		for (Object key : properties.keySet()) {
			options.add(systemProperty((String) key).value(properties.getProperty((String) key)));
		}
		
		System.out.println(options);
		
		return options;
	}

	/**
	 * Verify that the BundleContext is available.
	 */
	@Test
	public void testBundleContextAvailability() {
		Assert.assertNotNull(bundleContext);
	}

}
