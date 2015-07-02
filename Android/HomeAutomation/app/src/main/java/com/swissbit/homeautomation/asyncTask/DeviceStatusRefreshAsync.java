package com.swissbit.homeautomation.asyncTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.swissbit.homeautomation.activity.DeviceActivity;
import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.model.Device;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;
import com.tum.ssdapi.CardAPI;

/**
 * Created by manit on 21/06/15.
 */
public class DeviceStatusRefreshAsync extends AsyncTask {

    boolean subscriptionResponse = false;

    private KuraPayload payload;

    private String requestId;

    private Object refreshMonitor;

    private int deviceNodeId;

    private boolean deviceStatus;

    private DevicesInfoDbAdapter devicesInfoDbAdapter;

    private Switch socketSwitch;

    private ImageView imageDevice;

    private ProgressDialog progressDialog;

    private Device device;

    private CardAPI secureElementAccess;

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(ActivityContexts.getDeviceActivityContext(), "Retrieving Device Status",
                "Please Wait", true);
        devicesInfoDbAdapter = DBFactory.getDevicesInfoDbAdapter(ActivityContexts.getDeviceActivityContext());
        device = devicesInfoDbAdapter.getDevice();
        deviceNodeId = device.getDeviceNodeId();
        secureElementAccess = new CardAPI(ActivityContexts.getMainActivityContext());
//        Log.d("SE",MQTTFactory.getSecureElementId());
    }

    @Override
    protected Object doInBackground(Object[] params) {

        IKuraMQTTClient client = MQTTFactory.getClient();
        boolean status = false;

        if (!client.isConnected())
            status = client.connect();

        status = client.isConnected();

        String topic = null;

        refreshMonitor = new Object();

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
                        deviceStatus = (boolean) kuraPayload.getMetric("status");
                        if(deviceStatus == true){
                            devicesInfoDbAdapter.updateDeviceStatus("true", deviceNodeId);
                        }
                        else{
                            devicesInfoDbAdapter.updateDeviceStatus("false", deviceNodeId);
                        }
                        publishProgress();

                        Log.d("DeviceStatus", ""+deviceStatus);
                        Log.d("Metrics", ""+kuraPayload.metrics());

                        if (status == 200) {
                            Log.d("Response", "success");
                            subscriptionResponse = true;
                        } else {
                            Log.d("Response", "Failed");
                            subscriptionResponse = false;
                        }
                        synchronized (refreshMonitor) {
                            refreshMonitor.notify();
                            Log.d("Notify1", "After");
                        }
                        Log.d("Inside onProcess", "" + subscriptionResponse);
                    } catch (Exception e) {
                        Log.e("Kura MQTT Exception", e.getCause().getMessage());
                    }

                }
            });

        Log.d("secureId",MQTTFactory.getSecureElementId());

        payload = MQTTFactory.generatePayload("", requestId);
        String encryptedDeviceNodeId = secureElementAccess.encryptMsgWithID(MQTTFactory.getSecureElementId(),Integer.toString(deviceNodeId));

        payload.addMetric("nodeId", deviceNodeId);
        payload.addMetric("encVal", encryptedDeviceNodeId);

        MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.RETRIEVE_DEVICE_STATUS_PUB), payload);


        Log.d("NodeId", "" + deviceNodeId);
        Log.d("statustopic",MQTTFactory.getTopicToPublish(TopicsConstants.RETRIEVE_DEVICE_STATUS_PUB));

        synchronized (refreshMonitor) {
            try {
                Log.d("Notify", "Before");
                refreshMonitor.wait(10000);
                Log.d("Notify", "After");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
//        if(subscriptionResponse) {
            DeviceActivity deviceActivity = (DeviceActivity) ActivityContexts.getDeviceActivityContext();
            deviceActivity.addToListView();
//        }

        Log.d("Inside onPost", "" + subscriptionResponse);
        if (!subscriptionResponse){
            progressDialog.dismiss();
            Toast.makeText(ActivityContexts.getMainActivityContext(), "Device Command Failed! Try again", Toast.LENGTH_LONG).show();
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