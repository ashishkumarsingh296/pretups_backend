package com.apicontrollers.extgw.LastTransactionStatus;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.RandomGeneration;

public class EXTGW_LastTransactionStatus_DP extends CaseMaster{
    
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
		EXTGW_LastTransactionStatus_API lastTransfer = new EXTGW_LastTransactionStatus_API();
		int dataRowCounter = 0;
		String channelUserCategory = null;
		
		RandomGeneration randomGeneration = new RandomGeneration();
		/*
		 * Variable initializations
		 */
		
		/*ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i <= dataRowCounter; i ++) {
			String EXTGWStatus = ExcelUtility.getCellData(0, "EXTGW", i);
			if (EXTGWStatus.equalsIgnoreCase("Y") && !ExcelUtility.getCellData(0, "Category Users", i).equalsIgnoreCase("Operator")) {
				channelUserCategory = ExcelUtility.getCellData(0, "Category Users", i);
				break;
			}
		}*/
		
		/*ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();*/
		
		/*for (int i = 0; i<=dataRowCounter;i++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			if(excelCategory.equalsIgnoreCase(channelUserCategory))
			{*/
				apiData.put(lastTransfer.MSISDN1, DBHandler.AccessHandler.getP2PSubscriber("MSISDN")[0]);
				String pin = DBHandler.AccessHandler.getP2PSubscriber("PIN")[0];
				
				apiData.put(lastTransfer.PIN, Decrypt.decryption(pin));
				
		/*		break;
			}
		}*/
		
		return apiData;
	}

}
