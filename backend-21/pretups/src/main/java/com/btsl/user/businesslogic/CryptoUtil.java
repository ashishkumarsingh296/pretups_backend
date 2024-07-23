/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * This final class used for Crypt utilities methods.
 * 
 * @author muralib
 */
public final class CryptoUtil {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoUtil.class);

    protected static final int DESedeKeyLength = 24;

    /** The Constant IV. */
    private static final byte[] IV_PARAMS = new byte[] {
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    /** The Constant HEXTAB. */
    private static final char[] HEXTAB = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    
    
    private static final int KEY_STRING_MAX_LENGTH = NumberConstants.N16.getIntValue();

    /**
     * Instantiates a new crypto util.
     */
    CryptoUtil() {
    }

    /**
     * Encrypt the input.
     *
     * @param plainText
     *            - plainText
     * @param myKeyString
     *            - myKeyString
     * @return String
     * @throws GeneralSecurityException
     *             - GeneralSecurityException
     */
    public static String encrypt(String plainText, String myKeyString) throws GeneralSecurityException {
        String keyString = myKeyString.toLowerCase(Locale.ENGLISH);
        byte[] plainTextBytes = new byte[plainText.length()];
        final byte[] keyBytes = new byte[DESedeKeyLength];
        byte[] cipherText = new byte[plainTextBytes.length];
        if ((keyString.length() / NumberConstants.TWO.getIntValue() != NumberConstants.N16.getIntValue())
                && (keyString.length() / NumberConstants.TWO.getIntValue() != NumberConstants.N24.getIntValue())) {
            throw new ApplicationException(
                    "Wrong key Size (" + keyString.length() + "): " + "Should be a 32 or 48 characters string");
        } else if (keyString.length() / NumberConstants.TWO.getIntValue() == NumberConstants.N16.getIntValue()) {
            keyString = keyString.concat(keyString.substring(NumberConstants.ZERO.getIntValue(), NumberConstants.N16.getIntValue()));
        }
        plainTextBytes = plainText.getBytes();
        if (binHexToBytes(keyString, keyBytes, NumberConstants.ZERO.getIntValue(), DESedeKeyLength) != (DESedeKeyLength)) {
            throw new ApplicationException("Error while converting keyStrings to byte[] ");
        }

        final SecretKey desEdeKey = new SecretKeySpec(keyBytes, CryptoConstants.DES_EDE_ALG.getStrValue());
        Cipher desEdeCipher;
        desEdeCipher = Cipher.getInstance(CryptoConstants.DES_EDE_CIP_PARAMS.getStrValue());
        final IvParameterSpec ivSpec1 = new IvParameterSpec(IV_PARAMS);
        desEdeCipher.init(Cipher.ENCRYPT_MODE, desEdeKey, ivSpec1);
        cipherText = desEdeCipher.doFinal(plainTextBytes);
        return bytesToBinHex(cipherText);
    }

    /**
     * Decrypt the encrypt value.
     *
     * @param cipherText
     *            - cipherText
     * @param myKeyString
     *            - myKeyString
     * @return String
     * @throws GeneralSecurityException
     *             - GeneralSecurityException
     */

    public static String decrypt(String cipherText, String myKeyString) throws GeneralSecurityException {
        String keyString = myKeyString.toLowerCase();
        
        final byte[] cipherBytes = new byte[cipherText.length() / NumberConstants.TWO.getIntValue()];
        final byte[] keyBytes = new byte[DESedeKeyLength];
        byte[] plainText = new byte[cipherBytes.length];
        if ((keyString.length() / NumberConstants.TWO.getIntValue() != NumberConstants.N16.getIntValue())
                && (keyString.length() / NumberConstants.TWO.getIntValue() != NumberConstants.N24.getIntValue())) {
            throw new ApplicationException(
                    "Wrong key Size  (" + keyString.length() + "): Should be a 32 or 48 characters string");
        } else if (keyString.length() / NumberConstants.TWO.getIntValue() == NumberConstants.N16.getIntValue()) {
            keyString = keyString.concat(keyString.substring(NumberConstants.ZERO.getIntValue(), KEY_STRING_MAX_LENGTH));
        }

        if (binHexToBytes(cipherText.toLowerCase(), cipherBytes, 0, cipherText.length()
                / NumberConstants.TWO.getIntValue()) != (cipherText.length() / NumberConstants.TWO.getIntValue())) {
            throw new ApplicationException("decrypt - Error while converting cipherText to byte[] ");
        }
        if (binHexToBytes(keyString, keyBytes, NumberConstants.ZERO.getIntValue(), DESedeKeyLength) != (DESedeKeyLength)) {
            throw new ApplicationException("decrypt - Error while converting keyStrings to byte - binHexToBytes ");
        }

        final SecretKey desEdeKey = new SecretKeySpec(keyBytes, CryptoConstants.DES_EDE_ALG.getStrValue());
        Cipher desEdeCipher;
        desEdeCipher = Cipher.getInstance(CryptoConstants.DES_EDE_CIP_PARAMS.getStrValue());
        final IvParameterSpec ivSpec1 = new IvParameterSpec(IV_PARAMS);
        desEdeCipher.init(Cipher.DECRYPT_MODE, desEdeKey, ivSpec1);
        plainText = desEdeCipher.doFinal(cipherBytes);
        return new String(plainText, 0, plainText.length);
    }

    /**
     * Generate the Hash with SHA2.
     *
     * @param message
     *            - message
     * @return String
     */
    public static String getHashSHA2(String message) {
        try {
            byte[] buffer = message.getBytes(Charset.defaultCharset());
            MessageDigest md = MessageDigest.getInstance(CryptoConstants.SHA256_ALG.getStrValue());
            md.update(buffer);
            byte[] digest = md.digest();
            StringBuilder hexValue = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                int b = digest[i] & 0xff;
                if (Integer.toHexString(b).length() == 1) {
                    hexValue.append(NumberConstants.S0.getStrValue());
                }
                hexValue.append(Integer.toHexString(b));
            }
            return hexValue.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Exception occurs generate the HashSH2: {}", e);
        }
        return null;
    }

    /**
     * converts a binhex string back into a byte array (invalid codes will be
     * skipped).
     *
     * @param sBinHex
     *            binhex string
     * @param data
     *            the target array
     * @param nSrcPos
     *            from which character in the string the conversion should
     *            begin, remember that (nSrcPos modulo 2) should equals 0
     *            normally
     * @param nNumOfBytes
     *            number of bytes to extract
     * @return number of extracted bytes
     */
    private static int binHexToBytes(String sBinHex, byte[] data, int nSrcPos, int nNumOfBytes) {
        int nDstPos = CryptoConstants.INT_INIT.getIntValue();
        int nStrLen = sBinHex.length();
        int nAvailBytes = (nStrLen - nSrcPos) >> 1;
        nNumOfBytes = getNumOfBytesByAvailBytes(nAvailBytes, nNumOfBytes);
        int nOutputCapacity = data.length;
        nNumOfBytes = getnNumOfBytesByOutputCapacity(nOutputCapacity, nNumOfBytes);
        int nResult = 0;
        for (int nI = 0; nI < nNumOfBytes; nI++) {
            byte bActByte = CryptoConstants.BYTE_INIT.getByteValue();
            for (int nJ = 0; nJ < NumberConstants.TWO.getIntValue(); nJ++) {
                bActByte <<= NumberConstants.FOUR.getIntValue();
                char cActChar = sBinHex.charAt(nSrcPos);
                nSrcPos++;
                bActByte = getByteValue(bActByte, cActChar);
            }
            data[nDstPos] = bActByte;
            nDstPos++;
            nResult++;
        }
        return nResult;
    }

    /**
     * Gets the byte value.
     *
     * @param bActByte
     *            the b act byte
     * @param cActChar
     *            the c act char
     * @return the byte value
     */
    private static byte getByteValue(byte bActByte, char cActChar) {
        if ((cActChar >= CryptoConstants.CHARSA.getCharValue())
                && (cActChar <= CryptoConstants.CHARSF.getCharValue())) {
            bActByte |= (byte) (cActChar - CryptoConstants.CHARSA.getCharValue()) + NumberConstants.N10.getIntValue();
        } else if ((cActChar >= CryptoConstants.CHARCA.getCharValue())
                && (cActChar <= CryptoConstants.CHARCF.getCharValue())) {
            bActByte |= (byte) (cActChar - CryptoConstants.CHARCA.getCharValue()) + NumberConstants.N10.getIntValue();
        } else {
            if ((cActChar >= CryptoConstants.CHAR0.getCharValue())
                    && (cActChar <= CryptoConstants.CHAR9.getCharValue())) {
                bActByte |= (byte) (cActChar - CryptoConstants.CHAR0.getCharValue());
            }
        }
        return bActByte;
    }

    /**
     * Gets the num of bytes by avail bytes.
     *
     * @param nAvailBytes
     *            the n avail bytes
     * @param nNumOfBytes
     *            the n num of bytes
     * @return the num of bytes by avail bytes
     */
    private static int getNumOfBytesByAvailBytes(int nAvailBytes, int nNumOfBytes) {
        if (nAvailBytes < nNumOfBytes) {
            nNumOfBytes = nAvailBytes;
        }
        return nNumOfBytes;
    }

    /**
     * Gets the n num of bytes by output capacity.
     *
     * @param nOutputCapacity
     *            the n output capacity
     * @param nNumOfBytes
     *            the n num of bytes
     * @return the n num of bytes by output capacity
     */
    private static int getnNumOfBytesByOutputCapacity(int nOutputCapacity, int nNumOfBytes) {
        if (nNumOfBytes > nOutputCapacity) {
            nNumOfBytes = nOutputCapacity;
        }
        return nNumOfBytes;
    }

    public static String bytesToBinHex(byte[] data) {
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
    public static String bytesToBinHex(byte[] data, int nStartPos, int nNumOfBytes) {
        final StringBuilder sbuf = new StringBuilder();
        sbuf.setLength(nNumOfBytes << 1);

        int nPos = 0;
        for (int nI = 0; nI < nNumOfBytes; nI++) {
            sbuf.setCharAt(nPos, HEXTAB[(data[nI + nStartPos] >> NumberConstants.FOUR.getIntValue()) & 0x0f]);
            nPos = nPos + 1;
            sbuf.setCharAt(nPos, HEXTAB[data[nI + nStartPos] & 0x0f]);
            nPos = nPos + 1;
        }
        return sbuf.toString();
    }

}
