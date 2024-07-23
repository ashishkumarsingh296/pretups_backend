package com.apicontrollers.extgw.P2PHistory;

import java.util.HashMap;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class EXTGW_P2PHistory_DP {
	
	
	public static String Category = null;
	public static String CrdGrp = null;
	
	public static HashMap<String, String> getAPIdata() throws Exception {
	

	/*
	 * Variable Declaration
	 */
	HashMap<String, String> apiData = new HashMap<String, String>();
	EXTGW_P2PHistoryAPI P2PHistoryAPI = new EXTGW_P2PHistoryAPI();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	String CreditTransferCode = _masterVO.getProperty("CreditTransferCode");
	String ProductType = _masterVO.getProperty("PrepaidProductType");
	String productCode = null;
	String UserCategory = null;
	
	int dataRowCounter = 0;
	
	/*
	 * Object Declaration
	 */
	RandomGeneration RandomGeneration = new RandomGeneration();

	/*
	 * Variable initializations
	 */	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
	dataRowCounter = ExcelUtility.getRowCount();
	for (int i=0; i <= dataRowCounter; i++) {
		String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
		String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
		if (categoryServices.contains(CreditTransferCode) && gatewayType.contains("EXTGW")) {
			UserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			break;
		}
	}
	
	
	
	String MSISDN = DBHandler.AccessHandler.getP2PSubscriberMSISDN("PRE","Y");
	apiData.put(P2PHistoryAPI.MSISDN1, MSISDN);
	
	String MSISDNexists = DBHandler.AccessHandler.checkSubscriberMSISDNexist(MSISDN);
	
	if(MSISDNexists.equalsIgnoreCase("Y")){
	apiData.put(P2PHistoryAPI.PIN, Decrypt.decryption(DBHandler.AccessHandler.getSubscriberP2PPin(MSISDN)));
	}
	else
	{
		apiData.put(P2PHistoryAPI.PIN,	"1357");
	}
	
	apiData.put(P2PHistoryAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
	
	
	return apiData;
}	
	


}
