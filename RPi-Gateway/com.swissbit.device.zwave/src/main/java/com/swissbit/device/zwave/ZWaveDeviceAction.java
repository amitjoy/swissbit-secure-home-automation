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
package com.swissbit.device.zwave;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.cloud.Cloudlet;
import org.eclipse.kura.cloud.CloudletTopic;
import org.eclipse.kura.message.KuraRequestPayload;
import org.eclipse.kura.message.KuraResponsePayload;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swissbit.activity.log.IActivityLogService;
import com.swissbit.assd.comm.IASSDCommunication;
import com.swissbit.device.zwave.util.CommandUtil;
import com.swissbit.ifttt.IFTTTConfiguration;

/**
 * The implementation of {@link IZwaveDeviceAction}
 *
 * @see IZwaveDeviceAction
 * @author AMIT KUMAR MONDAL
 */
@Component(name = "com.swissbit.device.zwave")
@Service(value = { IZwaveDeviceAction.class, ZWaveDeviceAction.class })
public class ZWaveDeviceAction extends Cloudlet implements IZwaveDeviceAction {

	/**
	 * Defines Application ID for ZWave Component
	 */
	private static final String APP_ID = "DEVICE-V1";

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ZWaveDeviceAction.class);

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
	 * Kura Cloud Service Injection
	 */
	@Reference(bind = "bindCloudService", unbind = "unbindCloudService")
	private volatile CloudService m_cloudService;

	/**
	 * IFTTT Service Dependency
	 */
	@Reference(bind = "bindIFTTTService", unbind = "unbindIFTTTService")
	private volatile IFTTTConfiguration m_iftttService;

	/**
	 * Thread Pool Executor Service
	 */
	private final ExecutorService m_worker;

	/**
	 * Constructor
	 */
	public ZWaveDeviceAction() {
		super(APP_ID);
		this.m_worker = Executors.newSingleThreadExecutor();
	}

	/**
	 * Callback while this component is getting registered
	 *
	 * @param properties
	 *            the service configuration properties
	 */
	@Override
	@Activate
	protected synchronized void activate(final ComponentContext context) {
		LOGGER.info("Activating ZWave Component....");
		super.activate(context);
		LOGGER.info("Activating ZWave Component... Done.");
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
	 * Kura Cloud Service Binding Callback
	 */
	public synchronized void bindCloudService(final CloudService cloudService) {
		if (this.m_cloudService == null) {
			super.setCloudService(this.m_cloudService = cloudService);
		}
	}

	/**
	 * IFTTT Service Binding Callback
	 */
	public synchronized void bindIFTTTService(final IFTTTConfiguration ifttService) {
		if (this.m_iftttService == null) {
			this.m_iftttService = ifttService;
		}
	}

	/**
	 * Callback while this component is getting deregistered
	 *
	 * @param properties
	 *            the service configuration properties
	 */
	@Override
	@Deactivate
	protected synchronized void deactivate(final ComponentContext context) {
		LOGGER.info("Deactivating ZWave Component....");
		super.deactivate(context);
		// shutting down the worker and cleaning up the properties
		this.m_worker.shutdown();
		LOGGER.info("Deactivating ZWave Component... Done.");
	}

	/** {@inheritDoc} */
	@Override
	protected void doExec(final CloudletTopic reqTopic, final KuraRequestPayload reqPayload,
			final KuraResponsePayload respPayload) throws KuraException {

		// Parse the nodeId
		final String nodeId = String.valueOf(reqPayload.getMetric("nodeId"));
		final String encryptedString = String.valueOf(reqPayload.getMetric("encVal"));
		final List<String> list = this.m_assdCommunication.decrypt(encryptedString);

		String decryptedString = null;

		if (list != null) {
			decryptedString = list.get(1);
		}

		if (decryptedString != null) {

			if ("on".equals(reqTopic.getResources()[0])) {
				this.m_activityLogService.saveLog("Device is turned on");
				this.switchOn(nodeId);
			}

			if ("off".equals(reqTopic.getResources()[0])) {
				this.m_activityLogService.saveLog("Device is turned off");
				this.switchOff(nodeId);
			}

			this.m_worker.submit(() -> this.m_iftttService.trigger());
			respPayload.setResponseCode(KuraResponsePayload.RESPONSE_CODE_OK);
		}

	}

	/** {@inheritDoc} */
	@Override
	protected void doGet(final CloudletTopic reqTopic, final KuraRequestPayload reqPayload,
			final KuraResponsePayload respPayload) throws KuraException {

		// Parse the nodeId
		final String nodeId = String.valueOf(reqPayload.getMetric("nodeId"));
		final String encryptedString = String.valueOf(reqPayload.getMetric("encVal"));
		final List<String> list = this.m_assdCommunication.decrypt(encryptedString);

		String decryptedString = null;

		if (list != null) {
			decryptedString = list.get(1);
		}

		if (decryptedString != null) {
			if ("status".equals(reqTopic.getResources()[0])) {
				this.m_activityLogService.saveLog("Device status is retrieved");
				respPayload.addMetric("status", this.getStatus(nodeId));
			}
		}

		if ("list".equals(reqTopic.getResources()[0])) {
			this.m_activityLogService.saveLog("Connected Devices List is retrieved");
			this.getConnectedDevices().forEach(node -> {
				int i = 0;
				respPayload.addMetric("node.id_" + i++, node);
			});
		}
		respPayload.setResponseCode(KuraResponsePayload.RESPONSE_CODE_OK);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getConnectedDevices() {
		return (List<String>) CommandUtil.switchOp("10", "LIST");
	}

	/** {@inheritDoc} */
	@Override
	public Boolean getStatus(final String nodeId) {
		return Boolean.valueOf((String) CommandUtil.switchOp(nodeId, "STATUS"));
	}

	/** {@inheritDoc} */
	@Override
	public Boolean switchOff(final String nodeId) {
		return (Boolean) CommandUtil.switchOp(nodeId, "OFF");
	}

	/** {@inheritDoc} */
	@Override
	public Boolean switchOn(final String nodeId) {
		return (Boolean) CommandUtil.switchOp(nodeId, "ON");
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
	 * Kura Cloud Service Callback while deregistering
	 */
	public synchronized void unbindCloudService(final CloudService cloudService) {
		if (this.m_cloudService == cloudService) {
			super.setCloudService(this.m_cloudService = null);
		}
	}

	/**
	 * IFTTT Service Unbinding Callback
	 */
	public synchronized void unbindIFTTTService(final IFTTTConfiguration ifttService) {
		if (this.m_iftttService == ifttService) {
			this.m_iftttService = null;
		}
	}

}
