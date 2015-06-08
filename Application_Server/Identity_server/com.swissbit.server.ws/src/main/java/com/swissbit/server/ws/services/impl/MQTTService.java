package com.swissbit.server.ws.services.impl;

import java.util.concurrent.TimeUnit;

import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClient;
import com.swissbit.mqtt.client.message.KuraPayload;
import com.swissbit.server.ws.services.IMQTTService;

public final class MQTTService implements IMQTTService {

	private static final String APP_ID = "AUTH-V1";
	private static final String CLIENT_ID = "SWISSBIT_IDENTITY_SERVER";
	private static final String MQTT_HOST = "m20.cloudmqtt.com";
	private static final String MQTT_PASSWORD = "teYct6ev0It6bu";
	private static final String MQTT_PORT = "13273";
	private static final String MQTT_REQUEST_ID = "5437683849421421";
	private static final String MQTT_TOPIC_PREFIX = "$EDC/swissbit/";
	private static final String MQTT_USERNAME = "lord-voldemort-IoT";

	private static IKuraMQTTClient s_kuraClient;
	private static boolean s_statusResponse;

	static {
		s_kuraClient = new KuraMQTTClient.Builder().setHost(MQTT_HOST).setPort(MQTT_PORT).setClientId(CLIENT_ID)
				.setUsername(MQTT_USERNAME).setPassword(MQTT_PASSWORD).build();
		s_kuraClient.connect();
		subscribeDecryptionResponse();
	}

	private static void subscribeDecryptionResponse() {
		s_kuraClient.subscribe(MQTT_TOPIC_PREFIX + CLIENT_ID + "/REPLY/" + APP_ID + "/" + MQTT_REQUEST_ID, payload -> {
			if (payload.getMetric("data") != "") {
				s_statusResponse = true;
			}
		});
	}

	private void publishDecryptionRequest(final String mobileClientMacAddress, final String rPiMacAddress) {
		final KuraPayload payload = new KuraPayload();
		payload.addMetric("request.id", MQTT_REQUEST_ID);
		payload.addMetric("requester.client.id", CLIENT_ID);
		payload.setBody(mobileClientMacAddress.getBytes());

		s_kuraClient.publish(MQTT_TOPIC_PREFIX + rPiMacAddress + "/" + APP_ID + "/EXEC/decrypt", payload);
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
