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
import org.eclipse.kura.core.util.ProcessUtil;
import org.eclipse.kura.core.util.SafeProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * Represents the python POSIX command utility
	 */
	private static final String CMD_PYTHON = "python";

	/**
	 * Home Folder Location
	 */
	private static final String HOME_LOCATION = "/home/pi/swissbit/";

	/**
	 * Represents location for all the ZWave Related Commands
	 */
	private static final String JAR_LOCATION = HOME_LOCATION + "com.swissbit.device.zwave.operation.jar";

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandUtil.class);

	/**
	 * Represents location for SERIAL PORT RESET Command Utility
	 */
	private static final String RESET_SERIAL = HOME_LOCATION + "resetTTYUSB.py";

	/**
	 * Represents location of RXTX native library files
	 */
	private static final String RXTX_LIBRARY_PATH = "/usr/lib/jni";

	/**
	 * Represents RXTX JAR file location
	 */
	private static final String RXTX_LOCATION = "/usr/share/java/RXTXcomm.jar";

	/**
	 * Represents the driver name to be reset. This can be found by lsmod | grep
	 * usbserial command
	 */
	private static final String SERIAL_PORT_DRIVER = "cp210x";

	/**
	 * Resets the Serial Port responsible for ZWave PC Controller (Aeon Z-Stick)
	 */
	public static void resetSerialPort(final String moduleName) {
		LOGGER.info("Resetting of Serial Port USB Started...");

		SafeProcess process = null;
		BufferedReader br = null;
		final String[] command = { CMD_PYTHON, RESET_SERIAL, moduleName };

		try {
			process = ProcessUtil.exec(command);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;

			while ((line = br.readLine()) != null) {
				if (line.contains("command not found")) {
					LOGGER.error("Resetting Command Not Found");
					throw new KuraException(KuraErrorCode.OPERATION_NOT_SUPPORTED);
				}
			}

			LOGGER.info("Resetting of Serial Port USB Started...Done");
		} catch (final Exception e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
		} finally {
			try {
				LOGGER.debug("Closing Buffered Reader and destroying Process", process);
				br.close();
				process.destroy();
			} catch (final IOException e) {
				LOGGER.error("Error closing read buffer", Throwables.getStackTraceAsString(e));
			}
		}
	}

	/**
	 * Used to be called to turn off the specified device
	 */
	public static Object switchOp(final String nodeId, final String operationName) {
		LOGGER.info("Executing Z-Wave Device Operation..." + operationName + " on Z-Wave Device #" + nodeId);

		Process process = null;
		BufferedReader br = null;
		final List<String> listOfDevices = Lists.newArrayList();

		try {
			// Before each serial port communication, reset the serial port to
			// make sure that all operations related to ZWave Device gets to be
			// successful
			resetSerialPort(SERIAL_PORT_DRIVER);
			process = Runtime.getRuntime().exec("java -Djava.library.path=" + RXTX_LIBRARY_PATH + " -cp "
					+ RXTX_LOCATION + " -jar " + JAR_LOCATION + " " + nodeId + " " + operationName);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;

			while ((line = br.readLine()) != null) {
				if (line.contains("Something bad happened")) {
					LOGGER.error("Something bad happened with Serial Communicaation");
					throw new KuraException(KuraErrorCode.OPERATION_NOT_SUPPORTED);
				}

				if ("ON".equals(operationName)) {
					if (line.contains("ZWave Node is switched on")) {
						LOGGER.info("Executing Z-Wave Device Switch ON Operation on Z-Wave Device #" + nodeId
								+ "....... Done");
						return true;
					}
				}

				if ("OFF".equals(operationName)) {
					if (line.contains("ZWave Node is switched off")) {
						LOGGER.info("Executing Z-Wave Device Switch OFF Operation on Z-Wave Device #" + nodeId
								+ "....... Done");
						return true;
					}
				}

				if ("STATUS".equals(operationName)) {
					if (line.contains("Status")) {
						LOGGER.info("Executing Z-Wave Device Switch Status Operation on Z-Wave Device #" + nodeId
								+ "....... Done");
						return line.split(":")[1];
					}
				}

				if ("LIST".equals(operationName)) {
					if (line.contains("ZWave Device Added")) {
						LOGGER.info("Executing Z-Wave Devices List Operation....... Done");
						listOfDevices.add(line.split(":")[1]);
						return listOfDevices;
					}
				}
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
