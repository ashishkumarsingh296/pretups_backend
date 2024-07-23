package com.apicontrollers.extgw.c2sTransferStatus;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.apicontrollers.extgw.c2sTransfer.c2sGiftRecharge.EXTGW_GRCAPI;
import com.apicontrollers.extgw.c2sTransfer.c2sGiftRecharge.EXTGW_GRCDP;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CDP;
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

public class EXTGW_C2STransferStatus extends BaseTest{


	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	
	public String TxnId;
	public String extRefNo;

	/**
	 * @throws Exception 
	 * 
	 * @testid EXTGWC2STransfer01
	 * Positive Test Case For TRFCATEGORY: C2STransferStatus
	 */
	@Test
	public void TC_C2S01_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER01");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGWC2SAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = dataMap.get(C2STransferAPI.EXTREFNUM);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_C2S02_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER02");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.LOGINID, "");
		Map.put(c2stransferStatusAPI.PASSWORD,"");
		Map.put(c2stransferStatusAPI.EXTCODE,"");

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S03_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER03");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.MSISDN, "");
		Map.put(c2stransferStatusAPI.PIN,"");
		Map.put(c2stransferStatusAPI.EXTCODE,"");
		
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_C2S04_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER04");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.LOGINID, "");
		Map.put(c2stransferStatusAPI.PASSWORD,"");
		Map.put(c2stransferStatusAPI.MSISDN, "");
		Map.put(c2stransferStatusAPI.PIN,"");

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_C2S05_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER05");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		
		String CorrectNWCode = Map.get(c2stransferStatusAPI.EXTNWCODE);

		String InValNWCode;

		do
		{
			InValNWCode = RandomGeneration.randomAlphaNumeric(4);

		} while(CorrectNWCode==InValNWCode);
		
		Map.put(c2stransferStatusAPI.EXTNWCODE,InValNWCode);
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_C2S06_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER06");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.MSISDN,"");
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_C2S07_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER07");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.PIN,"");
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	@Test
	public void TC_C2S08_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER08");
	
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
	
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.LOGINID,"");
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_C2S09_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER09");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.PASSWORD,"");
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_C2S10_PositiveC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER10");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.EXTCODE,"");
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	
	
	@Test
	public void TC_C2S11_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER11");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		
		String EXTCODE = Map.get(c2stransferStatusAPI.EXTCODE);

		String InValExtCode;

		do
		{
			InValExtCode = RandomGeneration.randomNumeric(6);

		}while(EXTCODE==InValExtCode);

		
		
		Map.put(c2stransferStatusAPI.EXTCODE,InValExtCode);
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	

	
	@Test
	public void TC_C2S12_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER12");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		
		String CorrectMSISDN = Map.get(c2stransferStatusAPI.MSISDN);

		String InValMSISDN;

		do
		{
			InValMSISDN = RandomGeneration.randomNumeric(9);

		} while(CorrectMSISDN==InValMSISDN);

		
		
		Map.put(c2stransferStatusAPI.MSISDN, InValMSISDN+'@');
		Map.put(c2stransferStatusAPI.LOGINID, "");
		Map.put(c2stransferStatusAPI.EXTCODE,"");

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	


	
	@Test
	public void TC_C2S13_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER13");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.PASSWORD,RandomGeneration.randomNumeric(9));
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_C2S14_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER14");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		String CorrectPin = Map.get(c2stransferStatusAPI.PIN);

		String InValPin;

		do
		{
			InValPin = RandomGeneration.randomNumeric(4);

		}while(CorrectPin==InValPin);

		Map.put(c2stransferStatusAPI.PIN,InValPin);	

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	
	@Test
	public void TC_C2S15_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER15");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.LANGUAGE1, RandomGeneration.randomNumeric(3));
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	
	
	
	@Test
	public void TC_C2S16_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER16");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

        TxnId= null;		
		Map.put(c2stransferStatusAPI.EXTREFNUM, "" );
		Map.put(c2stransferStatusAPI.MSISDN, "");
		Map.put(c2stransferStatusAPI.PIN, "" );
		Map.put(c2stransferStatusAPI.LOGINID, "");
		Map.put(c2stransferStatusAPI.PASSWORD, "" );
		Map.put(c2stransferStatusAPI.TXNID, RandomGeneration.randomNumeric(14));
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_C2S17_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER17");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
				
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		String API0 = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		
		TxnId= xmlPath0.get(EXTGWO2CAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = xmlPath0.get(EXTGWO2CAPI.EXTTXNNO);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		
	}

	
	/*
	@Test
	public void TC_R_NegativeC2STransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER18");
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
				
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = c2sTransferStatusDP.getAPIdata();
		
		
		HashMap<String, String> apiData = EXTGWC2CDP.getAPIdata();
		String API0 = C2CTransferAPI.prepareAPI(apiData);
		long preBalance = Long
				.parseLong(DBHandler.AccessHandler.getUserBalance(EXTGWC2CDP.ProductCode, apiData.get(C2CTransferAPI.MSISDN2)));

		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
	
		TxnId= xmlPath0.get(C2CTransferAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = xmlPath0.get(C2CTransferAPI.EXTTXNNO);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		
	}
*/
	
	
/*
 * PostPaid Bill Payment Transfer Status Test Cases	
 */
	
	/*
	@Test
	public void TC_PPB01_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER19");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = PPBTransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGW_PPB_API.TXNID);
		System.out.println(TxnId);

		extRefNo = dataMap.get(PPBTransferAPI.EXTREFNUM);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_PPB02_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER20");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.LOGINID, "");
		Map.put(c2stransferStatusAPI.PASSWORD,"");
		Map.put(c2stransferStatusAPI.EXTCODE,"");

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_PPB03_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER21");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.MSISDN, "");
		Map.put(c2stransferStatusAPI.PIN,"");
		Map.put(c2stransferStatusAPI.EXTCODE,"");
		
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_PPB04_PositivePPBTransferStatusAPI() throws Exception {
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER22");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.LOGINID, "");
		Map.put(c2stransferStatusAPI.PASSWORD,"");
		Map.put(c2stransferStatusAPI.MSISDN, "");
		Map.put(c2stransferStatusAPI.PIN,"");

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_PPB05_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER23");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		
		String CorrectNWCode = Map.get(c2stransferStatusAPI.EXTNWCODE);

		String InValNWCode;

		do
		{
			InValNWCode = RandomGeneration.randomAlphaNumeric(4);

		} while(CorrectNWCode==InValNWCode);
		
		Map.put(c2stransferStatusAPI.EXTNWCODE,InValNWCode);
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_PPB06_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER24");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

	

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.MSISDN,"");
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_PPB07_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER25");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.PIN,"");
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	@Test
	public void TC_PPB08_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER26");
	
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
	
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();
		
		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.LOGINID,"");
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_PPB09_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER27");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();
		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.PASSWORD,"");
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_PPB10_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER28");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();
		
		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.EXTCODE,"");
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	
	
	@Test
	public void TC_PPB11_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER29");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		
		String EXTCODE = Map.get(c2stransferStatusAPI.EXTCODE);

		String InValExtCode;

		do
		{
			InValExtCode = RandomGeneration.randomNumeric(6);

		}while(EXTCODE==InValExtCode);

		
		
		Map.put(c2stransferStatusAPI.EXTCODE,InValExtCode);
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	

	
	@Test
	public void TC_PPB12_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER30");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		
		String CorrectMSISDN = Map.get(c2stransferStatusAPI.MSISDN);

		String InValMSISDN;

		do
		{
			InValMSISDN = RandomGeneration.randomNumeric(9);

		} while(CorrectMSISDN==InValMSISDN);

		
		
		Map.put(c2stransferStatusAPI.MSISDN, InValMSISDN);
		Map.put(c2stransferStatusAPI.LOGINID, "");
		Map.put(c2stransferStatusAPI.EXTCODE,"");

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	


	
	@Test
	public void TC_PPB13_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER31");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();
		
		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.PASSWORD,RandomGeneration.randomNumeric(9));
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	@Test
	public void TC_PPB14_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER32");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();
		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		String CorrectPin = Map.get(c2stransferStatusAPI.PIN);

		String InValPin;

		do
		{
			InValPin = RandomGeneration.randomNumeric(4);

		}while(CorrectPin==InValPin);

		Map.put(c2stransferStatusAPI.PIN,InValPin);	
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	
	@Test
	public void TC_PPB15_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER33");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();
		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );
		Map.put(c2stransferStatusAPI.LANGUAGE1, RandomGeneration.randomNumeric(3));
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	
	
	
	@Test
	public void TC_PPB16_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER34");
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		TxnId= null;
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();
		Map.put(c2stransferStatusAPI.MSISDN, "");
		Map.put(c2stransferStatusAPI.PIN, "" );
		Map.put(c2stransferStatusAPI.LOGINID, "");
		Map.put(c2stransferStatusAPI.PASSWORD, "" );
		Map.put(c2stransferStatusAPI.EXTREFNUM, "" );
		Map.put(c2stransferStatusAPI.TXNID, RandomGeneration.randomNumeric(14));
		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_PPB17_NegativePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER35");
		EXTGWO2CAPI O2CTransferAPI = new EXTGWO2CAPI();
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
				
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> Map = c2sTransferStatusDP.getPPBAPIdata();
		HashMap<String, String> apiData = EXTGWO2CDP.getAPIdata();
		String API0 = O2CTransferAPI.prepareAPI(apiData);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		
		TxnId= xmlPath0.get(EXTGWO2CAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = xmlPath0.get(EXTGWO2CAPI.EXTTXNNO);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
		
		
	}


	
	*/
	
	/*
	 * GIFT RECHARGE
	 */
	
	
	
	//@Test
	public void TC_GRC01_PositivePPBTransferStatusAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2STRANSFER37");
		EXTGW_GRCAPI GRCTransferAPI = new EXTGW_GRCAPI();
		c2stransferStatusAPI c2stransferStatusAPI = new c2stransferStatusAPI();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_GRCDP.getAPIdata();
		HashMap<String, String> Map = c2sTransferStatusDP.getGRCAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = GRCTransferAPI.prepareAPI(dataMap);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
		TxnId= xmlPath0.get(EXTGW_GRCAPI.TXNID);
		System.out.println(TxnId);

		extRefNo = dataMap.get(GRCTransferAPI.EXTREFNUM);

		Map.put(c2stransferStatusAPI.TXNID, TxnId);
		Map.put(c2stransferStatusAPI.EXTREFNUM, extRefNo );

		String API = c2stransferStatusAPI.prepareAPI(Map);
		System.out.println(API);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

}
