package com.swissbit.server.ws.services.impl;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.swissbit.server.ws.model.Customer;
import com.swissbit.server.ws.services.ILoginService;

public class LoginService implements ILoginService {

	private final String databaseUrl = "jdbc:mysql://localhost/spark";
	private static final String MYSQL_USER = "root";
	private static final String MYSQL_PASSWORD = "root";
	
	private ConnectionSource connectionSource = null;
	private Dao<Customer, String> piDao = null;

	public LoginService() {
		try {
			this.connectionSource = new JdbcConnectionSource(this.databaseUrl);
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
	public Customer getUser(final String username) {
		final QueryBuilder<Customer, String> queryBuilder = this.piDao.queryBuilder();
		List<Customer> user = null;
		Customer admin = null;
		try {
			user = this.piDao.query(queryBuilder.where().eq("username", username).prepare());

			if (user.size() > 0) {
				admin = user.get(0);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			return admin;
		} finally {
			try {
				this.connectionSource.close();
			} catch (final SQLException e) {
				e.printStackTrace();
				return admin;
			}
		}
		return admin;
	}

}
