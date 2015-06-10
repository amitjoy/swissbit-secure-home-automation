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
	private String macaddr;

	@DatabaseField
	private String name;

	@DatabaseField
	private String pin;

	/* 
	 * Validated = 0 means is verified and added by customer.
	 * Validated = 1 means is not yet verified by customer. (Default)
	 * When the Swissbit sells System it enters with Validated = 1
	 */
	
	@DatabaseField
	private String validated;

	public RaspberryPi() {

	}

	public String getCustomer() {
		return this.customer;
	}
	
	public String getId() {
		return this.id;
	}

	public String getMacAddr() {
		return this.macaddr;
	}

	public String getName() {
		return this.name;
	}

	public String getPin() {
		return this.pin;
	}
	
	public String getValidated() {
		return this.validated;
	}

	public void setCustomer(final String customer) {
		this.customer = customer;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setMacAddr(final String macaddr) {
		this.macaddr = macaddr;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPin(final String pin) {
		this.pin = pin;
	}

	public void setValidated(final String validated) {
		this.validated = validated;
	}
}