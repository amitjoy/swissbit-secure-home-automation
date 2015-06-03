package tum.com.ssdapi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Intent;

import com.secureflashcard.sfclibrary.SfcTerminal;


public class SSDAPIMain extends Activity {

    SfcTerminal sfcTerminal;
    Intent iData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssdapimain);

        iData = new Intent();

        Bundle bundle = getIntent().getExtras();
        int function = bundle.getInt("Function");
        Toast msg = Toast.makeText(this, function + "::In Main1 of another activity", Toast.LENGTH_LONG);
        msg.show();

        switch(function){

            /*
             * Function code 1006, can be used to Encrypt a message.
             * The Message to be encrypted will be passed in Bundle as a parameter with name "MSG"
             */
            case 1006:
                encryptMsg(bundle.getString("MSG"));
                break;

            /*
             * Function code 1007, can be used to Decrypt a message.
             * The Message to be encrypted will be passed in Bundle as a parameter with name "MSG"
             */
            case 1007:
                decryptMsg(bundle.getByteArray("MSG"));
                break;

            /*
             * Function code 1009, can be used to check whether the card is present or not.
             */
            case 1009:
                isCardPresent();
                break;

            default:
                defaultCall();
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ssdapimain1, menu);
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

    public void defaultCall(){

        iData.putExtra("Response", "Card Present");
        setResult(android.app.Activity.RESULT_OK, iData);
        finish();
    }

    public void isCardPresent() {

        connectCard();

        try {
            boolean avail = sfcTerminal.isCardPresent();
            if (avail) {
                iData.putExtra("Response", "Card Present");
            } else {
                iData.putExtra("Response", "Card Not Present");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            disconnectCard();

            setResult(android.app.Activity.RESULT_OK, iData);
            finish();
        }
    }

    public void encryptMsg(String msg) {

        connectCard();

        try {
            boolean avail = sfcTerminal.isCardPresent();
            if (avail) {
                iData.putExtra("Response", "Message to be encrypted:" + msg);
            } else {
                iData.putExtra("Response", "Card Not Present");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            disconnectCard();

            setResult(android.app.Activity.RESULT_OK, iData);
            finish();
        }
    }

    public void decryptMsg(byte[] msg) {

        connectCard();

        try {
            boolean avail = sfcTerminal.isCardPresent();
            if (avail) {
                iData.putExtra("Response", "Message to be encrypted:" + msg);
            } else {
                iData.putExtra("Response", "Card Not Present");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            disconnectCard();

            setResult(android.app.Activity.RESULT_OK, iData);
            finish();
        }
    }

    /*
     * This function is used to connect to the card
     */
    public void connectCard() {

        sfcTerminal = new SfcTerminal(getApplicationContext());
    }

    /*
     * This function is used to disconnect the card
     */
    public void disconnectCard() {
        try {
            sfcTerminal.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}