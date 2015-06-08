package com.swissbit.homeautomation.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.android.swissbit.homeautomation.R;
import com.swissbit.homeautomation.activity.MainActivity;
import com.swissbit.homeautomation.utils.CustomAdapter;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

/**
 * Created by manit on 05/06/15.
 */
public class RaspberryHeartBeatAsync extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        final boolean status = MQTTFactory.getClient().isConnected();

        if (!status)
            MQTTFactory.getClient().connect();

        if (status)
            MQTTFactory.getClient().subscribe((String) MQTTFactory.getTopicToSubscribe(TopicsConstants.HEARTBEAT), new MessageListener() {
                @Override
                public void processMessage(KuraPayload kuraPayload) {
                    if (kuraPayload != null) {
//                        CustomAdapter customAdapter = null;
                        Log.d("Kura HeartBeat", "Raspberry Alive...");
                        publishProgress(null);
                    }
                }
            });
        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
//        CustomAdapter.setImageStatus(R.drawable.btnon);
//        MainActivity.customAdapter.notifyDataSetChanged();
    }
}
