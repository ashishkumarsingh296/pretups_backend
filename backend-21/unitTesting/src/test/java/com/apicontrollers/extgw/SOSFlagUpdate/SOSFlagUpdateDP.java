package com.apicontrollers.extgw.SOSFlagUpdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CAPI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class SOSFlagUpdateDP {

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
	
	public static HashMap<String, String> getC2CAPIdata() {
		final String methodname = "getAPIdata";
		 
		String[] domainForSOS = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE), "DOMAINCODE_FOR_SOS_YABX").split(",");
		List<String> domainList = new ArrayList<String>();
		for(String domainSOS : domainForSOS) {
		String domainName = DBHandler.AccessHandler.fetchDomainName(domainSOS);
		domainList.add(domainName);
		}
		 HashMap<String, String> apiData = new HashMap<String, String>();
		 EXTGWC2CAPI extgwC2CAPI = new EXTGWC2CAPI();
		masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();
		int userCounter;
		String domainCode = null;
		String toUser = null;
		String userMSISDN = null;
		
		String transactionType = _masterVO.getProperty("C2CTransferCode");
		apiData.put(extgwC2CAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		for (userCounter = 1; userCounter <= sheetRowCounter; userCounter++) {
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, userCounter);
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, userCounter);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			ArrayList<String> gatewayList = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
			String fromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, userCounter);
			String fromDomain = ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, userCounter);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userCounter);
			if ((domainList.contains(fromDomain))&&(aList.contains(transactionType)||aList.contains(transactionType+"[P]")||
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
					apiData.put(extgwC2CAPI.LOGINID, LoginID);
					apiData.put(extgwC2CAPI.PASSWORD, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, userDetailsCounter)));
				}
				
				apiData.put(extgwC2CAPI.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
				apiData.put(extgwC2CAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, userDetailsCounter)));
				apiData.put(extgwC2CAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(apiData.get(extgwC2CAPI.MSISDN1), "EXTERNAL_CODE")[0]);
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
					apiData.put(extgwC2CAPI.LOGINID2, LoginID);
				}
				
				apiData.put(extgwC2CAPI.MSISDN2, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
				apiData.put(extgwC2CAPI.EXTCODE2, DBHandler.AccessHandler.getUserDetails(apiData.get(extgwC2CAPI.MSISDN2), "EXTERNAL_CODE")[0]);
				break;
			}
		}
		
		RandomGeneration RandomGeneration = new RandomGeneration();
		apiData.put(extgwC2CAPI.EXTREFNUM, RandomGeneration.randomNumeric(5));
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.PRODUCT_SHEET);
		ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1);
		apiData.put(extgwC2CAPI.PRODUCTCODE, ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, 1));
		ProductName = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, 1);
		apiData.put(extgwC2CAPI.QTY, "100");

		return apiData;
	}
}