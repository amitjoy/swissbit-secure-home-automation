package com.swissbit.homeautomation.utils;

import android.content.Context;
import android.util.Log;

import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClient;
import com.swissbit.mqtt.client.message.KuraPayload;

import org.fusesource.mqtt.client.Topic;

import java.util.Random;

/**
 * Created by manit on 04/06/15.
 */
public final class MQTTFactory {

    private static String clientId;

    private static String raspberryId;

    private static IKuraMQTTClient iKuraMQTTClient;

    public static String getClientId() {
        if (clientId == null)
            clientId = DBFactory.getDevicesInfoDbAdapter(ActivityContexts.getMainActivityContext()).getCredentials()[2];
        Log.d("Kura MQTT",clientId);
        return clientId;
    }

    public static synchronized IKuraMQTTClient getClient() {

        if (iKuraMQTTClient == null){
            iKuraMQTTClient = new KuraMQTTClient.Builder()
                    .setHost("m20.cloudmqtt.com").setPort("13273")
                    .setClientId(getClientId()).setUsername(getUsername())
                    .setPassword(getPassword()).build();
        }
        return iKuraMQTTClient;
    }

    public static String getUsername() {

        return DBFactory.getDevicesInfoDbAdapter(ActivityContexts.getMainActivityContext()).getCredentials()[0];
    }

    public static String getPassword() {

        return DBFactory.getDevicesInfoDbAdapter(ActivityContexts.getMainActivityContext()).getCredentials()[1];
    }

    public static void setRaspberryId(String raspberryId) {
        MQTTFactory.raspberryId = raspberryId;
    }

    public static String getRaspberryPiById() {
//        return DBFactory.getDevicesInfoDbAdapter(context).getRaspberryId();
        return raspberryId;
    }

    public static String[] getTopicToSubscribe(String id) {

        final String requestId = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
        switch (id) {
            case TopicsConstants.LWT:
                return new String[]{getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.LWT};

            case TopicsConstants.HEARTBEAT:
                return new String[]{getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.HEARTBEAT + "mqtt/heartbeat"};

            case TopicsConstants.ZWAVE_STATUS:
                return new String[]{getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.ZWAVE_STATUS, requestId};

            case TopicsConstants.RASPBERRY_AUTH_SUB:
                return new String[]{getMQTTTopicPrefix(TopicsConstants.TOPIC_SUBSCRIBE) + TopicsConstants.RASPBERRY_AUTH_SUB + requestId, requestId};

            case TopicsConstants.SWITCH_ON_OFF_LIST_STATUS_SUB:
                return new String[]{getMQTTTopicPrefix(TopicsConstants.TOPIC_SUBSCRIBE) + TopicsConstants.SWITCH_ON_OFF_LIST_STATUS_SUB + requestId, requestId};

        }

        return null;
    }

    public static String getMQTTTopicPrefix(String type) {

        switch (type) {
            case TopicsConstants.TOPIC_PUBLISH:
                return "$EDC/" + "swissbit/" + getRaspberryPiById() + "/";

            case TopicsConstants.TOPIC_SUBSCRIBE:
                return "$EDC/" + "swissbit/" + getClientId() + "/";
        }

        return null;

    }

    public static String getTopicToPublish(String id) {

        switch (id) {
            case TopicsConstants.DUMMY_PUBLISH_TOPIC:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + "CONF-V1/GET/configurations" ;

            case TopicsConstants.ZWAVE_GET:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.ZWAVE_GET;

            case TopicsConstants.ZWAVE_POST:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.ZWAVE_POST;

            case TopicsConstants.RASPBERRY_AUTH_PUB:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.RASPBERRY_AUTH_PUB;

            case TopicsConstants.SURVEILLANCE:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.SURVEILLANCE;

            case TopicsConstants.SWITCH_ON_PUB:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.SWITCH_ON_PUB;

            case TopicsConstants.SWITCH_OFF_PUB:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.SWITCH_OFF_PUB;

            case TopicsConstants.RETRIEVE_DEVICE_LIST_PUB:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.RETRIEVE_DEVICE_LIST_PUB;

            case TopicsConstants.RETRIEVE_DEVICE_STATUS_PUB:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.RETRIEVE_DEVICE_STATUS_PUB;
        }

        return null;
    }

    public static KuraPayload generatePayload (String extraBody, String requestId) {
        final KuraPayload payload = new KuraPayload();

        payload.addMetric("request.id", requestId);
        payload.addMetric("requester.client.id", getClientId());
        payload.setBody(extraBody.getBytes());
        return payload;
    }

}
