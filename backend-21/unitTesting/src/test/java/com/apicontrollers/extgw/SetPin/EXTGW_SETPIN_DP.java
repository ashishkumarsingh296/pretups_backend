package com.apicontrollers.extgw.SetPin;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.CommonUtils;
import com.utils.Decrypt;
import com.utils._masterVO;

public class EXTGW_SETPIN_DP extends CaseMaster {
	
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
		EXTGW_SETPIN_API setPin = new EXTGW_SETPIN_API();
		String masterSheetpath= _masterVO.getProperty("DataProvider");
  		/*ExcelUtility.setExcelFile(masterSheetpath, ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int i=0;
		for(i=1;i<=rowCount;i++){
			if(ExcelUtility.getCellData(0, ExcelI.EXTGW, i).equals("Y")&& 
					!ExcelUtility.getCellData(0, ExcelI.CATEGORY_USERS, i).equals("Operator"))
			{
				break;
			}
		}
		
		String category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_USERS, i);
*/
	/*	rowNum=0;
		try {
			rowNum=ExcelUtility.searchStringRowNum(masterSheetpath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, category);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		nPIN = new CommonUtils().isSMSPinValid();
		apiData.put(setPin.MSISDN1, DBHandler.AccessHandler.getP2PSubscriber("MSISDN")[0]);
        String pin = DBHandler.AccessHandler.getP2PSubscriber("PIN")[0];
        
        apiData.put(setPin.PIN, Decrypt.decryption(pin));

/*		ExcelUtility.setExcelFile(masterSheetpath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		apiData.put(setPin.MSISDN1, ExcelUtility.getCellData(0,ExcelI.MSISDN,rowNum));
		apiData.put(setPin.PIN, ExcelUtility.getCellData(0,ExcelI.PIN,rowNum))*/;
		apiData.put(setPin.NEWPIN, nPIN);
		apiData.put(setPin.CONFIRMPIN, nPIN);
//		apiData.put(setPin.TYPE, "CCPNREQ");	
		apiData.put(setPin.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		return apiData;
	}
	
       public static void setPIN(String status){
		
		/*if(status.equals("200")){
			Log.info("Updating PIN in DataProvider: "+nPIN);
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			ExcelUtility.setCellData(0, ExcelI.PIN, rowNum,nPIN);
			Log.info("PIN updated ["+nPIN+"] at location, rownum["+rowNum+"]");
		}*/
	}

}
