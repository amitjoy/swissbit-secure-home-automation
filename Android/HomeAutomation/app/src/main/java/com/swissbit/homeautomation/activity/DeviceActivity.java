package com.swissbit.homeautomation.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.swissbit.homeautomation.R;
import com.google.common.collect.Lists;
import com.swissbit.homeautomation.asyncTask.DeviceCmdAsync;
import com.swissbit.homeautomation.asyncTask.RetrieveDeviceListAsync;
import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.model.Device;
import com.swissbit.homeautomation.ui.adapter.DeviceAdapter;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.message.KuraPayload;

import java.util.List;

/**
 * Created by manit on 16/06/15.
 */
public class DeviceActivity extends ActionBarActivity {

    private ListView deviceListView;

    private DeviceAdapter adapter;

    private List<Device> listOfDevice;

    private Device device;

    private KuraPayload payload;

    private IKuraMQTTClient client;

    private DeviceCmdAsync deviceCmdAsync;

    private Bundle extras;

    private String raspberryId;

    private DevicesInfoDbAdapter devicesInfoDbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);
        //Save the context of DeviceActivity for later usage in other classes
        ActivityContexts.setDeviceActivity(this);
        client = MQTTFactory.getClient();
        devicesInfoDbAdapter = DBFactory.getDevicesInfoDbAdapter(this);

        deviceListView = (ListView) findViewById(R.id.listDevice);
        extras = getIntent().getExtras();
        raspberryId = extras.getString("RaspberryId");

        Log.d("Device Activity", raspberryId);
        getDeviceList();

    }

    public void getDeviceList(){
        if(devicesInfoDbAdapter.getDevice() == null){
            RetrieveDeviceListAsync retrieveDeviceListAsync = new RetrieveDeviceListAsync(raspberryId);
            retrieveDeviceListAsync.execute();
        }
        else
            addToListView();

    }


    public void addToListView() {
        Log.d("Adding List view","Added");
        device = devicesInfoDbAdapter.getDevice();
        listOfDevice = Lists.newArrayList(device);

        adapter = new DeviceAdapter(getApplicationContext(), listOfDevice);
        deviceListView.setAdapter(adapter);

    }


}



