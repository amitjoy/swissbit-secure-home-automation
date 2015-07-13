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

import com.swissbit.server.ws.model.Customer;
import com.swissbit.server.ws.model.RaspberryPi;

public interface ICustomerService extends IAbstractService {

	public Customer createUser(String name, String email, String username, String password, String pin);

	public List<Customer> getAllUsers();

	public Customer getUser(String name);
	
	public Customer getCustomer(String field, String value);
	
	public Customer deleteUser(String id);

	public Customer updateUser(String id, String name, String email, String username, String password, String pin);

}
