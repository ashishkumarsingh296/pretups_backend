package com.apicontrollers.extgw.c2sTransactionEnquiry;

import java.util.ArrayList;
import java.util.Arrays;
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

public class c2sTxnENQ_DP extends CaseMaster{
	
	
	public static String LoginID = null;
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String TCPID = null;
	public static String Domain = null;
	
	
	public static HashMap<String, String> getAPIdata() throws Exception {
	
	/*
	 * Variable Declaration
	 */
	HashMap<String, String> apiData = new HashMap<String, String>();
	c2sTxnENQ_API c2stransENQAPI = new c2sTxnENQ_API();
	
	String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
	String channelUserCategory = null;
	int dataRowCounter = 0;

	/*
	 * Object Declaration
	 */
	RandomGeneration RandomGeneration = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();

	/*
	 * Variable initializations
	 */
	apiData.put(c2stransENQAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));

	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
	dataRowCounter = ExcelUtility.getRowCount();
	for (int i=0; i <= dataRowCounter; i++) {
		String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
		ArrayList<String> alist = new ArrayList<String>(Arrays.asList(categoryServices.split("[ ]*,[ ]*")));
		String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
		ArrayList<String> alist1 = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
		if (alist.contains(CustomerRechargeCode)&& alist1.contains("XMLGW")) {
			channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			break;
		}
	}

	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	dataRowCounter = ExcelUtility.getRowCount();
	for (int i = 0; i<=dataRowCounter;i++) {
		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		
		if (excelCategory.equals(channelUserCategory)) {
			apiData.put(c2stransENQAPI.SENDERMSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
			//apiData.put(C2STransferAPI.PIN, Decrypt.APIEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
			apiData.put(c2stransENQAPI.PIN, (ExcelUtility.getCellData(0, ExcelI.PIN, i)));
			LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			
			CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			
			TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
			TCPID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
			Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			//apiData.put(C2STransferAPI.PASSWORD, Decrypt.APIEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
			//apiData.put(c2stransENQAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
			//apiData.put(c2stransENQAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
			break;
		}
	}
	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
	int rowCounter = ExcelUtility.getRowCount();
	for (int k = 0; k<=rowCounter;k++) {
		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k);
		String UserCategory  = "CCE";
		
		if (excelCategory.equals(UserCategory)) {
			apiData.put(c2stransENQAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k));
			apiData.put(c2stransENQAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, k)));
			apiData.put(c2stransENQAPI.CATCODE,ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k));
			apiData.put(c2stransENQAPI.EMPCODE,DBHandler.AccessHandler.getEmpCode(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k)));
			
		}
	}
	
	
	
	apiData.put(c2stransENQAPI.EXTREFNUM, RandomGeneration.randomNumeric(10));
	
	
	apiData.put(c2stransENQAPI.DATE, _APIUtil.getCurrentTimeStamp());
	apiData.put(c2stransENQAPI.FROMDATE,_APIUtil.getCurrentTimeStampXML());
	apiData.put(c2stransENQAPI.TODATE, _APIUtil.getCurrentTimeStampXML());
	apiData.put(c2stransENQAPI.SRVTYPE,_masterVO.getProperty("CustomerRechargeCode"));
	apiData.put(c2stransENQAPI.AMOUNT, "100");
	
	apiData.put(c2stransENQAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
	apiData.put(c2stransENQAPI.TRANSACTIONID,null);
	
	return apiData;
}
	
	
	

	
	
	
	public static HashMap<String, String> getAPIdataForPPB() throws Exception {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		c2sTxnENQ_API c2stransENQAPI = new c2sTxnENQ_API();
		
		String PostpaidBillPaymentCode = _masterVO.getProperty("PostpaidBillPaymentCode");
		String channelUserCategory = null;
		int dataRowCounter = 0;

		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		/*
		 * Variable initializations
		 */
		apiData.put(c2stransENQAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i=0; i <= dataRowCounter; i++) {
			String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> alist = new ArrayList<String>(Arrays.asList(categoryServices.split("[ ]*,[ ]*")));
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
			ArrayList<String> alist1 = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
			if (alist.contains(PostpaidBillPaymentCode)&& alist1.contains("XMLGW")) {
				channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				break;
			}
		}

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i = 0; i<=dataRowCounter;i++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			
			if (excelCategory.equals(channelUserCategory)) {
				apiData.put(c2stransENQAPI.SENDERMSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				//apiData.put(C2STransferAPI.PIN, Decrypt.APIEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
				apiData.put(c2stransENQAPI.PIN, (ExcelUtility.getCellData(0, ExcelI.PIN, i)));
				LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				
				CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
				
				TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
				TCPID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
				Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				//apiData.put(C2STransferAPI.PASSWORD, Decrypt.APIEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
				//apiData.put(c2stransENQAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
				//apiData.put(c2stransENQAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCounter = ExcelUtility.getRowCount();
		for (int k = 0; k<=rowCounter;k++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k);
			String UserCategory  = "CCE";
			
			if (excelCategory.equals(UserCategory)) {
				apiData.put(c2stransENQAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k));
				apiData.put(c2stransENQAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, k)));
				apiData.put(c2stransENQAPI.CATCODE,ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k));
				apiData.put(c2stransENQAPI.EMPCODE,DBHandler.AccessHandler.getEmpCode(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k)));
				
			}
		}
		
		
		
		apiData.put(c2stransENQAPI.EXTREFNUM, RandomGeneration.randomNumeric(10));
		
		
		apiData.put(c2stransENQAPI.DATE, _APIUtil.getCurrentTimeStamp());
		apiData.put(c2stransENQAPI.FROMDATE, _APIUtil.getCurrentTimeStampXML());
		apiData.put(c2stransENQAPI.TODATE, _APIUtil.getCurrentTimeStampXML());
		apiData.put(c2stransENQAPI.SRVTYPE,PostpaidBillPaymentCode);
		apiData.put(c2stransENQAPI.AMOUNT, "100");
		apiData.put(c2stransENQAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		apiData.put(c2stransENQAPI.TRANSACTIONID,null);
		
		return apiData;
	}
	

}
