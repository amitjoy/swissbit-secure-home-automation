package com.swissbit.server.ws.controller;

import static com.swissbit.server.ws.util.JsonUtil.json;
import static com.swissbit.server.ws.util.JsonUtil.toJson;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import com.swissbit.server.ws.ControllerBuilder;
import com.swissbit.server.ws.error.ResponseError;
import com.swissbit.server.ws.model.RaspberryPi;
import com.swissbit.server.ws.services.IAbstractService;
import com.swissbit.server.ws.services.IRaspberryPiService;
import com.swissbit.server.ws.services.impl.RaspberryPiService;

public class RaspberryPiController extends AbstractController {

	@Override
	public void apply(final IAbstractService iAbstractService) {

		final IRaspberryPiService raspberryPiService = (IRaspberryPiService) iAbstractService;

		// Used to show all the Raspberry Pi by the Front-end UI
		get("/pis", (req, res) -> raspberryPiService.getAllRaspberryPi(), json());

		// Used to check whether the QR Code is owned by genuine user (mainly
		// used by Mobile Client)
		get("/pi/:id", (req, res) -> {
			final String id = req.params(":id");
			final RaspberryPi rasp = raspberryPiService.getRaspberryPi("id", id);
			if (rasp != null) {
				return rasp;
			}
			res.status(400);
			return new ResponseError("No Raspberry Pi with id '%s' found", id);
		} , json());
		
		get("/addPi/:rPiMacAddress", (req, res) -> {
			final String rPiMacAddress = req.params(":rPiMacAddress");
			return raspberryPiService.validateRaspberryPi(rPiMacAddress);
		} , json());

		// Used to create Raspberry Pi details (used at the Front-End UI)
		post("/pi", (req, res) -> raspberryPiService.createRaspberryPi(req.queryParams("name"), 
				req.queryParams("pin"), req.queryParams("macaddr")), json());

		// Used to update Raspberry Pi details (used at the front-End UI)
		put("/pi/:id", (req, res) -> raspberryPiService.updateRaspberryPi(req.params(":id"), req.queryParams("name"),
				req.queryParams("pin")), json());

		after((req, res) -> {
			res.type("application/json");
		});

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});

	}
}