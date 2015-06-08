package com.swissbit.server.ws;

import static com.swissbit.server.ws.util.JsonUtil.json;
import static spark.Spark.get;

import com.swissbit.server.ws.services.IMQTTService;

public class MQTTController {

	public MQTTController(final IMQTTService mqttService) {

		// Used to ask the provided Raspberry Pi whether the Mobile client who
		// is trying to add it, is authorized (mainly used by mobile client)
		get("/addRPi/:rPiMacAddress/:mobileClientMacAddress", (req, res) -> {
			final String rPiMacAddress = req.params(":rPiMacAddress");
			final String mobileClientMacAddress = req.params(":mobileClientMacAddress");
			return mqttService.verifyClient(mobileClientMacAddress, rPiMacAddress);
		} , json());
	}
}