package com.swissbit.server.ws.controller;

import static com.swissbit.server.ws.util.LogUtil.generateXML;
import static com.swissbit.server.ws.util.LogUtil.parseLogs;
import static spark.Spark.get;

import com.swissbit.server.ws.services.IAbstractService;
import com.swissbit.server.ws.services.ILogService;;

public class LogController extends AbstractController {

	@Override
	public void apply(final IAbstractService iAbstractService) {

		final ILogService mqttService = (ILogService) iAbstractService;

		get("/logs/:rPiMacAddress/:type", (req, res) -> {
			res.type("text/xml");
			final String rPiMacAddress = req.params(":rPiMacAddress");
			final String type = req.params(":type");
			final String[] logs = mqttService.getLogs(rPiMacAddress);

			if ("code".equals(type)) {
				return logs[1];
			}
			if ("app".equals(type)) {
				return generateXML(parseLogs(logs[0]).getLogs());
			}
			return null;
		});

	}
}