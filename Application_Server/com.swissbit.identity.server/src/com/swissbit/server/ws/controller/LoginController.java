package com.swissbit.server.ws.controller;

import static com.swissbit.server.ws.util.JsonUtil.json;
import static com.swissbit.server.ws.util.JsonUtil.toJson;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;

import com.swissbit.server.ws.error.ResponseError;
import com.swissbit.server.ws.model.Admin;
import com.swissbit.server.ws.services.IAbstractService;
import com.swissbit.server.ws.services.ILoginService;

public class LoginController extends AbstractController {

	@Override
	public void apply(final IAbstractService iAbstractService) {

		final ILoginService adminService = (ILoginService) iAbstractService;
		
		// Used to check whether the admin user exits and is entering the right
		// Password.
		get("/login/:id", (req, res) -> {
			final String id = req.params(":id");
			final Admin adminid = adminService.getId(id);
			if (adminid != null) {
				return adminid;
			}
			res.status(400);
			return new ResponseError("No Adminstrator with ID '%s' found", id);
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