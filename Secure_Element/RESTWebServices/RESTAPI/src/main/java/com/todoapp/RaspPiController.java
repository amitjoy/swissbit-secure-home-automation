package com.todoapp;

import static spark.Spark.*;
import static com.todoapp.JsonUtil.*;


public class RaspPiController {

	public RaspPiController(final RaspPiService userService) {

		get("/pi", (req, res) -> userService.getAllUsers(), json());

		get("/pi/:id", (req, res) -> {
			String id = req.params(":id");
			RaspPi user = userService.getRaspPi(id);
			if (user != null) {
				return user;
			}
			res.status(400);
			return new ResponseError("No Raspberry Pi with id '%s' found", id);
		}, json());

		post("/pi", (req, res) -> userService.createUser(
				req.queryParams("name"),
				req.queryParams("email"),
				req.queryParams("username"),
				req.queryParams("password"),
				req.queryParams("pin")
		), json());

		put("/pi/:id", (req, res) -> userService.updateUser(
				req.params(":id"),
				req.queryParams("name"),
				req.queryParams("email"),
				req.queryParams("username"),
				req.queryParams("password"),
				req.queryParams("pin")
		), json());

		after((req, res) -> {
			res.type("application/json");
		});

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});
	}
}