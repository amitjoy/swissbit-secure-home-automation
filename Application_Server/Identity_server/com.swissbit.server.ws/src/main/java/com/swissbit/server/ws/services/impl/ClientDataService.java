package com.swissbit.server.ws.services.impl;

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
import com.swissbit.server.ws.model.RaspPi;
import com.swissbit.server.ws.services.IClientDataService;

public class ClientDataService implements IClientDataService {

	private ConnectionSource connectionSource = null;
	private final String databaseUrl = "jdbc:mysql://localhost/spark";
	Dao<RaspPi, String> piDao = null;

	public ClientDataService() {
		try {
			this.connectionSource = new JdbcConnectionSource(this.databaseUrl);
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((JdbcConnectionSource) this.connectionSource).setUsername("root");
		((JdbcConnectionSource) this.connectionSource).setPassword("root");
		try {
			TableUtils.createTableIfNotExists(this.connectionSource, RaspPi.class);
			this.piDao = DaoManager.createDao(this.connectionSource, RaspPi.class);
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public RaspPi createUser(final String name, final String email, final String username, final String password,
			final String pin) {
		this.failIfInvalid(name, email, username, password, pin);
		final RaspPi rasp = new RaspPi();
		rasp.setId(UUID.randomUUID().toString());
		rasp.setEmail(email);
		rasp.setName(name);
		rasp.setUsername(username);
		rasp.setPassword(password);
		rasp.setPin(pin);
		try {
			this.piDao.create(rasp);
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.connectionSource.close();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
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
	public List<RaspPi> getAllUsers() {

		ArrayList<RaspPi> userList = null;
		try {
			userList = (ArrayList<RaspPi>) this.piDao.queryForAll();
		} catch (final SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (userList.size() > 0) {

		}
		try {
			this.connectionSource.close();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userList;
	}

	@Override
	public RaspPi getRaspPi(final String name) {
		final QueryBuilder<RaspPi, String> queryBuilder = this.piDao.queryBuilder();
		List<RaspPi> raspPi = null;
		RaspPi pi = null;
		try {
			raspPi = this.piDao.query(queryBuilder.where().eq("name", name).prepare());

			if (raspPi.size() > 0) {
				pi = raspPi.get(0);
			}
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return pi;
		} finally {
			try {
				this.connectionSource.close();
			} catch (final SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return pi;
			}
		}
		return pi;
	}

	@Override
	public RaspPi updateUser(final String id, final String name, final String email, final String username,
			final String password, final String pin) {
		final RaspPi user = this.getRaspPi(id);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return user;
		}
		return user;
	}
}
