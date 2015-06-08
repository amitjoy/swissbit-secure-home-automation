package com.swissbit.server.ws.services;

public interface IMQTTService {

	public boolean verifyClient(String mobileClientMacAddress, String rPiMacAddress);

}
