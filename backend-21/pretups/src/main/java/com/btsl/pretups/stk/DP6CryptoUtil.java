package com.btsl.pretups.stk;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.util.CryptoUtil;

public class DP6CryptoUtil extends CryptoUtil {

    private static Log _log = LogFactory.getLog(DP6CryptoUtil.class.getName());

    /**
     * decrypt348Data used to decrypt the message using the decryption key
     * 
     * @param simProfile
     *            SimProfileVO
     * @param cipherText
     *            String
     * @param myKeyString
     *            String
     * @throws Exception348
     *             ,GeneralSecurityException
     */

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
            if (_log.isDebugEnabled()) {
                _log.debug("decrypt348Data", "Null SimProfile. Using default parameters: Algorithm-DESede, Mode-CBC and Padding-NoPadding");
            }
            algo = Algorithm;
            cipherParams = CipherParameters;
        } else {
            algo = simProfile.getEncryptALGO();
            mode = simProfile.getEncryptMode();
            padding = simProfile.getEncryptPad();
            if (algo == null || mode == null || padding == null) {
                algo = Algorithm;
                cipherParams = CipherParameters;
                if (_log.isDebugEnabled()) {
                    _log.debug("decrypt348Data", "Unable to get Ciphering parameters form SimProfile.");
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("decrypt348Data", "Using default parameters: Algorithm-DESede, Mode-CBC and Padding-NoPadding");
                }
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

        // plainTextStr=modifyMessage(plainTextStr,simProfile.getSimID());
        // //this method is not used because message do not contains fix data
        return plainTextStr;
    }

    /**
     * For encryption
     * 
     * @param cipherText
     * @param myKeyString
     * @param simProfile
     * @return
     * @throws Exception348
     * @throws GeneralSecurityException
     */
    public String encryptData(String cipherText, String myKeyString, SimProfileVO simProfile) throws Exception348, GeneralSecurityException {
        if (_log.isDebugEnabled()) {
            _log.debug("encrypt", "iccid=" + cipherText + "master key=" + myKeyString);
        }
        // if iccid is not of 32 bytes make it 32.
        cipherText = cipherText.trim();
        int iccidLength = cipherText.length();
        if (iccidLength != 32) {
            int padLength = 32 - iccidLength;
            for (int i = 0; i < padLength; i++) {
                cipherText = cipherText + "0";

            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("encrypt", "iccid final" + cipherText);
        }
        String keyString = myKeyString;
        byte[] cipherBytes = new byte[cipherText.length() / 2];
        byte[] keyBytes = new byte[DESedeKeyLength];
        byte[] plainText = new byte[cipherBytes.length];
        // if keyString is 16 bytes make it 24
        if ((keyString.length() / 2 != 16) && (keyString.length() / 2 != 24)) {
            throw new Exception348("Wrong key Size (" + keyString.length() + "): " + "Should be a 32 or 48 characters string");
        } else if (keyString.length() / 2 == 16) {
            keyString = keyString.concat(keyString.substring(0, 16)); // Make
                                                                      // length
                                                                      // to 48
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
            if (_log.isDebugEnabled()) {
                _log.debug("encrypt", "Null SimProfile. Using default parameters: Algorithm-DESede, Mode-CBC and Padding-NoPadding");
            }
            algo = Algorithm;
            cipherParams = CipherParameters;
        } else {
            algo = simProfile.getEncryptALGO();
            mode = simProfile.getEncryptMode();
            padding = simProfile.getEncryptPad();
            if (algo == null || mode == null || padding == null) {
                algo = Algorithm;
                cipherParams = CipherParameters;
                if (_log.isDebugEnabled()) {
                    _log.debug("encrypt", "Unable to get Ciphering parameters form SimProfile.");
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("encrypt", "Using default parameters: Algorithm-DESede, Mode-CBC and Padding-NoPadding");
                }
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
        plainText = desEdeCipher.doFinal(cipherBytes);
        return bytesToBinHex(plainText);

    }

    public static void main(String args[]){
    	try{
        String iccid = "89911702071270000023";
        String masterkey = "000102030405060708090A0B0C0D0E0F";

        SimProfileVO simProfile = new SimProfileVO();
        simProfile.setEncryptALGO("DESede");
        simProfile.setEncryptMode("CBC");
        simProfile.setEncryptPad("NoPadding");
        DP6CryptoUtil abc = new DP6CryptoUtil();
        // Code for Encrypting
        // String test = "00000000000089912901071270000020";

        // String ciphertext = abc.encrypt(test,keyString.toLowerCase());
        // if(_log.isDebugEnabled())
        // _log.debug("encrypt","12121:::"+ciphertext);
        if (_log.isDebugEnabled()) {
            _log.debug("main", "PlainText: " + abc.encryptData(iccid.toLowerCase(), masterkey.toLowerCase(), simProfile));
        }
        // if(_log.isDebugEnabled()) _log.debug("encrypt","encrypt: " +
        // decrypt348Data(test,keyString.toLowerCase(),simProfile));
    	}
    	catch(Exception e){
   		 _log.errorTrace("main", e);
   		}
    }

    /**
     * modifyMessage used to process the fixed data.
     * 
     * @param strProfile
     *            String
     * @param decodedData
     *            String
     * @throws Exception348
     */
    private String modifyMessage(String decodedData, String strProfile) throws Exception348 {
        final String METHOD_NAME = "modifyMessage";
        if (!"0".equalsIgnoreCase(strProfile)) {
            int fixedIndex = decodedData.indexOf(" F");
            if (_log.isDebugEnabled()) {
                _log.debug("modifyMessage", "fixedIndex " + fixedIndex);
            }

            if (fixedIndex != -1) {

                int admInd = decodedData.indexOf("ADM");
                if (_log.isDebugEnabled()) {
                    _log.debug("encrypt", "admInd " + admInd);
                }
                if (admInd == -1) {
                    String s1 = decodedData.substring(0, fixedIndex + 2);
                    String s2 = decodedData.substring(fixedIndex + 2, fixedIndex + 2 + 13);
                    String s3 = "";
                    try {
                        s3 = decodedData.substring(fixedIndex + 2 + 13);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        s3 = "";
                    }
                    int spaceInd = -1;
                    if (_log.isDebugEnabled()) {
                        _log.debug("encrypt", "S1=" + s1 + ": s2=" + s2 + ": s3=" + s3);
                    }
                    
                    char c = 0xFF;
                    do {
                        spaceInd = s2.indexOf(' ');
                        if (spaceInd != -1) {
                            s2 = s2.replace(' ', c);
                        }
                    } while (spaceInd != -1);

                    decodedData = s1 + s2 + s3;
                    if (_log.isDebugEnabled()) {
                        _log.debug("modifyMessage", "Decoded Data" + decodedData);
                    }
                }
            }// /end of indx
            else// This block has to be reviewed in future
            {
                throw new Exception348("Message cannot be properly decrypted because fixed info is not found for decoded message decodedData");
            }
        }
        int ind;
        boolean flag = true;
        while (flag) {
            ind = decodedData.lastIndexOf(0);
            if ((ind + 1) == decodedData.length()) {
                decodedData = decodedData.substring(0, ind);
            } else {
                flag = false;
            }
        }

        return decodedData;
    }

    public String binaryToHex(String binary) {

        StringBuffer hex = new StringBuffer();
        int binaryLength = binary.length();
        for (int i = 0; i < binaryLength; i++) {

            String hexVal = Integer.toHexString(binary.charAt(i)).toUpperCase();

            while (hexVal.length() < 2) {

                hexVal = "0" + hexVal;

            }

            hex.append(hexVal);

        }

        return hex.toString();

    }
}
