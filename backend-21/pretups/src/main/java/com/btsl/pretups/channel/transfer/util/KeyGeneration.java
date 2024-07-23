package com.btsl.pretups.channel.transfer.util;

/*
 * @# KeyGeneration.java
 * This class is utility class for generating random key.
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Mar 25, 2010 Amit Singh Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2010 Comviva Technologies Ltd.
 */

import java.security.SecureRandom;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
public class KeyGeneration {
	private static Log _log = LogFactory.getLog(KeyGeneration.class.getName());
    public static String getGeneratedKey(int keyLength) {
        final String methodName = "getGeneratedKey";
    	final KeyGeneration keyGeneration = new KeyGeneration();
        try {
            Thread.sleep(300);
        } catch (InterruptedException iex) {
        	_log.error(methodName, "");			
        	_log.errorTrace(methodName, iex);
        }
        int count = 0;
        String keyCode = "";
        final char charsUsedToGenerateKey[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        final SecureRandom r = new SecureRandom();
        int randInt = 0;
        for (count = 0; count < keyLength; count++) {
            randInt = Math.abs(r.nextInt(charsUsedToGenerateKey.length));
            keyCode = keyCode + charsUsedToGenerateKey[randInt];
        }
        // validates the integer value of the key. If key value is 16 is invalid
        // for decryption.
        while (keyGeneration.keyValue(keyCode) == 16) {
            keyCode = getGeneratedKey(keyLength);
        }

        return keyCode;
    }

    public int keyValue(String key) {
        int sum = 0, enc_key = 0;
         int keys=key.length();
        for (int i = 0; i <keys ; i++) {
            sum = sum + key.charAt(i);
        }

        while (sum != 0) {
            enc_key = enc_key + sum % 10;
            sum = sum / 10;
        }

        return enc_key;
    }

}
