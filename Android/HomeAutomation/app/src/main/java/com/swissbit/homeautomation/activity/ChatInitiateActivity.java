/**
 * ****************************************************************************
 * Copyright (C) 2015 - Gaurav Kumar Srivastava
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package com.swissbit.homeautomation.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.swissbit.homeautomation.R;
import com.swissbit.homeautomation.utils.ActivityContexts;

public class ChatInitiateActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Save the context of DeviceActivity for later usage in other classes
        ActivityContexts.setChatActivityContext(this);

        Button initiate = (Button) findViewById(R.id.btnJoin);


        initiate.setOnClickListener
                (new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         // Redirect to chat activity on success.
                         Intent intent = new Intent(ChatInitiateActivity.this, SecureChat.class);
                         EditText name = (EditText) findViewById(R.id.name);
                         intent.putExtra("name", name.getText().toString());
                         startActivity(intent);
                     }
                 }

                );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handling back button press in the device activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityContexts.setCurrentActivityContext(ActivityContexts.getMainActivityContext());
    }

}
