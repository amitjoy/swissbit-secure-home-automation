package com.todoapp;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "rasppi")
public class RaspPi {
	
	@DatabaseField
	private String id;
	
	@DatabaseField
	private String name;
	
	@DatabaseField
	private String email;
	
	@DatabaseField
	private String username;
	
	@DatabaseField
	private String password;
	
	@DatabaseField
	private String pin;

	public RaspPi(){
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
}