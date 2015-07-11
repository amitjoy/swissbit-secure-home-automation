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
import com.swissbit.server.ws.services.IAdminService;

public class AdminController extends AbstractController {
	
	@Override
	public void apply(final IAbstractService iAbstractService) {

		final IAdminService adminService = (IAdminService) iAbstractService;
		
		
		// Used to show all the admins by the Front-end UI
		get("/admins", (req, res) -> adminService.getAllUsers(), json());
		
		
		// Used to create admin details (used at the Front-End UI)
		post("/admin", (req, res) -> adminService.createUser(req.queryParams("email"), req.queryParams("id"), req.queryParams("fname"), 
				req.queryParams("lname"), req.queryParams("password")), json());
		
		after((req, res) -> {
			res.type("application/json");
		});

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});
	}
}

