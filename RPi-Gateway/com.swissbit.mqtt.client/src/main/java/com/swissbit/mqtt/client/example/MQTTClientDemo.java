/*******************************************************************************
 * Copyright (C) 2015 - Amit Kumar Mondal <admin@amitinside.com>
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

import java.io.UnsupportedEncodingException;

import com.google.common.base.Throwables;
import com.swissbit.mqtt.client.KuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClientImpl;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

public final class MQTTClientDemo {

	public static void main(String... args) {

		final KuraMQTTClient client = new KuraMQTTClientImpl();

		// Connect to the Message Broker
		final boolean status = client.connect("m20.cloudmqtt.com", "13273",
				"SAMPLE_CLIENT", "uefsbjsc", "HoVaapD7gKE-");
		System.out.println(status);

		// Declare the topics
		final String CONF_REQUEST_TOPIC = "$EDC/swissbit/AMIT/CONF-V1/GET/configurations";
		final String CONF_RESPONSE_TOPIC = "$EDC/swissbit/AMIT_083027868/CONF-V1/REPLY/55361535117";

		// Subscribe to the topic first
		if (status)
			client.subscribe(CONF_RESPONSE_TOPIC, new MessageListener() {

				@Override
				public void processMessage(KuraPayload payload) {
					try {
						System.out.println(new String(payload.getBody(),
								"UTF-8"));
					} catch (final UnsupportedEncodingException e) {
						System.out.println(Throwables.getStackTraceAsString(e));
					}
				}
			});

		// Then publish the message
		final KuraPayload payload = new KuraPayload();
		payload.addMetric("request.id", "55361535117");
		payload.addMetric("requester.client.id", "AMIT_083027868");

		if (status)
			client.publish(CONF_REQUEST_TOPIC, payload);

		System.out.println("Subscribed to channels "
				+ client.getSubscribedChannels());

		System.out.println("Waiting for new messages");

		while (!Thread.currentThread().isInterrupted()) {
		}

		// Finally disconnect
		client.disconnect();
	}

}
