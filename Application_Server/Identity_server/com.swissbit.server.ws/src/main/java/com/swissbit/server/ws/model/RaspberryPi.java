package com.swissbit.server.ws.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "raspberrypi")
public class RaspberryPi {

	@DatabaseField
	private String customer;

	@DatabaseField
	private String id;

	@DatabaseField
	private String name;

	@DatabaseField
	private String pin;

	
	public RaspberryPi() {

	}

	public String getCustomer() {
		return this.customer;
	}
	
	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getPin() {
		return this.pin;
	}

	public void setCustomer(final String customer) {
		this.customer = customer;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPin(final String pin) {
		this.pin = pin;
	}
}