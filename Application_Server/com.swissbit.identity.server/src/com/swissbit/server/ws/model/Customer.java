package com.swissbit.server.ws.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "customer")
public class Customer {

	@DatabaseField
	private String email;

	@DatabaseField
	private String id;

	@DatabaseField
	private String name;

	@DatabaseField
	private String password;

	@DatabaseField
	private String pin;

	@DatabaseField
	private String username;

	public Customer() {

	}

	public String getEmail() {
		return this.email;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getPassword() {
		return this.password;
	}

	public String getPin() {
		return this.pin;
	}

	public String getUsername() {
		return this.username;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setPin(final String pin) {
		this.pin = pin;
	}

	public void setUsername(final String username) {
		this.username = username;
	}
}