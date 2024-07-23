package com.btsl.pretups.stk;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.util.CryptoUtil;

public class STKCryptoUtil extends CryptoUtil {

    public String decrypt348Data(String cipherText, String myKeyString, SimProfileVO simProfile) throws Exception348, GeneralSecurityException {
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

            algo = Algorithm;
            cipherParams = CipherParameters;
        } else {
            algo = simProfile.getEncryptALGO();
            mode = simProfile.getEncryptMode();
            padding = simProfile.getEncryptPad();
            if (algo == null || mode == null || padding == null) {
                algo = Algorithm;
                cipherParams = CipherParameters;

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
        return plainTextStr;
    }

    // This is used in OTA Message class and uses SimProfile
    public String encrypt348Data(byte[] plainTextBytes, String myKeyString, SimProfileVO simProfile) throws Exception348, GeneralSecurityException {
        String keyString = myKeyString;
        byte[] keyBytes = new byte[DESedeKeyLength];
        byte[] cipherText = new byte[plainTextBytes.length];
        // if keyString is 16 bytes make it 24
        if ((keyString.length() / 2 != 16) && (keyString.length() / 2 != 24)) {
            throw new Exception348("Wrong key Size (" + keyString.length() + "): " + "Should be a 32 or 48 characters string");
        } else if (keyString.length() / 2 == 16) {
            keyString = keyString.concat(keyString.substring(0, 16));
        }

        // plainTextBytes = plainText.getBytes();

        if (binHexToBytes(keyString, keyBytes, 0, DESedeKeyLength) != (DESedeKeyLength)) {
            throw new Exception348("Error while converting keyStrings to byte[] ");
        }

        String algo = null;
        String mode = null;
        String padding = null;
        String cipherParams = null;

        if (simProfile == null) {

            algo = Algorithm;
            cipherParams = CipherParameters;
        } else {
            algo = simProfile.getEncryptALGO();
            mode = simProfile.getEncryptMode();
            padding = simProfile.getEncryptPad();
            if (algo == null || mode == null || padding == null) {
                algo = Algorithm;
                cipherParams = CipherParameters;

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

        return bytesToBinHex(cipherText);
    }
}
