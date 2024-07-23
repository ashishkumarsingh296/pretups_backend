/**
 * @(#)DESedeDecryption.java
 *                                       
 Name Date History
 * ------------------------------------------------------------------------
 * Zeeshan Aleem 28/12/2019 Initial Creation
 * ------------------------------------------------------------------------
 * COMVIVA
 */

package com.btsl.pretups.channel.transfer.util.clientutils;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;


public class DESedeDecryption {
	private Log _log = LogFactory.getLog(this.getClass().getName());
	
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec myKeySpec;
    private SecretKeyFactory mySecretKeyFactory;
    private Cipher cipher;
    byte[] keyAsBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;

    public DESedeDecryption(String p_key) throws Exception
    {    	
        myEncryptionKey = p_key;
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        keyAsBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        myKeySpec = new DESedeKeySpec(keyAsBytes);
        mySecretKeyFactory = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = mySecretKeyFactory.generateSecret(myKeySpec);
    }

    /**
     * Method To Encrypt The String
     */
    public String encrypt(String unencryptedString) {
    	final String METHOD_NAME = "encrypt";
    	
    	 if (_log.isDebugEnabled()) {
             _log.debug(METHOD_NAME, "Entered unencryptedString=" + unencryptedString);
         }
    	 
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = BTSLUtil.encode(encryptedText);
        } catch (Exception e) {
           _log.errorTrace(METHOD_NAME,e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting unencryptedString=" + encryptedString);
        }
        return encryptedString;
    }
    /**
     * Method To Decrypt An Ecrypted String
     */
    public String decrypt(String encryptedString) {
    	final String METHOD_NAME = "decrypt";
    	
    	if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered encryptedString=" + encryptedString);
        }
    	
        String decryptedText=null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = BTSLUtil.decodeBuffer(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText= bytes2String(plainText);
        } catch (Exception e) {
        	 _log.errorTrace(METHOD_NAME,e);
        }
        
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting decryptedText=" + decryptedText);
        }
        return decryptedText;
    }
    /**
     * Returns String From An Array Of Bytes
     */
    private static String bytes2String(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }
}
