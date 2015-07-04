package com.swissbit.homeautomation.utils;

/**
 * Created by manit on 04/06/15.
 */
public interface TopicsConstants {

    public static final String LWT = "MQTT/LWT";

    public static final String HEARTBEAT = "HEARTBEAT-V1/";

    public static final String ZWAVE_GET = "ZWAVE-V1/GET/";

    public static final String ZWAVE_STATUS = "ZWAVE-V1/REPLY/";

    public static final String ZWAVE_POST = "ZWAVE-V1/POST/";

    public static final String TOPIC_PUBLISH = "PUB";

    public static final String TOPIC_SUBSCRIBE = "SUB";

    public static final String RASPBERRY_AUTH_PUB = "AUTH-V1/EXEC/decrypt";

    public static final String RASPBERRY_AUTH_SUB = "AUTH-V1/REPLY/";

    public static final String SURVEILLANCE = "SURVEILLANCE-V1/POST/sample";

    public static final String SWITCH_ON_PUB = "DEVICE-V1/EXEC/on";

    public static final String SWITCH_OFF_PUB = "DEVICE-V1/EXEC/off";

    public static final String SWITCH_ON_OFF_LIST_STATUS_SUB = "DEVICE-V1/REPLY/";

    public static final String RETRIEVE_DEVICE_LIST_PUB = "DEVICE-V1/GET/list";

    public static final String RETRIEVE_DEVICE_STATUS_PUB = "DEVICE-V1/GET/status";

    public static final String ACCESS_REVOCATION_SUB = "/permission/revoked";


}
