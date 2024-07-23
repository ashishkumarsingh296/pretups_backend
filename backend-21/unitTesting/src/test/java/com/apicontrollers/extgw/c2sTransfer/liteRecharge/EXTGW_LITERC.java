package com.apicontrollers.extgw.c2sTransfer.liteRecharge;

import java.util.HashMap;
import java.util.Map;

import com.Features.*;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import org.testng.annotations.Test;

import com.apicontrollers.extgw.cardGroupEnquiry.EXTGW_CRDGRPENQ_API;
import com.apicontrollers.extgw.cardGroupEnquiry.EXTGW_CardGroupENQ_DP;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.PretupsI;
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

public class EXTGW_LITERC extends BaseTest{


	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	String service = null;
	String serviceClass = null;


	@Test
	public void TC_LiteRC01_Positive() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC01");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}



		Object[] dataObject = EXTGW_LiteRCDP.getAPIdataWithAllUsers();




		for (int i = 0; i < dataObject.length; i++) {

			EXTGW_LITERCDAO APIDAO = (EXTGW_LITERCDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();


			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);

			String API = EXTGWLiteRCAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}



	@Test
	public void TC_LiteRC02_NegativeLiteRCAPI_BlankPin() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC02");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();

		dataMap.put(EXTGWLiteRCAPI.PIN, "");


		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_LiteRC03_NegativeLiteRCAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC03");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		dataMap.put(EXTGWLiteRCAPI.EXTNWCODE, "");
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	@Test
	public void TC_LiteRC04_PositiveLiteRCAPI_withBlankSenderMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC04");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		dataMap.put(EXTGWLiteRCAPI.MSISDN1, "");
		dataMap.put(EXTGWLiteRCAPI.PIN,"");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_LiteRC05_Negative_BlankSubMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC05");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		dataMap.put(EXTGWLiteRCAPI.MSISDN2, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	





	@Test
	public void TC_LiteRC06_NegativeLiteRCAPI_InvalidSubMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC06");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		String MSISDN2 = _masterVO.getMasterValue(MasterI.SUBSCRIBER_POSTPAID_PREFIX) +RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
		dataMap.put(EXTGWLiteRCAPI.MSISDN2, MSISDN2);

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	





	@Test
	public void TC_LiteRC07_NegativeLiteRCAPI_InvalidLanguageCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC07");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();

		dataMap.put(EXTGWLiteRCAPI.LANGUAGE2,RandomGeneration.randomNumeric(3));
		dataMap.put(EXTGWLiteRCAPI.LANGUAGE1,RandomGeneration.randomNumeric(3));

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_LiteRC08_NegativeLiteRCAPI_InvalidChannelMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC08");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();



		String MSISDN = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) +RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
		dataMap.put(EXTGWLiteRCAPI.MSISDN1, MSISDN);
		dataMap.put(EXTGWLiteRCAPI.LOGINID, "");
		dataMap.put(EXTGWLiteRCAPI.EXTCODE,"");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	



	@Test
	public void TC_LiteRC09_NegativeLiteRCAPI_InvalidPin() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC09");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();


		String CorrectPin = dataMap.get(EXTGWLiteRCAPI.PIN);

		String InValPin;

		do
		{
			InValPin = RandomGeneration.randomNumeric(4);

		}while(CorrectPin==InValPin);

		dataMap.put(EXTGWLiteRCAPI.PIN,InValPin);	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_LiteRC10_NegativeLiteRCAPI_BlankAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC10");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();

		dataMap.put(EXTGWLiteRCAPI.AMOUNT,"");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_LiteRC11_NegativeLiteRCAPI_NegAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC11");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();

		dataMap.put(EXTGWLiteRCAPI.AMOUNT,"-1");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_LiteRC12_NegativeLiteRCAPI_IncorrectSelectorCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC12");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();

		dataMap.put(EXTGWLiteRCAPI.SELECTOR,RandomGeneration.randomAlphaNumeric(5));	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_LiteRC13_NegativeLiteRCAPI_IncorrectSERVICECLASS() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC13");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();

		dataMap.put(EXTGWLiteRCAPI.SERVICECLASS,"abc");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_LiteRC14_PositiveLiteRCAPI_ValidSERVICECLASS() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC14");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();
		EXTGW_CRDGRPENQ_API CardGroupAPI = new EXTGW_CRDGRPENQ_API();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		HashMap<String, String> apiData1 = EXTGW_CardGroupENQ_DP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API0 = CardGroupAPI.prepareAPI(apiData1);
		String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API0);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
		XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);

		serviceClass = xmlPath0.get(CardGroupAPI.SERVICECLASS);
		Log.info("Service class from Card Group Enquiry Response status is" +serviceClass);



		dataMap.put(EXTGWLiteRCAPI.SERVICECLASS,DBHandler.AccessHandler.getServiceClassID(serviceClass));	


		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_LiteRC15_PositiveLiteRCAPI_DecimalAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC15");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();

		dataMap.put(EXTGWLiteRCAPI.AMOUNT,"95.5");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_LiteRC16_suspendAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC16");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		String Transfer_ID = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Suspend Additional Commission Profile slab");

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)
		{			
			if((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(_masterVO.getProperty("CustomerRechargeCode"))))
				break;
		}
		service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		Log.info("service is:" +service);

		long time2 = CommissionProfile.suspendAdditionalCommProfileForGivenService(EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName , service,_masterVO.getProperty("CustomerRechargeCode"));

		Thread.sleep(time2);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_LiteRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.CHANNEL_TO_SUBSCRIBER_TRANSACTION_ID);


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
	public void TC_LiteRC17_resumeAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC17");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID1= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);
		ExtentI.Markup(ExtentColor.TEAL, "Resume Additional Commission Profile slab");

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)
		{			
			if((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(_masterVO.getProperty("CustomerRechargeCode"))))
				break;
		}
		String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		Log.info("service is:" +service);

		long time = CommissionProfile.resumeAdditionalCommProfileForGivenService(EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName , service,_masterVO.getProperty("CustomerRechargeCode"));
		Log.info("Wait for Commission Profile Version to be active");
		Thread.sleep(time);

		ExtentI.Markup(ExtentColor.TEAL, "Perform C2S transaction after Resuming Additional Commission Profile slab");


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_LiteRCAPI.MESSAGE);
		Transfer_ID1 = _parser.getTransactionID(Message, PretupsI.CHANNEL_TO_SUBSCRIBER_TRANSACTION_ID);
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
	public void TC_LiteRC18_NegativeSuspendTCP() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC18");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LiteRCDP.getAPIdata();
		String API = EXTGWLiteRCAPI.prepareAPI(apiData);

		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.TCPName, null);

		String[] APIResponse = null;

		try{
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}
		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Gift Recharge is not successful with  error message" );

		}

		TCPObj.channelLevelTransferControlProfileActive(0, EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.TCPName, null);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

	}


	@Test
	public void TC_LiteRC19_Negative_SenderOutSuspended() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC19");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_LiteRCDP.getAPIdata();

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(EXTGWLiteRCAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGW_LiteRCDP.CUCategory, channelMap);

		String[] APIResponse = null;
		try{
			String API = EXTGWLiteRCAPI.prepareAPI(apiData);
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}

		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Lite Recharge is not successful with  error message" );

		}

		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGW_LiteRCDP.CUCategory, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_LiteRC20_Negative__MinResidualBalance() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC20");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();
		_parser parser = new _parser();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_LiteRCDP.getAPIdata();

		String API = EXTGWLiteRCAPI.prepareAPI(apiData);


		String balance = DBHandler.AccessHandler.getUserBalance(EXTGW_LiteRCDP.ProductCode, EXTGW_LiteRCDP.LoginID);
		parser.convertStringToLong(balance).changeDenomation();

		System.out.println("The balance is:" +balance);
		long usrBalance = (long) (parser.getValue()) - 100 + 2;
		System.out.println(usrBalance);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying Minimum Residual Balance in Transfer Control Profile");


		String[] values = {String.valueOf(usrBalance),String.valueOf(usrBalance)};
		String[] parameters = {"minBalance","altBalance"};

		trfCntrlProf.modifyProductValuesInTCP(EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.TCPID, parameters,values , EXTGW_LiteRCDP.ProductName, true);

		String[] APIResponse = null;

		try{
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);	
		}
		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Gift Recharge is not successful with  error message" );

		}

		ExtentI.Markup(ExtentColor.TEAL, "Updating Minimum Residual Balance in Transfer Control Profile");
		values = new String[]{"0","0"};
		trfCntrlProf.modifyProductValuesInTCP(EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.TCPID, parameters,values , EXTGW_LiteRCDP.ProductName, true);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_LiteRC21_Negative__C2SMinAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC21");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_LiteRCDP.getAPIdata();
		apiData.put(EXTGWLiteRCAPI.AMOUNT, "90");
		String API = EXTGWLiteRCAPI.prepareAPI(apiData);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SminimumAmt(EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.TCPID, "100","100", EXTGW_LiteRCDP.ProductName);

		String[] APIResponse = null;
		try{
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}
		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Gift Recharge is not successful with  error message" );

		}

		ExtentI.Markup(ExtentColor.TEAL, "Updating C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SminimumAmt(EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.TCPID,_masterVO.getProperty("MinimumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), EXTGW_LiteRCDP.ProductName);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_LiteRC22_C2STaxAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC22");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");
		String actual = CommissionProfile.getCommissionSlabCount(EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade);
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


		long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName,service,_masterVO.getProperty("CustomerRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_LiteRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.CHANNEL_TO_SUBSCRIBER_TRANSACTION_ID);



		for(int j=0;j<AddSlabCount;j++){
			if(j==0){
				slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
				slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));
			}
			else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
			slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
			}}

		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap,EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName,service,_masterVO.getProperty("CustomerRechargeCode"));
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
	public void TC_LiteRC23_C2STaxAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC23");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);

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



		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName, service,_masterVO.getProperty("CustomerRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_LiteRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.CHANNEL_TO_SUBSCRIBER_TRANSACTION_ID);


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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap,EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName,service,_masterVO.getProperty("CustomerRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}







	@Test
	public void TC_LiteRC24_C2STimeSlabAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC24");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);

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



		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_TimeSlab_ParticularService(slabMap,EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName,service,_masterVO.getProperty("CustomerRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_LiteRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.CHANNEL_TO_SUBSCRIBER_TRANSACTION_ID);



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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName,service,_masterVO.getProperty("CustomerRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}






	@Test
	public void TC_LiteRC25_C2SMinTransferValidation()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC25");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);

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

		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName,service,_masterVO.getProperty("CustomerRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_LiteRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.CHANNEL_TO_SUBSCRIBER_TRANSACTION_ID);
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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName,service,_masterVO.getProperty("CustomerRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}




	@Test
	public void TC_LiteRC26_SlabToRangeValidation()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC26");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_LiteRCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWLiteRCAPI.prepareAPI(dataMap);

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


		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName,service,_masterVO.getProperty("CustomerRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_LiteRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.CHANNEL_TO_SUBSCRIBER_TRANSACTION_ID);



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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_LiteRCDP.Domain, EXTGW_LiteRCDP.CUCategory, EXTGW_LiteRCDP.grade, EXTGW_LiteRCDP.CPName,service,_masterVO.getProperty("CustomerRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}
	@Test
	public void TC_LiteRC27_Barredsender() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC27");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}



		Object[] dataObject = EXTGW_LiteRCDP.getAPIdataWithAllUsers();




		for (int i = 0; i < dataObject.length; i++) {

			EXTGW_LITERCDAO APIDAO = (EXTGW_LITERCDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();


			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			BarUnbar BarUnbar = new BarUnbar(driver);
			ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
			BarUnbar.barringUser("C2S","SENDER",apiData.get(EXTGWLiteRCAPI.MSISDN1));
			String API = EXTGWLiteRCAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
			BarUnbar.unBarringUser("C2S","SENDER",apiData.get(EXTGWLiteRCAPI.MSISDN1));
		}
	}
	@Test
	public void TC_LiteRC28_Barredreceiver() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC28");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}



		Object[] dataObject = EXTGW_LiteRCDP.getAPIdataWithAllUsers();




		for (int i = 0; i < dataObject.length; i++) {

			EXTGW_LITERCDAO APIDAO = (EXTGW_LITERCDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();


			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			BarUnbar BarUnbar = new BarUnbar(driver);
			ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
			BarUnbar.barringUser("C2S","RECEIVER",apiData.get(EXTGWLiteRCAPI.MSISDN1));
			String API = EXTGWLiteRCAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
			BarUnbar.unBarringUser("C2S","RECEIVER",apiData.get(EXTGWLiteRCAPI.MSISDN1));
		}
	}

	@Test
	public void TC_LiteRC29_Barredsubscriberreceiver() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC29");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}



		Object[] dataObject = EXTGW_LiteRCDP.getAPIdataWithAllUsers();




		for (int i = 0; i < dataObject.length; i++) {

			EXTGW_LITERCDAO APIDAO = (EXTGW_LITERCDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();


			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			BarUnbar BarUnbar = new BarUnbar(driver);
			ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
			BarUnbar.barringUser("C2S","RECEIVER",apiData.get(EXTGWLiteRCAPI.MSISDN2));
			String API = EXTGWLiteRCAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
			BarUnbar.unBarringUser("C2S","RECEIVER",apiData.get(EXTGWLiteRCAPI.MSISDN2));
		}
	}
	@Test
	public void TC_LiteRC30_NegativeSuspended() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC30");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();
		SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
		ResumeChannelUser CUResume = new ResumeChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

			HashMap<String, String> apiData = EXTGW_LiteRCDP.getAPIdata();


			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			CUSuspend.suspendChannelUser_MSISDN(apiData.get(EXTGWLiteRCAPI.MSISDN1), "Suspending channel user");
			CUSuspend.approveCSuspendRequest_MSISDN(apiData.get(EXTGWLiteRCAPI.MSISDN1), "Approval for suspending channel user");

			String API = EXTGWLiteRCAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			CUResume.resumeChannelUser_MSISDN(apiData.get(EXTGWLiteRCAPI.MSISDN1), "Resuming channel user");
		}
	@Test
	public void TC_LiteRC31_NegativeINSuspended() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWLITERC31");
		EXTGW_LiteRCAPI EXTGWLiteRCAPI = new EXTGW_LiteRCAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_LiteRCDP.getAPIdata();

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(EXTGWLiteRCAPI.MSISDN1));
		channelMap.put("inSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGW_LiteRCDP.CUCategory, channelMap);

		String[] APIResponse = null;
		try{
			String API = EXTGWLiteRCAPI.prepareAPI(apiData);
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}

		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Lite Recharge is not successful with  error message" );

		}

		channelMap.put("inSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGW_LiteRCDP.CUCategory, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_LiteRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




}
