package com.apicontrollers.ussd.Last5TxnForParticularMSISDN;

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

public class USSDPlainLast5TxnWidMSISDN extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test//Positive
	public void TCA1_PositiveLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM01");
		USSDPlainLast5TxnWidMSISDN_API last5Txn = new USSDPlainLast5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5TxnWidMSISDN_DP.getAPIdata();
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCB2_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM02");
		USSDPlainLast5TxnWidMSISDN_API last5Txn = new USSDPlainLast5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("MSISDN", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCC3_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM03");
		USSDPlainLast5TxnWidMSISDN_API last5Txn = new USSDPlainLast5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("PIN", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCD4_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM04");
		USSDPlainLast5TxnWidMSISDN_API last5Txn = new USSDPlainLast5TxnWidMSISDN_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("MSISDN", randomGeneration.randomNumeric(9));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCE5_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM05");
		USSDPlainLast5TxnWidMSISDN_API last5Txn = new USSDPlainLast5TxnWidMSISDN_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("PIN", randomGeneration.randomNumeric(4));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCF6_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM06");
		USSDPlainLast5TxnWidMSISDN_API last5Txn = new USSDPlainLast5TxnWidMSISDN_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("TYPE", randomGeneration.randomAlphabets(10));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCG7_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM07");
		USSDPlainLast5TxnWidMSISDN_API last5Txn = new USSDPlainLast5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("TYPE", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCH8_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM08");
		USSDPlainLast5TxnWidMSISDN_API last5Txn = new USSDPlainLast5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("PIN", "0000");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCI9_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM09");
		USSDPlainLast5TxnWidMSISDN_API last5Txn = new USSDPlainLast5TxnWidMSISDN_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("MSISDN2", randomGeneration.randomNumeric(9));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCJ10_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM10");
		USSDPlainLast5TxnWidMSISDN_API last5Txn = new USSDPlainLast5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("MSISDN2", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
}
