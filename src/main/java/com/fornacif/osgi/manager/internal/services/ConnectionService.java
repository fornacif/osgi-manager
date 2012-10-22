package com.fornacif.osgi.manager.internal.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.models.ConnectionModel;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

@Component(name="ConnectionService", provide=ConnectionService.class)
public class ConnectionService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private ConfigurationService configurationService;
	
	@Reference
	private void bindConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public Callable<List<ConnectionModel>> listConnections() {
		return new Callable<List<ConnectionModel>>() {
			@Override
			public List<ConnectionModel> call() throws Exception {
				return listVirtualMachines(VirtualMachine.list());
			}
		};
	}

	public Callable<List<Void>> configureConnection(final VirtualMachine virtualMachine) {
		return new Callable<List<Void>>() {
			@Override
			public List<Void> call() throws Exception {
				Map<String, Object> properties = new HashMap<>();
				Properties vmProperties = virtualMachine.getAgentProperties();
				properties.put(JMXService.JMX_SERVICE_URL, vmProperties.getProperty("com.sun.management.jmxremote.localConnectorAddress"));
				configurationService.configure("/configurations/JMXService.properties", properties);
				return null;
			}
		};
	}

	private List<ConnectionModel> listVirtualMachines(List<VirtualMachineDescriptor> virtualMachineDescriptors) {
		List<ConnectionModel> virtualMachines = new ArrayList<>();
		for (VirtualMachineDescriptor virtualMachineDescriptor : virtualMachineDescriptors) {
			try {
				VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
				ConnectionModel virtualMachineModel = new ConnectionModel();
				virtualMachineModel.setId(virtualMachineDescriptor.id());
				virtualMachineModel.setName(virtualMachineDescriptor.displayName());
				virtualMachineModel.setVirtualMachine(virtualMachine);
				virtualMachines.add(virtualMachineModel);
			} catch (AttachNotSupportedException | IOException e) {
				LOGGER.debug("Unable to attach Java process {}", virtualMachineDescriptor.id());
			}	
		}
		return virtualMachines;
	}	

}
