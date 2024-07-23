package com.apicontrollers.smsc.P2PCreditTransfer;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class SMSCPlain_P2PCreditTransfer_DP extends CaseMaster{

    public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
    public static String getAPIdata() {

    	StringBuilder apiData = new StringBuilder();
		int dataRowCounter = 0;
		String channelUserCategory = null;
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		RandomGeneration randomGeneration = new RandomGeneration();
		
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
				String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
				apiData.append(prefix + randomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));
				apiData.append("+");
				apiData.append(100);
				apiData.append("+");
				apiData.append(ExcelUtility.getCellData(0, ExcelI.PIN, i));
				apiData.append("&");
				apiData.append("MSISDN="+ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				break;
			}
		}
		return apiData.toString();
		}
		

}
