package tum.com.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {

    static byte[] message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * This part of the code is added to call the SSDAPI apk which will
         * do the necessary task based on the Function code passed
         * MSG parameter is used to pass a message. In case of status this parameter is not required
         * For encrypt this would be a String Message
         * For decrypt this would be Byte Array
         */
        Button encryptButton = (Button) findViewById(R.id.encrypt);
        encryptButton.setOnClickListener
                (new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                          EditText plainMsg = (EditText) findViewById(R.id.encryptText);
                          Intent intent = new Intent("tum.com.ssdapi.MAIN_ACTIVITY");
                          Bundle bundle = new Bundle();
                          bundle.putInt("Function", 1006);
                          bundle.putString("MSG", plainMsg.getText().toString());
                          intent.putExtras(bundle);
                          /*
                           * 6403 is REQUEST_CODE. This is just a number to uniquely identify a request.
                           * The Function id is used to define the task, like encrypt or decrypt
                           */
                           startActivityForResult(intent, 6403);
                        }
                                         }
        );

         /*
         * This part of the code is added to call the SSDAPI apk which will
         * do the necessary task based on the Function code passed
         * MSG parameter is used to pass a message. In case of status this parameter is not required
         * For encrypt this would be a String Message
         * For decrypt this would be Byte Array
         */
        Button decryptButton = (Button) findViewById(R.id.decrypt);
        decryptButton.setOnClickListener
                (new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         EditText encryptMsg = (EditText) findViewById(R.id.decryptText);
                         Intent intent = new Intent("tum.com.ssdapi.MAIN_ACTIVITY");
                         Bundle bundle = new Bundle();
                         bundle.putInt("Function", 1007);
                         bundle.putByteArray("MSG", message);
                         intent.putExtras(bundle);
                       /*
                        * 6404 is REQUEST_CODE. This is just a number to uniquely identify a request.
                        * The Function id is used to define the task, like encrypt or decrypt
                        */
                         startActivityForResult(intent, 6404);
                     }
                 }
        );
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent pData)
    {
        if ( requestCode == 6403 )
        {
            if (resultCode == Activity.RESULT_OK )
            {
                final byte[] zData = pData.getExtras().getByteArray("Response");

                EditText resp = (EditText) findViewById(R.id.decryptText);
                resp.setText("" + zData);

                message = zData;
            }
        }

        if ( requestCode == 6404 )
        {
            if (resultCode == Activity.RESULT_OK )
            {
                final String zData = pData.getExtras().get("Response").toString();

                EditText resp = (EditText) findViewById(R.id.decryptText);
                resp.setText(zData);
            }
        }
    }
}
