package com.apicontrollers.extgw.o2ctransfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWO2CDP extends CaseMaster {
	
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String ProductName = null;
	public static String LoginID = null;
	public static String NA_TCP_ID = null;
	public static String grade = null;

	//Response Parameters
	public static final String TXNSTATUS = "COMMAND.TXNSTATUS";
	public static final String TXNID = "COMMAND.TXNID";
	public static Object[] getAPIdataWithAllUsers() {
		
		/*
		 * Variable Declaration
		 */
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
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
			if (categoryServices.contains(O2CTransferCode) && gatewayType.contains("EXTGW")) {
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
				ArrayList<String> gatewayType = new ArrayList<String>(Arrays.asList(ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, counter).split("[ ]*,[ ]*")));
				String channelUserCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, counter);
				if (categoryServices.contains(O2CTransferCode) && gatewayType.contains("EXTGW")) {
					for (int productCounter = 0; productCounter < productSize.length; productCounter++) {
					EXTGW_O2CDAO APIDataDAO = new EXTGW_O2CDAO();
					ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
					dataRowCounter = ExcelUtility.getRowCount();
					for (int i = 0; i<=dataRowCounter;i++) {
						String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
						if (excelCategory.equals(channelUserCategory)) {
							apiData.put(O2CTransferAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
							apiData.put(O2CTransferAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
							APIDataDAO.setLoginID(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
							APIDataDAO.setCategory(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
							APIDataDAO.setTCPName(ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i));
							APIDataDAO.setDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i));
							apiData.put(O2CTransferAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(APIDataDAO.getLoginID(), "EXTERNAL_CODE")[0]);
							break;
						}
					}
					apiData.put(O2CTransferAPI.EXTTXNNUMBER, RandomGeneration.randomNumeric(10));
					apiData.put(O2CTransferAPI.EXTTXNDATE, _APIUtil.getCurrentTimeStamp());
					
					APIDataDAO.setProductCode(productSize[productCounter][1].toString());
					apiData.put(O2CTransferAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
					apiData.put(O2CTransferAPI.PRODUCTCODE, productSize[productCounter][0].toString());
					apiData.put(O2CTransferAPI.QTY, "100");
					apiData.put(O2CTransferAPI.TRFCATEGORY, "SALE");
					apiData.put(O2CTransferAPI.REFNUMBER, RandomGeneration.randomNumeric(5));
					apiData.put(O2CTransferAPI.PAYMENTINSTNUMBER, RandomGeneration.randomNumeric(5));
					apiData.put(O2CTransferAPI.PAYMENTTYPE, "CHQ");
					apiData.put(O2CTransferAPI.PAYMENTDATE, _APIUtil.getCurrentTimeStamp());
					apiData.put(O2CTransferAPI.REMARKS, "Automation API Testing");
					APIDataDAO.setApiData((HashMap<String, String>)apiData.clone());
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
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
		String channelUserCategory = null;
		int dataRowCounter = 0;
		
		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */
		apiData.put(O2CTransferAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i=0; i <= dataRowCounter; i++) {
			String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			if (categoryServices.contains(O2CTransferCode)) {
				channelUserCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i = 0; i<=dataRowCounter;i++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			if (excelCategory.equals(channelUserCategory)) {
				apiData.put(O2CTransferAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				apiData.put(O2CTransferAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
				LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
				NA_TCP_ID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
				Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				grade = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
				apiData.put(O2CTransferAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
				break;
			}
		}
		apiData.put(O2CTransferAPI.EXTTXNNUMBER, RandomGeneration.randomNumeric(10));
		apiData.put(O2CTransferAPI.EXTTXNDATE, _APIUtil.getCurrentTimeStamp());
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1);
		ProductName = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, 1);
		apiData.put(O2CTransferAPI.PRODUCTCODE, ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, 1));
		apiData.put(O2CTransferAPI.QTY, "100");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "SALE");
		apiData.put(O2CTransferAPI.REFNUMBER, RandomGeneration.randomNumeric(5));
		apiData.put(O2CTransferAPI.PAYMENTINSTNUMBER, RandomGeneration.randomNumeric(5));
		apiData.put(O2CTransferAPI.PAYMENTTYPE, "CHQ");
		apiData.put(O2CTransferAPI.PAYMENTDATE, _APIUtil.getCurrentTimeStamp());
		apiData.put(O2CTransferAPI.REMARKS, "Automation API Testing");
		
		return apiData;
	}
	
}
