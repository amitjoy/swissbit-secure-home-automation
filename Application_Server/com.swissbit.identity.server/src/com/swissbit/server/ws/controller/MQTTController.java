package com.swissbit.server.ws.controller;

import static com.swissbit.server.ws.util.JsonUtil.json;
import static spark.Spark.get;

import com.swissbit.server.ws.ControllerBuilder;
import com.swissbit.server.ws.services.IAbstractService;
import com.swissbit.server.ws.services.IMQTTService;
import com.swissbit.server.ws.services.IRaspberryPiService;
import com.swissbit.server.ws.services.impl.RaspberryPiService;

public class MQTTController extends AbstractController {

	@Override
	public void apply(final IAbstractService iAbstractService) {

		final IMQTTService mqttService = (IMQTTService) iAbstractService;

		// Used to ask the provided Raspberry Pi whether the Mobile client who
		// is trying to add it, is authorized (mainly used by mobile client)
		get("/addRPi/:rPiMacAddress/:encryptedMobileClientMacAddress", (req, res) -> {
			final String rPiMacAddress = req.params(":rPiMacAddress");
			final String encryptedMobileClientMacAddress = req.params(":encryptedMobileClientMacAddress");
			if (mqttService.verifyClient(encryptedMobileClientMacAddress, rPiMacAddress)) {
				final ControllerBuilder<RaspberryPiController, RaspberryPiService> rpiBuilder = new ControllerBuilder<RaspberryPiController, RaspberryPiService>(
						RaspberryPiController.class, RaspberryPiService.class);
				final IRaspberryPiService raspService = rpiBuilder.buildService();
				return raspService.validateRaspberryPi(rPiMacAddress);
			}
			return false;
		} , json());

	}
}