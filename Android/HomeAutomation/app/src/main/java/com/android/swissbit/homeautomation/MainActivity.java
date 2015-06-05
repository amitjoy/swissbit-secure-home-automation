package com.android.swissbit.homeautomation;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends ActionBarActivity {

    DevicesInfoDbAdapter devicesInfoDbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devicesInfoDbAdapter = new DevicesInfoDbAdapter(this);

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
        if (id == R.id.register_server) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String rid = scanResult.getContents();
            Toast.makeText(getApplicationContext(),rid,Toast.LENGTH_LONG).show();
            addRaspberry(rid);
        }
    }

    public void addRaspberry(String rid){
        long id = devicesInfoDbAdapter.insertData(rid,"Raspberry1","Respberry1");
        if(id<0){
            Log.d("Insert","Failed to insert");
        }
        else {
            Log.d("Insert", "Successful insert");
        }
    }
}
