package com.utils;

import com.commons.MasterI;
import com.commons.PretupsI;
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
	
	public static String generateRandomMSISDNWithinNetwork(String PrefixType) {
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		int remainingMSISDN = SystemPreferences.MAX_MSISDN_LENGTH - SystemPreferences.MSISDN_PREFIX_LENGTH;
		
		if (PrefixType.equalsIgnoreCase(PretupsI.PREPAID_LOOKUP))
			return _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + RandomGeneration.randomNumeric(remainingMSISDN);
		else if (PrefixType.equalsIgnoreCase(PretupsI.POSTPAID_LOOKUP))
			return _masterVO.getMasterValue(MasterI.SUBSCRIBER_POSTPAID_PREFIX) + RandomGeneration.randomNumeric(remainingMSISDN);
		else
			return null;
	}
	
}
