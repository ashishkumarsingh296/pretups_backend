package com.apicontrollers.extgw.C2SRechargeReversal;

import java.util.HashMap;

import com.Features.ChannelUser;
import org.testng.annotations.Test;

import com.apicontrollers.extgw.c2sTransactionEnquiry.c2sTxnENQ_DP;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_C2SReversal extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	public String TxnId;
	public String extRefNo;
	public String MSISDN;


	/*
	 * @throws Exception 
	 * @test Id EXTGWC2S01
	 * Positive Test Case For TRFCATEGORY: C2S
	 */
	
	
	@Test
	public void TC_01_PositiveC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV01");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		C2SReversal_API C2SRevAPI = new C2SReversal_API();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = dataMap.get(C2STransferAPI.EXTREFNUM);
		MSISDN = dataMap.get(C2STransferAPI.MSISDN2);

		Map.put(C2SRevAPI.TXNID, TxnId);
		Map.put(C2SRevAPI.EXTREFNUM, extRefNo );
		Map.put(C2SRevAPI.MSISDN2,MSISDN);

		String API1 = C2SRevAPI.prepareAPI(Map);
		System.out.println(API1);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SRevAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	
	
	@Test
	public void TC_02_C2S_Reversal_API() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV02");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		C2SReversal_API C2SReversal_API = new C2SReversal_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API0 = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);
		
		Map.put(C2SReversal_API.TXNID, TxnId);
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );
		Map.put(C2SReversal_API.LOGINID, "");
		Map.put(C2SReversal_API.PASSWORD,"");
		Map.put(C2SReversal_API.MSISDN2,MSISDN);



		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S03_PositiveC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV03");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();

		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API0 = C2STransferAPI.prepareAPI(dataMap);
		
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);

		Map.put(C2SReversal_API.TXNID, TxnId);
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );
		Map.put(C2SReversal_API.MSISDN, "");
	


		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_C2S04_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV04");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API0 = C2STransferAPI.prepareAPI(dataMap);
		
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);


		Map.put(C2SReversal_API.TXNID, TxnId);
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );



		String CorrectNWCode = Map.get(C2SReversal_API.EXTNWCODE);

		String InValNWCode;

		do
		{
			InValNWCode = RandomGeneration.randomAlphaNumeric(4);

		} while(CorrectNWCode==InValNWCode);

		Map.put(C2SReversal_API.EXTNWCODE,InValNWCode);
		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S05_PositiveC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV05");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API0 = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);


		Map.put(C2SReversal_API.TXNID, TxnId);
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );
		Map.put(C2SReversal_API.MSISDN,"");
		Map.put(C2SReversal_API.MSISDN2,MSISDN);


		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_C2S06_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV06");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API0 = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);
		Map.put(C2SReversal_API.TXNID, TxnId);
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );
		Map.put(C2SReversal_API.LOGINID,"");
		
		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_C2S07_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV07");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(C2SReversal_API.TXNID, TxnId);
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );

		Map.put(C2SReversal_API.MSISDN2,MSISDN);
		Map.put(C2SReversal_API.PASSWORD,"");
		Map.put(C2SReversal_API.MSISDN2,MSISDN);
		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_C2S08_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV08");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = dataMap.get(C2STransferAPI.EXTREFNUM);
		MSISDN = dataMap.get(C2STransferAPI.MSISDN2);

		Map.put(C2SReversal_API.TXNID, TxnId);
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );
		Map.put(C2SReversal_API.EXTCODE, "");
		String CorrectMSISDN = Map.get(C2SReversal_API.MSISDN);

		String InValMSISDN;

		do
		{
			InValMSISDN = RandomGeneration.randomNumeric(9);

		} while(CorrectMSISDN==InValMSISDN);



		Map.put(C2SReversal_API.MSISDN, InValMSISDN);
		Map.put(C2SReversal_API.LOGINID, "");


		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	




	@Test
	public void TC_C2S09_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV09");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(C2SReversal_API.TXNID, TxnId);
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );
		Map.put(C2SReversal_API.PASSWORD,RandomGeneration.randomNumeric(9));
		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_C2S10_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV10");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(C2SReversal_API.TXNID, TxnId);
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );


		String InValSubMSISDN;

		do
		{
			InValSubMSISDN = RandomGeneration.randomNumeric(3);

		}while(MSISDN==InValSubMSISDN);

		Map.put(C2SReversal_API.MSISDN2,InValSubMSISDN);	

		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_C2S11_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV11");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(C2SReversal_API.TXNID,"");
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );
		Map.put(C2SReversal_API.MSISDN,"");
		Map.put(C2SReversal_API.MSISDN2,"");
		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	




    @Test
	public void TC_C2S12_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV12");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

				
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );
		Map.put(C2SReversal_API.TXNID, RandomGeneration.randomNumeric(14));
		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

    @Test
	public void TC_C2S14_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV13");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

				
		Map.put(C2SReversal_API.EXTREFNUM, extRefNo );
		Map.put(C2SReversal_API.TXNID, "");
		Map.put(C2SReversal_API.MSISDN2, MSISDN);
		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	//@Test INVALID CASE TO BE REMOVED 
	public void TC_C2S13_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV14");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

				
		Map.put(C2SReversal_API.EXTREFNUM, "" );
		Map.put(C2SReversal_API.TXNID, "");
		Map.put(C2SReversal_API.MSISDN,"");
		Map.put(C2SReversal_API.MSISDN2,"");

		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


   

	
	
	@Test
	public void TC_C2S15_NegativeC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV15");
		C2SReversal_API C2SReversal_API = new C2SReversal_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);


		Map.put(C2SReversal_API.EXTREFNUM, "abc" );
		String InValTxnID = DBHandler.AccessHandler.getTransactionID(Map.get(C2SReversal_API.MSISDN));
		Map.put(C2SReversal_API.TXNID,InValTxnID );
		String API = C2SReversal_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SReversal_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	@Test
	public void TC_16_NegaiveC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV16");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		C2SReversal_API C2SRevAPI = new C2SReversal_API();
		ChannelUser ChannelUser = new ChannelUser(driver);


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = dataMap.get(C2STransferAPI.EXTREFNUM);
		MSISDN = dataMap.get(C2STransferAPI.MSISDN2);

		Map.put(C2SRevAPI.TXNID, TxnId);
		Map.put(C2SRevAPI.EXTREFNUM, extRefNo );
		Map.put(C2SRevAPI.MSISDN2,MSISDN);
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", Map.get(C2SRevAPI.MSISDN));
		channelMap.put("inSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(C2SReversal_DP.CUCategory, channelMap);
		String API1 = C2SRevAPI.prepareAPI(Map);
		System.out.println(API1);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SRevAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		channelMap.put("inSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(C2SReversal_DP.CUCategory, channelMap);
	}

	@Test
	public void TC_17_PositiveC2SReversalAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SREV17");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		C2SReversal_API C2SRevAPI = new C2SReversal_API();
		ChannelUser ChannelUser = new ChannelUser(driver);


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> Map = C2SReversal_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = dataMap.get(C2STransferAPI.EXTREFNUM);
		MSISDN = dataMap.get(C2STransferAPI.MSISDN2);

		Map.put(C2SRevAPI.TXNID, TxnId);
		Map.put(C2SRevAPI.EXTREFNUM, extRefNo );
		Map.put(C2SRevAPI.MSISDN2,MSISDN);
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", Map.get(C2SRevAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(C2SReversal_DP.CUCategory, channelMap);
		String API1 = C2SRevAPI.prepareAPI(Map);
		System.out.println(API1);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API1);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(C2SRevAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(C2SReversal_DP.CUCategory, channelMap);
	}

	

}
