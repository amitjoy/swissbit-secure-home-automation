/*******************************************************************************
 * Copyright 2015 Amit Kumar Mondal <admin@amitinside.com>, Subburam <subburam.rb@gmail.com>, Gaurav Srivastava <gaurav.srivastava7@gmail.com>
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
package com.swissbit.server.ws.services.impl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClient;
import com.swissbit.mqtt.client.message.KuraPayload;
import com.swissbit.server.ws.services.ILogService;

public final class LogService implements ILogService {

	private static final String APP_ID = "LOGS-V1";
	private static final String CLIENT_ID = "SWISSBIT_IDENTITY_SERVER";
	private static final String MQTT_HOST = "m20.cloudmqtt.com";
	private static final String MQTT_PASSWORD = "teYct6ev0It6bu";
	private static final String MQTT_PORT = "13273";
	private static final String MQTT_REQUEST_ID = "5437683849421421";
	private static final String MQTT_TOPIC_PREFIX = "$EDC/swissbit/";
	private static final String MQTT_USERNAME = "lord-voldemort-IoT";
	private static final Lock s_lock = new ReentrantLock();
	private static final Condition s_waker = s_lock.newCondition();
	private IKuraMQTTClient m_kuraClient;
	private String m_kuraLog = null;

	private String m_swissbitLog = null;

	@Override
	public String[] getLogs(final String rPiMacAddress) {
		this.m_kuraClient = new KuraMQTTClient.Builder().setHost(MQTT_HOST).setPort(MQTT_PORT).setClientId(CLIENT_ID)
				.setUsername(MQTT_USERNAME).setPassword(MQTT_PASSWORD).build();
		this.m_kuraClient.connect();
		this.subscribeResponse();

		try {
			this.publishRequest(rPiMacAddress);
			s_lock.lock();
			s_waker.await();
		} catch (final Exception e) {
			// Noting to log
		} finally {
			s_lock.unlock();
		}

		return new String[] { this.m_swissbitLog, this.m_kuraLog };
	}

	private void publishRequest(final String rPiMacAddress) {
		final KuraPayload payload = new KuraPayload();
		payload.addMetric("request.id", MQTT_REQUEST_ID);
		payload.addMetric("requester.client.id", CLIENT_ID);

		this.m_kuraClient.publish(MQTT_TOPIC_PREFIX + rPiMacAddress + "/" + APP_ID + "/GET/logs", payload);
	}

	private void subscribeResponse() {
		this.m_kuraClient.subscribe(MQTT_TOPIC_PREFIX + CLIENT_ID + "/" + APP_ID + "/REPLY/" + MQTT_REQUEST_ID,
				payload -> {
					if (payload.getMetric("swissbitlog") != null) {
						this.m_swissbitLog = (String) payload.getMetric("swissbitlog");
					}

					if (payload.getMetric("kuralog") != null) {
						this.m_kuraLog = (String) payload.getMetric("kuralog");
					}

					// Signal the Thread
					try {
						s_lock.lock();
						s_waker.signal();
					} finally {
						s_lock.unlock();
					}
				});
	}

}
