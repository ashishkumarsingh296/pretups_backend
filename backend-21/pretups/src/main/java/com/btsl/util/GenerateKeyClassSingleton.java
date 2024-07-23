package com.btsl.util;

/**
 * @(#)GenerateKeyClassSingleton
 *                               Copyright(c) 2008, Bharti Telesoft Int. Public
 *                               Ltd.
 *                               All Rights Reserved
 *                               This class is an utility Class for Pretups
 *                               System to generate encryption key.
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Sanjeev Sharma July 22,2008 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 */

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class GenerateKeyClassSingleton {

    private static Log _log = LogFactory.getLog(GenerateKeyClassSingleton.class.getName());
    private static Key key = null;

    // Constructor must be protected or private to perevent creating new object
    private GenerateKeyClassSingleton() {
    }

    // Generates a symmetric key using AES as algoirthm
    // consider that all check such as null, length & data type is verified
    private static Key GenerateAESKey() {
        if (_log.isDebugEnabled()) {
            _log.debug("GenerateAESKey", " Entering GenerateAESKey");
        }
        final String METHOD_NAME = "GenerateAESKey";
        try {
            final KeyGenerator kg = KeyGenerator.getInstance("AES");
            final SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            final byte[] seeds = "2638aa7b 05e71b54 9348082b 67b47b26 08090a0b 0c0d0e0f 10111213 14151617".getBytes();
            sr.setSeed(seeds);
            kg.init(128, sr);
            final Key k = kg.generateKey();
            if (_log.isDebugEnabled()) {
                _log.debug("GenerateAESKey", " Exit After GenerateAESKey");
            }
            return k;
        } catch (NoSuchAlgorithmException nsae) {
            _log.error("GenerateAESKey", "Exception in getting AES Key:" + nsae.getMessage());
            _log.errorTrace(METHOD_NAME, nsae);
            return null;
        }
    }

    public static Key getKey() {
        if (key == null) {
            key = GenerateAESKey();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getKey", " Exit After getKey");
        }
        return key;
    }

}// end of class