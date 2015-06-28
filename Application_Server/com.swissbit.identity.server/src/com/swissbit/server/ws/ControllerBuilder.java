package com.swissbit.server.ws;

import com.swissbit.server.ws.controller.AbstractController;
import com.swissbit.server.ws.services.IAbstractService;

public final class ControllerBuilder<T extends AbstractController, U extends IAbstractService> {

	private final Class<T> clazz;
	private final Class<U> clazzz;

	public ControllerBuilder(final Class<T> clazz, final Class<U> clazzz) {
		this.clazz = clazz;
		this.clazzz = clazzz;
	}

	public T buildController() {
		try {
			return this.clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public U buildService() {
		try {
			return this.clazzz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
