package com.swissbit.server.ws.services;

import com.swissbit.server.ws.model.Customer;

public interface ILoginService extends IAbstractService {

	public Customer getUser(String name);

}
