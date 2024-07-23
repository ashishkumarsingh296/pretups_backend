package com.apicontrollers.extgw.SetNotificationLanguage;

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

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_SetNotificationLanguage extends BaseTest {
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TC1_PositiveSNLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSNL01");
		EXTGW_SNLAPI setNotificationLanguageAPI = new EXTGW_SNLAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_SNLDP.getAPIdata();
		
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC2_NegativeSNLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSNL02");
		EXTGW_SNLAPI setNotificationLanguageAPI = new EXTGW_SNLAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.MSISDN1, "");
		apiData.put(setNotificationLanguageAPI.PIN, "");
		apiData.put(setNotificationLanguageAPI.LANGUAGE1, "");
		apiData.put(setNotificationLanguageAPI.EXTNWCODE, "");
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}

	@Test
	public void TC3_NegativeSNLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSNL03");
		EXTGW_SNLAPI setNotificationLanguageAPI = new EXTGW_SNLAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.LANGUAGE1, RandomGeneration.randomNumeric(3));
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC4_NegativeSNLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSNL04");
		EXTGW_SNLAPI setNotificationLanguageAPI = new EXTGW_SNLAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.EXTNWCODE, RandomGeneration.randomAlphabets(2));
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC5_NegativeSNLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSNL05");
		EXTGW_SNLAPI setNotificationLanguageAPI = new EXTGW_SNLAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.MSISDN1, RandomGeneration.randomNumeric(10));
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC6_NegativeSNLAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSNL06");
		EXTGW_SNLAPI setNotificationLanguageAPI = new EXTGW_SNLAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_SNLDP.getAPIdata();
		apiData.put(setNotificationLanguageAPI.PIN, RandomGeneration.randomNumeric(4));
		String API = setNotificationLanguageAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(setNotificationLanguageAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
}
