/*
 * Created on Jan 14, 2004
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.btsl.voms.voucher.businesslogic;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class PINEncryptor {
    private static Logger _logger = Logger.getLogger(PINEncryptor.class.getName());
    private static Log _log = LogFactory.getLog(PINEncryptor.class.getName());

   
    private static final int DESedeKeyLength = 24;
    private static final String Algorithm = "DESede";
    private static final String CipherParameters = "DESede/CBC/PKCS5Padding";
    private final byte[] iv = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    private final char[] HEXTAB = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

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
    private int binHexToBytes(String sBinHex, byte[] data, int nSrcPos, int nNumOfBytes) {
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
                    bActByte |= (cActChar - 'a') + 10;
                } else {
                    if ((cActChar >= '0') && (cActChar <= '9')) {
                        bActByte |=(cActChar - '0');
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
    private String bytesToBinHex(byte[] data) throws Exception {
        StringBuffer sbuf = new StringBuffer();
        int nNumOfBytes = data.length;
        int nStartPos = 0;
        sbuf.setLength(nNumOfBytes << 1);
        int nPos = 0;
        for (int nI = 0; nI < nNumOfBytes; nI++) {
            sbuf.setCharAt(nPos++, HEXTAB[(data[nI + nStartPos] >> 4) & 0x0f]);
            sbuf.setCharAt(nPos++, HEXTAB[data[nI + nStartPos] & 0x0f]);
        }
        return sbuf.toString();
    }

    public void encryptPINs(String[][] p_voucherInfoArray, String myKeyString) throws  GeneralSecurityException {
        String keyString = myKeyString.toLowerCase();
        byte[] keyBytes = new byte[DESedeKeyLength];

        // if keyString is 16 bytes make it 24
        if ((keyString.length() / 2 != 16) && (keyString.length() / 2 != 24)) {
        	_log.error("encryptPINs", "Wrong key Size (" + keyString.length() + "): " + "Should be a 32 or 48 characters string");
        } else if (keyString.length() / 2 == 16) {
            keyString = keyString.concat(keyString.substring(0, 16));
        }
        if (binHexToBytes(keyString, keyBytes, 0, DESedeKeyLength) != (DESedeKeyLength)) {
        	_log.error("encryptPINs", "Error while converting keyStrings to byte[] ");
        }

        String plainText = null;
        String cipherText = null;

        SecretKey desEdeKey = new SecretKeySpec(keyBytes, Algorithm);
        Cipher desEdeCipher;
        desEdeCipher = Cipher.getInstance(CipherParameters);
        IvParameterSpec ivSpec1 = new IvParameterSpec(iv);
        for (int i = 0; i < p_voucherInfoArray.length; i++) {
            plainText = (String) p_voucherInfoArray[i][1];
            desEdeCipher.init(Cipher.ENCRYPT_MODE, desEdeKey, ivSpec1);
            try {
				cipherText = bytesToBinHex(desEdeCipher.doFinal(plainText.getBytes()));
			} catch (Exception e) {
				_log.error("encryptPINs", e.getMessage());
			}
            p_voucherInfoArray[i][3] = (String) cipherText;
        }
    }

    public static void main(String args[]) {

        int count = 10;
        try {
            _log.debug("main", "No of Pins Required :" + count);
        } catch (Exception e) {
            _log.errorTrace("main", e);
        }
    }
}
