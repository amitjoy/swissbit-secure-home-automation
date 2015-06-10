package com.swissbit.server.ws.controller;

import com.swissbit.server.ws.services.IAbstractService;

//Marker Interface
public abstract class AbstractController {

	protected abstract void apply(IAbstractService iAbstractService);

}
