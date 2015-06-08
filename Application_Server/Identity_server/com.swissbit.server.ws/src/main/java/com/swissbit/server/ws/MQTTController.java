package com.swissbit.server.ws;

import static com.swissbit.server.ws.util.JsonUtil.json;
import static spark.Spark.get;

import com.swissbit.server.ws.services.IMQTTService;

public class MQTTController {

	public MQTTController(final IMQTTService mqttService) {

		get("/pi/:mobileClientMacAddress/:rPiMacAddress", (req, res) -> {
			final String rPiMacAddress = req.params(":rPiMacAddress");
			final String mobileClientMacAddress = req.params(":mobileClientMacAddress");
			final boolean status = mqttService.verifyClient(mobileClientMacAddress, rPiMacAddress);
			return status;
		} , json());
	}
}