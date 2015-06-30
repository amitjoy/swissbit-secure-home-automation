package com.swissbit.homeautomation.utils;

import android.content.Context;

/**
 * Created by manit on 11/06/15.
 */
public class ActivityContexts {

    private static Context mainActivityContext;

    private static Context deviceActivityContext;

    private static Context currentActivityContext;

    public static Context getCurrentActivityContext() {
        return currentActivityContext;
    }

    public static void setCurrentActivityContext(Context currentActivityContext) {
        ActivityContexts.currentActivityContext = currentActivityContext;
    }

    public static Context getMainActivityContext() {
        return mainActivityContext;
    }

    public static void setMainActivityContext(Context mainActivityContext) {
        ActivityContexts.mainActivityContext = mainActivityContext;
        setCurrentActivityContext(mainActivityContext);
    }

    public static Context getDeviceActivityContext() {
        return deviceActivityContext;
    }

    public static void setDeviceActivityContext(Context deviceActivityContext) {
        ActivityContexts.deviceActivityContext = deviceActivityContext;
        setCurrentActivityContext(deviceActivityContext);
    }

}
