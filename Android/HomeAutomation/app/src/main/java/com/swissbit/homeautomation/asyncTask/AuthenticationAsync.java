package com.swissbit.homeautomation.asyncTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.swissbit.homeautomation.activity.EncryptCommandActivity;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.EncryptionFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

/**
 * Created by manit on 11/06/15.
 */
public class AuthenticationAsync extends AsyncTask {

    private static IKuraMQTTClient client = MQTTFactory.getClient();

    EncryptionFactory encryptionFactory = new EncryptionFactory();

    @Override
    protected Object doInBackground(Object[] params) {
        final boolean status = MQTTFactory.getClient().connect();
        String requestId = null;
        String topic = null;
        final Object topicData = MQTTFactory.getTopicToSubscribe(TopicsConstants.RASPBERRY_AUTH_SUB);
        Log.d("Kura MQTT2", Boolean.toString(status));

        if (status)
            client.subscribe(topic, new MessageListener() {
                @Override
                public void processMessage(KuraPayload kuraPayload) {
                    try {
                        Log.d("Kura MSG", String.valueOf(kuraPayload.getMetric("data")));
                    } catch (Exception e) {
                        Log.e("Kura MQTT", e.getCause().getMessage());
                    }

                }
            });


        Intent intent = new Intent(ActivityContexts.getMainActivityContext(), EncryptCommandActivity.class);
        ActivityContexts.getMainActivityContext().startActivity(intent);

        if (!status)
            MQTTFactory.getClient().connect();

        if (status)
            MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.RASPBERRY_AUTH_PUB), MQTTFactory.generatePayload(encryptionFactory.getEncryptedString(), "5323523532"));


        return null;

    }
}
