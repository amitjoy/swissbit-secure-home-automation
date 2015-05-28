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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.Cloudlet;
import org.eclipse.kura.cloud.CloudletTopic;
import org.eclipse.kura.db.DbService;
import org.eclipse.kura.message.KuraRequestPayload;
import org.eclipse.kura.message.KuraResponsePayload;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

/**
 * The implementation of ActivityLogService
 * 
 * @see ActivityLogService
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
	 * Defines Application ID for Activity Logs
	 */
	private static final String APP_ID = "LOGS-V1";

	/**
	 * HyperSQL Database name which comprises all the activity logs
	 */
	private static final String TABLE_NAME = "logs";

	/**
	 * The HyperSQL Connection Reference
	 */
	private Connection m_connection;

	/**
	 * The HyperSQL Statement Reference
	 */
	private Statement m_statement;

	/**
	 * Kura DB Service Reference
	 */
	@Reference(bind = "bindDBService", unbind = "bindDBService")
	private volatile DbService m_dbService;

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
		LOGGER.info("Activating Activity Log Service....");
		super.activate(context);
		try {
			m_connection = m_dbService.getConnection();
			m_statement = m_connection.createStatement();
		} catch (final SQLException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
		LOGGER.info("Activating Activity Log Service... Done.");
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

		if (m_statement != null)
			m_dbService.close(m_statement);

		if (m_connection != null)
			m_dbService.close(m_connection);

		LOGGER.info("Deactivating Activity Log Service... Done.");
	}

	/**
	 * Kura DB Service Binding Callback
	 */
	protected synchronized void bindDBService(DbService dbService) {
		if (m_dbService == null)
			m_dbService = dbService;
	}

	/**
	 * Kura DB Service Binding Callback
	 */
	protected synchronized void unbindDBService(DbService dbService) {
		if (m_dbService == dbService)
			m_dbService = null;
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
