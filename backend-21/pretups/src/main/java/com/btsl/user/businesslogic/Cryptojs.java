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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import net.iharder.Base64;

/**
 * This class is used for Cryptojs for client side encryption and decryptions.
 *
 * @author VENKATESAN S
 * 
 * @date : 11-02-2020
 */

public final class Cryptojs {

    private static final Logger LOGGER = LoggerFactory.getLogger(Cryptojs.class);

    /**
     * Instantiates a new common utils.
     */
    private Cryptojs() {
    }

    /**
     * Decrypt method
     * 
     * @param encrypted
     *            is a string value
     * @param aeskey
     *            is a string value
     * @return String
     */
    public static String decrypt(String encrypted, String aeskey) {
        try {
            SecretKey key = new SecretKeySpec(Base64.decode(aeskey), "AES");
            AlgorithmParameterSpec iv = new IvParameterSpec(Base64.decode(aeskey));
            byte[] decodeBase64 = Base64.decode(encrypted);
            Cipher cipher = Cipher.getInstance(Constants.CIPHER_INSTANCE.getStrValue());
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(cipher.doFinal(decodeBase64), Constants.UTF_8.getStrValue());
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            LOGGER.debug("Unable to decrypt", e);
            throw new ValidationException(Constants.AUTHENDICATION_ERROR.getStrValue(),
                    MessageCodes.USER_AUTHENDICATION_ERROR.getStrValue());

        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IOException e) {
            LOGGER.debug("Unable to decrypt", e);
            throw new ValidationException(Constants.AUTHENDICATION_ERROR.getStrValue(),
                    MessageCodes.USER_AUTHENDICATION_ERROR.getStrValue());
        }
    }
    
    /**
     * Decrypt method
     * 
     * @param encrypted
     *            is a string value
     * @param aeskey
     *            is a string value
     * @return String
     */
    public static String encrypt(String data, String aeskey) {
        try {
            SecretKey key = new SecretKeySpec(Base64.decode(aeskey), "AES");
            AlgorithmParameterSpec iv = new IvParameterSpec(Base64.decode(aeskey));
            Cipher cipher = Cipher.getInstance(Constants.CIPHER_INSTANCE.getStrValue());
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] decodeBase64 = cipher.doFinal(data.getBytes(Constants.UTF_8.getStrValue()));           
            return Base64.encodeBytes(decodeBase64);       
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            LOGGER.debug("Unable to decrypt", e);
            throw new ValidationException(Constants.AUTHENDICATION_ERROR.getStrValue(),
                    MessageCodes.USER_AUTHENDICATION_ERROR.getStrValue());

        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IOException e) {
            LOGGER.debug("Unable to decrypt", e);
            throw new ValidationException(Constants.AUTHENDICATION_ERROR.getStrValue(),
                    MessageCodes.USER_AUTHENDICATION_ERROR.getStrValue());
        }
    }
}
