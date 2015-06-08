package com.swissbit.server.ws;

import static com.swissbit.server.ws.util.JsonUtil.json;
import static com.swissbit.server.ws.util.JsonUtil.toJson;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import com.swissbit.server.ws.error.ResponseError;
import com.swissbit.server.ws.model.RaspPi;
import com.swissbit.server.ws.services.IClientDataService;

public class RaspPiController {

	public RaspPiController(final IClientDataService userService) {

		// Used to show all the users by the Front-end UI
		get("/pi", (req, res) -> userService.getAllUsers(), json());

		// Used to check whether the QR Code is owned by genuine user (mainly
		// used by Mobile Client)
		get("/pi/:id", (req, res) -> {
			final String id = req.params(":id");
			final RaspPi user = userService.getRaspPi(id);
			if (user != null) {
				return user;
			}
			res.status(400);
			return new ResponseError("No Raspberry Pi with id '%s' found", id);
		} , json());

		// Used to create user details (used at the Front-End UI)
		post("/pi", (req, res) -> userService.createUser(req.queryParams("name"), req.queryParams("email"),
				req.queryParams("username"), req.queryParams("password"), req.queryParams("pin")), json());

		// Used to update user details (used at the front-End UI)
		put("/pi/:id",
				(req, res) -> userService.updateUser(req.params(":id"), req.queryParams("name"),
						req.queryParams("email"), req.queryParams("username"), req.queryParams("password"),
						req.queryParams("pin")),
				json());

		after((req, res) -> {
			res.type("application/json");
		});

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});
	}
}