package com.swissbit.homeautomation.asyncTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.swissbit.homeautomation.activity.DeviceActivity;
import com.swissbit.homeautomation.db.ApplicationDb;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;
import com.tum.ssdapi.CardAPI;

/**
 * Created by manit on 18/06/15.
 */
public class RetrieveDeviceListAsync extends AsyncTask {

    public RetrieveDeviceListAsync(String raspberryId) {
        this.raspberryId = raspberryId;
    }

    boolean subscriptionResponse = false;

    private KuraPayload payload;

    private String cmd;

    private String requestId;

    private int deviceNodeId;

    private ApplicationDb applicationDb;

    private String raspberryId;

    private Object monitor;

    private ProgressDialog progressDialog;

    private CardAPI secureElementAccess;

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(ActivityContexts.getDeviceActivityContext(), "Retrieving Device List",
                "Please Wait", true);
        secureElementAccess = new CardAPI(ActivityContexts.getDeviceActivityContext());
    }

    @Override
    protected Object doInBackground(Object[] params) {
        final IKuraMQTTClient client = MQTTFactory.getClient();
        applicationDb = DBFactory.getDevicesInfoDbAdapter(ActivityContexts.getDeviceActivityContext());
        boolean status = false;

        monitor = new Object();

        if (!client.isConnected())
            status = client.connect();

        status = client.isConnected();

        String[] topicData = MQTTFactory.getTopicToSubscribe(TopicsConstants.SWITCH_ON_OFF_LIST_STATUS_SUB);
        requestId = topicData[1];
        Log.d("RequestID", requestId);
        final String topic = topicData[0];
        if (status) {

            client.subscribe(topic, new MessageListener() {
                @Override
                public void processMessage(KuraPayload kuraPayload) {
                    try {
                        Log.d("Inside", "subscribe");
                        int status = (int) kuraPayload.getMetric("response.code");

                        if (status == 200) {
                            Log.d("Response", "success");
                            deviceNodeId = Integer.valueOf((String) kuraPayload.getMetric("node.id_0"));
                            Log.d("HaspMap", "" + deviceNodeId);
                            Log.d("Device", "" + applicationDb.checkDeviceById(deviceNodeId));
                            if (!applicationDb.checkDeviceById(deviceNodeId)) {
                                Log.d("Device", "Inserted");
                                applicationDb.insertDevice(deviceNodeId, raspberryId, null, null, "false");
                                Log.d("Device", "Inserted");
                            }
                            publishProgress();
                            Log.d("Notify1", "Before");
                            synchronized (monitor) {
                                monitor.notify();
                                Log.d("Notify1", "After");
                            }
                            subscriptionResponse = true;

                        } else {
                            Log.d("Response", "Failed");
                            subscriptionResponse = false;
                        }
                        Log.d("Inside onProcess", "" + subscriptionResponse);
                    } catch (Exception e) {
                        Log.e("Kura MQTT Exception", e.getCause().getMessage());
                    }

                }
            });


            payload = MQTTFactory.generatePayload("", requestId);
            String encryptedString = secureElementAccess.encryptMsgWithID(MQTTFactory.getSecureElementId(),"Raspberry");
            payload.addMetric("encVal", encryptedString);
            Log.d("Encrypted", encryptedString);
            MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.RETRIEVE_DEVICE_LIST_PUB), payload);
            Log.d("Topic Published", MQTTFactory.getTopicToPublish(TopicsConstants.RETRIEVE_DEVICE_LIST_PUB));
        }


        synchronized (monitor) {
            try {
                Log.d("Notify", "Before");
                monitor.wait(20000);
                Log.d("Notify", "After");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(subscriptionResponse) {
            DeviceActivity deviceActivity = (DeviceActivity) ActivityContexts.getDeviceActivityContext();
            deviceActivity.addToListView();
        }
        Log.d("Inside onPost", "" + subscriptionResponse);
        if (!subscriptionResponse){
            progressDialog.dismiss();
            Toast.makeText(ActivityContexts.getMainActivityContext(), "Failed to retrieve device list! Please Try again", Toast.LENGTH_LONG).show();
        }
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        Log.d("Inside onCancelled", "" + subscriptionResponse);
    }

    @Override
    public void onProgressUpdate(Object[] values) {
        progressDialog.dismiss();
        Log.d("Onprogess", "reached");

    }
}
