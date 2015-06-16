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
package com.swissbit.accesscontrol;

import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.cloud.Cloudlet;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.eclipse.kura.message.KuraPayload;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;

/**
 * This is used to revoke permissions of clients to access the raspberry pi
 *
 * @author AMIT KUMAR MONDAL
 *
 */
@Component(immediate = false, name = "com.swissbit.accesscontrol")
@Service(value = { AccessControl.class })
public class AccessControl extends Cloudlet implements ConfigurableComponent {
	/**
	 * Defines Application Configuration Metatype Id
	 */
	private static final String APP_CONF_ID = "com.swissbit.accesscontrol";

	/**
	 * Application Identifier
	 */
	private static final String APP_ID = "SURVEILLANCE-V1";

	/**
	 * Configurable property to set Client Ids to revoke permission to access
	 * this Raspberry Pi
	 */
	private static final String CLIENTS_LIST = "com.swissbit.accesscontrol.clients";

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessControl.class);

	/**
	 * Eclipse Kura Cloud Service Dependency
	 */
	@Reference(bind = "bindCloudService", unbind = "unbindCloudService")
	private volatile CloudService m_cloudService;

	/**
	 * Map to store list of configurations
	 */
	private Map<String, Object> m_properties;

	/* Constructor */
	public AccessControl() {
		super(APP_ID);
	}

	/**
	 * Callback used when this service component is activating
	 */
	@Activate
	protected synchronized void activate(final ComponentContext componentContext,
			final Map<String, Object> properties) {
		LOGGER.info("Activating Access Control Component...");

		this.m_properties = properties;
		super.setCloudService(this.m_cloudService);
		super.activate(componentContext);

		this.doRevokePermission(this.m_properties);

		LOGGER.info("Activating Access Control Component... Done.");

	}

	/**
	 * Callback to be used while {@link CloudService} is registering
	 */
	public synchronized void bindCloudService(final CloudService cloudService) {
		if (this.m_cloudService == null) {
			super.setCloudService(this.m_cloudService = cloudService);
		}
	}

	/**
	 * Callback used when this service component is deactivating
	 */
	@Override
	@Deactivate
	protected void deactivate(final ComponentContext context) {
		LOGGER.debug("Deactivating Access Control Component...");
		super.deactivate(context);
		// Releasing the CloudApplicationClient
		LOGGER.info("Releasing CloudApplicationClient for {}...", APP_ID);
		this.getCloudApplicationClient().release();
		LOGGER.debug("Deactivating Access Control Component... Done.");
	}

	/**
	 * Used to revoke permission of the specified clients
	 */
	private void doRevokePermission(final Map<String, Object> properties) {
		final String clientList = (String) properties.get(CLIENTS_LIST);
		final List<String> clients = this.parseClientIds(clientList);
		final boolean retain = true;
		final int QoS = 1;

		final KuraPayload payload = new KuraPayload();
		payload.addMetric("access", false);

		clients.forEach(clientId -> {
			try {
				this.getCloudApplicationClient().controlPublish(clientId + "/surveillance", payload, QoS, retain,
						DFLT_PRIORITY);
			} catch (final Exception e) {
				LOGGER.error(Throwables.getStackTraceAsString(e));
			}
		});

	}

	/**
	 * Used to parse all the clients ids
	 */
	private List<String> parseClientIds(final String clientList) {
		final String SEPARATOR = "#";

		final Splitter splitter = Splitter.on(SEPARATOR).omitEmptyStrings().trimResults();
		return splitter.splitToList(clientList);
	}

	/**
	 * Callback to be used while {@link CloudService} is deregistering
	 */
	public synchronized void unbindCloudService(final CloudService cloudService) {
		if (this.m_cloudService == cloudService) {
			super.setCloudService(this.m_cloudService = null);
		}
	}

	/**
	 * Used to be called when configurations will get updated
	 */
	public void updated(final Map<String, Object> properties) {
		LOGGER.info("Updated MQTT Heartbeat Component...");

		this.m_properties = properties;
		properties.keySet().forEach(s -> LOGGER.info("Update - " + s + ": " + properties.get(s)));

		this.doRevokePermission(this.m_properties);
		LOGGER.info("Updated MQTT Heartbeat Component... Done.");
	}

}