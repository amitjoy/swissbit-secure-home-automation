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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.cloud.Cloudlet;
import org.eclipse.kura.cloud.CloudletTopic;
import org.eclipse.kura.message.KuraPayload;
import org.eclipse.kura.message.KuraRequestPayload;
import org.eclipse.kura.message.KuraResponsePayload;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.swissbit.activity.log.IActivityLogService;
import com.swissbit.assd.comm.IASSDCommunication;

/**
 * This is used to revoke permissions of clients to access the raspberry pi
 *
 * @author AMIT KUMAR MONDAL
 *
 */
@Component(immediate = false, name = "com.swissbit.accesscontrol")
@Service(value = { IAccessControl.class })
public class AccessControl extends Cloudlet implements IAccessControl {

	/**
	 * Application Identifier
	 */
	private static final String APP_ID = "SURVEILLANCE-V1";

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessControl.class);

	/**
	 * Activity Log Service Dependency
	 */
	@Reference(bind = "bindActivityLogService", unbind = "unbindActivityLogService")
	private volatile IActivityLogService m_activityLogService;

	/**
	 * ASSD Communication Service Dependency
	 */
	@Reference(bind = "bindASSDCommunicationService", unbind = "unbindASSDCommunicationService")
	private volatile IASSDCommunication m_assdCommunication;

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

		LOGGER.info("Activating Access Control Component... Done.");

	}

	/**
	 * Callback to be used while {@link IActivityLogService} is registering
	 */
	public synchronized void bindActivityLogService(final IActivityLogService activityLogService) {
		if (this.m_activityLogService == null) {
			this.m_activityLogService = activityLogService;
		}
	}

	/**
	 * ASSD Communication Service Binding Callback
	 */
	public synchronized void bindASSDCommunicationService(final IASSDCommunication iassdCommunication) {
		if (this.m_assdCommunication == null) {
			this.m_assdCommunication = iassdCommunication;
		}
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

	/** {@inheritDoc}} */
	@Override
	protected void doPost(final CloudletTopic reqTopic, final KuraRequestPayload reqPayload,
			final KuraResponsePayload respPayload) throws KuraException {

		final String secureElementId = (String) reqPayload.getMetric("secure_element");
		final String encryptedString = String.valueOf(reqPayload.getMetric("encVal"));
		final List<String> list = this.m_assdCommunication.decrypt(encryptedString);

		String decryptedString = null;

		if (list != null) {
			decryptedString = list.get(1);
		}

		if (decryptedString != null) {

			this.m_activityLogService.saveLog("Saving New Permissions..");
			try {
				Files.append(secureElementId + System.lineSeparator(), new File(ALL_CLIENTS_FILE_LOCATION),
						Charsets.UTF_8);
			} catch (final IOException e) {
				LOGGER.error(Throwables.getStackTraceAsString(e));
			}
		}
	}

	/**
	 * Used to publish a message to a control topic for revoking permissions
	 * from the clients
	 */
	private void doRevokePermision(final File file) throws IOException {
		// publish permission data to
		// $EDC/swissbit/[RPi-MAC]/SURVEILLANCE-V1/[secureElementId]/access/revoked
		Files.readLines(file, Charsets.UTF_8).forEach(secureElementId -> {
			if (!secureElementId.contains(" ") | !secureElementId.contains("")) {
				final KuraPayload payload = new KuraPayload();
				payload.addMetric("secureElementId", secureElementId);
				try {
					this.getCloudApplicationClient().controlPublish(secureElementId + "/access/revoked", payload, 2,
							false, 5);
				} catch (final Exception e) {
					LOGGER.error(Throwables.getStackTraceAsString(e));
				}
			}
		});
	}

	/** {@inheritDoc}} */
	@Override
	public String readAllClientsFile() {
		try {
			return Files.toString(new File(ALL_CLIENTS_FILE_LOCATION), Charsets.UTF_8);
		} catch (final IOException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
		return null;
	}

	/** {@inheritDoc}} */
	@Override
	public String readPermissionFile() {
		try {
			return Files.toString(new File(PERMISSION_FILE_LOCATION), Charsets.UTF_8);
		} catch (final IOException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
		return null;
	}

	/** {@inheritDoc}} */
	@Override
	public void savePermission(final String permissionData) {
		try {
			final File file = new File(PERMISSION_FILE_LOCATION);
			Files.write(permissionData, file, Charsets.UTF_8);

			this.doRevokePermision(file);
		} catch (final IOException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
	}

	/**
	 * Callback to be used while {@link IActivityLogService} is deregistering
	 */
	public synchronized void unbindActivityLogService(final IActivityLogService activityLogService) {
		if (this.m_activityLogService == activityLogService) {
			this.m_activityLogService = null;
		}
	}

	/**
	 * ASSD Communication Service Unbinding Callback
	 */
	public synchronized void unbindASSDCommunicationService(final IASSDCommunication iassdCommunication) {
		if (this.m_assdCommunication == iassdCommunication) {
			this.m_assdCommunication = null;
		}
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

		// this.doRevokePermission(this.m_properties);
		LOGGER.info("Updated MQTT Heartbeat Component... Done.");
	}

}