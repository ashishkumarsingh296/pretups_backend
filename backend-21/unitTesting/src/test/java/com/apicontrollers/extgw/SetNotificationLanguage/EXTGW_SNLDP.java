package com.apicontrollers.extgw.SetNotificationLanguage;

import java.util.HashMap;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGW_SNLDP {

	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_SNLAPI setNotificationLanguage = new EXTGW_SNLAPI();
		int dataRowCounter = 0;
		String channelUserCategory = null;
		
		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */

		apiData.put(setNotificationLanguage.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i <= dataRowCounter; i ++) {
			String EXTGWStatus = ExcelUtility.getCellData(0, "EXTGW", i);
			if (EXTGWStatus.equalsIgnoreCase("Y")) {
				channelUserCategory = ExcelUtility.getCellData(0, "Category Users", i);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i<=dataRowCounter;i++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			
				apiData.put(setNotificationLanguage.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, 1));
				apiData.put(setNotificationLanguage.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, 1)));
				break;
			
		}
		
		apiData.put(setNotificationLanguage.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		
		return apiData;
	}
}
