package com.swissbit.homeautomation.asyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.swissbit.homeautomation.R;
import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

/**
 * Created by manit on 17/06/15.
 */
public class DeviceCmdAsync extends AsyncTask {

    boolean subResponse = false;

    private KuraPayload payload;

    private String cmd;

    private String requestId;

    private Object monitor;

    private int nodeId;

    private ImageView imageDevice;

    private DevicesInfoDbAdapter devicesInfoDbAdapter;

    private ProgressDialog progressDialog;

    public DeviceCmdAsync(String cmd, int nodeId) {
        this.cmd = cmd;
        this.nodeId = nodeId;
        devicesInfoDbAdapter = DBFactory.getDevicesInfoDbAdapter(ActivityContexts.getDeviceActivityContext());
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(ActivityContexts.getDeviceActivityContext(), "Executing Command",
          "Please Wait", true);
    }

    @Override
    protected Object doInBackground(Object[] params) {

        IKuraMQTTClient client = MQTTFactory.getClient();
        boolean status = false;

        if (!client.isConnected())
            status = client.connect();

        status = client.isConnected();

        String topic = null;

        monitor = new Object();

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
                            if (cmd.equals("on"))
                                devicesInfoDbAdapter.updateDeviceStatus("true", nodeId);
                            else
                                devicesInfoDbAdapter.updateDeviceStatus("false", nodeId);
                        } else {
                            Log.d("Response", "Failed");
                            subResponse = false;

                        }
                        synchronized (monitor) {
                            monitor.notify();
                            Log.d("Notify1", "After");
                        }

                        Log.d("Inside onProcess", "" + subResponse);
                    } catch (Exception e) {
                        Log.e("Kura MQTT Exception", e.getCause().getMessage());
                    }

                }
            });

        payload = MQTTFactory.generatePayload("", requestId);
        payload.addMetric("nodeId", nodeId);

        if (cmd.equals("on")) {
            MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.SWITCH_ON_PUB), payload);
            Log.d("Switch", "onpresses" + MQTTFactory.getTopicToPublish(TopicsConstants.SWITCH_ON_PUB));
        } else if (cmd.equals("off")) {
            MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.SWITCH_OFF_PUB), payload);
            Log.d("Switch", "offpresses");
        }

        synchronized (monitor) {
            try {
                Log.d("Notify", "Before");
                monitor.wait();
                Log.d("Notify", "After");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        publishProgress();

        cancel(true);

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        Log.d("Inside onPost", "" + subResponse);
        if (!subResponse)
            Toast.makeText(ActivityContexts.getDeviceActivityContext(), "Device Command Failed! Try again", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCancelled() {
        Log.d("Inside onCancelled", "" + subResponse);
        if (!subResponse)
            Toast.makeText(ActivityContexts.getDeviceActivityContext(), "Device Command Failed! Try again", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProgressUpdate(Object[] values) {
        Log.d("Onprogess", "reached");
        progressDialog.dismiss();

        View rootView = ((Activity) ActivityContexts.getDeviceActivityContext()).getWindow().getDecorView().findViewById(android.R.id.content);
        imageDevice = (ImageView) rootView.findViewById(R.id.imgDevice);
        if (cmd.equals("on")) {
            imageDevice.setImageResource(R.drawable.socketswitchon);
        } else {
            imageDevice.setImageResource(R.drawable.socketswitchoff);
        }
    }
}
