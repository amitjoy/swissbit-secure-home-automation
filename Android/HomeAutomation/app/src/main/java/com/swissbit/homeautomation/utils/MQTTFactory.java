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
    private static Context s_context;

    private static IKuraMQTTClient iKuraMQTTClient;

    public static String generateClientId(Context context) {
        s_context = context;
        if (clientId == null)
            clientId = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
        Log.d("Kura MQTT",clientId);
        return clientId;
    }

    public static IKuraMQTTClient getClient(Context context) {
        s_context = context;
        if (iKuraMQTTClient == null)
            iKuraMQTTClient = new KuraMQTTClient.Builder()
                    .setHost("m20.cloudmqtt.com").setPort("13273")
                    .setClientId(generateClientId(context)).setUsername(getUsername(context))
                    .setPassword(getPassword(context)).build();

        return iKuraMQTTClient;
    }

    public static String getUsername(Context context) {
        s_context = context;
        return DBFactory.getDevicesInfoDbAdapter(context).getCredentials()[0];
    }

    public static String getPassword(Context context) {
        s_context = context;
        return DBFactory.getDevicesInfoDbAdapter(context).getCredentials()[1];
    }

    public static String getRaspberryPiById(Context context, int id) {
        s_context = context;
//        return DBFactory.getDevicesInfoDbAdapter(context).getRaspberryId();
        return "B8:27:EB:BE:3F:BF";
    }

    public static Object getTopicToSubscribe(Context context, String id) {
        s_context = context;
        final String requestId = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
        switch (id) {
            case TopicsConstants.LWT:
                return getMQTTTopicPrefix(context, TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.LWT;
            //TODO
            case TopicsConstants.HEARTBEAT:
                return getMQTTTopicPrefix(context, TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.HEARTBEAT + "mqtt/heartbeat";

            case TopicsConstants.ZWAVE_STATUS:
                return new Object[] { getMQTTTopicPrefix(context, TopicsConstants.TOPIC_SUBSCRIBE) + TopicsConstants.ZWAVE_STATUS + requestId, requestId };

            case TopicsConstants.DUMMY_PUBLISH_TOPIC:
                return new Object[] { getMQTTTopicPrefix(context, TopicsConstants.TOPIC_SUBSCRIBE) + TopicsConstants.DUMMY_PUBLISH_TOPIC + requestId, requestId };
        }

        return null;
    }

    public static String getMQTTTopicPrefix(Context context, String type) {
        s_context = context;
        //TO-DO
        switch (type) {
            case TopicsConstants.TOPIC_PUBLISH:
                return "$EDC/" + "swissbit/" + getRaspberryPiById(context,1) + "/";

            case TopicsConstants.TOPIC_SUBSCRIBE:
                return "$EDC/" + "swissbit/" + generateClientId(context) + "/";
        }

        return null;

    }

    public static String getTopicToPublish(Context context, String id) {
        s_context = context;
        switch (id) {
            case TopicsConstants.DUMMY_PUBLISH_TOPIC:
                return getMQTTTopicPrefix(context, TopicsConstants.TOPIC_PUBLISH) + "CONF-V1/GET/configurations" ;

            case TopicsConstants.ZWAVE_GET:
                return getMQTTTopicPrefix(context, TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.ZWAVE_GET;

            case TopicsConstants.ZWAVE_POST:
                return getMQTTTopicPrefix(context, TopicsConstants.TOPIC_PUBLISH) + TopicsConstants.ZWAVE_POST;
        }

        return null;
    }

    public static KuraPayload generatePayload (Context context, String extraBody, String requestId) {
        s_context = context;
        final KuraPayload payload = new KuraPayload();

        payload.addMetric("request.id", requestId);
        payload.addMetric("requester.client.id", generateClientId(context));
        payload.setBody(extraBody.getBytes());
        return payload;
    }

}
