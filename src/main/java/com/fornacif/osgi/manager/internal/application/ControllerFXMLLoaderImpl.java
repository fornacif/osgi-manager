package com.fornacif.osgi.manager.internal.application;

import java.io.IOException;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import aQute.bnd.annotation.component.Component;

import com.fornacif.osgi.manager.service.ControllerFXMLLoader;

@Component
public class ControllerFXMLLoaderImpl implements ControllerFXMLLoader {

	@Override
	public void loadFXML(Pane pane, Map<String, ?> properties) throws IOException {
		String fxml = (String) properties.get(FXML);
		FXMLLoader fxmlLoader = new FXMLLoader(pane.getClass().getResource(fxml));
		fxmlLoader.setRoot(pane);
		fxmlLoader.setController(pane);
		fxmlLoader.load();
	}

}
