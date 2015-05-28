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
import com.swissbit.activity.log.ActivityLogService;
import com.whizzosoftware.wzwave.commandclass.BasicCommandClass;
import com.whizzosoftware.wzwave.controller.ZWaveController;
import com.whizzosoftware.wzwave.node.ZWaveEndpoint;

/**
 * The implementation of {@link IZwaveDeviceAction}
 * 
 * @see IZwaveDeviceAction
 * @author AMIT KUMAR MONDAL
 */
@Component
@Service
public class ZWaveDeviceAction extends Cloudlet implements IZwaveDeviceAction {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZWaveDeviceAction.class);

	/**
	 * Defines Application ID for ZWave Component
	 */
	private static final String APP_ID = "DEVICE-V1";

	/**
	 * ZWave Controller Service
	 */
	@Reference(bind = "bindZwaveController", unbind = "unbindZwaveController")
	private volatile ZWaveController m_controller;

	/**
	 * ZWave Endpoint Service
	 */
	@Reference(bind = "bindZwaveNode", unbind = "unbindZwaveNode")
	private volatile ZWaveEndpoint m_waveEndpoint;

	/**
	 * Activity Log Service Dependency
	 */
	@Reference(bind = "bindActivityLogService", unbind = "unbindActivityLogService")
	private volatile ActivityLogService m_activityLogService;

	/**
	 * Stores list of all {@link ZWaveEndpoint} service objects
	 */
	private final List<ZWaveEndpoint> list = Lists.newCopyOnWriteArrayList();

	/**
	 * Kura Cloud Service Injection
	 */
	@Reference(bind = "bindCloudService", unbind = "unbindCloudService")
	private volatile CloudService m_cloudService;

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
	protected synchronized void activate(ComponentContext context) {
		LOGGER.info("Activating ZWave Component....");
		super.activate(context);
		LOGGER.info("Activating ZWave Component... Done.");
	}

	/**
	 * Callback while this component is getting deregistered
	 * 
	 * @param properties
	 *            the service configuration properties
	 */
	@Override
	@Deactivate
	protected synchronized void deactivate(ComponentContext context) {
		LOGGER.info("Deactivating ZWave Component....");
		super.deactivate(context);

		LOGGER.info("Deactivating ZWave Component... Done.");
	}

	/**
	 * Callback to be used while {@link ActivityLogService} is registering
	 */
	public synchronized void bindActivityLogService(
			ActivityLogService activityLogService) {
		if (m_activityLogService == null) {
			m_activityLogService = activityLogService;
		}
	}

	/**
	 * Callback to be used while {@link ActivityLogService} is deregistering
	 */
	public synchronized void unbindActivityLogService(
			ActivityLogService activityLogService) {
		if (m_activityLogService == activityLogService)
			m_activityLogService = null;
	}

	/**
	 * ZWaveController Service Binding Callback
	 */
	public synchronized void bindZwaveController(ZWaveController zWaveController) {
		if (m_controller == null) {
			m_controller = zWaveController;
		}
	}

	/**
	 * ZWaveController Service Callback while deregistering
	 */
	public synchronized void unbindZwaveController(
			ZWaveController zWaveController) {
		if (m_controller == zWaveController)
			m_controller = null;
	}

	/**
	 * ZWaveNode Service Binding Callback
	 */
	public synchronized void bindZwaveNode(ZWaveEndpoint zWaveEndpoint) {
		if (m_waveEndpoint == null) {
			list.add(zWaveEndpoint);
			m_waveEndpoint = zWaveEndpoint;
		}
	}

	/**
	 * ZWaveNode Service Callback while deregistering
	 */
	public synchronized void unbindZwaveNode(ZWaveEndpoint zWaveEndpoint) {
		if (m_waveEndpoint == zWaveEndpoint) {
			m_waveEndpoint = null;
			if (list.contains(zWaveEndpoint))
				list.remove(zWaveEndpoint);
		}
	}

	/**
	 * Kura Cloud Service Binding Callback
	 */
	public synchronized void bindCloudService(CloudService cloudService) {
		if (m_cloudService == null) {
			super.setCloudService(m_cloudService = cloudService);
		}
	}

	/**
	 * Kura Cloud Service Callback while deregistering
	 */
	public synchronized void unbindCloudService(CloudService cloudService) {
		if (m_cloudService == cloudService)
			super.setCloudService(m_cloudService = null);
	}

	/** {@inheritDoc} */
	@Override
	protected void doGet(CloudletTopic reqTopic, KuraRequestPayload reqPayload,
			KuraResponsePayload respPayload) throws KuraException {
		// Parse the nodeId
		final byte nodeId = (byte) ((int) (Integer.valueOf((String) reqPayload
				.getMetric("nodeId"))));

		if ("on".equals(reqTopic.getResources()[0])) {
			m_activityLogService.saveLog("Device is turned on");
			switchOn(nodeId);
		}
		if ("off".equals(reqTopic.getResources()[0])) {
			m_activityLogService.saveLog("Device is turned off");
			switchOff(nodeId);
		}
		if ("status".equals(reqTopic.getResources()[0])) {
			m_activityLogService.saveLog("Device status is retrieved");
			respPayload.addMetric("status", getStatus(nodeId));
		}
		if ("list".equals(reqTopic.getResources()[0])) {
			m_activityLogService.saveLog("Connected Devices List is retrieved");
			for (final ZWaveEndpoint node : list) {
				respPayload.addMetric("node.id", node.getNodeId());
			}
		}
		respPayload.setResponseCode(KuraResponsePayload.RESPONSE_CODE_OK);
	}

	/** {@inheritDoc} */
	@Override
	public boolean switchOn(byte nodeId) {
		final ZWaveEndpoint node = findNode(nodeId);
		if (Objects.nonNull(node)) {
			m_controller.sendDataFrame(BasicCommandClass.createSetv1(nodeId,
					(byte) 0xFF));
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean switchOff(byte nodeId) {
		final ZWaveEndpoint node = findNode(nodeId);
		if (Objects.nonNull(node)) {
			m_controller.sendDataFrame(BasicCommandClass.createSetv1(nodeId,
					(byte) 0x00));
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean getStatus(byte nodeId) {
		final ZWaveEndpoint node = findNode(nodeId);
		final BasicCommandClass basicCommandClass = (BasicCommandClass) node
				.getCommandClass(BasicCommandClass.ID);
		final byte value = basicCommandClass.getValue();
		if (value == (byte) 0xFF)
			return false;
		else
			return true;
	}

	/** {@inheritDoc} */
	@Override
	public List<? extends ZWaveEndpoint> getConnectedDevices() {
		return list;
	}

	/**
	 * Returns {@link ZWaveEndpoint} reference for the given node id
	 */
	private ZWaveEndpoint findNode(byte nodeId) {
		for (final ZWaveEndpoint node : list) {
			if (node.getNodeId() == nodeId)
				return node;
		}
		return null;
	}

}
