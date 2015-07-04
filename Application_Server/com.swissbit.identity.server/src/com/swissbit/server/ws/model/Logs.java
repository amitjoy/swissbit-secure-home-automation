package com.swissbit.server.ws.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

@XmlRootElement
public final class Logs {

	@XmlElementWrapper
	@XmlElement
	private final List<Log> logs = Lists.newArrayList();

	/**
	 * @return the logs
	 */
	public final List<Log> getLogs() {
		return this.logs;
	}

}
