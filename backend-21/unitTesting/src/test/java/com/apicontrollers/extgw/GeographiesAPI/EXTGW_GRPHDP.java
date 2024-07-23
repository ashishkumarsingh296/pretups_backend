package com.apicontrollers.extgw.GeographiesAPI;

import java.util.HashMap;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGW_GRPHDP {

	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String USERLOGINID = null;
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_GRPHAPI GeographiesAPI = new EXTGW_GRPHAPI();
		int dataRowCounter = 0;
		String grphDomainTypeName = null;
		String parentCategory = null;
		String category = null;
		String grphDomainType = null;
		/*
		 * Object Declaration
		 */
		RandomGeneration RandomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */

		apiData.put(GeographiesAPI.DATE, _APIUtil.getCurrentTimeStamp());
		apiData.put(GeographiesAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		apiData.put(GeographiesAPI.EXTREFNUM, RandomGeneration.randomNumeric(5));
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		parentCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		apiData.put(GeographiesAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, 1));
		apiData.put(GeographiesAPI.USERLOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1));
		apiData.put(GeographiesAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1), "EXTERNAL_CODE")[0]);
		for (int i = 0; i<=dataRowCounter;i++) {
			category = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			if (category.equals(parentCategory)) {
				apiData.put(GeographiesAPI.CATCODE, ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i));
				grphDomainType = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i<=dataRowCounter;i++) {
			String domainCode = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
			if (domainCode.equals(grphDomainType)) {
				grphDomainTypeName = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE_NAME, i);
				break;
			}
		}
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		dataRowCounter = ExcelUtility.getRowCount();
		
		for (int i = 0; i<=dataRowCounter;i++) {
			String domainName = ExcelUtility.getCellData(0, ExcelI.DOMAIN_TYPE_NAME, i);
			if (domainName.equals(grphDomainTypeName)) {
				apiData.put(GeographiesAPI.GEOCODE, ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i));
				break;
			}
		}
		
		return apiData;
	}
}
