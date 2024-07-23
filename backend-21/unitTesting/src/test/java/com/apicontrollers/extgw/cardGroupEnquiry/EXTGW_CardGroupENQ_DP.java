package com.apicontrollers.extgw.cardGroupEnquiry;

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

public class EXTGW_CardGroupENQ_DP extends CaseMaster{

	public static String CUCategory = null;
	public static String TCPName = null;
	public static String TCPID = null;
	public static String CrdGrp = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String ProductName = null;
	public static String LoginID = null;
	public static String CPName = null;
	public static String parentCategory = null;
	public static String grade = null;





	public static Object[] getAPIdataWithAllUsers() throws Exception {

		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();

		EXTGW_CRDGRPENQ_API EXTGW_CRDGRPENQ_API = new EXTGW_CRDGRPENQ_API();


		String PostpaidBillPaymentCode = _masterVO.getProperty("PostpaidBillPaymentCode");

		int dataRowCounter = 0;
		int objSize = 0;

		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		EXTGW_CRDGRPENQ_API cardGrpEnqAPI = new EXTGW_CRDGRPENQ_API();
		/*
		 * Variable initializations
		 */	

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i=0; i <= dataRowCounter; i++) {
			String To_Category = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
			if (To_Category.contains("Subscriber") && gatewayType.contains("EXTGW")) {

				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				ArrayList<String> serviceList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
				serviceList.remove(PostpaidBillPaymentCode);

				objSize = objSize + serviceList.size();
			}
		}


		Object[] apiDataObj = new Object[objSize];
		int objCounter = 0;


		for (int counter = 0; counter <= dataRowCounter; counter++) {


			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
			String To_Category = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, counter);
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, counter);

			if (To_Category.contains("Subscriber") && gatewayType.contains("EXTGW")) {

				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, counter);
				ArrayList<String> serviceList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
				serviceList.remove(PostpaidBillPaymentCode);

				for (int j = 0; j < serviceList.size(); j++) {
					EXTGW_CrdGrpENQ_DAO APIDataDAO = new EXTGW_CrdGrpENQ_DAO();
					String channelUserCategory = null;

					channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, counter);

					ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
					int CURowCounter = ExcelUtility.getRowCount();
					for (int i = 0; i<=CURowCounter;i++) {
						String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
						if (excelCategory.equals(channelUserCategory) && !channelUserCategory.equals("Subscriber") ) {
							apiData.put(cardGrpEnqAPI.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
							apiData.put(cardGrpEnqAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
							apiData.put(cardGrpEnqAPI.PASSWORD, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
							apiData.put(cardGrpEnqAPI.LOGINID,(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i)));
							APIDataDAO.setCategory(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
							APIDataDAO.setTCPName(ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i));
							APIDataDAO.setDomain(ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i));
							break;
						}
					}

					String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);

					apiData.put(cardGrpEnqAPI.MSISDN2, prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));
					apiData.put(cardGrpEnqAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
					
					apiData.put(cardGrpEnqAPI.AMOUNT, "100");


					ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
					int rowCount = ExcelUtility.getRowCount();
					for (int rownum = 1; rownum <= rowCount; rownum++) {
						String excelService = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
						String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
						CrdGrp = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_SETID, rownum);
						if (excelService.equalsIgnoreCase(serviceList.get(j)) && !cardGroupName.isEmpty()) {
							apiData.put(cardGrpEnqAPI.SERVICETYPE, excelService);
							apiData.put(cardGrpEnqAPI.SUBSERVICE,DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum),excelService));
							break;
						}
					}

					APIDataDAO.setApiData((HashMap<String, String>) apiData.clone());
					apiDataObj[objCounter] = (EXTGW_CrdGrpENQ_DAO) APIDataDAO;
					objCounter++;
				}
			}
		}


		return apiDataObj;
	}






	public static HashMap<String, String> getAPIdata() throws Exception {


		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_CRDGRPENQ_API EXTGW_CRDGRPENQ_API = new EXTGW_CRDGRPENQ_API();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		String channelUserCategory = null;
		int dataRowCounter = 0;

		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */
		apiData.put(EXTGW_CRDGRPENQ_API.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.TRANSFER_RULE_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i=0; i <= dataRowCounter; i++) {

			String To_Category = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			String categoryServices = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> alist = new ArrayList<String>(Arrays.asList(categoryServices.split("[ ]*,[ ]*")));
			String gatewayType = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
			ArrayList<String> alist1 = new ArrayList<String>(Arrays.asList(gatewayType.split("[ ]*,[ ]*")));
			if (alist.contains(CustomerRechargeCode)&& alist1.contains("EXTGW") && To_Category.equalsIgnoreCase("Subscriber")) {
				channelUserCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				break;
			}
		}	

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		for (int i = 0; i<=dataRowCounter;i++) {
			String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);

			if (excelCategory.equals(channelUserCategory)) {
				apiData.put(EXTGW_CRDGRPENQ_API.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				apiData.put(EXTGW_CRDGRPENQ_API.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
				LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
				TCPID = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
				Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				CPName = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
				parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				grade = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
				apiData.put(EXTGW_CRDGRPENQ_API.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
				apiData.put(EXTGW_CRDGRPENQ_API.PASSWORD, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));

				break;
			}
		}

		

		apiData.put(EXTGW_CRDGRPENQ_API.AMOUNT, "100");

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
			String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i);
			String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, i);
			CrdGrp = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_SETID, i);
			if (service.equals(CustomerRechargeCode)&& !cardGroupName.isEmpty()) {
				apiData.put(EXTGW_CRDGRPENQ_API.SERVICETYPE,ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i));
				apiData.put(EXTGW_CRDGRPENQ_API.SUBSERVICE,DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i),service));
				break;
			}
		}


		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);

		apiData.put(EXTGW_CRDGRPENQ_API.MSISDN2, prefix + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN()));

		return apiData;

	}
}