package com.apicontrollers.ussd.resume;

import java.util.HashMap;

import com.classes.BaseTest;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils._APIUtil;

public class USSDPlainRESUMEDP extends BaseTest{
	
	public static int rowNum;
	public static HashMap<String, String> getAPIdata() {
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSDPlainRESUMEAPI SuspendAPI = new USSDPlainRESUMEAPI();
		
		String values[] = new String[2];
		values[0] = "MSISDN";
		values[1] = "PIN";
		String p2p_subscriber[] = DBHandler.AccessHandler.getP2PSubscriberWithStatusS(values);
		
		apiData.put(SuspendAPI.MSISDN1,p2p_subscriber[0]);
		apiData.put(SuspendAPI.TYPE,"RESREQ");
		String pin = Decrypt.decryption(p2p_subscriber[1]);
		apiData.put(SuspendAPI.PIN,_APIUtil.implementEncryption(pin));
		return apiData;
	}
}
