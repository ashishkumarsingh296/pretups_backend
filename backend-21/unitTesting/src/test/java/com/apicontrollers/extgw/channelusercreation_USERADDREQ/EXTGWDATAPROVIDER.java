package com.apicontrollers.extgw.channelusercreation_USERADDREQ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGW_O2CDAO;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWDATAPROVIDER {
	
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String ProductName = null;
	public static String NA_TCP_ID = null;

	public static Object[] O2C_getAPIdataWithAllUsers() {

		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
		int dataRowCounter = 0;
		int objSize = 0;
		
		RandomGeneration RandomGeneration = new RandomGeneration();

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
					ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET);
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
					apiData.put(O2CTransferAPI.QTY, "500");
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
	
	

	static String masterSheetPath;
	static int sheetRowCounter;
	public static HashMap<String, String> c2cMap = new HashMap<>();
	
	public static String FROM_Category = null;
	public static String FROM_TCPName = null;
	public static String FROM_Domain = null;
	public static String TO_Category = null;
	public static String TO_TCPName = null;
	public static String TO_TCP_ID = null;
	public static String TO_Domain = null;
	public static String ProductCode1 = null;
	public static String ProductName1 = null;
	public static String TO_CommissionProfileName=null;
	public static String FROM_CommissionProfileName=null;
	public static String TO_Grade = null;
	public static String FROM_Grade = null;
	
	public static HashMap<String, String> C2C_getAPIdata() {
		
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();
		int userCounter;
		
		String transactionType = _masterVO.getProperty("C2CTransferCode");
		apiData.put(C2CTransferAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		for (userCounter = 1; userCounter <= sheetRowCounter; userCounter++) {
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, userCounter);
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, userCounter);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			ArrayList<String> gatewayList = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
			String fromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, userCounter);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userCounter);
			if ((aList.contains(transactionType)||aList.contains(transactionType+"[P]")||
            		aList.contains(transactionType+"[S]")||aList.contains(transactionType+"[O]")||
            		aList.contains(transactionType+"[D]")) && gatewayList.contains("EXTGW") && !fromCategory.equals(toCategory)) {
				break;
			}
		}
		
		String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, userCounter);
		String fromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, userCounter);
		
		String FROMUser_WEBAccessStatus = DBHandler.AccessHandler.webInterface(fromCategory);
		String TOUser_WEBAccessStatus = DBHandler.AccessHandler.webInterface(toCategory);
		
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET);
		sheetRowCounter = ExcelUtility.getRowCount();

		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			String sheetCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
			if (sheetCategory.equals(fromCategory)) {
				FROM_Category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
				FROM_Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, userDetailsCounter);
				FROM_TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, userDetailsCounter);
				FROM_CommissionProfileName=ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, userDetailsCounter);
				FROM_Grade=ExcelUtility.getCellData(0, ExcelI.GRADE, userDetailsCounter);
				if (FROMUser_WEBAccessStatus.equalsIgnoreCase("Y")) {
					String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter);
					apiData.put(C2CTransferAPI.LOGINID, LoginID);
					apiData.put(C2CTransferAPI.PASSWORD, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, userDetailsCounter)));
				}
				
				apiData.put(C2CTransferAPI.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
				apiData.put(C2CTransferAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, userDetailsCounter)));
				apiData.put(C2CTransferAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(apiData.get(C2CTransferAPI.MSISDN1), "EXTERNAL_CODE")[0]);
				break;
			}
		}
		
		for (int userDetailsCounter = 1; userDetailsCounter <= sheetRowCounter; userDetailsCounter++) {
			String sheetCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
			if (sheetCategory.equals(toCategory)) {
				TO_Category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, userDetailsCounter);
				TO_Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, userDetailsCounter);
				TO_TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, userDetailsCounter);
				TO_TCP_ID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, userDetailsCounter);
				TO_CommissionProfileName=ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, userDetailsCounter);
				TO_Grade =ExcelUtility.getCellData(0, ExcelI.GRADE, userDetailsCounter);
				if (TOUser_WEBAccessStatus.equalsIgnoreCase("Y")) {
					String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, userDetailsCounter);
					apiData.put(C2CTransferAPI.LOGINID2, LoginID);
				}
				
				apiData.put(C2CTransferAPI.MSISDN2, ExcelUtility.getCellData(0, ExcelI.MSISDN, userDetailsCounter));
				apiData.put(C2CTransferAPI.EXTCODE2, DBHandler.AccessHandler.getUserDetails(apiData.get(C2CTransferAPI.MSISDN2), "EXTERNAL_CODE")[0]);
				break;
			}
		}
		
		RandomGeneration RandomGeneration = new RandomGeneration();
		apiData.put(C2CTransferAPI.EXTREFNUM, RandomGeneration.randomNumeric(5));
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.PRODUCT_SHEET);
		ProductCode1 = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1);
		apiData.put(C2CTransferAPI.PRODUCTCODE, ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, 1));
		ProductName1 = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, 1);
		apiData.put(C2CTransferAPI.QTY, "20");

		return apiData;
	}
	
	public static String LoginID=null;
	public static String CommProfile=null;
	public static String Grade=null;
	public static HashMap<String, String> O2C_getAPIdata() {
		
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
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET);
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
				CommProfile=ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
				Grade=ExcelUtility.getCellData(0, ExcelI.GRADE, i);
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
