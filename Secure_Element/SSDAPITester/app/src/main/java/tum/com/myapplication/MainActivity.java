package tum.com.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.app.Activity;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent pData)
    {
        if ( requestCode == 6403 )
        {
            if (resultCode == Activity.RESULT_OK )
            {
                final String zData = pData.getExtras().get("Response").toString();

                Toast msg1 = Toast.makeText(this, zData, Toast.LENGTH_LONG );
                msg1.show();

            }
        }

    }
}
