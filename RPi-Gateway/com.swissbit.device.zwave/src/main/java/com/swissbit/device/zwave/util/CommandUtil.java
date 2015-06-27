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
package com.swissbit.device.zwave.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.kura.KuraErrorCode;
import org.eclipse.kura.KuraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * Used to execute commands for ZWave Operations
 *
 * @author AMIT KUMAR MONDAL
 *
 */
public final class CommandUtil {

	/**
	 * Represents location for all the ZWave Related Commands
	 */
	private static final String JAR_LOCATION = "/home/pi/swissbit/com.swissbit.device.zwave.operation.jar";

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandUtil.class);

	/**
	 * Represents location of RXTX native library files
	 */
	private static final String RXTX_LIBRARY_PATH = "/usr/lib/jni";

	/**
	 * Represents RXTX JAR file location
	 */
	private static final String RXTX_LOCATION = "/usr/share/java/RXTXcomm.jar";

	/**
	 * Used to be called to turn off the specified device
	 */
	public static Object switchOp(final String nodeIdUnescaped, final String operationName) {
		Process process = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		final List<String> listOfDevices = Lists.newArrayList();

		try {
			// Validate node id as after decryption null characters are getting
			// added to it
			final String nodeId = CharMatcher.inRange('0', '9').retainFrom(nodeIdUnescaped);
			process = Runtime.getRuntime().exec("java -Djava.library.path=" + RXTX_LIBRARY_PATH + " -cp "
					+ RXTX_LOCATION + " -jar " + JAR_LOCATION + " " + nodeId + " " + operationName);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				if (line.contains("Something bad happened")) {
					throw new KuraException(KuraErrorCode.OPERATION_NOT_SUPPORTED);
				}

				if ("ON".equals(operationName)) {
					if (line.contains("ZWave Node is switched on")) {
						return true;
					}
				}

				if ("OFF".equals(operationName)) {
					if (line.contains("ZWave Node is switched off")) {
						return true;
					}
				}

				if ("STATUS".equals(operationName)) {
					if (line.contains("Status")) {
						return line.split(":")[1];
					}
				}

				if ("LIST".equals(operationName)) {
					if (line.contains("ZWave Device Added")) {
						listOfDevices.add(line.split(":")[1]);
						return listOfDevices;
					}
				}
				sb.append(line);
			}
		} catch (final Exception e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
			return false;
		} finally {
			try {
				LOGGER.debug("Closing Buffered Reader and destroying Process", process);
				br.close();
				process.destroy();
			} catch (final IOException e) {
				LOGGER.error("Error closing read buffer", Throwables.getStackTraceAsString(e));
			}
		}
		return true;
	}

}
