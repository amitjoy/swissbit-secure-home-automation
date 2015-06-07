/*******************************************************************************
 * Copyright 2015 Amit Kumar Mondal <admin@amitinside.com>
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
package com.swissbit.assd.comm;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swissbit.assd.comm.util.ASSDUtil;

/**
 * The implementation of {@link IASSDCommunication}
 *
 * @author AMIT KUMAR MONDAL
 *
 */
@Component(immediate = true, name = "com.swissbit.assd.comm")
@Service(value = IASSDCommunication.class)
public class ASSDCommunication implements IASSDCommunication {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ASSDCommunication.class);

	/** {@inheritDoc}} */
	@Override
	public String decrypt(final String text) {
		LOGGER.debug("Decrypting data...");
		return ASSDUtil.cryptoTool(text, "decrypt.py");
	}

	/** {@inheritDoc}} */
	@Override
	public String encrypt(final String text) {
		LOGGER.debug("Encrypting data...");
		return ASSDUtil.cryptoTool(text, "encrypt.py");
	}

	/** {@inheritDoc}} */
	@Override
	public boolean isCardPresent() {
		// TODO Auto-generated method stub
		return false;
	}

}
