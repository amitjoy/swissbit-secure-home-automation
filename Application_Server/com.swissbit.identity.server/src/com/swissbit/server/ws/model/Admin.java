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
package com.swissbit.server.ws.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "admin")
public class Admin {

	@DatabaseField
	private String email;

	@DatabaseField(id = true)
	private String id;

	@DatabaseField
	private String fname;
	
	@DatabaseField
	private String lname;

	@DatabaseField
	private String password;

	public Admin() {

	}

	public String getEmail() {
		return this.email;
	}

	public String getId() {
		return this.id;
	}

	public String getFName() {
		return this.fname;
	}

	public String getLName() {
		return this.lname;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setFName(final String fname) {
		this.fname = fname;
	}
	
	public void setLName(final String lname) {
		this.lname = lname;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

}
