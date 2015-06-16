package tum.com.ssdapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;

import com.secureflashcard.sfclibrary.SfcTerminal;


public class SSDAPIMain extends Activity {

    SfcTerminal sfcTerminal;
    Intent iData;
    byte[] atr = null;
    byte[] response = null;
    String plainMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssdapimain);

        Log.d("API: ", "onCreate function in SSD API");
        iData = new Intent();

        /*
         * Based on the Function Id of the Request Identify which functionality is called in the API
         * 1006  :  Encrypt
         * 1007  :  Decrypt
         * 1008  :  Get Card ID
         * 1009  :  Check Card is Present.
         */

        Bundle bundle = getIntent().getExtras();
        int function = bundle.getInt("Function");


        switch(function){

            /*
             * Function code 1006, can be used to Encrypt a message.
             * The Message to be encrypted will be passed in Bundle as a parameter with name "MSG"
             */
            case 1006:

                Log.d("Function Called: ", "Encrypt");
                encryptMsg(bundle.getString("MSG"));
                break;

            /*
             * Function code 1007, can be used to Decrypt a message.
             * The Message to be encrypted will be passed in Bundle as a parameter with name "MSG"
             */
            case 1007:

                Log.d("Function Called: ", "Decrypt");
                decryptMsg(bundle.getByteArray("MSG"));
                break;

            /*
             * Function code 1008 is used to Get the Unique ID of the Card present in the system.
             */
            case 1008:

                Log.d("Function Called: ", "Get Card ID");
                decryptMsg(bundle.getByteArray("MSG"));
                break;

            /*
             * Function code 1009, can be used to check whether the card is present or not.
             */
            case 1009:

                Log.d("Function Called: ", "Card Present?");
                isCardPresent();
                break;

            /*
             * This is default call if any invalid function code is presented.
             */
            default:
                defaultCall();
                break;

        }
    }

    public void defaultCall(){
        Log.d("Inside Function:", "Default Function call, Invalid Function code passed");
        iData.putExtra("Response", "Invalid Function call.");
        setResult(android.app.Activity.RESULT_OK, iData);
        finish();
    }

    public void isCardPresent() {

        Log.d("Inside Function:", "isCardPresent, Sets the response after the call.");

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

        Log.d("Inside Function:", "encryptMsg, Sets the response with ByteArray (Encrypted String).");

        connectCard();

        try {
            boolean avail = sfcTerminal.isCardPresent();
            if (avail) {
                atr = sfcTerminal.getAtr();

                //Converting message to Byte Array.
                byte[] msgBytes = msg.getBytes("UTF-8");

                //Calculate the pad length because the message should be a multiple of 16
                int padLength = (16 - ((msg.length()+1)%16))%16;

                byte[] plainText = new byte[msg.length() + 1 +  padLength];

                /*
                 * String modifications:
                 * Copy the byte array of original message to cipherText from byte 1,
                 */
                System.arraycopy (msgBytes, 0, plainText, 1, msgBytes.length);

                /*
                 * Byte 0 in Cipher Text contains the length of original message.
                 * Payload length should be part of Crypto Gram Data (Protocol Design)
                 */

                plainText[0]= (byte)msgBytes.length;


                // library cares about keep alive, if app has android.permission.WAKE_LOCK permission!

                sfcTerminal.connect();

                /*
                 * This function is used to select the applet for further communication.
                 * This step is mandatory cause without it the user will not be able to
                 * talk to the applet.
                 */
                selectApplet();

                byte[] cmd = new byte[] { (byte) 0x90, (byte) 0x21, (byte) 0x00, (byte)0x00, (byte)0x40 };

                cmd[4] = (byte) plainText.length;

                byte[] encryptCommand = new byte[cmd.length + plainText.length];

                System.arraycopy (cmd, 0, encryptCommand, 0, cmd.length);
                System.arraycopy (plainText, 0, encryptCommand, cmd.length, plainText.length);

                Log.d("Encryption Command:", bytesToString(encryptCommand));

                response = sfcTerminal.transmit(encryptCommand);

                Log.d("Encrypted Message:", bytesToString(response));

                /*
                 * Should make a check that last 2 bytes are 90 00 which means everything went well.
                 */
                byte[] responseBytes = new byte[response.length - 2];
                System.arraycopy (response, 0, responseBytes, 0, responseBytes.length);

                iData.putExtra("Response", responseBytes);

                Log.d("API returns encrypted:", bytesToString(responseBytes));


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

        Log.d("Inside Function:", "decryptMsg, Sets the response with 'Plain Text' .");

        connectCard();

        try {

            boolean avail = sfcTerminal.isCardPresent();
            if (avail) {

                atr = sfcTerminal.getAtr();

                Log.d("Byte Array Received:", bytesToString(msg));

                byte[] msgBytes = msg;


                // library cares about keep alive, if app has android.permission.WAKE_LOCK permission!
                sfcTerminal.connect();

                /*
                 * This function is used to select the applet for further communication.
                 * This step is mandatory cause without it the user will not be able to
                 * talk to the applet.
                 */

                selectApplet();


                byte[] cmd = new byte[] { (byte) 0x90, (byte) 0x22, (byte) 0x00, (byte)0x00, (byte)0x40 };

                cmd[4] = (byte)msgBytes.length;

                byte[] decryptCommand = new byte[cmd.length + msgBytes.length];
                System.arraycopy (cmd, 0, decryptCommand, 0, cmd.length);
                System.arraycopy (msgBytes, 0, decryptCommand, cmd.length, msgBytes.length);

                Log.d("Decryption Command:", bytesToString(decryptCommand));

                response = sfcTerminal.transmit(decryptCommand);

                this.plainMessage = getMessageFromResponse(response);

                Log.d("Decrypted Message:", this.plainMessage);

                iData.putExtra("Response", plainMessage);

            } else {
                iData.putExtra("Response", "Card Not Present");
            }

        } catch (Exception e) {
            iData.putExtra("Response", e.getMessage());
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

    public void selectApplet() {
        try {
            byte[] cmd =  new byte[]{ (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x0B,
                                      (byte) 0xD2, (byte) 0x76, (byte) 0x00, (byte) 0x01, (byte) 0x62,
                                      (byte) 0x44, (byte) 0x65, (byte) 0x6D, (byte) 0x6F, (byte) 0x01,
                                      (byte) 0x01
                                    };
            sfcTerminal.transmit(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String bytesToString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            if(b==0)	// we need leading zeroes ...
                sb.append("00 ");
            else
                sb.append(String.format("%02X ", b & 0xFF));
        }
        return sb.toString();
    }

    /*
     * This function is used to get the message from the decrypted string
     * received from the card.
     */
    private String getMessageFromResponse(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        int lengthOfMessage = bytes[0];
        Log.d("Length of Message: ", lengthOfMessage + "");

        byte[] message = new byte[bytes.length - 1];

        System.arraycopy(bytes, 1, message, 0, bytes.length - 1);

        int counter = 1;
        while (lengthOfMessage != 0){
           sb.append((char) bytes[counter]);
           counter ++;
           lengthOfMessage--;
        }

        return sb.toString();
    }
}