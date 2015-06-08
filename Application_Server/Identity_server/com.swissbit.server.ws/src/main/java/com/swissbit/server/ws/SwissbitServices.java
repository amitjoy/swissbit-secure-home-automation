package com.swissbit.server.ws;

import com.swissbit.server.ws.controller.CustomerController;
import com.swissbit.server.ws.controller.MQTTController;
import com.swissbit.server.ws.services.impl.ClientDataService;
import com.swissbit.server.ws.services.impl.MQTTService;

public class SwissbitServices {

	public static void main(final String... args) {

		ControllerBuilder.getInstance().addController(new CustomerController(new ClientDataService()))
				.addController(new MQTTController(new MQTTService()));
	}

}
