package com.swissbit.server.ws.services;

import com.swissbit.server.ws.model.Admin;

public interface ILoginService extends IAbstractService {
	
	public Admin getId(String id);

}
