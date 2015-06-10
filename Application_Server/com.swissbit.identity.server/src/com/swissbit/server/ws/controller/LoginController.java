package com.swissbit.server.ws.controller;

import static com.swissbit.server.ws.util.JsonUtil.json;
import static com.swissbit.server.ws.util.JsonUtil.toJson;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;

import com.swissbit.server.ws.error.ResponseError;
import com.swissbit.server.ws.model.Customer;
import com.swissbit.server.ws.services.IAbstractService;
import com.swissbit.server.ws.services.ILoginService;

public class LoginController extends AbstractController {

	@Override
	public void apply(final IAbstractService iAbstractService) {

		final ILoginService customerService = (ILoginService) iAbstractService;

		// Used to check whether the admin user exits and is entering the right
		// Password.
		get("/login/:id", (req, res) -> {
			final String id = req.params(":id");
			final Customer user = customerService.getUser(id);
			if (user != null) {
				return user;
			}
			res.status(400);
			return new ResponseError("No Adminstrator with username '%s' found", id);
		} , json());

		after((req, res) -> {
			res.type("application/json");
		});

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});

	}
}