package com.apicontrollers.extgw.c2ctransfer;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import com.Features.*;
import com.commons.MasterI;
import com.pretupsControllers.BTSLUtil;
import org.testng.annotations.Test;

import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CAPI;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.Assertion;
import com.utils.ExtentI;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_C2CTransfer extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/**
	 * @throws ParseException
	 * @throws SQLException
	 * @testid EXTGWC2C01 Positive Test Case For TRFCATEGORY: C2C
	 */
	@Test
	public void TCA_PositiveC2CAPIUsingMSISDNAndPIN() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C01");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), apiData.get(C2CTransferAPI.MSISDN1), apiData.get(C2CTransferAPI.MSISDN2));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		initiatedQuantities.put(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.QTY));

		String API = C2CTransferAPI.prepareAPI(apiData);
		apiData.put(C2CTransferAPI.LOGINID, "");
		apiData.put(C2CTransferAPI.PASSWORD, "");

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());



		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
		BusinessValidator.validateStocks(TransactionVO);

		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
		int maxApprovalLevel=0;
		if(BTSLUtil.isNullString(value)) {
			maxApprovalLevel=0;
		}
		else
			maxApprovalLevel = Integer.parseInt(value);
		if(maxApprovalLevel==0) {
			BusinessValidator.validateUserBalances(TransactionVO);
		}else{
			Assertion.assertSkip("Skipping following validation as Approval required");
		}

	}

	@Test
	public void TCB_PositiveC2CAPIUsingLoginIDAndPassword() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C02");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), apiData.get(C2CTransferAPI.MSISDN1), apiData.get(C2CTransferAPI.MSISDN2));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		initiatedQuantities.put(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.QTY));

		String API = C2CTransferAPI.prepareAPI(apiData);
		apiData.put(C2CTransferAPI.MSISDN1, "");
		apiData.put(C2CTransferAPI.PIN, "");

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
		BusinessValidator.validateStocks(TransactionVO);

		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
		int maxApprovalLevel=0;
		if(!BTSLUtil.isNullString(value)) {
			maxApprovalLevel = Integer.parseInt(value);
		}

		if(maxApprovalLevel==0) {
			BusinessValidator.validateUserBalances(TransactionVO);
		}else{
			Assertion.assertSkip("Approval Required for Balance Transaction");
		}

	}

	@Test
	public void TCC_NegativeC2CAPIwithBlankEXTNWCODE() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C03");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.EXTNWCODE, "");
		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCD_NegativeC2CAPIwithMSISDNPinMismatch() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C04");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.PIN, RandomGeneration.randomNumeric(4));
		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCE_NegativeC2CAPIwithBlankReceiver() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C05");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.MSISDN2, "");
		apiData.put(C2CTransferAPI.LOGINID2, "");
		apiData.put(C2CTransferAPI.EXTCODE2, "");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCF_NegativeC2CAPIwithInvalidLoginID() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C06");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.LOGINID, "abc");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCG_NegativeC2CAPIwithBlankPassword() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C07");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.PASSWORD, "");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCH_NegativeC2CAPIwithReferenceNumberLimitExceeded() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C08");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.EXTREFNUM, RandomGeneration.randomNumeric(51));
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCI_NegativeC2CAPIwithNegativeAmount() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C09");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.QTY, "-1");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCJ_NegativeC2CAPIwithInvalidEXTCODE2() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C10");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.EXTCODE2, RandomGeneration.randomNumeric(4));
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCK_NegativeC2CAPIwithInvalidMSISDN2() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C11");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();
		String invalidMSISDN = gnMsisdn.generateRandomMSISDNWithinNetwork(PretupsI.PREPAID_LOOKUP);
		apiData.put(C2CTransferAPI.MSISDN2, invalidMSISDN);
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCL_NegativeC2CAPIwithInvalidLoginID2() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C12");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.LOGINID2, RandomGeneration.randomAlphaNumeric(5));
		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCM_NegativeC2CAPIwithoutPIN() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C13");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.PIN, "");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCN_NegativeC2CAPIwithBlankProductCode() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C14");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.PRODUCTCODE, "");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCO_NegativeC2CAPIwithBlankQuantity() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C15");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.QTY, "");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCP_NegativeC2CAPIwithBlankFields() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C16");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();

		apiData.put(C2CTransferAPI.MSISDN1, "");
		apiData.put(C2CTransferAPI.PIN, "");
		apiData.put(C2CTransferAPI.LOGINID, "");
		apiData.put(C2CTransferAPI.PASSWORD, "");
		apiData.put(C2CTransferAPI.EXTCODE, "");
		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCQ_NegativeC2CAPIwithInvalidLanguageCode() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C17");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.LANGUAGE1,"a");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCR_NegativeC2CAPIwithInvalidExternalCode() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C18");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.EXTCODE, RandomGeneration.randomNumeric(9));
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCS_PositiveC2CAPIwithSecderAndReceiverExternalCode() throws
			SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C19");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration
				RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test =
					extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.LOGINID, "");
		apiData.put(C2CTransferAPI.MSISDN1, "");
		apiData.put(C2CTransferAPI.LOGINID2, "");
		apiData.put(C2CTransferAPI.MSISDN2, "");
		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new
				XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(),
				CaseMaster.getErrorCode());
	}

	@Test
	public void TCT_PositiveC2CAPIwithSecderAndReceiverExternalCode() throws
			SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C20");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration
				RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test =
					extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.LOGINID, "");
		apiData.put(C2CTransferAPI.MSISDN1, "");
		apiData.put(C2CTransferAPI.LOGINID2, "");
		apiData.put(C2CTransferAPI.EXTCODE2, "");
		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new
				XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(),
				CaseMaster.getErrorCode());
	}

	@Test
	public void TCU_PositiveC2CAPIwithSecderAndReceiverExternalCode() throws
			SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C21");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration
				RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test =
					extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.LOGINID, "");
		apiData.put(C2CTransferAPI.MSISDN1, "");
		apiData.put(C2CTransferAPI.MSISDN2, "");
		apiData.put(C2CTransferAPI.EXTCODE2, "");
		if (!apiData.containsKey(C2CTransferAPI.LOGINID2)) {
			Assertion.assertSkip("Not Valid case for this release");
		} else {
			String API = C2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new
					XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(),
					CaseMaster.getErrorCode());
		}
	}

	@Test
	public void TCV_NegativeC2CAPIwithalphanumericLanguageCode() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C22");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.LANGUAGE1, "a");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCW_NegativeC2CAPIwithalphanumericEXTCODE() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C23");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.EXTCODE, "a");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCX_NegativeC2CAPIwithInvalidMSISDN2() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C24");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.MSISDN2, RandomGeneration.randomNumeric(21));
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCY_NegativeSuspendTCP() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C25");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);

		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWC2CDP.FROM_Domain, EXTGWC2CDP.FROM_Category, EXTGWC2CDP.FROM_TCPName, null);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWC2CDP.FROM_Domain, EXTGWC2CDP.FROM_Category, EXTGWC2CDP.FROM_TCPName, null);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

	}

	@Test
	public void TCZ_1_NegativeSuspendTCP() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C26");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);

		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.TO_TCPName, null);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.TO_TCPName, null);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

	}

	@Test
	public void TCZ_2_Negative_SenderOutSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C27");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(C2CTransferAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWC2CDP.FROM_Category, channelMap);

		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);


		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWC2CDP.FROM_Category, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCZ_3_Negative_ReceiverInSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C28");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(C2CTransferAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWC2CDP.TO_Category, channelMap);

		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);


		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWC2CDP.TO_Category, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCZ_4_Negative__MaxResidualBalance() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C29");
		RandomGeneration RandomGeneration = new RandomGeneration();
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		String API = C2CTransferAPI.prepareAPI(apiData);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying Maximum Residual Balance in Transfer Control Profile");
		trfCntrlProf.modifyTCPmaximumBalance(EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.TO_TCP_ID, "50", "49", EXTGWC2CDP.ProductName);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		ExtentI.Markup(ExtentColor.TEAL, "Updating Maximum Residual Balance in Transfer Control Profile");
		trfCntrlProf.modifyTCPmaximumBalance(EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.TO_TCP_ID, _masterVO.getProperty("MaximumBalance"), _masterVO.getProperty("AlertingCount"), EXTGWC2CDP.ProductName);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TCZ_5_Negative_ReceiverDailyInReached() throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C30");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		Map_TCPValues tcpMap = new Map_TCPValues();
		HashMap<String, String> userData = new HashMap<String, String>();
		TransferControlProfile tcpchange = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getExtentCase());
			TestCaseCounter = true;
		}

		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();

		String[][] valuesToModfiy = new String[][]{{"enterDailyTransferInCount", "1"},
				{"enterDailyTransferInAlertingCount", "1"}};

		String[][] defaultToModify = new String[][]{{"enterDailyTransferInCount", tcpMap.DataMap_TCPCategoryLevel().get("DailyInCount")},
				{"enterDailyTransferInAlertingCount", tcpMap.DataMap_TCPCategoryLevel().get("DailyInAlertingCount")}};

		userData.put("tcpID", EXTGWC2CDP.TO_TCP_ID);
		userData.put("domainName", EXTGWC2CDP.TO_Domain);
		userData.put("categoryName", EXTGWC2CDP.TO_Category);

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily in count to 1");
		tcpchange.modifyAnyTCPValue(valuesToModfiy, userData, "channel");

		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily out count to default");
		tcpchange.modifyAnyTCPValue(defaultToModify, userData, "channel");

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	//To verify that C2C is successful when executed through EXTGW when CBC is not applicable on -Ve Commissioning
	@Test
	public void TCZ_6_PositiveC2CAPICBCNegativeCOMM1() throws SQLException, ParseException {

		String str = null;
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C31");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), apiData.get(C2CTransferAPI.MSISDN1), apiData.get(C2CTransferAPI.MSISDN2));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		initiatedQuantities.put(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.QTY));

		String API = C2CTransferAPI.prepareAPI(apiData);
		ExtentI.Markup(ExtentColor.TEAL, "Adding profile having no cbc for Normal cammissioning");


		try {
			str = CommissionProfile.addcommissonwithoutCBC(EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.grade, PretupsI.Normal_Commission);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (str.equals("skip")) {
			Assertion.assertSkip("This case should be skipped as cbc is not available");
		} else {


			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
		BusinessValidator.validateStocks(TransactionVO);
		*/
		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
	/*	currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		BusinessValidator.validateUserBalances(TransactionVO);*/
	}

	//To verify that C2C is successful when executed through EXTGW when CBC is applicable on -Ve Commissioning
	@Test
	public void TCZ_7_PositiveC2CAPICBCNegativeCOMM2() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C32");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		String str = null;
		String[] args = {};
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), apiData.get(C2CTransferAPI.MSISDN1), apiData.get(C2CTransferAPI.MSISDN2));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		initiatedQuantities.put(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.QTY));

		String API = C2CTransferAPI.prepareAPI(apiData);
		ExtentI.Markup(ExtentColor.TEAL, "Removing cbc Applicability for Normal cammissioning");


		try {
			args = CommissionProfile.addCommissionProfileCBC(EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.grade, PretupsI.Normal_Commission);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if (args[2].equals("skip")) {
			Assertion.assertSkip("This case should be skipped as cbc is not available");
		} else {
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
/*
			
			 * Test Case to validate Network Stocks after successful O2C Transfer
			 
			currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
			currentNode.assignCategory("Smoke");
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
			BusinessValidator.validateStocks(TransactionVO);
			
			
			 * Test Case to validate Channel User balance after successful O2C Transfer
			 
			currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
			currentNode.assignCategory("Smoke");
			BusinessValidator.validateUserBalances(TransactionVO);*/
		}
	}

	//To verify that C2C is successful when executed through EXTGW when CBC is not applicable on +Ve Commissioning
	@Test
	public void TCZ_8_PositiveC2CAPICBCPositiveCOMM1() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C33");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), apiData.get(C2CTransferAPI.MSISDN1), apiData.get(C2CTransferAPI.MSISDN2));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		initiatedQuantities.put(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.QTY));

		String API = C2CTransferAPI.prepareAPI(apiData);
		ExtentI.Markup(ExtentColor.TEAL, "Removing cbc Applicability for Normal cammissioning");


		try {
			CommissionProfile.addcommissonwithoutCBC(EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.grade, PretupsI.Postive_Commission);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 */
		/*	currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
			currentNode.assignCategory("Smoke");
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
			BusinessValidator.validateStocks(TransactionVO);
			
			
			 * Test Case to validate Channel User balance after successful O2C Transfer
			 
			currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
			currentNode.assignCategory("Smoke");
			BusinessValidator.validateUserBalances(TransactionVO);*/
	}


	//To verify that C2C is successful when executed through EXTGW when CBC is applicable on +Ve Commissioning
			@Test
	public void TCZ_9_PositiveC2CAPICBCPositiiveCOMM2() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C34");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), apiData.get(C2CTransferAPI.MSISDN1), apiData.get(C2CTransferAPI.MSISDN2));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		initiatedQuantities.put(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.QTY));

		String API = C2CTransferAPI.prepareAPI(apiData);
		ExtentI.Markup(ExtentColor.TEAL, "Removing cbc Applicability for Normal cammissioning");


		try {
			String[] args = CommissionProfile.addCommissionProfileCBC(EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.grade, PretupsI.Postive_Commission);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

				/*	
					 * Test Case to validate Network Stocks after successful O2C Transfer
					 
					currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
					currentNode.assignCategory("Smoke");
					TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
					BusinessValidator.validateStocks(TransactionVO);
					
					
					 * Test Case to validate Channel User balance after successful O2C Transfer
					 
					currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
					currentNode.assignCategory("Smoke");
					BusinessValidator.validateUserBalances(TransactionVO);*/
	}

	//To verify that user's CBC counts are updated and CBC is credited when c2c is performed through EXTGW.
		@Test
	public void TCZ_10_PositiveC2CAPIO2CAPI_CBCAPPLICABLE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C35");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		HashMap<String, String> OTFValue = null;
		try {
			OTFValue = CommissionProfile.CBCgetAllOTFvalue(EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.grade, "NC");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

		apiData.put(C2CTransferAPI.QTY, OTFValue.get("CBCValue0"));
		String API1 = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		apiData.put(C2CTransferAPI.QTY, "100");
		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString());
		String Transfer_ID = xmlPath.get(EXTGWC2CDP.TXNID);

		String[] OTFApplicable = DBHandler.AccessHandler.checkForOTFApplicable(Transfer_ID);
		if (OTFApplicable[0].equals("Y") && OTFApplicable[1] != null) {
			ExtentI.Markup(ExtentColor.GREEN, " TestCase is Successful");
			currentNode.log(Status.PASS, "TestCase is Successful");
		} else {

			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful");
			currentNode.log(Status.FAIL, "TestCase is not successful ");

		}
	}

	//To verify that CBC is applicable when O2C is made through EXTGW +VE COMMISSIONING
			@Test
	public void TCZ_11_PositiveC2CAPIO2CAPI_CBCAPPLICABLE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C36");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> OTFValue = null;
		try {
			OTFValue = CommissionProfile.CBCgetAllOTFvalue(EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.grade, "PC");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.QTY, OTFValue.get("CBCValue0"));
		String API1 = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		apiData.put(C2CTransferAPI.QTY, "100");
		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString());
		String Transfer_ID = xmlPath.get(EXTGWC2CDP.TXNID);

		String[] OTFApplicable = DBHandler.AccessHandler.checkForOTFApplicable(Transfer_ID);
		if (OTFApplicable[0].equals("Y")) {
			ExtentI.Markup(ExtentColor.GREEN, " TestCase is Successful");
			currentNode.log(Status.PASS, "TestCase is Successful");
		} else {

			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful");
			currentNode.log(Status.FAIL, "TestCase is not successful ");

		}
	}

	//To verify that user's CBC counts are updated and CBC is credited when c2c is performed through EXTGW. +VE COMMISSIONING
		@Test
	public void TCZ_12_PositiveC2CAPIO2CAPI_CBCAPPLICABLE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C37");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		HashMap<String, String> OTFValue = null;
		try {
			OTFValue = CommissionProfile.CBCgetAllOTFvalue(EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.grade, "PC");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

		apiData.put(C2CTransferAPI.QTY, OTFValue.get("CBCValue0"));
		String API1 = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		apiData.put(C2CTransferAPI.QTY, "100");
		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString());
		String Transfer_ID = xmlPath.get(EXTGWC2CDP.TXNID);

		String[] OTFApplicable = DBHandler.AccessHandler.checkForOTFApplicable(Transfer_ID);
		if (OTFApplicable[0].equals("Y") && OTFApplicable[1] != null) {
			ExtentI.Markup(ExtentColor.GREEN, " TestCase is Successful");
			currentNode.log(Status.PASS, "TestCase is Successful");
		} else {

			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful");
			currentNode.log(Status.FAIL, "TestCase is not successful ");

		}
	}

	//To verify that CBC is applicable when O2C is made through EXTGW
	@Test
	public void TCZ_13_PositiveC2CAPIO2CAPI_CBCAPPLICABLE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C38");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		HashMap<String, String> OTFValue = null;
		try {
			OTFValue = CommissionProfile.CBCgetAllOTFvalue(EXTGWC2CDP.TO_Domain, EXTGWC2CDP.TO_Category, EXTGWC2CDP.grade, "NC");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

		apiData.put(C2CTransferAPI.QTY, OTFValue.get("CBCValue0"));
		String API1 = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		apiData.put(C2CTransferAPI.QTY, "100");
		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString());
		String Transfer_ID = xmlPath.get(EXTGWC2CDP.TXNID);

		String[] OTFApplicable = DBHandler.AccessHandler.checkForOTFApplicable(Transfer_ID);
		if (OTFApplicable[0].equals("Y")) {
			ExtentI.Markup(ExtentColor.GREEN, " TestCase is Successful");
			currentNode.log(Status.PASS, "TestCase is Successful");
		} else {

			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful");
			currentNode.log(Status.FAIL, "TestCase is not successful ");

		}
	}

	@Test
	public void TCZ_14_NegativeC2CAPIwithZeroQuantity() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C39");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		apiData.put(C2CTransferAPI.QTY, "0");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

	}


	@Test
	public void TCZ_15_Positive_ReceiverOutSuspended() throws InterruptedException, ParseException, SQLException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C40");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), apiData.get(C2CTransferAPI.MSISDN1), apiData.get(C2CTransferAPI.MSISDN2));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		initiatedQuantities.put(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.QTY));

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(C2CTransferAPI.MSISDN2));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWC2CDP.FROM_Category, channelMap);

		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);


		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWC2CDP.FROM_Category, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
		BusinessValidator.validateStocks(TransactionVO);

		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
		int maxApprovalLevel=0;
		if(BTSLUtil.isNullString(value)) {
			maxApprovalLevel=0;
		}
		else
			maxApprovalLevel = Integer.parseInt(value);
		if(maxApprovalLevel==0) {
			BusinessValidator.validateUserBalances(TransactionVO);
		}else{
			Assertion.assertSkip("Skipping following validation as Approval required");
		}
	}

	@Test
	public void TCZ_16_Positive_SenderINSuspended() throws InterruptedException, ParseException, SQLException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C41");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		businessController businessController = new businessController(_masterVO.getProperty("C2CTransferCode"), apiData.get(C2CTransferAPI.MSISDN1), apiData.get(C2CTransferAPI.MSISDN2));
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		initiatedQuantities.put(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.QTY));

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(C2CTransferAPI.MSISDN1));
		channelMap.put("inSuspend_chk", "True");
		ChannelUser.modifyChannelUserDetails(EXTGWC2CDP.FROM_Category, channelMap);

		String API = C2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);


		channelMap.put("inSuspend_chk", "False");
		ChannelUser.modifyChannelUserDetails(EXTGWC2CDP.FROM_Category, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
		BusinessValidator.validateStocks(TransactionVO);

		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
		int maxApprovalLevel=0;
		if(BTSLUtil.isNullString(value)) {
			maxApprovalLevel=0;
		}
		else
			maxApprovalLevel = Integer.parseInt(value);
		if(maxApprovalLevel==0) {
			BusinessValidator.validateUserBalances(TransactionVO);
		}else{
			Assertion.assertSkip("Skipping following validation as Approval required");
		}
	}

	@Test
	public void TCZ_17_NegativeC2CAPIwithSuspendedUser() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C42");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
		suspendCHNLUser.suspendChannelUser_MSISDN(apiData.get(C2CTransferAPI.MSISDN1), "CASEA_CuspendedO2C");
		ExtentI.Markup(ExtentColor.TEAL, "Approving Channel User Suspend Request");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(apiData.get(C2CTransferAPI.MSISDN1), "CASEA_CuspendedO2C");
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

		ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
		resumeCHNLUser.resumeChannelUser_MSISDN(apiData.get(C2CTransferAPI.MSISDN1), "Auto Resume Remarks");
	}

	@Test
	public void TCZ_18_NegativeC2CAPIwithBarredSender() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C43");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		BarUnbar BarUnbar = new BarUnbar(driver);
		ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
		BarUnbar.barringUser("C2S","SENDER",apiData.get(C2CTransferAPI.MSISDN1));
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
		BarUnbar.unBarringUser("C2S","SENDER",apiData.get(C2CTransferAPI.MSISDN1));
	}
	@Test
	public void TCZ_19_NegativeC2CAPIwithBarredReceiver() throws SQLException, ParseException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2C44");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		BarUnbar BarUnbar = new BarUnbar(driver);
		ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
		BarUnbar.barringUser("C2S","RECEIVER",apiData.get(C2CTransferAPI.MSISDN2));
		String API = C2CTransferAPI.prepareAPI(apiData);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
		BarUnbar.unBarringUser("C2S","RECEIVER",apiData.get(C2CTransferAPI.MSISDN2));
	}
}
				
				
				
				
	
	
	

