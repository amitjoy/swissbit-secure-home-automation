package com.swissbit.homeautomation.ws;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.swissbit.homeautomation.utils.WSConstants;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by manit on 09/06/15.
 */
public class VerifyRaspberryPi {

    private AsyncHttpClient asyncHttpClient;
    private Context maincontext;
    private boolean valid;

    public boolean executeVerifyRaspberryPiWS(Context context, String rid) {
        this.maincontext = context;

        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(WSConstants.ADD_RPI_WS + "/" + rid, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("DEBUG SWISSRCHECK", "JSONObject");
                    valid = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.d("DEBUG SWISSRCHECK", "JSONObject");
                    valid = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    valid = false;
                    Log.d("DEBUG SWISS", "Invalid");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                try {
                    valid = false;
                    Log.d("DEBUG SWISS", "Invalid");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        return true;
    }
}

