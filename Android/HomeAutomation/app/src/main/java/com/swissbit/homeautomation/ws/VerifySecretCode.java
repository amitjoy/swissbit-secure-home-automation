package com.swissbit.homeautomation.ws;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.ui.dialog.SecureCodeDialog;
import com.swissbit.homeautomation.utils.MQTTFactory;
import com.swissbit.homeautomation.utils.WSConstants;
import com.swissbit.mqtt.client.IKuraMQTTClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by manit on 09/06/15.
 */
public class VerifySecretCode {

    private AsyncHttpClient asyncHttpClient;
    private SecureCodeDialog secureCodeDialog;
    private String username;
    private String password;
    private String secretCode;
    private Context mainContext;
    private IKuraMQTTClient client = null;

    public VerifySecretCode(Context context,DevicesInfoDbAdapter devicesInfoDbAdapter, String secretCode) {
        this.devicesInfoDbAdapter = devicesInfoDbAdapter;
        this.secretCode = secretCode;
        this.mainContext = context;
    }

    private DevicesInfoDbAdapter devicesInfoDbAdapter;

    public void executeCredentialWS() {
        if(secretCode.length() == 0){
            SecureCodeDialog secureCodeDialog = new SecureCodeDialog();
            secureCodeDialog.getSecureCode(mainContext);
        }
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(WSConstants.CREDENTIAL_WS + secretCode, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("DEBUG SWISS", "JSONObject");
                    username = (String) response.get("username");
                    password = (String) response.get("password");
                    Log.d("WSCred", username);
                    Log.d("WSCred", password);
                    if (username != null && password != null)
                        devicesInfoDbAdapter.setCredentials(secretCode, username, password);
                    client = MQTTFactory.getClient();
                    boolean status = client.connect();
                    Log.d("After Dialog", "" + status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG SWISS", "JSONArray");
                JSONObject usernameTmp;
                JSONObject passwordTmp;
                try {
                    usernameTmp = (JSONObject) response.get(0);
                    passwordTmp = (JSONObject) response.get(1);
                    username = usernameTmp.getString("username");
                    password = passwordTmp.getString("password");
                    if (username != null && password != null)
                        devicesInfoDbAdapter.setCredentials(secretCode, username, password);
                    Log.d("WSCred", username);
                    Log.d("WSCred", password);
                    client = MQTTFactory.getClient();
                    boolean status = client.connect();
                    Log.d("After Dialog", "" + status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG SWISS", "INSIDE 4" + throwable.getCause());

                try {
                    Toast.makeText(mainContext, "Invalid Code", Toast.LENGTH_LONG).show();
                    SecureCodeDialog secureCodeDialog = new SecureCodeDialog();
                    secureCodeDialog.getSecureCode(mainContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG SWISS", "INSIDE 5");
                try {
                    Toast.makeText(mainContext, "Invalid Code", Toast.LENGTH_LONG).show();
                    SecureCodeDialog secureCodeDialog = new SecureCodeDialog();
                    secureCodeDialog.getSecureCode(mainContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
