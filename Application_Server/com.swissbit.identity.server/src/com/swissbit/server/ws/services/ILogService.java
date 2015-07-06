package com.swissbit.server.ws.services;

public interface ILogService extends IAbstractService {

	public String[] getLogs(String rPiMacAddress);

}
