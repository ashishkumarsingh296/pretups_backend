/*
 * Created on Jun 18, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.comverse;

/**
 * @author abhay.singh
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

//import java.util.Base64;

public final class PasswordService {
    private static PasswordService instance;

    private PasswordService() {
    }

    public synchronized String encrypt(String nonce, String timestamp, String pwd) // throws
                                                                                   // SystemUnavailableException
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1"); // step 2
        } catch (NoSuchAlgorithmException e) {
            // throw new SystemUnavailableException(e.getMessage());
            e.printStackTrace();
        }
        try {
            md.update(Base64.decodeBase64(nonce.getBytes())); // step 3
            md.update(timestamp.getBytes("UTF-8")); // step 3
            md.update(pwd.getBytes()); // step 3
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte raw[] = md.digest(); // step 4
//        String hash = (new BASE64Encoder()).encode(raw); // step 5
        java.util.Base64.Encoder enc = java.util.Base64.getEncoder();
        String hash =   enc.encodeToString(raw);
        return hash; // step 6
    }

    public static synchronized PasswordService getInstance() // step 1
    {
        if (instance == null) {
            instance = new PasswordService();
        }
        return instance;
    }

    public static void main(String args[]) {
        // System.out.println(PasswordService.getInstance().encrypt("money4fa"));
    }

}
