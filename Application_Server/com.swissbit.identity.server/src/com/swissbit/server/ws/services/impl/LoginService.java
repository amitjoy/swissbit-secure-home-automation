package com.swissbit.server.ws.services.impl;

import static com.swissbit.server.ws.Constants.DB_URL;
import static com.swissbit.server.ws.Constants.MYSQL_PASSWORD;
import static com.swissbit.server.ws.Constants.MYSQL_USER;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.swissbit.server.ws.error.ResponseError;
import com.swissbit.server.ws.model.Admin;
import com.swissbit.server.ws.services.ILoginService;

public class LoginService implements ILoginService {

	private ConnectionSource connectionSource = null;
	private Dao<Admin, String> piDao = null;


	public LoginService() {
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
	
	public Admin getId(final String id) {
		final QueryBuilder<Admin, String> queryBuilder = this.piDao.queryBuilder();
		List<Admin> ids = null;
		Admin administrator = null;
		try {
			ids = this.piDao.query(queryBuilder.where().eq("id", id).prepare());

			if (ids.size() > 0) {
				administrator = ids.get(0);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			return administrator;
		} finally {
			try {
				this.connectionSource.close();
			} catch (final SQLException e) {
				e.printStackTrace();
				return administrator;
			}
		}
		return administrator;
	}

	public Admin getUserByEmail(final String email) {
		final QueryBuilder<Admin, String> queryBuilder = this.piDao.queryBuilder();
		List<Admin> emails = null;
		Admin administrator = null;
		try {
			emails = this.piDao.query(queryBuilder.where().eq("email", email).prepare());

			if (emails.size() > 0) {
				administrator = emails.get(0);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			return administrator;
		} finally {
			try {
				this.connectionSource.close();
			} catch (final SQLException e) {
				e.printStackTrace();
				return administrator;
			}
		}
		return administrator;
	}
	

	public String authenticateUser(String email, String password) {
		final QueryBuilder<Admin, String> queryBuilder = this.piDao.queryBuilder();
		List<Admin> emails = null;
		Admin administrator = null;
		String administratorFName = null;
		try {
			emails = this.piDao.query(queryBuilder.where().eq("email", email).prepare());
			if (emails.size() > 0) {
				administrator = emails.get(0);
				administratorFName = administrator.getFName();
			}
			
			else {
				throw new IllegalArgumentException("Email address not found");
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			return administratorFName;
		} finally {
			try {
				this.connectionSource.close();
			} catch (final SQLException e) {
				e.printStackTrace();
				return administratorFName;
			}
		}
		String adminPassword = administrator.getPassword();
		if (password.equals(adminPassword)) {
			return administratorFName;
		}
		else {
			//return adminPassword;
			throw new IllegalArgumentException("Password is Incorrect");
		}
		
	}
}

