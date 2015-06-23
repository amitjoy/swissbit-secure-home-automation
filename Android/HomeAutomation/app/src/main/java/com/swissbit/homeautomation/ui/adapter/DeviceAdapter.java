package com.swissbit.homeautomation.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.swissbit.homeautomation.R;
import com.swissbit.homeautomation.asyncTask.DeviceCmdAsync;
import com.swissbit.homeautomation.asyncTask.DeviceStatusRefreshAsync;
import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.model.Device;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.mqtt.client.IKuraMQTTClient;
import com.swissbit.mqtt.client.message.KuraPayload;

import java.util.List;

/**
 * Created by manit on 17/06/15.
 */

public class DeviceAdapter extends ArrayAdapter<Device> {

    private List<Device> deviceInfo;
    private Device device;

    private View customView;
    private Switch socketSwitch;
    private KuraPayload payload;

    private IKuraMQTTClient client;

    private ImageView imageDevice;

    private DevicesInfoDbAdapter devicesInfoDbAdapter;

    public DeviceAdapter(Context context, List<Device> deviceInfo) {
        super(context, R.layout.row_device_details, deviceInfo);
        this.deviceInfo = deviceInfo;
        device = deviceInfo.get(0);
        payload = new KuraPayload();
        client = MQTTFactory.getClient();
        devicesInfoDbAdapter = DBFactory.getDevicesInfoDbAdapter(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        customView = layoutInflater.inflate(R.layout.row_device_details, parent, false);


        imageDevice = (ImageView) customView.findViewById(R.id.imgDevice);
        socketSwitch = (Switch) customView.findViewById(R.id.socketSwitch);
        TextView switchStatus = (TextView) customView.findViewById(R.id.refreshStatus);
        ImageButton btnRefresh = (ImageButton) customView.findViewById(R.id.btnRefresh);



        if (device.getStatus().equals("true")){
            socketSwitch.setChecked(true);
            imageDevice.setImageResource(R.drawable.socketswitchon);
        }
        else{
            socketSwitch.setChecked(false);
            imageDevice.setImageResource(R.drawable.socketswitchoff);
        }


        socketSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Inside listner", "1");
                if (isChecked) {
                    Log.d("Task Started..", "1");
                    DeviceCmdAsync deviceCmdAsync = new DeviceCmdAsync("on", device.getId());
                    deviceCmdAsync.execute();
                    Log.d("Task done..", "1");
                } else {
                    Log.d("Task Started..", "1");
                    DeviceCmdAsync deviceCmdAsync = new DeviceCmdAsync("off", device.getId());
                    deviceCmdAsync.execute();
                    Log.d("Task done..", "1");
                }

            }
        });

//        btnRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.d("socketswitch1", "" + socketSwitch);
////                DeviceStatusRefreshAsync deviceStatusRefreshAsync = new DeviceStatusRefreshAsync(8, socketSwitch,imageDevice);
////                deviceStatusRefreshAsync.execute();
//
////                if ("true".equals(devicesInfoDbAdapter.getDevice().getStatus()))
////                    socketSwitch.setChecked(false);
////                else
////                    socketSwitch.setChecked(false);
//
//                imageDevice.setImageResource(R.drawable.socketswitchoff);
//            }
//        });

        return customView;
    }


}
