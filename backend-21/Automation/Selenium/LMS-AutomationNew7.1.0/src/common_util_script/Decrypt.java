package common_util_script;

import simulator.decryptutility.BTSLUtil;
import common_util_script.logs;

/**
 * @author lokesh.kontey
 * @see this class is created to Decrypt PIN / Password
 * @returns Decrypted PIN / Password
 */
public class Decrypt {
	
    static String[]  pin  = new String[2];
    static String dec;
    
    	public static String decryption(String PIN){
    			logs.info("Encrypted PIN / Password is :: "+PIN);
    			pin[0] = PIN;
    			dec = BTSLUtil.main(pin);
    			logs.info("Decrypted value returned as: "+ dec);
    			return dec;
    		}
}
