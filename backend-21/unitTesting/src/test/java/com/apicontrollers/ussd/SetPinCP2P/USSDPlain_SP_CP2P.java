package com.apicontrollers.ussd.SetPinCP2P;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.pretupsControllers.BTSLUtil;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

public class USSDPlain_SP_CP2P extends BaseTest {
	
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
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void B2_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP02");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.PIN, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void C3_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP03");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.NEWPIN, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void D4_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP04");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.CONFIRMPIN, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void E5_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP05");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.PIN, "0000");
		apiData.put(setPin.NEWPIN, "0000");
		apiData.put(setPin.CONFIRMPIN, "0000");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void F6_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP06");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.CONFIRMPIN, randomGeneration.randomNumeric(4));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void G7_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP07");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.TYPE, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void H8_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP08");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.MSISDN1, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void I9_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP09");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.LANGUAGE1, "");
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void J10_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP10");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.TYPE, randomGeneration.randomAlphabets(6));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void K11_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP11");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.NEWPIN, randomGeneration.randomAlphaNumeric(4));
		String newPin = apiData.get("NEWPIN");
		apiData.put(setPin.CONFIRMPIN, newPin);
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void L12_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP12");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.CONFIRMPIN, randomGeneration.randomAlphaNumeric(4));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void M13_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP13");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.NEWPIN, randomGeneration.randomNumeric(6));
		String newPin = apiData.get("NEWPIN");
		apiData.put(setPin.CONFIRMPIN, newPin);
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void N14_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP14");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.CONFIRMPIN, randomGeneration.randomNumeric(6));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}
	
	@Test
	public void O15_SetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSP15");
		USSDPlain_SP_CP2P_API setPin = new USSDPlain_SP_CP2P_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> apiData = USSDPlain_SP_CP2P_DP.getAPIdata();
		apiData.put(setPin.PIN, randomGeneration.randomNumeric(4));
		String API = setPin.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		Map<String, String> map = BTSLUtil.getQueryMap(APIResponse[1]);
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		USSDPlain_SP_CP2P_DP.setPIN(map.get(USSDPlain_SP_CP2P_API.TXNSTATUS));
	}


}
