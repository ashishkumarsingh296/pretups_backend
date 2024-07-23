package com.apicontrollers.extgw.AlternateNumber;


import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWMODALTRNUMBERDP extends CaseMaster {

	public static String LoginID = null;
	
	public static HashMap<String, String> getAPIdata() throws Exception {

		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		// EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWMODALTNUMERAPI modAlternateNUmberAPI = new EXTGWMODALTNUMERAPI();

		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

				int dataRowCounter = 0;

		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */
		apiData.put(modAlternateNUmberAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));

		dataRowCounter = ExcelUtility.getRowCount();
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
			apiData.put(modAlternateNUmberAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, 1));
			apiData.put(modAlternateNUmberAPI.PIN,
					_APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, 1)));

			
			apiData.put(modAlternateNUmberAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1));
			apiData.put(modAlternateNUmberAPI.PASSWORD,
					_APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, 1)));
			apiData.put(modAlternateNUmberAPI.EXTCODE,ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, 1));
		
		
		apiData.put(modAlternateNUmberAPI.EXTREFNUM, RandomGeneration.randomNumeric(10));
		apiData.put(modAlternateNUmberAPI.DATE, _APIUtil.getCurrentTimeStamp());

		apiData.put(modAlternateNUmberAPI.LANGUAGE1,
				DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		
		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);

		String [] arr= DBHandler.AccessHandler.getdetailsfromUsersTable(ExcelUtility.getCellData(0, ExcelI.MSISDN, 1), "alternate_msisdn");
		apiData.put(modAlternateNUmberAPI.ALTMSISDN, arr[0]);
		
		//String [] arr= DBHandler.AccessHandler.getdetailsfromUsersTable(modAlternateNUmberAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, 1), "alternate_msisdn");
		//apiData.put(modAlternateNUmberAPI.ALTMSISDN, arr[0]);

		apiData.put(modAlternateNUmberAPI.NEWMSISDN, prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));
		
		return apiData;
		
	}



}
