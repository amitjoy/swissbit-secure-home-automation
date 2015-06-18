package com.swissbit.homeautomation.utils;

import android.content.Context;

/**
 * Created by manit on 11/06/15.
 */
public class ActivityContexts {

    private static Context mainActivityContext;

    private static Context encryptCommandActivityContext;

    private static Context deviceActivity;

    public static Context getEncryptCommandActivityContext() {
        return encryptCommandActivityContext;
    }

    public static void setEncryptCommandActivityContext(Context encryptCommandActivityContext) {
        ActivityContexts.encryptCommandActivityContext = encryptCommandActivityContext;
    }

    public static Context getMainActivityContext() {
        return mainActivityContext;
    }

    public static void setMainActivityContext(Context mainActivityContext) {
        ActivityContexts.mainActivityContext = mainActivityContext;
    }

    public static Context getDeviceActivity() {
        return deviceActivity;
    }

    public static void setDeviceActivity(Context deviceActivity) {
        ActivityContexts.deviceActivity = deviceActivity;
    }

}
