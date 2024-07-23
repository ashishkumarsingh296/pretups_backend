package com.apicontrollers.extgw.c2sTransfer.EXTGW_FixLineRC;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.Features.BarUnbar;
import com.Features.C2STransfer;
import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.Map_CommissionProfile;
import com.Features.TransferControlProfile;
import com.apicontrollers.extgw.c2sTransfer.EXTGW_InternetRC.EXTGW_INTERNETRCAPI;
import com.apicontrollers.extgw.c2sTransfer.EXTGW_InternetRC.EXTGW_INTERNETRCDP;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
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

public class EXTGW_FIXLINERC extends BaseTest{
	
	
	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	String service = null;
	String serviceClass = null;


	@Test
	public void TC_FixLineRC01_Positive() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC01");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] dataObject = EXTGW_FIXLINERCDP.getAPIdataWithAllUsers();

		for (int i = 0; i < dataObject.length; i++) {

			EXTGW_FIXLINERCDAO APIDAO = (EXTGW_FIXLINERCDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), APIDAO.getCategory()));
			currentNode.assignCategory(extentCategory);

			String API = EXTGW_FIXLINERCAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			String txnStatus = DBHandler.AccessHandler.fetchTransferStatus(xmlPath.get(EXTGW_INTERNETRCAPI.TXNID).toString());
			if(txnStatus.equalsIgnoreCase(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString())){
				ExtentI.Markup(ExtentColor.GREEN, "Transaction status updated correctly in Database");
			}
			else{
				ExtentI.Markup(ExtentColor.RED, "The Transaction status as updated in database is : " +txnStatus);
			}
			Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}




	@Test
	public void TC_FixLineRC02_NegativeInternetRCAPI_BlankPin() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC02");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

		dataMap.put(EXTGW_FIXLINERCAPI.PIN, "");
		dataMap.put(EXTGW_FIXLINERCAPI.LOGINID, "");
		dataMap.put(EXTGW_FIXLINERCAPI.PASSWORD, "");
		dataMap.put(EXTGW_FIXLINERCAPI.EXTCODE, "");

		String PinRequired = DBHandler.AccessHandler.check_PIN_REQUIRED(PretupsI.PIN_REQUIRED);

		if(PinRequired.equalsIgnoreCase("false")){
			currentNode.skip("PIN is not mandatory for the transaction,Hence test case skipped");
		}
		else {
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}}



	@Test
	public void TC_FixLineRC03_NegativeInternetRCAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC03");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		dataMap.put(EXTGW_FIXLINERCAPI.EXTNWCODE, "");
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	



	@Test
	public void TC_FixLineRC04_PositiveInternetRCAPI_withBlankSenderMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC04");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		dataMap.put(EXTGW_FIXLINERCAPI.MSISDN, "");
		dataMap.put(EXTGW_FIXLINERCAPI.PIN,"");
		dataMap.put(EXTGW_FIXLINERCAPI.LOGINID,"");
		dataMap.put(EXTGW_FIXLINERCAPI.EXTCODE,"");
		dataMap.put(EXTGW_FIXLINERCAPI.PASSWORD,"");
		dataMap.put(EXTGW_FIXLINERCAPI.EXTREFNUM,"");
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_FixLineRC05_Negative_BlankSubMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC05");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		dataMap.put(EXTGW_FIXLINERCAPI.MSISDN2, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	





	@Test
	public void TC_FixLineRC06_NegativeInternetRCAPI_InvalidSubMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC06");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		String MSISDN2 = _masterVO.getMasterValue(MasterI.SUBSCRIBER_POSTPAID_PREFIX) +RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
		dataMap.put(EXTGW_FIXLINERCAPI.MSISDN2, MSISDN2);

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	





	@Test
	public void TC_FixLineRC07_NegativeInternetRCAPI_InvalidLanguageCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC07");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

		dataMap.put(EXTGW_FIXLINERCAPI.LANGUAGE2,RandomGeneration.randomNumeric(3));
		dataMap.put(EXTGW_FIXLINERCAPI.LANGUAGE1,RandomGeneration.randomNumeric(3));

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_FixLineRC08_NegativeInternetRCAPI_InvalidChannelMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC08");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();



		String MSISDN = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) +RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
		dataMap.put(EXTGW_FIXLINERCAPI.MSISDN, MSISDN);
		dataMap.put(EXTGW_FIXLINERCAPI.LOGINID, "");
		dataMap.put(EXTGW_FIXLINERCAPI.EXTCODE,"");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	



	@Test
	public void TC_FixLineRC09_NegativeInternetRCAPI_InvalidPin() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC09");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();


		String CorrectPin = dataMap.get(EXTGW_FIXLINERCAPI.PIN);

		String InValPin;

		do
		{
			InValPin = RandomGeneration.randomNumeric(4);

		}while(CorrectPin==InValPin);

		dataMap.put(EXTGW_FIXLINERCAPI.PIN,InValPin);	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_FixLineRC10_NegativeInternetRCAPI_BlankAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC10");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

		dataMap.put(EXTGW_FIXLINERCAPI.AMOUNT,"");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_FixLineRC11_NegativeInternetRCAPI_NegAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC11");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

		dataMap.put(EXTGW_FIXLINERCAPI.AMOUNT,"-1");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_FixLineRC12_NegativeInternetRCAPI_IncorrectSelectorCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC12");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

		dataMap.put(EXTGW_FIXLINERCAPI.SELECTOR,RandomGeneration.randomAlphaNumeric(5));	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_FixLineRC13_NegativeInternetRCAPI_InvalidNotificationMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC13");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

		dataMap.put(EXTGW_FIXLINERCAPI.NOTIFICATION_MSISDN,"abc");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_FixLineRC14_PositiveInternetRCAPI_blankNotificationMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC14");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		
		dataMap.put(EXTGW_FIXLINERCAPI.NOTIFICATION_MSISDN,"");


		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_FixLineRC15_PositiveInternetRCAPI_DecimalAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC15");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

		dataMap.put(EXTGW_FIXLINERCAPI.AMOUNT,"95.5");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_FixLineRC16_suspendAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC16");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		String Transfer_ID = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Suspend Additional Commission Profile slab");

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)
		{			
			if((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(_masterVO.getProperty("FixLineRechargeCode"))))
				break;
		}
		service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		Log.info("service is:" +service);

		long time2 = CommissionProfile.suspendAdditionalCommProfileForGivenService(EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName , service,_masterVO.getProperty("FixLineRechargeCode"));

		Thread.sleep(time2);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_FIXLINERCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.FIXLINE_TRANSACTION_ID);


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
	public void TC_FixLineRC17_resumeAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC17");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID1= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		ExtentI.Markup(ExtentColor.TEAL, "Resume Additional Commission Profile slab");

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)
		{			
			if((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(_masterVO.getProperty("FixLineRechargeCode"))))
				break;
		}
		String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		Log.info("service is:" +service);

		long time = CommissionProfile.resumeAdditionalCommProfileForGivenService(EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName , service,_masterVO.getProperty("FixLineRechargeCode"));
		Log.info("Wait for Commission Profile Version to be active");
		Thread.sleep(time);

		ExtentI.Markup(ExtentColor.TEAL, "Perform C2S transaction after Resuming Additional Commission Profile slab");


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_FIXLINERCAPI.MESSAGE);
		Transfer_ID1 = _parser.getTransactionID(Message, PretupsI.FIXLINE_TRANSACTION_ID);
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
	public void TC_FixLineRC18_NegativeSuspendTCP() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC18");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_FIXLINERCDP.getAPIdata();
		String API = EXTGW_FIXLINERCAPI.prepareAPI(apiData);

		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.TCPName, null);

		String[] APIResponse = null;

		try{
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}
		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Recharge is not successful with  error message" );

		}

		TCPObj.channelLevelTransferControlProfileActive(0, EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.TCPName, null);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

	}


	@Test
	public void TC_FixLineRC19_Negative_SenderOutSuspended() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC19");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_FIXLINERCDP.getAPIdata();

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(EXTGW_FIXLINERCAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGW_FIXLINERCDP.CUCategory, channelMap);

		String[] APIResponse = null;
		try{
			String API = EXTGW_FIXLINERCAPI.prepareAPI(apiData);
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}

		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Recharge is not successful with  error message" );

		}

		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGW_FIXLINERCDP.CUCategory, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_FixLineRC20_Negative__MinResidualBalance() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC20");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		_parser parser = new _parser();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_FIXLINERCDP.getAPIdata();

		String API = EXTGW_FIXLINERCAPI.prepareAPI(apiData);


		String balance = DBHandler.AccessHandler.getUserBalance(EXTGW_FIXLINERCDP.ProductCode, EXTGW_FIXLINERCDP.LoginID);
		parser.convertStringToLong(balance).changeDenomation();

		System.out.println("The balance is:" +balance);
		long usrBalance = (long) (parser.getValue()) - 100 + 2;
		System.out.println(usrBalance);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying Minimum Residual Balance in Transfer Control Profile");


		String[] values = {String.valueOf(usrBalance),String.valueOf(usrBalance)};
		String[] parameters = {"minBalance","altBalance"};

		trfCntrlProf.modifyProductValuesInTCP(EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.TCPID, parameters,values , EXTGW_FIXLINERCDP.ProductName, true);

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
		trfCntrlProf.modifyProductValuesInTCP(EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.TCPID, parameters,values , EXTGW_FIXLINERCDP.ProductName, true);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_FixLineRC21_Negative__C2SMinAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC21");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_FIXLINERCDP.getAPIdata();
		apiData.put(EXTGW_FIXLINERCAPI.AMOUNT, "90");
		String API = EXTGW_FIXLINERCAPI.prepareAPI(apiData);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SminimumAmt(EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.TCPID, "100","100", EXTGW_FIXLINERCDP.ProductName);

		String[] APIResponse = null;
		try{
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}
		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Gift Recharge is not successful with  error message" );

		}

		ExtentI.Markup(ExtentColor.TEAL, "Updating C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SminimumAmt(EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.TCPID,_masterVO.getProperty("MinimumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), EXTGW_FIXLINERCDP.ProductName);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_FixLineRC22_C2STaxAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC22");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");
		String actual = CommissionProfile.getCommissionSlabCount(EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade);
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


		long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName,service,_masterVO.getProperty("FixLineRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_FIXLINERCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.FIXLINE_TRANSACTION_ID);



		for(int j=0;j<AddSlabCount;j++){
			if(j==0){
				slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
				slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));
			}
			else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
			slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
			}}

		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap,EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName,service,_masterVO.getProperty("FixLineRechargeCode"));
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
	public void TC_FixLineRC23_C2STaxAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC23");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);

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



		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName, service,_masterVO.getProperty("FixLineRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_FIXLINERCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.FIXLINE_TRANSACTION_ID);


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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap,EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName,service,_masterVO.getProperty("FixLineRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}







	@Test
	public void TC_FixLineRC24_C2STimeSlabAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC24");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);

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



		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_TimeSlab_ParticularService(slabMap,EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName,service,_masterVO.getProperty("FixLineRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_FIXLINERCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.FIXLINE_TRANSACTION_ID);



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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName,service,_masterVO.getProperty("FixLineRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}






	@Test
	public void TC_FixLineRC25_C2SMinTransferValidation()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC25");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);

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

		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName,service,_masterVO.getProperty("FixLineRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_FIXLINERCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.FIXLINE_TRANSACTION_ID);
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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName,service,_masterVO.getProperty("FixLineRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}




	@Test
	public void TC_FixLineRC26_SlabToRangeValidation()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC26");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);

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


		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName,service,_masterVO.getProperty("FixLineRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_FIXLINERCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.FIXLINE_TRANSACTION_ID);



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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.grade, EXTGW_FIXLINERCDP.CPName,service,_masterVO.getProperty("FixLineRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}
	
	
	

	
	
	
	
	@Test
	public void TC_FixlineRC27_PositiveLoginPassword() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC27");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

		dataMap.put(EXTGW_FIXLINERCAPI.MSISDN, "");
		dataMap.put(EXTGW_FIXLINERCAPI.PIN, "");
		dataMap.put(EXTGW_FIXLINERCAPI.EXTCODE, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}

	
	
	@Test
	public void TC_FixlineRC28_PositiveMSISDNPIN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC28");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

		dataMap.put(EXTGW_FIXLINERCAPI.LOGINID, "");
		dataMap.put(EXTGW_FIXLINERCAPI.PASSWORD, "");
		dataMap.put(EXTGW_FIXLINERCAPI.EXTCODE, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	@Test
	public void TC_RC29_PositiveEXTCODE() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC29");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGW_FIXLINERCAPI.LOGINID, "");
		dataMap.put(EXTGW_FIXLINERCAPI.PASSWORD, "");
		dataMap.put(EXTGW_FIXLINERCAPI.MSISDN, "");
		dataMap.put(EXTGW_FIXLINERCAPI.PIN, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	

	
	
	@Test
	public void TC_RC30_NegativeMRPBlockTime() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC30");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		C2STransfer c2STransfer = new C2STransfer(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String msisdn = dataMap.get(EXTGW_FIXLINERCAPI.MSISDN2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		
		c2STransfer.modifyMRPPreference("true",true,"120");
		BigDecimal minutes = new BigDecimal(120).divide(new BigDecimal(60),2, RoundingMode.HALF_UP);
		
		// Perform Transaction to the same Subscriber
		 
		dataMap.put(EXTGW_FIXLINERCAPI.MSISDN2,msisdn);
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse1);
		XmlPath xmlPath1 = new XmlPath(CompatibilityMode.HTML, APIResponse1[1]);
		
		Validator.messageCompare(xmlPath1.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	


	/*
	
	@Test
	public void TC_RC30_NegativeBarredChannelUSer() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC30");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		BarUnbar BarUnbar = new BarUnbar(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		
		
		
		BarUnbar.barringUser("C2S", "SENDER", dataMap.get(EXTGW_FIXLINERCAPI.MSISDN));
		String actual= new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected= MessagesDAO.prepareMessageByKey("subscriber.barreduser.add.mobile.success",dataMap.get(EXTGW_FIXLINERCAPI.MSISDN));
		if(actual.equalsIgnoreCase(expected)){
			ExtentI.Markup(ExtentColor.TEAL, "Channel user is barred");
		}
		// Perform Transaction to the same Subscriber
		 
		
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String msisdn = dataMap.get(EXTGW_FIXLINERCAPI.MSISDN2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	
	
	*/
	

	
	
	@Test
	public void TC_RC31_NegativeBarredChannelUSer() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC31");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		BarUnbar BarUnbar = new BarUnbar(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
		BarUnbar.barringUser("C2S","SENDER",dataMap.get(EXTGW_FIXLINERCAPI.MSISDN));
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String msisdn = dataMap.get(EXTGW_FIXLINERCAPI.MSISDN2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		ExtentI.Markup(ExtentColor.TEAL, "UnBarring Channel User");
		BarUnbar.unBarringUser("C2S", "SENDER", dataMap.get(EXTGW_FIXLINERCAPI.MSISDN));
	}	
	
	
	

	
	
	
	@Test
	public void TC_RC32_NegativeBarredSubscriber() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC32");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		BarUnbar BarUnbar = new BarUnbar(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);


		ExtentI.Markup(ExtentColor.TEAL, "Barring Subscriber");
		BarUnbar.barringUser("C2S", "RECEIVER", dataMap.get(EXTGW_FIXLINERCAPI.MSISDN));
		// Perform Transaction to the same Subscriber
		 
		
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String msisdn = dataMap.get(EXTGW_FIXLINERCAPI.MSISDN2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		ExtentI.Markup(ExtentColor.TEAL, "UnBarring Subscriber");
		BarUnbar.unBarringUser("C2S", "RECEIVER", dataMap.get(EXTGW_FIXLINERCAPI.MSISDN));
	}	
	
	
	

	
	
	@Test
	public void TC_RC33_NegativeBarredChannelUSer() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC33");
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();
		BarUnbar BarUnbar = new BarUnbar(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_FIXLINERCDP.getAPIdata();

	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);


		ExtentI.Markup(ExtentColor.TEAL, "Barring Channel user");
		BarUnbar.barringUser("C2S", "RECEIVER", dataMap.get(EXTGW_FIXLINERCAPI.MSISDN2));
		// Perform Transaction to the same Subscriber
		 
		
		String API = EXTGW_FIXLINERCAPI.prepareAPI(dataMap);
		String msisdn = dataMap.get(EXTGW_FIXLINERCAPI.MSISDN2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		ExtentI.Markup(ExtentColor.TEAL, "UnBarring Channel user");
		BarUnbar.unBarringUser("C2S", "RECEIVER", dataMap.get(EXTGW_FIXLINERCAPI.MSISDN2));
	}	
	
	
	
	
	
	
	@Test
	public void TC_RC34_Negative__C2SMaxAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWFIXLINERC34");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGW_FIXLINERCAPI EXTGW_FIXLINERCAPI = new EXTGW_FIXLINERCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_FIXLINERCDP.getAPIdata();
		apiData.put(EXTGW_FIXLINERCAPI.AMOUNT, "90");
		String API = EXTGW_FIXLINERCAPI.prepareAPI(apiData);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SmaximumAmt(EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.TCPID, "80","80", EXTGW_FIXLINERCDP.ProductName);

		String[] APIResponse = null;
		try{
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}
		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Fix line Recharge is not successful with  error message" );

		}

		ExtentI.Markup(ExtentColor.TEAL, "Updating C2S Max Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SmaximumAmt(EXTGW_FIXLINERCDP.Domain, EXTGW_FIXLINERCDP.CUCategory, EXTGW_FIXLINERCDP.TCPID,_masterVO.getProperty("MaximumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), EXTGW_FIXLINERCDP.ProductName);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_FIXLINERCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	
	
	
	
	


}
