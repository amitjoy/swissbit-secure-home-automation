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

import java.util.List;

/**
 * Used to provide simple device on off operation
 *
 * @author AMIT KUMAR MONDAL
 *
 */
public interface IZwaveDeviceAction {

	/**
	 * Returns the list of devices connected to the RPi
	 */
	public List<String> getConnectedDevices();

	/**
	 * Returns the device status
	 */
	public Boolean getStatus(String nodeId);

	/**
	 * Used to switch the device off
	 */
	public Boolean switchOff(String nodeId);

	/**
	 * Used to switch the device on
	 */
	public Boolean switchOn(String nodeId);

}
