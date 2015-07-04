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
