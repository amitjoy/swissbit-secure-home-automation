package com.swissbit.homeautomation.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
public class LWTAsync extends AsyncTask {

    private ImageView imgStatus;

    @Override
    protected Object doInBackground(Object[] params) {
        IKuraMQTTClient client = MQTTFactory.getClient();
        boolean status = false;

        if (!client.isConnected())
            status = client.connect();

        status = client.isConnected();

        Log.d("Kura MQTT Connect LWT", Boolean.toString(status));

        if (status) {
            Log.d("LWTTOPIC", MQTTFactory.getTopicToSubscribe(TopicsConstants.LWT)[0]);
            client.subscribe(MQTTFactory.getTopicToSubscribe(TopicsConstants.LWT)[0], new MessageListener() {
                @Override
                public void processMessage(KuraPayload kuraPayload) {
                    if (kuraPayload != null) {
                        Log.d("Kura HeartBeat", "Raspberry Dead...");
                        publishProgress();
                    }
                }
            });
        }
        return null;

    }

    @Override
    public void onProgressUpdate(Object[] values) {
        Toast.makeText(ActivityContexts.getCurrentActivityContext(), "Server Connection Lost", Toast.LENGTH_LONG).show();
        View rootView = ((Activity) ActivityContexts.getMainActivityContext()).getWindow().getDecorView().findViewById(android.R.id.content);
        imgStatus = (ImageView) rootView.findViewById(R.id.imgStatus);
        imgStatus.setImageResource(R.drawable.btnoff);
    }
}
