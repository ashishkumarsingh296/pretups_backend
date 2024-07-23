package com.apicontrollers.extgw.LendMeBalance;

import java.util.HashMap;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class EXTGW_LMB_DP {
	
	
	
	public static String Category = null;
	public static String CrdGrp = null;
	
	public static HashMap<String, String> getAPIdata() throws Exception {
	

	/*
	 * Variable Declaration
	 */
	HashMap<String, String> apiData = new HashMap<String, String>();
	LMB_API LMBAPI = new LMB_API();
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
		if (categoryServices.contains(CreditTransferCode) && gatewayType.contains("EXTGW")) {
			UserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			break;
		}
	}
	
	
	
	
	apiData.put(LMBAPI.AMOUNT, "5");
	
		
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
	int rowCount1 = ExcelUtility.getRowCount();
	
	for (int i = 1; i <= rowCount1; i++) {
		String product = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, i);
		
		if (product.equals(ProductType)) {
			productCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			
			break;
		}
	}
	
	
	//String MSISDN = DBHandler.AccessHandler.getSubscriberMSISDN(productCode);
			
	apiData.put(LMBAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
	apiData.put(LMBAPI.LANGUAGE2, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
	
	
	String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
	//ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	//String msisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, 1);
	apiData.put(LMBAPI.MSISDN1, prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));
	//apiData.put(LMBAPI.MSISDN1, msisdn);
	apiData.put(LMBAPI.CELLID,RandomGeneration.randomNumeric(4));
	apiData.put(LMBAPI.SWITCHID,RandomGeneration.randomNumeric(4));
	
	
	return apiData;
}


}
