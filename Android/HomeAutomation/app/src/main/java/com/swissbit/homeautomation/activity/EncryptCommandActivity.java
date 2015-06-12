package com.swissbit.homeautomation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.swissbit.homeautomation.R;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.EncryptionFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;

/**
 * Created by manit on 05/06/15.
 */
public class EncryptCommandActivity extends Activity{

    EncryptionFactory encryptionFactory = new EncryptionFactory();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encrpyt_command_activity);
        ActivityContexts.setEncryptCommandActivityContext(this);

        Intent intent = new Intent("tum.com.ssdapi.MAIN_ACTIVITY");

        Bundle bundle = new Bundle();

        bundle.putInt("Function", 1006);
        bundle.putString("MSG", MQTTFactory.getRaspberryPiById(1));
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
                encryptionFactory.setEncryptedString(pData.getExtras().get("Response").toString());
            }
        }
        Log.d("Encrypted Data", encryptionFactory.getEncryptedString());
    }

}
