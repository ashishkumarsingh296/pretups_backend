package com.apicontrollers.ussd.SetPinCP2P;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class USSDPlain_SP_CP2P_DP extends CaseMaster {
   
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	public static String nPIN = null;
	public static int dataRowCounter;
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();
		RandomGeneration randomGenerator = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i <= dataRowCounter; i ++) {
			String USSDStatus = ExcelUtility.getCellData(0, "USSD", i);
			if (USSDStatus.equalsIgnoreCase("Y")) {
				apiData.put(setPin.MSISDN1, _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + gnMsisdn.generateMSISDN());
				apiData.put(setPin.PIN, "0000");
				break;
			}
		}
		nPIN = new CommonUtils().isSMSPinValid();
		apiData.put(setPin.NEWPIN, nPIN);
		apiData.put(setPin.CONFIRMPIN, nPIN);
		apiData.put(setPin.TYPE, "CCPNREQ");	
		apiData.put(setPin.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		return apiData;
	}
	
       public static void setPIN(String status){
		
		if(status.equals("200")){
			Log.info("Updating PIN in DataProvider: "+nPIN);
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			ExcelUtility.setCellData(0, ExcelI.PIN, dataRowCounter,nPIN);
			Log.info("PIN updated ["+nPIN+"] at location, rownum["+dataRowCounter+"]");
		}
	}
}
