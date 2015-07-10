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
package com.swissbit.ifttt;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.eclipse.kura.system.SystemService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is used to configure HypeSQL Connection
 *
 * @author AMIT KUMAR MONDAL
 *
 */
@Component(immediate = true, name = "com.swissbit.ifttt")
@Service(value = { IFTTTConfgurationImpl.class })
public class IFTTTConfgurationImpl implements ConfigurableComponent, IFTTTConfiguration {

	/**
	 * Configurable property to set different email IFTTT hashtags
	 */
	private static final String IFTTT_EMAIL_HASHTAGS = "ifttt.email.hashtags";

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IFTTTConfgurationImpl.class);

	/**
	 * Configurable Property to set SMTP Server Address
	 */
	private static final String SMTP_HOST = "smtp.host";

	/**
	 * Configurable Property to set SMTP Server Password
	 */
	private static final String SMTP_PASSWORD = "smtp.password";

	/**
	 * Configurable Property to set SMTP Server Port
	 */
	private static final String SMTP_PORT = "smtp.port";

	/**
	 * Configurable property specifying the SSL Onconenct Property
	 */
	private static final String SMTP_SSL_ONCONNECT = "smtp.ssl.onconnect";

	/**
	 * Configurable Property to set SMTP Server Username
	 */
	private static final String SMTP_USERNAME = "smtp.username";

	/**
	 * Map to store list of configurations
	 */
	private Map<String, Object> m_properties;

	/**
	 * Eclipse Kura System Service Dependency
	 */
	@Reference(bind = "bindSystemService", unbind = "unbindSystemService")
	private volatile SystemService m_systemService;

	/* Constructor */
	public IFTTTConfgurationImpl() {
	}

	/**
	 * Callback used when this service component is activating
	 */
	@Activate
	protected synchronized void activate(final ComponentContext componentContext,
			final Map<String, Object> properties) {
		LOGGER.info("Activating IFTTT Component...");

		this.m_properties = properties;
		this.setConfiguration();

		LOGGER.info("Activating IFTTT Component... Done.");

	}

	/**
	 * Callback to be used while {@link SystemService} is registering
	 */
	public synchronized void bindSystemService(final SystemService systemService) {
		if (this.m_systemService == null) {
			this.m_systemService = systemService;
		}
	}

	/**
	 * Callback used when this service component is deactivating
	 */
	@Deactivate
	protected void deactivate(final ComponentContext context) {
		LOGGER.debug("Deactivating IFTTT Component...");

		LOGGER.debug("Deactivating IFTTT Component... Done.");
	}

	/** {@inheritDoc} */
	@Override
	public boolean sendEmail(final String subject) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Sets or updates the configuration parameters in {@link SystemService}
	 */
	private void setConfiguration() {
		this.m_systemService.getProperties().put(IFTTT_EMAIL_HASHTAGS, this.m_properties.get(IFTTT_EMAIL_HASHTAGS));

		this.m_systemService.getProperties().put(SMTP_HOST, this.m_properties.get(SMTP_HOST));

		this.m_systemService.getProperties().put(SMTP_PORT, this.m_properties.get(SMTP_PORT));

		this.m_systemService.getProperties().put(SMTP_USERNAME, this.m_properties.get(SMTP_USERNAME));

		this.m_systemService.getProperties().put(SMTP_PASSWORD, this.m_properties.get(SMTP_PASSWORD));

		this.m_systemService.getProperties().put(SMTP_SSL_ONCONNECT, this.m_properties.get(SMTP_SSL_ONCONNECT));
	}

	/**
	 * Callback to be used while {@link SystemService} is deregistering
	 */
	public synchronized void unbindSystemService(final SystemService systemService) {
		if (this.m_systemService == systemService) {
			this.m_systemService = null;
		}
	}

	/**
	 * Used to be called when configurations will get updated
	 */
	public void updated(final Map<String, Object> properties) {
		LOGGER.info("Updated IFTTT Component...");

		this.m_properties = properties;
		this.setConfiguration();
		properties.keySet().forEach(s -> LOGGER.info("Update - " + s + ": " + properties.get(s)));

		LOGGER.info("Updated IFTTT Component... Done.");
	}

}