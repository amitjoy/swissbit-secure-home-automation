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
import java.util.Objects;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
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

import com.google.common.collect.Lists;
import com.swissbit.activity.log.IActivityLogService;
import com.whizzosoftware.wzwave.commandclass.BasicCommandClass;
import com.whizzosoftware.wzwave.controller.ZWaveController;
import com.whizzosoftware.wzwave.node.ZWaveEndpoint;

/**
 * The implementation of {@link IZwaveDeviceAction}
 *
 * @see IZwaveDeviceAction
 * @author AMIT KUMAR MONDAL
 */
@Component(name = "com.swissbit.device.zwave")
@Service(value = { IZwaveDeviceAction.class })
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
	 * Stores list of all {@link ZWaveEndpoint} service objects
	 */
	private final List<ZWaveEndpoint> list = Lists.newCopyOnWriteArrayList();

	/**
	 * Activity Log Service Dependency
	 */
	@Reference(bind = "bindActivityLogService", unbind = "unbindActivityLogService")
	private volatile IActivityLogService m_activityLogService;

	/**
	 * Kura Cloud Service Injection
	 */
	@Reference(bind = "bindCloudService", unbind = "unbindCloudService")
	private volatile CloudService m_cloudService;

	/**
	 * ZWave Controller Service
	 */
	@Reference(bind = "bindZwaveController", unbind = "unbindZwaveController", cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private volatile ZWaveController m_controller;

	/**
	 * ZWave Endpoint Service
	 */
	@Reference(bind = "bindZwaveNode", unbind = "unbindZwaveNode", cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE)
	private volatile ZWaveEndpoint m_waveEndpoint;

	/**
	 * Constructor
	 */
	public ZWaveDeviceAction() {
		super(APP_ID);
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
	 * Kura Cloud Service Binding Callback
	 */
	public synchronized void bindCloudService(final CloudService cloudService) {
		if (this.m_cloudService == null) {
			super.setCloudService(this.m_cloudService = cloudService);
		}
	}

	/**
	 * ZWaveController Service Binding Callback
	 */
	public synchronized void bindZwaveController(final ZWaveController zWaveController) {
		if (this.m_controller == null) {
			this.m_controller = zWaveController;
		}
	}

	/**
	 * ZWaveNode Service Binding Callback
	 */
	public synchronized void bindZwaveNode(final ZWaveEndpoint zWaveEndpoint) {
		if (this.m_waveEndpoint == null) {
			this.list.add(zWaveEndpoint);
			this.m_waveEndpoint = zWaveEndpoint;
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

		LOGGER.info("Deactivating ZWave Component... Done.");
	}

	/** {@inheritDoc} */
	@Override
	protected void doGet(final CloudletTopic reqTopic, final KuraRequestPayload reqPayload,
			final KuraResponsePayload respPayload) throws KuraException {
		// Parse the nodeId
		final byte nodeId = (byte) ((int) (Integer.valueOf((String) reqPayload.getMetric("nodeId"))));

		if ("on".equals(reqTopic.getResources()[0])) {
			this.m_activityLogService.saveLog("Device is turned on");
			this.switchOn(nodeId);
		}
		if ("off".equals(reqTopic.getResources()[0])) {
			this.m_activityLogService.saveLog("Device is turned off");
			this.switchOff(nodeId);
		}
		if ("status".equals(reqTopic.getResources()[0])) {
			this.m_activityLogService.saveLog("Device status is retrieved");
			respPayload.addMetric("status", this.getStatus(nodeId));
		}
		if ("list".equals(reqTopic.getResources()[0])) {
			this.m_activityLogService.saveLog("Connected Devices List is retrieved");
			this.list.forEach(node -> respPayload.addMetric("node.id", node.getNodeId()));
		}
		respPayload.setResponseCode(KuraResponsePayload.RESPONSE_CODE_OK);
	}

	/**
	 * Returns {@link ZWaveEndpoint} reference for the given node id
	 */
	private ZWaveEndpoint findNode(final byte nodeId) {
		for (final ZWaveEndpoint node : this.list) {
			if (node.getNodeId() == nodeId) {
				return node;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public List<? extends ZWaveEndpoint> getConnectedDevices() {
		return this.list;
	}

	/** {@inheritDoc} */
	@Override
	public boolean getStatus(final byte nodeId) {
		final ZWaveEndpoint node = this.findNode(nodeId);
		final BasicCommandClass basicCommandClass = (BasicCommandClass) node.getCommandClass(BasicCommandClass.ID);
		final byte value = basicCommandClass.getValue();
		if (value == (byte) 0xFF) {
			return false;
		} else {
			return true;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean switchOff(final byte nodeId) {
		final ZWaveEndpoint node = this.findNode(nodeId);
		if (Objects.nonNull(node)) {
			this.m_controller.sendDataFrame(BasicCommandClass.createSetv1(nodeId, (byte) 0x00));
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean switchOn(final byte nodeId) {
		final ZWaveEndpoint node = this.findNode(nodeId);
		if (Objects.nonNull(node)) {
			this.m_controller.sendDataFrame(BasicCommandClass.createSetv1(nodeId, (byte) 0xFF));
			return true;
		}
		return false;
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
	 * Kura Cloud Service Callback while deregistering
	 */
	public synchronized void unbindCloudService(final CloudService cloudService) {
		if (this.m_cloudService == cloudService) {
			super.setCloudService(this.m_cloudService = null);
		}
	}

	/**
	 * ZWaveController Service Callback while deregistering
	 */
	public synchronized void unbindZwaveController(final ZWaveController zWaveController) {
		if (this.m_controller == zWaveController) {
			this.m_controller = null;
		}
	}

	/**
	 * ZWaveNode Service Callback while deregistering
	 */
	public synchronized void unbindZwaveNode(final ZWaveEndpoint zWaveEndpoint) {
		if (this.m_waveEndpoint == zWaveEndpoint) {
			this.m_waveEndpoint = null;
			if (this.list.contains(zWaveEndpoint)) {
				this.list.remove(zWaveEndpoint);
			}
		}
	}

}
