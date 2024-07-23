package com.apicontrollers.extgw.LastTransactionDetails;

import java.util.HashMap;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGW_LastTransactionDP {

	static String masterSheetPath;
	static int sheetRowCounter;
	public static HashMap<String, String> lastTransactionMap = new HashMap<>();
	
	public static HashMap<String, String> getAPIdata() {
		final String methodname = "getAPIdata";
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_LastTransactionAPI lastTransactionAPI = new EXTGW_LastTransactionAPI();
		masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();
		int userCounter;
		
		apiData.put(lastTransactionAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter);
			String MSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter);
			String Pin = ExcelUtility.getCellData(0, ExcelI.PIN, userDetailsCounter);
			apiData.put(lastTransactionAPI.LOGINID, LoginID);
			String Password = ExcelUtility.getCellData(0, ExcelI.PASSWORD, userDetailsCounter);
			apiData.put(lastTransactionAPI.PASSWORD, _APIUtil.implementEncryption(Password));
			apiData.put(lastTransactionAPI.MSISDN, MSISDN);
			apiData.put(lastTransactionAPI.PIN, _APIUtil.implementEncryption(Pin));
			apiData.put(lastTransactionAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
			break;
		}
		return apiData;
	}
}
