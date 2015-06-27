package com.swissbit.server.ws.controller;

import static spark.Spark.get;

import com.swissbit.server.ws.services.IAbstractService;
import com.swissbit.server.ws.services.ILogService;

public class LogController extends AbstractController {

	@Override
	public void apply(final IAbstractService iAbstractService) {

		final ILogService mqttService = (ILogService) iAbstractService;

		get("/logs/:rPiMacAddress/:type", (req, res) -> {
			res.type("text/plain");
			final String rPiMacAddress = req.params(":rPiMacAddress");
			final String type = req.params(":type");
			final String[] logs = mqttService.getLogs(rPiMacAddress);
			if ("code".equals(type)) {
				return logs[1];
			}
			if ("app".equals(type)) {
				System.out.println(logs[0]);
				return logs[0];
			}
			return null;
		});

	}
}