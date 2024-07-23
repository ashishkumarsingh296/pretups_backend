package com.apicontrollers.ussd.InquiryByTxnID;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class USSD_Inquiry_DP extends CaseMaster{

	static String masterSheetPath;
	static int sheetRowCounter;
	
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() {
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSD_Inquiry_API inquiryAPI = new USSD_Inquiry_API();
		int dataRowCounter = 0;
		String channelUserCategory = null;
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i <= dataRowCounter; i ++) {
			String USSDStatus = ExcelUtility.getCellData(0, "USSD", i);
			channelUserCategory = ExcelUtility.getCellData(0, "Category Users", i);
			if (USSDStatus.equalsIgnoreCase("Y") && !channelUserCategory.equalsIgnoreCase("Operator")) {
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
				apiData.put(inquiryAPI.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				apiData.put(inquiryAPI.PIN, ExcelUtility.getCellData(0, ExcelI.PIN, i));
				break;
			}
		}
		
		return apiData;
		}
}
