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

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.Cloudlet;
import org.eclipse.kura.cloud.CloudletTopic;
import org.eclipse.kura.message.KuraRequestPayload;
import org.eclipse.kura.message.KuraResponsePayload;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	/** {@inheritDoc} */
	@Override
	protected void doGet(CloudletTopic reqTopic, KuraRequestPayload reqPayload,
			KuraResponsePayload respPayload) throws KuraException {
		if ("on".equals(reqTopic.getResources()[0])) {
			switchOn();
		}
		if ("off".equals(reqTopic.getResources()[0])) {
			switchOff();
		}
		respPayload.setResponseCode(KuraResponsePayload.RESPONSE_CODE_OK);
	}

	/** {@inheritDoc} */
	@Override
	public boolean switchOn() {
		// TO-DO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean switchOff() {
		// TO-DO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean getStatus() {
		// TO-DO Auto-generated method stub
		return false;
	}

}
