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
import com.swissbit.server.ws.services.ICustomerService;

public class CustomerService implements ICustomerService {

	private ConnectionSource connectionSource = null;
	private Dao<Customer, String> piDao = null;

	public CustomerService() {
		try {
			this.connectionSource = new JdbcConnectionSource(DB_URL);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		((JdbcConnectionSource) this.connectionSource).setUsername(MYSQL_USER);
		((JdbcConnectionSource) this.connectionSource).setPassword(MYSQL_PASSWORD);
		try {
			TableUtils.createTableIfNotExists(this.connectionSource, Customer.class);
			this.piDao = DaoManager.createDao(this.connectionSource, Customer.class);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Customer createUser(final String name, final String email, final String username, final String password,
			final String pin) {
		this.failIfInvalid(name, email, username, password, pin);
		final Customer rasp = new Customer();
		rasp.setId(UUID.randomUUID().toString());
		rasp.setEmail(email);
		rasp.setName(name);
		rasp.setUsername(username);
		rasp.setPassword(password);
		rasp.setPin(pin);
		try {
			this.piDao.create(rasp);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		try {
			this.connectionSource.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return rasp;
	}

	private void failIfInvalid(final String name, final String email, final String username, final String password,
			final String pin) {
		if ((name == null) || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' cannot be empty");
		}
		if ((email == null) || email.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'email' cannot be empty");
		}
		if ((username == null) || username.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'username' cannot be empty");
		}
		if ((password == null) || password.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'password' cannot be empty");
		}
		if ((pin == null) || pin.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'pin' cannot be empty");
		}
	}

	@Override
	public List<Customer> getAllUsers() {

		ArrayList<Customer> userList = null;
		try {
			userList = (ArrayList<Customer>) this.piDao.queryForAll();
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}
		if (userList.size() > 0) {

		}
		try {
			this.connectionSource.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}

	@Override
	public Customer getUser(final String name) {
		final QueryBuilder<Customer, String> queryBuilder = this.piDao.queryBuilder();
		List<Customer> raspPi = null;
		Customer pi = null;
		try {
			raspPi = this.piDao.query(queryBuilder.where().eq("pin", name).prepare());

			if (raspPi.size() > 0) {
				pi = raspPi.get(0);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			return pi;
		} finally {
			try {
			this.connectionSource.close();
			} catch (final SQLException e) {
				e.printStackTrace();
				return pi;
			}
		}
		return pi;
	}
	
	@Override
	public Customer getCustomer(final String field, final String value) {
		final QueryBuilder<Customer, String> queryBuilder = this.piDao.queryBuilder();
		List<Customer> custList = null;
		Customer cust = null;
		try {
			custList = this.piDao.query(queryBuilder.where().eq(field, value).prepare());

			if (custList.size() > 0) {
				cust = custList.get(0);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			return cust;
		}
		return cust;
	}
	
	@Override
	public Customer updateUser(final String id, final String name, final String email, final String username,
			final String password, final String pin) {
		final Customer user = this.getCustomer("id", id);
		if (user == null) {
			throw new IllegalArgumentException("No user with id '" + id + "' found");
		}
		this.failIfInvalid(name, email, username, password, pin);
		user.setName(name);
		user.setEmail(email);
		user.setUsername(username);
		user.setPassword(password);
		user.setPin(pin);

		try {
			this.piDao.update(user);
		} catch (final SQLException e) {
			e.printStackTrace();
			return user;
		}
		return user;
	}
	
	@Override
	public Customer deleteUser(final String id) {
		final Customer user = this.getCustomer("id", id);
		if (user == null) {
			throw new IllegalArgumentException("No user with id '" + id + "' found");
		}

		try {
			this.piDao.delete(user);
		} catch (final SQLException e) {
			e.printStackTrace();
			return user;
		}
		return user;
	}
	
}
