package com.fornacif.osgi.manager.internal.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;

import com.fornacif.osgi.manager.internal.models.ConnectionModel;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

@Component(name = "ConnectionService", provide = ConnectionService.class, configurationPolicy = ConfigurationPolicy.require)
public class ConnectionService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private static final String INITIAL_JMX_PORT = "intial.jmx.port";

	private ObjectName bundleStateObjectName;
	private ObjectName frameworkObjectName;

	private JMXConnector jmxConnector;

	private BundleContext bundleContext;

	private ServiceRegistration<JMXService> serviceRegistration;

	private int initialJMXPort;

	@Activate
	private void activate(BundleContext bundleContext, Map<String, ?> properties) throws IOException, MalformedObjectNameException {
		this.bundleContext = bundleContext;
		this.bundleStateObjectName = new ObjectName(BundleStateMBean.OBJECTNAME);
		this.frameworkObjectName = new ObjectName(FrameworkMBean.OBJECTNAME);
		this.initialJMXPort = Integer.valueOf((String) properties.get(INITIAL_JMX_PORT));
	}

	public List<ConnectionModel> listLocalConnections() throws Exception {
		return listLocalVirtualMachines(VirtualMachine.list());
	}

	public void connect(ConnectionModel connectionModel) throws Exception {
		disconnect();

		jmxConnector = JMXConnectorFactory.connect(new JMXServiceURL(connectionModel.getUrl()));
		MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();

		if (mbeanServerConnection.isRegistered(frameworkObjectName)) {
			JMXService jmxService = new JMXService(mbeanServerConnection, frameworkObjectName, bundleStateObjectName);
			serviceRegistration = bundleContext.registerService(JMXService.class, jmxService, null);
		} else {
			throw new Exception("Connection not open due to missing OSGi MBeans");
		}
	}

	public void disconnect() throws IOException {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
			serviceRegistration = null;
		}
		if (jmxConnector != null) {
			jmxConnector.close();
			jmxConnector = null;
		}
	}

	private List<ConnectionModel> listLocalVirtualMachines(List<VirtualMachineDescriptor> virtualMachineDescriptors) throws Exception {
		List<ConnectionModel> virtualMachines = new ArrayList<>();
		for (VirtualMachineDescriptor virtualMachineDescriptor : virtualMachineDescriptors) {
			VirtualMachine virtualMachine = null;
			try {
				virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);

				Properties agentProperties = virtualMachine.getAgentProperties();
				String jmxServiceURL = agentProperties.getProperty("com.sun.management.jmxremote.localConnectorAddress");

				if (jmxServiceURL == null) {
					Properties systemProperties = virtualMachine.getSystemProperties();
					String home = systemProperties.getProperty("java.home");
					String agent = home + File.separator + "lib" + File.separator + "management-agent.jar";
					virtualMachine.loadAgent(agent, "com.sun.management.jmxremote.port=" + initialJMXPort + ",com.sun.management.jmxremote.authenticate=false,com.sun.management.jmxremote.ssl=false");
					initialJMXPort++;
				}

				agentProperties = virtualMachine.getAgentProperties();
				jmxServiceURL = agentProperties.getProperty("com.sun.management.jmxremote.localConnectorAddress");
				if (jmxServiceURL != null) {
					ConnectionModel virtualMachineModel = new ConnectionModel();
					virtualMachineModel.setId(virtualMachineDescriptor.id());
					virtualMachineModel.setName(virtualMachineDescriptor.displayName());
					virtualMachineModel.setUrl(jmxServiceURL);
					virtualMachines.add(virtualMachineModel);
				} else {
					LOGGER.info("No JMX agent found for VM ID {}", virtualMachine.id());
				}

			} catch (AttachNotSupportedException | IOException e) {
				LOGGER.info("Unable to attach Java process {}", virtualMachineDescriptor.id());
			} catch (AgentLoadException | AgentInitializationException e) {
				LOGGER.info("Unable to load the management agent for Java process {}", virtualMachineDescriptor.id());
			} finally {
				if (virtualMachine != null) {
					try {
						virtualMachine.detach();
					} catch (IOException e) {
						LOGGER.info("Unable to dettach VM ID {}", virtualMachine.id());
					}
				}
			}
		}
		return virtualMachines;
	}

}
