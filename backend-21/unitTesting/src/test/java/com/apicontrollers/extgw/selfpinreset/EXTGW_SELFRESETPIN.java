package com.apicontrollers.extgw.selfpinreset;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExtentI;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_SELFRESETPIN extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	
	
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * 
	 */
	@Test
	public void _01_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFPINRESETREQ01");
		EXTGWSELFRESETPINAPI ResetPinAPI = new EXTGWSELFRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		
		HashMap<String, String> apiData = EXTGWSELFRESETPINDP.getAPIdata();
		
		apiData.put(EXTGWSELFRESETPINAPI.EXTNWCODE, "");
		
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Assertion.assertEquals(xmlPath.get(EXTGWSELFRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
	}

	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * 
	 */
	@Test
	public void _02_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFPINRESETREQ02");
		EXTGWSELFRESETPINAPI ResetPinAPI = new EXTGWSELFRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		
		HashMap<String, String> apiData = EXTGWSELFRESETPINDP.getAPIdata();
		
		apiData.put(EXTGWSELFRESETPINAPI.LOGINID, "");
		apiData.put(EXTGWSELFRESETPINAPI.PASSWORD, "");
		
		apiData.put(EXTGWSELFRESETPINAPI.MSISDN, "");
		apiData.put(EXTGWSELFRESETPINAPI.PIN, "");
		
		apiData.put(EXTGWSELFRESETPINAPI.EXTCODE, "");
		
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Assertion.assertEquals(xmlPath.get(EXTGWSELFRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
	}
	
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * 
	 */
	@Test
	public void _03_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFPINRESETREQ03");
		EXTGWSELFRESETPINAPI ResetPinAPI = new EXTGWSELFRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		
		HashMap<String, String> apiData = EXTGWSELFRESETPINDP.getAPIdata();
		
		
		apiData.put(EXTGWSELFRESETPINAPI.OPERATION, "");
		
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Assertion.assertEquals(xmlPath.get(EXTGWSELFRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 *  TPINRSETREQ01
	 * Positive Test Case For Reset PIN
	 */
	@Test
	public void _04_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFPINRESETREQ04");
		EXTGWSELFRESETPINAPI ResetPinAPI = new EXTGWSELFRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		
		HashMap<String, String> apiData = EXTGWSELFRESETPINDP.getAPIdata();
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Assertion.assertEquals(xmlPath.get(EXTGWSELFRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		if(xmlPath.get(EXTGWSELFRESETPINAPI.TXNSTATUS).toString().equals("200"))
		{
		//get the user reset pin
		String PIN = DBHandler.AccessHandler.fetchUserPIN("", apiData.get(EXTGWSELFRESETPINAPI.MSISDN));
		// change pin to use service again
		String newPin = new CommonUtils().isSMSPinValid();
		new ChannelUser(driver).changeUserFirstTimePIN("", apiData.get(EXTGWSELFRESETPINAPI.MSISDN).toString(), PIN, newPin);
		// update pin in sheet
		ExtentI.insertValueInDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.PIN, 1,newPin);
		}
	}
	
	
	/**
	 * @throws ParseException 
	 * @throws SQLException 
	 * 
	 */
	@Test
	public void _05_ResetPinAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SELFPINRESETREQ05");
		EXTGWSELFRESETPINAPI ResetPinAPI = new EXTGWSELFRESETPINAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		
		
		HashMap<String, String> apiData = EXTGWSELFRESETPINDP.getAPIdata();
		
		
		apiData.put(EXTGWSELFRESETPINAPI.OPERATION, "A");
		
		String API = ResetPinAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Assertion.assertEquals(xmlPath.get(EXTGWSELFRESETPINAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
	}

}