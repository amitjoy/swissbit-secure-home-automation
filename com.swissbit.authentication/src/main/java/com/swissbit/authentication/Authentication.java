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

/**
 * The implementation of {@link IAuthentication}
 * 
 * @see IAuthentication
 * @author AMIT KUMAR MONDAL
 */
@Component(immediate = true, name = "com.swissbit.authentication")
@Service(value = { IAuthentication.class })
public class Authentication extends Cloudlet implements IAuthentication {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Authentication.class);

	/**
	 * Defines Application ID for Authentication Module
	 */
	private static final String APP_ID = "AUTH-V1";

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
	protected synchronized void activate(ComponentContext context) {
		LOGGER.info("Activating Authentication Component....");
		super.activate(context);
		super.setCloudService(m_cloudService);
		LOGGER.info("Activating Authentication Component... Done.");
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
		LOGGER.info("Deactivating Activity Log Service....");
		super.deactivate(context);
		LOGGER.info("Deactivating Activity Log Service... Done.");
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
		if ("encode".equals(reqTopic.getResources()[0])) {
			// TO-DO
		}
		if ("decode".equals(reqTopic.getResources()[0])) {
			// TO-DO
		}
		respPayload.setResponseCode(KuraResponsePayload.RESPONSE_CODE_OK);
	}

	/** {@inheritDoc} */
	@Override
	public String encode(String text) {
		// TO-DO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public String decode(String text) {
		// TO-DO Auto-generated method stub
		return null;
	}

}
