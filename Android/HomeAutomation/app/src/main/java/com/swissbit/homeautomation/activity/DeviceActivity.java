package com.swissbit.homeautomation.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.swissbit.homeautomation.R;
import com.google.common.collect.Lists;
import com.swissbit.homeautomation.asyncTask.DeviceStatusRefreshAsync;
import com.swissbit.homeautomation.asyncTask.RetrieveDeviceListAsync;
import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.model.Device;
import com.swissbit.homeautomation.ui.adapter.DeviceAdapter;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.DBFactory;

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

    private DevicesInfoDbAdapter devicesInfoDbAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        //Save the context of DeviceActivity for later usage in other classes
        ActivityContexts.setDeviceActivityContext(this);
        devicesInfoDbAdapter = DBFactory.getDevicesInfoDbAdapter(this);

        deviceListView = (ListView) findViewById(R.id.listDevice);
        extras = getIntent().getExtras();
        raspberryId = extras.getString("RaspberryId");



//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                getDeviceList();
//            }
//        }, 7000);
        getDeviceList();
        Log.d("Device Activity", raspberryId);

    }

    public void getDeviceList(){
        if(devicesInfoDbAdapter.getDevice() == null){
            RetrieveDeviceListAsync retrieveDeviceListAsync = new RetrieveDeviceListAsync(raspberryId);
            retrieveDeviceListAsync.execute();
        }
        else{
            DeviceStatusRefreshAsync deviceStatusRefreshAsync = new DeviceStatusRefreshAsync();
            deviceStatusRefreshAsync.execute();
        }
//            addToListView();

    }


    public void addToListView() {
        Log.d("Adding List view","Added");
        device = devicesInfoDbAdapter.getDevice();
        listOfDevice = Lists.newArrayList(device);

        adapter = new DeviceAdapter(getApplicationContext(), listOfDevice);

        deviceListView.setAdapter(adapter);


    }


}



