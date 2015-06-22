package com.swissbit.server.ws;

public interface Constants {

	public static final String DB_URL = "jdbc:mysql://" + System.getenv().get("OPENSHIFT_MYSQL_DB_HOST") + ":" 
										+ System.getenv().get("OPENSHIFT_MYSQL_DB_PORT") + "/andropraktikumtum";
	
	public static final String IP_ADDRESS = System.getenv().get("OPENSHIFT_DIY_IP");
	public static final String MYSQL_USER = System.getenv().get("OPENSHIFT_MYSQL_DB_USERNAME");
	public static final String MYSQL_PASSWORD = System.getenv().get("OPENSHIFT_MYSQL_DB_PASSWORD");
	public static final String PORT = System.getenv().get("OPENSHIFT_DIY_PORT");	

}
