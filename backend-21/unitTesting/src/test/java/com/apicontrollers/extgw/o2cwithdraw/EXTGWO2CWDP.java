package com.apicontrollers.extgw.o2cwithdraw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWO2CWDP {
	
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String ProductName = null;
	public static String LoginID = null;
	public static String NA_TCP_ID = null;

	public static Object[] getAPIdataWithAllUsers() {
		
		/*
		 * Variable Declaration
		 */
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();
		String O2CWithdrawCode = _masterVO.getProperty("O2CWithdrawCode");
		int dataRowCounter = 0;
		int objSize = 0;
		
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
			if (categoryServices.contains(O2CWithdrawCode) && gatewayType.contains("EXTGW")) {
				objSize++;
			}
		}
		
		Object[] apiDataObj = new Object[objSize];
		int objCounter = 0;
		
		for (int counter = 0; counter <= dataRowCounter; counter++) {
			HashMap<String, String> apiData = new HashMap<String, String>();
			
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
			String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, counter);
			ArrayList<String> gatewayType = new ArrayList<String>(Arrays.asList(ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, counter).split("[ ]*,[ ]*")));
			String channelUserCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, counter);
			if (categoryServices.contains(O2CWithdrawCode) && gatewayType.contains("EXTGW")) {
				EXTGW_O2CWDAO APIDataDAO = new EXTGW_O2CWDAO();
				ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
				dataRowCounter = ExcelUtility.getRowCount();
				for (int i = 0; i<=dataRowCounter;i++) {
					String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
					if (excelCategory.equals(channelUserCategory)) {
						apiData.put(O2CWithdrawAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
						apiData.put(O2CWithdrawAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
						APIDataDAO.setLoginID(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
						APIDataDAO.setCategory(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
						APIDataDAO.setTCPName(ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i));
						APIDataDAO.setDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i));
						apiData.put(O2CWithdrawAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(APIDataDAO.getLoginID(), "EXTERNAL_CODE")[0]);
						break;
					}
				}
				apiData.put(O2CWithdrawAPI.EXTTXNNUMBER, RandomGeneration.randomNumeric(10));
				apiData.put(O2CWithdrawAPI.EXTTXNDATE, _APIUtil.getCurrentTimeStamp());
				
				ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
				APIDataDAO.setProductCode(ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1));
				apiData.put(O2CWithdrawAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
				apiData.put(O2CWithdrawAPI.PRODUCTCODE, ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, 1));
				apiData.put(O2CWithdrawAPI.QTY, "100");
				apiData.put(O2CWithdrawAPI.REMARKS, "Automation API Testing");
				APIDataDAO.setApiData((HashMap<String, String>)apiData.clone());
				apiDataObj[objCounter] = (EXTGW_O2CWDAO) APIDataDAO;
				objCounter++;
			}
		}

		return apiDataObj;
	}
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();
		String O2CWithdrawCode = _masterVO.getProperty("O2CWithdrawCode");
		String channelUserCategory = null;
		int dataRowCounter = 0;
		
		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */
		apiData.put(O2CWithdrawAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i=0; i <= dataRowCounter; i++) {
			String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			if (categoryServices.contains(O2CWithdrawCode)) {
				channelUserCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i = 0; i<=dataRowCounter;i++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			if (excelCategory.equals(channelUserCategory)) {
				apiData.put(O2CWithdrawAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				apiData.put(O2CWithdrawAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
				LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
				Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				apiData.put(O2CWithdrawAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
				break;
			}
		}
		apiData.put(O2CWithdrawAPI.EXTTXNNUMBER, RandomGeneration.randomNumeric(10));
		apiData.put(O2CWithdrawAPI.EXTTXNDATE, _APIUtil.getCurrentTimeStamp());
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1);
		apiData.put(O2CWithdrawAPI.PRODUCTCODE, ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, 1));
		apiData.put(O2CWithdrawAPI.QTY, "100");
		apiData.put(O2CWithdrawAPI.REMARKS, "Automation API Testing");
		
		return apiData;
	}
	
}
