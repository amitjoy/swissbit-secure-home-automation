package com.swissbit.homeautomation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.android.swissbit.homeautomation.R;
import com.swissbit.homeautomation.asyncTask.AuthenticationAsync;
import com.swissbit.homeautomation.utils.ActivityContexts;
import com.swissbit.homeautomation.utils.EncryptionFactory;
import com.swissbit.homeautomation.utils.MQTTFactory;

/**
 * Created by manit on 05/06/15.
 */
public class EncryptCommandActivity extends Activity {

    private MainActivity mainActivity;

    private Intent intent;

    public EncryptCommandActivity() {
        this.mainActivity = (MainActivity) ActivityContexts.getMainActivityContext();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encrpyt_command_activity);
        ActivityContexts.setEncryptCommandActivityContext(this);

        intent = new Intent("tum.com.ssdapi.MAIN_ACTIVITY");

        Bundle bundle = new Bundle();

        bundle.putInt("Function", 1006);
        bundle.putString("MSG", "AB");
        intent.putExtras(bundle);

        startActivityForResult(intent, 0);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent pData) {
        super.onActivityResult(requestCode, resultCode, pData);

        if (requestCode == 0) {
//            Toast.makeText(this, "encrypt", Toast.LENGTH_LONG).show();
            EncryptionFactory.setEncryptedString(pData.getExtras().get("Response").toString());
            Log.d("Encrypted Data", EncryptionFactory.getEncryptedString());

            AuthenticationAsync authenticationAsync = new AuthenticationAsync(this, mainActivity, MQTTFactory.getRaspberryPiById());
            Log.d("Main EncryptCmd", "" + mainActivity);
            authenticationAsync.execute();

//            authenticationAsync.cancel(true);

        }

    }
}
