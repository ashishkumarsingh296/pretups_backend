package com.apicontrollers.ussd.AddBuddy;

import java.util.HashMap;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class USSD_AddBuddyDP {
	

	public static String Category = null;
	public static String CrdGrp = null;
	
	public static HashMap<String, String> getAPIdata() throws Exception {
	

	/*
	 * Variable Declaration
	 */
	HashMap<String, String> apiData = new HashMap<String, String>();
	USSD_AddBuddyAPI AddBuddyAPI = new USSD_AddBuddyAPI();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	String CreditTransferCode = _masterVO.getProperty("CreditTransferCode");
	String ProductType = _masterVO.getProperty("PrepaidProductType");
	String productCode = null;
	String UserCategory = null;
	
	int dataRowCounter = 0;
	
	/*
	 * Object Declaration
	 */
	RandomGeneration RandomGeneration = new RandomGeneration();

	/*
	 * Variable initializations
	 */	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
	dataRowCounter = ExcelUtility.getRowCount();
	for (int i=0; i <= dataRowCounter; i++) {
		String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
		String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
		if (categoryServices.contains(CreditTransferCode) && gatewayType.contains("USSD")) {
			UserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			break;
		}
	}
	
	
	
	
	apiData.put(AddBuddyAPI.PRFAMT, "100");
	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.P2P_SERVICES_SHEET);
	int rowCount = ExcelUtility.getRowCount();
	for (int i = 1; i <= rowCount; i++) {
		String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i);
		String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
		if (service.equals(CreditTransferCode)&& !cardGroupName.isEmpty()) {
			CrdGrp = cardGroupName;
			break;
		}
	}
	
	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
	int rowCount1 = ExcelUtility.getRowCount();
	
	for (int i = 1; i <= rowCount1; i++) {
		String product = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, i);
		
		if (product.equals(ProductType)) {
			productCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			
			break;
		}
	}
	
	
	String MSISDN = DBHandler.AccessHandler.getSubscriberMSISDN(productCode);
	
	
	apiData.put(AddBuddyAPI.MSISDN1, MSISDN);
	
	String MSISDNexists = DBHandler.AccessHandler.checkSubscriberMSISDNexist(MSISDN);
	
	if(MSISDNexists.equalsIgnoreCase("Y")){
	apiData.put(AddBuddyAPI.PIN, Decrypt.decryption(DBHandler.AccessHandler.getSubscriberP2PPin(MSISDN)));
	}
	else
	{
		apiData.put(AddBuddyAPI.PIN,	"1357");
	}
	
	
	
	String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
	
	apiData.put(AddBuddyAPI.MSISDN2, prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));
	apiData.put(AddBuddyAPI.BUDDYNAME,"BUDDY"+RandomGeneration.randomNumeric(3));
	return apiData;
}


}
