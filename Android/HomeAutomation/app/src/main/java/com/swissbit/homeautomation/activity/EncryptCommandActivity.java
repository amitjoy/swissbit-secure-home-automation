package com.swissbit.homeautomation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.swissbit.homeautomation.R;

/**
 * Created by manit on 05/06/15.
 */
public class EncryptCommandActivity extends Activity{

    private String encryptedString;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encrpyt_command_activity);

        Intent intent = new Intent("tum.com.ssdapi.MAIN_ACTIVITY");

        Bundle bundle = new Bundle();

        bundle.putInt("Function", 1006);
        bundle.putString("MSG", "ABC");
        intent.putExtras(bundle);

        startActivityForResult(intent, 6403);
        finish();

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
        Log.d("Encrypted Data", encryptedString);
    }

}
