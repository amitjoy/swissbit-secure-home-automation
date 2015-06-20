package com.swissbit.homeautomation.utils;

import android.content.Context;
import android.util.Log;

import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.model.RaspberryPi;

/**
 * Created by manit on 04/06/15.
 */
public final class DBFactory {

    private static DevicesInfoDbAdapter devicesInfoDbAdapter;

    public static DevicesInfoDbAdapter getDevicesInfoDbAdapter(Context context) {
        if (devicesInfoDbAdapter == null)
            devicesInfoDbAdapter = new DevicesInfoDbAdapter(context);

        return devicesInfoDbAdapter;
    }

    public static void addRaspberryPi(String rid){
        long id = devicesInfoDbAdapter.insertRaspberry(rid, "Raspberry", rid);
        if(id<0){
            Log.d("Insert", "Failed to insert");
        }
        else {
            Log.d("Insert", "Successful insert");
        }

    }

    public static boolean checkRaspberryPiInDB(String rid, Context context) {
        return getDevicesInfoDbAdapter(context).checkRaspberryId(rid);
    }

    public static RaspberryPi getRaspberrys(){
        return devicesInfoDbAdapter.getRaspberry();
    }
}
