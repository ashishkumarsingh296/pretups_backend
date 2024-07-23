package com.apicontrollers.extgw.chnlReversal;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CAPI;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CDP;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
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

public class XMLGW_C2CTXNREV extends BaseTest{

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	public String TxnId;
	public String extRefNo;
	public String EMPCODE;

	/**
	 * @throws Exception 
	 * 
	 * @testid EXTGWC2STransfer01
	 * Positive Test Case For TRFCATEGORY: C2STransactionEnquiry
	 */
	@Test
	public void TC_01_PositiveC2CTransRevAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV01");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2CDP.getAPIdata();
		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = C2CTransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);
		EMPCODE = Map.get(chnlTxnREV_API.EMPCODE);

		Map.put(chnlTxnREV_API.TRANSACTIONID, TxnId);
	
		String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TC_02_PositiveC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV02");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2CDP.getAPIdata();
		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = C2CTransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		
		Map.put(chnlTxnREV_API.TRANSACTIONID, TxnId);
		Map.put(chnlTxnREV_API.EXTREFNUM, extRefNo );
		Map.put(chnlTxnREV_API.LOGINID, "");
		Map.put(chnlTxnREV_API.PASSWORD,"");
		Map.put(chnlTxnREV_API.EMPCODE,EMPCODE);
		
	
		String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_03_NegativeC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV03");
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();
	
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(chnlTxnREV_API.TRANSACTIONID, TxnId);
		Map.put(chnlTxnREV_API.EXTREFNUM, extRefNo );
		Map.put(chnlTxnREV_API.LOGINID, "");
		Map.put(chnlTxnREV_API.PASSWORD,"");
		Map.put(chnlTxnREV_API.EMPCODE,"");

		String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TC_C2C04_NegativeC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV04");
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();
	
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		RandomGeneration randomGeneration= new RandomGeneration();

		String CorrectNWCode = Map.get(chnlTxnREV_API.EXTNWCODE);

		String InValNWCode;

		do
		{
			InValNWCode = randomGeneration.randomAlphaNumeric(4);

		} while(CorrectNWCode==InValNWCode);

		Map.put(chnlTxnREV_API.EXTNWCODE,InValNWCode);
		String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());	}


	@Test
	public void TC_C2C05_NegativeC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV05");
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();
	
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(chnlTxnREV_API.TRANSACTIONID, TxnId);
		Map.put(chnlTxnREV_API.EXTREFNUM, extRefNo );
		Map.put(chnlTxnREV_API.CATCODE,"");
		Map.put(chnlTxnREV_API.EMPCODE,"");

		String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2C06_NegativeC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV06");
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();
	
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(chnlTxnREV_API.TRANSACTIONID, TxnId);
		Map.put(chnlTxnREV_API.EXTREFNUM, extRefNo );
		Map.put(chnlTxnREV_API.PASSWORD,"");
		Map.put(chnlTxnREV_API.EMPCODE,"");

		String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());		Map.put(chnlTxnREV_API.EMPCODE,"");

	}


	@Test
	public void TC_C2C07_NegativeC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV07");
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();
	
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		Map.put(chnlTxnREV_API.TRANSACTIONID, TxnId);
		Map.put(chnlTxnREV_API.EXTREFNUM, extRefNo );
		String EmpCODE = Map.get(chnlTxnREV_API.EMPCODE);
		String InValEmpCode;
		do
		{
			InValEmpCode = RandomGeneration.randomNumeric(4);

		}while(EmpCODE==InValEmpCode);

		Map.put(chnlTxnREV_API.EMPCODE,InValEmpCode);
		Map.put(chnlTxnREV_API.LOGINID,"");
		Map.put(chnlTxnREV_API.PASSWORD,"");

		String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());		Map.put(chnlTxnREV_API.EMPCODE,"");

	}


	@Test
	public void TC_C2C08_NegativeC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV08");
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		Map.put(chnlTxnREV_API.TRANSACTIONID, "");
		Map.put(chnlTxnREV_API.EXTREFNUM, extRefNo );
				String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());		Map.put(chnlTxnREV_API.EMPCODE,"");

	}	


	@Test
	public void TC_C2C09_NegativeC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV09");
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(chnlTxnREV_API.TRANSACTIONID, TxnId);
		Map.put(chnlTxnREV_API.EXTREFNUM, extRefNo );
		Map.put(chnlTxnREV_API.PASSWORD,RandomGeneration.randomNumeric(9));
		String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_C2C10_NegativeC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV10");
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(chnlTxnREV_API.TRANSACTIONID, TxnId);
		Map.put(chnlTxnREV_API.EXTREFNUM, extRefNo );

		Map.put(chnlTxnREV_API.LOGINID,RandomGeneration.randomNumeric(3));	
		String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2C11_NegativeC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV11");
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

				
		Map.put(chnlTxnREV_API.EXTREFNUM, extRefNo );
		Map.put(chnlTxnREV_API.TRANSACTIONID, RandomGeneration.randomNumeric(14));
		String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TC_C2C12_NegativeC2CTransferREVAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWC2CREV12");
		chnlTxnREV_API chnlTxnREV_API = new chnlTxnREV_API();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = chnlTxnREV_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(chnlTxnREV_API.TRANSACTIONID, TxnId);
		Map.put(chnlTxnREV_API.EXTREFNUM, extRefNo );
		String InValNWCode;


		InValNWCode = RandomGeneration.randomAlphabets(2);

		if(!InValNWCode.equalsIgnoreCase(null)){

			Map.put(chnlTxnREV_API.EXTNWCODE,InValNWCode);
			String API = chnlTxnREV_API.prepareC2CREVAPI(Map);
			System.out.println(API);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(chnlTxnREV_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		else {
			currentNode.skip("Network Code other than " +Map.get(chnlTxnREV_API.EXTNWCODE)+ " is not available in System");
		}
	}


	

}
