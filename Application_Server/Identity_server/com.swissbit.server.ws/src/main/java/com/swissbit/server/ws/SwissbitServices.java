package com.swissbit.server.ws;

import com.swissbit.server.ws.controller.CustomerController;
import com.swissbit.server.ws.controller.MQTTController;
import com.swissbit.server.ws.controller.RaspberryPiController;
import com.swissbit.server.ws.services.impl.ClientDataService;
import com.swissbit.server.ws.services.impl.MQTTService;
import com.swissbit.server.ws.services.impl.RaspberryPiDataService;

public class SwissbitServices {

	public static void main(final String... args) {

		ControllerBuilder.getInstance().addController(new CustomerController(new ClientDataService()))
				.addController(new MQTTController(new MQTTService()))
				.addController(new RaspberryPiController(new RaspberryPiDataService()));
	}

}
