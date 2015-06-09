package com.swissbit.server.ws;

import com.swissbit.server.ws.controller.CustomerController;
import com.swissbit.server.ws.controller.MQTTController;
import com.swissbit.server.ws.controller.RaspberryPiController;
import com.swissbit.server.ws.services.impl.CustomerService;
import com.swissbit.server.ws.services.impl.MQTTService;
import com.swissbit.server.ws.services.impl.RaspberryPiDataService;

public class SwissbitServiceStarter {

	public static void main(final String... args) {

		// Building customer controller
		final ControllerBuilder<CustomerController, CustomerService> customerBuilder = new ControllerBuilder<CustomerController, CustomerService>(
				CustomerController.class, CustomerService.class);

		customerBuilder.buildController().apply(customerBuilder.buildService());

		// Building MQTT controller
		final ControllerBuilder<MQTTController, MQTTService> mqttBuilder = new ControllerBuilder<MQTTController, MQTTService>(
				MQTTController.class, MQTTService.class);

		mqttBuilder.buildController().apply(mqttBuilder.buildService());

		// Building RPi controller
		final ControllerBuilder<RaspberryPiController, RaspberryPiDataService> rpiBuilder = new ControllerBuilder<RaspberryPiController, RaspberryPiDataService>(
				RaspberryPiController.class, RaspberryPiDataService.class);

		rpiBuilder.buildController().apply(rpiBuilder.buildService());

	}

}
