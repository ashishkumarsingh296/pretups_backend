package com.apicontrollers.extgw.selfTPinReset_TPINRSETREQ;

import java.io.IOException;
import java.util.HashMap;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWRESETPINDP {
	
	public static int rowNum;
	public static HashMap<String, String> getAPIdata() {
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGWRESETPINAPI ResetPinAPI = new EXTGWRESETPINAPI();
		RandomGeneration rndgen = new RandomGeneration();
		String masterSheetpath= _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetpath, ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int i=0;
		for(i=1;i<=rowCount;i++){
			if(ExcelUtility.getCellData(0, ExcelI.EXTGW, i).equals("Y")&& 
					(ExcelUtility.getCellData(0, ExcelI.EXTGW, i)!=null ||
					!ExcelUtility.getCellData(0, ExcelI.EXTGW, i).equals("")))
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
		
		ExcelUtility.setExcelFile(masterSheetpath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		apiData.put(ResetPinAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		apiData.put(ResetPinAPI.MSISDN, ExcelUtility.getCellData(0,ExcelI.MSISDN,rowNum));	
		apiData.put(ResetPinAPI.REMARKS, rndgen.randomAlphaNumeric(15));
		apiData.put(ResetPinAPI.TYPE,"TPINRSETREQ");
		apiData.put(ResetPinAPI.LANGUAGE1,"0");
		apiData.put(ResetPinAPI.OPERATION,"R");
		
		String categoryCode="BCU";
		int optrow=0;
		try {
			optrow = ExcelUtility.searchStringRowNum(masterSheetpath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, categoryCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ExcelUtility.setExcelFile(masterSheetpath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		apiData.put(ResetPinAPI.LOGINID,ExcelUtility.getCellData(0,ExcelI.LOGIN_ID,optrow));
		apiData.put(ResetPinAPI.PASSWORD,_APIUtil.implementEncryption(ExcelUtility.getCellData(0,ExcelI.PASSWORD,optrow)));
		
		return apiData;
	}

public static void setPIN(String status, String nPIN){
		
		if(status.equals("200")){
			Log.info("Updating PIN in DataProvider ");
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			ExcelUtility.setCellData(0, ExcelI.PIN, rowNum,nPIN);
			Log.info("PIN updated ["+nPIN+"] at location, rownum["+rowNum+"]");
		}
	}
}
