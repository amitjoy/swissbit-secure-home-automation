package com.swissbit.homeautomation.utils;

/**
 * Created by manit on 11/06/15.
 */
public final class EncryptionFactory {

    public static String getEncryptedString() {
        return encryptedString;
    }

    public static void setEncryptedString(String encryptedStringTmp) {
        encryptedString = encryptedStringTmp;
    }

    private static String encryptedString;
}
