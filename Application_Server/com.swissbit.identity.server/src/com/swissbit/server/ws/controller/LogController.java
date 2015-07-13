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

import static spark.Spark.get;

import com.swissbit.server.ws.services.IAbstractService;
import com.swissbit.server.ws.services.ILogService;;

public class LogController extends AbstractController {

	@Override
	public void apply(final IAbstractService iAbstractService) {

		final ILogService mqttService = (ILogService) iAbstractService;

		get("/logs/:rPiMacAddress/:type", (req, res) -> {
			res.type("text/xml");
			final String rPiMacAddress = req.params(":rPiMacAddress");
			final String type = req.params(":type");
			final String[] logs = mqttService.getLogs(rPiMacAddress);

			if ("code".equals(type)) {
				return logs[1];
			}
			if ("app".equals(type)) {
				return logs[0];
			}
			return null;
		});

	}
}