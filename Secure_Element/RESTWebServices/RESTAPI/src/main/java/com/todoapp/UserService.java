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

	private String databaseUrl = "jdbc:mysql://localhost/spark";
	private ConnectionSource connectionSource = null;
	Dao<RaspPi,String> piDao = null;


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
			piDao = DaoManager.createDao(connectionSource, RaspPi.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<RaspPi> getAllUsers() {

		ArrayList<RaspPi> userList = null;
		try {
			userList = (ArrayList<RaspPi>) piDao.queryForAll();
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

	public RaspPi getRaspPi(String pin) {
		QueryBuilder<RaspPi, String> queryBuilder = piDao.queryBuilder();
		List<RaspPi> raspPi = null;
		RaspPi pi = null;
		try {
			raspPi = piDao.query(queryBuilder.where().eq("pin", pin).prepare());
			
			if(raspPi.size() > 0){
				pi =  raspPi.get(0); 
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return pi;
		} finally{
			try {
				connectionSource.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return pi;
			}
		}
		return pi;
	}

	public RaspPi createUser(String name, String email, String username, String password, String pin) {
		failIfInvalid(name, email, username, password, pin);
		RaspPi rasp = new RaspPi();
		rasp.setId(UUID.randomUUID().toString());
		rasp.setEmail(email);
		rasp.setName(name);
		rasp.setUsername(username);
		rasp.setPassword(password);
		rasp.setPin(pin);
		try {
			piDao.create(rasp);
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
		return rasp;
	}

	public RaspPi updateUser(String id, String name, String email, String username, String password, String pin) {
		RaspPi user = getRaspPi(id);
		if (user == null) {
			throw new IllegalArgumentException("No user with id '" + id + "' found");
		}
		failIfInvalid(name, email, username, password, pin);
		user.setName(name);
		user.setEmail(email);
		user.setUsername(username);
		user.setPassword(password);
		user.setPin(pin);
		
		try {
			piDao.update(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return user;
		}
		return user;
	}

	private void failIfInvalid(String name, String email, String username, String password, String pin) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' cannot be empty");
		}
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'email' cannot be empty");
		}
		if (username == null || username.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'username' cannot be empty");
		}
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'password' cannot be empty");
		}
		if (pin == null || pin.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'pin' cannot be empty");
		}
	}
}
