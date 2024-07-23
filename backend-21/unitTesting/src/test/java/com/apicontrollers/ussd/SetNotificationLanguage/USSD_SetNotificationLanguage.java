package com.apicontrollers.ussd.SetNotificationLanguage;

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

public class USSD_SetNotificationLanguage extends BaseTest  {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TC1_PositiveSNLAPI() {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSNL01");
		USSD_SNLAPI setNotificationLanguageAPI = new USSD_SNLAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SNLDP.getAPIdata();
		
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC2_NegativeSNLAPI() {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSNL02");
		USSD_SNLAPI setNotificationLanguageAPI = new USSD_SNLAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.MSISDN1, "");
		apiData.put(setNotificationLanguageAPI.PIN, "");
		apiData.put(setNotificationLanguageAPI.LANGUAGE1, "");
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC3_NegativeSNLAPI() {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSNL03");
		USSD_SNLAPI setNotificationLanguageAPI = new USSD_SNLAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.LANGUAGE1, RandomGeneration.randomNumeric(3));
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC4_NegativeSNLAPI() {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSNL04");
		USSD_SNLAPI setNotificationLanguageAPI = new USSD_SNLAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.MSISDN1, RandomGeneration.randomNumeric(10)+"@");
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC5_NegativeSNLAPI() {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSNL05");
		USSD_SNLAPI setNotificationLanguageAPI = new USSD_SNLAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.PIN, RandomGeneration.randomNumeric(4));
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC6_NegativeSNLAPI() {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSNL06");
		USSD_SNLAPI setNotificationLanguageAPI = new USSD_SNLAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.MSISDN1, "");
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC7_NegativeSNLAPI() {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSNL07");
		USSD_SNLAPI setNotificationLanguageAPI = new USSD_SNLAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.PIN, "");
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC8_NegativeSNLAPI() {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDSNL08");
		USSD_SNLAPI setNotificationLanguageAPI = new USSD_SNLAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.LANGUAGE1, "");
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}


}
