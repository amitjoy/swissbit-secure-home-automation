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

import org.eclipse.kura.KuraErrorCode;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.core.util.ProcessUtil;
import org.eclipse.kura.core.util.SafeProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

/**
 * Used to execute commands for ZWave Operations
 *
 * @author AMIT KUMAR MONDAL
 *
 */
public final class CommandUtil {

	/**
	 * Represents command to execute external jar file
	 */
	private static final String CMD_JAVA = "java";

	/**
	 * Represents location for all the ZWave Related Commands
	 */
	private static final String JAR_LOCATION = "/home/pi/abc.jar";

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
	public static boolean switchOp(final String nodeId, final String operationName) {
		SafeProcess process = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		final String[] command = { CMD_JAVA, "-Djava.library.path=" + RXTX_LIBRARY_PATH, "-cp " + RXTX_LOCATION,
				"-jar " + JAR_LOCATION, nodeId, operationName };

		try {
			process = ProcessUtil.exec(command);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				if (line.contains("Something bad happened")) {
					throw new KuraException(KuraErrorCode.OPERATION_NOT_SUPPORTED);
				}

				if ("ON".equals(operationName)) {
					if (line.contains("Z-Wave node added")) {
						// TODO
					}
				}

				if ("OFF".equals(operationName)) {
					if (line.contains("Z-Wave node added")) {
						// TODO
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
