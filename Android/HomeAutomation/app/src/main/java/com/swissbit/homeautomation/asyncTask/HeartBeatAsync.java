package com.swissbit.homeautomation.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.swissbit.homeautomation.R;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

/**
 * Created by manit on 05/07/15.
 */
public class HeartBeatAsync extends AsyncTask {

    private ImageView imgStatus;

    @Override
    protected Object doInBackground(Object[] params) {
        boolean status = false;
        IKuraMQTTClient client = MQTTFactory.getClient();
        Log.d("Kura MQTT Client ", "" + client);

        if (!client.isConnected()) {
            status = client.connect();
        } else
            Log.d("Kura MQTT Connected HB", Boolean.toString(client.isConnected()));

        status = client.isConnected();

        Log.d("Kura MQTT Connect HB", Boolean.toString(status));
        if (status) {
            Log.d("HEARTBEATTOPIC", MQTTFactory.getTopicToSubscribe(TopicsConstants.HEARTBEAT)[0]);
            client.subscribe(MQTTFactory.getTopicToSubscribe(TopicsConstants.HEARTBEAT)[0], new MessageListener() {
                @Override
                public void processMessage(KuraPayload kuraPayload) {
                    if (kuraPayload != null) {
                        Log.d("Kura HeartBeat", "Raspberry Alive...");
                        publishProgress();
                    }
                }
            });
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object[] values) {
        View rootView = ((Activity) ActivityContexts.getMainActivityContext()).getWindow().getDecorView().findViewById(android.R.id.content);
        imgStatus = (ImageView) rootView.findViewById(R.id.imgStatus);
        imgStatus.setImageResource(R.drawable.btnon);
    }
}
