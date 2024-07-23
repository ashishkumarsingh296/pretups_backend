package com.utils;

import com.classes.CONSTANT;
import com.dbrepository.DBHandler;
import com.utils.AESEncryption.AESEncryptionUtil;

import simulator.decryptutility.BTSLUtil;

/**
 * @author lokesh.kontey
 * @see this class is created to Decrypt PIN / Password
 * @returns Decrypted value
 */
public class Decrypt {
	
    static String dec;
    static String enc;
    
    	public static String decryption(String encryptedValue){
    			Log.info("Encrypted value is :: "+encryptedValue);
    			dec = BTSLUtil.main(encryptedValue);
    			Log.info("Decrypted value returned as: "+ dec);
    			return dec;
    		}
    	
    	public static String encryption(String decryptedValue){
			Log.info("Decrypted value is :: " + decryptedValue);
			enc = BTSLUtil.encryptText(decryptedValue);
			Log.info("Encrypted value returned as: "+ enc);
			return enc;
		}
    	
    	public static String APIEncryption(String decryptedValue) {
    		Log.info("Decrypted value is :: " + decryptedValue);
    		
    		int EncryptionType = Integer.parseInt(_masterVO.getClientDetail("ENCRYPTION_TYPE"));
			if (EncryptionType == 0)
				Log.debug("Encryption Type found as 0 :: General Encryption Applies.");
			else if (EncryptionType == 1) 
				Log.debug("Encryption Type found as 1 :: IDEA Based Encryption Applies.");
			else {
				EncryptionType = 0;
				Log.debug("Invalid Encryption Type Found in Client Lib :: General Encryption Applies" );
			}
			enc = BTSLUtil.APIencrypt(decryptedValue, EncryptionType);
			Log.info("Encrypted value returned as: "+ enc);
			return enc;		
    	}
    	
    	//For VMS only
    	public static String decryptionVMS(String encryptedValue){
    		if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMSPIN_EN_DE_CRYPTION_TYPE).equalsIgnoreCase("DES"))
			{Log.info("Encrypted value[DES] is :: "+encryptedValue);
			dec = BTSLUtil.main(encryptedValue);
			Log.info("Decrypted value returned as: "+ dec);}
    		
    		else if(DBHandler.AccessHandler.getSystemPreference(CONSTANT.VMSPIN_EN_DE_CRYPTION_TYPE).equalsIgnoreCase("AES"))
			{Log.info("Encrypted value[AES] is :: "+encryptedValue);
			dec = new AESEncryptionUtil().DecryptAES(encryptedValue);
			Log.info("Decrypted value returned as: "+ dec);}
    		else{
    			Log.info("Invalid Encryption Type Found in Client Lib :: General Encryption Applies");
    		}
    		
			return dec;
		}
    	
}
