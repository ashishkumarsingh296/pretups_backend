package com.apicontrollers.extgw.channelusermodify;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;



public class EXTGW_USERMODIFY extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void _01_modifyUserDetails() throws IOException{
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERMODREQ1");

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		EXTGWUSERMODDP iMap= new EXTGWUSERMODDP();
		EXTGWUSERMODAPI iAPI = new EXTGWUSERMODAPI();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
	
		HashMap<String,String> extgwUserModMap = new HashMap<String, String>();
		extgwUserModMap=iMap.getAPIData();
		
		if(!DBHandler.AccessHandler.pinPreferenceForTXN("Operator").equals("Y"))
		{
			extgwUserModMap.put(iAPI.MSISDN, "");
			extgwUserModMap.put(iAPI.PIN, "");
		}
		
		extgwUserModMap.put(iAPI.EMAILID, "newEmailID"+new RandomGeneration().randomNumeric(3)+"@mail.com");
		
		String API = iAPI.prepareAPI(extgwUserModMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERMODAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());}

	
	@Test
	public void _02_modifyUserDetails() throws IOException{
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERMODREQ2");

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		EXTGWUSERMODDP iMap= new EXTGWUSERMODDP();
		EXTGWUSERMODAPI iAPI = new EXTGWUSERMODAPI();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
	
		HashMap<String,String> extgwUserModMap = new HashMap<String, String>();
		extgwUserModMap=iMap.getAPIData();
		
			extgwUserModMap.put(iAPI.MSISDN, "");
			extgwUserModMap.put(iAPI.PIN, "");
			extgwUserModMap.put(iAPI.LOGINID, "");
			extgwUserModMap.put(iAPI.PASSWORD,"");
			extgwUserModMap.put(iAPI.ADDRESS1, "NewADD1"+new RandomGeneration().randomNumeric(5));
			
		String API = iAPI.prepareAPI(extgwUserModMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERMODAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());}

	@Test
	public void _03_modifyUserDetails() throws IOException{
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERMODREQ3");

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		EXTGWUSERMODDP iMap= new EXTGWUSERMODDP();
		EXTGWUSERMODAPI iAPI = new EXTGWUSERMODAPI();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
	
		HashMap<String,String> extgwUserModMap = new HashMap<String, String>();
		extgwUserModMap=iMap.getAPIData();
		
			extgwUserModMap.put(iAPI.MSISDN, "");
			extgwUserModMap.put(iAPI.PIN, "");
			extgwUserModMap.put(iAPI.EMPCODE, "");
			extgwUserModMap.put(iAPI.ADDRESS2, "NewADD2"+new RandomGeneration().randomNumeric(5));
			
		String API = iAPI.prepareAPI(extgwUserModMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERMODAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());}

	@Test
	public void _04_modifyUserDetails() throws IOException{
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWUSERMODREQ4");

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		EXTGWUSERMODDP iMap= new EXTGWUSERMODDP();
		EXTGWUSERMODAPI iAPI = new EXTGWUSERMODAPI();

		HashMap<String,String> extgwUserModMap = new HashMap<String, String>();
		extgwUserModMap=iMap.getAPIData();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		if(!BTSLUtil.isNullString(extgwUserModMap.get(iAPI.PIN))){
			extgwUserModMap.put(iAPI.LOGINID, "");
			extgwUserModMap.put(iAPI.PASSWORD, "");
			extgwUserModMap.put(iAPI.EMPCODE, "");

			String API = iAPI.prepareAPI(extgwUserModMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWUSERMODAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());}
		else currentNode.log(Status.SKIP, "Since, SMS interface is not allowed to the Operator user,hence the case is skipped.");
		}
	
	@Test
	public void _05_modifyUserDetails() throws IOException{

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("EXTGWUSERMODREQ5");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("EXTGWUSERMODREQ6");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster1.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERMODDP iMap= new EXTGWUSERMODDP();
		EXTGWUSERMODAPI iAPI = new EXTGWUSERMODAPI();

		HashMap<String,String> extgwUserModMap = new HashMap<String, String>();
		extgwUserModMap=iMap.getAPIData();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory(extentCategory);

		extgwUserModMap.put(iAPI.MSISDN, "");
		extgwUserModMap.put(iAPI.PIN, "");
		extgwUserModMap.put(iAPI.EMPCODE, "");
		extgwUserModMap.put(iAPI.MSISDN1, UniqueChecker.UC_MSISDN());
		String API = iAPI.prepareAPI(extgwUserModMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster1, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERMODAPI.TXNSTATUS).toString(), CaseMaster1.getErrorCode());

		currentNode = test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String PIN = DBHandler.AccessHandler.fetchUserPIN(extgwUserModMap.get(iAPI.LOGINID), extgwUserModMap.get(iAPI.MSISDN1));
		String defaultPIN=DBHandler.AccessHandler.getSystemPreference(CONSTANT.C2S_DEFAULT_SMSPIN);
		Validator.messageCompare(PIN, defaultPIN);
		}
	
	@Test
	public void _06_modifyUserDetails() throws IOException{

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("EXTGWUSERMODREQ7");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("EXTGWUSERMODREQ8");
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster1.getModuleCode());
			TestCaseCounter = true;
		}

		EXTGWUSERMODDP iMap= new EXTGWUSERMODDP();
		EXTGWUSERMODAPI iAPI = new EXTGWUSERMODAPI();

		HashMap<String,String> extgwUserModMap = new HashMap<String, String>();
		extgwUserModMap=iMap.getAPIData();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory(extentCategory);

		extgwUserModMap.put(iAPI.MSISDN, "");
		extgwUserModMap.put(iAPI.PIN, "");
		extgwUserModMap.put(iAPI.EMPCODE, "");
		extgwUserModMap.put(iAPI.LOGINID, UniqueChecker.UC_LOGINID());
		String API = iAPI.prepareAPI(extgwUserModMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.OperatorReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster1, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWUSERMODAPI.TXNSTATUS).toString(), CaseMaster1.getErrorCode());

		currentNode = test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String password = DBHandler.AccessHandler.fetchUserPassword(extgwUserModMap.get(iAPI.LOGINID));
		String defaultPassword=DBHandler.AccessHandler.getSystemPreference(CONSTANT.C2S_DEFAULT_PASSWORD);
		Validator.messageCompare(password, defaultPassword);
		}
	
}
