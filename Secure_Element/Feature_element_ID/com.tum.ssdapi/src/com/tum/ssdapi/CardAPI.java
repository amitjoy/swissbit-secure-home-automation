package com.tum.ssdapi;

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
	//Intent iData;
	byte[] atr = null;
	byte[] response = null;
	
	String plainMessage = "";
	String[] plainMessageWithID = {"",""};

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

		connectCard();

		System.out.println("Inside Function: " + "isCardPresent, Sets the response after the call.");

		try {
			boolean avail = sfcTerminal.isCardPresent();
			if (avail) {
				//iData.putExtra("Response", "Card Present");
				return true;
			} else {
				//iData.putExtra("Response", "Card Not Present");
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
		System.out.println("Inside Function:" + "Default Function call, Invalid Function code passed");
		//iData.putExtra("Response", "Invalid Function call.");
		return "Invalid Function call.";
		//setResult(android.app.Activity.RESULT_OK, iData);
		//finish();
	}

	@Override
	public String encryptMsg(String msg) {
		System.out.println("Inside Function:" + "encryptMsg, Sets the response with ByteArray (Encrypted String).");

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

				System.out.println("Encryption Command:" + byteArrayToHex(encryptCommand));

				response = sfcTerminal.transmit(encryptCommand);

				/*
				 * Should make a check that last 2 bytes are 90 00 which means everything went well.
				 */
				byte[] responseBytes = new byte[response.length - 2];
				System.arraycopy (response, 0, responseBytes, 0, responseBytes.length);

				System.out.println("API returns encrypted: " + byteArrayToHex(responseBytes));

				//iData.putExtra("Response", byteArrayToHex(responseBytes));
				return byteArrayToHex(responseBytes);


			} else {
				//iData.putExtra("Response", "Card Not Present");
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

		System.out.println("Inside Function: " + "decryptMsg, Sets the response with 'Plain Text' .");

		connectCard();

		try {

			boolean avail = sfcTerminal.isCardPresent();
			if (avail) {

				atr = sfcTerminal.getAtr();

				System.out.println("Byte Array Received: " + msg);

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

				System.out.println("Decryption Command: " + byteArrayToHex(decryptCommand));

				response = sfcTerminal.transmit(decryptCommand);
				System.out.println("Response Message: " + byteArrayToHex(response));

				this.plainMessage = getMessageFromResponse(response);

				System.out.println("Decrypted Message: " + this.plainMessage);

				//iData.putExtra("Response", plainMessage);
				return plainMessage;

			} else {
				//iData.putExtra("Response", "Card Not Present");
				return "Card Not Present";
			}

		} catch (Exception e) {
			//iData.putExtra("Response", e.getMessage());
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

		System.out.println("Length of Message: " + lengthOfMessage + "");

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

	@Override
	public String getMyId() {

		System.out.println("Inside Function:" + "getMyId, Set the response to SSD ID.");

		connectCard();

		try {
			boolean avail = sfcTerminal.isCardPresent();
			if (avail) {
				atr = sfcTerminal.getAtr();


				// library cares about keep alive, if app has android.permission.WAKE_LOCK permission!

				sfcTerminal.connect();

				/*
				 * This function is used to select the applet for further communication.
				 * This step is mandatory cause without it the user will not be able to
				 * talk to the applet.
				 */
				selectApplet();

				/*
				 * This command is used to get SSD Card ID from the Applet
				 */
				byte[] cmd = new byte[] { (byte) 0x90, (byte) 0x10, (byte) 0x00, (byte)0x00, (byte)0x00 };


				response = sfcTerminal.transmit(cmd);

				/*
				 * Should make a check that last 2 bytes are 90 00 which means everything went well.
				 */
				byte[] responseBytes = new byte[response.length - 2];
				System.arraycopy (response, 0, responseBytes, 0, responseBytes.length);
				
				Log.d("ID in bytes: " , bytesToString(responseBytes) );
				return byteArrayToHex(responseBytes);

			} else {
				return byteArrayToHex(new byte[] { (byte) 0x90, (byte) 0x00});
			}
		} catch (Exception e) {
			e.printStackTrace();
			return byteArrayToHex(new byte[] { (byte) 0x00, (byte) 0x00});

		} finally {

			disconnectCard();
		}
	}

	@Override
	public String encryptMsgWithID(String recieverId, String msg) {

		System.out.println("Inside Function:" + "encryptMsgWithId, Sets the response with ByteArray (Encrypted String With Id).");

		connectCard();

		try {
			boolean avail = sfcTerminal.isCardPresent();
			if (avail) {
				atr = sfcTerminal.getAtr();

				//Converting message to Byte Array.
				byte[] msgBytes = msg.getBytes("UTF-8");
				byte[] recieverIdBytes = hexToByteArray(recieverId);
				//byte[] senderIdBytes = hexToByteArray(getMyId());

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

				plainText[0]= (byte) msgBytes.length;


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

				//cmd[4] = (byte) (nonce.length + senderIdBytes.length + recieverIdBytes.length + plainText.length);
				cmd[4] = (byte) (nonce.length + recieverIdBytes.length + plainText.length);

				byte[] encryptCommand = new byte[cmd.length + nonce.length + recieverIdBytes.length + plainText.length];

				System.arraycopy (cmd, 0, encryptCommand, 0, cmd.length);
				System.arraycopy (recieverIdBytes, 0, encryptCommand, cmd.length, recieverIdBytes.length);
				System.arraycopy (nonce, 0, encryptCommand, cmd.length + recieverIdBytes.length, nonce.length);
				System.arraycopy (plainText, 0, encryptCommand, cmd.length + recieverIdBytes.length + nonce.length, plainText.length);

				System.out.println("Encryption Command:" + byteArrayToHex(encryptCommand));

				response = sfcTerminal.transmit(encryptCommand);

				/*
				 * Should make a check that last 2 bytes are 90 00 which means everything went well.
				 */
				byte[] responseBytes = new byte[response.length - 2];
				System.arraycopy (response, 0, responseBytes, 0, responseBytes.length);

				System.out.println("API returns encrypted:" + byteArrayToHex(responseBytes));

				//iData.putExtra("Response", byteArrayToHex(responseBytes));
				return byteArrayToHex(responseBytes);


			} else {
				//iData.putExtra("Response", "Card Not Present");
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
	public String[] decryptMsgWithID(String msg) {


		System.out.println("Inside Function:" + "decryptMsgWithId, Sets the response with 'Plain Text' .");

		connectCard();

		try {

			boolean avail = sfcTerminal.isCardPresent();
			if (avail) {

				atr = sfcTerminal.getAtr();

				System.out.println("Byte Array Received: " + msg);

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

				System.out.println("Decryption Command: " + byteArrayToHex(decryptCommand));

				byte[] responseMsg = sfcTerminal.transmit(decryptCommand);
				
				byte[] responseBytes = new byte[responseMsg.length - 2];
				
				System.arraycopy (responseMsg, 0, responseBytes, 0, responseBytes.length);

				System.out.println("Response Message: " + byteArrayToHex(responseBytes));

				this.plainMessageWithID = getMessageFromResponseWithId(responseMsg);

				System.out.println("Decrypted Message: " + this.plainMessageWithID[1]);

				//iData.putExtra("Response", plainMessage);
				return plainMessageWithID;

			} else {
				//iData.putExtra("Response", "Card Not Present");
				return plainMessageWithID;
			}

		} catch (Exception e) {
			//iData.putExtra("Response", e.getMessage());
			e.printStackTrace();
			this.plainMessageWithID[1] = e.getMessage();
			return this.plainMessageWithID;
		} finally {

			disconnectCard();

			//setResult(android.app.Activity.RESULT_OK, iData);
			//finish();
		}

		}

	@Override
	public String[] getMessageFromResponseWithId(byte[] bytes) {
		
		String [] response = {bytesToString(bytes), "Something Went Wrong"};
		
		if (bytes.length == 2) {
			if ((bytes[0] == (byte) 0x6A) && (bytes[1] == (byte) 0x80)){
				response[1] = "Error: Message Not for me.";
			}
			
			return response;
		}
		StringBuffer plainText = new StringBuffer();

		System.out.println("Message: " + byteArrayToHex(bytes) + "");

		
		int lengthOfMessage = bytes[32];

		System.out.println("Length of Message: " + lengthOfMessage + "");

		//byte[] message = new byte[bytes.length - 17];

		//System.arraycopy(bytes, 17, message, 0, bytes.length - 17);

		int counter = 33;
		while (lengthOfMessage != 0){
			plainText.append((char) bytes[counter]);
			counter ++;
			lengthOfMessage--;
		}

		byte[] senderId = new byte[16];
		System.arraycopy (bytes, 16, senderId, 0, 16);
		

		String [] responseMesg = { byteArrayToHex(senderId), plainText.toString()};
		return responseMesg;
	}
	
	private static String bytesToString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b & 0xFF));
        }
        return sb.toString();
    }
}
