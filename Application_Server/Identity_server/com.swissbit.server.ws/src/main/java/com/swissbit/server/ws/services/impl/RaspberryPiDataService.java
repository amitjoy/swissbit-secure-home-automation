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
import com.swissbit.server.ws.model.RaspberryPi;
import com.swissbit.server.ws.services.IRaspberryPiDataService;;

public class RaspberryPiDataService implements IRaspberryPiDataService {

	private final String databaseUrl = "jdbc:mysql://localhost/spark";
	private static final String MYSQL_USER = "root";
	private static final String MYSQL_PASSWORD = "root";
	
	private ConnectionSource connectionSource = null;
	private Dao<RaspberryPi, String> piDao = null;

	public RaspberryPiDataService() {
		try {
			this.connectionSource = new JdbcConnectionSource(this.databaseUrl);
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
	public RaspberryPi createRaspberryPi(final String name, final String pin) {
		this.failIfInvalid(name, pin);
		final RaspberryPi rasp = new RaspberryPi();
		rasp.setId(UUID.randomUUID().toString());
		rasp.setName(name);
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

	private void failIfInvalid(final String name, final String pin) {
		if ((name == null) || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' cannot be empty");
		}
		if ((pin == null) || pin.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'pin' cannot be empty");
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
		try {
			this.connectionSource.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return piList;
	}

	@Override
	public RaspberryPi getRaspberryPi(final String name) {
		final QueryBuilder<RaspberryPi, String> queryBuilder = this.piDao.queryBuilder();
		List<RaspberryPi> raspPi = null;
		RaspberryPi pi = null;
		try {
			raspPi = this.piDao.query(queryBuilder.where().eq("name", name).prepare());

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
	public RaspberryPi updateRaspberryPi(final String id, final String name, final String pin) {
		final RaspberryPi rasp = this.getRaspberryPi(id);
		if (rasp == null) {
			throw new IllegalArgumentException("No user with id '" + id + "' found");
		}
		this.failIfInvalid(name, pin);
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
}
