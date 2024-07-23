package com.client.pretups.stk;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class DES {

    // Algorithm used
    private static final String ALGORITHM = "DES";
    private static final String HEX = "0123456789ABCDEF";
    private static final Log LOG = LogFactory.getLog(DES.class.getName());
    /**
     * Encrypt data
     * @param secretKey -   a secret key used for encryption
     * @param data      -   data to encrypt
     * @return  Encrypted data
     * @throws Exception
     */
    public static String cipher(String secretKey, String data) throws Exception {
        // Key has to be of length 8
        final String methodName = "cipher";
        LogFactory.printLog(methodName,"Enter data: " + data, LOG);
        if (secretKey == null || secretKey.length() != 8){
            throw new Exception("Invalid key length - 8 bytes key needed!");
        }
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        LogFactory.printLog(methodName,"Exit with ciphered data ", LOG);
        return toHex(cipher.doFinal(toByte(data)));
    }
    /**
     * Decrypt data
     * @param secretKey -   a secret key used for decryption
     * @param data      -   data to decrypt
     * @return  Decrypted data
     * @throws Exception 1147896DE2DF4A18 
     */
    public static String decipher(String secretKey, String data) throws Exception {
        final String methodName = "decipher";
        LogFactory.printLog(methodName,"Enter data: " + data, LOG);
        if (secretKey == null || secretKey.length() != 8){
            throw new Exception("Invalid key length - 8 bytes key needed!");
        }
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        LogFactory.printLog(methodName,"Exit with Deciphered data ", LOG);
        return new String(cipher.doFinal(toByte(data)));
    }
    /**
     * Method toByte
     * toByte Method , converts hexString to byte
     * 
     * @param hexString
     * @return byte
     */
    private static byte[] toByte(String hexString) {
        final String methodName = "toByte";
        LogFactory.printLog(methodName,"Enter hexString: " + hexString, LOG);
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++){
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        }
        LogFactory.printLog(methodName,"Exit result: " + result, LOG);
        return result;
    }
    
    /**
     * Method toHex
     * toHex Method , converts byte to hexString
     * 
     * @param stringBytes
     * @return String
     */
    public static String toHex(byte[] stringBytes) {
        final String methodName = "toHex";
        LogFactory.printLog(methodName,"Enter stringBytes: " + stringBytes, LOG);
        StringBuilder result = new StringBuilder(2*stringBytes.length);
        for (int i = 0; i < stringBytes.length; i++) {
            result.append(HEX.charAt((stringBytes[i]>>4)&0x0f)).append(HEX.charAt(stringBytes[i]&0x0f));
        }
        LogFactory.printLog(methodName,"Exit result: " + result.toString(), LOG);
        return result.toString();
    }   
}