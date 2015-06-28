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
import com.tum.ssdapi.CardAPI;

/**
 * Created by manit on 17/06/15.
 */
public class DeviceCmdAsync extends AsyncTask {

    boolean subscriptionResponse = false;

    private KuraPayload payload;

    private String cmd;

    private String requestId;

    private Object monitor;

    private int deviceNodeId;

    private ImageView imageDevice;

    private DevicesInfoDbAdapter devicesInfoDbAdapter;

    private ProgressDialog progressDialog;

    private String raspberryId;

    private CardAPI secureElementAccess;

    public DeviceCmdAsync(String cmd, int deviceNodeId, String raspberryId) {
        this.cmd = cmd;
        this.deviceNodeId = deviceNodeId;
        this.raspberryId = raspberryId;
        devicesInfoDbAdapter = DBFactory.getDevicesInfoDbAdapter(ActivityContexts.getDeviceActivityContext());
        secureElementAccess = new CardAPI(ActivityContexts.getMainActivityContext());
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
                            subscriptionResponse = true;
                            if (cmd.equals("on"))
                                devicesInfoDbAdapter.updateDeviceStatus("true", deviceNodeId);
                            else
                                devicesInfoDbAdapter.updateDeviceStatus("false", deviceNodeId);
                        } else {
                            Log.d("Response", "Failed");
                            subscriptionResponse = false;

                        }
                        synchronized (monitor) {
                            monitor.notify();
                            Log.d("Notify1", "After");
                        }

                        Log.d("Inside onProcess", "" + subscriptionResponse);
                    } catch (Exception e) {
                        Log.e("Kura MQTT Exception", e.getCause().getMessage());
                    }

                }
            });

        payload = MQTTFactory.generatePayload("", requestId);

        String encryptedDeviceNodeId = secureElementAccess.encryptMsgWithID(MQTTFactory.getSecureElementId(),Integer.toString(deviceNodeId));
//        payload.addMetric("nodeId", deviceNodeId);
        payload.addMetric("nodeId", encryptedDeviceNodeId);

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
                monitor.wait(15000);
                Log.d("Notify", "After");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        publishProgress();



        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        Log.d("Inside onPost", "" + subscriptionResponse);
        if (!subscriptionResponse) {
            Toast.makeText(ActivityContexts.getDeviceActivityContext(), "Device Command Failed! Try again", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        Log.d("Inside onCancelled", "" + subscriptionResponse);
    }

    @Override
    public void onProgressUpdate(Object[] values) {
        Log.d("Onprogess", "reached");
        progressDialog.dismiss();

        if(subscriptionResponse) {
            View rootView = ((Activity) ActivityContexts.getDeviceActivityContext()).getWindow().getDecorView().findViewById(android.R.id.content);
            imageDevice = (ImageView) rootView.findViewById(R.id.imgDevice);
            if (cmd.equals("on")) {
                imageDevice.setImageResource(R.drawable.socketswitchon);
            } else {
                imageDevice.setImageResource(R.drawable.socketswitchoff);
            }
        }
    }
}
