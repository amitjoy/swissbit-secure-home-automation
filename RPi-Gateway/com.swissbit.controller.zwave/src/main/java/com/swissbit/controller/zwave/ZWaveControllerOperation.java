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
package com.swissbit.controller.zwave;

import java.util.Dictionary;
import java.util.Hashtable;
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
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.eclipse.kura.message.KuraPayload;
import org.eclipse.kura.message.KuraRequestPayload;
import org.eclipse.kura.message.KuraResponsePayload;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.swissbit.activity.log.IActivityLogService;
import com.whizzosoftware.wzwave.controller.ZWaveController;
import com.whizzosoftware.wzwave.controller.ZWaveControllerListener;
import com.whizzosoftware.wzwave.controller.netty.NettyZWaveController;
import com.whizzosoftware.wzwave.node.ZWaveEndpoint;

/**
 * The implementation of {@link IAuthentication}
 * 
 * @see IAuthentication
 * @author AMIT KUMAR MONDAL
 */
@Component(immediate = true, name = "com.swissbit.controller.zwave")
@Service(value = { ZWaveControllerOperation.class })
public class ZWaveControllerOperation extends Cloudlet implements
		ConfigurableComponent, IZwaveControllerOperation,
		ZWaveControllerListener {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZWaveControllerOperation.class);

	/**
	 * Configurable property to specify ZWave Controller Device Address
	 */
	private static final String CONTROLLER_DEVICE_ADDR = "controller.device.address";

	/**
	 * Defines Application ID for Zwave Controller Module
	 */
	private static final String APP_ID = "CONTROLLER-V1";

	/**
	 * Kura Cloud Service Injection
	 */
	@Reference(bind = "bindCloudService", unbind = "unbindCloudService")
	private volatile CloudService m_cloudService;

	/**
	 * Activity Log Service Dependency
	 */
	@Reference(bind = "bindActivityLogService", unbind = "unbindActivityLogService")
	private volatile IActivityLogService m_activityLogService;

	/**
	 * Configurable Properties set using Metatype Configuration Management
	 */
	private Map<String, Object> m_properties;

	/**
	 * Bundle Context.
	 */
	private BundleContext m_context;

	/**
	 * ZWave Controller
	 */
	private ZWaveController m_zwaveController;

	/**
	 * Constructor
	 */
	public ZWaveControllerOperation() {
		super(APP_ID);
	}

	/**
	 * Callback while this component is getting registered
	 * 
	 * @param properties
	 *            the service configuration properties
	 */
	@Activate
	protected synchronized void activate(ComponentContext context,
			Map<String, Object> properties) {
		LOGGER.info("Activating Zwave Controller Component....");
		super.activate(context);
		super.setCloudService(m_cloudService);
		m_properties = properties;
		m_context = context.getBundleContext();
		LOGGER.info("Activating Zwave Controller Component... Done.");
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
		LOGGER.info("Deactivating Zwave Controller Component....");
		super.deactivate(context);
		LOGGER.info("Deactivating Zwave Controller Component... Done.");
	}

	/**
	 * Used to be called when configurations will get updated
	 */
	public void updated(Map<String, Object> properties) {
		LOGGER.info("Updating Zwave Controller Component...");

		m_properties = properties;
		for (final String s : properties.keySet()) {
			LOGGER.info("Update - " + s + ": " + properties.get(s));
		}

		LOGGER.info("Updated Zwave Controller Component... Done.");
	}

	/**
	 * Callback to be used while {@link IActivityLogService} is registering
	 */
	public synchronized void bindActivityLogService(
			IActivityLogService activityLogService) {
		if (m_activityLogService == null) {
			m_activityLogService = activityLogService;
		}
	}

	/**
	 * Callback to be used while {@link IActivityLogService} is deregistering
	 */
	public synchronized void unbindActivityLogService(
			IActivityLogService activityLogService) {
		if (m_activityLogService == activityLogService)
			m_activityLogService = null;
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
		if ("on".equals(reqTopic.getResources()[0])) {
			m_activityLogService.saveLog("ZWave Controller is started");
			start();
		}

		if ("off".equals(reqTopic.getResources()[0])) {
			m_activityLogService.saveLog("ZWave Controller is stopped");
			stop();
		}
		respPayload.setResponseCode(KuraResponsePayload.RESPONSE_CODE_OK);
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		LOGGER.info("ZWave Controller is starting....");
		final String controllerDeviceAddr = (String) m_properties
				.get(CONTROLLER_DEVICE_ADDR);
		m_zwaveController = new NettyZWaveController(controllerDeviceAddr);
		m_zwaveController.setListener(this);

		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("controller.device.address", controllerDeviceAddr);
		properties.put("controller.node.id", m_zwaveController.getNodeId());
		properties.put("controller.home.id", m_zwaveController.getHomeId());

		m_context.registerService(ZWaveController.class, m_zwaveController,
				properties);
		LOGGER.info("ZWave Controller is starting....Done");
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		LOGGER.info("ZWave Controller is stopping....");
		m_zwaveController.stop();
		getCloudApplicationClient().release();
		LOGGER.info("ZWave Controller is stopping....Done");
	}

	/** {@inheritDoc} */
	@Override
	public void onZWaveConnectionFailure(Throwable throwable) {
		LOGGER.debug("Zwave Connection Failure"
				+ Throwables.getStackTraceAsString(throwable));
		try {
			getCloudApplicationClient().publish("zwave/failure", null,
					DFLT_PUB_QOS, DFLT_RETAIN);
		} catch (final KuraException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}

	}

	/** {@inheritDoc} */
	@Override
	public void onZWaveNodeAdded(ZWaveEndpoint node) {
		LOGGER.info("New ZWave Device Found " + node.getGenericDeviceClass());
		try {
			final KuraPayload payload = new KuraPayload();
			payload.addMetric("node.address", node.getGenericDeviceClass());
			payload.addMetric("node.id", node.getNodeId());
			getCloudApplicationClient().publish("zwave/node/added", payload,
					DFLT_PUB_QOS, DFLT_RETAIN);
			registerZwaveEndpointAsService(node);
		} catch (final KuraException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void onZWaveNodeUpdated(ZWaveEndpoint node) {
		LOGGER.info("ZWave Device Updated " + node.getGenericDeviceClass());
		try {
			final KuraPayload payload = new KuraPayload();
			payload.addMetric("node.address", node.getGenericDeviceClass());
			payload.addMetric("node.id", node.getNodeId());
			getCloudApplicationClient().publish("zwave/node/updated", payload,
					DFLT_PUB_QOS, DFLT_RETAIN);
			reregisterZwaveEndpointAsService(node);
		} catch (final KuraException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
	}

	/**
	 * Used to register the currently added ZWave Node as OSGi Service
	 */
	private void registerZwaveEndpointAsService(ZWaveEndpoint node) {
		LOGGER.info("Registering New Node in Registry... " + node.getNodeId());
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("node.address", node.getNodeId());
		m_context.registerService(ZWaveEndpoint.class, node, properties);
		LOGGER.info("Registering New Node in Registry... Done"
				+ node.getNodeId());
	}

	/**
	 * Used to re-register the currently updated ZWave Node as OSGi Service
	 */
	private void reregisterZwaveEndpointAsService(final ZWaveEndpoint node) {
		LOGGER.info("Re-registering Updated Node in Registry... "
				+ node.getNodeId());
		try {
			searchNodeServiceAndUnregister(node);
		} catch (final InvalidSyntaxException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
		registerZwaveEndpointAsService(node);
		LOGGER.info("Re-rgistering Updated Node in Registry... Done"
				+ node.getNodeId());
	}

	/**
	 * Used to consume the ZwaveEndPoint Service for unregistering the previous
	 * invalid service reference
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void searchNodeServiceAndUnregister(final ZWaveEndpoint node)
			throws InvalidSyntaxException {

		final String filterText = "&(objectClass="
				+ ZWaveEndpoint.class.getName() + ")(node.address="
				+ node.getNodeId() + ")";

		final Filter filter = m_context.createFilter(filterText);
		final ServiceTracker zWaveTracker = new ServiceTracker(m_context,
				filter, null);

		zWaveTracker.open();

		final Object[] services = zWaveTracker.getServices();

		if (services != null) {
			for (int i = 0; i < services.length; i++) {
				final ServiceReference reference = zWaveTracker
						.getServiceReference();
				m_context.ungetService(reference);
			}
		}

	}
}
