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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.swissbit.homeautomation.asyncTask.LWTAsync;
import com.swissbit.homeautomation.asyncTask.MqttSubscribeAsync;
import com.android.swissbit.homeautomation.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.swissbit.homeautomation.asyncTask.RaspberryHeartBeatAsync;
import com.swissbit.homeautomation.utils.CustomAdapter;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
import com.swissbit.homeautomation.utils.WSConstants;
import com.swissbit.mqtt.client.message.KuraPayload;
import com.loopj.android.http.*;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {


    private Button btnSecureCode;

    private Button btnPublish;

    private EditText txtPublish;

    private AlertDialog.Builder dialogBuilder;

    private MqttSubscribeAsync mqttSubscribeAsync;

    private LWTAsync LWTAsync;

    private RaspberryHeartBeatAsync raspberryHeartBeatAsync;

    private ListView listView;

    private AsyncHttpClient asyncHttpClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSecureCode = (Button) findViewById(R.id.btnSecureCode);
        btnPublish = (Button) findViewById(R.id.btnPublish);
        txtPublish = (EditText) findViewById(R.id.txtPublish);

        listView = (ListView) findViewById(R.id.listRaspberryPi);

        mqttSubscribeAsync = new MqttSubscribeAsync();
        mqttSubscribeAsync.execute();

        LWTAsync = new LWTAsync();
        LWTAsync.execute();

        raspberryHeartBeatAsync = new RaspberryHeartBeatAsync();
        raspberryHeartBeatAsync.execute();

        asyncHttpClient = new AsyncHttpClient();

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

    public void getSecureCode(View view) {
        dialogBuilder = new AlertDialog.Builder(this);
        final EditText txtCode = new EditText(this);

        dialogBuilder.setTitle("Enter the Secure Code");
        dialogBuilder.setMessage("Enter the Secure Code");
        dialogBuilder.setView(txtCode);
        dialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String secretCode = txtCode.getText().toString();
                executeCredentialWS();
                Log.d("DEBUG SWISS", "INSIDE 1");
            }
        });


        AlertDialog dialogSecretCode = dialogBuilder.create();
        dialogSecretCode.show();
    }

    private void executeCredentialWS() {
        Log.d("DEBUG SWISS", "INSIDE 2");
        asyncHttpClient.get(WSConstants.CREDENTIAL_WS, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("fgggfgfggfgf", "here"+response.getString("username"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG SWISS", "INSIDE 3");
                JSONObject username = null;
                JSONObject password = null;
                try {
                    username = (JSONObject) response.get(0);
                    password = (JSONObject) response.get(1);
                    Log.d("WSCred", username.getString("username"));
                    Log.d("WSCred", password.getString("password"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG SWISS", "INSIDE 4"+throwable.getCause());
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG SWISS", "INSIDE 5");
            }
        });
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
        if (scanResult != null) {
            String rid = scanResult.getContents();
            Toast.makeText(getApplicationContext(), rid, Toast.LENGTH_LONG).show();
            checkRaspberryId(rid);
        }
    }

    public void checkRaspberryId(String rid) {
        if (DBFactory.checkRaspberryPiInDB(rid, this)) {
            DBFactory.addRaspberryPi(rid);
            addToListView(rid);
        } else
            Toast.makeText(getApplicationContext(), "Duplicate Server", Toast.LENGTH_LONG).show();
    }

    private void addToListView(String rId) {
        if (rId != null) {
            ListAdapter listAdapter = new CustomAdapter(this, new String[]{rId});
            listView.setAdapter(listAdapter);
        }

    }

}
