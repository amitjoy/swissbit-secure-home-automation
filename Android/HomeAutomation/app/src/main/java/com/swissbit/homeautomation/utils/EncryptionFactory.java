package com.swissbit.homeautomation.utils;

/**
 * Created by manit on 11/06/15.
 */
public final class EncryptionFactory {

    public static byte[] getEncryptedString() {
        return encryptedString;
    }

    public static void setEncryptedString(byte[] encryptedStringTmp) {
        encryptedString = encryptedStringTmp;
    }

    private static byte[] encryptedString;
}
