package com.apicontrollers.extgw.LendMeBalance;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.LMB;
import com.apicontrollers.extgw.CreditTransfer.EXTGW_PRC_API;
import com.apicontrollers.extgw.CreditTransfer.PRC_DP;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_LendMeBalance extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/**
	 * @throws Exception 
	 * @testid EXTGWLMB01
	 * Positive Test Case For TRFCATEGORY: PRC
	 */
	
	
	@Test
	public void TC_A_PositiveEXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB01");
		LMB_API LMBAPI = new LMB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = LMBAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	

	
	
	
	@Test
	public void TC_B_Neg_EXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB02");
		LMB_API LMBAPI = new LMB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put(LMBAPI.MSISDN1,"");
		String API = LMBAPI.prepareAPI(dataMap);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	//TEST CASE IS WRONG AS SOS AMOUNT IS TAKEN FROM PREFRENCE SOS_RECHARGE_AMOUNT WHEN GIVEN BLANK 
	@Test
	public void TC_C_Neg_EXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB03");
		LMB_API LMBAPI = new LMB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put(LMBAPI.AMOUNT,"");
		String API = LMBAPI.prepareAPI(dataMap);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_D_Neg_EXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB04");
		LMB_API LMBAPI = new LMB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put(LMBAPI.CELLID,"");
		String API = LMBAPI.prepareAPI(dataMap);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_E_Neg_EXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB05");
		LMB_API LMBAPI = new LMB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		dataMap.put(LMBAPI.SWITCHID,"");
		String API = LMBAPI.prepareAPI(dataMap);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	

	
	
	@Test
	public void TC_F_Neg_EXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB06");
		LMB_API LMBAPI = new LMB_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String MSISDN = RandomGeneration.randomAlphabets(2) +RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
		dataMap.put(LMBAPI.MSISDN1,MSISDN);
		String API = LMBAPI.prepareAPI(dataMap);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	
	@Test
	public void TC_G_Neg_EXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB07");
		LMB_API LMBAPI = new LMB_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String MSISDN = DBHandler.AccessHandler.getSubscriberMSISDNFrombarredlist("P2P");
		
		dataMap.put(LMBAPI.MSISDN1,MSISDN);
		String API = LMBAPI.prepareAPI(dataMap);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
		
	
	
	
	@Test
	public void TC_H_Neg_EXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB08");
		LMB_API LMBAPI = new LMB_API();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		String API = LMBAPI.prepareAPI(dataMap);
		String MSISDN = dataMap.get(LMBAPI.MSISDN1);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		//Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
		dataMap.put(LMBAPI.MSISDN1,MSISDN);
		String API1 = LMBAPI.prepareAPI(dataMap);
		
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath1 = new XmlPath(CompatibilityMode.HTML, APIResponse1[1]);
		Validator.messageCompare(xmlPath1.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	
	}	
	
	
	

	
	
	@Test
	public void TC_I_Neg_EXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB09");
		LMB_API LMBAPI = new LMB_API();
		EXTGW_PRC_API CreditTransAPI = new EXTGW_PRC_API();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		String API = LMBAPI.prepareAPI(dataMap);
		String MSISDN = dataMap.get(LMBAPI.MSISDN1);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		HashMap<String, String> Map = PRC_DP.getAPIdata();
		Map.put(CreditTransAPI.MSISDN2,MSISDN);
		
		String API1 = CreditTransAPI.prepareAPI(Map);
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath1 = new XmlPath(CompatibilityMode.HTML, APIResponse1[1]);
		Validator.messageCompare(xmlPath1.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		

	}
	
	
	
	
	@Test
	public void TC_J_Neg_EXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB10");
		LMB_API LMBAPI = new LMB_API();
		EXTGWC2SAPI C2SAPI = new EXTGWC2SAPI();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		String API = LMBAPI.prepareAPI(dataMap);
		String MSISDN = dataMap.get(LMBAPI.MSISDN1);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		HashMap<String, String> Map = EXTGWC2SDP.getAPIdata();
		Map.put(C2SAPI.MSISDN2,MSISDN);
		
		String API1 = C2SAPI.prepareAPI(Map);
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath1 = new XmlPath(CompatibilityMode.HTML, APIResponse1[1]);
		Validator.messageCompare(xmlPath1.get(C2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}
	

	
	
	
	
	@Test
	public void TC_K_Neg_EXTGW_LMB_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLMB10");
		LMB_API LMBAPI = new LMB_API();
		EXTGWC2SAPI C2SAPI = new EXTGWC2SAPI();
		LMB LMB = new LMB();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LMB_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		String API = LMBAPI.prepareAPI(dataMap);
		String MSISDN = dataMap.get(LMBAPI.MSISDN1);
		
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(LMB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		LMB.OfflineSettlementScript();
		
	
	}
	
	
	
	
}
