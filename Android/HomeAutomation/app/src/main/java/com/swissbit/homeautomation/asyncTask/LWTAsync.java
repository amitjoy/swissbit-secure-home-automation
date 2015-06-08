package com.swissbit.homeautomation.asyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.android.swissbit.homeautomation.R;
import com.swissbit.homeautomation.activity.MainActivity;
import com.swissbit.homeautomation.utils.CustomAdapter;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

/**
 * Created by manit on 04/06/15.
 */
public class LWTAsync extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {

        final boolean status = MQTTFactory.getClient().isConnected();

        if (!status)
            MQTTFactory.getClient().connect();

        if (status)
            MQTTFactory.getClient().subscribe((String) MQTTFactory.getTopicToSubscribe(TopicsConstants.LWT), new MessageListener() {
                @Override
                public void processMessage(KuraPayload kuraPayload) {
                    if (kuraPayload != null) {
                        CustomAdapter.setImageStatus(R.drawable.btnoff);
                        Log.d("Kura HeartBeat", "Raspberry Dead...");
                    }
                }
            });
        return null;
    }


}
