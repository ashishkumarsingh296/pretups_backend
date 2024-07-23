package com.apicontrollers.ussd.FixedLineRecharge;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

public class USSDPlain_FixedLine extends BaseTest {
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TC1_PositiveFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL01");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC2_PositiveFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL02");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.LANGUAGE1, "");
		apiData.put(fixedLineAPI.LANGUAGE2, "");
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC3_PositiveFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL03");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC4_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL04");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.MSISDN1, "");
		apiData.put(fixedLineAPI.MSISDN2, "");
		apiData.put(fixedLineAPI.NOTIFICATION_MSISDN, "");
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC5_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL05");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.PIN, "");
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCF6_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL06");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.AMOUNT, "");
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCG7_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL07");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.SELECTOR, "");
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCH8_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL08");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.MSISDN2, "");
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCI9_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL09");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.NOTIFICATION_MSISDN, "");
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCJ10_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL10");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.AMOUNT, "-100");
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCK11_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL11");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.SELECTOR, "-100");
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCL12_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL12");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.MSISDN1, randomGeneration.randomNumeric(10));
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}

	@Test
	public void TCL13_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL13");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.NOTIFICATION_MSISDN, randomGeneration.randomNumeric(10));
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCL14_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL14");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.PIN, randomGeneration.randomNumeric(4));
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCL15_NegativeFLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDFL15");
		USSDPlain_FixedLine_API fixedLineAPI = new USSDPlain_FixedLine_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_FixedLine_DP.getAPIdata();
		apiData.put(fixedLineAPI.SELECTOR, randomGeneration.randomNumeric(4));
		String API = fixedLineAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}

}
