package com.apicontrollers.extgw.UserBalanceEnquiry;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.TransferControlProfile;
import com.apicontrollers.extgw.UserBalanceEnquiryAgentBased.EXTGWUBE_API;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;


public class EXTGW_UserBalanceEnquiry extends BaseTest{

	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
		
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWUB01
	 * Positive Test Case For User Balance Enquiry 
	 */
	
	@Test
	public void TC1_PositiveUBAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB01");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
		apiData.put(UserBalanceAPI.EXTCODE, "");
		apiData.put(UserBalanceAPI.LOGINID, "");
		apiData.put(UserBalanceAPI.PASSWORD, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		EXTGWUBDP.validateBalance(xmlPath);	
	}

	@Test
	public void TC2_PositiveUBAPIUsingEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB02");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
		apiData.put(UserBalanceAPI.MSISDN, "");
		apiData.put(UserBalanceAPI.PIN, "");
		apiData.put(UserBalanceAPI.LOGINID, "");
		apiData.put(UserBalanceAPI.PASSWORD, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		EXTGWUBDP.validateBalance(xmlPath);
		
	}
	
	@Test
	public void TC3_PositiveUBAPIUsingLoginIDPassword() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB03");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
		apiData.put(UserBalanceAPI.MSISDN, "");
		apiData.put(UserBalanceAPI.PIN, "");
		apiData.put(UserBalanceAPI.EXTCODE, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		EXTGWUBDP.validateBalance(xmlPath);
		
	}
	
	@Test
	public void TC4_PositiveUBAPIUsingMSISDNEXTCODELoginIDPassword() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB04");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
		//apiData.put(UserBalanceAPI.MSISDN, "");
		//apiData.put(UserBalanceAPI.PIN, "");
		//apiData.put(UserBalanceAPI.EXTCODE, "");
		//apiData.put(UserBalanceAPI.LOGINID, "");
		//apiData.put(UserBalanceAPI.PASSWORD, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		EXTGWUBDP.validateBalance(xmlPath);
		
	}
	
	@Test
	public void TC5_PositiveUBAPIUsingMSISDNLoginIDPassword() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB05");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
	    apiData.put(UserBalanceAPI.EXTCODE, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		EXTGWUBDP.validateBalance(xmlPath);
		
	}
	
	@Test
	public void TC6_PositiveUBAPIUsingEXTCODELoginIDPassword() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB06");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
	    apiData.put(UserBalanceAPI.MSISDN, "");
	    apiData.put(UserBalanceAPI.PIN, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		EXTGWUBDP.validateBalance(xmlPath);
		
	}
	
	@Test
	public void TC7_PositiveUBAPIUsingEXTCODEMSISDNPIN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB07");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
	    apiData.put(UserBalanceAPI.LOGINID, "");
	    apiData.put(UserBalanceAPI.PASSWORD, "");
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		EXTGWUBDP.validateBalance(xmlPath);
		
	}
	
	@Test
	public void TC8_NegetiveUBAPIUsingInvalidPIN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB08");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
		String maxPinLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.MAX_SMS_PIN_LENGTH);
		int pinLength = Integer.parseInt(maxPinLength);
	    apiData.put(UserBalanceAPI.PIN, RandomGeneration.randomNumeric(pinLength));
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (userBalanceList.size() > 0 && ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		EXTGWUBDP.validateBalance(xmlPath, "0");//Expecting 0 as Pin is incorrect
		
	}
	
	@Test
	public void TC9_NegetiveUBAPIUsingInvalidLoginID() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB09");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
	    apiData.put(UserBalanceAPI.LOGINID, RandomGeneration.randomNumeric(5));
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		/*String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(ProductShortCode)) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBDP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBAPI.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
	*/	
	}
	
	@Test
	public void TC10_NegetiveUBAPIUsingInvalidEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB10");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
	    apiData.put(UserBalanceAPI.EXTCODE, RandomGeneration.randomNumeric(5));
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		/*String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(ProductShortCode)) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBDP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBAPI.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);*/
		
	}
	
	@Test
	public void TC11_NegetiveUBAPIUsingInvalidMSISDN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB11");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
	    apiData.put(UserBalanceAPI.MSISDN, UniqueChecker.UC_MSISDN());//RandomGeneration.randomNumeric(8));
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		/*String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(ProductShortCode)) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBDP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBAPI.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);*/
		
	}
	
	@Test
	public void TC12_NegetiveUBAPIUsingBlankMSISDN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB12");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
	    apiData.put(UserBalanceAPI.MSISDN, "");
	    apiData.put(UserBalanceAPI.LOGINID, "");
	    apiData.put(UserBalanceAPI.PASSWORD, "");   
	    apiData.put(UserBalanceAPI.EXTCODE, "");
	    apiData.put(UserBalanceAPI.EXTREFNUM, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		/*String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(ProductShortCode)) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBDP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBAPI.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);*/
		
	}
	
	@Test
	public void TC13_NegetiveUBAPIUsingBlankPIN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB13");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
	    apiData.put(UserBalanceAPI.PIN, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		/*String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(ProductShortCode)) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBDP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBAPI.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);*/
		
	}
	
	@Test
	public void TC14_NegetiveUBAPIUsingBlankPassword() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB14");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
	    apiData.put(UserBalanceAPI.PASSWORD, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		/*String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(ProductShortCode)) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBDP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBAPI.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);
		*/
	}
	
	@Test
	public void TC15_NegetiveUBAPIUsingBlankLoginID() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB15");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
	    apiData.put(UserBalanceAPI.LOGINID, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		List<Object> userBalanceList = xmlPath.getList(EXTGWUBE_API.PRODUCTCODE);
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(userBalanceList.get(0))) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		EXTGWUBDP.validateBalance(xmlPath);
		
	}
	
	@Test
	public void TC16_NegetiveUBAPIUsingBlankMSISDNPINLOGINPasswordEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB16");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
				
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
		apiData.put(UserBalanceAPI.MSISDN, "");
		apiData.put(UserBalanceAPI.PIN, "");
	    apiData.put(UserBalanceAPI.LOGINID, "");
	    apiData.put(UserBalanceAPI.PASSWORD, "");
	    apiData.put(UserBalanceAPI.EXTCODE, "");
	    String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		/*String ProductShortCode = xmlPath.get(EXTGWUBAPI.PRODUCTCODE).toString();
		String ProductCode = null;
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int dataRowCounter = ExcelUtility.getRowCount();
		for (int i=1; i<=dataRowCounter; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i).equals(ProductShortCode)) {
				ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			}
		}
		
		String postBalance = _parser.getDisplayAmount(Long.parseLong(DBHandler.AccessHandler.getUserBalance(ProductCode, EXTGWUBDP.LoginID)));
		String userBalance = xmlPath.get(EXTGWUBAPI.BALANCE).toString();
		Validator.messageCompare(userBalance, postBalance);*/
		
	}
	
	@Test
	public void TC17_NegativeUBAPIWhileTCPSuspended() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB17");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getTestCaseCode() + ": " + CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWUBDP.Domain, EXTGWUBDP.CUCategory, EXTGWUBDP.TCPName, null);
		
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWUBDP.Domain, EXTGWUBDP.CUCategory, EXTGWUBDP.TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
  @Test
	public void TC21_PositiveUBAPIWhileCUOutSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUB21");
		EXTGWUBAPI UserBalanceAPI = new EXTGWUBAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWUBDP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(UserBalanceAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWUBDP.CUCategory, channelMap);
		
		String API = UserBalanceAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWUBDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
			
}