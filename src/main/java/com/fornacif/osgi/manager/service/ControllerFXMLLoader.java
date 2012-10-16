package com.fornacif.osgi.manager.service;

import java.io.IOException;
import java.util.Map;

import javafx.scene.layout.Pane;

public interface ControllerFXMLLoader {
	public final static String FXML = "fxml";
	
	void loadFXML(Pane pane, Map<String, ?> properties) throws IOException;
}
