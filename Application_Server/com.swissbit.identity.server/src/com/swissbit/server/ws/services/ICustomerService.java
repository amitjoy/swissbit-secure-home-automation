package com.swissbit.server.ws.services;

import java.util.List;

import com.swissbit.server.ws.model.Customer;

public interface ICustomerService extends IAbstractService {

	public Customer createUser(String name, String email, String username, String password, String pin);

	public List<Customer> getAllUsers();

	public Customer getUser(String name);

	public Customer updateUser(String id, String name, String email, String username, String password, String pin);

}
