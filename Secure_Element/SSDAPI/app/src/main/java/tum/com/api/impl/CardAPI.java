package tum.com.api.impl;

import android.content.Intent;
import android.util.Log;
import android.content.Context;

import com.secureflashcard.sfclibrary.SfcTerminal;

import java.util.Random;

/**
 * Created by Gaurav Kumar Srivastava on 20/06/15.
 */

public class CardAPI implements CardAPIInterface{

    Context context;
    SfcTerminal sfcTerminal;
    Intent iData;
    byte[] atr = null;
    byte[] response = null;
    String plainMessage = "";

    public CardAPI(Context context){
        this.context = context;
    }

    @Override
    public void disconnectCard() {
        try {
            sfcTerminal.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
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

    @Override
    public void connectCard() {
        sfcTerminal = new SfcTerminal(context);
    }

    @Override
    public boolean isCardPresent() {
        Log.d("Inside Function:", "isCardPresent, Sets the response after the call.");

        connectCard();

        try {
            boolean avail = sfcTerminal.isCardPresent();
            if (avail) {
                iData.putExtra("Response", "Card Present");
                return true;
            } else {
                iData.putExtra("Response", "Card Not Present");
                return false ;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {

            disconnectCard();

            //setResult(android.app.Activity.RESULT_OK, iData);
            //finish();
        }
    }

    @Override
    public String defaultCall() {
        Log.d("Inside Function:", "Default Function call, Invalid Function code passed");
        iData.putExtra("Response", "Invalid Function call.");
        return "Invalid Function call.";
        //setResult(android.app.Activity.RESULT_OK, iData);
        //finish();
    }

    @Override
    public String encryptMsg(String msg) {
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

                /*
                 * Get Nonce String To make High Security
                 */
                byte[] nonce = new byte[16];
                new Random().nextBytes(nonce);

                byte[] cmd = new byte[] { (byte) 0x90, (byte) 0x21, (byte) 0x00, (byte)0x00, (byte)0x40 };

                cmd[4] = (byte) (plainText.length + nonce.length);

                byte[] encryptCommand = new byte[cmd.length + nonce.length + plainText.length];

                System.arraycopy (cmd, 0, encryptCommand, 0, cmd.length);
                System.arraycopy (nonce, 0, encryptCommand, cmd.length, nonce.length);
                System.arraycopy (plainText, 0, encryptCommand, cmd.length + nonce.length, plainText.length);

                Log.d("Encryption Command:", byteArrayToHex(encryptCommand));

                response = sfcTerminal.transmit(encryptCommand);

                Log.d("Encrypted Message:", byteArrayToHex(response));

                /*
                 * Should make a check that last 2 bytes are 90 00 which means everything went well.
                 */
                byte[] responseBytes = new byte[response.length - 2];
                System.arraycopy (response, 0, responseBytes, 0, responseBytes.length);

                Log.d("API returns encrypted:", byteArrayToHex(responseBytes));

                iData.putExtra("Response", byteArrayToHex(responseBytes));
                return byteArrayToHex(responseBytes);


            } else {
                iData.putExtra("Response", "Card Not Present");
                return "Card Not Present";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {

            disconnectCard();

            //setResult(android.app.Activity.RESULT_OK, iData);
            //finish();
        }

    }

    @Override
    public String decryptMsg(String msg) {

        Log.d("Inside Function:", "decryptMsg, Sets the response with 'Plain Text' .");

        connectCard();

        try {

            boolean avail = sfcTerminal.isCardPresent();
            if (avail) {

                atr = sfcTerminal.getAtr();

                Log.d("Byte Array Received:", msg);

                byte[] msgBytes = hexToByteArray(msg);


                // library cares about keep alive, if app has android.permission.WAKE_LOCK permission!
                sfcTerminal.connect();

                /*
                 * This function is used to select the applet for further communication.
                 * This step is mandatory cause without it the user will not be able to
                 * talk to the applet.
                 */

                selectApplet();


                byte[] cmd = new byte[] { (byte) 0x90, (byte) 0x22, (byte) 0x00, (byte)0x00, (byte)0x40 };

                cmd[4] = (byte) msgBytes.length;

                byte[] decryptCommand = new byte[cmd.length + msgBytes.length];
                System.arraycopy (cmd, 0, decryptCommand, 0, cmd.length);
                System.arraycopy (msgBytes, 0, decryptCommand, cmd.length, msgBytes.length);

                Log.d("Decryption Command:", byteArrayToHex(decryptCommand));

                response = sfcTerminal.transmit(decryptCommand);
                Log.d("Response Message:", byteArrayToHex(response));

                this.plainMessage = getMessageFromResponse(response);

                Log.d("Decrypted Message:", this.plainMessage);

                iData.putExtra("Response", plainMessage);
                return plainMessage;

            } else {
                iData.putExtra("Response", "Card Not Present");
                return "Card Not Present";
            }

        } catch (Exception e) {
            iData.putExtra("Response", e.getMessage());
            e.printStackTrace();
            return e.getMessage();
        } finally {

            disconnectCard();

            //setResult(android.app.Activity.RESULT_OK, iData);
            //finish();
        }

    }

    @Override
    public String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    @Override
    public byte[] hexToByteArray(String encoded) {
        if ((encoded.length() % 2) != 0)
            throw new IllegalArgumentException("Input string must contain an even number of characters");

        final byte result[] = new byte[encoded.length()/2];
        final char enc[] = encoded.toCharArray();
        for (int i = 0; i < enc.length; i += 2) {
            StringBuilder curr = new StringBuilder(2);
            curr.append(enc[i]).append(enc[i + 1]);
            result[i/2] = (byte) Integer.parseInt(curr.toString(), 16);
        }
        return result;
    }

    @Override
    public String getMessageFromResponse(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        int lengthOfMessage = bytes[16];

        Log.d("Length of Message: ", lengthOfMessage + "");

        byte[] message = new byte[bytes.length - 17];

        System.arraycopy(bytes, 17, message, 0, bytes.length - 17);

        int counter = 17;
        while (lengthOfMessage != 0){
            sb.append((char) bytes[counter]);
            counter ++;
            lengthOfMessage--;
        }

        return sb.toString();
    }
}
