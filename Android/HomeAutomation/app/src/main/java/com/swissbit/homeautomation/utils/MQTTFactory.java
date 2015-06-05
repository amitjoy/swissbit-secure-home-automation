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

    private static IKuraMQTTClient iKuraMQTTClient;

    public static String generateClientId() {
        if (clientId == null)
            clientId = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
        Log.d("Kura MQTT",clientId);
        return clientId;
    }

    public static IKuraMQTTClient getClient() {
        if (iKuraMQTTClient == null)
            iKuraMQTTClient = new KuraMQTTClient.Builder()
                    .setHost("m20.cloudmqtt.com").setPort("13273")
                    .setClientId(generateClientId()).setUsername(getUsername())
                    .setPassword(getPassword()).build();

        return iKuraMQTTClient;
    }

    public static String getUsername() {
        //TODO
        return "user@email.com";
    }

    public static String getPassword() {
        //TODO
        return "tEev-Aiv-H";
    }

    public static String getRaspberryPiById(int id) {
        //TO-DO
        return "B8:27:EB:BE:3F:BF";
    }

    public static Object getTopicToSubscribe(String id) {
        final String requestId = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
        switch (id) {
            case TopicsConstants.LWT:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.LWT;
            //TODO
            case TopicsConstants.HEARTBEAT:
                return getMQTTTopicPrefix(TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.HEARTBEAT + "mqtt/heartbeat";

            case TopicsConstants.ZWAVE_STATUS:
                return new Object[] { getMQTTTopicPrefix(TopicsConstants.TOPIC_SUBSCRIBE) + TopicsConstants.ZWAVE_STATUS + requestId, requestId };

            case TopicsConstants.DUMMY_PUBLISH_TOPIC:
                return new Object[] { getMQTTTopicPrefix(TopicsConstants.TOPIC_SUBSCRIBE) + TopicsConstants.DUMMY_PUBLISH_TOPIC + requestId, requestId };
        }

        return null;
    }

    public static String getMQTTTopicPrefix(String type) {
        //TO-DO
        switch (type) {
            case TopicsConstants.TOPIC_PUBLISH:
                return "$EDC/" + "swissbit/" + getRaspberryPiById(1) + "/";

            case TopicsConstants.TOPIC_SUBSCRIBE:
                return "$EDC/" + "swissbit/" + generateClientId() + "/";
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
        }

        return null;
    }

    public static KuraPayload generatePayload (String extraBody, String requestId) {
        final KuraPayload payload = new KuraPayload();

        payload.addMetric("request.id", requestId);
        payload.addMetric("requester.client.id", generateClientId());
        payload.setBody(extraBody.getBytes());
        return payload;
    }

}
