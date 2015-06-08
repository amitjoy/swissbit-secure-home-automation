package com.swissbit.server.ws.services;

import java.util.List;

import com.swissbit.server.ws.model.RaspberryPi;

public interface IRaspberryPiDataService {

	public RaspberryPi createRaspberryPi(String name, String pin);

	public List<RaspberryPi> getAllRaspberryPi();

	public RaspberryPi getRaspberryPi(String name);

	public RaspberryPi updateRaspberryPi(String id, String name, String pin);

}
