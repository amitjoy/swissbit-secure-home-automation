/*******************************************************************************
 * Copyright 2015 Amit Kumar Mondal <admin@amitinside.com>, Subburam <subburam.rb@gmail.com>, Gaurav Srivastava <gaurav.srivastava7@gmail.com>
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
package com.swissbit.server.ws;

public interface Constants {

	public static final String DB_URL = "jdbc:mysql://" + System.getenv().get("OPENSHIFT_MYSQL_DB_HOST") + ":" 
										+ System.getenv().get("OPENSHIFT_MYSQL_DB_PORT") + "/andropraktikumtum";
	
	public static final String IP_ADDRESS = System.getenv().get("OPENSHIFT_DIY_IP");
	public static final String MYSQL_USER = System.getenv().get("OPENSHIFT_MYSQL_DB_USERNAME");
	public static final String MYSQL_PASSWORD = System.getenv().get("OPENSHIFT_MYSQL_DB_PASSWORD");
	public static final String PORT = System.getenv().get("OPENSHIFT_DIY_PORT");	

}
