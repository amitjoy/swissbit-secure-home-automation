package com.swissbit.homeautomation.asyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

/**
 * Created by manit on 18/06/15.
 */
public class RetrieveDeviceListAsync extends AsyncTask {

    public RetrieveDeviceListAsync(String raspberryId){
        this.raspberryId = raspberryId;
    }

    boolean subResponse = false;

    private KuraPayload payload;

    private String cmd;

    private String requestId;

    private int nodeId;

    private DevicesInfoDbAdapter devicesInfoDbAdapter;

    private String raspberryId;

    private Thread subscribeThread;

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Object doInBackground(Object[] params) {

                IKuraMQTTClient client = MQTTFactory.getClient();
                devicesInfoDbAdapter = DBFactory.getDevicesInfoDbAdapter(ActivityContexts.getDeviceActivity());
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
                                    nodeId = Integer.valueOf((String) kuraPayload.getMetric("node.id_0"));
                                    Log.d("HaspMap",""+nodeId);
                                    Log.d("Device", ""+devicesInfoDbAdapter.checkDeviceById(nodeId));
                                    if(!devicesInfoDbAdapter.checkDeviceById(nodeId)){
                                        Log.d("Device", "Inserted");
                                        devicesInfoDbAdapter.insertDevice(nodeId,raspberryId,null,null,"false");
                                        Log.d("Device", "Inserted");
                                    }
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

        payload = MQTTFactory.generatePayload("", requestId);

        MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.RETRIEVE_DEVICE_LIST_PUB), payload);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        cancel(true);
        Log.d("Inside onPost", "" + subResponse);
        if (!subResponse)
            Toast.makeText(ActivityContexts.getMainActivityContext(), "Failed to retrieve device list! Please Try again", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCancelled() {
        Log.d("Inside onCancelled", "" + subResponse);
        if (!subResponse)
            Toast.makeText(ActivityContexts.getMainActivityContext(), "Failed to retrieve device list! Please Try again", Toast.LENGTH_LONG).show();

    }
}
