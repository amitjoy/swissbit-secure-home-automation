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
package com.swissbit.accesscontrol;

/**
 * Defines methods to store and retrieve user permissions
 *
 * @author AMIT KUMAR MONDAL
 *
 */
public interface IAccessControl {

	/**
	 * Represents the location of the all clients file
	 */
	public static final String ALL_CLIENTS_FILE_LOCATION = "/home/pi/swissbit/all-clients-connected.perm";

	/**
	 * Represents the location of the permission file
	 */
	public static final String PERMISSION_FILE_LOCATION = "/home/pi/swissbit/clients-revoked.perm";

	/**
	 * Used to retrieve the contents of the file containing all the clients who
	 * have connected to the RPi previously
	 */
	public String readAllClientsFile();

	/**
	 * Used to retrieve the Permission Configuration stored in the Permission
	 * File
	 */
	public String readPermissionFile();

	/**
	 * Used to store the Permission Configuration to the Permission File
	 */
	public void savePermission(String permissionData);

}
