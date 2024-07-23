/*
 * @(#)DESEVDEncryption.java
 * Copyright(c) 2009, Comviva Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Harsh Dixit 28/09/12 Initial Creation
 * ------------------------------------------------------------------------------
 * -------------------
 * This Encryption Logic is used to encrypt the response message (Consists of
 * Serial Number & Voucher PIN)
 * received against the EVD request through POS Terminal
 */
package com.btsl.voms.util;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;

public class DESEVDEncryption {

	private Log _log = LogFactory.getLog(this.getClass().getName());
    private static final String UNICODE_FORMAT = "UTF8";
    private static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec myKeySpec;
    private SecretKeyFactory mySecretKeyFactory;
    private Cipher cipher;
    private byte[] keyAsBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme; 
    private SecretKey key;

    public DESEVDEncryption() throws Exception {
        myEncryptionKey = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXT_VOMS_MSG_DES_ENDE_CRYPT_KEY));
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        keyAsBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        myKeySpec = new DESedeKeySpec(keyAsBytes);
        mySecretKeyFactory = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = mySecretKeyFactory.generateSecret(myKeySpec);
    }

    public DESEVDEncryption(String myEncryptionKey) throws Exception {
        if (myEncryptionKey == (null) || ("".equals(myEncryptionKey)) || myEncryptionKey.length() == 0) {
            myEncryptionKey = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXT_VOMS_MSG_DES_ENDE_CRYPT_KEY));
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
            String[] ar = encryptedString.split("\n");
            String enc = "";
            for (String s : ar) {
                if (s.endsWith("\n")) {
                    s = s.substring(0, s.length() - 1);
                }
                enc = enc + s;
            }
            encryptedString = enc;

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        return encryptedString;
    }

    /**
     * Testing The DESEVD Encryption Technique
     */
    public static void main(String args[]){
        /*
         * DESEVDEncryption myEncryptor= new DESEVDEncryption();
         * String stringToEncrypt=
         * "Transaction number E121001.1443.100001 to recharge 2.50 EGP to 0150144794 is successful. Your new balance is 898.50 EGP. SERIAL NO is 111920778585 and PIN is 66219413010677 ."
         * ;
         * String encrypted=myEncryptor.encrypt(stringToEncrypt);
         * System.out.println("Encrypted String :: "+encrypted);
         */
    }

}
