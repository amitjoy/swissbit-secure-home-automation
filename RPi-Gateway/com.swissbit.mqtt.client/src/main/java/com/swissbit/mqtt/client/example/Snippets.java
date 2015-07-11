/*******************************************************************************
 * Copyright 2015 Amit Kumar Mondal <admin@amitinside.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.swissbit.mqtt.client.example;

import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

public final class Snippets {

	private static String ACCESS_REVOCATION_SUBSCRIPTION = "$EDC/swissbit/B8:27:EB:BE:3F:BF/SURVEILLANCE-V1/1b58095eb4c6b36d794c3ed776ae2378/access/revoked";
	private static IKuraMQTTClient client;
	private static String clientId = "AMITA";
	private static final String REQUEST_PERM = "$EDC/swissbit/B8:27:EB:BE:3F:BF/SURVEILLANCE-V1/POST/sample";
	private static final String REQUEST_REVOKE = "$EDC/swissbit/B8:27:EB:BE:3F:BF/SURVEILLANCE-V1/1b58095eb4c6b36d794c3ed776ae2378/access/revoked";
	private static final String REQUEST_TOPIC = "$EDC/swissbit/B8:27:EB:BE:3F:BF/AUTH-V1/EXEC/decrypt";
	private static final String REQUEST_TOPIC1 = "$EDC/swissbit/B8:27:EB:BE:3F:BF/CONF-V1/GET/configurations";
	private static final String REQUEST_ZWAVE = "$EDC/swissbit/B8:27:EB:BE:3F:BF/DEVICE-V1/EXEC/off";
	private static final String REQUEST_ZWAVE_LIST = "$EDC/swissbit/B8:27:EB:BE:3F:BF/DEVICE-V1/GET/list";
	private static final String REQUEST_ZWAVE_STATUS = "$EDC/swissbit/B8:27:EB:BE:3F:BF/DEVICE-V1/GET/status";
	private static String RESPONSE_PERM = "$EDC/swissbit/AMITA/SURVEILLANCE-V1/REPLY/454545454545456";
	private static String RESPONSE_TOPIC = "$EDC/swissbit/AMITA/AUTH-V1/REPLY/454545454545456";
	private static String RESPONSE_TOPIC1 = "$EDC/swissbit/AMITA/CONF-V1/REPLY/422141241212";
	private static String RESPONSE_ZWAVE = "$EDC/swissbit/AMITA/DEVICE-V1/REPLY/454545454545456";
	private static String RESPONSE_ZWAVE_LIST = "$EDC/swissbit/AMITA/DEVICE-V1/REPLY/454545454545456";
	private static boolean status;

	public static void main(final String... args) {
		// Create the connection object
		client = new KuraMQTTClient.Builder().setHost("m11.cloudmqtt.com").setPort("14384").setUsername("wvnlscci")
				.setPassword("JDndgf1WMS4n").setClientId(clientId).build();

		// Connect to the Message Broker
		status = client.connect();

		if (status) {
			client.subscribe(RESPONSE_ZWAVE_LIST, new MessageListener() {

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
				"821b5d53fcba7680aecafbfd9a29658923e6d0a27315daa4345ffa865c4fd7a964bfe51a252cd8e891a8503ae09b82836ffb5e15b1e61233b7f3938b5869900a93da74ceb1ba272d26f3cf0f7ba073b1");
		System.out.println(status);
		System.out.println(REQUEST_ZWAVE_LIST);

		if (status) {
			client.publish(REQUEST_ZWAVE_LIST, payload);

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
