package com.todoapp;

import java.sql.SQLException;
import java.util.*;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class UserService {

	private Map<String, RaspPi> users = new HashMap<>();
	private String databaseUrl = "jdbc:mysql://localhost/spark";
	private ConnectionSource connectionSource = null;
	Dao<RaspPi,String> userDao = null;

	
	public UserService(){
		try {
			connectionSource = new JdbcConnectionSource(databaseUrl);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((JdbcConnectionSource)connectionSource).setUsername("root");
		((JdbcConnectionSource)connectionSource).setPassword("root");
		try {
			TableUtils.createTableIfNotExists(connectionSource, RaspPi.class);
			userDao = DaoManager.createDao(connectionSource, RaspPi.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<RaspPi> getAllUsers() {
		
		  QueryBuilder<RaspPi, String> queryBuilder = userDao.queryBuilder();
	        ArrayList<RaspPi> userList = null;
			try {
				userList = (ArrayList)userDao.queryForAll();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        if(userList.size() > 0)
	        {
	            
	        }
	        try {
				connectionSource.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return userList;
	}

	public RaspPi getUser(String id) {
		return users.get(id);
	}

	public RaspPi createUser(String name, String email) {
		failIfInvalid(name, email);
		RaspPi user = new RaspPi();
		user.setId(UUID.randomUUID().toString());
		user.setEmail(email);
		user.setName(name);
		try {
			users.put(user.getId(), user);
			userDao.create(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connectionSource.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}

	public RaspPi updateUser(String id, String name, String email) {
		RaspPi user = users.get(id);
		if (user == null) {
			throw new IllegalArgumentException("No user with id '" + id + "' found");
		}
		failIfInvalid(name, email);
		user.setName(name);
		user.setEmail(email);
		return user;
	}

	private void failIfInvalid(String name, String email) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' cannot be empty");
		}
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'email' cannot be empty");
		}
	}
}
