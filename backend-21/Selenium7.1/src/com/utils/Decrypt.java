package com.utils;

import simulator.decryptutility.BTSLUtil;
import com.utils.Log;

/**
 * @author lokesh.kontey
 * @see this class is created to Decrypt PIN / Password
 * @returns Decrypted value
 */
public class Decrypt {
	
    static String[]  pin  = new String[2];
    static String dec;
    
    	public static String decryption(String encryptedValue){
    			Log.info("Encrypted value is :: "+encryptedValue);
    			pin[0] = encryptedValue;
    			dec = BTSLUtil.main(pin);
    			Log.info("Decrypted value returned as: "+ dec);
    			return dec;
    		}
}
