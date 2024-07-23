package com.apicontrollers.extgw.o2ctransfer;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.Map_TCPValues;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.commons.PretupsI;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_O2CTransfer extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	private String Exisiting_TXN_No = null;
		
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2C01
	 * Positive Test Case For TRFCATEGORY: O2C
	 */
	@Test
	public void TC1A_PositiveO2CAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C01");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
			EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();
			businessController businessController = new businessController(_masterVO.getProperty("O2CTransferCode"), null, apiData.get(O2CTransferAPI.MSISDN));
			
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), APIDAO.getCategory()));
			currentNode.assignCategory(extentCategory);
			apiData.put(O2CTransferAPI.EXTCODE, "");
			Exisiting_TXN_No = apiData.get(O2CTransferAPI.EXTTXNNUMBER);
			TransactionVO TransactionVO = businessController.preparePreTransactionVO();
			TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
			
			String API = O2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			//Test Case to validate Network Stocks after successful O2C Transfer
			currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> initiatedQty = new HashMap<String, String>();
			initiatedQty.put(APIDAO.getProductCode(), apiData.get(O2CTransferAPI.QTY));
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
			BusinessValidator.validateStocks(TransactionVO);
			
			// Test Case to validate Channel User balance after successful O2C Transfer
			currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
			currentNode.assignCategory(extentCategory);
			BusinessValidator.validateUserBalances(TransactionVO);
		}
	}
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * @testid EXTGWO2C02
	 * Positive Test Case For TRFCATEGORY: FOC
	 */
	@Test
	public void TC2_PositiveFOCAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C02");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		Object[] dataObject = EXTGWO2CDP.getAPIdataWithAllUsers();
		
		for (int i = 0; i < dataObject.length; i++) {
			EXTGW_O2CDAO APIDAO = (EXTGW_O2CDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();
			businessController businessController = new businessController(_masterVO.getProperty("FOCCode"), null, apiData.get(O2CTransferAPI.MSISDN));
			
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), APIDAO.getCategory()));
			currentNode.assignCategory(extentCategory);
			apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
			String API = O2CTransferAPI.prepareAPI(apiData);
			TransactionVO TransactionVO = businessController.preparePreTransactionVO();
			TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_EXTGW);
			
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			//Test Case to validate Network Stocks after successful O2C Transfer
			currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
			currentNode.assignCategory(extentCategory);
			HashMap<String, String> initiatedQty = new HashMap<String, String>();
			initiatedQty.put(APIDAO.getProductCode(), apiData.get(O2CTransferAPI.QTY));
			TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
			BusinessValidator.validateStocks(TransactionVO);
			
			// Test Case to validate Channel User balance after successful O2C Transfer
			currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
			currentNode.assignCategory(extentCategory);
			BusinessValidator.validateUserBalances(TransactionVO);
		}
	}
	
	/**
	 * @testid EXTGWO2C03
	 * Positive Test Case for O2C Using Only EXTCODE
	 */
	@Test
	public void TC3_PositiveO2CAPIUsingEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C03");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.MSISDN, "");
		apiData.put(O2CTransferAPI.PIN, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/**
	 * @testid EXTGWO2C04
	 * Positive Test Case for FOC Using Only EXTCODE
	 */
	@Test
	public void TC4_PositiveFOCAPIUsingEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C04");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		apiData.put(O2CTransferAPI.MSISDN, "");
		apiData.put(O2CTransferAPI.PIN, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @throws InterruptedException 
	 * @testid EXTGWO2C05
	 * Positive Test Case for O2C While Channel User is OutSuspended
	 */
	@Test
	public void TC5_PositiveO2CAPIWhileCUOutSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C05");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(O2CTransferAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CDP.CUCategory, channelMap);
		
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @throws InterruptedException 
	 * @testid EXTGWO2C06
	 * Positive Test Case for FOC While Channel User is OutSuspended
	 */
	@Test
	public void TC6_PositiveFOCAPIWhileCUOutSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C06");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(O2CTransferAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CDP.CUCategory, channelMap);
		
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C07
	 * Negative Test Case for O2C While TCP is OutSuspended
	 */
	@Test
	public void TC7_NegativeO2CAPIWhileTCPSuspended() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C07");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWO2CDP.Domain, EXTGWO2CDP.CUCategory, EXTGWO2CDP.TCPName, null);
		
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWO2CDP.Domain, EXTGWO2CDP.CUCategory, EXTGWO2CDP.TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C08
	 * Negative Test Case for FOC While TCP is OutSuspended
	 */
	@Test
	public void TC8_NegativeFOCAPIWhileTCPSuspended() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C08");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWO2CDP.Domain, EXTGWO2CDP.CUCategory, EXTGWO2CDP.TCPName, null);
		
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWO2CDP.Domain, EXTGWO2CDP.CUCategory, EXTGWO2CDP.TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C09
	 * Negative Test Case for O2C While Channel User is Suspended.
	 */
	@Test
	public void TC9_NegativeO2CAPIWhileCUSuspended() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C09");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
		ResumeChannelUser CUResume = new ResumeChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		CUSuspend.suspendChannelUser_MSISDN(apiData.get(O2CTransferAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");
		CUSuspend.approveCSuspendRequest_MSISDN(apiData.get(O2CTransferAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");
		
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		CUResume.resumeChannelUser_MSISDN(apiData.get(O2CTransferAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C10
	 * Negative Test Case for O2C While Channel User is Suspended.
	 */
	@Test
	public void TC10_NegativeFOCAPIWhileCUSuspended() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C10");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
		ResumeChannelUser CUResume = new ResumeChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		CUSuspend.suspendChannelUser_MSISDN(apiData.get(O2CTransferAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");
		CUSuspend.approveCSuspendRequest_MSISDN(apiData.get(O2CTransferAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");
		
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		CUResume.resumeChannelUser_MSISDN(apiData.get(O2CTransferAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C11
	 * Positive Test Case for O2C With only MSISDN & PIN.
	 */
	@Test
	public void TC11_PositiveO2CAPIWithMSISDNAndPIN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C11");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTCODE, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C12
	 * Positive Test Case for FOC With only MSISDN & PIN.
	 */
	@Test
	public void TC12_PositiveFOCAPIWithMSISDNAndPIN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C12");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		apiData.put(O2CTransferAPI.EXTCODE, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C13
	 * Positive Test Case for O2C With Invalid Product Code.
	 */
	@Test
	public void TC13_NegativeO2CAPIWithInvalidProductCode() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C13");
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		apiData.put(O2CTransferAPI.PRODUCTCODE, RandomGeneration.randomNumeric(5));
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C14
	 * Positive Test Case for FOC With Invalid Product Code.
	 */
	@Test
	public void TC14_NegativeFOCAPIWithInvalidProductCode() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C14");
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		apiData.put(O2CTransferAPI.PRODUCTCODE, RandomGeneration.randomNumeric(5));
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C15
	 * Positive Test Case for O2C With Existing EXTTXNNUMBER.
	 */
	@Test
	public void TC15_NegativeO2CAPIWithExisting_EXTTXNNUMBER() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C15");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTTXNNUMBER, Exisiting_TXN_No);
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C16
	 * Positive Test Case for FOC With Existing EXTTXNNUMBER.
	 */
	@Test
	public void TC16_NegativeFOCAPIWithExisting_EXTTXNNUMBER() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C16");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTTXNNUMBER, Exisiting_TXN_No);
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C17
	 * Positive Test Case for O2C With Invalid Date Format.
	 */
	@Test
	public void TC17_NegativeO2CAPIInvalid_DateFormat() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C17");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTTXNDATE, new Date().toString());
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C18
	 * Positive Test Case for FOC With Invalid Date Format.
	 */
	@Test
	public void TC18_NegativeFOCAPIInvalid_DateFormat() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C18");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTTXNDATE, new Date().toString());
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C19
	 * Positive Test Case for O2C With No External Transaction Date.
	 */
	@Test
	public void TC19_NegativeO2CAPI_BlankEXTTXNDATE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C19");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTTXNDATE, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C20
	 * Positive Test Case for FOC With No External Transaction Date.
	 */
	@Test
	public void TC20_NegativeFOCAPI_BlankEXTTXNDATE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C20");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTTXNDATE, "");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C21
	 * Positive Test Case for FOC With Invalid QTY.
	 */
	@Test
	public void TC21_NegativeO2CAPI_InvalidQTY() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C21");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.QTY, "TESTING");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C22
	 * Positive Test Case for FOC With Invalid QTY.
	 */
	@Test
	public void TC22_NegativeFOCAPI_InvalidQTY() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C22");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.QTY, "TESTING");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C23
	 * Negative Test Case for O2C With Reference No. of 15 Characters.
	 */
	@Test
	public void TC23_NegativeO2CAPI_15CharReferenceNo() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C23");
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.REFNUMBER, RandomGeneration.randomNumeric(15));
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C24
	 * Negative Test Case for FOC With Reference No. of 15 Characters.
	 */
	@Test
	public void TC24_NegativeFOCAPI_15CharReferenceNo() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C24");
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.REFNUMBER, RandomGeneration.randomNumeric(15));
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C25
	 * Positive Test Case for O2C With No Reference No..
	 */
	@Test
	public void TC25_PositiveO2CAPI_NoReferenceNo() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C25");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.REFNUMBER, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C26
	 * Positive Test Case for FOC With No Reference No.
	 */
	@Test
	public void TC26_PositiveFOCAPI_NoReferenceNo() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C26");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.REFNUMBER, "");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C27
	 * Positive Test Case for O2C With No Payment Type.
	 */
	@Test
	public void TC27_NegativeO2CAPI_NoPaymentType() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C27");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PAYMENTTYPE, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C28
	 * Positive Test Case for FOC With No Payment Type.
	 */
	@Test
	public void TC28_NegativeFOCAPI_NoPaymentType() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C28");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PAYMENTTYPE, "");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C29
	 * Negative Test Case for O2C With No Payment Date.
	 */
	@Test
	public void TC29_NegativeO2CAPI_NoPaymentDate() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C29");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PAYMENTDATE, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C30
	 * Negative Test Case for FOC With No Payment Date.
	 */
	@Test
	public void TC30_NegativeFOCAPI_NoPaymentDate() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C30");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PAYMENTDATE, "");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C31
	 * Negative Test Case for O2C With Invalid Payment Date Format.
	 */
	@Test
	public void TC31_NegativeO2CAPI_InvalidPaymentDate() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C31");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PAYMENTDATE, new Date().toString());
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C32
	 * Negative Test Case for FOC With Invalid Payment Date Format.
	 */
	//@Test
	public void TC32_NegativeFOCAPI_InvalidPaymentDate() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C32");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PAYMENTDATE, new Date().toString());
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C33
	 * Positive Test Case for O2C Without Remarks.
	 */
	@Test
	public void TC33_NegativeO2CAPI_NoRemarks() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C33");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.REMARKS, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C34
	 * Positive Test Case for FOC Without Remarks.
	 */
	@Test
	public void TC34_NegativeFOCAPI_NoRemarks() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C34");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.REMARKS, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C35
	 * Negative Test Case for O2C With 150 Characters Remarks.
	 */
	@Test
	public void TC35_NegativeO2CAPI_150CharRemarks() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C35");
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.REMARKS, "Automation API Testing " + RandomGeneration.randomAlphaNumeric(120));
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C36
	 * Negative Test Case for FOC With 150 Characters Remarks.
	 */
	@Test
	public void TC36_NegativeFOCAPI_150CharRemarks() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C36");
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.REMARKS, "Automation API Testing " + RandomGeneration.randomAlphaNumeric(120));
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @throws InterruptedException 
	 * @testid EXTGWO2C37
	 * Negative Test Case for O2C While Channel User is InSuspended
	 */
	@Test
	public void TC37_NegativeO2CAPIWhileCUInSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C37");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(O2CTransferAPI.MSISDN));
		channelMap.put("inSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CDP.CUCategory, channelMap);
		
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		channelMap.put("inSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @throws InterruptedException 
	 * @testid EXTGWO2C38
	 * Negative Test Case for FOC While Channel User is InSuspended
	 */
	@Test
	public void TC38_NegativeFOCAPIWhileCUInSuspended() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C38");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(O2CTransferAPI.MSISDN));
		channelMap.put("inSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CDP.CUCategory, channelMap);
		
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		channelMap.put("inSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWO2CDP.CUCategory, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C39
	 * Negative Test Case for O2C Without MSISDN / PIN & EXTCODE.
	 */
	@Test
	public void TC39_NegativeO2CAPI_WithoutPINMSISDNEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C39");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.MSISDN, "");
		apiData.put(O2CTransferAPI.PIN, "");
		apiData.put(O2CTransferAPI.EXTCODE, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C40
	 * Negative Test Case for FOC Without MSISDN / PIN & EXTCODE.
	 */
	@Test
	public void TC40_NegativeFOCAPI_WithoutPINMSISDNEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C40");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.MSISDN, "");
		apiData.put(O2CTransferAPI.PIN, "");
		apiData.put(O2CTransferAPI.EXTCODE, "");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C41
	 * Negative Test Case for O2C Without EXTTXNNUMBER.
	 */
	@Test
	public void TC41_NegativeO2CAPI_WithoutEXTTXNNUMBER() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C41");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTTXNNUMBER, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C42
	 * Negative Test Case for FOC Without EXTTXNNUMBER.
	 */
	@Test
	public void TC42_NegativeFOCAPI_WithoutEXTTXNNUMBER() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C42");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTTXNNUMBER, "");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C43
	 * Negative Test Case for O2C Without PIN & EXTCODE.
	 */
	@Test
	public void TC43_NegativeO2CAPI_WithoutPIN_EXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C43");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PIN, "");
		apiData.put(O2CTransferAPI.EXTCODE, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C44
	 * Negative Test Case for FOC Without PIN & EXTCODE.
	 */
	@Test
	public void TC44_NegativeFOCAPI_WithoutPIN_EXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C44");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PIN, "");
		apiData.put(O2CTransferAPI.EXTCODE, "");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C45
	 * Negative Test Case for O2C with Invalid EXTCODE.
	 */
	@Test
	public void TC45_NegativeO2CAPI_InvalidEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C45");
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTCODE, RandomGeneration.randomAlphaNumeric(10));
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C46
	 * Negative Test Case for FOC with Invalid EXTCODE.
	 */
	@Test
	public void TC46_NegativeFOCAPI_InvalidEXTCODE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C46");
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.EXTCODE, RandomGeneration.randomAlphaNumeric(10));
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C47
	 * Negative Test Case for O2C With No ProductCode.
	 */
	@Test
	public void TC47_NegativeO2CAPI_NoProductCode() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C47");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PRODUCTCODE, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C48
	 * Negative Test Case for FOC With No ProductCode.
	 */
	@Test
	public void TC48_NegativeFOCAPI_NoProductCode() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C48");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PRODUCTCODE, "");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	/** 
	 * @testid EXTGWO2C49
	 * Negative Test Case for O2C With No Quantity.
	 */
	@Test
	public void TC49_NegativeO2CAPI_NoQty() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C49");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.QTY, "");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C50
	 * Negative Test Case for FOC With No Quantity.
	 */
	@Test
	public void TC50_NegativeFOCAPI_NoQty() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C50");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.QTY, "");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C51
	 * Negative Test Case for O2C With Negative Quantity.
	 */
	@Test
	public void TC51_NegativeFOCAPI_NegativeQty() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C51");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.QTY, "-100");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C52
	 * Negative Test Case for FOC With Negative Quantity.
	 */
	@Test
	public void TC52_NegativeFOCAPI_NegativeQty() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C52");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.QTY, "-100");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C53
	 * Negative Test Case for O2C With Decimal Quantity.
	 */
	@Test
	public void TC53_NegativeO2CAPI_DecimalQty() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C53");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.QTY, "100.50");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C54
	 * Negative Test Case for FOC With Decimal Quantity.
	 */
	@Test
	public void TC54_NegativeFOCAPI_DecimalQty() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C54");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.QTY, "100.50");
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C55
	 * Negative Test Case for O2C With Invalid PIN.
	 */
	@Test
	public void TC55_NegativeO2CAPI_InvalidPIN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C55");
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PIN, RandomGeneration.randomNumeric(4));
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C56
	 * Negative Test Case for FOC With Invalid PIN.
	 */
	@Test
	public void TC56_NegativeFOCAPI_InvalidPIN() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C56");
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		apiData.put(O2CTransferAPI.PIN, RandomGeneration.randomNumeric(4));
		apiData.put(O2CTransferAPI.TRFCATEGORY, "FOC");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @testid EXTGWO2C57
	 * Negative Test Case for O2C With Max Residual Balance.
	 */
	@Test
	public void TC57_NegativeO2CAPI_MaxResidualBalance() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C57");
		RandomGeneration RandomGeneration = new RandomGeneration();
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		String API = O2CTransferAPI.prepareAPI(apiData);
		
		ExtentI.Markup(ExtentColor.TEAL, "Modifying Maximum Residual Balance in Transfer Control Profile");
		trfCntrlProf.modifyTCPmaximumBalance(EXTGWO2CDP.Domain, EXTGWO2CDP.CUCategory, EXTGWO2CDP.NA_TCP_ID, "50","49", EXTGWO2CDP.ProductName);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		
		ExtentI.Markup(ExtentColor.TEAL, "Updating Maximum Residual Balance in Transfer Control Profile");
		trfCntrlProf.modifyTCPmaximumBalance(EXTGWO2CDP.Domain, EXTGWO2CDP.CUCategory, EXTGWO2CDP.NA_TCP_ID, _masterVO.getProperty("MaximumBalance"), _masterVO.getProperty("AlertingCount"), EXTGWO2CDP.ProductName);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	/** 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @testid EXTGWO2C58
	 * Negative Test Case for O2C With Daily Transfer In Count Reached.
	 */
	@Test
	public void TC58_NegativeO2CAPI_DailyInCountReached() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C58");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		Map_TCPValues tcpMap = new Map_TCPValues();
		HashMap<String, String> userData = new HashMap<String, String>();
		TransferControlProfile tcpchange = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		
	    String[][] valuesToModfiy = new String[][]{ {"enterDailyTransferInCount","1"}, 
	    										  {"enterDailyTransferInAlertingCount","1"}};
		
	    String[][] defaultToModify = new String[][]{ {"enterDailyTransferInCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyInCount")}, 
				  								 {"enterDailyTransferInAlertingCount",tcpMap.DataMap_TCPCategoryLevel().get("DailyInAlertingCount")}};
	    
		userData.put("tcpID", EXTGWO2CDP.NA_TCP_ID);
		userData.put("domainName", EXTGWO2CDP.Domain);
		userData.put("categoryName", EXTGWO2CDP.CUCategory);
		  
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);					
	    ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily in count to 1");
	    tcpchange.modifyAnyTCPValue(valuesToModfiy, userData,"channel");
	    
	    String API = O2CTransferAPI.prepareAPI(apiData);
	    String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		
	    ExtentI.Markup(ExtentColor.TEAL, "Modifying TCP daily out count to default");
		tcpchange.modifyAnyTCPValue(defaultToModify, userData,"channel");  
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	//To verify that CBC is applicable when O2C is made through EXTGW
//	@Test
	public void TC33_POSITIVEO2CAPI_CBCAPPLICABLE() {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C58");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		HashMap<String, String> OTFValue=null;
		try {
			OTFValue = CommissionProfile.CBCgetAllOTFvalue(EXTGWO2CDP.Domain, EXTGWO2CDP.CUCategory, EXTGWO2CDP.grade,"NC");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		apiData.put(O2CTransferAPI.QTY, OTFValue.get("CBCValue0"));
		String API1 = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		apiData.put(O2CTransferAPI.QTY,"100");
		String API = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString());
        String Transfer_ID = xmlPath.get(EXTGWO2CDP.TXNID);

		String []OTFApplicable = DBHandler.AccessHandler.checkForOTFApplicable(Transfer_ID);
		if(OTFApplicable[0].equals("Y")){
			ExtentI.Markup(ExtentColor.GREEN, " TestCase is Successful");
			currentNode.log(Status.PASS,"TestCase is Successful");
		}
		else{
			
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful");
				currentNode.log(Status.FAIL, "TestCase is not successful ");
			
		}
	}
	
	//To verify that user's CBC counts are updated and CBC is credited when O2C is performed through EXTGW.
	//	@Test
		public void TC34_POSITIVEO2CAPI_CBCAPPLICABLE() {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C57");
			EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
			CommissionProfile CommissionProfile = new CommissionProfile(driver);
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);			
			HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
			HashMap<String, String> OTFValue=null;
			try {
				OTFValue = CommissionProfile.CBCgetAllOTFvalue(EXTGWO2CDP.Domain, EXTGWO2CDP.CUCategory, EXTGWO2CDP.grade,"NC");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		
		
			apiData.put(O2CTransferAPI.QTY, OTFValue.get("CBCValue0"));
			String API1 = O2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
			apiData.put(O2CTransferAPI.QTY,"100");
			String API = O2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Log.info("The Txn status is " + xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString());
	        String Transfer_ID = xmlPath.get(EXTGWO2CDP.TXNID);

			String []OTFApplicable = DBHandler.AccessHandler.checkForOTFApplicable(Transfer_ID);
			if(OTFApplicable[0].equals("Y")&&OTFApplicable[1]!=null ){
				ExtentI.Markup(ExtentColor.GREEN, " TestCase is Successful");
				currentNode.log(Status.PASS,"TestCase is Successful");
			}
			else{
				
					ExtentI.Markup(ExtentColor.RED, "TestCase is not successful");
					currentNode.log(Status.FAIL, "TestCase is not successful ");
				
			}
		}
		
		//To verify that CBC is applicable when O2C is made through EXTGW +VE COMM
	//	@Test
		public void TC35_POSITIVEO2CAPI_CBCAPPLICABLE() {

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C57");
			EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
			CommissionProfile CommissionProfile = new CommissionProfile(driver);
			if (TestCaseCounter == false) {
				test = extent.createTest(CaseMaster.getModuleCode());
				TestCaseCounter = true;
			}
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);			
			HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
			HashMap<String, String> OTFValue=null;
			try {
		
				OTFValue = CommissionProfile.CBCgetAllOTFvalue(EXTGWO2CDP.Domain, EXTGWO2CDP.CUCategory, EXTGWO2CDP.grade,"PC");
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			
			apiData.put(O2CTransferAPI.QTY, OTFValue.get("CBCValue0"));
			String API1 = O2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
			apiData.put(O2CTransferAPI.QTY,"100");
			String API = O2CTransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Log.info("The Txn status is " + xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString());
	        String Transfer_ID = xmlPath.get(EXTGWO2CDP.TXNID);

			String []OTFApplicable = DBHandler.AccessHandler.checkForOTFApplicable(Transfer_ID);
			if(OTFApplicable[0].equals("Y")){
				ExtentI.Markup(ExtentColor.GREEN, " TestCase is Successful");
				currentNode.log(Status.PASS,"TestCase is Successful");
			}
			else{
				
					ExtentI.Markup(ExtentColor.RED, "TestCase is not successful");
					currentNode.log(Status.FAIL, "TestCase is not successful ");
				
			}
		}
		
		//To verify that user's CBC counts are updated and CBC is credited when O2C is performed through EXTGW. +VE COMM
		//	@Test
			public void TC36_POSITIVEO2CAPI_CBCAPPLICABLE() {

				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWO2C57");
				EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
				CommissionProfile CommissionProfile = new CommissionProfile(driver);
				if (TestCaseCounter == false) {
					test = extent.createTest(CaseMaster.getModuleCode());
					TestCaseCounter = true;
				}
				
				HashMap<String, String> OTFValue=null;
				currentNode = test.createNode(CaseMaster.getExtentCase());
				currentNode.assignCategory(extentCategory);			
				HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
				try {
				
					OTFValue = CommissionProfile.CBCgetAllOTFvalue(EXTGWO2CDP.Domain, EXTGWO2CDP.CUCategory, EXTGWO2CDP.grade,"NC");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
				
				apiData.put(O2CTransferAPI.QTY, OTFValue.get("CBCValue0"));
				String API1 = O2CTransferAPI.prepareAPI(apiData);
				String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
				apiData.put(O2CTransferAPI.QTY,"100");
				String API = O2CTransferAPI.prepareAPI(apiData);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
				_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Log.info("The Txn status is " + xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString());
		        String Transfer_ID = xmlPath.get(EXTGWO2CDP.TXNID);

				String []OTFApplicable = DBHandler.AccessHandler.checkForOTFApplicable(Transfer_ID);
				if(OTFApplicable[0].equals("Y")&&OTFApplicable[1]!=null ){
					ExtentI.Markup(ExtentColor.GREEN, " TestCase is Successful");
					currentNode.log(Status.PASS,"TestCase is Successful");
				}
				else{
					
						ExtentI.Markup(ExtentColor.RED, "TestCase is not successful");
						currentNode.log(Status.FAIL, "TestCase is not successful ");
					
				}
			}
		
		
		
		
}
