package com.apicontrollers.extgw.chnlReversal;

import java.util.ArrayList;
import java.util.Arrays;
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

public class chnlTxnREV_DP extends CaseMaster{
	
	
	public static String LoginID = null;
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String TCPID = null;
	public static String Domain = null;
	
	
	public static HashMap<String, String> getAPIdata() throws Exception {
	
	/*
	 * Variable Declaration
	 */
	HashMap<String, String> apiData = new HashMap<String, String>();
	chnlTxnREV_API chnlTxnREVAPI = new chnlTxnREV_API();
	
	String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
	String channelUserCategory = null;
	int dataRowCounter = 0;

	/*
	 * Object Declaration
	 */
	RandomGeneration RandomGeneration = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();

	/*
	 * Variable initializations
	 */
	apiData.put(chnlTxnREVAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));

	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
	int rowCounter = ExcelUtility.getRowCount();
	for (int k = 0; k<=rowCounter;k++) {
		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k);
		String UserCategory  = "CCE";
		
		if (excelCategory.equals(UserCategory)) {
			apiData.put(chnlTxnREVAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k));
			apiData.put(chnlTxnREVAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, k)));
			apiData.put(chnlTxnREVAPI.CATCODE,ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k));
			apiData.put(chnlTxnREVAPI.EMPCODE,DBHandler.AccessHandler.getEmpCode(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k)));
			
		}
	}
	apiData.put(chnlTxnREVAPI.EXTREFNUM, RandomGeneration.randomNumeric(10));
	apiData.put(chnlTxnREVAPI.DATE, _APIUtil.getCurrentTimeStamp());
	apiData.put(chnlTxnREVAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
	apiData.put(chnlTxnREVAPI.TRANSACTIONID,null);
	
	return apiData;
}
	
	
	

}
