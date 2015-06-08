package com.swissbit.server.ws;

import com.swissbit.server.ws.services.impl.ClientDataService;
import com.swissbit.server.ws.services.impl.MQTTService;

public class SwissbitServices {

	public static void main(final String[] args) {

		new RaspPiController(new ClientDataService());
		new MQTTController(new MQTTService());

	}

}
