package com.apicontrollers.extgw.UserBalanceEnquiry;

import java.util.HashMap;
import java.util.Iterator;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;

import io.restassured.internal.path.xml.NodeChildrenImpl;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.element.Node;

public class EXTGWUBDP {
	
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
	EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
	int dataRowCounter = 0;
	String channelUserCategory = null;
	
	/*
	 * Object Declaration
	 */
	RandomGeneration RandomGeneration = new RandomGeneration();

	/*
	 * Variable initializations
	 */

	apiData.put(UserBalanceAPI.DATE, _APIUtil.getCurrentTimeStamp());
	apiData.put(UserBalanceAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ACCESS_BEARER_MATRIX_SHEET);
	dataRowCounter = ExcelUtility.getRowCount();
	
	for (int i = 0; i <= dataRowCounter; i ++) {
		String EXTGWStatus = ExcelUtility.getCellData(0, "EXTGW", i);
		if (EXTGWStatus.equalsIgnoreCase("Y") && !ExcelUtility.getCellData(0, "Category Users", i).equalsIgnoreCase("Operator")) {
			channelUserCategory = ExcelUtility.getCellData(0, "Category Users", i);
			break;
		}
	}
	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	dataRowCounter = ExcelUtility.getRowCount();
	
	for (int i = 0; i<=dataRowCounter;i++) {
		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		if (excelCategory.equals(channelUserCategory)) {
			apiData.put(UserBalanceAPI.MSISDN, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
			apiData.put(UserBalanceAPI.PIN, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PIN, i)));
			apiData.put(UserBalanceAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
			apiData.put(UserBalanceAPI.PASSWORD, _APIUtil.implementEncryption(ExcelUtility.getCellData(0, ExcelI.PASSWORD, i)));
			LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			CUCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
			Domain = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			apiData.put(UserBalanceAPI.EXTCODE, DBHandler.AccessHandler.getUserDetails(LoginID, "EXTERNAL_CODE")[0]);
			break;
		}
	}
	
	ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
	ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, 1);
	apiData.put(UserBalanceAPI.EXTREFNUM, RandomGeneration.randomNumeric(5));
	apiData.put(UserBalanceAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
	
	return apiData;
}
public static void validateBalance(XmlPath xmlPath) {
	Double postBalance = Double.parseDouble(_parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserSumBalance(EXTGWUBDP.LoginID))));
	String postBal = BTSLUtil.formatDouble(postBalance);
	NodeChildrenImpl nodeChildrenImpl = (NodeChildrenImpl)xmlPath.get(EXTGWUBAPI.RECORD);
	double sum = 0d;
	for(int j =0; j < nodeChildrenImpl.size(); j++) {
		Iterator<Node> iterator = nodeChildrenImpl.get(j).children().nodeIterator();
		 while(iterator.hasNext()) {
			 Node node = iterator.next();
			 if(node.name() == "BALANCE") {
				 if(node.value() != null)
					 sum = sum + Double.parseDouble(node.value());
			 }
		 }
	}
	Validator.messageCompare(BTSLUtil.formatDouble(sum), postBal);
}

public static void validateBalance(XmlPath xmlPath, String postBalance) {
	NodeChildrenImpl nodeChildrenImpl = (NodeChildrenImpl)xmlPath.get(EXTGWUBAPI.RECORD);
	double sum = 0;
	for(int j =0; j < nodeChildrenImpl.size(); j++) {
		Iterator<Node> iterator = nodeChildrenImpl.get(j).children().nodeIterator();
		 while(iterator.hasNext()) {
			 Node node = iterator.next();
			 if(node.name() == "BALANCE") {
				 if(node.value() != null)
					 sum = sum + Double.parseDouble(node.value());
			 }
		 }
	}
	Validator.messageCompare(BTSLUtil.formatDouble(sum), postBalance);
}
}
