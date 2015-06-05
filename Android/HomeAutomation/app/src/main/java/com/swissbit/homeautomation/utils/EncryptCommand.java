package com.swissbit.homeautomation.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

/**
 * Created by manit on 05/06/15.
 */
public class EncryptCommand extends Activity {

    private String encryptedString;

    /*
     * This part of the code is added to call the SSDAPI apk which will
     * do the necessary task based on the Function code passed
     * MSG parameter is used to pass a message. In case of status this parameter is not required
     * For encrypt this would be a String Message
     * For decrypt this would be Byte Array
     */

    public String encrpytData(){
        Intent intent = new Intent("tum.com.ssdapi.MAIN_ACTIVITY");
        Bundle bundle = new Bundle();
        bundle.putInt("Function", 1006);
        bundle.putString("MSG", "ABC");
        intent.putExtras(bundle);

    /*
     * 6403 is REQUEST_CODE. This is just a number to uniquely identify a request.
     * The Function id is used to define the task, like encrypt or decrypt
     */
        startActivityForResult(intent, 6403);

        return encryptedString;

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent pData)
    {
        if ( requestCode == 6403 )
        {
            if (resultCode == Activity.RESULT_OK )
            {
                encryptedString = pData.getExtras().get("Response").toString();
            }
        }

    }

}
