package com.swissbit.server.ws.services.impl;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClient;
import com.swissbit.mqtt.client.message.KuraPayload;
import com.swissbit.server.ws.services.IMQTTService;

public class MQTTService implements IMQTTService {

	private static final String CLIENT_ID = "SWISSBIT_IDENT_SERV";
	private static IKuraMQTTClient s_kuraClient;
	private static boolean s_statusResponse;

	static {
		s_kuraClient = new KuraMQTTClient.Builder().setHost("m20.cloudmqtt.com").setPort("13273")
				.setClientId("SWISSBIT_IDENT_SERV").setUsername("lord-voldemort-IoT").setPassword("teYct6ev0It6bu")
				.build();
		s_kuraClient.connect();
		subscribeDecryptionResponse();
	}

	private static void subscribeDecryptionResponse() {
		s_kuraClient.subscribe("$EDC/swissbit/" + CLIENT_ID + "/REPLY/AUTH-V1/5437683849421421", payload -> {
			s_statusResponse = Boolean.valueOf(new String(payload.getBody(), Charsets.UTF_8));
		});
	}

	private void publishDecryptionRequest(final String mobileClientMacAddress, final String rPiMacAddress) {
		final KuraPayload payload = new KuraPayload();
		payload.addMetric("request.id", "5437683849421421");
		payload.addMetric("requester.client.id", CLIENT_ID);
		payload.setBody(mobileClientMacAddress.getBytes());

		s_kuraClient.publish("$EDC/swissbit/" + rPiMacAddress + "/AUTH-V1/EXEC/decrypt", payload);
	}

	@Override
	public boolean verifyClient(final String mobileClientMacAddress, final String rPiMacAddress) {
		this.publishDecryptionRequest(mobileClientMacAddress, rPiMacAddress);

		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (final InterruptedException e) {
			// No need to log
		}

		return s_statusResponse;
	}

}
