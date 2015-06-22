package com.swissbit.homeautomation.asyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.swissbit.homeautomation.model.Device;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.EncryptionFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.homeautomation.utils.WSConstants;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

import org.apache.http.Header;

/**
 * Created by manit on 17/06/15.
 */
public class DeviceCmdAsync extends AsyncTask {

    boolean subResponse = false;

    private KuraPayload payload;

    private String cmd;

    private String requestId;

    public DeviceCmdAsync(String cmd) {
        this.cmd = cmd;
    }

    @Override
    protected void onPreExecute() {
        IKuraMQTTClient client = MQTTFactory.getClient();
        boolean status = false;

        if (!client.isConnected())
            status = client.connect();

        status = client.isConnected();

        String topic = null;

        String[] topicData = MQTTFactory.getTopicToSubscribe(TopicsConstants.SWITCH_ON_OFF_LIST_STATUS_SUB);
        requestId = topicData[1];
        Log.d("RequestID", requestId);
        topic = topicData[0];

        if (status)
            client.subscribe(topic, new MessageListener() {
                @Override
                public void processMessage(KuraPayload kuraPayload) {
                    try {
                        int status = (int) kuraPayload.getMetric("response.code");

                        if (status == 200) {
                            Log.d("Response", "success");
                            subResponse = true;
                        } else {
                            Log.d("Response", "Failed");
                            subResponse = false;
                        }
                        Log.d("Inside onProcess", "" + subResponse);
                    } catch (Exception e) {
                        Log.e("Kura MQTT Exception", e.getCause().getMessage());
                    }

                }
            });
    }

    @Override
    protected Object doInBackground(Object[] params) {

        payload = MQTTFactory.generatePayload("", requestId);
        payload.addMetric("nodeId", 8);

        if (cmd.equals("on")) {
            MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.SWITCH_ON_PUB), payload);
            Log.d("Switch", "onpresses" + MQTTFactory.getTopicToPublish(TopicsConstants.SWITCH_ON_PUB));
        } else if (cmd.equals("off")) {
            MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.SWITCH_OFF_PUB), payload);
            Log.d("Switch", "offpresses");
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cancel(true);

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        Log.d("Inside onPost", "" + subResponse);
        if (!subResponse)
            Toast.makeText(ActivityContexts.getMainActivityContext(), "Device Command Failed! Try again", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCancelled() {
        Log.d("Inside onCancelled", "" + subResponse);
        if (!subResponse)
            Toast.makeText(ActivityContexts.getMainActivityContext(), "Device Command Failed! Try again", Toast.LENGTH_LONG).show();

    }
}
