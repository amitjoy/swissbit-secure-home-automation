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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.swissbit.homeautomation.asyncTask.AuthenticationAsync;
import com.android.swissbit.homeautomation.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.swissbit.homeautomation.asyncTask.HeartBeatAsync;
import com.swissbit.homeautomation.asyncTask.LWTAsync;
import com.swissbit.homeautomation.asyncTask.PermissionRevocationAsync;
import com.swissbit.homeautomation.db.ApplicationDb;
import com.swissbit.homeautomation.model.RaspberryPi;
import com.swissbit.homeautomation.ui.adapter.RPiAdapter;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.EncryptionFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.ui.dialog.SecureCodeDialog;
import com.tum.ssdapi.CardAPI;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    private ListView listView;

    private RaspberryPi raspberryPi;

    private ApplicationDb applicationDb;

    private RPiAdapter adapter;

    private List<RaspberryPi> listOfRPi;

    private CardAPI secureElementAccess;

    private String secureElementId;

    private PermissionRevocationAsync permissionRevocationAsync;

    private HeartBeatAsync heartBeatAsync;

    private LWTAsync lwtAsync;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Save the context of MainActivity for later usage in other classes
        ActivityContexts.setMainActivityContext(this);

        applicationDb = DBFactory.getDevicesInfoDbAdapter(this);
        listView = (ListView) findViewById(R.id.listRaspberryPi);
        listView.setEmptyView(findViewById(R.id.empty_list_item));

        secureElementAccess = new CardAPI(getApplicationContext());

        permissionRevocationAsync = new PermissionRevocationAsync(this);

        heartBeatAsync = new HeartBeatAsync();

        lwtAsync = new LWTAsync();

        checkAccessRevoked();

        Log.d("1:", "" + secureElementAccess.getMyId());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.register_raspberry) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Caution!");
            alertDialog.setMessage("Please make sure your RaspberryPi was turned on atleast 1 minute ago");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                            integrator.initiateScan();
                        }
                    });
            alertDialog.show();

        }

        if (id == R.id.reset_data) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Caution!");
            alertDialog.setMessage("Are you sure you want to reset all data? Application will be restarted in this case ");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            applicationDb.resetData();
                            dialog.dismiss();
                            finish();
                            System.exit(0);
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            alertDialog.show();

        }

        return super.onOptionsItemSelected(item);
    }

    public void getSecureCode() {
        if (applicationDb.checkSecretCodeDialogShow() == 0) {
            SecureCodeDialog secureCodeDialog = new SecureCodeDialog();
            secureCodeDialog.getSecureCode(this);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            String qrCode = scanResult.getContents();
            String qrCodeArray[] = qrCode.split("#");
            secureElementId = qrCodeArray[0];
            String rid = qrCodeArray[1];
            Log.d("Rid", rid);
            Log.d("Sid", secureElementId);

            MQTTFactory.setRaspberryId(rid);
            MQTTFactory.setSecureElementId(secureElementId);

            Toast.makeText(getApplicationContext(), rid, Toast.LENGTH_SHORT).show();

            Log.d("Secureelement", "" + secureElementAccess.isCardPresent());

            if (secureElementAccess.isCardPresent()) {
                String encryptedString = secureElementAccess.encryptMsgWithID(secureElementId, rid);
                EncryptionFactory.setEncryptedString(encryptedString);
                Log.d("Encrypted Data", EncryptionFactory.getEncryptedString());
                AuthenticationAsync authenticationAsync = new AuthenticationAsync(this, MQTTFactory.getRaspberryPiById());
                authenticationAsync.execute();
            } else {
                Toast.makeText(getApplicationContext(), "Secure SD card not present", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkRaspberryId(String rid) {

        if (DBFactory.checkRaspberryPiInDB(rid, this)) {
            DBFactory.addRaspberryPi(rid, secureElementId);
            addToListView();
        } else
            Toast.makeText(getApplicationContext(), "Duplicate Server", Toast.LENGTH_LONG).show();
    }

    public void addToListView() {

        raspberryPi = DBFactory.getRaspberry();

        if (raspberryPi != null) {
            MQTTFactory.setRaspberryId(raspberryPi.getId());
            listOfRPi = Lists.newArrayList(raspberryPi);
            adapter = new RPiAdapter(getApplicationContext(), listOfRPi);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    RaspberryPi clickedItem = (RaspberryPi) listView.getItemAtPosition(position);

                    Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("RaspberryId", clickedItem.getId());
                    extras.putString("SecureElementId", raspberryPi.getSecureElementId());
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });

            heartBeatAsync.execute();

            lwtAsync.execute();

            permissionRevocationAsync.execute();
        }

    }

    public void checkAccessRevoked(){

        Log.d("SDEnabled",secureElementAccess.getEnabled().toString());
        if("029000".equals(secureElementAccess.getEnabled().toString())) {
            Log.d("Inside Alert","Alert");
            Context context = MainActivity.this;
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Warning!");
            alertDialog.setMessage("Your Access has been revoked. Application will no longer function");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                            System.exit(0);
                        }
                    });
            alertDialog.show();
        }
        else {

            getSecureCode();

            addToListView();
        }
    }

}
