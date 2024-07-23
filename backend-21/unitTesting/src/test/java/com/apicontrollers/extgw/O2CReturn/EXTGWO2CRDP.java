package com.apicontrollers.extgw.O2CReturn;

import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWO2CRDP extends CaseMaster {
	
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
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
		String O2CReturnCode = _masterVO.getProperty("O2CReturnCode");
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
			if (categoryServices.contains(O2CReturnCode) && gatewayType.contains("EXTGW")) {
				objSize++;
			}
		}
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int productSheetRows = ExcelUtility.getRowCount();
		Object[][] productSize = new Object[productSheetRows][2];
		
		for (int counter = 1; counter <= productSheetRows; counter++) {
			productSize[counter-1][0] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, counter);
			productSize[counter-1][1] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, counter);
		}
		
		Object[] apiDataObj = new Object[objSize * productSize.length];
		int objCounter = 0;
		
		for (int counter = 0; counter <= dataRowCounter; counter++) {
				HashMap<String, String> apiData = new HashMap<String, String>();
				
				ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
				String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, counter);
				String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, counter);
				String channelUserCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, counter);
				if (categoryServices.contains(O2CReturnCode) && gatewayType.contains("EXTGW")) {
					for (int productCounter = 0; productCounter < productSize.length; productCounter++) {
					EXTGW_O2CDAO APIDataDAO = new EXTGW_O2CDAO();
					ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
					dataRowCounter = ExcelUtility.getRowCount();
					for (int i = 0; i<=dataRowCounter;i++) {
						String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
						if (excelCategory.equals(channelUserCategory)) {
							apiData.put(O2CReturnAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
							apiData.put(O2CReturnAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
							APIDataDAO.setLoginID(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
							APIDataDAO.setCategory(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
							APIDataDAO.setTCPName(ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i));
							APIDataDAO.setDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i));
							apiData.put(O2CReturnAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(APIDataDAO.getLoginID(), "EXTERNAL_CODE")[0]);
							break;
						}
					}
					apiData.put(O2CReturnAPI.EXTTXNNUMBER, RandomGeneration.randomNumeric(10));
					apiData.put(O2CReturnAPI.EXTTXNDATE, _APIUtil.getCurrentTimeStamp());
					
					APIDataDAO.setProductCode(productSize[productCounter][1].toString());
					apiData.put(O2CReturnAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
					apiData.put(O2CReturnAPI.PRODUCTCODE, productSize[productCounter][0].toString());
					apiData.put(O2CReturnAPI.QTY, "100");
					apiData.put(O2CReturnAPI.TRFCATEGORY, "SALE");
					apiData.put(O2CReturnAPI.REFNUMBER, RandomGeneration.randomNumeric(5));
					apiData.put(O2CReturnAPI.PAYMENTINSTNUMBER, RandomGeneration.randomNumeric(5));
					apiData.put(O2CReturnAPI.PAYMENTTYPE, "CHQ");
					apiData.put(O2CReturnAPI.PAYMENTDATE, _APIUtil.getCurrentTimeStamp());
					apiData.put(O2CReturnAPI.REMARKS, "Automation API Testing");
					APIDataDAO.setApiData((HashMap<String, String>) apiData.clone());
					apiDataObj[objCounter] = (EXTGW_O2CDAO) APIDataDAO;
					objCounter++;
					}
				}
		}

		return apiDataObj;
	}
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGWO2CRAPI O2CReturnAPI = new EXTGWO2CRAPI();
		String O2CReturnCode = _masterVO.getProperty("O2CReturnCode");
		String channelUserCategory = null;
		int dataRowCounter = 0;
		
		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */
		apiData.put(O2CReturnAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i=0; i <= dataRowCounter; i++) {
			String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			if (categoryServices.contains(O2CReturnCode)) {
				channelUserCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i = 0; i<=dataRowCounter;i++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			if (excelCategory.equals(channelUserCategory)) {
				apiData.put(O2CReturnAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				apiData.put(O2CReturnAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
				LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
				NA_TCP_ID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
				Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				apiData.put(O2CReturnAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
				break;
			}
		}
		apiData.put(O2CReturnAPI.EXTTXNNUMBER, RandomGeneration.randomNumeric(10));
		apiData.put(O2CReturnAPI.EXTTXNDATE, _APIUtil.getCurrentTimeStamp());
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1);
		ProductName = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, 1);
		apiData.put(O2CReturnAPI.PRODUCTCODE, ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, 1));
		apiData.put(O2CReturnAPI.QTY, "100");
		apiData.put(O2CReturnAPI.TRFCATEGORY, "SALE");
		apiData.put(O2CReturnAPI.REFNUMBER, RandomGeneration.randomNumeric(5));
		apiData.put(O2CReturnAPI.PAYMENTINSTNUMBER, RandomGeneration.randomNumeric(5));
		apiData.put(O2CReturnAPI.PAYMENTTYPE, "CHQ");
		apiData.put(O2CReturnAPI.PAYMENTDATE, _APIUtil.getCurrentTimeStamp());
		apiData.put(O2CReturnAPI.REMARKS, "Automation API Testing");
		
		return apiData;
	}
	
}
