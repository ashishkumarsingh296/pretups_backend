package com.apicontrollers.ussd.Transfer;

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

public class USSDPlain_Transfer extends BaseTest {
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	@Test
	public void TCA1_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF01");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		
		String API = transferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCB2_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF02");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		apiData.put(transferAPI.LANGUAGE1, "");
		String API = transferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCC3_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF03");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		apiData.put(transferAPI.PRODUCTCODE, "");
		String API = transferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCD4_PositiveAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF04");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		apiData.put(transferAPI.LANGUAGE1, randomGeneration.randomNumeric(3));
		String API = transferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCE5_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF05");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		apiData.put(transferAPI.PIN, "");
		String API = transferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCF6_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF06");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		apiData.put(transferAPI.TOPUPVALUE, "-100");
		String API = transferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCG7_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF07");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		apiData.put(transferAPI.TOPUPVALUE, "");
		String API = transferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCH8_NegativeAPI() throws SQLException, ParseException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF08");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		apiData.put(transferAPI.PRODUCTCODE,randomGeneration.randomNumeric(3)+"a");
		apiData.put(transferAPI.TOPUPVALUE, randomGeneration.randomNumeric(3));
		String API = transferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCI9_NegativeSuspendTCP() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF09");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		String API = transferAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, USSDPlain_TRANSFER_DP.FROM_Domain, USSDPlain_TRANSFER_DP.FROM_Category, USSDPlain_TRANSFER_DP.FROM_TCPName, null);
		
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, USSDPlain_TRANSFER_DP.FROM_Domain, USSDPlain_TRANSFER_DP.FROM_Category, USSDPlain_TRANSFER_DP.FROM_TCPName, null);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCJ_NegativeSuspendTCP() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF10");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		String API = transferAPI.prepareAPI(apiData);
		
		TCPObj.channelLevelTransferControlProfileSuspend(0, USSDPlain_TRANSFER_DP.TO_Domain, USSDPlain_TRANSFER_DP.TO_Category, USSDPlain_TRANSFER_DP.TO_TCPName, null);
		
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		TCPObj.channelLevelTransferControlProfileActive(0, USSDPlain_TRANSFER_DP.TO_Domain, USSDPlain_TRANSFER_DP.TO_Category, USSDPlain_TRANSFER_DP.TO_TCPName, null);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
		
	}
	
	@Test
	public void TCK_Negative() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF11");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(transferAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(USSDPlain_TRANSFER_DP.TO_Category, channelMap);
		
		String API = transferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(USSDPlain_TRANSFER_DP.TO_Category, channelMap);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
	}
	
	@Test
	public void TCL_Negative() throws InterruptedException {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDTRF12");
		USSDPlain_TRANSFER_API transferAPI = new USSDPlain_TRANSFER_API();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = USSDPlain_TRANSFER_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(transferAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(USSDPlain_TRANSFER_DP.FROM_Category, channelMap);
		
		String API = transferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(USSDPlain_TRANSFER_DP.FROM_Category, channelMap);
		
		Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
	}

}
