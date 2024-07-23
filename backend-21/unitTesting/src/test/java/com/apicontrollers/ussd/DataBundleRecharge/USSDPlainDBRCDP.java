package com.apicontrollers.ussd.DataBundleRecharge;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class USSDPlainDBRCDP extends CaseMaster{
	
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String TCPID = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String ProductName = null;
	public static String LoginID = null;
	public static String CPName = null;
	public static String parentCategory = null;
	public static String grade = null;
	
	public static HashMap<String, String> getAPIdata() throws Exception {	

	/*
	 * Variable Declaration
	 */
	HashMap<String, String> apiData = new HashMap<String, String>();
	USSDPlainDBRCAPI C2STransferAPI = new USSDPlainDBRCAPI();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	String CustomerRechargeCode = _masterVO.getProperty("DataBundleRecharge");
	String channelUserCategory = null;
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
		if (categoryServices.contains(CustomerRechargeCode) && gatewayType.contains("USSD")) {
			channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			break;
		}
	}
	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	dataRowCounter = ExcelUtility.getRowCount();
	for (int i = 0; i<=dataRowCounter;i++) {
		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		if (excelCategory.equals(channelUserCategory)) {
			apiData.put(C2STransferAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
			apiData.put(C2STransferAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
			LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
			TCPID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
			Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			CPName = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
			parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			grade = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
			apiData.put("parentCategory", parentCategory);
			apiData.put("category", CUCategory);
			break;
		}
	}
	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
	ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1);
	ProductName = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, 1);
	apiData.put(C2STransferAPI.AMOUNT, "100");
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
	int rowCount = ExcelUtility.getRowCount();
	for (int i = 1; i <= rowCount; i++) {
		String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i);
		String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
		if (service.equals(CustomerRechargeCode)&& !cardGroupName.isEmpty()) {
			apiData.put(C2STransferAPI.SELECTOR,DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i),service));
			break;
		}
	}
	
	String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
	
	apiData.put(C2STransferAPI.MSISDN2, prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));
	
	return apiData;
}
	
}
