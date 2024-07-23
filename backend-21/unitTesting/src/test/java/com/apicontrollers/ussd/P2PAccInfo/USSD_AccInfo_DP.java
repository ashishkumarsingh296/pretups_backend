package com.apicontrollers.ussd.P2PAccInfo;

import java.util.HashMap;

import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class USSD_AccInfo_DP {
	
	
	
	
	public static String Category = null;
	public static String CrdGrp = null;
	
	public static HashMap<String, String> getAPIdata() throws Exception {
	

	/*
	 * Variable Declaration
	 */
	HashMap<String, String> apiData = new HashMap<String, String>();
	USSD_AccInfoAPI AccInfoAPI = new USSD_AccInfoAPI();
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

	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.P2P_SERVICES_SHEET);
	int rowCount = ExcelUtility.getRowCount();
	for (int i = 1; i <= rowCount; i++) {
		String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i);
		String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
		if (service.equals(CreditTransferCode)&& !cardGroupName.isEmpty()) {
			apiData.put(AccInfoAPI.SELECTOR,DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i),service));
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
	
	String MSISDN = DBHandler.AccessHandler.getP2PSubscriberMSISDN("PRE","Y");
	apiData.put(AccInfoAPI.MSISDN1, MSISDN);
	
	return apiData;
}


}
