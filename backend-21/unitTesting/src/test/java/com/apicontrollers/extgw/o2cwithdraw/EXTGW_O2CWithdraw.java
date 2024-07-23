package com.apicontrollers.extgw.o2cwithdraw;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CDP;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.Assertion;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.commons.PretupsI;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_O2CWithdraw extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	public static String Exisiting_TXN_NO = null;
		
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW01
	 * Positive Test Case For TRFCATEGORY: O2C
	 */
	@Test
	public void TC1_PositiveO2CWAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW01");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CWDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		EXTGW_O2CWDAO APIDAO = (EXTGW_O2CWDAO) dataObject[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		businessController businessController = new businessController(_masterVO.getProperty("O2CWithdrawCode"), apiData.get(O2CWithdrawAPI.MSISDN), null);
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), APIDAO.getCategory()));
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CWithdrawAPI.EXTCODE, "");
		Exisiting_TXN_NO = apiData.get(O2CWithdrawAPI.EXTTXNNUMBER);
		long preBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(APIDAO.getProductCode(), APIDAO.getLoginID()));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		//Test Case to validate Network Stocks after successful O2C Transfer
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Withdraw");
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(APIDAO.getProductCode(), apiData.get(O2CWithdrawAPI.QTY));
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		// Test Case to validate Channel User balance after successful O2C Transfer
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Withdraw");
		currentNode.assignCategory(extentCategory);
		BusinessValidator.validateUserBalances(TransactionVO);
		}
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW02
	 * Positive Test Case For O2C Withdraw using only EXTCODE
	 */
	@Test
	public void TC2_PositiveO2CWAPI_OnlyEXTCODE() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW02");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CWDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		EXTGW_O2CWDAO APIDAO = (EXTGW_O2CWDAO) dataObject[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		businessController businessController = new businessController(_masterVO.getProperty("O2CWithdrawCode"), apiData.get(O2CWithdrawAPI.MSISDN), null);
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), APIDAO.getCategory()));
		currentNode.assignCategory(extentCategory);
		apiData.put(O2CWithdrawAPI.MSISDN, "");
		apiData.put(O2CWithdrawAPI.PIN, "");
		long preBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(APIDAO.getProductCode(), APIDAO.getLoginID()));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		//Test Case to validate Network Stocks after successful O2C Transfer
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Withdraw");
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(APIDAO.getProductCode(), apiData.get(O2CWithdrawAPI.QTY));
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		// Test Case to validate Channel User balance after successful O2C Transfer
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Withdraw");
		currentNode.assignCategory(extentCategory);
		BusinessValidator.validateUserBalances(TransactionVO);
		}
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW03
	 * Positive Test Case For O2C withdraw using MSISDN, PIN & EXTCODE
	 */
	@Test
	public void TC3_PositiveO2CWAPI_UsingALL() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW03");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CWDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
		EXTGW_O2CWDAO APIDAO = (EXTGW_O2CWDAO) dataObject[i];
		HashMap<String, String> apiData = APIDAO.getApiData();
		businessController businessController = new businessController(_masterVO.getProperty("O2CWithdrawCode"), apiData.get(O2CWithdrawAPI.MSISDN), null);
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), APIDAO.getCategory()));
		currentNode.assignCategory(extentCategory);
		long preBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(APIDAO.getProductCode(), APIDAO.getLoginID()));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		//Test Case to validate Network Stocks after successful O2C Transfer
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Withdraw");
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(APIDAO.getProductCode(), apiData.get(O2CWithdrawAPI.QTY));
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		// Test Case to validate Channel User balance after successful O2C Transfer
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Withdraw");
		currentNode.assignCategory(extentCategory);
		BusinessValidator.validateUserBalances(TransactionVO);
		}
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW04
	 * Positive Test Case For O2C withdraw without Remarks
	 */
	@Test
	public void TC4_PositiveO2CWAPI_WithoutRemarks() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW04");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.REMARKS, "");
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW05
	 * Negative Test Case For O2C withdraw without MSISDN, PIN & EXTCODE
	 */
	@Test
	public void TC5_NegativeO2CWAPI_WithoutALL() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW05");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.MSISDN, "");
		apiData.put(O2CWithdrawAPI.PIN, "");
		apiData.put(O2CWithdrawAPI.EXTCODE, "");
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW06
	 * Negative Test Case For O2C withdraw without PRODUCTCODE
	 */
	@Test
	public void TC6_NegativeO2CWAPI_WithoutProductCode() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW06");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.PRODUCTCODE, "");
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW07
	 * Negative Test Case For O2C withdraw without QTY
	 */
	@Test
	public void TC7_NegativeO2CWAPI_WithoutQty() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW07");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.QTY, "");
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW08
	 * Negative Test Case For O2C withdraw without EXTTXNDATE
	 */
	@Test
	public void TC8_NegativeO2CWAPI_WithoutEXTTXNDATE() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW08");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.EXTTXNDATE, "");
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW09
	 * Negative Test Case For O2C withdraw without EXTTXNNUMBER
	 */
	@Test
	public void TC9_NegativeO2CWAPI_WithoutEXTTXNNUMBER() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW09");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.EXTTXNNUMBER, "");
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW10
	 * Negative Test Case For O2C withdraw without PIN
	 */
	@Test
	public void TC10_NegativeO2CWAPI_WithoutPIN() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW10");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.EXTCODE, "");
		apiData.put(O2CWithdrawAPI.PIN, "");
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW11
	 * Negative Test Case For O2C withdraw with Invalid EXTCODE
	 */
	@Test
	public void TC11_NegativeO2CWAPI_WithInvalidEXTCODE() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW11");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.EXTCODE, RandomGeneration.randomNumeric(8));
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW12
	 * Negative Test Case For O2C withdraw with Invalid PIN
	 */
	@Test
	public void TC12_NegativeO2CWAPI_WithInvalidEXTCODE() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW12");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.PIN, RandomGeneration.randomNumeric(4));
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW13
	 * Negative Test Case For O2C withdraw with Remarks Greater than 150 Characters
	 */
	@Test
	public void TC13_NegativeO2CWAPI_WithRemarksGreaterThan150() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW13");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.REMARKS, RandomGeneration.randomAlphabets(150));
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW14
	 * Negative Test Case For O2C withdraw with Existing EXTTXNNUM
	 */
	@Test
	public void TC14_NegativeO2CWAPI_WithExistingEXTTNNUM() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW14");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.EXTTXNNUMBER, Exisiting_TXN_NO);
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW15
	 * Negative Test Case For O2C withdraw with Invalid QTY
	 */
	@Test
	public void TC15_NegativeO2CWAPI_WithInvalidQTY() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW15");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.QTY, RandomGeneration.randomAlphabets(3));
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW16
	 * Negative Test Case For O2C withdraw with Decimal QTY
	 */
	@Test
	public void TC16_NegativeO2CWAPI_WithDecimalQTY() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW16");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);	
		if(_masterVO.getProperty("CommMultipleOf").contains(".")){
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.QTY, "100.25");
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		else{
			Assertion.assertSkip("As multipleOf is '"+_masterVO.getProperty("CommMultipleOf")+"' , decimal values not supported. Hence, case skipped.");
		}
		}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2CW17
	 * Negative Test Case For O2C withdraw with Negative QTY
	 */
	@Test
	public void TC17_NegativeO2CWAPI_WithDecimalQTY() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW17");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		apiData.put(O2CWithdrawAPI.QTY, "-100");
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @throws InterruptedException 
	 * @testid EXTGWO2CW18
	 * Positive Test Case For O2C withdraw with Channel User Out Suspended
	 */
	@Test
	public void TC18_PositiveO2CWAPI_WithCUOUTSuspended() throws SQLException, ParseException, InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW18");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), EXTGWO2CDP.CUCategory));
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(O2CWithdrawAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CWDP.CUCategory, channelMap);
		
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CWDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @throws InterruptedException 
	 * @testid EXTGWO2CW19
	 * Positive Test Case For O2C withdraw with Channel User IN Suspended
	 */
	@Test
	public void TC19_PositiveO2CWAPI_WithCUINSuspended() throws SQLException, ParseException, InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2CW19");
		EXTGWO2CWAPI O2CWithdrawAPI = new EXTGWO2CWAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), EXTGWO2CDP.CUCategory));
		currentNode.assignCategory(extentCategory);		
		HashMap<String, String> apiData = EXTGWO2CWDP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(O2CWithdrawAPI.MSISDN));
		channelMap.put("inSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CWDP.CUCategory, channelMap);
		
		String API = O2CWithdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		channelMap.put("inSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CWDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CWAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

}
