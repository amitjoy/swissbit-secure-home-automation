package com.swissbit.homeautomation.utils;

/**
 * Created by manit on 08/06/15.
 */
public interface DBConstants {

    public static final String DATABASE_NAME="DevicesInfoDb";

    public static final int DATABASE_VERSION = 57;

    public static final String TABLE_NAME_RASPBERRYINFO="RaspberryInfo";
    public static final String UID = "_id";
    public static final String RASPBERRYID = "RaspberryId";
    public static final String RASPBERRYNAME = "RaspberryName";
    public static final String RASPBERRYDESC = "RaspberryDesc";

    public static final String TABLE_NAME_CREDENTIALS="Credentials";
    public static final String CODE = "Code";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    public static final String DIALOGSHOW = "DialogShow";

    public static final String CREATE_TABLE_RASPBERRYINFO = "CREATE TABLE " + TABLE_NAME_RASPBERRYINFO
            + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RASPBERRYID
            + " VARCHAR(15), " + RASPBERRYNAME
            + " VARCHAR(25), " + RASPBERRYDESC
            + " VARCHAR(50));";

    public static final String CREATE_TABLE_CREDENTIALS = "CREATE TABLE " + TABLE_NAME_CREDENTIALS
            + " (" + CODE + " VARCHAR(10) DEFAULT NULL, " + USERNAME
            + " VARCHAR(15), " + PASSWORD
            + " VARCHAR(15), " + DIALOGSHOW
            + " INTEGER DEFAULT 0);";

    public static final String DROP_TABLE_RASPBERRYINFO = "DROP TABLE IF EXISTS " + TABLE_NAME_RASPBERRYINFO;
    public static final String DROP_TABLE_CREDENTIALS = "DROP TABLE IF EXISTS " + TABLE_NAME_CREDENTIALS;

}
