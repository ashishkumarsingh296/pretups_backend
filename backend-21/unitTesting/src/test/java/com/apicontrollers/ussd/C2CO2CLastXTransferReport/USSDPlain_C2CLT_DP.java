package com.apicontrollers.ussd.C2CO2CLastXTransferReport;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class USSDPlain_C2CLT_DP extends CaseMaster  {

	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSDPlain_C2CLT_API c2cLastTransfer = new USSDPlain_C2CLT_API();
		int dataRowCounter = 0;
		String channelUserCategory = null;
		
		/*
		 * Variable initializations
		 */
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i <= dataRowCounter; i ++) {
			String USSDStatus = ExcelUtility.getCellData(0, "USSD", i);
			String categoryUser = ExcelUtility.getCellData(0, "Category Users", i);
			if (USSDStatus.equalsIgnoreCase("Y")) {
				channelUserCategory = ExcelUtility.getCellData(0, "Category Users", i);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i<=dataRowCounter;i++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			if(excelCategory.equalsIgnoreCase(channelUserCategory))
			{
				apiData.put(c2cLastTransfer.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, 2));
				apiData.put(c2cLastTransfer.PIN, ExcelUtility.getCellData(0, ExcelI.PIN, 2));
				break;
			}
		}
		
		return apiData;
	}
}
