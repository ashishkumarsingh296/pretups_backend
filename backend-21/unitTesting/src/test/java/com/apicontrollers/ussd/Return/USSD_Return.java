package com.apicontrollers.ussd.Return;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.TransferControlProfile;
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

public class USSD_Return extends BaseTest{

    public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TCA1_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET01");
		USSD_Return_API returnAPI = new USSD_Return_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		
		String API = returnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCB2_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET02");
		USSD_Return_API returnAPI = new USSD_Return_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		apiData.put(returnAPI.LANGUAGE1, "");
		String API = returnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCC3_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET03");
		USSD_Return_API returnAPI = new USSD_Return_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		apiData.put(returnAPI.PRODUCTCODE, "");
		String API = returnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCD4_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET04");
		USSD_Return_API returnAPI = new USSD_Return_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		apiData.put(returnAPI.LANGUAGE1, randomGeneration.randomNumeric(3));
		String API = returnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCE5_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET05");
		USSD_Return_API returnAPI = new USSD_Return_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		apiData.put(returnAPI.PIN, "");
		String API = returnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCF6_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET06");
		USSD_Return_API returnAPI = new USSD_Return_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		apiData.put(returnAPI.TOPUPVALUE, "-100");
		String API = returnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCG7_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET07");
		USSD_Return_API returnAPI = new USSD_Return_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		apiData.put(returnAPI.TOPUPVALUE, "");
		String API = returnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCH8_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET08");
		USSD_Return_API returnAPI = new USSD_Return_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		apiData.put(returnAPI.TOPUPVALUE, randomGeneration.randomNumeric(2));
		String API = returnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCI9_NegativeSuspendTCP() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET09");
		USSD_Return_API returnAPI = new USSD_Return_API();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		String API = returnAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, USSD_Return_DP.FROM_Domain, USSD_Return_DP.FROM_Category, USSD_Return_DP.FROM_TCPName, null);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, USSD_Return_DP.FROM_Domain, USSD_Return_DP.FROM_Category, USSD_Return_DP.FROM_TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCJ_NegativeSuspendTCP() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET10");
		USSD_Return_API returnAPI = new USSD_Return_API();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		String API = returnAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, USSD_Return_DP.TO_Domain, USSD_Return_DP.TO_Category, USSD_Return_DP.TO_TCPName, null);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, USSD_Return_DP.TO_Domain, USSD_Return_DP.TO_Category, USSD_Return_DP.TO_TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCK_Negative() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET11");
		USSD_Return_API returnAPI = new USSD_Return_API();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(returnAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(USSD_Return_DP.TO_Category, channelMap);
		
		String API = returnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(USSD_Return_DP.TO_Category, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCL_Negative() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRET12");
		USSD_Return_API returnAPI = new USSD_Return_API();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = USSD_Return_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(returnAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(USSD_Return_DP.FROM_Category, channelMap);
		
		String API = returnAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(USSD_Return_DP.FROM_Category, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(returnAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	
	
}
