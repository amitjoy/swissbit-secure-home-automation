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

import com.swissbit.server.ws.controller.AbstractController;
import com.swissbit.server.ws.services.IAbstractService;

public final class ControllerBuilder<T extends AbstractController, U extends IAbstractService> {

	private final Class<T> clazz;
	private final Class<U> clazzz;

	public ControllerBuilder(final Class<T> clazz, final Class<U> clazzz) {
		this.clazz = clazz;
		this.clazzz = clazzz;
	}

	public T buildController() {
		try {
			return this.clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public U buildService() {
		try {
			return this.clazzz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
