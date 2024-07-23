package com.apicontrollers.extgw.c2ctransfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWPlainC2CDP {

	static String masterSheetPath;
	static int sheetRowCounter;
	public static HashMap<String, String> c2cMap = new HashMap<>();
	
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
	
	public static HashMap<String, String> getAPIdata() {
		final String methodname = "getAPIdata";
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGWPlainC2CAPI C2CTransferAPI = new EXTGWPlainC2CAPI();
		masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();
		int userCounter;
		String domainCode = null;
		String toUser = null;
		String userMSISDN = null;
		
		String transactionType = _masterVO.getProperty("C2CTransferCode");
		apiData.put(C2CTransferAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		for (userCounter = 1; userCounter <= sheetRowCounter; userCounter++) {
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, userCounter);
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, userCounter);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			ArrayList<String> gatewayList = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
			String fromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, userCounter);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userCounter);
			if ((aList.contains(transactionType)||aList.contains(transactionType+"[P]")||
            		aList.contains(transactionType+"[S]")||aList.contains(transactionType+"[O]")||
            		aList.contains(transactionType+"[D]")) && gatewayList.contains("EXTGW") && !fromCategory.equals(toCategory)) {
				break;
			}
		}
		
		String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userCounter);
		String fromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, userCounter);
		
		String FROMUser_WEBAccessStatus = DBHandler.AccessHandler.webInterface(fromCategory);
		String TOUser_WEBAccessStatus = DBHandler.AccessHandler.webInterface(toCategory);
		
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();

		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			String sheetCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
			if (sheetCategory.equals(fromCategory)) {
				FROM_Category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
				FROM_Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, userDetailsCounter);
				FROM_TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, userDetailsCounter);
				if (FROMUser_WEBAccessStatus.equalsIgnoreCase("Y")) {
					String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter);
					apiData.put(C2CTransferAPI.LOGINID, LoginID);
					apiData.put(C2CTransferAPI.PASSWORD, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, userDetailsCounter)));
				}
				
				apiData.put(C2CTransferAPI.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
				apiData.put(C2CTransferAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, userDetailsCounter)));
				apiData.put(C2CTransferAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(apiData.get(C2CTransferAPI.MSISDN1), "EXTERNAL_CODE")[0]);
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
				if (TOUser_WEBAccessStatus.equalsIgnoreCase("Y")) {
					String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter);
					apiData.put(C2CTransferAPI.LOGINID2, LoginID);
				}
				
				apiData.put(C2CTransferAPI.MSISDN2, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
				apiData.put(C2CTransferAPI.EXTCODE2, DBHandler.AccessHandler.getUserDetails(apiData.get(C2CTransferAPI.MSISDN2), "EXTERNAL_CODE")[0]);
				break;
			}
		}
		
		RandomGeneration RandomGeneration = new RandomGeneration();
		apiData.put(C2CTransferAPI.EXTREFNUM, RandomGeneration.randomNumeric(5));
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.PRODUCT_SHEET);
		ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1);
		apiData.put(C2CTransferAPI.PRODUCTCODE, ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, 1));
		ProductName = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, 1);
		apiData.put(C2CTransferAPI.QTY, "200");

		return apiData;
	}
}
