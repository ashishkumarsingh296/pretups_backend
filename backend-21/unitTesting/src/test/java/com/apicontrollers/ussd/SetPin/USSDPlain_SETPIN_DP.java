package com.apicontrollers.ussd.SetPin;

import java.io.IOException;
import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class USSDPlain_SETPIN_DP extends CaseMaster {
   
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	public static String nPIN = null;
	public static int rowNum;
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSDPlain_SETPIN_API setPin = new USSDPlain_SETPIN_API();
		RandomGeneration rndgen = new RandomGeneration();
		String masterSheetpath= _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetpath, ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int i=0;
		for(i=1;i<=rowCount;i++){
			if(ExcelUtility.getCellData(0, ExcelI.USSD, i).equals("Y")&& 
					(ExcelUtility.getCellData(0, ExcelI.USSD, i)!=null ||
					!ExcelUtility.getCellData(0, ExcelI.USSD, i).equals("")))
			{break;
			}
		}
		
		String category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_USERS, i);

		rowNum=0;
		try {
			rowNum=ExcelUtility.searchStringRowNum(masterSheetpath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, category);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		nPIN = new CommonUtils().isSMSPinValid();
		ExcelUtility.setExcelFile(masterSheetpath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		apiData.put(setPin.MSISDN1, ExcelUtility.getCellData(0,ExcelI.MSISDN,rowNum));
		apiData.put(setPin.PIN, ExcelUtility.getCellData(0,ExcelI.PIN,rowNum));
		apiData.put(setPin.NEWPIN, nPIN);
		apiData.put(setPin.CONFIRMPIN, nPIN);
		apiData.put(setPin.TYPE, "RCPNREQ");	
		apiData.put(setPin.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		return apiData;
	}
	
       public static void setPIN(String status){
		
		if(status.equals("200")){
			Log.info("Updating PIN in DataProvider: "+nPIN);
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			ExcelUtility.setCellData(0, ExcelI.PIN, rowNum,nPIN);
			Log.info("PIN updated ["+nPIN+"] at location, rownum["+rowNum+"]");
		}
	}
}
