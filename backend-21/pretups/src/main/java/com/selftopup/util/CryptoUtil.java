package com.selftopup.util;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {

    // Algo is Triple DES with 2 keys in Edncrypt-Decrypt-Encrypt Mode
    protected static final String Algorithm = "DESede";
    protected static final String CipherParameters = "DESede/CBC/PKCS5Padding";
    protected static final String CipherParameters2 = "DESede/ECB/PKCS5Padding";
    protected static final int DESedeKeyLength = 24;
    protected static final int PlainTextLength = 32;

    // Intialization vector : All zeros
    protected static final byte[] iv = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    // our table for binhex conversion
    final static char[] HEXTAB = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    // Utility Functions

    /**
     * converts a binhex string back into a byte array (invalid codes will be
     * skipped)
     * 
     * @param sBinHex
     *            binhex string
     * @param data
     *            the target array
     * @param nSrcPos
     *            from which character in the string the conversion should
     *            begin,
     *            remember that (nSrcPos modulo 2) should equals 0 normally
     * @param nNumOfBytes
     *            number of bytes to extract
     * @return number of extracted bytes
     */
    protected int binHexToBytes(String sBinHex, byte[] data, int nSrcPos, int nNumOfBytes) {
        // Dest pos set to zero.
        int nDstPos = 0;

        // check for correct ranges
        int nStrLen = sBinHex.length();

        int nAvailBytes = (nStrLen - nSrcPos) >> 1;
        if (nAvailBytes < nNumOfBytes) {
            nNumOfBytes = nAvailBytes;
        }

        int nOutputCapacity = data.length;
        if (nNumOfBytes > nOutputCapacity) {
            nNumOfBytes = nOutputCapacity;
        }

        // convert now
        int nResult = 0;
        for (int nI = 0; nI < nNumOfBytes; nI++) {
            byte bActByte = 0;
            boolean blConvertOK = true;
            for (int nJ = 0; nJ < 2; nJ++) {
                bActByte <<= 4;
                char cActChar = sBinHex.charAt(nSrcPos++);

                if ((cActChar >= 'a') && (cActChar <= 'f')) {
                    bActByte |= (byte) (cActChar - 'a') + 10;
                } else if ((cActChar >= 'A') && (cActChar <= 'F')) {
                    bActByte |= (byte) (cActChar - 'A') + 10;
                } else {
                    if ((cActChar >= '0') && (cActChar <= '9')) {
                        bActByte |= (byte) (cActChar - '0');
                    } else {
                        blConvertOK = false;
                    }
                }
            }
            if (blConvertOK) {
                data[nDstPos++] = bActByte;
                nResult++;
            }
        }

        return nResult;
    }

    public String bytesToBinHex(byte[] data) {
        // just map the call
        return bytesToBinHex(data, 0, data.length);
    }

    /**
     * converts a byte array to a binhex string
     * 
     * @param data
     *            the byte array
     * @param nStartPos
     *            start index where to get the bytes
     * @param nNumOfBytes
     *            number of bytes to convert
     * @return the binhex string
     */
    public String bytesToBinHex(byte[] data, int nStartPos, int nNumOfBytes) {
        StringBuffer sbuf = new StringBuffer();
        sbuf.setLength(nNumOfBytes << 1);

        int nPos = 0;
        for (int nI = 0; nI < nNumOfBytes; nI++) {
            sbuf.setCharAt(nPos++, HEXTAB[(data[nI + nStartPos] >> 4) & 0x0f]);
            sbuf.setCharAt(nPos++, HEXTAB[data[nI + nStartPos] & 0x0f]);
        }
        return sbuf.toString();
    }

    public String decrypt(String cipherText, String myKeyString) throws Exception348, GeneralSecurityException {
        String keyString = myKeyString.toLowerCase();
        // System.out.println("decrypt : key ::::"+keyString);
        byte[] cipherBytes = new byte[cipherText.length() / 2];
        byte[] keyBytes = new byte[DESedeKeyLength];
        byte[] plainText = new byte[cipherBytes.length];
        // if keyString is 16 bytes make it 24
        if ((keyString.length() / 2 != 16) && (keyString.length() / 2 != 24))
            throw new Exception348("Wrong key Size (" + keyString.length() + "): " + "Should be a 32 or 48 characters string");
        else if (keyString.length() / 2 == 16) {
            keyString = keyString.concat(keyString.substring(0, 16));
        }

        if (binHexToBytes(cipherText.toLowerCase(), cipherBytes, 0, cipherText.length() / 2) != (cipherText.length() / 2))
            throw new Exception348("Error while converting cipherText to byte[] ");
        if (binHexToBytes(keyString, keyBytes, 0, DESedeKeyLength) != (DESedeKeyLength))
            throw new Exception348("Error while converting keyStrings to byte[] ");

        SecretKey desEdeKey = new SecretKeySpec(keyBytes, Algorithm);
        Cipher desEdeCipher;
        // Create the cipher
        desEdeCipher = Cipher.getInstance(CipherParameters);
        IvParameterSpec ivSpec1 = new IvParameterSpec(iv);
        desEdeCipher.init(Cipher.DECRYPT_MODE, desEdeKey, ivSpec1);
        plainText = desEdeCipher.doFinal(cipherBytes);
        return new String(plainText, 0, plainText.length);
    }

    public ArrayList decryptList(ArrayList cipherList, String myKeyString) throws Exception, GeneralSecurityException {
        String keyString = myKeyString.toLowerCase();
        byte[] keyBytes = new byte[DESedeKeyLength];

        // if keyString is 16 bytes make it 24
        if ((keyString.length() / 2 != 16) && (keyString.length() / 2 != 24))
            throw new Exception("Wrong key Size (" + keyString.length() + "): " + "Should be a 32 or 48 characters string");
        else if (keyString.length() / 2 == 16) {
            keyString = keyString.concat(keyString.substring(0, 16));
        }

        if (binHexToBytes(keyString, keyBytes, 0, DESedeKeyLength) != (DESedeKeyLength))
            throw new Exception("Error while converting keyStrings to byte[] ");

        SecretKey desEdeKey = new SecretKeySpec(keyBytes, Algorithm);
        Cipher desEdeCipher;
        desEdeCipher = Cipher.getInstance(CipherParameters);
        IvParameterSpec ivSpec1 = new IvParameterSpec(iv);

        String plainText = null;
        String cipherText = null;
        ArrayList plainList = new ArrayList();
        byte[] cipherBytes = new byte[PlainTextLength];
        byte[] plainBytes = new byte[cipherBytes.length];

        for (int j = 0; j < cipherList.size(); j++) {
            cipherText = (String) cipherList.get(j);
            if (binHexToBytes(cipherText.toLowerCase(), cipherBytes, 0, cipherText.length() / 2) != (cipherText.length() / 2))
                throw new Exception("Error while converting cipherText to byte[] ");

            desEdeCipher.init(Cipher.DECRYPT_MODE, desEdeKey, ivSpec1);
            plainBytes = desEdeCipher.doFinal(cipherBytes, 0, cipherText.length() / 2);
            plainText = new String(plainBytes, 0, plainBytes.length);
            plainList.add(j, (String) plainText);
        }
        return plainList;

    }

    public String encrypt(String plainText, String myKeyString) throws Exception348, GeneralSecurityException {
        String keyString = myKeyString.toLowerCase();

        byte[] plainTextBytes = new byte[plainText.length()];
        byte[] keyBytes = new byte[DESedeKeyLength];
        byte[] cipherText = new byte[plainTextBytes.length];
        // if keyString is 16 bytes make it 24
        if ((keyString.length() / 2 != 16) && (keyString.length() / 2 != 24))
            throw new Exception348("Wrong key Size (" + keyString.length() + "): " + "Should be a 32 or 48 characters string");
        else if (keyString.length() / 2 == 16) {
            keyString = keyString.concat(keyString.substring(0, 16));
        }

        plainTextBytes = plainText.getBytes();

        if (binHexToBytes(keyString, keyBytes, 0, DESedeKeyLength) != (DESedeKeyLength))
            throw new Exception348("Error while converting keyStrings to byte[] ");

        SecretKey desEdeKey = new SecretKeySpec(keyBytes, Algorithm);
        Cipher desEdeCipher;
        // Create the cipher
        desEdeCipher = Cipher.getInstance(CipherParameters);
        IvParameterSpec ivSpec1 = new IvParameterSpec(iv);
        desEdeCipher.init(Cipher.ENCRYPT_MODE, desEdeKey, ivSpec1);
        cipherText = desEdeCipher.doFinal(plainTextBytes);

        return bytesToBinHex(cipherText);

    }

    public ArrayList encryptList(ArrayList plainList, String myKeyString) throws Exception, GeneralSecurityException {
        String keyString = myKeyString.toLowerCase();
        byte[] keyBytes = new byte[DESedeKeyLength];

        // if keyString is 16 bytes make it 24
        if ((keyString.length() / 2 != 16) && (keyString.length() / 2 != 24))
            throw new Exception("Wrong key Size (" + keyString.length() + "): " + "Should be a 32 or 48 characters string");

        else if (keyString.length() / 2 == 16) {
            keyString = keyString.concat(keyString.substring(0, 16));
        }
        if (binHexToBytes(keyString, keyBytes, 0, DESedeKeyLength) != (DESedeKeyLength))
            throw new Exception("Error while converting keyStrings to byte[] ");

        String plainText = null;
        String cipherText = null;
        ArrayList cipherList = new ArrayList();

        SecretKey desEdeKey = new SecretKeySpec(keyBytes, Algorithm);
        Cipher desEdeCipher;
        desEdeCipher = Cipher.getInstance(CipherParameters);
        IvParameterSpec ivSpec1 = new IvParameterSpec(iv);

        for (int i = 0; i < plainList.size(); i++) {
            plainText = (String) plainList.get(i);
            desEdeCipher.init(Cipher.ENCRYPT_MODE, desEdeKey, ivSpec1);
            cipherText = bytesToBinHex(desEdeCipher.doFinal(plainText.getBytes()));
            cipherList.add(i, (String) cipherText);
        }
        return cipherList;

    }

    public static void main(String args[]) throws Exception {
        String keyString = "202122232425262728292A2B2C2D2E2F";

        CryptoUtil crypto = new CryptoUtil();
        ArrayList list = new ArrayList();
        list.add("Rajat");
        list.add("text");
        list.add("xyz");
        list.add("text111111111");
        list.add("1234567890123456");
        list.add("123456789012");
        list.add("123456789012345");
        list.add("123456789012345");
        list.add("123456789012345");
        list.add("12345678901234");
        for (int i = 0; i < 10000; i++)
            list.add("12345678901234");

        ArrayList list2;

        list2 = crypto.encryptList(list, keyString);

        ArrayList plainList = crypto.decryptList(list2, keyString);

        for (int i = 0; i < 100; i++)
            System.out.println(plainList.get(i));

        // System.out.println("Cipher Text: " +
        // crypto.encrypt(plainText,keyString));
        // String ciphertext = "bca9012fa60a2682"; // Output of encrypt function
        // System.out.println("PlainText: " +
        // crypto.decrypt(ciphertext,keyString));

    }
}
