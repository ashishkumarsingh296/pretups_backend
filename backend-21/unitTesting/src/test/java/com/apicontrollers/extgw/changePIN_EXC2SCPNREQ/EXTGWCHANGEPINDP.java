package com.apicontrollers.extgw.changePIN_EXC2SCPNREQ;

import java.io.IOException;
import java.util.HashMap;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWCHANGEPINDP {
	
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	public static String nPIN = null;
	public static int rowNum;
	public static HashMap<String, String> getAPIdata() {
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGWCHANGEPINAPI ChangePinAPI = new EXTGWCHANGEPINAPI();
		RandomGeneration rndgen = new RandomGeneration();
		String masterSheetpath= _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetpath, ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int i=0;
		for(i=1;i<=rowCount;i++){
			if(ExcelUtility.getCellData(0, ExcelI.EXTGW, i).equals("Y")&& 
					(ExcelUtility.getCellData(0, ExcelI.EXTGW, i)!=null ||
					!ExcelUtility.getCellData(0, ExcelI.EXTGW, i).equals("")) && !ExcelUtility.getCellData(0, ExcelI.CATEGORY_USERS, i).equalsIgnoreCase("Operator"))
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
		apiData.put(ChangePinAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		apiData.put(ChangePinAPI.MSISDN, ExcelUtility.getCellData(0,ExcelI.MSISDN,rowNum));
		apiData.put(ChangePinAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0,ExcelI.PIN,rowNum)));
		apiData.put(ChangePinAPI.NEWPIN,_APIUtil.implementEncryption(nPIN));
		apiData.put(ChangePinAPI.CONFIRMPIN, _APIUtil.implementEncryption(nPIN));		
		apiData.put(ChangePinAPI.REMARKS, rndgen.randomAlphaNumeric(15));
		apiData.put(ChangePinAPI.EXTREFNUM, rndgen.randomAlphaNumeric(10));
		apiData.put(ChangePinAPI.TYPE,"EXC2SCPNREQ");
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
