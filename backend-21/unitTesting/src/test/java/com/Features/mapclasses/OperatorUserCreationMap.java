package com.Features.mapclasses;

import java.util.HashMap;
import java.util.Map;

import com.classes.UniqueChecker;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class OperatorUserCreationMap {
	
	String[] domainData;
	public String LoginID;
	
	public Map<String, String> defaultMap() {
		
		UniqueChecker uniqueChecker = new UniqueChecker();
		RandomGeneration randStr = new RandomGeneration();
		
		Map<String, String> paraMeterMap = new HashMap<>();
		domainData = uniqueChecker.UC_DomainData();
		
		paraMeterMap.put("firstName", "AUTFN" + randStr.randomNumeric(4));
		paraMeterMap.put("lastName", "AUTLN" + randStr.randomNumeric(4));
		paraMeterMap.put("UserName", paraMeterMap.get("firstName") + " " + paraMeterMap.get("lastName"));
		paraMeterMap.put("shortName", "AUTSN" + randStr.randomNumeric(4));
		paraMeterMap.put("subscriberCode", ""+ randStr.randomNumeric(6));
		paraMeterMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		paraMeterMap.put("EXTCODE", UniqueChecker.UC_EXTCODE());
		paraMeterMap.put("contactNo", "" + randStr.randomNumeric(6));
		paraMeterMap.put("address1","Add1" + randStr.randomNumeric(4));
		paraMeterMap.put("address2","Add2" + randStr.randomNumeric(4));
		paraMeterMap.put("city", "City" + randStr.randomNumeric(4));
		paraMeterMap.put("state","State" + randStr.randomNumeric(4));
		paraMeterMap.put("country","Country"
				+ randStr.randomNumeric(2));
		paraMeterMap.put("email",randStr.randomAlphaNumeric(5)
				.toLowerCase() + "@mail.com");
		paraMeterMap.put("state","State" + randStr.randomNumeric(4));
		LoginID = UniqueChecker.UC_LOGINID();
		paraMeterMap.put("LOGINID",LoginID);
		paraMeterMap.put("PIN", _masterVO.getProperty("PIN"));
		String PASSWORD = _masterVO.getProperty("Password");
		String CONFIRMPASSWORD = _masterVO.getProperty("ConfirmPassword");
		paraMeterMap.put("PASSWORD",PASSWORD);
		paraMeterMap.put("CONFIRMPASSWORD",CONFIRMPASSWORD);
		paraMeterMap.put("DIVISION","Y");
		paraMeterMap.put("AssignNetwork","Y");
		paraMeterMap.put("AssignGeography","Y");
		paraMeterMap.put("AssignPhoneNumber","Y");
		paraMeterMap.put("AssignProduct","Y");
		paraMeterMap.put("AssignDomain","Y");
		return paraMeterMap;
	   }
	
	public Map<String, String> getOperatorUserMap(String Key, String Value) {	
		Map<String, String> instanceMap = defaultMap();
		instanceMap.put(Key, Value);
		return instanceMap;
}


}
