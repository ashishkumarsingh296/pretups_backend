package com.utils;

import com.commons.SystemPreferences;

/**
 * @author lokesh.kontey
 * @description This class is created to Generate MSISDN.
 * @return Integer
 */
public class GenerateMSISDN {
	
	public int generateMSISDN() {
		
		int remainingMSISDN = SystemPreferences.MAX_MSISDN_LENGTH-SystemPreferences.MSISDN_PREFIX_LENGTH;
		Log.info("GenerateMSISDN class Returns: Remaining MSISDNLength (" + remainingMSISDN + ")");
		return remainingMSISDN;
		
	}
	
}
