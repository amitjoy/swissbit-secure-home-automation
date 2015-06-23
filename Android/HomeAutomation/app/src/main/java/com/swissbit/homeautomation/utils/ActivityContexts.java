package com.swissbit.homeautomation.utils;

import android.content.Context;

/**
 * Created by manit on 11/06/15.
 */
public class ActivityContexts {

    private static Context mainActivityContext;

    private static Context deviceActivity;

    public static Context getMainActivityContext() {
        return mainActivityContext;
    }

    public static void setMainActivityContext(Context mainActivityContext) {
        ActivityContexts.mainActivityContext = mainActivityContext;
    }

    public static Context getDeviceActivityContext() {
        return deviceActivity;
    }

    public static void setDeviceActivityContext(Context deviceActivity) {
        ActivityContexts.deviceActivity = deviceActivity;
    }

}
