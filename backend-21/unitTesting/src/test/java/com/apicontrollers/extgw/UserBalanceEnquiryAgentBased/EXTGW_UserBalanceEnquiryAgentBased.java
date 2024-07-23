package com.apicontrollers.extgw.UserBalanceEnquiryAgentBased;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.TransferControlProfile;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_UserBalanceEnquiryAgentBased  extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
		
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWUB01
	 * Positive Test Case For User Balance Enquiry 
	 */
	
	@Test
	public void TCA1_PositiveUBAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB01");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
		apiData.put(UserBalanceAPI.EXTCODE, "");
		apiData.put(UserBalanceAPI.LOGINID, "");
		apiData.put(UserBalanceAPI.PASSWORD, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		String ProductShortCode = xmlPath.get(EXTGWUBE_API.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBE_DP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBE_API.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
	}
	
	@Test
	public void TCB2_PositiveUBAPIUsingEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB02");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
		apiData.put(UserBalanceAPI.MSISDN, "");
		apiData.put(UserBalanceAPI.PIN, "");
		apiData.put(UserBalanceAPI.LOGINID, "");
		apiData.put(UserBalanceAPI.PASSWORD, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBE_API.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBE_DP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBE_API.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
		
	}
	
	@Test
	public void TCC3_PositiveUBAPIUsingLoginIDPassword() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB03");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
		apiData.put(UserBalanceAPI.MSISDN, "");
		apiData.put(UserBalanceAPI.PIN, "");
		apiData.put(UserBalanceAPI.EXTCODE, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBE_API.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBE_DP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBE_API.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
		
	}
	
	@Test
	public void TCD4_PositiveUBAPIUsingMSISDNEXTCODELoginIDPassword() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB04");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBE_API.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBE_DP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBE_API.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
		
	}
	
	@Test
	public void TCE5_PositiveUBAPIUsingMSISDNLoginIDPassword() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB05");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.EXTCODE, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBE_API.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBE_DP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBE_API.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
		
	}
	
	@Test
	public void TCF6_PositiveUBAPIUsingEXTCODELoginIDPassword() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB06");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.MSISDN, "");
	    apiData.put(UserBalanceAPI.PIN, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBE_API.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBE_DP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBE_API.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
		
	}
	
	@Test
	public void TCG7_PositiveUBAPIUsingEXTCODEMSISDNPIN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB07");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.LOGINID, "");
	    apiData.put(UserBalanceAPI.PASSWORD, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBE_API.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBE_DP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBE_API.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
		
	}
	
	@Test
	public void TCH8_NegetiveUBAPIUsingInvalidPIN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB08");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.PIN, RandomGeneration.randomNumeric(5));
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBE_API.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBE_DP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBE_API.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
		
	}
	
	@Test
	public void TCI9_NegetiveUBAPIUsingInvalidLoginID() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB09");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.LOGINID, RandomGeneration.randomNumeric(5));
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCJ10_NegetiveUBAPIUsingInvalidEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB10");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.EXTCODE, RandomGeneration.randomNumeric(5));
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCK11_NegetiveUBAPIUsingInvalidMSISDN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB11");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.MSISDN, RandomGeneration.randomNumeric(8));
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCL12_NegetiveUBAPIUsingBlankMSISDN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB12");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.MSISDN, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCM13_NegetiveUBAPIUsingBlankPIN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB13");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.PIN, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCN14_NegetiveUBAPIUsingBlankPassword() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB14");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.PASSWORD, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCO15_NegetiveUBAPIUsingBlankLoginID() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB15");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.LOGINID, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBE_API.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBE_DP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBE_API.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
		
	}
	
	@Test
	public void TCP16_NegetiveUBAPIUsingBlankMSISDNPINLOGINPasswordEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB16");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
		apiData.put(UserBalanceAPI.MSISDN, "");
		apiData.put(UserBalanceAPI.PIN, "");
	    apiData.put(UserBalanceAPI.LOGINID, "");
	    apiData.put(UserBalanceAPI.PASSWORD, "");
	    apiData.put(UserBalanceAPI.EXTCODE, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCQ17_NegativeUBAPIWhileTCPSuspended() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB17");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWUBE_DP.Domain, EXTGWUBE_DP.CUCategory, EXTGWUBE_DP.TCPName, null);
		
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWUBE_DP.Domain, EXTGWUBE_DP.CUCategory, EXTGWUBE_DP.TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
  @Test
	public void TCR18_PositiveUBAPIWhileCUOutSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB18");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(UserBalanceAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWUBE_DP.CUCategory, channelMap);
		
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWUBE_DP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
  
  @Test
	public void TCS19_NegetiveUBAPIUsingBlankMSISDN2() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB19");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.MSISDN2, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
  
  
  @Test
 	public void TCU20_NegetiveUBAPIUsingSELFMSISDN2() {

 		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB21");
 		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
 				
 		if (TestCaseCounter == false) {
 			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
 			TestCaseCounter = true;
 		}
 		
 		currentNode = test.createNode(CaseMaster.getDescription());
 		currentNode.assignCategory(extentCategory);
 		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
 		String msisdn1 = apiData.get("MSISDN");
 	    apiData.put(UserBalanceAPI.MSISDN2, msisdn1);
 	    String API = UserBalanceAPI.prepareAPI(apiData);
 		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
 		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
 		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
 		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
 	}
  
  @Test
	public void TCV21_NegetiveUBAPIUsingInvalidMSISDN2() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB22");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
	    apiData.put(UserBalanceAPI.MSISDN2, RandomGeneration.randomNumeric(8));
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
  
  @Test
	public void TCV22_NegetiveUBAPIUsingWrongHierarchy() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUBEAB23");
		EXTGWUBE_API UserBalanceAPI = new EXTGWUBE_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBE_DP.getAPIdata();
		String msisdn1 = apiData.get("MSISDN");
		String msisdn2 = apiData.get("MSISDN2");
		apiData.put(UserBalanceAPI.MSISDN2, msisdn1);
		apiData.put(UserBalanceAPI.MSISDN, msisdn2);
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBE_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
}
