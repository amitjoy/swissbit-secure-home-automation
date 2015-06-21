package com.swissbit.server.ws.services;

import java.util.List;

import com.swissbit.server.ws.model.RaspberryPi;

public interface IRaspberryPiService extends IAbstractService {

	public RaspberryPi createRaspberryPi(String customer, String name, String pin, String macaddr);

	public List<RaspberryPi> getAllRaspberryPi();

	public RaspberryPi getRaspberryPi(String field, String value);

	public RaspberryPi updateRaspberryPi(String id, String name, String pin);

	public boolean validateRaspberryPi(String macAddr);

}
