package com.swissbit.homeautomation.asyncTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.swissbit.homeautomation.activity.MainActivity;
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
 * Created by manit on 11/06/15.
 */
public class AuthenticationAsync extends AsyncTask {


    private MainActivity mainActivity;

    private String rid;

    private EncryptionFactory encryptionFactory;

    boolean subResponse = false;

    private AsyncHttpClient asyncHttpClient;

    private AlertDialog alertDialog;

    private KuraPayload payload;

    private String dialogMessage;

    private Context mainActivityContext;

    private Object monitor;

    public AuthenticationAsync(Context context, final String rid) {
        this.mainActivity = (MainActivity)context;
        this.mainActivityContext = context;
        this.rid = rid;
        encryptionFactory = new EncryptionFactory();
    }

    public void showDialog(){
        alertDialog = new AlertDialog.Builder(mainActivityContext).create();
        alertDialog.setTitle("Information");
        alertDialog.setMessage(dialogMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancel(true);
                        mainActivity.checkRaspberryId(rid);
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        IKuraMQTTClient client = MQTTFactory.getClient();
        boolean status = false;

        monitor = new Object();

        if (!client.isConnected())
            status = client.connect();

        status = client.isConnected();

        String requestId = null;
        String topic = null;

        String[] topicData = MQTTFactory.getTopicToSubscribe(TopicsConstants.RASPBERRY_AUTH_SUB);
        requestId = topicData[1];
        topic = topicData[0];

        Log.d("Kura MQTT2", Boolean.toString(status));

        encryptionFactory = new EncryptionFactory();

        Log.d("Kura MQTTTopic", topic);

        if (status)
            client.subscribe(topic, new MessageListener() {
                @Override
                public void processMessage(KuraPayload kuraPayload) {
                    try {

                        String metricData = String.valueOf(kuraPayload.getMetric("data"));
                        if (metricData.isEmpty()) {
                            Log.d("Metrics......1", "" + kuraPayload.metrics());
                            subResponse = false;
                        } else {
                            Log.d("Metrics......2", "" + kuraPayload.metrics());
                            subResponse = true;
                        }
                        synchronized (monitor) {
                            monitor.notify();
                            Log.d("Notify1", "After");
                        }
                        Log.d("Inside onProcess", "" + subResponse);
                    } catch (Exception e) {
                        Log.e("Kura MQTT", e.getCause().getMessage());
                    }

                }
            });


        if (!status)
            MQTTFactory.getClient().connect();


        Log.d("EncryptAsyncFactory", "" + MQTTFactory.getClient());

        Log.d("EncryptAsyncFactory", "" + MQTTFactory.getTopicToPublish(TopicsConstants.RASPBERRY_AUTH_PUB));

        Log.d("EncryptAsyncFactory", "" + EncryptionFactory.getEncryptedString());

        payload = MQTTFactory.generatePayload(EncryptionFactory.getEncryptedString().replaceAll(" ", ""), requestId);
        if (status)
            MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.RASPBERRY_AUTH_PUB), payload);


        synchronized (monitor) {
            try {
                Log.d("Notify", "Before");
                monitor.wait();
                Log.d("Notify", "After");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    @Override
    protected void onCancelled() {
        Log.d("Async", "cancelled");

    }
    @Override
    protected void onPostExecute(Object o) {
        Log.d("Inside onPost", "" + subResponse);
        if (subResponse) {
            Log.d("Inside onPostWS", WSConstants.ADD_RPI_WS + MQTTFactory.getRaspberryPiById());
            asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.get(WSConstants.ADD_RPI_WS + MQTTFactory.getRaspberryPiById(), new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("DEBUG AUTHASYNC", "INSIDE SUCCESS");
                    Log.d("Main Activity", "" + mainActivity);
                    MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.SURVEILLANCE), payload);
                    Toast.makeText(ActivityContexts.getMainActivityContext(), "RaspberryPi Validated", Toast.LENGTH_LONG).show();
                    dialogMessage = "RaspberryPi Validated";
                    showDialog();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("DEBUG AUTHASYNC", "INSIDE FAILURE");
                    Toast.makeText(ActivityContexts.getMainActivityContext(), "RaspberryPi Registration Unsuccessful. Please try Again1", Toast.LENGTH_LONG).show();
                    dialogMessage = "RaspberryPi Registration Unsuccessful. Please try Again";
                    showDialog();
                }
            });

        } else {
            Toast.makeText(ActivityContexts.getMainActivityContext(), "RaspberryPi Registration Unsuccessful. Please try Again3", Toast.LENGTH_LONG).show();
            dialogMessage = "RaspberryPi Registration Unsuccessful. Please try Again";
            showDialog();
        }
        cancel(true);
    }
}
