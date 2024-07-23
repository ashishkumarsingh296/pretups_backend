package com.apicontrollers.ussd.EVR;

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

public class USSDPlain_EVR_DP extends CaseMaster{

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
		USSDPlain_EVR_API EVRAPI = new USSDPlain_EVR_API();
		int dataRowCounter = 0;
		String channelUserCategory = null;
		RandomGeneration randomGenerator = new RandomGeneration();
		String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		/*
		 * Variable initializations
		 */
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i=0; i <= dataRowCounter; i++) {
			String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> alist = new ArrayList<String>(Arrays.asList(categoryServices.split("[ ]*,[ ]*")));
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
			ArrayList<String> alist1 = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
			if (alist.contains(CustomerRechargeCode)&& alist1.contains("USSD")) {
				channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i<=dataRowCounter;i++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			
			if (excelCategory.equals(channelUserCategory)) {
				apiData.put(EVRAPI.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				apiData.put(EVRAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
				break;
			}
				
			}
		
		/*ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
			String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i);
			String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
			if (service.equals(CustomerRechargeCode)&& !cardGroupName.isEmpty()) {
                apiData.put(EVRAPI.SELECTOR,DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)));
				break;
			}
		}*/
		
		apiData.put(EVRAPI.SELECTOR,"1");
		apiData.put(EVRAPI.MSISDN2, _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + gnMsisdn.generateMSISDN());
		apiData.put(EVRAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		apiData.put(EVRAPI.LANGUAGE2, DBHandler.AccessHandler.checkForLangCode(LangCode));
		apiData.put(EVRAPI.AMOUNT, "200");
		return apiData;
		}
		


}
