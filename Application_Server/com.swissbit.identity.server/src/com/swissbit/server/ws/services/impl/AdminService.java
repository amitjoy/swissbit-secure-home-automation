package com.swissbit.server.ws.services.impl;

import static com.swissbit.server.ws.Constants.DB_URL;
import static com.swissbit.server.ws.Constants.MYSQL_PASSWORD;
import static com.swissbit.server.ws.Constants.MYSQL_USER;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.swissbit.server.ws.model.Admin;
import com.swissbit.server.ws.services.IAdminService;

public class AdminService implements IAdminService {

	private ConnectionSource connectionSource = null;
	private Dao<Admin, String> piDao = null;

	public AdminService() {
		try {
			this.connectionSource = new JdbcConnectionSource(DB_URL);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		((JdbcConnectionSource) this.connectionSource).setUsername(MYSQL_USER);
		((JdbcConnectionSource) this.connectionSource).setPassword(MYSQL_PASSWORD);
		try {
			TableUtils.createTableIfNotExists(this.connectionSource, Admin.class);
			this.piDao = DaoManager.createDao(this.connectionSource, Admin.class);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public Admin createUser(String email, String id, String fname, String lname, String password) {
		this.failIfInvalid(email, id, fname, lname, password);
		final Admin rasp = new Admin();
		rasp.setEmail(email);
		rasp.setId(id);
		rasp.setFName(fname);
		rasp.setLName(lname);
		rasp.setPassword(password);
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
	
	private void failIfInvalid(final String email, final String id, final String fname, final String lname, final String password) {
		if ((email == null) || email.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'email' cannot be empty");
		}
		if ((id == null) || id.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'id' cannot be empty");
		}
		if ((fname == null) || fname.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'fname' cannot be empty");
		}
		if ((lname == null) || lname.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'lname' cannot be empty");
		}
		if ((password == null) || password.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'password' cannot be empty");
		}
	}

	@Override
	public List<Admin> getAllUsers() {

		ArrayList<Admin> userList = null;
		try {
			userList = (ArrayList<Admin>) this.piDao.queryForAll();
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
}


