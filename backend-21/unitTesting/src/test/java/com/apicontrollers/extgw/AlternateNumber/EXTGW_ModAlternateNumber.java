package com.apicontrollers.extgw.AlternateNumber;


import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_ModAlternateNumber extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/*
	 * @throws Exception 
	 * @test Id EXTGWMODALTNMBR01
	 * ADD ALTERNATE SUCCESSFULL WITH ALL CORRECT DATA
	 */
	
	@Test
	public void _01_ModAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWMODALTNMBR01");
		EXTGWMODALTNUMERAPI modAlternateNumberAPI = new EXTGWMODALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWMODALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
			
		String API = modAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWMODALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	
	@Test
	public void _02_ModAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWMODALTNMBR02");
		EXTGWMODALTNUMERAPI modAlternateNumberAPI = new EXTGWMODALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWMODALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		apiData.put(EXTGWMODALTNUMERAPI.MSISDN, "");
		apiData.put(EXTGWMODALTNUMERAPI.PIN,"");
		
		apiData.put(EXTGWMODALTNUMERAPI.EXTCODE, "");
			
		String API = modAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWMODALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	
	@Test
	public void _03_ModAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWMODALTNMBR03");
		EXTGWMODALTNUMERAPI modAlternateNumberAPI = new EXTGWMODALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWMODALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		apiData.put(EXTGWMODALTNUMERAPI.LOGINID, "");
		apiData.put(EXTGWMODALTNUMERAPI.PASSWORD,"");
		
		apiData.put(EXTGWMODALTNUMERAPI.EXTCODE, "");
			
		String API = modAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWMODALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void _04_ModAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWMODALTNMBR04");
		EXTGWMODALTNUMERAPI modAlternateNumberAPI = new EXTGWMODALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWMODALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		apiData.put(EXTGWMODALTNUMERAPI.LOGINID, "");
		apiData.put(EXTGWMODALTNUMERAPI.PASSWORD,"");
		
		apiData.put(EXTGWMODALTNUMERAPI.MSISDN, "");
		apiData.put(EXTGWMODALTNUMERAPI.PIN, "");
			
		String API = modAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWMODALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void _05_ModAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWMODALTNMBR05");
		EXTGWMODALTNUMERAPI modAlternateNumberAPI = new EXTGWMODALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWMODALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		apiData.put(EXTGWMODALTNUMERAPI.LOGINID, "");
		apiData.put(EXTGWMODALTNUMERAPI.PASSWORD,"");
		
		apiData.put(EXTGWMODALTNUMERAPI.MSISDN, "");
		apiData.put(EXTGWMODALTNUMERAPI.PIN, "");
		
		apiData.put(EXTGWMODALTNUMERAPI.EXTCODE, "");
			
		String API = modAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWMODALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void _06_ModAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWMODALTNMBR06");
		EXTGWMODALTNUMERAPI modAlternateNumberAPI = new EXTGWMODALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWMODALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		
		apiData.put(EXTGWMODALTNUMERAPI.ALTMSISDN, "");
			
		String API = modAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWMODALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	
	@Test
	public void _07_ModAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWMODALTNMBR07");
		EXTGWMODALTNUMERAPI modAlternateNumberAPI = new EXTGWMODALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWMODALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		
		apiData.put(EXTGWMODALTNUMERAPI.NEWMSISDN, "");
			
		String API = modAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWMODALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void _08_ModAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWMODALTNMBR08");
		EXTGWMODALTNUMERAPI modAlternateNumberAPI = new EXTGWMODALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWMODALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		
		apiData.put(EXTGWMODALTNUMERAPI.ALTMSISDN, "999999");
			
		String API = modAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWMODALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	
	@Test
	public void _09_ModAlternateNumberAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWMODALTNMBR09");
		EXTGWMODALTNUMERAPI modAlternateNumberAPI = new EXTGWMODALTNUMERAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		HashMap<String, String> apiData = EXTGWMODALTRNUMBERDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		
		apiData.put(EXTGWMODALTNUMERAPI.NEWMSISDN, apiData.get(EXTGWMODALTNUMERAPI.ALTMSISDN));
			
		String API = modAlternateNumberAPI.prepareAPI(apiData);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWMODALTNUMERAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
}
 