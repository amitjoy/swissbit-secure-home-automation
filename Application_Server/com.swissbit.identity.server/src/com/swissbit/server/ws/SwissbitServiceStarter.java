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
package com.swissbit.server.ws;

import static com.swissbit.server.ws.Constants.IP_ADDRESS;
import static com.swissbit.server.ws.Constants.PORT;

import com.swissbit.server.ws.controller.AdminController;
import com.swissbit.server.ws.controller.CustomerController;
import com.swissbit.server.ws.controller.LogController;
import com.swissbit.server.ws.controller.LoginController;
import com.swissbit.server.ws.controller.RaspberryPiController;
import com.swissbit.server.ws.services.impl.AdminService;
import com.swissbit.server.ws.services.impl.CustomerService;
import com.swissbit.server.ws.services.impl.LogService;
import com.swissbit.server.ws.services.impl.LoginService;
import com.swissbit.server.ws.services.impl.RaspberryPiService;

import spark.Spark;

public class SwissbitServiceStarter {

	public static void main(final String... args) {

		Spark.port(new Integer(PORT));
		Spark.ipAddress(IP_ADDRESS);
		Spark.staticFileLocation("/resources/public");

		// Building customer controller
		final ControllerBuilder<CustomerController, CustomerService> customerBuilder = new ControllerBuilder<CustomerController, CustomerService>(
				CustomerController.class, CustomerService.class);

		customerBuilder.buildController().apply(customerBuilder.buildService());

		// Building RPi controller
		final ControllerBuilder<RaspberryPiController, RaspberryPiService> rpiBuilder = new ControllerBuilder<RaspberryPiController, RaspberryPiService>(
				RaspberryPiController.class, RaspberryPiService.class);

		rpiBuilder.buildController().apply(rpiBuilder.buildService());

		// Login controller
		final ControllerBuilder<LoginController, LoginService> loginBuilder = new ControllerBuilder<LoginController, LoginService>(
				LoginController.class, LoginService.class);

		loginBuilder.buildController().apply(loginBuilder.buildService());

		// Admin controller
		final ControllerBuilder<AdminController, AdminService> adminBuilder = new ControllerBuilder<AdminController, AdminService>(
				AdminController.class, AdminService.class);

		adminBuilder.buildController().apply(adminBuilder.buildService());

		// Building Log controller
		final ControllerBuilder<LogController, LogService> logBuilder = new ControllerBuilder<LogController, LogService>(
				LogController.class, LogService.class);

		logBuilder.buildController().apply(logBuilder.buildService());

	}

}
