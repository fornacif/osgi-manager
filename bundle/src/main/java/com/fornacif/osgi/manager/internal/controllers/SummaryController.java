package com.fornacif.osgi.manager.internal.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.fornacif.osgi.manager.internal.configurations.SummaryControllerConfiguration;
import com.fornacif.osgi.manager.internal.models.Models;
import com.fornacif.osgi.manager.internal.services.ModelsService;
import com.fornacif.osgi.manager.services.AsynchService;
import com.fornacif.osgi.manager.services.ServiceCaller;

@Component(name = "SummaryController", provide = { Pane.class }, configurationPolicy = ConfigurationPolicy.require, designate = SummaryControllerConfiguration.class)
public class SummaryController extends VBox implements Initializable {

	private ServiceCaller serviceCaller;

	private ModelsService modelsService;

	private Timeline timeline;
	
	public ModelsService getModelsService() {
		return modelsService;
	}

	@Reference
	private void bindServiceCaller(ServiceCaller serviceCaller) {
		this.serviceCaller = serviceCaller;
	}
	
	@Reference
	private void bindModelsService (ModelsService modelsService) {
		this.modelsService = modelsService;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		loadModels();
	}
	
	public void loadModels() {
		serviceCaller.execute(new AsynchService<Models>() {
			@Override
			public Models call() throws Exception {
				return modelsService.loadModels();
			}
			
			@Override
			public void succeeded(Models result) {
				modelsService.setModels(result);
				startUptimeRefresh();		
			}
		}, true, true);
	}
	
	private void startUptimeRefresh() {
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				long uptime = modelsService.getModels().getSummaryModel().getUptime();
				uptime = (long) (uptime + Duration.seconds(1).toMillis());
				modelsService.getModels().getSummaryModel().setUptime(uptime);
			}
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.playFromStart();
	}
	
	@Deactivate
	private void stopUptimeRefresh() {
		timeline.stop();
	}

}
