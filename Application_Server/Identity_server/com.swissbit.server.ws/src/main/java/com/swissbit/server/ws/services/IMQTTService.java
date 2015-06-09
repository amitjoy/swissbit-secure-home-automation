package com.swissbit.server.ws.services;

public interface IMQTTService extends IAbstractService {

	public boolean verifyClient(String mobileClientMacAddress, String rPiMacAddress);

}
