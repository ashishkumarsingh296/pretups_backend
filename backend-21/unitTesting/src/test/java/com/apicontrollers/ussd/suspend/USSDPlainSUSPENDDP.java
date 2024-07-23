package com.apicontrollers.ussd.suspend;

import java.util.HashMap;

import com.classes.BaseTest;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils._APIUtil;

public class USSDPlainSUSPENDDP extends BaseTest{
	
	public static int rowNum;
	public static HashMap<String, String> getAPIdata() {
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSDPlainSUSPENDAPI SuspendAPI = new USSDPlainSUSPENDAPI();
		
		String values[] = new String[2];
		values[0] = "MSISDN";
		values[1] = "PIN";
		String p2p_subscriber[] = DBHandler.AccessHandler.getP2PSubscriberWithStatusY(values);
		
		apiData.put(SuspendAPI.MSISDN1,p2p_subscriber[0]);
		String pin = Decrypt.decryption(p2p_subscriber[1]);
		apiData.put(SuspendAPI.PIN,_APIUtil.implementEncryption(pin));
		return apiData;
	}
}
