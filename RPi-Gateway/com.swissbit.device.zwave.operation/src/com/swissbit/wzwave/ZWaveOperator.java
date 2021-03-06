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
package com.swissbit.wzwave;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.whizzosoftware.wzwave.commandclass.BinarySwitchCommandClass;
import com.whizzosoftware.wzwave.commandclass.MeterCommandClass;
import com.whizzosoftware.wzwave.controller.ZWaveController;
import com.whizzosoftware.wzwave.controller.ZWaveControllerListener;
import com.whizzosoftware.wzwave.controller.netty.NettyZWaveController;
import com.whizzosoftware.wzwave.node.ZWaveEndpoint;

/**
 * This class is used to communicate with ZWave PC Controller
 *
 * @author AMIT KUMAR MONDAL
 *
 */
public final class ZWaveOperator implements ZWaveControllerListener {

	/**
	 * Used to hold the passed argument as command
	 */
	private static String s_argCommand = null;

	/**
	 * Used to hold the passed argument as command for detecting node id
	 */
	private static String s_argDevCommand = null;

	/**
	 * Lock before receiving data
	 */
	private static final Lock s_lock = new ReentrantLock();

	/**
	 * Represents the node id to control
	 */
	private static byte s_nodeId;

	/**
	 * Condition to get notified
	 */
	private static final Condition s_waker = s_lock.newCondition();

	/**
	 * ZWave PC Controller Location
	 */
	private static final String ZWAVE_PC_CONTROLLER = "/dev/ttyUSB0";

	/**
	 * ZWave PC Controller Location
	 */
	private static final String ZWAVE_PC_CONTROLLER_LOCK_FILE = "/var/lock/LCK..ttyUSB0";

	public static void main(final String... args) {

		if (args.length > 1) {
			s_argCommand = args[0];
			s_argDevCommand = args[1];
		}

		if ((s_argCommand == null) || (s_argDevCommand == null)) {
			throw new IllegalArgumentException("You have to mention command and node id to operate on the device");
		}

		try {
			s_nodeId = ((byte) ((int) (Integer.valueOf(s_argCommand))));
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException("You have to mention an integer as node id");
		}

		try {

			if (Files.exists(Paths.get(ZWAVE_PC_CONTROLLER_LOCK_FILE))) {
				Files.delete(Paths.get(ZWAVE_PC_CONTROLLER_LOCK_FILE));
			}

		} catch (final IOException e) {
			// No need to log
		}

		try {
			new ZWaveOperator();
			s_lock.lock();
			s_waker.await();
		} catch (final InterruptedException e) {
			// Ignore
		} finally {
			s_lock.unlock();
			System.exit(0);
		}

	}

	/**
	 * ZWave Controller Reference
	 */
	private final ZWaveController controller;

	/**
	 * Constructor
	 */
	public ZWaveOperator() {
		this.controller = new NettyZWaveController(ZWAVE_PC_CONTROLLER);
		this.controller.setListener(this);
		this.controller.start();
	}

	/** {@inheritDoc}} */
	@Override
	public void onZWaveConnectionFailure(final Throwable t) {
		System.out.println("Something bad happened: " + t);
	}

	/** {@inheritDoc}} */
	@Override
	public void onZWaveNodeAdded(final ZWaveEndpoint node) {
		// Don't consider the RPi as a ZWave Node to which the ZWave PC
		// Controller is attached
		if (node.getNodeId() == 2) {
			return;
		}

		System.out.println("ZWave Device Added:" + node.getNodeId());

		switch (s_argDevCommand) {
		case "ON":
			if (node.getNodeId() == s_nodeId) {
				System.out.println("ZWave Node is switched on");
				this.controller.sendDataFrame(BinarySwitchCommandClass.createSetv1(node.getNodeId(), true));
			}
			break;

		case "OFF":
			if (node.getNodeId() == s_nodeId) {
				System.out.println("ZWave Node is switched off");
				this.controller.sendDataFrame(BinarySwitchCommandClass.createSetv1(node.getNodeId(), false));
			}
			break;

		case "STATUS":
			if (node.getNodeId() == s_nodeId) {
				this.controller.sendDataFrame(BinarySwitchCommandClass.createGetv1(node.getNodeId()));
				final BinarySwitchCommandClass bscc = (BinarySwitchCommandClass) node
						.getCommandClass(BinarySwitchCommandClass.ID);
				System.out.println("Status:" + (bscc.isOn()));
			}
			break;

		case "CONSUMPTION":
			if (node.getNodeId() == s_nodeId) {
				this.controller.sendDataFrame(
						MeterCommandClass.createGetv2(node.getNodeId(), MeterCommandClass.SCALE_ELECTRIC_KWH));
				final MeterCommandClass mcc = (MeterCommandClass) node.getCommandClass(MeterCommandClass.ID);
				System.out.println("Consumption:" + (mcc.getCurrentValue()));
			}
			break;

		case "LIST":
			break;

		default:
			throw new IllegalArgumentException("Your command is not recognized");
		}

		// Signal the Thread
		try {
			s_lock.lock();
			s_waker.signal();
		} finally {
			s_lock.unlock();
		}
	}

	/** {@inheritDoc}} */
	@Override
	public void onZWaveNodeUpdated(final ZWaveEndpoint node) {
		// No need
	}
}