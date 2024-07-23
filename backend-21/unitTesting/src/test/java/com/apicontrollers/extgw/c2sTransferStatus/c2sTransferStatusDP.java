package com.apicontrollers.extgw.c2sTransferStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class c2sTransferStatusDP {
	
	
	
	public static String LoginID = null;
	
	
	public static HashMap<String, String> getAPIdata() throws Exception {
	
	/*
	 * Variable Declaration
	 */
	HashMap<String, String> apiData = new HashMap<String, String>();
	EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
	c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
	
	String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
	String channelUserCategory = null;
	int dataRowCounter = 0;

	/*
	 * Object Declaration
	 */
	RandomGeneration RandomGeneration = new RandomGeneration();

	/*
	 * Variable initializations
	 */
	apiData.put(c2stransferStatusAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));

	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
	dataRowCounter = ExcelUtility.getRowCount();
	for (int i=0; i <= dataRowCounter; i++) {
		String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
		ArrayList<String> alist = new ArrayList<String>(Arrays.asList(categoryServices.split("[ ]*,[ ]*")));
		String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
		ArrayList<String> alist1 = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
		if (alist.contains(CustomerRechargeCode)&& alist1.contains("EXTGW")) {
			channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			break;
		}
	}

	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	dataRowCounter = ExcelUtility.getRowCount();
	for (int i = 0; i<=dataRowCounter;i++) {
		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		
		if (excelCategory.equals(channelUserCategory)) {
			apiData.put(c2stransferStatusAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
			//apiData.put(C2STransferAPI.PIN, Decrypt.APIEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
			apiData.put(c2stransferStatusAPI.PIN, (ExcelUtility.getCellData(0, ExcelI.PIN, i)));
			LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			apiData.put(c2stransferStatusAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
			//apiData.put(C2STransferAPI.PASSWORD, Decrypt.APIEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
			apiData.put(c2stransferStatusAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
			apiData.put(c2stransferStatusAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
			break;
		}
	}
	apiData.put(c2stransferStatusAPI.EXTREFNUM, RandomGeneration.randomNumeric(10));
	apiData.put(c2stransferStatusAPI.DATE, _APIUtil.getCurrentTimeStamp());
	apiData.put(c2stransferStatusAPI.AMOUNT, "100");
	apiData.put(c2stransferStatusAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
	apiData.put(c2stransferStatusAPI.TXNID,null);
	
	return apiData;
}
	
	
	
	
			
		
		public static HashMap<String, String> getPPBAPIdata() throws Exception {
		
		/*
		 * Variable Declaration
		 */
			
		HashMap<String, String> apiData = new HashMap<String, String>();
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		
		String PostpaidBillPaymentCode = _masterVO.getProperty("PostpaidBillPaymentCode");
		String channelUserCategory = null;
		int dataRowCounter = 0;

		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */
		apiData.put(c2stransferStatusAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i=0; i <= dataRowCounter; i++) {
			String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> alist = new ArrayList<String>(Arrays.asList(categoryServices.split("[ ]*,[ ]*")));
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
			ArrayList<String> alist1 = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
			if (alist.contains(PostpaidBillPaymentCode)&& alist1.contains("EXTGW")) {
				channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				break;
			}
		}

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i = 0; i<=dataRowCounter;i++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			
			if (excelCategory.equals(channelUserCategory)) {
				apiData.put(c2stransferStatusAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				//apiData.put(C2STransferAPI.PIN, Decrypt.APIEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
				apiData.put(c2stransferStatusAPI.PIN, (ExcelUtility.getCellData(0, ExcelI.PIN, i)));
				LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				apiData.put(c2stransferStatusAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
				//apiData.put(C2STransferAPI.PASSWORD, Decrypt.APIEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
				apiData.put(c2stransferStatusAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
				apiData.put(c2stransferStatusAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
				break;
			}
		}
		apiData.put(c2stransferStatusAPI.EXTREFNUM, RandomGeneration.randomNumeric(10));
		apiData.put(c2stransferStatusAPI.DATE, _APIUtil.getCurrentTimeStamp());
		apiData.put(c2stransferStatusAPI.AMOUNT, "100");
		apiData.put(c2stransferStatusAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		apiData.put(c2stransferStatusAPI.TXNID,null);
		
		return apiData;
	}

	


		
		
		public static HashMap<String, String> getGRCAPIdata() throws Exception {
			
			/*
			 * Variable Declaration
			 */
				
			HashMap<String, String> apiData = new HashMap<String, String>();
			c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
			
			String GiftRechargeCode = _masterVO.getProperty("GiftRechargeCode");
			String channelUserCategory = null;
			int dataRowCounter = 0;

			/*
			 * Object Declaration
			 */
			RandomGeneration RandomGeneration = new RandomGeneration();

			/*
			 * Variable initializations
			 */
			apiData.put(c2stransferStatusAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));

			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
			dataRowCounter = ExcelUtility.getRowCount();
			for (int i=0; i <= dataRowCounter; i++) {
				String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				ArrayList<String> alist = new ArrayList<String>(Arrays.asList(categoryServices.split("[ ]*,[ ]*")));
				String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
				ArrayList<String> alist1 = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
				if (alist.contains(GiftRechargeCode)&& alist1.contains("EXTGW")) {
					channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
					break;
				}
			}

			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			dataRowCounter = ExcelUtility.getRowCount();
			for (int i = 0; i<=dataRowCounter;i++) {
				String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				
				if (excelCategory.equals(channelUserCategory)) {
					apiData.put(c2stransferStatusAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
					//apiData.put(C2STransferAPI.PIN, Decrypt.APIEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
					apiData.put(c2stransferStatusAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
					LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
					apiData.put(c2stransferStatusAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
					//apiData.put(C2STransferAPI.PASSWORD, Decrypt.APIEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
					apiData.put(c2stransferStatusAPI.PASSWORD, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
					apiData.put(c2stransferStatusAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
					break;
				}
			}
			apiData.put(c2stransferStatusAPI.EXTREFNUM, RandomGeneration.randomNumeric(10));
			apiData.put(c2stransferStatusAPI.DATE, _APIUtil.getCurrentTimeStamp());
			apiData.put(c2stransferStatusAPI.AMOUNT, "100");
			apiData.put(c2stransferStatusAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
			apiData.put(c2stransferStatusAPI.TXNID,null);
			
			return apiData;
		}	
	
}
