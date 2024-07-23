package com.btsl.voms.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;


public class DESedeEncryption {

    public static Log _log = LogFactory.getLog(DESedeEncryption.class.getName());
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec myKeySpec;
    private SecretKeyFactory mySecretKeyFactory;
    private Cipher cipher;
    private byte[] keyAsBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    private SecretKey key;

    public DESedeEncryption(String myEncryptionKey) throws GeneralSecurityException, IOException  {
        byte[] iv = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        if (null == (myEncryptionKey) || "".equals(myEncryptionKey)) {
            myEncryptionKey = "ZO4UGUGCAFTLK9MOZO4UGUGCAFTLK9MO";
        }
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
        String encryptedString = null;
        final String METHOD_NAME = "encrypt";
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = BTSLUtil.encode(encryptedText);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        return encryptedString;
    }

    /**
     * Method To Decrypt An Ecrypted String
     */
    public String decrypt(String encryptedString) {
        String decryptedText = null;
        final String METHOD_NAME = "decrypt";
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = BTSLUtil.decodeBuffer(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = bytes2String(plainText);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
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

    /**
     * Testing The DESede Encryption And Decryption Technique
     */
    public static void main(String args[]){
    	try{
        DESedeEncryption myEncryptor = new DESedeEncryption("");
        String stringToEncrypt = "210120380320702";

        for (int i = 0; i < 10; i++) {
            stringToEncrypt = stringToEncrypt.substring(1) + i;
            String encrypted = myEncryptor.encrypt(stringToEncrypt);
            String decrypted = myEncryptor.decrypt(encrypted);
            if (_log.isDebugEnabled()) {
            _log.debug("main",encrypted + " " + decrypted);
            }
        }
    	}catch(Exception e){
    		 _log.errorTrace("main", e);
    	}
    }

}
