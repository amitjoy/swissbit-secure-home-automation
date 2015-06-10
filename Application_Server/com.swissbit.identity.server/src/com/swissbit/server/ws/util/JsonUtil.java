package com.swissbit.server.ws.util;

import com.google.gson.Gson;

import spark.ResponseTransformer;

public class JsonUtil {

	public static ResponseTransformer json() {
		return JsonUtil::toJson;
	}

	public static String toJson(final Object object) {
		return new Gson().toJson(object);
	}
}