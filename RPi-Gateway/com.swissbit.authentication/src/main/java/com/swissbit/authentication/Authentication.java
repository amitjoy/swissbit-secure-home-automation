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
package com.swissbit.authentication;

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

import com.google.common.base.Charsets;
import com.swissbit.activity.log.IActivityLogService;
import com.swissbit.assd.comm.IASSDCommunication;

/**
 * The implementation of {@link IAuthentication}
 *
 * @see IAuthentication
 * @author AMIT KUMAR MONDAL
 */
@Component(immediate = true, name = "com.swissbit.authentication")
@Service(value = { Authentication.class })
public class Authentication extends Cloudlet {

	/**
	 * Defines Application ID for Authentication Module
	 */
	private static final String APP_ID = "AUTH-V1";

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Authentication.class);

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
	 * Constructor
	 */
	public Authentication() {
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
		LOGGER.info("Activating Authentication Component....");
		super.activate(context);
		super.setCloudService(this.m_cloudService);
		LOGGER.info("Activating Authentication Component... Done.");
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
	 * ASSD Communication Service Binding Callback
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
		LOGGER.info("Deactivating Activity Log Service....");
		super.deactivate(context);
		LOGGER.info("Deactivating Activity Log Service... Done.");
	}

	/** {@inheritDoc} */
	@Override
	protected void doExec(final CloudletTopic reqTopic, final KuraRequestPayload reqPayload,
			final KuraResponsePayload respPayload) throws KuraException {

		if ("decrypt".equals(reqTopic.getResources()[0])) {
			this.m_activityLogService.saveLog("Decryption Requested");
			if (this.m_assdCommunication != null) {
				respPayload.addMetric("data",
						this.m_assdCommunication.decrypt(new String(reqPayload.getBody(), Charsets.UTF_8)));
			}
		}
		respPayload.setResponseCode(KuraResponsePayload.RESPONSE_CODE_OK);
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

}
