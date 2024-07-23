package com.selftopup.util;

/**
 * @(#)OneWayHashingAlgoUtil.java
 *                                Copyright(c) 2010, Comviva Technologies
 *                                Limited
 *                                All Rights Reserved
 *                                Action class for interaction between front end
 *                                and backend
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Sanjeev Sharma 09/02/2010 Initial Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;

import java.util.Base64;
import java.util.Base64;

public class OneWayHashingAlgoUtil {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    private static OneWayHashingAlgoUtil instance;
    private static String SHA_TYPE = SystemPreferences.SHA2_FAMILY_TYPE;
    private final static int ITERATION_NUMBER = 100;

    public String encrypt(String plaintext) {
        if (_log.isDebugEnabled())
            _log.debug("encrypt", "Entered: with text to encrypt =" + plaintext);

        MessageDigest md = null;
        String hashMessage = null;
        try {
            // Create a Message Digest from a Factory method
            if (SHA_TYPE != null)
                md = MessageDigest.getInstance(SHA_TYPE);
            else
                md = MessageDigest.getInstance("SHA-256"); // throw
                                                           // NoSuchAlgorithmException

            // Do the transformation of "plaintext": generate an array of bytes
            // using
            // UTF-8 encoding format that represent the digested (encrypted)
            // password value.
            byte[] msg = plaintext.getBytes("UTF-8");

            // Update the message digest with some more bytes
            // This can be performed multiple times before creating the hash
            md.update(msg);// throw UnsupportedEncodingException

            // Create the digest from the message
            byte raw[] = md.digest();
            hashMessage = (new BASE64Encoder()).encode(raw);

            return hashMessage;
        } catch (NoSuchAlgorithmException e) {
            if (_log.isErrorEnabled())
                _log.debug("encrypt", "Error while encrypting the text");

            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            if (_log.isErrorEnabled())
                _log.debug("encrypt", "Error while encrypting the text");

            e.printStackTrace();
            return null;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("encrypt", "Exiting after encrypting the test  =" + hashMessage);
            }
        }
    }

    public String encryptWithSecureRandomSalt(String plaintext, String _saltInfo) {

        if (_log.isDebugEnabled())
            _log.debug("encryptWithSecureRandomSalt", "Entered: with text to encrypt =" + plaintext);

        String hashMessage = null;
        try {

            /*
             * We are using the secure random no for salt & storing this salt
             * into the DB
             * because we need this salt while authenticating user.
             * 
             * We will store the salt value(sSalt, if required) &
             * hashMessage(hashMessage) in DB for authentication.
             */

            // Create the digest from the message
            // Digest computation
            byte[] bDigest = getHash(ITERATION_NUMBER, plaintext, _saltInfo);

            hashMessage = byteToBase64(bDigest);

            return hashMessage; // this hashMessage we will store in DB
        } catch (NoSuchAlgorithmException e) {
            if (_log.isErrorEnabled())
                _log.debug("encryptWithSecureRandomSalt", "Error while encrypting the text");

            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            if (_log.isErrorEnabled())
                _log.debug("encryptWithSecureRandomSalt", "Error while encrypting the text");

            e.printStackTrace();
            return null;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("encryptWithSecureRandomSalt", "Exiting after encrypting the test  =" + hashMessage);
            }
        }
    }

    /**
     * Authenticates the user with a given login and password
     * If password and/or login is null then always returns false.
     * If the user does not exist in the database returns false.
     * 
     * @param con
     *            Connection An open connection to a databse
     * @param login
     *            String The login of the user
     * @param password
     *            String The password of the user
     * @return boolean Returns true if the user is authenticated, false
     *         otherwise
     * @throws SQLException
     *             If the database is inconsistent or unavailable (
     *             (Two users with the same login, salt or digested password
     *             altered etc.)
     * @throws NoSuchAlgorithmException
     *             If the algorithm SHA-1 is not supported by the JVM
     */
    public boolean authenticate(String login, String password, String p_DbPass, String p_SaltInfo) throws Exception, NoSuchAlgorithmException {
        if (_log.isDebugEnabled())
            _log.debug("authenticate", "Entered: to authenticate user with login ID=" + login + ",password = " + password + ",p_DbPass = " + p_DbPass + ",p_SaltInfo = " + p_SaltInfo);

        boolean authenticated = false;
        try {

            // userExist = false;

            byte[] bDigest = base64ToByte(p_DbPass);

            // Compute the new DIGEST
            byte[] proposedDigest = getHash(ITERATION_NUMBER, password, p_SaltInfo);

            authenticated = Arrays.equals(proposedDigest, bDigest);
            return authenticated;
        } catch (Exception ex) {
            if (_log.isDebugEnabled())
                _log.debug("authenticate", "Got exception while authenticating user");
            ex.printStackTrace();

            return false;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("authenticate", "  User authentication is =" + authenticated);
        }
    }

    /**
     * From a base 64 representation, returns the corresponding byte[]
     * 
     * @param data
     *            String The base64 representation
     * @return byte[]
     * @throws IOException
     */
    public static byte[] base64ToByte(String data) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(data);
    }

    /**
     * From a byte[] returns a base 64 representation
     * 
     * @param data
     *            byte[]
     * @return String
     * @throws IOException
     */
    public static String byteToBase64(byte[] data) {
        BASE64Encoder endecoder = new BASE64Encoder();
        return endecoder.encode(data);
    }

    /**
     * From a password, a number of iterations and a salt,
     * returns the corresponding digest
     * 
     * @param iterationNb
     *            int The number of iterations of the algorithm
     * @param password
     *            String The password to encrypt
     * @param salt
     *            byte[] The salt
     * @return byte[] The digested password
     * @throws NoSuchAlgorithmException
     *             If the algorithm SHA-256 or the SecureRandom is not supported
     *             by the JVM
     * @throws UnsupportedEncodingException
     */
    public byte[] getHash(int iterationNb, String password, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // Create a Message Digest from a Factory method
        MessageDigest digest;
        if (SHA_TYPE != null)
            digest = MessageDigest.getInstance(SHA_TYPE);
        else
            digest = MessageDigest.getInstance("SHA-256"); // throw
                                                           // NoSuchAlgorithmException

        digest.reset();

        // Update the message digest with some more bytes
        // This can be performed multiple times before creating the hash
        digest.update(salt.getBytes());// throw UnsupportedEncodingException

        // Do the transformation of "plaintext": generate an array of bytes
        // using
        // UTF-8 encoding format that represent the digested (encrypted)
        // password value.
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < iterationNb; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
    }

    /**
     * Call this function if we required a salt value with user password for
     * hashing.
     * returns the corresponding salt value as String.
     * Advantages of using SALT.
     * * 1. Due to the birthday paradox , the attacker can find a password very
     * quickly.
     * 2. An attacker can use a list of precomputed hashed to break passwords in
     * seconds.
     */
    public String getSalt() {

        /**
         * We are using the secure random number for salt & storing this salt
         * into the DB
         * because we need this salt while authenticating user(if required).
         * We will store the salt value(sSalt, if required) &
         * hashMessage(hashMessage) in DB
         * for authentication.
         */

        SecureRandom random;
        byte[] bSalt = null;

        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            // Salt generation 64 bits long
            bSalt = new byte[8];
            random.nextBytes(bSalt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return byteToBase64(bSalt);// this sSalt will be stored in DB
    }

    public static OneWayHashingAlgoUtil getInstance() {
        if (instance == null) {
            instance = new OneWayHashingAlgoUtil();
        }
        return instance;
    }

    /*
     * public static void main(String[] args)
     * {
     * try
     * {
     * //System.out.println(""+ new
     * OneWayHashingAlgoUtil().getInstance().encrypt("aaaaa"));
     * // System.out.println("\n"+ new
     * OneWayHashingAlgoUtil().getInstance().encryptWithSecureRandomSalt
     * ("test123","JwO18dDs2Zk="));
     * //System.out.println("\n"+ new
     * OneWayHashingAlgoUtil().authenticate("test",
     * "test123","I0X0R4HyqZZLna13n043jGoZUzB4860clyk95wEaqEY="
     * ,"JwO18dDs2Zk="));
     * }
     * catch (NoSuchAlgorithmException e)
     * {
     * e.printStackTrace();
     * }
     * catch (Exception ex)
     * {
     * ex.printStackTrace();
     * }
     * }
     */
}