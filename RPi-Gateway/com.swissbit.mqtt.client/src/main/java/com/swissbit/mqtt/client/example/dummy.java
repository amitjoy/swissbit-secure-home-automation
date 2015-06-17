package com.swissbit.mqtt.client.example;

import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

public class dummy {

	private static IKuraMQTTClient client;
	private static String clientId = "ANNIT";
	private static final String REQUEST_TOPIC = "$EDC/swissbit/B8:27:EB:BE:3F:BF/AUTH-V1/EXEC/decrypt";
	private static final String REQUEST_TOPIC1 = "$EDC/swissbit/B8:27:EB:BE:3F:BF/CONF-V1/GET/configurations";
	private static final String REQUEST_ZWAVE = "$EDC/swissbit/B8:27:EB:BE:3F:BF/DEVICE-V1/EXEC/on";
	private static final String REQUEST_PERM = "$EDC/swissbit/B8:27:EB:BE:3F:BF/SURVEILLANCE-V1/POST/sample";
	private static final String REQUEST_ZWAVE_LIST = "$EDC/swissbit/B8:27:EB:BE:3F:BF/DEVICE-V1/GET/list";
	private static String RESPONSE_TOPIC = "$EDC/swissbit/AMA/AUTH-V1/REPLY/454545454545456";
	private static String RESPONSE_TOPIC1 = "$EDC/swissbit/AMA/CONF-V1/REPLY/422141241212";
	private static String RESPONSE_ZWAVE = "$EDC/swissbit/AMA/DEVICE-V1/REPLY/454545454545456";
	private static String RESPONSE_PERM = "$EDC/swissbit/AMA/SURVEILLANCE-V1/REPLY/454545454545456";
	private static String RESPONSE_ZWAVE_LIST = "$EDC/swissbit/AMA/DEVICE-V1/REPLY/454545454545456";
	private static boolean status;

	public static void main(final String... args) {
		// Create the connection object
		client = new KuraMQTTClient.Builder().setHost("m20.cloudmqtt.com").setPort("13273").setClientId(clientId)
				.setUsername("user@email.com").setPassword("tEev-Aiv-H").build();

		// Connect to the Message Broker
		status = client.connect();

		if (status) {
			client.subscribe(RESPONSE_PERM, new MessageListener() {

				@Override
				public void processMessage(final KuraPayload payload) {
					System.out.println(payload.metrics());
				}
			});

			System.out.println("Subscribed to channels " + client.getSubscribedChannels());

			System.out.println("Waiting for new messages");

		}

		final KuraPayload payload = new KuraPayload();
		payload.addMetric("request.id", "454545454545456");
		payload.addMetric("requester.client.id", clientId);
		payload.addMetric("nodeId", 8);
		// payload.setBody("81896ecbb9afb39894c7144b5e962b08f132e9fd228539521aba75d4abbc18fe".getBytes());
		System.out.println(status);
		System.out.println(REQUEST_PERM);

		if (status) {
			client.publish(REQUEST_PERM, payload);

			System.out.println("--------------------------------------------------------------------");
			System.out.println("Request Published");
			System.out.println("Request ID : " + "454545454545456");
			System.out.println("Request Client ID : " + clientId);
			System.out.println("--------------------------------------------------------------------");
		}

		while (!Thread.currentThread().isInterrupted()) {
		}
	}

}
