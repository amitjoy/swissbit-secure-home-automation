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
@Service(value = { IZWaveControllerOperation.class })
public class ZWaveControllerOperation extends Cloudlet
		implements ConfigurableComponent, IZWaveControllerOperation, ZWaveControllerListener {

	/**
	 * Defines Application ID for Zwave Controller Module
	 */
	private static final String APP_ID = "CONTROLLER-V1";

	/**
	 * Configurable property to specify ZWave Controller Device Address
	 */
	private static final String CONTROLLER_DEVICE_ADDR = "controller.device.address";

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ZWaveControllerOperation.class);

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
	 * Bundle Context.
	 */
	private BundleContext m_context;

	/**
	 * Configurable Properties set using Metatype Configuration Management
	 */
	private Map<String, Object> m_properties;

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
	protected synchronized void activate(final ComponentContext context, final Map<String, Object> properties) {
		LOGGER.info("Activating Zwave Controller Component....");
		super.activate(context);
		super.setCloudService(this.m_cloudService);
		this.m_properties = properties;
		this.m_context = context.getBundleContext();
		LOGGER.info("Activating Zwave Controller Component... Done.");
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
	 * Callback while this component is getting deregistered
	 *
	 * @param properties
	 *            the service configuration properties
	 */
	@Override
	@Deactivate
	protected synchronized void deactivate(final ComponentContext context) {
		LOGGER.info("Deactivating Zwave Controller Component....");
		super.deactivate(context);
		LOGGER.info("Deactivating Zwave Controller Component... Done.");
	}

	/** {@inheritDoc} */
	@Override
	protected void doExec(final CloudletTopic reqTopic, final KuraRequestPayload reqPayload,
			final KuraResponsePayload respPayload) throws KuraException {
		if ("on".equals(reqTopic.getResources()[0])) {
			this.m_activityLogService.saveLog("ZWave Controller is started");
			this.start();
		}

		if ("off".equals(reqTopic.getResources()[0])) {
			this.m_activityLogService.saveLog("ZWave Controller is stopped");
			this.stop();
		}
		respPayload.setResponseCode(KuraResponsePayload.RESPONSE_CODE_OK);
	}

	/** {@inheritDoc} */
	@Override
	public void onZWaveConnectionFailure(final Throwable throwable) {
		LOGGER.debug("Zwave Connection Failure" + Throwables.getStackTraceAsString(throwable));
		try {
			this.getCloudApplicationClient().publish("zwave/failure", null, DFLT_PUB_QOS, DFLT_RETAIN);
		} catch (final KuraException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}

	}

	/** {@inheritDoc} */
	@Override
	public void onZWaveNodeAdded(final ZWaveEndpoint node) {
		LOGGER.info("New ZWave Device Found " + node.getGenericDeviceClass());
		try {
			final KuraPayload payload = new KuraPayload();
			payload.addMetric("node.address", node.getGenericDeviceClass());
			payload.addMetric("node.id", node.getNodeId());
			this.getCloudApplicationClient().publish("zwave/node/added", payload, DFLT_PUB_QOS, DFLT_RETAIN);
			this.registerZwaveEndpointAsService(node);
		} catch (final KuraException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void onZWaveNodeUpdated(final ZWaveEndpoint node) {
		LOGGER.info("ZWave Device Updated " + node.getGenericDeviceClass());
		try {
			final KuraPayload payload = new KuraPayload();
			payload.addMetric("node.address", node.getGenericDeviceClass());
			payload.addMetric("node.id", node.getNodeId());
			this.getCloudApplicationClient().publish("zwave/node/updated", payload, DFLT_PUB_QOS, DFLT_RETAIN);
			this.reregisterZwaveEndpointAsService(node);
		} catch (final KuraException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
	}

	/**
	 * Used to register the currently added ZWave Node as OSGi Service
	 */
	private void registerZwaveEndpointAsService(final ZWaveEndpoint node) {
		LOGGER.info("Registering New Node in Registry... " + node.getNodeId());
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("node.address", node.getNodeId());
		this.m_context.registerService(ZWaveEndpoint.class, node, properties);
		LOGGER.info("Registering New Node in Registry... Done" + node.getNodeId());
	}

	/**
	 * Used to re-register the currently updated ZWave Node as OSGi Service
	 */
	private void reregisterZwaveEndpointAsService(final ZWaveEndpoint node) {
		LOGGER.info("Re-registering Updated Node in Registry... " + node.getNodeId());
		try {
			this.searchNodeServiceAndUnregister(node);
		} catch (final InvalidSyntaxException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
		this.registerZwaveEndpointAsService(node);
		LOGGER.info("Re-rgistering Updated Node in Registry... Done" + node.getNodeId());
	}

	/**
	 * Used to consume the ZwaveEndPoint Service for unregistering the previous
	 * invalid service reference
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void searchNodeServiceAndUnregister(final ZWaveEndpoint node) throws InvalidSyntaxException {

		final String filterText = "&(objectClass=" + ZWaveEndpoint.class.getName() + ")(node.address="
				+ node.getNodeId() + ")";

		final Filter filter = this.m_context.createFilter(filterText);
		final ServiceTracker zWaveTracker = new ServiceTracker(this.m_context, filter, null);

		zWaveTracker.open();

		// It will always return an array of length 1
		final Object[] services = zWaveTracker.getServices();

		if (services != null) {
			final ServiceReference reference = zWaveTracker.getServiceReference();
			this.m_context.ungetService(reference);
		}

	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		LOGGER.info("ZWave Controller is starting....");
		final String controllerDeviceAddr = (String) this.m_properties.get(CONTROLLER_DEVICE_ADDR);
		this.m_zwaveController = new NettyZWaveController(controllerDeviceAddr);
		this.m_zwaveController.setListener(this);

		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("controller.device.address", controllerDeviceAddr);
		properties.put("controller.node.id", this.m_zwaveController.getNodeId());
		properties.put("controller.home.id", this.m_zwaveController.getHomeId());

		this.m_context.registerService(ZWaveController.class, this.m_zwaveController, properties);
		LOGGER.info("ZWave Controller is starting....Done");
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		LOGGER.info("ZWave Controller is stopping....");
		this.m_zwaveController.stop();
		this.getCloudApplicationClient().release();
		LOGGER.info("ZWave Controller is stopping....Done");
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
	 * Used to be called when configurations will get updated
	 */
	public void updated(final Map<String, Object> properties) {
		LOGGER.info("Updating Zwave Controller Component...");

		this.m_properties = properties;

		properties.keySet().forEach(s -> LOGGER.info("Update - " + s + ": " + properties.get(s)));

		LOGGER.info("Updated Zwave Controller Component... Done.");
	}
}
