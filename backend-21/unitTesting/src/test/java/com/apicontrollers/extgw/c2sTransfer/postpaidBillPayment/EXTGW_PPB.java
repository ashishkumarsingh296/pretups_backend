package com.apicontrollers.extgw.c2sTransfer.postpaidBillPayment;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.Map_CommissionProfile;
import com.Features.TransferControlProfile;
import com.apicontrollers.extgw.c2sTransferStatus.c2sTransferStatusDP;
import com.apicontrollers.extgw.c2sTransferStatus.c2stransferStatusAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CAPI;
import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CDP;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
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

public class EXTGW_PPB extends BaseTest{


	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	String service = null;
	public String TxnId;
	public String extRefNo;

	/**
	 * @throws Exception
	 * @testid EXTGWPPB01
	 * Positive Test Case For TRFCATEGORY: PostPaid Bill Payment
	 * 
	 */



	@Test
	public void TC_A_PositivePPBAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB01");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

      Object[] dataObject = EXTGWPPBDP.getAPIdataWithAllUsers();

		
		for (int i = 0; i < dataObject.length; i++) {
			
			EXTGW_PPBDAO APIDAO = (EXTGW_PPBDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		apiData.put(C2SBillPaymentAPI.LOGINID, "");
		apiData.put(C2SBillPaymentAPI.PASSWORD, "");
		apiData.put(C2SBillPaymentAPI.EXTCODE, "");
		String API = C2SBillPaymentAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}


	@Test
	public void TC_B_NegativeC2SAPI_BlankPin() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB02");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		dataMap.put(C2SBillPaymentAPI.LOGINID, "");
		dataMap.put(C2SBillPaymentAPI.PASSWORD, "");
		dataMap.put(C2SBillPaymentAPI.EXTCODE, "");
		dataMap.put(C2SBillPaymentAPI.PIN, "");
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_C_PositiveC2SAPI_withBlankMSISDN_PIN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB03");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		dataMap.put(C2SBillPaymentAPI.MSISDN, "");
		dataMap.put(C2SBillPaymentAPI.PIN,"");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_D_PositiveC2SAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB04");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_E_PositiveC2SAPI_BlankExtCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB05");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		dataMap.put(C2SBillPaymentAPI.EXTCODE, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_F_PositiveC2SAPI_BlankLoginPwd() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB06");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		dataMap.put(C2SBillPaymentAPI.LOGINID, "");
		dataMap.put(C2SBillPaymentAPI.PASSWORD,"");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_G_NegativeC2SAPI_withInvalidPassword() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB07");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		dataMap.put(C2SBillPaymentAPI.PASSWORD, RandomGeneration.randomNumeric(9));

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_H_NegativeC2SAPI_BlankPassword() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB08");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		dataMap.put(C2SBillPaymentAPI.PASSWORD, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	 @Test
	public void TC_I_NegativeC2SAPI_BlankSubMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB09");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		dataMap.put(C2SBillPaymentAPI.MSISDN2, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	





	@Test
	public void TC_J_NegativeC2SAPI_InvalidSubMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB10");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		String MSISDN2 = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) +RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
		dataMap.put(C2SBillPaymentAPI.MSISDN, MSISDN2);
		dataMap.put(C2SBillPaymentAPI.LOGINID, "");
		dataMap.put(C2SBillPaymentAPI.EXTCODE, "");
		dataMap.put(C2SBillPaymentAPI.PASSWORD, "");
		dataMap.put(C2SBillPaymentAPI.EXTREFNUM, "");
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	





	@Test
	public void TC_K_NegativeC2SAPI_InvalidLanguageCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB11");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();

		//dataMap.put(C2SBillPaymentAPI.LANGUAGE1,RandomGeneration.randomNumeric(3));
		dataMap.put(C2SBillPaymentAPI.LANGUAGE2,RandomGeneration.randomNumeric(3));

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_L_NegativeC2SAPI_InvalidChannelMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB12");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();

		/*String CorrectMSISDN = dataMap.get(C2SBillPaymentAPI.MSISDN);

		String InValMSISDN;

		do
		{
			InValMSISDN = RandomGeneration.randomNumeric(9);

		} while(CorrectMSISDN==InValMSISDN);*/


		String MSISDN = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) +RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
		dataMap.put(C2SBillPaymentAPI.MSISDN, MSISDN);
		dataMap.put(C2SBillPaymentAPI.LOGINID, "");
		dataMap.put(C2SBillPaymentAPI.EXTCODE,"");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	



	@Test
	public void TC_M_NegativeC2SAPI_InvalidPin() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB13");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();
		RandomGeneration RandomGeneration = new RandomGeneration();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();


		String CorrectPin = dataMap.get(C2SBillPaymentAPI.PIN);

		String InValPin;

		do
		{
			InValPin = RandomGeneration.randomNumeric(4);

		}while(CorrectPin==InValPin);

		dataMap.put(C2SBillPaymentAPI.PIN,InValPin);	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_N_NegativeC2SAPI_InvalidExtCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB14");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();
		RandomGeneration RandomGeneration = new RandomGeneration();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();


		String EXTCODE = dataMap.get(C2SBillPaymentAPI.EXTCODE);

		String InValExtCode;

		do
		{
			InValExtCode = RandomGeneration.randomNumeric(6);

		}while(EXTCODE==InValExtCode);

		dataMap.put(C2SBillPaymentAPI.EXTCODE,InValExtCode);	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TC_O_PositiveC2SAPI_alphaNumericExtCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB15");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		dataMap.put(C2SBillPaymentAPI.EXTCODE, RandomGeneration.randomAlphaNumeric(6));

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_P_NegativeC2SAPI_BlankAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB16");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();

		dataMap.put(C2SBillPaymentAPI.AMOUNT,"");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_Q_NegativeC2SAPI_NegAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB17");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();

		dataMap.put(C2SBillPaymentAPI.AMOUNT,"-1");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_R_NegativeC2SAPI_IncorrectSelectorCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB18");
		EXTGW_PPB_API C2SBillPaymentAPI = new EXTGW_PPB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();

		dataMap.put(C2SBillPaymentAPI.SELECTOR,"-1");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = C2SBillPaymentAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}








	@Test
	public void TC_S_suspendAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB19");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		String Transfer_ID = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = PPBTransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Suspend Additional Commission Profile slab");

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)
		{			
			if((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(_masterVO.getProperty("PostpaidBillPaymentCode"))))
				break;
		}
		service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		Log.info("service is:" +service);

		long time2 = CommissionProfile.suspendAdditionalCommProfileForGivenService(EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName , service,_masterVO.getProperty("PostpaidBillPaymentCode"));

		Thread.sleep(time2);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGW_PPB_API.TXNID);

		String TransferIDExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);
		if(Transfer_ID == null|| Transfer_ID.equals("")){
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		}
		else{
			if (!TransferIDExists.equals("Y")){
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				currentNode.log(Status.PASS,"Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
			}		
			else 		
			{
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
			}
		}
	}



	@Test
	public void TC_T_resumeAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB20");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID1= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = PPBTransferAPI.prepareAPI(dataMap);
		ExtentI.Markup(ExtentColor.TEAL, "Resume Additional Commission Profile slab");

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)
		{			
			if((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(_masterVO.getProperty("PostpaidBillPaymentCode"))))
				break;
		}
		String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		Log.info("service is:" +service);

		long time = CommissionProfile.resumeAdditionalCommProfileForGivenService(EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName , service,_masterVO.getProperty("PostpaidBillPaymentCode"));
		Log.info("Wait for Commission Profile Version to be active");
		Thread.sleep(time);

		ExtentI.Markup(ExtentColor.TEAL, "Perform C2S transaction after Resuming Additional Commission Profile slab");


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString());


		Transfer_ID1 = xmlPath.get(EXTGW_PPB_API.TXNID);
		String TransferIDExists1 = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID1);
		if(Transfer_ID1 == null|| Transfer_ID1.equals("")){
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		}
		else
		{
			if (!TransferIDExists1.equals("Y")){
				ExtentI.Markup(ExtentColor.RED, "Transaction ID does not exist as: " + Transfer_ID1 + " in Adjustments Table,Hence TestCase is not Successful");
				currentNode.log(Status.FAIL,"Transaction ID does not exist as: " + Transfer_ID1 + " in Adjustments Table,Hence TestCase is not Successful");
			}		
			else 
			{
				ExtentI.Markup(ExtentColor.GREEN, "TestCase is successful as Transfer ID : "+Transfer_ID1+" exists in Adjustments table ");
				currentNode.log(Status.PASS, "TestCase is successful as Transfer ID : "+Transfer_ID1+" exists in Adjustments table ");
			}
		}
	}


	@Test
	public void TC_U_NegativeSuspendTCP() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB21");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		
		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGWPPBDP.getAPIdata();
		String API = PPBTransferAPI.prepareAPI(apiData);

		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.TCPName, null);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		TCPObj.channelLevelTransferControlProfileActive(0, EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.TCPName, null);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());

	}


	@Test
	public void TC_V_Negative_SenderOutSuspended() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB22");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWPPBDP.getAPIdata();

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(PPBTransferAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGWPPBDP.CUCategory, channelMap);

		String API = PPBTransferAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);

		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGWPPBDP.CUCategory, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_W_Negative__MinResidualBalance() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB23");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		_parser parser = new _parser();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWPPBDP.getAPIdata();

		String API = PPBTransferAPI.prepareAPI(apiData);


		String balance = DBHandler.AccessHandler.getUserBalance(EXTGWPPBDP.ProductCode, EXTGWPPBDP.LoginID);
		parser.convertStringToLong(balance).changeDenomation();

		System.out.println("The balance is:" +balance);
		long usrBalance = (long) (parser.getValue()) - 100 + 2;
		System.out.println(usrBalance);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying Minimum Residual Balance in Transfer Control Profile");


		String[] values = {String.valueOf(usrBalance),String.valueOf(usrBalance)};
		String[] parameters = {"minBalance","altBalance"};

		trfCntrlProf.modifyProductValuesInTCP(EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.TCPID, parameters,values , EXTGWPPBDP.ProductName, true);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	

		ExtentI.Markup(ExtentColor.TEAL, "Updating Minimum Residual Balance in Transfer Control Profile");
		values = new String[]{"0","0"};
		trfCntrlProf.modifyProductValuesInTCP(EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.TCPID, parameters,values , EXTGWPPBDP.ProductName, true);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_X_Negative__C2SMinAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB24");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGWPPBDP.getAPIdata();
		apiData.put(PPBTransferAPI.AMOUNT, "90");
		String API = PPBTransferAPI.prepareAPI(apiData);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SminimumAmt(EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.TCPID, "100","100", EXTGWPPBDP.ProductName);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	

		ExtentI.Markup(ExtentColor.TEAL, "Updating C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SminimumAmt(EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.TCPID,_masterVO.getProperty("MinimumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), EXTGWPPBDP.ProductName);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_Y_C2STaxAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB25");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = PPBTransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");
		String actual = CommissionProfile.getCommissionSlabCount(EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade);
		Log.info(actual);

		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile();

		int slabCount =Integer.parseInt(AddCommMap.get("slabCount"));

		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = AddCommMap;
		for(int k=0;k<slabCount;k++){


			if(k==0){
				slabMap.put("Sstart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("A"+k))));
				slabMap.put("Send"+k, String.valueOf(Integer.parseInt(AddCommMap.get("A"+(k+1)))));
			}
			else {slabMap.put("Sstart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("A"+k))+1));
			slabMap.put("Send"+k, AddCommMap.get("A"+(k+1)));	
			}}


		int AddSlabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));

		for(int j=0;j<AddSlabCount;j++){
			if(j==0){
				slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+100));
				slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));
			}
			else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
			slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
			}}


		long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName,service,_masterVO.getProperty("PostpaidBillPaymentCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGW_PPB_API.TXNID);



		for(int j=0;j<AddSlabCount;j++){
			if(j==0){
				slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
				slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));
			}
			else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
			slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
			}}

		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap,EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName,service,_masterVO.getProperty("PostpaidBillPaymentCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

		String TransferIDExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);

		if(Transfer_ID == null|| Transfer_ID.equals("")){
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		}
		else{
			if (!TransferIDExists.equals("Y")){
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				currentNode.log(Status.PASS,"Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
			}		
			else 		
			{
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
			}
		}
	}





	@Test
	public void TC_ZA_C2STaxAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB26");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = PPBTransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");


		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile();

		int slabCount =Integer.parseInt(AddCommMap.get("AddSlabCount"));

		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = AddCommMap;
		for(int k=0;k<slabCount;k++){


			if(k==0){
				slabMap.put("addcommrate1",String.valueOf(Integer.parseInt(AddCommMap.get("A"+k))+4));
				slabMap.put("taxRateAmt", String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+2));
				Log.info("The new value of tax1Value is " + slabMap.get("taxRateAmt"));
				slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+10));
				slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

			}
			else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
			slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
			}}



		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName, service,_masterVO.getProperty("PostpaidBillPaymentCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGW_PPB_API.TXNID);


		long Tax1Value = DBHandler.AccessHandler.getAdditionalTax1Value(Transfer_ID);
		String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);
		String tax1Value=String.valueOf(_parser.getDisplayAmount(Tax1Value));

		if(Transfer_ID == null|| Transfer_ID.equals("")){
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		}
		else if (!transferIdExists.equalsIgnoreCase("Y")){ 
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID" +Transfer_ID + " does not exists in Adjustments table ");
			currentNode.log(Status.FAIL, "TestCase is not successful");
		}

		else{

			if (tax1Value.equals(slabMap.get("taxRateAmt"))){
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID exist as: " + Transfer_ID + " in Adjustments Table with tax1 value as: " +(slabMap.get("taxRateAmt"))+ ",Hence TestCase is Successful");
				currentNode.log(Status.PASS,"Transaction ID exist as: " + Transfer_ID + " in Adjustments Table with tax1 value as: " +(slabMap.get("taxRateAmt"))+ ",Hence TestCase is Successful");
			}		
			else 
			{
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Tax1 Value for TxnId : "+Transfer_ID+" is not equal to " +(slabMap.get("taxRateAmt"))+ " in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Tax1 Value for TxnId : "+Transfer_ID+" is not equal to " +(slabMap.get("taxRateAmt"))+ " in Adjustments table");
			}

		}

		for(int j=0;j<slabCount;j++){


			if(j==0){
				slabMap.put("addcommrate1",String.valueOf(Integer.parseInt(AddCommMap.get("A"+j))));
				slabMap.put("taxRateAmt", String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
				slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
				slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));

			}
			else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
			slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
			}}
		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap,EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName,service,_masterVO.getProperty("PostpaidBillPaymentCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}







	@Test
	public void TC_ZB_C2STimeSlabAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB27");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = PPBTransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");


		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile();


		int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));
		//int slabCount = 5;
		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = AddCommMap;
		for(int k=0;k<slabCount;k++){


			if(k==0){

				slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+10));
				slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

			}
			else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
			slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
			}}



		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_TimeSlab_ParticularService(slabMap,EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName,service,_masterVO.getProperty("PostpaidBillPaymentCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGW_PPB_API.TXNID);



		String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);


		if(Transfer_ID == null|| Transfer_ID.equals("")){
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		}
		else{
			if (!transferIdExists.equals("Y")){
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				currentNode.log(Status.PASS,"Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
			}		
			else 		
			{
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
			}
		}

		for(int j=0;j<slabCount;j++){


			if(j==0){

				slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
				slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));

			}
			else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
			slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
			}}
		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName,service,_masterVO.getProperty("PostpaidBillPaymentCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}






	@Test
	public void TC_ZC_C2SMinTransferValidation()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB28");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = PPBTransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying Min Transfer Amount of  Additional Commission Profile slab");
		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile();
		int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));
		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = AddCommMap;
		slabMap.put("MintransferValue", "110");
		for(int k=0;k<slabCount;k++){

			if(k==0){

				slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+200));
				slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

			}
			else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
			slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
			}}

		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName,service,_masterVO.getProperty("PostpaidBillPaymentCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGW_PPB_API.TXNID);
		String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);

		if(Transfer_ID == null|| Transfer_ID.equals("")){
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		}
		else{
			if (!transferIdExists.equals("Y")){
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				currentNode.log(Status.PASS,"Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
			}		
			else 		
			{
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
			}
		}

		slabMap.put("MintransferValue", _masterVO.getProperty("MintransferValue"));

		for(int j=0;j<slabCount;j++){


			if(j==0){

				slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
				slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));

			}
			else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
			slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
			}}
		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName,service,_masterVO.getProperty("PostpaidBillPaymentCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}




	@Test
	public void TC_ZD_C2S_SlabToRangeValidation()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWPPB29");
		EXTGW_PPB_API PPBTransferAPI = new EXTGW_PPB_API();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGWPPBDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = PPBTransferAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying To Range of  Additional Commission Profile slab");


		Map<String,String> AddCommMap = Map_CommProfile.DataMap_CommissionProfile();


		int slabCount = Integer.parseInt(AddCommMap.get("AddSlabCount"));

		Map<String,String> slabMap = new HashMap<String, String>();

		slabMap = AddCommMap;

		for(int k=0;k<slabCount;k++){

			if(k==0){

				slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))));
				slabMap.put("AddSend"+k, "90");

			}
			else if(k==1){

				slabMap.put("AddStart"+k, "200");
				slabMap.put("AddSend"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(k+1)))));

			}
			else {slabMap.put("AddStart"+k, String.valueOf(Integer.parseInt(AddCommMap.get("B"+k))+1));
			slabMap.put("AddSend"+k, AddCommMap.get("B"+(k+1)));	
			}}


		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName,service,_masterVO.getProperty("PostpaidBillPaymentCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_PPB_API.TXNSTATUS).toString());


		Transfer_ID = xmlPath.get(EXTGW_PPB_API.TXNID);



		String transferIdExists = DBHandler.AccessHandler.checkForC2STRANSFER_ID(Transfer_ID);


		if(Transfer_ID == null|| Transfer_ID.equals("")){
			ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : null or blank ");
			currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : null or blank ");
		}
		else{
			if (!transferIdExists.equals("Y")){
				ExtentI.Markup(ExtentColor.GREEN, "Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
				currentNode.log(Status.PASS,"Transaction ID does not exist as: " + Transfer_ID + " in Adjustments Table,Hence TestCase is Successful");
			}		
			else 		
			{
				ExtentI.Markup(ExtentColor.RED, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
				currentNode.log(Status.FAIL, "TestCase is not successful as Transfer ID : "+Transfer_ID+" exists in Adjustments table ");
			}
		}



		for(int j=0;j<slabCount;j++){


			if(j==0){

				slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
				slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));

			}
			else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
			slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
			}}
		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGWPPBDP.Domain, EXTGWPPBDP.CUCategory, EXTGWPPBDP.grade, EXTGWPPBDP.CPName,service,_masterVO.getProperty("PostpaidBillPaymentCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}
	/*
	 * PostPaid Bill Payment Transfer Status Test Cases	
	 */
		
		
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
			Validator.partialmessageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
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
			Validator.partialmessageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
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
			Validator.partialmessageCompare(xmlPath.get(c2stransferStatusAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
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




}
