package com.swissbit.homeautomation.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import com.swissbit.homeautomation.db.DevicesInfoDbAdapter;
import com.swissbit.homeautomation.utils.DBFactory;
import com.swissbit.homeautomation.ws.VerifySecretCode;

/**
 * Created by manit on 06/06/15.
 */
public class SecureCodeDialog {

    private AlertDialog.Builder dialogBuilder;
    private Context mainContext;
    private VerifySecretCode verifySecretCode;
    private DevicesInfoDbAdapter devicesInfoDbAdapter;
    private String secretCode;


    public void getSecureCode(Context context){
        this.mainContext = context;
        devicesInfoDbAdapter = DBFactory.getDevicesInfoDbAdapter(context);

        dialogBuilder = new AlertDialog.Builder(context);
        final EditText txtCode = new EditText(context);

        dialogBuilder.setTitle("Enter the Secure Code");
        dialogBuilder.setMessage("Enter the Secure Code");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(txtCode);
        dialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                secretCode = txtCode.getText().toString().trim();
                Log.d("After Dialog", "Trim"+secretCode);
                verifySecretCode = new VerifySecretCode(mainContext, devicesInfoDbAdapter, secretCode);
                verifySecretCode.executeCredentialWS();
                Log.d("After Set","ExecWS");

            }
        });

        AlertDialog dialogSecretCode = dialogBuilder.create();
        dialogSecretCode.show();
        Log.d("After Dialog", "Dailog");
    }


}
