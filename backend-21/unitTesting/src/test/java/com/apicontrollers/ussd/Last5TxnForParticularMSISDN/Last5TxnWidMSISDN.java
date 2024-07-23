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

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class Last5TxnWidMSISDN extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test//Positive
	public void TCA1_PositiveLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM01");
		Last5TxnWidMSISDN_API last5Txn = new Last5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = Last5TxnWidMSISDN_DP.getAPIdata();
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(last5Txn.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCB2_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM02");
		Last5TxnWidMSISDN_API last5Txn = new Last5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = Last5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("MSISDN", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(last5Txn.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCC3_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM03");
		Last5TxnWidMSISDN_API last5Txn = new Last5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = Last5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("PIN", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(last5Txn.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCD4_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM04");
		Last5TxnWidMSISDN_API last5Txn = new Last5TxnWidMSISDN_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = Last5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("MSISDN", randomGeneration.randomNumeric(9));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(last5Txn.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCE5_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM05");
		Last5TxnWidMSISDN_API last5Txn = new Last5TxnWidMSISDN_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = Last5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("PIN", randomGeneration.randomNumeric(4));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(last5Txn.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCF6_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM06");
		Last5TxnWidMSISDN_API last5Txn = new Last5TxnWidMSISDN_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = Last5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("TYPE", randomGeneration.randomAlphabets(10));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(last5Txn.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCG7_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM07");
		Last5TxnWidMSISDN_API last5Txn = new Last5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = Last5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("TYPE", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(last5Txn.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCH8_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM08");
		Last5TxnWidMSISDN_API last5Txn = new Last5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = Last5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("PIN", "0000");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(last5Txn.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCI9_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM09");
		Last5TxnWidMSISDN_API last5Txn = new Last5TxnWidMSISDN_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = Last5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("MSISDN2", randomGeneration.randomNumeric(9));
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(last5Txn.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test//Negative
	public void TCJ10_NegativeLast5TxnAPI() throws Exception {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDL5TM10");
		Last5TxnWidMSISDN_API last5Txn = new Last5TxnWidMSISDN_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = Last5TxnWidMSISDN_DP.getAPIdata();
		apiData.put("MSISDN2", "");
		String API = last5Txn.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(last5Txn.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
}
