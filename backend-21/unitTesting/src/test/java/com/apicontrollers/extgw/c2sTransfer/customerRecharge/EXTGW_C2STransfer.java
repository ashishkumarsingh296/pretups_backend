package com.apicontrollers.extgw.c2sTransfer.customerRecharge;


import java.util.HashMap;
import java.util.Map;

import com.Features.*;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.ExtentI;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class EXTGW_C2STransfer extends BaseTest {

	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";

	/*
	 * @throws Exception
	 * @test Id EXTGWC2S01
	 * Positive Test Case For TRFCATEGORY: C2S
	 */

	@Test
	public void TC_A_PositiveC2SAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S01");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] dataObject = EXTGWC2SDP.getAPIdataWithAllUsers();


		for (int i = 0; i < dataObject.length; i++) {

			EXTGW_C2SDAO APIDAO = (EXTGW_C2SDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();


			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			String API = C2STransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}


	@Test
	public void TC_B_NegativeC2SAPI_BlankPin() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S02");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		dataMap.put(C2STransferAPI.LOGINID, "");
		dataMap.put(C2STransferAPI.PASSWORD, "");
		dataMap.put(C2STransferAPI.EXTCODE, "");
		dataMap.put(C2STransferAPI.PIN, "");
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_C_PositiveC2SAPI_withBlankMSISDN_PIN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S03");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		dataMap.put(C2STransferAPI.MSISDN, "");
		dataMap.put(C2STransferAPI.PIN, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_D_PositiveC2SAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S04");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_E_PositiveC2SAPI_BlankExtCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S05");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		dataMap.put(C2STransferAPI.EXTCODE, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_F_PositiveC2SAPI_BlankLoginPwd() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S06");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		dataMap.put(C2STransferAPI.LOGINID, "");
		dataMap.put(C2STransferAPI.PASSWORD, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_G_NegativeC2SAPI_withInvalidPassword() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S07");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		dataMap.put(C2STransferAPI.PASSWORD, RandomGeneration.randomNumeric(9));

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_H_NegativeC2SAPI_BlankPassword() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S08");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		dataMap.put(C2STransferAPI.PASSWORD, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_I_NegativeC2SAPI_BlankSubMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S09");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		dataMap.put(C2STransferAPI.MSISDN2, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_J_NegativeC2SAPI_InvalidSubMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S10");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		String MSISDN2 = RandomGeneration.randomAlphabets(2) + RandomGeneration.randomNumeric(8);
		dataMap.put(C2STransferAPI.MSISDN2, MSISDN2);

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_K_NegativeC2SAPI_InvalidLanguageCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S11");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		dataMap.put(C2STransferAPI.LANGUAGE1, RandomGeneration.randomNumeric(3));
		dataMap.put(C2STransferAPI.LANGUAGE2, RandomGeneration.randomNumeric(3));

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_L_NegativeC2SAPI_InvalidChannelMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S12");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		String CorrectMSISDN = dataMap.get(C2STransferAPI.MSISDN);

		String InValMSISDN;

		do {
			InValMSISDN = RandomGeneration.randomNumeric(9);

		} while (CorrectMSISDN == InValMSISDN);


		//String MSISDN = _masterVO.getMasterValue(MasterI.SUBSCRIBER_POSTPAID_PREFIX) +RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
		dataMap.put(C2STransferAPI.MSISDN, InValMSISDN);
		dataMap.put(C2STransferAPI.LOGINID, "");
		dataMap.put(C2STransferAPI.EXTCODE, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_M_NegativeC2SAPI_InvalidPin() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S13");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();


		String CorrectPin = dataMap.get(C2STransferAPI.PIN);

		String InValPin;

		do {
			InValPin = RandomGeneration.randomNumeric(4);

		} while (CorrectPin == InValPin);

		dataMap.put(C2STransferAPI.PIN, InValPin);

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_N_NegativeC2SAPI_InvalidExtCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S14");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();


		String EXTCODE = dataMap.get(C2STransferAPI.EXTCODE);

		String InValExtCode;

		do {
			InValExtCode = RandomGeneration.randomNumeric(6);

		} while (EXTCODE == InValExtCode);

		dataMap.put(C2STransferAPI.EXTCODE, InValExtCode);

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TC_O_PositiveC2SAPI_alphaNumericExtCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S15");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		dataMap.put(C2STransferAPI.EXTCODE, RandomGeneration.randomAlphaNumeric(6));

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_P_NegativeC2SAPI_BlankAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S16");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		dataMap.put(C2STransferAPI.AMOUNT, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_Q_NegativeC2SAPI_NegAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S17");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		dataMap.put(C2STransferAPI.AMOUNT, "-1");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_R_NegativeC2SAPI_IncorrectSelectorCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S18");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();

		dataMap.put(C2STransferAPI.SELECTOR, "-1");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_S_suspendAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S19");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		C2STransfer c2STransfer = new C2STransfer(driver);
		String Transfer_ID = null;
		String Transfer_ID1 = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Suspend Additional Commission Profile slab");

		long time2 = CommissionProfile.suspendAdditionalCommProfileExisting(EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName, _masterVO.getProperty("CustomerRechargeCode"));

		Thread.sleep(time2);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGWC2SAPI.TXNID);

		String TransferIDExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);
		if (Transfer_ID == null || Transfer_ID.equals("")) {
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		} else {
			if (!TransferIDExists.equals("Y")) {
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				currentNode.log(Status.PASS, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
			} else {
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : " + Transfer_ID + " exists in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : " + Transfer_ID + " exists in Adjustments table ");
			}
		}
	}


	@Test
	public void TC_T_resumeAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S20");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID1 = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);
		ExtentI.Markup(ExtentColor.TEAL, "Resume Additional Commission Profile slab");
		long time = CommissionProfile.resumeAdditionalCommProfileSpecificService(EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName, _masterVO.getProperty("CustomerRechargeCode"));
		Log.info("Wait for Commission Profile Version to be active");
		Thread.sleep(time);

		ExtentI.Markup(ExtentColor.TEAL, "Perform C2S transaction after Resuming Additional Commission Profile slab");


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString());


		Transfer_ID1 = xmlPath.get(EXTGWC2SAPI.TXNID);
		String TransferIDExists1 = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID1);
		if (Transfer_ID1 == null || Transfer_ID1.equals("")) {
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		} else {
			if (!TransferIDExists1.equals("Y")) {
				ExtentI.Markup(ExtentColor.RED, "Transaction ID does not exist as: " + Transfer_ID1 + " in Adjustments Table,Hence TestCase is not Successful");
				currentNode.log(Status.FAIL, "Transaction ID does not exist as: " + Transfer_ID1 + " in Adjustments Table,Hence TestCase is not Successful");
			} else {
				ExtentI.Markup(ExtentColor.GREEN, "TestCase is successful as Transfer ID : " + Transfer_ID1 + " exists in Adjustments table ");
				currentNode.log(Status.PASS, "TestCase is successful as Transfer ID : " + Transfer_ID1 + " exists in Adjustments table ");
			}
		}
	}


	@Test
	public void TC_U_NegativeSuspendTCP() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S21");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();
		String API = C2STransferAPI.prepareAPI(apiData);

		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.TCPName, null);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.TCPName, null);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

	}


	@Test
	public void TC_V_Negative_SenderOutSuspended() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S22");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(C2STransferAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);

		String API = C2STransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_W_Negative__MinResidualBalance() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S23");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		_parser parser = new _parser();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();

		String API = C2STransferAPI.prepareAPI(apiData);


		String balance = DBHandler.AccessHandler.getUserBalance(EXTGWC2SDP.ProductCode, EXTGWC2SDP.LoginID);
		parser.convertStringToLong(balance).changeDenomation();

		System.out.println("The balance is:" + balance);
		long usrBalance = (long) (parser.getValue()) - 100 + 2;
		System.out.println(usrBalance);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying Minimum Residual Balance in Transfer Control Profile");


		String[] values = {String.valueOf(usrBalance), String.valueOf(usrBalance)};
		String[] parameters = {"minBalance", "altBalance"};

		trfCntrlProf.modifyProductValuesInTCP(EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.TCPID, parameters, values, EXTGWC2SDP.ProductName, true);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		ExtentI.Markup(ExtentColor.TEAL, "Updating Minimum Residual Balance in Transfer Control Profile");
		values = new String[]{"0", "0"};
		trfCntrlProf.modifyProductValuesInTCP(EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.TCPID, parameters, values, EXTGWC2SDP.ProductName, true);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_X_Negative__C2SMinAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S24");
		RandomGeneration RandomGeneration = new RandomGeneration();
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();
		apiData.put(C2STransferAPI.AMOUNT, "90");
		String API = C2STransferAPI.prepareAPI(apiData);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SminimumAmt(EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.TCPID, "100", "100", EXTGWC2SDP.ProductName);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		ExtentI.Markup(ExtentColor.TEAL, "Updating C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SminimumAmt(EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.TCPID, _masterVO.getProperty("MinimumBalance"), _masterVO.getProperty("AllowedMaxPercentage"), EXTGWC2SDP.ProductName);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_Y_C2STaxAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S25");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);


		String Transfer_ID = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");

		String actual = CommissionProfile.getCommissionSlabCount(EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade);
		Log.info(actual);
		Map<String, String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile();

		int slabCount = Integer.parseInt(AddCommMap.get("slabCount"));

		Map<String, String> slabMap = new HashMap<String, String>();

		slabMap = AddCommMap;
		for (int k = 0; k < slabCount; k++) {


			if (k == 0) {
				slabMap.put("Sstart" + k, String.valueOf(Integer.parseInt(AddCommMap.get("A" + k))));
				slabMap.put("Send" + k, String.valueOf(Integer.parseInt(AddCommMap.get("A" + (k + 1)))));
			} else {
				slabMap.put("Sstart" + k, String.valueOf(Integer.parseInt(AddCommMap.get("A" + k)) + 1));
				slabMap.put("Send" + k, AddCommMap.get("A" + (k + 1)));
			}
		}


		int AddSlabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));

		for (int j = 0; j < AddSlabCount; j++) {
			if (j == 0) {
				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j)) + 100));
				slabMap.put("AddSend" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + (j + 1)))));
			} else {
				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j)) + 1));
				slabMap.put("AddSend" + j, AddCommMap.get("B" + (j + 1)));
			}
		}


		long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName);
		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGWC2SAPI.TXNID);


		for (int j = 0; j < AddSlabCount; j++) {
			if (j == 0) {
				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j))));
				slabMap.put("AddSend" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + (j + 1)))));
			} else {
				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j)) + 1));
				slabMap.put("AddSend" + j, AddCommMap.get("B" + (j + 1)));
			}
		}

		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		CommissionProfile.modifyAdditionalCommissionProfile_SIT(slabMap, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName);
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

		String TransferIDExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);

		if (Transfer_ID == null || Transfer_ID.equals("")) {
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		} else {
			if (!TransferIDExists.equals("Y")) {
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				currentNode.log(Status.PASS, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
			} else {
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : " + Transfer_ID + " exists in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : " + Transfer_ID + " exists in Adjustments table ");
			}
		}
	}


	@Test
	public void TC_Z_1_C2STaxAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S26");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);


		String Transfer_ID = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");


		Map<String, String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile();

		int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));

		Map<String, String> slabMap = new HashMap<String, String>();

		slabMap = AddCommMap;
		for (int k = 0; k < slabCount; k++) {


			if (k == 0) {
				slabMap.put("addcommrate1", String.valueOf(Integer.parseInt(AddCommMap.get("A" + k)) + 4));
				slabMap.put("taxRateAmt", String.valueOf(Integer.parseInt(AddCommMap.get("B" + k)) + 2));
				Log.info("The new value of tax1Value is " + slabMap.get("taxRateAmt"));
				slabMap.put("AddStart" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + k)) + 10));
				slabMap.put("AddSend" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + (k + 1)))));

			} else {
				slabMap.put("AddStart" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + k)) + 1));
				slabMap.put("AddSend" + k, AddCommMap.get("B" + (k + 1)));
			}
		}


		long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName);

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGWC2SAPI.TXNID);


		long Tax1Value = DBHandler.AccessHandler.getAdditionalTax1Value(Transfer_ID);
		String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);
		String tax1Value = String.valueOf(_parser.getDisplayAmount(Tax1Value));

		if (Transfer_ID == null || Transfer_ID.equals("")) {
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		} else if (!transferIdExists.equalsIgnoreCase("Y")) {
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID" + Transfer_ID + " does not exists in Adjustments table ");
			currentNode.log(Status.FAIL, "TestCase is not successful");
		} else {

			if (tax1Value.equals(slabMap.get("taxRateAmt"))) {
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID exist as: " + Transfer_ID + " in Adjustments Table with tax1 value as: " + (slabMap.get("taxRateAmt")) + ",Hence TestCase is Successful");
				currentNode.log(Status.PASS, "Transaction ID exist as: " + Transfer_ID + " in Adjustments Table with tax1 value as: " + (slabMap.get("taxRateAmt")) + ",Hence TestCase is Successful");
			} else {
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Tax1 Value for TxnId : " + Transfer_ID + " is not equal to " + (slabMap.get("taxRateAmt")) + " in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Tax1 Value for TxnId : " + Transfer_ID + " is not equal to " + (slabMap.get("taxRateAmt")) + " in Adjustments table");
			}

		}

		for (int j = 0; j < slabCount; j++) {


			if (j == 0) {
				slabMap.put("addcommrate1", String.valueOf(Integer.parseInt(AddCommMap.get("A" + j))));
				slabMap.put("taxRateAmt", String.valueOf(Integer.parseInt(AddCommMap.get("B" + j))));
				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j))));
				slabMap.put("AddSend" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + (j + 1)))));

			} else {
				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j)) + 1));
				slabMap.put("AddSend" + j, AddCommMap.get("B" + (j + 1)));
			}
		}
		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		CommissionProfile.modifyAdditionalCommissionProfile_SIT(slabMap, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName);
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}


	@Test
	public void TC_Z_2_C2STimeSlabAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S27");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");


		Map<String, String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile();


		int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));
		//int slabCount = 5;
		Map<String, String> slabMap = new HashMap<String, String>();

		slabMap = AddCommMap;
		for (int k = 0; k < slabCount; k++) {


			if (k == 0) {

				slabMap.put("AddStart" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + k)) + 10));
				slabMap.put("AddSend" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + (k + 1)))));

			} else {
				slabMap.put("AddStart" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + k)) + 1));
				slabMap.put("AddSend" + k, AddCommMap.get("B" + (k + 1)));
			}
		}


		long time2 = CommissionProfile.modifyAdditionalCommissionProfile_TimeSlab(slabMap, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName, _masterVO.getProperty("CustomerRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGWC2SAPI.TXNID);


		String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);


		if (Transfer_ID == null || Transfer_ID.equals("")) {
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		} else {
			if (!transferIdExists.equals("Y")) {
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				currentNode.log(Status.PASS, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
			} else {
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : " + Transfer_ID + " exists in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : " + Transfer_ID + " exists in Adjustments table ");
			}
		}

		for (int j = 0; j < slabCount; j++) {


			if (j == 0) {

				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j))));
				slabMap.put("AddSend" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + (j + 1)))));

			} else {
				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j)) + 1));
				slabMap.put("AddSend" + j, AddCommMap.get("B" + (j + 1)));
			}
		}
		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		long time = CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName);
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}


	@Test
	public void TC_Z_3_C2SMinTransferValidation()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S28");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying Min Transfer Amount of  Additional Commission Profile slab");
		Map<String, String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile();
		int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));
		Map<String, String> slabMap = new HashMap<String, String>();

		slabMap = AddCommMap;
		slabMap.put("MintransferValue", "110");
		for (int k = 0; k < slabCount; k++) {

			if (k == 0) {

				slabMap.put("AddStart" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + k)) + 200));
				slabMap.put("AddSend" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + (k + 1)))));

			} else {
				slabMap.put("AddStart" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + k)) + 1));
				slabMap.put("AddSend" + k, AddCommMap.get("B" + (k + 1)));
			}
		}

		long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName);

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGWC2SAPI.TXNID);
		String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);

		if (Transfer_ID == null || Transfer_ID.equals("")) {
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		} else {
			if (!transferIdExists.equals("Y")) {
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				currentNode.log(Status.PASS, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
			} else {
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : " + Transfer_ID + " exists in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : " + Transfer_ID + " exists in Adjustments table ");
			}
		}

		slabMap.put("MintransferValue", _masterVO.getProperty("MintransferValue"));

		for (int j = 0; j < slabCount; j++) {


			if (j == 0) {

				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j))));
				slabMap.put("AddSend" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + (j + 1)))));

			} else {
				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j)) + 1));
				slabMap.put("AddSend" + j, AddCommMap.get("B" + (j + 1)));
			}
		}
		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		long time = CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName);
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}


	@Test
	public void TC_Z_4_C2S_SlabToRangeValidation()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S29");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWC2SDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);


		String Transfer_ID = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2STransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying To Range of  Additional Commission Profile slab");


		Map<String, String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile();


		int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));

		Map<String, String> slabMap = new HashMap<String, String>();

		slabMap = AddCommMap;

		for (int k = 0; k < slabCount; k++) {

			if (k == 0) {

				slabMap.put("AddStart" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + k))));
				slabMap.put("AddSend" + k, "90");

			} else if (k == 1) {

				slabMap.put("AddStart" + k, "200");
				slabMap.put("AddSend" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + (k + 1)))));

			} else {
				slabMap.put("AddStart" + k, String.valueOf(Integer.parseInt(AddCommMap.get("B" + k)) + 1));
				slabMap.put("AddSend" + k, AddCommMap.get("B" + (k + 1)));
			}
		}


		long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName);

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGWC2SAPI.TXNID);


		String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);


		if (Transfer_ID == null || Transfer_ID.equals("")) {
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		} else {
			if (!transferIdExists.equals("Y")) {
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				currentNode.log(Status.PASS, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
			} else {
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : " + Transfer_ID + " exists in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : " + Transfer_ID + " exists in Adjustments table ");
			}
		}


		for (int j = 0; j < slabCount; j++) {


			if (j == 0) {

				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j))));
				slabMap.put("AddSend" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + (j + 1)))));

			} else {
				slabMap.put("AddStart" + j, String.valueOf(Integer.parseInt(AddCommMap.get("B" + j)) + 1));
				slabMap.put("AddSend" + j, AddCommMap.get("B" + (j + 1)));
			}
		}
		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		long time = CommissionProfile.modifyAdditionalCommissionProfile_SIT(AddCommMap, EXTGWC2SDP.Domain, EXTGWC2SDP.CUCategory, EXTGWC2SDP.grade, EXTGWC2SDP.CPName);
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

		//Added by siddharth tomer
	}

	@Test
	public void TC_Z_5_BarredSenderC2SAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S30");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] dataObject = EXTGWC2SDP.getAPIdataWithAllUsers();


		for (int i = 0; i < dataObject.length; i++) {

			EXTGW_C2SDAO APIDAO = (EXTGW_C2SDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();


			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			BarUnbar BarUnbar = new BarUnbar(driver);
			ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
			BarUnbar.barringUser("C2S", "SENDER", apiData.get(C2STransferAPI.MSISDN));
			String API = C2STransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
			BarUnbar.unBarringUser("C2S", "SENDER", apiData.get(C2STransferAPI.MSISDN));
		}
	}

	@Test
	public void TC_Z_6_BarredReceiverC2SAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S31");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] dataObject = EXTGWC2SDP.getAPIdataWithAllUsers();


		for (int i = 0; i < dataObject.length; i++) {

			EXTGW_C2SDAO APIDAO = (EXTGW_C2SDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();


			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			BarUnbar BarUnbar = new BarUnbar(driver);
			ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
			BarUnbar.barringUser("C2S", "RECEIVER", apiData.get(C2STransferAPI.MSISDN));
			String API = C2STransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
			BarUnbar.unBarringUser("C2S", "RECEIVER", apiData.get(C2STransferAPI.MSISDN));
		}
	}

	@Test
	public void TC_Z_7_BarredReceiversubsC2SAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S32");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] dataObject = EXTGWC2SDP.getAPIdataWithAllUsers();


		for (int i = 0; i < dataObject.length; i++) {

			EXTGW_C2SDAO APIDAO = (EXTGW_C2SDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();


			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			BarUnbar BarUnbar = new BarUnbar(driver);
			ExtentI.Markup(ExtentColor.TEAL, "Barring Subscriber");
			BarUnbar.barringUser("C2S", "RECEIVER", apiData.get(C2STransferAPI.MSISDN2));
			String API = C2STransferAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			ExtentI.Markup(ExtentColor.TEAL, "Unbarring Subscriber");
			BarUnbar.unBarringUser("C2S", "RECEIVER", apiData.get(C2STransferAPI.MSISDN2));
		}
	}

	@Test
	public void TC_Z_8_SuspendINC2SAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S33");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		EXTGWC2SDP EXTGWC2SDP = new EXTGWC2SDP();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(C2STransferAPI.MSISDN));
		channelMap.put("inSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);

		String API = C2STransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		channelMap.put("inSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWC2SDP.CUCategory, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TC_Z_9_SuspendedC2SAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2S34");
		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
		ResumeChannelUser CUResume = new ResumeChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> apiData = EXTGWC2SDP.getAPIdata();


		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		CUSuspend.suspendChannelUser_MSISDN(apiData.get(C2STransferAPI.MSISDN), "Suspending channel user");
		CUSuspend.approveCSuspendRequest_MSISDN(apiData.get(C2STransferAPI.MSISDN), "Approval for suspending channel user");
		String API = C2STransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		CUResume.resumeChannelUser_MSISDN(apiData.get(C2STransferAPI.MSISDN), "Resuming channel user");

	}
}


