package com.apicontrollers.ussd.MultipleEVD;

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

public class USSD_MEVD extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TC1_PositiveMMEVDAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD01");
		USSD_MEVD_API MMEVDAPI = new USSD_MEVD_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_MEVD_DP.getAPIdata();
		
		String API = MMEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(MMEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC2_NegativeMEVDAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD02");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.PIN, "");
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC3_NegativeMEVDAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD03");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.PIN, randomGeneration.randomNumeric(4));
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC4_NegativeMEVDAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD04");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.MSISDN2, "");
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC5_NegativeMEVDAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD05");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.AMOUNT, "");
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC6_NegativeMEVDAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD06");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.SELECTOR, "");
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC7_NegativeMEVDAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD07");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.AMOUNT, "-100");
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TC8_NegativeMEVDAPI() throws SQLException, ParseException
	{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD08");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		
		if(TestCaseCounter == false)
		{
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.LANGUAGE1, "-100");
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.getString(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TC9_NegativeAPI() throws  SQLException, ParseException
	{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD09");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		
		if(TestCaseCounter == false)
		{
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter =true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String,String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.LANGUAGE2,"-100");
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.getString(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCJ10_NegativeAPI() throws SQLException, ParseException
	{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD10");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		
		if(TestCaseCounter == false)
		{
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.SELECTOR, "-1");
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.getString(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCK11_NegativeAPI() throws SQLException, ParseException
	{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD11");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if(TestCaseCounter == false)
		{
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.SELECTOR, randomGeneration.randomNumeric(3));
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.getString(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCL12_NegativeAPI() throws SQLException, ParseException
	{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD12");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if(TestCaseCounter == false)
		{
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String,String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.LANGUAGE1, randomGeneration.randomNumeric(4));
		String API =  MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML,APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.getString(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCM13_NegativeAPI() throws SQLException, ParseException
	{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD13");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if(TestCaseCounter == false)
		{
			test = extent.createTest(CaseMaster.getDescription());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String,String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.LANGUAGE2, randomGeneration.randomNumeric(4));
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML,APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.getString(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCM14_NegativeAPI() throws SQLException, ParseException
	{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD14");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if(TestCaseCounter == false)
		{
			test = extent.createTest(CaseMaster.getDescription());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String,String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.QTY, "");
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML,APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.getString(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCM15_NegativeAPI() throws SQLException, ParseException
	{
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDMEVD15");
		USSD_MEVD_API MEVDAPI = new USSD_MEVD_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if(TestCaseCounter == false)
		{
			test = extent.createTest(CaseMaster.getDescription());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String,String> apiData = USSD_MEVD_DP.getAPIdata();
		apiData.put(MEVDAPI.QTY, "-100");
		String API = MEVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML,APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.getString(MEVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
}
