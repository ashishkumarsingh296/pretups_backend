/**
 * 
 */
package com.Features.mapclasses;

import java.util.HashMap;
import java.util.Map;

import com.classes.UniqueChecker;
import com.utils.RandomGeneration;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class ChannelUserMap {

	RandomGeneration randStr = new RandomGeneration();
	
	private Map<String, String> defaultMap() {
		Map<String, String> paraMeterMap = new HashMap<>();
		String prefixFN = "AUTFN";
		paraMeterMap.put("fName", prefixFN + randStr.randomNumeric(4));
		paraMeterMap.put("lName",prefixFN + randStr.randomNumeric(4));
		paraMeterMap.put("uName",paraMeterMap.get("fName") + " "+ paraMeterMap.get("lName"));
		paraMeterMap.put("sName","AUTSN" + randStr.randomNumeric(4));
		paraMeterMap.put("subscriberCode", "" + randStr.randomNumeric(6));
		paraMeterMap.put("EXTCODE", UniqueChecker.UC_EXTCODE());
		paraMeterMap.put("MSISDN",UniqueChecker.UC_MSISDN());
		paraMeterMap.put("selectOutletSubOutlet", "Y");
		paraMeterMap.put("contactNo","" + randStr.randomNumeric(6));
		paraMeterMap.put("address1","Add1" + randStr.randomNumeric(4));
		paraMeterMap.put("address2","Add2" + randStr.randomNumeric(4));
		paraMeterMap.put("city","City" + randStr.randomNumeric(4));
		paraMeterMap.put("state","State" + randStr.randomNumeric(4));
		paraMeterMap.put("country","country" + randStr.randomNumeric(2));
		paraMeterMap.put("emailID",randStr.randomAlphaNumeric(5).toLowerCase()+ "@mail.com");
		paraMeterMap.put("LoginID",UniqueChecker.UC_LOGINID());
		paraMeterMap.put("assignGeography","Y");
		paraMeterMap.put("assignRoles","Y");
		paraMeterMap.put("assignServices","Y");
		paraMeterMap.put("assignProducts", "Y");
		paraMeterMap.put("assgnPhoneNumber", "Y");
		paraMeterMap.put("PASSWORD", _masterVO.getProperty("Password"));
		paraMeterMap.put("CONFIRMPASSWORD", _masterVO.getProperty("ConfirmPassword"));
		paraMeterMap.put("PIN", _masterVO.getProperty("PIN"));	
		paraMeterMap.put("loginChange","Y");
		paraMeterMap.put("searchMSISDN","");
		paraMeterMap.put("pinChange","Y");
		paraMeterMap.put("outSuspend_chk","N");
		paraMeterMap.put("outSuspend_unchk","N");
		paraMeterMap.put("documentType",null);
		paraMeterMap.put("paymentType",null);
		paraMeterMap.put("documentNo",null);
		
		return paraMeterMap;
	}
	
	public Map<String, String> getChannelUserMap(String Key, String Value) {	
			Map<String, String> instanceMap = defaultMap();
			instanceMap.put(Key, Value);
			return instanceMap;
	}
	public Map<String, String> getChannelUserMap(String Key, String Value,String Key1, String Value1) {	
		Map<String, String> instanceMap = defaultMap();
		instanceMap.put(Key, Value);
		instanceMap.put(Key1, Value1);
		if(instanceMap.get("documentType")!=null)
			instanceMap.put("documentNo",randStr.randomNumeric(15));	
		return instanceMap;
}
	public Map<String, String>getDefaultMap() {
		return defaultMap();
	}
}
