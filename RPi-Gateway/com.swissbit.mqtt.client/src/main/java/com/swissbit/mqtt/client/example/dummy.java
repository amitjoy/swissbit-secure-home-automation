package com.swissbit.mqtt.client.example;

import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

public class dummy {

	private static IKuraMQTTClient client;
	private static String clientId = "AMA";
	private static final String REQUEST_PERM = "$EDC/swissbit/B8:27:EB:BE:3F:BF/SURVEILLANCE-V1/POST/sample";
	private static final String REQUEST_TOPIC = "$EDC/swissbit/B8:27:EB:BE:3F:BF/AUTH-V1/EXEC/decrypt";
	private static final String REQUEST_TOPIC1 = "$EDC/swissbit/B8:27:EB:BE:3F:BF/CONF-V1/GET/configurations";
	private static final String REQUEST_ZWAVE = "$EDC/swissbit/B8:27:EB:BE:3F:BF/DEVICE-V1/EXEC/off";
	private static final String REQUEST_ZWAVE_LIST = "$EDC/swissbit/B8:27:EB:BE:3F:BF/DEVICE-V1/GET/list";
	private static String RESPONSE_PERM = "$EDC/swissbit/AMA/SURVEILLANCE-V1/REPLY/454545454545456";
	private static String RESPONSE_TOPIC = "$EDC/swissbit/AMA/AUTH-V1/REPLY/454545454545456";
	private static String RESPONSE_TOPIC1 = "$EDC/swissbit/AMA/CONF-V1/REPLY/422141241212";
	private static String RESPONSE_ZWAVE = "$EDC/swissbit/AMA/DEVICE-V1/REPLY/454545454545456";
	private static String RESPONSE_ZWAVE_LIST = "$EDC/swissbit/AMA/DEVICE-V1/REPLY/454545454545456";
	private static boolean status;

	public static void main(final String... args) {
		// Create the connection object
		client = new KuraMQTTClient.Builder().setHost("m20.cloudmqtt.com").setPort("13273").setClientId(clientId)
				.setUsername("user@email.com").setPassword("tEev-Aiv-H").build();

		// Connect to the Message Broker
		status = client.connect();

		if (status) {
			client.subscribe(RESPONSE_ZWAVE, new MessageListener() {

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
		payload.addMetric("nodeId", "8");
		payload.addMetric("encVal",
				"9df38b8729d743d4f8fcfb36f6bb4a5932d87b00e88f964604e86ea7cc599b49986a6d032f4fdb0d2be82d0dd026255ef06d14ac523d393283e1322d396163d91378868dbd45e821fe737f58f90a4a12");
		// payload.setBody("81896ecbb9afb39894c7144b5e962b08f132e9fd228539521aba75d4abbc18fe".getBytes());
		System.out.println(status);
		System.out.println(REQUEST_ZWAVE);

		if (status) {
			client.publish(REQUEST_ZWAVE, payload);

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
