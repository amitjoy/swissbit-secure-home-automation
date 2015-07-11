package com.swissbit.server.ws.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.MoreObjects;

@XmlRootElement
public final class Log {

	@XmlElement
	private String logDescription;

	@XmlElement
	private String logTime;

	/**
	 * @return the logDescription
	 */
	public final String getLogDescription() {
		return this.logDescription;
	}

	/**
	 * @return the logTime
	 */
	public final String getLogTime() {
		return this.logTime;
	}

	/**
	 * @param logDescription
	 *            the logDescription to set
	 */
	public final void setLogDescription(final String logDescription) {
		this.logDescription = logDescription;
	}

	/**
	 * @param logTime
	 *            the logTime to set
	 */
	public final void setLogTime(final String logTime) {
		this.logTime = logTime;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("logTime", this.logTime).add("logDesc", this.logDescription)
				.toString();
	}

}
