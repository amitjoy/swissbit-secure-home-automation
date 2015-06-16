package com.swissbit.homeautomation.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.KuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

/**
 * Created by manit on 04/06/15.
 */
public class MqttSubscribeAsync extends AsyncTask {

    private IKuraMQTTClient client = null;
    private Context m_context;

    public MqttSubscribeAsync(Context context) {
        m_context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        final boolean status = client.connect();
        String requestId = null;
        String topic = null;
        final Object topicData = MQTTFactory.getTopicToSubscribe(TopicsConstants.DUMMY_PUBLISH_TOPIC);
        Log.d("Kura MQTT2", Boolean.toString(status));

        if (topicData instanceof Object[]) {
            topic = (String)(((Object[])topicData)[0]);
            requestId = (String)(((Object[])topicData)[1]);
        }


        if (status)
            client.subscribe(topic, new MessageListener() {
                @Override
                public void processMessage(KuraPayload kuraPayload) {
                    try {
                        Log.d("Kura MSG", kuraPayload.getBody().toString());
                    } catch (Exception e) {
                        Log.e("Kura MQTT", e.getCause().getMessage());
                    }

                }
            });
        return null;
    }
}
