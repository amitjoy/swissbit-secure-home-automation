package com.swissbit.server.ws.services;

public interface IMQTTService extends IAbstractService {

	public boolean verifyClient(String encryptedMobileClientMacAddress, String rPiMacAddress);

}
