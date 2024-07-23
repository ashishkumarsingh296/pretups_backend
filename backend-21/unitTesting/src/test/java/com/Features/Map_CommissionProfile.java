package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.CONSTANT;
import com.classes.UniqueChecker;
import com.commons.PretupsI;
import com.pageobjects.networkadminpages.commissionprofile.AddCommissionProfileDetailsPage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils._masterVO;

public class Map_CommissionProfile {
	public WebDriver driver;
	NetworkAdminHomePage homePage;
	AddCommissionProfileDetailsPage addProfileDetailsPage;
	
	
	public Map_CommissionProfile(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		addProfileDetailsPage = new AddCommissionProfileDetailsPage(driver);
	
	}
	
	public Map<String, String> DataMap_CommissionProfile() {
		Map<String, String> dataMap = new HashMap<>();
		
		int p = Integer.parseInt(_masterVO.getProperty("MaxTransferValue"));
		int r = CONSTANT.COMM_SLAB_COUNT;
		//int r =5;
		
		int q = p / r;
		dataMap.put("A0", "1");
		for (int i=1; i<=r; i++) {
			dataMap.put("A" + i, "" + (q*i));
		}
		
		int s = CONSTANT.ADDCOMM_SLAB_COUNT;
		//int s = 5;
		int t = p / s;
		dataMap.put("B0", "1");
		for (int i=1; i<=s; i++) {
			dataMap.put("B" + i, "" + (t*i));
		}
		
		dataMap.put("ProfileName",UniqueChecker.UC_CPName());
		dataMap.put("ShortCode",UniqueChecker.UC_CPName());
		dataMap.put("CommMultipleOf",(_masterVO.getProperty("CommMultipleOf")));
		dataMap.put("MintransferValue",_masterVO.getProperty("MintransferValue"));
		dataMap.put("MaxtransferValue",_masterVO.getProperty("MaxTransferValue"));
		dataMap.put("BaseTimeSlab",_masterVO.getProperty("BaseTimeSlab"));
		dataMap.put("MintransferValue", _masterVO.getProperty("MintransferValue"));
		dataMap.put("MaxTransferValue1",_masterVO.getProperty("MaxTransferValue1"));
		dataMap.put("TimeSlab",_masterVO.getProperty("TimeSlab"));
		dataMap.put("GatewayCode",PretupsI.GATEWAY_TYPE_ALL);
		dataMap.put("Geography", _masterVO.getProperty("GeographicalDomain"));
		dataMap.put("slabCount",String.valueOf(CONSTANT.COMM_SLAB_COUNT));
		dataMap.put("AddSlabCount",String.valueOf(CONSTANT.ADDCOMM_SLAB_COUNT));
		dataMap.put("taxTypePct", (_masterVO.getProperty("TaxTypePCT")));
		dataMap.put("taxTypeAmt", (_masterVO.getProperty("TaxTypeAMT")));
		dataMap.put("taxRate", (_masterVO.getProperty("TaxRate")));
		dataMap.put("taxRateAmt", (_masterVO.getProperty("TaxRate")));
		dataMap.put("commRate", (_masterVO.getProperty("CommissionRateSlab0")));
		dataMap.put("addcommrate1", (_masterVO.getProperty("CommissionRateSlab0")));
		dataMap.put("ServiceCode",PretupsI.SERVICE_CODE_CUSTOMER_RECHARGE );
		
				
		return dataMap;

	}
	
	public String getCommMap(String Key) {	
		Map<String, String> instanceMap = DataMap_CommissionProfile();
		return instanceMap.get(Key);
}
	
	public Map<String, String> setCommMap(String[] Key, String[] Value) {	
		int x= Key.length;
		Map<String, String> instanceMap = DataMap_CommissionProfile();
		for(int i=0;i<x;i++){
		instanceMap.put(Key[i], Value[i]);}
		return instanceMap;
}

}
