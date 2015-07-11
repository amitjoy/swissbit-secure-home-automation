package com.swissbit.server.ws.services;


import java.util.List;

import com.swissbit.server.ws.model.Admin;

public interface IAdminService extends IAbstractService {
	
	public Admin createUser(String email, String id, String fname, String lname, String password);

	public List<Admin> getAllUsers();
	
}