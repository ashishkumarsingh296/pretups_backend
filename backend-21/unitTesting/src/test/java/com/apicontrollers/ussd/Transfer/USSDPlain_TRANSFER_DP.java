package com.apicontrollers.ussd.Transfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class USSDPlain_TRANSFER_DP extends CaseMaster {

	static String masterSheetPath;
	static int sheetRowCounter;
	//public static HashMap<String, String> c2cMap = new HashMap<>();
	
	public static String FROM_Category = null;
	public static String FROM_TCPName = null;
	public static String FROM_Domain = null;
	public static String TO_Category = null;
	public static String TO_TCPName = null;
	public static String TO_TCP_ID = null;
	public static String TO_Domain = null;
	public static String ProductCode = null;
	public static String ProductName = null;
	public static String LoginID = null;
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		int dataRowCounter = 0;
		String channelUserCategory = null;
		masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();
		int userCounter;
		
		String transactionType = _masterVO.getProperty("C2CTransferCode");
		for (userCounter = 1; userCounter <= sheetRowCounter; userCounter++) {
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, userCounter);
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, userCounter);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			ArrayList<String> gatewayList = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
			String fromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, userCounter);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userCounter);
			if (aList.contains(transactionType) && gatewayList.contains("USSD") && !fromCategory.equals(toCategory)) {
				break;
			}
		}
		
		String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userCounter);
		String fromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, userCounter);
		
		
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();

		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			String sheetCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
			if (sheetCategory.equals(fromCategory)) {
				FROM_Category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
				FROM_Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, userDetailsCounter);
				FROM_TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, userDetailsCounter);
				
				apiData.put("FROM_CATEGORY", FROM_Category);
				apiData.put(transferAPI.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
				apiData.put(transferAPI.PIN, ExcelUtility.getCellData(0, ExcelI.PIN, userDetailsCounter));
				break;
			}
		}
		
		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			String sheetCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
			if (sheetCategory.equals(toCategory)) {
				TO_Category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
				TO_Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, userDetailsCounter);
				TO_TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, userDetailsCounter);
				TO_TCP_ID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, userDetailsCounter);
				apiData.put("TO_CATEGORY", TO_Category);
			apiData.put(transferAPI.MSISDN2, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
			break;
		}
		}

		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1);
		apiData.put(transferAPI.PRODUCTCODE, ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, 1));
		
		apiData.put(transferAPI.TOPUPVALUE, "200");
		apiData.put(transferAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		
		return apiData;
	}
	
}
