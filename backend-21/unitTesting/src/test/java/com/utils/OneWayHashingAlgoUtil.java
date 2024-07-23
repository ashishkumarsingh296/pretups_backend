package com.utils;

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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;

import com.pretupsControllers.BTSLUtil;

import sun.misc.BASE64Decoder;
/*import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
*/
import sun.misc.BASE64Encoder;

public class OneWayHashingAlgoUtil {

    /**
     * Commons Logging instance.
     */
   // private Log _log = LogFactory.getLog(this.getClass().getName());

    private static OneWayHashingAlgoUtil instance;
    private static String SHA_TYPE = "";//SystemPreferences.SHA2_FAMILY_TYPE;
    private final static int ITERATION_NUMBER = 100;

	public String encrypt(String plaintext) {
		Log.info("Entered: with text to encrypt =" + plaintext);
		MessageDigest md = null;
		String hashMessage = null;
		try {
			if (BTSLUtil.isNullString(SHA_TYPE))
				md = MessageDigest.getInstance("SHA-256");
			else
				md = MessageDigest.getInstance(SHA_TYPE);
			byte[] msg = plaintext.getBytes("UTF-8");
			md.update(msg);
			byte[] bytes = md.digest();

			hashMessage = Base64.getEncoder().encodeToString(bytes);
			/*
			 * StringBuilder sb = new StringBuilder(); for(int i=0; i<
			 * bytes.length ;i++) { sb.append(Integer.toString((bytes[i] & 0xff)
			 * + 0x100, 16).substring(1)); } hashMessage = sb.toString();
			 */

			return hashMessage;
		} catch (NoSuchAlgorithmException e) {
			Log.info("Error while encrypting the text");

			Log.writeStackTrace(e);
			return null;
		} catch (UnsupportedEncodingException e) {
			Log.info("Error while encrypting the text");

			Log.writeStackTrace(e);
			return null;
		} finally {
			Log.info("Exiting after encrypting the test  ="+ hashMessage);
			
		}
	}

    
    
  /*  public String encrypt(String plaintext) {
       
            Log.info("Entered: with text to encrypt =" + plaintext);

        final String METHOD_NAME = "encrypt";
        MessageDigest md = null;
        String hashMessage = null;
        try {
            if (BTSLUtil.isNullString(SHA_TYPE)) {
                md = MessageDigest.getInstance("SHA-256");
            } else {
                md = MessageDigest.getInstance(SHA_TYPE);
            }
            md.update(plaintext.getBytes());
            final byte[] bytes = md.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashMessage = sb.toString();

            return hashMessage;
        } catch (NoSuchAlgorithmException e) {
        	 Log.writeStackTrace(e);
          
            return null;
        } finally {
            Log.info("Exiting after encrypting the test  =" + hashMessage);
            
        }
    }*/

    public String encryptWithSecureRandomSalt(String plaintext, String _saltInfo) {

        Log.info("Entered: with text to encrypt =" + plaintext);
        
        final String METHOD_NAME = "encryptWithSecureRandomSalt";
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
            final byte[] bDigest = getHash(ITERATION_NUMBER, plaintext, _saltInfo);

            hashMessage = byteToBase64(bDigest);

            return hashMessage; // this hashMessage we will store in DB
        } catch (NoSuchAlgorithmException e) {
            Log.writeStackTrace(e);
           
            return null;
        } catch (UnsupportedEncodingException e) {
        	 Log.writeStackTrace(e);
            return null;
        } finally {
            Log.info("Exiting after encrypting the test  =" + hashMessage);
            
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
       
               Log.info("Entered: to authenticate user with login ID=" + login + ",password = " + password + ",p_DbPass = " + p_DbPass + ",p_SaltInfo = " + p_SaltInfo);
        
        final String METHOD_NAME = "authenticate";
        boolean authenticated = false;
        try {

            // userExist = false;

            final byte[] bDigest = base64ToByte(p_DbPass);

            // Compute the new DIGEST
            final byte[] proposedDigest = getHash(ITERATION_NUMBER, password, p_SaltInfo);

            authenticated = Arrays.equals(proposedDigest, bDigest);
            return authenticated;
        } catch (Exception ex) {
            Log.writeStackTrace(ex);

            return false;
        } finally {
            Log.info("User authentication is =" + authenticated);
            
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
        final BASE64Decoder decoder = new BASE64Decoder();
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
        final BASE64Encoder endecoder = new BASE64Encoder();
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
        if (SHA_TYPE != null) {
            digest = MessageDigest.getInstance(SHA_TYPE);
        } else {
            digest = MessageDigest.getInstance("SHA-256"); // throw
            // NoSuchAlgorithmException
        }

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
        final String METHOD_NAME = "getSalt";
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            // Salt generation 64 bits long
            bSalt = new byte[8];
            random.nextBytes(bSalt);
        } catch (NoSuchAlgorithmException e) {
            Log.writeStackTrace(e);
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
     * e._log.errorTrace(METHOD_NAME,e);
     * }
     * catch (Exception ex)
     * {
     * ex.printStackTrace();
     * }
     * }
     */
}