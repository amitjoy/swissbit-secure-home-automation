package com.swissbit.server.ws.controller;

import static com.swissbit.server.ws.util.JsonUtil.json;
import static com.swissbit.server.ws.util.JsonUtil.toJson;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

import com.swissbit.server.ws.error.ResponseError;
import com.swissbit.server.ws.model.Admin;
import com.swissbit.server.ws.services.IAbstractService;
import com.swissbit.server.ws.services.ILoginService;

public class LoginController extends AbstractController {

	@Override
	public void apply(final IAbstractService iAbstractService) {

		final ILoginService adminService = (ILoginService) iAbstractService;
		
		// Used to check whether the admin user exists and is entering the right
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

		
		// Used to check whether the admin user exists and is entering the right
		// Password.
		get("/loginEmail/:email", (req, res) -> {
			final String email = req.params(":email");
			final Admin adminDetails = adminService.getUserByEmail(email);
			if (adminDetails != null) {
				return adminDetails;
			}
			res.status(400);
			return new ResponseError("No Adminstrator with Email '%s' found", email);
		} , json());
		
		
		// Used to authenticate admin  (used at the Front-End UI)
		post("/authenticate", (req, res) -> adminService.authenticateUser(req.queryParams("email"), req.queryParams("password")), json());

		after((req, res) -> {
			res.type("application/json");
		});

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});

	}
}