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
package com.swissbit.mqtt.heartbeat;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.cloud.Cloudlet;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.eclipse.kura.message.KuraPayload;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

/**
 * This is used to broadcast MQTT Heartbeat messages
 * 
 * @author AMIT KUMAR MONDAL
 *
 */
@Component(immediate = false, name = "com.swissbit.mqtt.heartbeat")
@Service(value = { MQTTHeartbeat.class })
public class MQTTHeartbeat extends Cloudlet implements ConfigurableComponent {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MQTTHeartbeat.class);

	/**
	 * Application Identifier
	 */
	private static final String APP_ID = "HEARTBEAT-V1";

	/**
	 * Defines Application Configuration Metatype Id
	 */
	private static final String APP_CONF_ID = "com.swissbit.mqtt.heartbeat";

	/**
	 * Eclipse Kura Cloud Service Dependency
	 */
	@Reference(bind = "bindCloudService", unbind = "unbindCloudService")
	private volatile CloudService m_cloudService;

	/**
	 * Configurable property to set MQTT Hearbeat Topic Namespace
	 */
	private static final String HEARTBEAT_TOPIC = "de.tum.in.mqtt.heartbeat.topic";

	/**
	 * Scheduled Thread Pool Executor Reference
	 */
	private final ScheduledExecutorService m_worker;

	/**
	 * Future Event Handle for Executor
	 */
	private ScheduledFuture<?> m_handle;

	/**
	 * Map to store list of configurations
	 */
	private Map<String, Object> m_properties;

	/* Constructor */
	public MQTTHeartbeat() {
		super(APP_ID);
		m_worker = Executors.newSingleThreadScheduledExecutor();
	}

	/**
	 * Callback used when this service component is activating
	 */
	@Activate
	protected synchronized void activate(ComponentContext componentContext,
			Map<String, Object> properties) {
		LOGGER.info("Activating MQTT Heartbeat Component...");

		m_properties = properties;
		super.setCloudService(m_cloudService);
		super.activate(componentContext);

		try {
			doBroadcastHeartbeat(m_properties);
		} catch (final KuraException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}

		LOGGER.info("Activating MQTT Heartbeat Component... Done.");

	}

	/**
	 * Broadcasts the heartbeat message
	 */
	private void doBroadcastHeartbeat(Map<String, Object> properties)
			throws KuraException {

		// cancel a current worker handle if one if active
		if (m_handle != null) {
			m_handle.cancel(true);
		}
		m_handle = m_worker.scheduleAtFixedRate(
				() -> {
					LOGGER.info("Sending MQTT Heartbeat...");
					Thread.currentThread().setName(getClass().getSimpleName());
					final KuraPayload kuraPayload = new KuraPayload();
					kuraPayload.addMetric("data", "live");
					try {
						getCloudApplicationClient().controlPublish(
								(String) properties.get(HEARTBEAT_TOPIC),
								kuraPayload, DFLT_PUB_QOS, DFLT_RETAIN,
								DFLT_PRIORITY);
					} catch (final KuraException e) {
						LOGGER.error(Throwables.getStackTraceAsString(e));
					}
				}, 0, 10, TimeUnit.SECONDS);
	}

	/**
	 * Callback used when this service component is deactivating
	 */
	@Override
	@Deactivate
	protected void deactivate(ComponentContext context) {
		LOGGER.debug("Deactivating MQTT Heartbeat Component...");
		super.deactivate(context);
		// shutting down the worker and cleaning up the properties
		m_worker.shutdown();
		// Releasing the CloudApplicationClient
		LOGGER.info("Releasing CloudApplicationClient for {}...", APP_ID);
		getCloudApplicationClient().release();
		LOGGER.debug("Deactivating MQTT Heartbeat Component... Done.");
	}

	/**
	 * Callback to be used while {@link CloudService} is registering
	 */
	public synchronized void bindCloudService(CloudService cloudService) {
		if (m_cloudService == null) {
			super.setCloudService(m_cloudService = cloudService);
		}
	}

	/**
	 * Callback to be used while {@link CloudService} is deregistering
	 */
	public synchronized void unbindCloudService(CloudService cloudService) {
		if (m_cloudService == cloudService)
			super.setCloudService(m_cloudService = null);
	}

	/**
	 * Used to be called when configurations will get updated
	 */
	public void updated(Map<String, Object> properties) {
		LOGGER.info("Updated MQTT Heartbeat Component...");

		m_properties = properties;
		properties.keySet().forEach(
				s -> LOGGER.info("Update - " + s + ": " + properties.get(s)));

		try {
			doBroadcastHeartbeat(m_properties);
		} catch (final Exception e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
		LOGGER.info("Updated MQTT Heartbeat Component... Done.");
	}

}