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
package com.swissbit.server.ws.services.impl;

import static com.swissbit.server.ws.Constants.DB_URL;
import static com.swissbit.server.ws.Constants.MYSQL_PASSWORD;
import static com.swissbit.server.ws.Constants.MYSQL_USER;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.swissbit.server.ws.model.Customer;
import com.swissbit.server.ws.model.RaspberryPi;
import com.swissbit.server.ws.services.IRaspberryPiService;;

public class RaspberryPiService implements IRaspberryPiService {

	private ConnectionSource connectionSource = null;

	private Dao<RaspberryPi, String> piDao = null;

	public RaspberryPiService() {
		try {
			this.connectionSource = new JdbcConnectionSource(DB_URL);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		((JdbcConnectionSource) this.connectionSource).setUsername(MYSQL_USER);
		((JdbcConnectionSource) this.connectionSource).setPassword(MYSQL_PASSWORD);
		try {
			TableUtils.createTableIfNotExists(this.connectionSource, RaspberryPi.class);
			this.piDao = DaoManager.createDao(this.connectionSource, RaspberryPi.class);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public RaspberryPi createRaspberryPi(final String customer, final String id, final String name, final String pin, final String macaddr) {
		this.failIfInvalid(customer, name, pin, macaddr);
		final RaspberryPi rasp = new RaspberryPi();
		rasp.setCustomer(customer);
		rasp.setId(id);
		rasp.setName(name);
		rasp.setPin(pin);
		rasp.setMacAddr(macaddr);
		rasp.setValidated("1");

		try {
			this.piDao.create(rasp);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return rasp;
	}

	private void failIfInvalid(final String customer, final String name, final String pin, final String macaddr) {
		if ((customer == null) || customer.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'customer' cannot be empty");
		}
		if ((name == null) || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' cannot be empty");
		}
		if ((pin == null) || pin.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'pin' cannot be empty");
		}
		if ((macaddr == null) || macaddr.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'macaddr' cannot be empty");
		}
	}

	@Override
	public List<RaspberryPi> getAllRaspberryPi() {

		ArrayList<RaspberryPi> piList = null;
		try {
			piList = (ArrayList<RaspberryPi>) this.piDao.queryForAll();
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}
		if (piList.size() > 0) {

		}
		return piList;
	}

	@Override
	public RaspberryPi getRaspberryPi(final String field, final String value) {
		final QueryBuilder<RaspberryPi, String> queryBuilder = this.piDao.queryBuilder();
		List<RaspberryPi> raspPi = null;
		RaspberryPi pi = null;
		try {
			raspPi = this.piDao.query(queryBuilder.where().eq(field, value).prepare());

			if (raspPi.size() > 0) {
				pi = raspPi.get(0);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			return pi;
		}
		return pi;
	}

	@Override
	public RaspberryPi updateRaspberryPi(final String id, final String name, final String pin) {
		final RaspberryPi rasp = this.getRaspberryPi("id", id);
		if (rasp == null) {
			throw new IllegalArgumentException("No user with id '" + id + "' found");
		}

		this.failIfInvalid(rasp.getCustomer() ,name, pin, rasp.getMacAddr());
		rasp.setName(name);
		rasp.setPin(pin);

		try {
			this.piDao.update(rasp);
		} catch (final SQLException e) {
			e.printStackTrace();
			return rasp;
		}
		return rasp;
	}
	
	@Override
	public RaspberryPi deleteRaspberryPi(final String id) {
		final RaspberryPi rasp = this.getRaspberryPi("id", id);
		if (rasp == null) {
			throw new IllegalArgumentException("No Raspberry Pi  with id '" + id + "' found");
		}

		try {
			this.piDao.delete(rasp);
		} catch (final SQLException e) {
			e.printStackTrace();
			return rasp;
		}
		return rasp;
	}

	@Override
	public boolean validateRaspberryPi(final String macAddr) {

		final RaspberryPi rasp = this.getRaspberryPi("macaddr", macAddr);
		if (rasp == null) {
			throw new IllegalArgumentException("No Raspberry Pi with Mac Address " + macAddr + " found");
		}

		// Validated = 0 means is verified and added by customer.
		rasp.setValidated("0");

		try {
			this.piDao.update(rasp);

		} catch (final SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
