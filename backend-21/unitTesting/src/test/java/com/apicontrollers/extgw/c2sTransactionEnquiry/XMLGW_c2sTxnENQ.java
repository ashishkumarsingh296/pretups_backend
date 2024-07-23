package com.apicontrollers.extgw.c2sTransactionEnquiry;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.apicontrollers.extgw.c2sTransfer.postpaidBillPayment.EXTGWPPBDP;
import com.apicontrollers.extgw.c2sTransfer.postpaidBillPayment.EXTGW_PPB_API;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CDP;
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

public class XMLGW_c2sTxnENQ extends BaseTest{



	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	public String TxnId;
	public String extRefNo;
	public String MSISDN;

	/**
	 * @throws Exception 
	 * 
	 * @testid EXTGWC2STransfer01
	 * Positive Test Case For TRFCATEGORY: C2STransactionEnquiry
	 */
	@Test
	public void TC_01_PositiveC2STransEnquiryAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ01");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

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

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.MSISDN2,MSISDN);

		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_02_C2STransferEnquiryAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ02");

		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.LOGINID, "");
		Map.put(c2sTxnENQ_API.PASSWORD,"");
		Map.put(c2sTxnENQ_API.MSISDN2,MSISDN);



		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S03_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ03");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);


		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.SENDERMSISDN, "");
		Map.put(c2sTxnENQ_API.EMPCODE,"");


		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_C2S05_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ05");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);



		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );



		String CorrectNWCode = Map.get(c2sTxnENQ_API.EXTNWCODE);

		String InValNWCode;

		do
		{
			InValNWCode = RandomGeneration.randomAlphaNumeric(4);

		} while(CorrectNWCode==InValNWCode);

		Map.put(c2sTxnENQ_API.EXTNWCODE,InValNWCode);
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S06_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ06");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);



		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.SENDERMSISDN,"");
		Map.put(c2sTxnENQ_API.MSISDN2,MSISDN);


		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_C2S07_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ07");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.CATCODE,"");
		Map.put(c2sTxnENQ_API.EMPCODE,"");
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_C2S08_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ08");

		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.LOGINID,"");
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S09_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ09");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );

		Map.put(c2sTxnENQ_API.MSISDN2,MSISDN);
		Map.put(c2sTxnENQ_API.EMPCODE,"");
		Map.put(c2sTxnENQ_API.PASSWORD,"");
		Map.put(c2sTxnENQ_API.MSISDN2,MSISDN);
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S10_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ10");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.EMPCODE,"");
		Map.put(c2sTxnENQ_API.LOGINID,"");
		Map.put(c2sTxnENQ_API.PASSWORD,"");
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S11_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ11");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );

		String EmpCODE = Map.get(c2sTxnENQ_API.EMPCODE);
		String InValEmpCode;
		do
		{
			InValEmpCode = RandomGeneration.randomNumeric(4);

		}while(EmpCODE==InValEmpCode);

		Map.put(c2sTxnENQ_API.EMPCODE,InValEmpCode);
		Map.put(c2sTxnENQ_API.LOGINID,"");
		Map.put(c2sTxnENQ_API.PASSWORD,"");
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_C2S12_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ12");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);



		Map.put(c2sTxnENQ_API.TRANSACTIONID, "");
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );

		String CorrectMSISDN = Map.get(c2sTxnENQ_API.SENDERMSISDN);

		String InValMSISDN;

		do
		{
			InValMSISDN = RandomGeneration.randomNumeric(9);

		} while(CorrectMSISDN==InValMSISDN);



		Map.put(c2sTxnENQ_API.SENDERMSISDN, InValMSISDN);
		Map.put(c2sTxnENQ_API.LOGINID, "");


		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	




	@Test
	public void TC_C2S13_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ13");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.PASSWORD,RandomGeneration.randomNumeric(9));
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_C2S14_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ14");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );


		String InValSubMSISDN;

		do
		{
			InValSubMSISDN = RandomGeneration.randomNumeric(3);

		}while(MSISDN==InValSubMSISDN);

		Map.put(c2sTxnENQ_API.MSISDN2,InValSubMSISDN);	

		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_C2S15_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ15");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID,"");
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.SENDERMSISDN,"");
		Map.put(c2sTxnENQ_API.MSISDN2,"");
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	




	@Test
	public void TC_C2S16_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ16");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

				
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.TRANSACTIONID, RandomGeneration.randomNumeric(14));
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_C2S18_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ18");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

				
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.TRANSACTIONID, "");
		Map.put(c2sTxnENQ_API.MSISDN2, MSISDN);
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_C2S19_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ19");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

				
		Map.put(c2sTxnENQ_API.EXTREFNUM, "abc" );
		Map.put(c2sTxnENQ_API.TRANSACTIONID, "");
		Map.put(c2sTxnENQ_API.SENDERMSISDN,"");
		Map.put(c2sTxnENQ_API.MSISDN2,"");

		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_C2S20_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ20");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

				
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.MSISDN2, MSISDN);
		Map.put(c2sTxnENQ_API.SRVTYPE,_masterVO.getProperty("PostpaidBillPaymentCode"));
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	


	@Test
	public void TC_C2S21_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ21");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

				
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.MSISDN2, MSISDN);
		Map.put(c2sTxnENQ_API.SRVTYPE,"");
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_C2S22_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ22");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.SENDERMSISDN, "");



		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S23_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ23");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		String InValNWCode;


		InValNWCode = DBHandler.AccessHandler.getNetworkCode(Map.get(c2sTxnENQ_API.EXTNWCODE));

		if(!InValNWCode.equalsIgnoreCase(null)){

			Map.put(c2sTxnENQ_API.EXTNWCODE,InValNWCode);
			String API = c2sTxnENQ_API.prepareAPI(Map);
			System.out.println(API);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		else {
			currentNode.skip("Network Code other than " +Map.get(c2sTxnENQ_API.EXTNWCODE)+ " is not available in System");
		}
	}



	@Test
	public void TC_C2S24_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ24");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);



		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );


		String CorrectMSISDN = Map.get(c2sTxnENQ_API.SENDERMSISDN);

		String InValMSISDN;

		do
		{
			InValMSISDN = RandomGeneration.randomNumeric(9);

		} while(CorrectMSISDN==InValMSISDN);



		Map.put(c2sTxnENQ_API.SENDERMSISDN, InValMSISDN);
		


		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	


	@Test
	public void TC_C2S25_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ25");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, "");
		Map.put(c2sTxnENQ_API.SENDERMSISDN,"");
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );

		String InValSubMSISDN;

		do
		{
			InValSubMSISDN = RandomGeneration.randomNumeric(3);

		}while(MSISDN==InValSubMSISDN);

		Map.put(c2sTxnENQ_API.MSISDN2,InValSubMSISDN);	

		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_C2S26_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ26");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);


		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		String InValTxnID = DBHandler.AccessHandler.getTransactionID(Map.get(c2sTxnENQ_API.SENDERMSISDN));
		Map.put(c2sTxnENQ_API.TRANSACTIONID,InValTxnID );
		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_C2S27_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ27");
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);


		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.MSISDN2,MSISDN);
		Map.put(c2sTxnENQ_API.FROMDATE,"");
		Map.put(c2sTxnENQ_API.TODATE,"");

		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	//PostpaidBillPayment



	@Test
	public void TC_C2S28_PositivePPBTransEnquiryAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ28");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdataForPPB();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = PPBTransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGW_PPB_API.TXNID);
		System.out.println(TxnId);

		extRefNo = dataMap.get(PPBTransferAPI.EXTREFNUM);
		MSISDN = dataMap.get(PPBTransferAPI.MSISDN2);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );
		Map.put(c2sTxnENQ_API.MSISDN2,MSISDN);

		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_C2S29_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2SENQ29");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		c2sTxnENQ_API c2sTxnENQ_API = new c2sTxnENQ_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}


		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = c2sTxnENQ_DP.getAPIdata();
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		String API0 = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);

		TxnId= xmlPath0.get(EXTGWO2CAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = xmlPath0.get(EXTGWO2CAPI.EXTTXNNO);

		Map.put(c2sTxnENQ_API.TRANSACTIONID, TxnId);
		Map.put(c2sTxnENQ_API.EXTREFNUM, extRefNo );

		String API = c2sTxnENQ_API.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2sTxnENQ_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());



	}



}
