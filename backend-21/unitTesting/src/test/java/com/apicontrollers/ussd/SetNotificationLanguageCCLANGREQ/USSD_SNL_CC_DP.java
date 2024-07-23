package com.apicontrollers.ussd.SetNotificationLanguageCCLANGREQ;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class USSD_SNL_CC_DP extends CaseMaster {

	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSD_SNL_CC_API setNotificationLanguage = new USSD_SNL_CC_API();
		int dataRowCounter = 0;
        RandomGeneration randomGenerator = new RandomGeneration();
		/*
		 * Variable initializations
		 */
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i <= dataRowCounter; i ++) {
			String USSDStatus = ExcelUtility.getCellData(0, "USSD", i);
			if (USSDStatus.equalsIgnoreCase("Y")) {
				String msisdn = DBHandler.AccessHandler.getP2PSubscriberMSISDN("PRE", "Y");
				apiData.put(setNotificationLanguage.MSISDN1, msisdn);
				apiData.put(setNotificationLanguage.PIN, Decrypt.decryption(DBHandler.AccessHandler.getSubscriberP2PPin(msisdn)));
				break;
			}
		}
		
		apiData.put(setNotificationLanguage.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		
		return apiData;
	}
}
