package com.swissbit.server.ws.error;

public class ResponseError {

	private final String message;

	public ResponseError(final Exception e) {
		this.message = e.getMessage();
	}

	public ResponseError(final String message, final String... args) {
		this.message = String.format(message, args);
	}

	public String getMessage() {
		return this.message;
	}
}