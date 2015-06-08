package com.swissbit.server.ws.services;

import java.util.List;

import com.swissbit.server.ws.model.RaspPi;

public interface IClientDataService {

	public RaspPi createUser(String name, String email, String username, String password, String pin);

	public List<RaspPi> getAllUsers();

	public RaspPi getRaspPi(String name);

	public RaspPi updateUser(String id, String name, String email, String username, String password, String pin);

}
