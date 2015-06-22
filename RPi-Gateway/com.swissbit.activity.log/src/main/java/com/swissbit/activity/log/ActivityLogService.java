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
package com.swissbit.activity.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.cloud.Cloudlet;
import org.eclipse.kura.cloud.CloudletTopic;
import org.eclipse.kura.db.DbService;
import org.eclipse.kura.message.KuraRequestPayload;
import org.eclipse.kura.message.KuraResponsePayload;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * The implementation of IActivityLogService
 *
 * @see IActivityLogService
 * @author AMIT KUMAR MONDAL
 */
@Component(immediate = true, name = "com.swissbit.activity.log.service")
@Service(value = { IActivityLogService.class })
public class ActivityLogService extends Cloudlet implements IActivityLogService {

	/**
	 * Defines Application ID for Activity Logs
	 */
	private static final String APP_ID = "LOGS-V1";

	/**
	 * HyperSQL Database name which comprises all the activity logs
	 */
	private static final String DB_TABLE_NAME = "LOGS.LOGS";

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogService.class);

	/**
	 * Activity logs data retrieval query
	 */
	private static final String QUERY = "SELECT * FROM " + DB_TABLE_NAME;

	/**
	 * Kura Cloud Service Injection
	 */
	@Reference(bind = "bindCloudService", unbind = "unbindCloudService")
	private volatile CloudService m_cloudService;

	/**
	 * The HyperSQL Connection Reference
	 */
	private Connection m_connection;

	/**
	 * Kura DB Service Reference
	 */
	@Reference(bind = "bindDBService", unbind = "unbindDBService")
	private volatile DbService m_dbService;

	/**
	 * The HyperSQL Statement Reference
	 */
	private Statement m_statement;

	/**
	 * Constructor
	 */
	public ActivityLogService() {
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
		LOGGER.info("Activating Activity Log Service....");
		super.setCloudService(this.m_cloudService);
		super.activate(context);
		try {
			this.m_connection = this.m_dbService.getConnection();
			this.m_statement = this.m_connection.createStatement();
		} catch (final SQLException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
		LOGGER.info("Activating Activity Log Service... Done.");
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
	 * Kura DB Service Binding Callback
	 */
	protected synchronized void bindDBService(final DbService dbService) {
		if (this.m_dbService == null) {
			this.m_dbService = dbService;
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

		if (this.m_statement != null) {
			this.m_dbService.close(this.m_statement);
		}

		if (this.m_connection != null) {
			this.m_dbService.close(this.m_connection);
		}

		LOGGER.info("Deactivating Activity Log Service... Done.");
	}

	/** {@inheritDoc} */
	@Override
	protected void doGet(final CloudletTopic reqTopic, final KuraRequestPayload reqPayload,
			final KuraResponsePayload respPayload) throws KuraException {
		if ("logs".equals(reqTopic.getResources()[0])) {
			final List<ActivityLog> logs = this.retrieveLogs();

			logs.forEach(
					activityLog -> respPayload.addMetric(activityLog.getTimestamp(), activityLog.getDescription()));

		}
		respPayload.setResponseCode(KuraResponsePayload.RESPONSE_CODE_OK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ActivityLog> retrieveLogs() {
		LOGGER.debug("Retrieving logs from the Activity Logs Database...");
		final List<ActivityLog> logs = Lists.newArrayList();
		try {
			final ResultSet resultSet = this.m_statement.executeQuery(QUERY);
			while (resultSet.next()) {
				final String timestamp = resultSet.getString("timestamp");
				final String description = resultSet.getString("description");

				final ActivityLog activityLog = new ActivityLog(timestamp, description);
				logs.add(activityLog);
			}
		} catch (final SQLException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}

		LOGGER.debug("Retrieving logs from the Activity Logs Database...Done");
		return logs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveLog(final String log) {
		LOGGER.debug("Saving log to the Activity Logs Database...");
		final String insertStatment = "INSERT INTO " + DB_TABLE_NAME + " (TIMESTAMP, DESCRIPTION) " + " VALUES (" + "'"
				+ LocalDateTime.now() + "'" + "," + "'" + log + "'" + " )";
		try {
			this.m_statement.execute(insertStatment);
		} catch (final SQLException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		}
		LOGGER.debug("Saving log to the Activity Logs Database...Done");
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
	 * Kura DB Service Binding Callback
	 */
	protected synchronized void unbindDBService(final DbService dbService) {
		if (this.m_dbService == dbService) {
			this.m_dbService = null;
		}
	}
}
