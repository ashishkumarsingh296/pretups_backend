package com.apicontrollers.ussd.Last5Transactions;

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

public class USSDPlainLast5Txn extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test//Positive
	public void TCA1_PositiveLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5T01");
		USSDPlainLast5Txn_API last5Txn = new USSDPlainLast5Txn_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5Txn_DP.getAPIdata();
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCB2_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5T02");
		USSDPlainLast5Txn_API last5Txn = new USSDPlainLast5Txn_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5Txn_DP.getAPIdata();
		apiData.put("MSISDN", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCC3_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5T03");
		USSDPlainLast5Txn_API last5Txn = new USSDPlainLast5Txn_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5Txn_DP.getAPIdata();
		apiData.put("PIN", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCD4_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5T04");
		USSDPlainLast5Txn_API last5Txn = new USSDPlainLast5Txn_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5Txn_DP.getAPIdata();
		apiData.put("MSISDN", randomGeneration.randomNumeric(9));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCE5_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5T05");
		USSDPlainLast5Txn_API last5Txn = new USSDPlainLast5Txn_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5Txn_DP.getAPIdata();
		apiData.put("PIN", randomGeneration.randomNumeric(4));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCF6_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5T06");
		USSDPlainLast5Txn_API last5Txn = new USSDPlainLast5Txn_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5Txn_DP.getAPIdata();
		apiData.put("TYPE", randomGeneration.randomAlphabets(10));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCG7_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5T07");
		USSDPlainLast5Txn_API last5Txn = new USSDPlainLast5Txn_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5Txn_DP.getAPIdata();
		apiData.put("TYPE", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCH8_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5T08");
		USSDPlainLast5Txn_API last5Txn = new USSDPlainLast5Txn_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlainLast5Txn_DP.getAPIdata();
		apiData.put("PIN", "0000");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
}
