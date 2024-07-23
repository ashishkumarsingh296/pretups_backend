package com.client.pretups.stk;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.pretups.stk.Exception348;
import com.btsl.util.CryptoUtil;

/**
 * Util for Encryption Decryption  in Vietnam STK
 * @author
 * @since 27/09/2017
 *
 */
public class STKVNMCryptoUtil extends CryptoUtil {
    
    private static final Log LOG = LogFactory.getLog(CryptoUtil.class.getName());
    private static final String ALGO = "DESede";
    private static final String CIPHERPARAMS = "DESede/CBC/NoPadding";

    public String decrypt348Data(String cipherText, String myKeyString, SimProfileVO simProfile) throws Exception348, GeneralSecurityException {
        final String methodName = "decrypt348Data";
        LogFactory.printLog(methodName,"Enter cipherText: " + cipherText +" myKeyString: "+ myKeyString, LOG);
        String keyString = myKeyString;
        byte[] cipherBytes = new byte[cipherText.length() / 2];
        byte[] keyBytes = new byte[DESedeKeyLength];
        byte[] plainText = new byte[cipherBytes.length];
        // if keyString is 16 bytes make it 24
        if ((keyString.length() / 2 != 16) && (keyString.length() / 2 != 24)) {
            throw new Exception348("Wrong key Size (" + keyString.length() + "): " + "Should be a 32 or 48 characters string");
        } else if (keyString.length() / 2 == 16) {
            keyString = keyString.concat(keyString.substring(0, 16));
        }

        if (binHexToBytes(cipherText, cipherBytes, 0, cipherText.length() / 2) != (cipherText.length() / 2)) {
            throw new Exception348("Error while converting cipherText to byte[] ");
        }
        if (binHexToBytes(keyString, keyBytes, 0, DESedeKeyLength) != (DESedeKeyLength)) {
            throw new Exception348("Error while converting keyStrings to byte[] ");
        }

        String algo = null;
        String mode = null;
        String padding = null;
        String cipherParams = null;

        if (simProfile == null) {
            algo = ALGO;
            cipherParams = CIPHERPARAMS;
        } else {
            algo = simProfile.getEncryptALGO();
            mode = simProfile.getEncryptMode();
            padding = simProfile.getEncryptPad();
            if (algo == null || mode == null || padding == null) {
                algo = ALGO;
                cipherParams = CIPHERPARAMS;

            } else {
                cipherParams = algo + "/" + mode + "/" + padding;
            }
        }

        SecretKey desEdeKey = new SecretKeySpec(keyBytes, algo);
        Cipher desEdeCipher;
        // Create the cipher
        desEdeCipher = Cipher.getInstance(cipherParams);
        IvParameterSpec ivSpec1 = new IvParameterSpec(iv);
        desEdeCipher.init(Cipher.DECRYPT_MODE, desEdeKey, ivSpec1);
        plainText = desEdeCipher.doFinal(cipherBytes);
        String plainTextStr = new String(plainText, 0, plainText.length);
        //added for Vietnam
        String hexString = Hex.encodeHexString(plainTextStr.getBytes());
        String finalHexString = hexString.toUpperCase().replace("FF", "");
        byte[] bytes = null;
        try {
            bytes= Hex.decodeHex(finalHexString.toCharArray());
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        LogFactory.printLog(methodName,"Enter String(bytes)" + new String(bytes) , LOG);
        return new String(bytes);
        //close for Vietnam
        
    }

    // This is used in OTA Message class and uses SimProfile
    public String encrypt348Data(byte[] plainTextBytes, String myKeyString, SimProfileVO simProfile) throws Exception348, GeneralSecurityException {
        final String methodName = "encrypt348Data";
        LogFactory.printLog(methodName,"Enter plainTextBytes: " + plainTextBytes +" myKeyString: "+ myKeyString, LOG);
        String keyString = myKeyString;
        byte[] keyBytes = new byte[DESedeKeyLength];
        byte[] cipherText = new byte[plainTextBytes.length];
        // if keyString is 16 bytes make it 24
        if ((keyString.length() / 2 != 16) && (keyString.length() / 2 != 24)) {
            throw new Exception348("Wrong key Size (" + keyString.length() + "): " + "Should be a 32 or 48 characters string");
        } else if (keyString.length() / 2 == 16) {
            keyString = keyString.concat(keyString.substring(0, 16));
        }

        if (binHexToBytes(keyString, keyBytes, 0, DESedeKeyLength) != (DESedeKeyLength)) {
            throw new Exception348("Error while converting keyStrings to byte[] ");
        }

        String algo = null;
        String mode = null;
        String padding = null;
        String cipherParams = null;

        if (simProfile == null) {

            algo = ALGO;
            cipherParams = CIPHERPARAMS;
        } else {
            algo = simProfile.getEncryptALGO();
            mode = simProfile.getEncryptMode();
            padding = simProfile.getEncryptPad();
            if (algo == null || mode == null || padding == null) {
                algo = ALGO;
                cipherParams = CIPHERPARAMS;
            } else {
                cipherParams = algo + "/" + mode + "/" + padding;
            }
        }

        SecretKey desEdeKey = new SecretKeySpec(keyBytes, algo);
        Cipher desEdeCipher;
        // Create the cipher
        desEdeCipher = Cipher.getInstance(cipherParams);
        IvParameterSpec ivSpec1 = new IvParameterSpec(iv);
        desEdeCipher.init(Cipher.ENCRYPT_MODE, desEdeKey, ivSpec1);
        cipherText = desEdeCipher.doFinal(plainTextBytes);
        LogFactory.printLog(methodName,"Enter bytesToBinHex(cipherText)" + bytesToBinHex(cipherText) , LOG);
        return bytesToBinHex(cipherText);
        
    }
}
