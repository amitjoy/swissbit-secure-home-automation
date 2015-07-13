/*******************************************************************************
 * Copyright 2015 Amit Kumar Mondal <admin@amitinside.com>, Subburam <subburam.rb@gmail.com>, Gaurav Srivastava <gaurav.srivastava7@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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

