package com.apicontrollers.ussd.LastTransferStatus_CP2P;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class USSD_LastTransferStatus_CP2P_DP extends CaseMaster   {

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
		USSD_LastTransferStatus_CP2P_API lastTransfer = new USSD_LastTransferStatus_CP2P_API();
		int dataRowCounter = 0;
		String channelUserCategory = null;
		
		/*
		 * Variable initializations
		 */
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i <= dataRowCounter; i ++) {
			String USSDStatus = ExcelUtility.getCellData(0, "USSD", i);
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
				apiData.put(lastTransfer.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				apiData.put(lastTransfer.PIN, ExcelUtility.getCellData(0, ExcelI.PIN, i));
				break;
			}
		}
		
		return apiData;
	}
}
