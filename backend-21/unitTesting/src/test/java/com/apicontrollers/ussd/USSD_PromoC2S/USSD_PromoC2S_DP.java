package com.apicontrollers.ussd.USSD_PromoC2S;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGW_C2SDAO;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class USSD_PromoC2S_DP extends CaseMaster{
	
	public static String CUCategory = null;
	public static String TCPName = null;
	public static String TCPID = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String ProductName = null;
	public static String LoginID = null;
	public static String CPName = null;
	public static String parentCategory = null;
	public static String grade = null;

	public static HashMap<String, String> getAPIdata() throws Exception {

        /*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		USSD_PromoC2S_API PromoC2STransferAPI = new USSD_PromoC2S_API();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		String Product_Type = _masterVO.getProperty("PrepaidProductType");
		String channelUserCategory = null;
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
				apiData.put(PromoC2STransferAPI.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				apiData.put(PromoC2STransferAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
				LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
				TCPID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
				Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				CPName = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
				parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				grade = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
				break;
			}
		}

		apiData.put(PromoC2STransferAPI.AMOUNT, "100");

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
			String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i);
			String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
			if (service.equals(CustomerRechargeCode)&& !cardGroupName.isEmpty()) {

				apiData.put(PromoC2STransferAPI.SELECTOR,DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i),service));
				break;
			}
		}

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int rowCount1 = ExcelUtility.getRowCount();
		System.out.println(Product_Type);
		for (int k = 1; k<=rowCount1;k++) {
			String ExcelProductType = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, k);

			if(ExcelProductType.equals(Product_Type)){		

				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, k);
				ProductName = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, k);
				System.out.println("Product Code is: " + ProductCode);
				break;
			}
		}

		apiData.put(PromoC2STransferAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		apiData.put(PromoC2STransferAPI.LANGUAGE2, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));


		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);

		apiData.put(PromoC2STransferAPI.MSISDN2, prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));

		return apiData;
	}


	public static Object[] getAPIdataWithAllUsers() {

		/*
		 * Variable Declaration
		 */
		
		String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		String Product_Type = _masterVO.getProperty("PrepaidProductType");
		int dataRowCounter = 0;
		int objSize = 0;

		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		USSD_PromoC2S_API PromoC2STransferAPI = new USSD_PromoC2S_API();
		/*
		 * Variable initializations
		 */	

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i=0; i <= dataRowCounter; i++) {
			String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
			if (categoryServices.contains(CustomerRechargeCode) && gatewayType.contains("USSD")) {
				objSize++;
			}
		}
		
		
		Object[] apiDataObj = new Object[objSize];
		int objCounter = 0;


		for (int counter = 0; counter <= dataRowCounter; counter++) {
			HashMap<String, String> apiData = new HashMap<String, String>();

			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
			String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, counter);
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, counter);
			
			if (categoryServices.contains(CustomerRechargeCode) && gatewayType.contains("USSD")) {

				EXTGW_C2SDAO APIDataDAO = new EXTGW_C2SDAO();
				String channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, counter);
				
				ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
				int CURowCounter = ExcelUtility.getRowCount();
				for (int i = 0; i<=CURowCounter;i++) {
					String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
					if (excelCategory.equals(channelUserCategory)) {
						apiData.put(PromoC2STransferAPI.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
						apiData.put(PromoC2STransferAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
						APIDataDAO.setLoginID(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
						APIDataDAO.setCategory(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
						APIDataDAO.setTCPName(ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i));
						APIDataDAO.setDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i));
						break;
					}
				}


				ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
				int rowCount1 = ExcelUtility.getRowCount();
				System.out.println(Product_Type);
				for (int k = 1; k<=rowCount1;k++) {
					String ExcelProductType = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, k);

					if(ExcelProductType.equals(Product_Type)){		

						APIDataDAO.setProductCode(ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, k));
						APIDataDAO.setProductName(ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, k));

						break;
					}
				}

				apiData.put(PromoC2STransferAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
				apiData.put(PromoC2STransferAPI.LANGUAGE2, DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));


				String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);

				apiData.put(PromoC2STransferAPI.MSISDN2, prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));
				apiData.put(PromoC2STransferAPI.AMOUNT, "100");
				
				ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
				int rowCount = ExcelUtility.getRowCount();
				for (int rownum = 1; rownum <= rowCount; rownum++) {
					String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
					String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
					if (service.equals(CustomerRechargeCode)&& !cardGroupName.isEmpty()) {

						apiData.put(PromoC2STransferAPI.SELECTOR,DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum),service));
						break;
					}
				}
				
				APIDataDAO.setApiData(apiData);
				apiDataObj[objCounter] = (EXTGW_C2SDAO) APIDataDAO;
				objCounter++;
			}
		}

		return apiDataObj;
	}	

}
