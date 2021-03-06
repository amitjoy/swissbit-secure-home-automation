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
package com.swissbit.assd.comm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
 * Responsible for executing all the necessary commands for ASSD Communication
 *
 * @author AMIT KUMAR MONDAL
 *
 */
public final class ASSDUtil {

	/**
	 * The ASSD Device representation in POSIX
	 */
	private static final String ASSD_DEVICE = "/dev/assd";

	/**
	 * Represents the insmod POSIX command utility
	 */
	private static final String CMD_INSMOD = "insmod";

	/**
	 * Represents the python POSIX command utility
	 */
	private static final String CMD_PYTHON = "python";

	/**
	 * The location of the python programs
	 */
	private static final String LOCATION = "/home/pi/assd/";

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ASSDUtil.class);

	/**
	 * Validates whether ASSD is present
	 */
	public static boolean checkASSD() {
		LOGGER.info("Checking ASSD Device presence...");
		return Files.exists(Paths.get(ASSD_DEVICE));
	}

	/**
	 * Decodes the provided text
	 */
	public static List<String> decrypt(final String text) {
		SafeProcess process = null;
		BufferedReader br = null;
		List<String> lines = null;
		final String fileName = LOCATION + "decrypt.py";
		final String[] command = { CMD_PYTHON, fileName, text };

		boolean assdAvail = checkASSD();

		if (!assdAvail) {
			assdAvail = loadASSD();
		}

		if (assdAvail) {
			try {
				process = ProcessUtil.exec(command);
				br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				lines = Lists.newArrayList();
				String line = null;

				while ((line = br.readLine()) != null) {
					if (line.contains("command not found")) {
						throw new KuraException(KuraErrorCode.OPERATION_NOT_SUPPORTED);
					}
					lines.add(line);
				}
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
			return lines;
		}
		return null;
	}

	/**
	 * Encodes the provided text
	 */
	public static String encrypt(final String text, final String secureElementId) {
		SafeProcess process = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		final String fileName = LOCATION + "encrypt.py";
		final String[] command = { CMD_PYTHON, fileName, secureElementId, text };

		boolean assdAvail = checkASSD();

		if (!assdAvail) {
			assdAvail = loadASSD();
		}

		if (assdAvail) {
			try {
				process = ProcessUtil.exec(command);
				br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				sb = new StringBuilder();
				String line = null;

				while ((line = br.readLine()) != null) {
					if (line.contains("command not found")) {
						throw new KuraException(KuraErrorCode.OPERATION_NOT_SUPPORTED);
					}
					sb.append(line);
				}
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
			return sb.toString();
		}
		return "";
	}

	/**
	 * Loads the ASSD Secure Element if not loaded
	 */
	public static boolean loadASSD() {
		SafeProcess process = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		final String commandParam = LOCATION + "assd.ko";
		final String[] command = { CMD_INSMOD, commandParam };

		try {
			process = ProcessUtil.exec(command);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				if (line.contains("command not found")) {
					throw new KuraException(KuraErrorCode.OPERATION_NOT_SUPPORTED);
				}
				sb.append(line + "\n");
			}
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
		return false;
	}

}
