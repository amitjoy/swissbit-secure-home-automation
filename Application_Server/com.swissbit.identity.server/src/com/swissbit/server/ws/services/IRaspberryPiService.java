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
package com.swissbit.server.ws.services;

import java.util.List;

import com.swissbit.server.ws.model.RaspberryPi;

public interface IRaspberryPiService extends IAbstractService {

	public RaspberryPi createRaspberryPi(String customer, String id, String name, String pin, String macaddr);

	public List<RaspberryPi> getAllRaspberryPi();

	public RaspberryPi getRaspberryPi(String field, String value);

	public RaspberryPi updateRaspberryPi(String id, String name, String pin);
	
	public RaspberryPi deleteRaspberryPi(String id);

	public boolean validateRaspberryPi(String macAddr);

}
