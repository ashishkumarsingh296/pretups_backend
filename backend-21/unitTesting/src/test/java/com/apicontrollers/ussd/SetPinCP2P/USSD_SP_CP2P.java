package com.apicontrollers.ussd.SetPinCP2P;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.apicontrollers.ussd.P2PRegistration.USSD_P2PREG_API;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.ExtentI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_SP_CP2P extends BaseTest {
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 *  USSDSP01
	 * Positive Test Case For Set PIN
	 */
	@Test
	public void A1_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP01");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void B2_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP02");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.PIN, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void C3_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP03");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.NEWPIN, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void D4_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP04");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.CONFIRMPIN, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void E5_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP05");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.PIN, "0000");
		apiData.put(setPin.NEWPIN, "0000");
		apiData.put(setPin.CONFIRMPIN, "0000");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void F6_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP06");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.CONFIRMPIN, randomGeneration.randomNumeric(4));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void G7_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP07");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.TYPE, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void H8_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP08");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.MSISDN1, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void I9_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP09");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.LANGUAGE1, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void J10_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP10");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.TYPE, randomGeneration.randomAlphabets(6));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void K11_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP11");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.NEWPIN, randomGeneration.randomAlphaNumeric(4));
		String newPin = apiData.get("NEWPIN");
		apiData.put(setPin.CONFIRMPIN, newPin);
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void L12_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP12");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.CONFIRMPIN, randomGeneration.randomAlphaNumeric(4));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void M13_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP13");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.NEWPIN, randomGeneration.randomNumeric(8));
		String newPin = apiData.get("NEWPIN");
		apiData.put(setPin.CONFIRMPIN, newPin);
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void N14_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP14");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.CONFIRMPIN, randomGeneration.randomNumeric(6));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	@Test
	public void O15_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP15");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.PIN, randomGeneration.randomNumeric(4));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	//To verify that registered subscriber is able to change self pin
	@Test
	public void O16_SetPinREGPostiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP16");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();
		USSD_P2PREG_API RechargeReg = new USSD_P2PREG_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		HashMap<String, String> preapiData2 =new HashMap<String, String>();
		preapiData2.put(RechargeReg.MSISDN,apiData.get("MSISDN1"));	
		preapiData2.put(RechargeReg.TYPE, "REGREQ");	
		preapiData2.put(RechargeReg.SUB_TYPE, "PRE");
		String preP2PAPI = RechargeReg.prepareAPI(preapiData2);
		_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
		apiData.put(setPin.PIN, Decrypt.decryption(DBHandler.AccessHandler.getSubscriberP2PPin(apiData.get("MSISDN1"))));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
//To verify that registered subscriber is not able to change self PIN with continuous and repetitive digits.
@Test
	public void O17_SetPinREGNegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP17");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();
		USSD_P2PREG_API RechargeReg = new USSD_P2PREG_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		HashMap<String, String> preapiData2 =new HashMap<String, String>();
		preapiData2.put(RechargeReg.MSISDN,apiData.get("MSISDN1"));	
		preapiData2.put(RechargeReg.TYPE, "REGREQ");	
		preapiData2.put(RechargeReg.SUB_TYPE, "PRE");
		String preP2PAPI = RechargeReg.prepareAPI(preapiData2);
		_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
		DBHandler.AccessHandler.getSubscriberP2PPin(apiData.get("MSISDN1"));
		apiData.put(setPin.PIN,Decrypt.decryption(DBHandler.AccessHandler.getSubscriberP2PPin(apiData.get("MSISDN1"))));
		apiData.put(setPin.NEWPIN, "1234");
		apiData.put(setPin.CONFIRMPIN, "1234");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	//To Verify that User is not getting blocked on entering Incorrect New Pin after the available attempts.
	@Test
	public void O18_SetPinAPIPINBlock() throws SQLException, ParseException {
		String pinblockcount=null;
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP18");
		USSD_SP_CP2P_API setPin = new USSD_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		USSD_P2PREG_API RechargeReg = new USSD_P2PREG_API();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSD_SP_CP2P_DP.getAPIdata();
		HashMap<String, String> preapiData2 =new HashMap<String, String>();
		preapiData2.put(RechargeReg.MSISDN,apiData.get("MSISDN1"));	
		preapiData2.put(RechargeReg.TYPE, "REGREQ");	
		preapiData2.put(RechargeReg.SUB_TYPE, "PRE");
		String preP2PAPI = RechargeReg.prepareAPI(preapiData2);
		_APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, preP2PAPI);
		pinblockcount=DBHandler.AccessHandler.getPinBlockCount(apiData.get("MSISDN1"));
		apiData.put(setPin.PIN,Decrypt.decryption((DBHandler.AccessHandler.getSubscriberP2PPin(apiData.get("MSISDN1")))));
		apiData.put(setPin.CONFIRMPIN, "2345");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		if(pinblockcount.equals(DBHandler.AccessHandler.getPinBlockCount(apiData.get("MSISDN1"))))
		{
			 ExtentI.Markup(ExtentColor.GREEN, "Pin block count not increased hence the test case is sucessful");
             currentNode.log(Status.PASS, "Pin block count not increased hence the test case is sucessful");
			
		}
		else
		{
			
			  ExtentI.Markup(ExtentColor.RED, "Pin block count  increased hence the test case is not sucessful");
              currentNode.log(Status.FAIL, "Pin block count increased hence the test case is not sucessful");
			
		}
	
		
		USSD_SP_CP2P_DP.setPIN(xmlPath.get(USSD_SP_CP2P_API.TXNSTATUS).toString());
	}
	
	
}
