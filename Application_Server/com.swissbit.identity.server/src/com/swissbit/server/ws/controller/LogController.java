package com.swissbit.server.ws.controller;

import static com.swissbit.server.ws.util.GZIPUtil.compressStringToGZip;
import static com.swissbit.server.ws.util.JsonUtil.json;
import static spark.Spark.get;

import com.swissbit.server.ws.services.IAbstractService;
import com.swissbit.server.ws.services.ILogService;

public class LogController extends AbstractController {

	@Override
	public void apply(final IAbstractService iAbstractService) {

		final ILogService mqttService = (ILogService) iAbstractService;

		get("/logs/:rPiMacAddress", (req, res) -> {
			final String rPiMacAddress = req.params(":rPiMacAddress");
			final String[] logs = mqttService.getLogs(rPiMacAddress);
			return compressStringToGZip(logs);
		} , json());

	}
}