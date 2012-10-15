package com.fornacif.osgi.manager.internal;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Callback;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(name = "ControllerFactory", provide = { ControllerFactory.class })
public class ControllerFactory implements Callback<Class<?>, Object> {
	private List<Controller> controllerComponents = new ArrayList<>();

	@Reference(multiple=true)
	public void bindBundleController(Controller controllerComponent) {
		this.controllerComponents.add(controllerComponent);
	}

	@Override
	public Object call(Class<?> clazz) {
		for (Controller controllerComponent : controllerComponents) {
			if (controllerComponent.getClass() == clazz) {
				return controllerComponent;
			}
		}
		// Default instantiation mechanism
		return null;
	}

}
