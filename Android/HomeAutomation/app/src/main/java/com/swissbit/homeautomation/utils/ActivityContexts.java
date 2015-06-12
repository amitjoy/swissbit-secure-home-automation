package com.swissbit.homeautomation.utils;

import android.content.Context;

import com.swissbit.homeautomation.activity.MainActivity;

/**
 * Created by manit on 11/06/15.
 */
public class ActivityContexts {

    private static Context MainActivityContext;

    private static Context EncryptCommandActivityContext;

    public static Context getEncryptCommandActivityContext() {
        return EncryptCommandActivityContext;
    }

    public static void setEncryptCommandActivityContext(Context encryptCommandActivityContext) {
        EncryptCommandActivityContext = encryptCommandActivityContext;
    }

    public static Context getMainActivityContext() {
        return MainActivityContext;
    }

    public static void setMainActivityContext(Context mainActivityContext) {
        MainActivityContext = mainActivityContext;
    }

}
