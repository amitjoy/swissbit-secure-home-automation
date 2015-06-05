package com.swissbit.server.ws;

import static spark.Spark.*;

import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClient;

public class SwissbitServices implements IClientDataService, ILoginService, IMQTTService {

	private static IKuraMQTTClient s_kuraClient;

	public static void main(String[] args) {

		s_kuraClient = new KuraMQTTClient.Builder().setHost("m20.cloudmqtt.com").setPort("13273")
				.setClientId("SWISSBIT_IDENT_SERV").setUsername("user@email.com").setPassword("tEev-Aiv-H").build();
		
		final boolean status = s_kuraClient.connect();

		get("/hello", (req, res) -> status);
	}

}
