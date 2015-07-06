package com.swissbit.homeautomation.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.swissbit.homeautomation.R;
import com.google.common.collect.Lists;
import com.swissbit.homeautomation.asyncTask.DeviceStatusRefreshAsync;
import com.swissbit.homeautomation.asyncTask.RetrieveDeviceListAsync;
import com.swissbit.homeautomation.db.ApplicationDb;
import com.swissbit.homeautomation.model.Device;
import com.swissbit.homeautomation.ui.adapter.DeviceAdapter;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;

import java.util.List;

/**
 * Created by manit on 16/06/15.
 */
public class DeviceActivity extends ActionBarActivity {

    private ListView deviceListView;

    private DeviceAdapter adapter;

    private List<Device> listOfDevice;

    private Device device;

    private Bundle extras;

    private String raspberryId;

    private ApplicationDb applicationDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        //Save the context of DeviceActivity for later usage in other classes
        ActivityContexts.setDeviceActivityContext(this);
        applicationDb = DBFactory.getDevicesInfoDbAdapter(this);

        deviceListView = (ListView) findViewById(R.id.listDevice);

        extras = getIntent().getExtras();
        Log.d("Device Activity",""+ extras);
        raspberryId = extras.getString("RaspberryId");

        MQTTFactory.setSecureElementId(extras.getString("SecureElementId"));

        Log.d("Device Activity", raspberryId);
        Log.d("Device Activity", ""+extras.getString("SecureElementId"));

        getDeviceList();


    }

    public void getDeviceList(){
        if(applicationDb.getDevice() == null){
            RetrieveDeviceListAsync retrieveDeviceListAsync = new RetrieveDeviceListAsync(raspberryId);
            retrieveDeviceListAsync.execute();
        }
        else{
            DeviceStatusRefreshAsync deviceStatusRefreshAsync = new DeviceStatusRefreshAsync();
            deviceStatusRefreshAsync.execute();
        }

    }


    public void addToListView() {
        Log.d("Adding List view","Added");
        device = applicationDb.getDevice();
        listOfDevice = Lists.newArrayList(device);

        adapter = new DeviceAdapter(getApplicationContext(), listOfDevice);

        deviceListView.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityContexts.setCurrentActivityContext(ActivityContexts.getMainActivityContext());
        Log.d("AppContext",""+ActivityContexts.getMainActivityContext());
    }
}



