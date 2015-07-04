package com.swissbit.homeautomation.asyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.swissbit.homeautomation.R;
import com.loopj.android.http.AsyncHttpClient;
import com.swissbit.homeautomation.activity.MainActivity;
import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.EncryptionFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;
import com.tum.ssdapi.CardAPI;

/**
 * Created by manit on 29/06/15.
 */
public class PermissionRevocationAsync extends AsyncTask {

    private MainActivity mainActivity;

    private String rid;

    private EncryptionFactory encryptionFactory;

    boolean subscriptionResponse = false;

    private AsyncHttpClient asyncHttpClient;

    private AlertDialog alertDialog;

    private KuraPayload payload;

    private String dialogMessage;

    private Context mainActivityContext;

    private Object monitor;

    private ProgressDialog progressDialog;

    private CardAPI secureElementAccess;

    public PermissionRevocationAsync(Context context) {
        this.mainActivity = (MainActivity)context;
        this.mainActivityContext = context;
        encryptionFactory = new EncryptionFactory();
        secureElementAccess = new CardAPI(ActivityContexts.getMainActivityContext());
    }
    @Override
    protected Object doInBackground(Object[] params) {
        boolean status = false;
        IKuraMQTTClient client = MQTTFactory.getClient();
        Log.d("Kura MQTT Client ", "" + client);

        if (!client.isConnected()) {
            status = client.connect();
        } else
            Log.d("Kura MQTT Connected AR", Boolean.toString(client.isConnected()));

        status = client.isConnected();

        Log.d("Kura MQTT Connect AR", Boolean.toString(status));
        if (status) {
            Log.d("AccessRevoke", MQTTFactory.getTopicToSubscribe(TopicsConstants.ACCESS_REVOCATION_SUB)[0]);
            client.subscribe(MQTTFactory.getTopicToSubscribe(TopicsConstants.ACCESS_REVOCATION_SUB)[0], new MessageListener() {
                @Override
                public void processMessage(KuraPayload kuraPayload) {
                    if (kuraPayload != null) {
                        Log.d("AccessRevoked", "Revoked");
                        DevicesInfoDbAdapter devicesInfoDbAdapter = DBFactory.getDevicesInfoDbAdapter(ActivityContexts.getCurrentActivityContext());
                        devicesInfoDbAdapter.resetData();

                        secureElementAccess.setDisabled();
                        publishProgress();
                    }
                }
            });
        }

        return null;
    }

    @Override
    public void onProgressUpdate(Object[] values) {

        DevicesInfoDbAdapter db = DBFactory.getDevicesInfoDbAdapter(ActivityContexts.getCurrentActivityContext());
        db.resetData();

        AlertDialog alertDialog = new AlertDialog.Builder(ActivityContexts.getCurrentActivityContext()).create();
        alertDialog.setTitle("Warning!");
        alertDialog.setMessage("Your Access has been revoked. Application will no longer function");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Activity currentActivity=(Activity)ActivityContexts.getCurrentActivityContext();
                        currentActivity.finish();
                        System.exit(0);
                    }
                });
        alertDialog.show();


    }
}
