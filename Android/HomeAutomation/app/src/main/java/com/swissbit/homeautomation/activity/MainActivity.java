/**
 * ****************************************************************************
 * Copyright (C) 2015 - Manit Kumar <vikky_manit@yahoo.co.in>
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */

package com.swissbit.homeautomation.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.swissbit.homeautomation.asyncTask.AuthenticationAsync;
import com.swissbit.homeautomation.asyncTask.MqttSubscribeAsync;
import com.android.swissbit.homeautomation.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.model.RaspberryPi;
import com.swissbit.homeautomation.ui.adapter.RPiAdapter;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.ui.dialog.SecureCodeDialog;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.homeautomation.ws.VerifyRaspberryPi;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.adapter.MessageListener;
import com.swissbit.mqtt.client.message.KuraPayload;

import junit.framework.Assert;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    private Button btnSecureCode;

    private Button btnPublish;

    private EditText txtPublish;

    private MqttSubscribeAsync mqttSubscribeAsync;

    private ListView listView;

    private RaspberryPi raspberryPi;

    private DevicesInfoDbAdapter devicesInfoDbAdapter;

    private RPiAdapter adapter;

    private List<RaspberryPi> listOfRPi;

    private IKuraMQTTClient client = null;

    private AsyncTask rPiHeartBeatAsync = new AsyncTask() {

        @Override
        protected Object doInBackground(Object[] params) {
            client = MQTTFactory.getClient();
            boolean status = client.connect();
            Log.d("Kura MQTT Connect ", Boolean.toString(status));

            if (!status)
                status = client.connect();

            Log.d("Kura MQTT Connect ", Boolean.toString(status));
            if (status) {
                client.subscribe((String) MQTTFactory.getTopicToSubscribe(TopicsConstants.HEARTBEAT), new MessageListener() {
                    @Override
                    public void processMessage(KuraPayload kuraPayload) {
                        if (kuraPayload != null) {
                            Log.d("Kura HeartBeat", "Raspberry Alive...");
                            publishProgress();
                        }
                    }
                });
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Object[] values){
            final ImageView imageView = (ImageView)findViewById(R.id.imgStatus);
            imageView.setImageResource(R.drawable.btnon);
        }
    };


    private AsyncTask lwtAsync = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] params) {
            final boolean status = client.isConnected();

            if (!status)
                client.connect();

            if (status)
                client.subscribe((String) MQTTFactory.getTopicToSubscribe(TopicsConstants.LWT), new MessageListener() {
                    @Override
                    public void processMessage(KuraPayload kuraPayload) {
                        if (kuraPayload != null) {
                            Log.d("Kura HeartBeat", "Raspberry Dead...");
                            publishProgress();
                        }
                    }
                });
            return null;
        }
        @Override
        public void onProgressUpdate(Object[] values){
            Toast.makeText(getApplicationContext(), "Server Connection Lost", Toast.LENGTH_LONG).show();
            final ImageView imageView = (ImageView)findViewById(R.id.imgStatus);
            imageView.setImageResource(R.drawable.btnoff);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Save the context of MainActivity for later usage in other classes
        ActivityContexts.setMainActivityContext(this);

        devicesInfoDbAdapter = DBFactory.getDevicesInfoDbAdapter(this);

        getSecureCode();
        Log.d("Check", "1");


        addToListView();

//        mqttSubscribeAsync = new MqttSubscribeAsync();
//        mqttSubscribeAsync.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.register_server) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getSecureCode() {
        if(devicesInfoDbAdapter.checkSecretCodeDialogShow() == 0) {
            SecureCodeDialog secureCodeDialog = new SecureCodeDialog();
            secureCodeDialog.getSecureCode(this);
        }
    }


    public void askForMQTTCredential(View view) {
        //TODO
        final boolean status = MQTTFactory.getClient().isConnected();

        Log.d("Kura MQTT ......", Boolean.toString(status));

        if (!status)
            MQTTFactory.getClient().connect();

        if (status)
            MQTTFactory.getClient().publish(MQTTFactory.getTopicToPublish(TopicsConstants.DUMMY_PUBLISH_TOPIC), MQTTFactory.generatePayload("ASD", "5323523532"));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents()!=null) {
            String rid = scanResult.getContents();
            Toast.makeText(getApplicationContext(), rid, Toast.LENGTH_LONG).show();

            AuthenticationAsync authenticationAsync = new AuthenticationAsync();
            authenticationAsync.execute();
            authenticationAsync.cancel(true);
            VerifyRaspberryPi verifyRaspberryPi = new VerifyRaspberryPi();
            if(verifyRaspberryPi.executeVerifyRaspberryPiWS(this,rid)){
                checkRaspberryId(rid);
            }
            else{
                Toast.makeText(getApplicationContext(), "Invalid Server", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void checkRaspberryId(String rid) {
        if (DBFactory.checkRaspberryPiInDB(rid, this)) {
            DBFactory.addRaspberryPi(rid);
            addToListView();
        } else
            Toast.makeText(getApplicationContext(), "Duplicate Server", Toast.LENGTH_LONG).show();
    }

    public void addToListView() {

            raspberryPi = DBFactory.getRaspberrys();
            if(raspberryPi != null) {
                listOfRPi = Lists.newArrayList(raspberryPi);
                adapter = new RPiAdapter(getApplicationContext(), listOfRPi);
                listView = (ListView) findViewById(R.id.listRaspberryPi);
                listView.setAdapter(adapter);

                rPiHeartBeatAsync.execute();

                lwtAsync.execute();
            }


    }

}
