package com.apicontrollers.ussd.Withdraw;

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

public class USSD_Withdraw extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TCA1_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH01");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		
		String API = withdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCB2_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH02");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		apiData.put(withdrawAPI.LANGUAGE1, "");
		String API = withdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCC3_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH03");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		apiData.put(withdrawAPI.PRODUCTCODE, "");
		String API = withdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCD4_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH04");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		apiData.put(withdrawAPI.LANGUAGE1, randomGeneration.randomNumeric(3));
		String API = withdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCE5_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH05");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		apiData.put(withdrawAPI.PIN, "");
		String API = withdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCF6_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH06");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		apiData.put(withdrawAPI.TOPUPVALUE, "-100");
		String API = withdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCG7_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH07");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		apiData.put(withdrawAPI.TOPUPVALUE, "");
		String API = withdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCH8_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH08");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		apiData.put(withdrawAPI.TOPUPVALUE, randomGeneration.randomNumeric(4));
		String API = withdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		Validator.APIMultiErrorCodeComapre(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCI9_NegativeSuspendTCP() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH09");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		String API = withdrawAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, USSD_Withdraw_DP.FROM_Domain, USSD_Withdraw_DP.FROM_Category, USSD_Withdraw_DP.FROM_TCPName, null);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, USSD_Withdraw_DP.FROM_Domain, USSD_Withdraw_DP.FROM_Category, USSD_Withdraw_DP.FROM_TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCJ_NegativeSuspendTCP() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH10");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		String API = withdrawAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, USSD_Withdraw_DP.TO_Domain, USSD_Withdraw_DP.TO_Category, USSD_Withdraw_DP.TO_TCPName, null);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, USSD_Withdraw_DP.TO_Domain, USSD_Withdraw_DP.TO_Category, USSD_Withdraw_DP.TO_TCPName, null);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCK_Negative() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH11");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(withdrawAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(USSD_Withdraw_DP.TO_Category, channelMap);
		
		String API = withdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(USSD_Withdraw_DP.TO_Category, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCL_Negative() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDWDTH12");
		USSD_Withdraw_API withdrawAPI = new USSD_Withdraw_API();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = USSD_Withdraw_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(withdrawAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(USSD_Withdraw_DP.FROM_Category, channelMap);
		
		String API = withdrawAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(USSD_Withdraw_DP.FROM_Category, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(withdrawAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	
	

}
