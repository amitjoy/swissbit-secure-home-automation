package com.swissbit.server.ws;

import java.util.List;

import com.google.common.collect.Lists;
import com.swissbit.server.ws.controller.AbstractController;

public final class ControllerBuilder<T extends AbstractController> {

	public static ControllerBuilder<AbstractController> getInstance() {
		return new ControllerBuilder<>();
	}

	private final List<T> controllers = Lists.newArrayList();

	private ControllerBuilder() {

	}

	public ControllerBuilder<T> addController(final T t) {
		this.controllers.add(t);
		return this;
	}

	public List<T> getControllers() {
		return this.controllers;
	}
}
