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

	private IKuraMQTTClient m_kuraClient;
	private boolean m_statusResponse;

	private void publishDecryptionRequest(final String encryptedMobileClientMacAddress, final String rPiMacAddress) {
		final KuraPayload payload = new KuraPayload();
		payload.addMetric("request.id", MQTT_REQUEST_ID);
		payload.addMetric("requester.client.id", CLIENT_ID);
		payload.setBody(encryptedMobileClientMacAddress.getBytes());

		this.m_kuraClient.publish(MQTT_TOPIC_PREFIX + rPiMacAddress + "/" + APP_ID + "/EXEC/decrypt", payload);
	}

	private void subscribeDecryptionResponse() {
		this.m_kuraClient.subscribe(MQTT_TOPIC_PREFIX + CLIENT_ID + "/" + APP_ID + "/REPLY/" + MQTT_REQUEST_ID,
				payload -> {
					if (!"".equals(payload.getMetric("data"))) {
						this.m_statusResponse = true;
					} else {
						this.m_statusResponse = false;
					}
				});
	}

	@Override
	public boolean verifyClient(final String encryptedMobileClientMacAddress, final String rPiMacAddress) {
		this.m_kuraClient = new KuraMQTTClient.Builder().setHost(MQTT_HOST).setPort(MQTT_PORT).setClientId(CLIENT_ID)
				.setUsername(MQTT_USERNAME).setPassword(MQTT_PASSWORD).build();
		this.m_kuraClient.connect();
		this.subscribeDecryptionResponse();

		this.publishDecryptionRequest(encryptedMobileClientMacAddress, rPiMacAddress);

		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (final InterruptedException e) {
			// No need to log
		}

		return this.m_statusResponse;
	}

}
