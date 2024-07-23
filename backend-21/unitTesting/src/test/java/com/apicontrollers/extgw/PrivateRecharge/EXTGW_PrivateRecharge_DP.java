package com.apicontrollers.extgw.PrivateRecharge;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class EXTGW_PrivateRecharge_DP extends CaseMaster{
	
    public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	public static int dataRowCounter;
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
    public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		GenerateMSISDN gnMsisdn= new GenerateMSISDN();
		EXTGW_PrivateRecharge_API privateRecharge = new EXTGW_PrivateRecharge_API();
		RandomGeneration randomGenerator = new RandomGeneration();
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i <= dataRowCounter; i ++) {
			String EXTGWStatus = ExcelUtility.getCellData(0, "EXTGW", i);
			if (EXTGWStatus.equalsIgnoreCase("Y")) {
				apiData.put(privateRecharge.MSISDN, _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + gnMsisdn.generateMSISDN());
				break;
			}
		}
		
		apiData.put(privateRecharge.SID, UniqueChecker.UC_SubsSID());	
		String sid = apiData.get("SID");
		apiData.put(privateRecharge.NEWSID, sid);	
		apiData.put(privateRecharge.TYPE, "SIDREQ");	
		apiData.put(privateRecharge.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		return apiData;
}


}
