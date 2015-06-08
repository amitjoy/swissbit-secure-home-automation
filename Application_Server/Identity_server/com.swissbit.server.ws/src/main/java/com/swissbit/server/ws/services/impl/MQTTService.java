package com.swissbit.server.ws.services.impl;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClient;
import com.swissbit.mqtt.client.message.KuraPayload;
import com.swissbit.server.ws.services.IMQTTService;

public class MQTTService implements IMQTTService {

	private static IKuraMQTTClient s_kuraClient;

	static {
		s_kuraClient = new KuraMQTTClient.Builder().setHost("m20.cloudmqtt.com").setPort("13273")
				.setClientId("SWISSBIT_IDENT_SERV").setUsername("lord-voldemort-IoT").setPassword("teYct6ev0It6bu")
				.build();
		s_kuraClient.connect();
	}

	private boolean statusResponse;

	private void publishDecryptionRequest(final String mobileClientMacAddress, final String rPiMacAddress) {
		final KuraPayload payload = new KuraPayload();
		payload.addMetric("request.id", "5437683849");
		payload.addMetric("requester.client.id", mobileClientMacAddress);
		payload.setBody(mobileClientMacAddress.getBytes());

		s_kuraClient.publish("$EDC/swissbit/" + rPiMacAddress + "/AUTH-V1/EXEC/decrypt", payload);
	}

	private void subscribeDecryptionResponse(final String mobileClientMacAddress) {
		s_kuraClient.subscribe("$EDC/swissbit/" + mobileClientMacAddress + "/REPLY/AUTH-V1/5437683849", payload -> {
			this.statusResponse = Boolean.valueOf(new String(payload.getBody(), Charsets.UTF_8));
		});
	}

	@Override
	public boolean verifyClient(final String mobileClientMacAddress, final String rPiMacAddress) {
		this.subscribeDecryptionResponse(mobileClientMacAddress);

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (final InterruptedException e) {
			Throwables.getStackTraceAsString(e);
		}

		this.publishDecryptionRequest(mobileClientMacAddress, rPiMacAddress);

		return this.statusResponse;
	}

}
