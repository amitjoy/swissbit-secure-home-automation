package com.swissbit.homeautomation.utils;

import android.content.Context;
import android.util.Log;

import com.swissbit.homeautomation.db.ApplicationDb;
import com.swissbit.homeautomation.model.RaspberryPi;

/**
 * Created by manit on 04/06/15.
 */
public final class DBFactory {

    private static ApplicationDb applicationDb;

    public static ApplicationDb getDevicesInfoDbAdapter(Context context) {
        if (applicationDb == null)
            applicationDb = new ApplicationDb(context);

        return applicationDb;
    }

    public static void addRaspberryPi(String rid, String secureElementId){
        long id = applicationDb.insertRaspberry(rid, secureElementId, "Raspberry", rid);
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

    public static RaspberryPi getRaspberry(){
        return applicationDb.getRaspberry();
    }
}
