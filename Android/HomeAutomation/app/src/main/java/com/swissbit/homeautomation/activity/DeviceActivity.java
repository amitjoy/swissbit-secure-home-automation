package com.swissbit.homeautomation.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.swissbit.homeautomation.R;
import com.google.common.collect.Lists;
import com.swissbit.homeautomation.asyncTask.DeviceCmdAsync;
import com.swissbit.homeautomation.model.Device;
import com.swissbit.homeautomation.ui.adapter.DeviceAdapter;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.TopicsConstants;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);
        //Save the context of MainActivity for later usage in other classes
        ActivityContexts.setMainActivityContext(this);
        client = MQTTFactory.getClient();

        deviceListView = (ListView) findViewById(R.id.listDevice);
        Bundle extras = getIntent().getExtras();
//        Toast.makeText(getApplicationContext(), extras.getString("RaspberryId"), Toast.LENGTH_LONG).show();
        Log.d("Device Activity", extras.getString("RaspberryId"));
        addToListView();
    }

    public void addToListView() {
        device = Device.createDevice("123", "B8:27:EB:BE:3F:BF", "Device1", "Device1", false);
        listOfDevice = Lists.newArrayList(device);

        adapter = new DeviceAdapter(getApplicationContext(), listOfDevice);
        deviceListView.setAdapter(adapter);

//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View customView = layoutInflater.inflate(R.layout.row_device_details, adapter.getViewGroup(), false);
//        ImageButton btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);
//        final Switch socketSwitch = (Switch) adapter.getView(0,null,adapter.getViewGroup()).findViewById(R.id.socketSwitch);



    }


}



