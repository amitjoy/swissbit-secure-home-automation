package tum.com.api.impl;

/**
 * Created by Gaurav Kumar Srivastava on 20/06/15.
 */
public interface CardAPIInterface {

    public void disconnectCard();

    public void selectApplet();

    public void connectCard();

    public boolean isCardPresent();

    public String defaultCall();

    public String encryptMsg(String msg);

    public String decryptMsg(String msg);






    public  String byteArrayToHex(byte[] bytes);

    public  byte[] hexToByteArray(String encoded) ;

    /*
     * This function is used to get the message from the decrypted string
     * received from the card.
     */
    public  String getMessageFromResponse(byte[] bytes);

}
