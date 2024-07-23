package com.apicontrollers.extgw.c2sTransfer.EXTGW_InternetRC;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.Features.*;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import org.testng.annotations.Test;

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

public class EXTGW_INTERNETRC extends BaseTest{


	public static boolean TestCaseCounter = false;
	private final String extentCategory = "API";
	String service = null;
	String serviceClass = null;


	@Test
	public void TC_InternetRC01_Positive() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC01");
		EXTGW_INTERNETRCAPI EXTGWIntRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		Object[] dataObject = EXTGW_INTERNETRCDP.getAPIdataWithAllUsers();

		for (int i = 0; i < dataObject.length; i++) {

			EXTGW_INTERNETRCDAO APIDAO = (EXTGW_INTERNETRCDAO) dataObject[i];
			HashMap<String, String> apiData = APIDAO.getApiData();
			_parser parser = new _parser();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), APIDAO.getCategory()));
			currentNode.assignCategory(extentCategory);

			String API = EXTGWIntRCAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);


			String PostBalance = DBHandler.AccessHandler.get_post_balance(xmlPath.get(EXTGW_INTERNETRCAPI.TXNID).toString());
			Log.info("User Post Balance in database after transaction is : " +  PostBalance);
			parser.convertStringToLong(PostBalance).changeDenomation();

			long postBal = (long)(parser.getValue());
			
			/*String balance = null;_parser.getPostBalance(xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE).toString());
			System.out.println(balance);
			
			parser.convertStringToLong(balance).changeDenomation();
			long newBalance = (long)(parser.getValue());
			
			if(newBalance==postBal){
				currentNode.pass("Balance updated correctly in Database");
			}
			else{
				currentNode.fail("Test Case is fail as the incorrect PostBalance is updated in database as : " +PostBalance);
			}
*/

			String txnStatus = DBHandler.AccessHandler.fetchTransferStatus(xmlPath.get(EXTGW_INTERNETRCAPI.TXNID).toString());
			if(txnStatus.equalsIgnoreCase(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString())){
				ExtentI.Markup(ExtentColor.GREEN, "Transaction status updated correctly in Database");
			}
			else{
				ExtentI.Markup(ExtentColor.RED, "The Transaction status as updated in database is : " +txnStatus);
			}
			Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
	}




	@Test
	public void TC_InternetRC02_NegativeInternetRCAPI_BlankPin() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC02");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGWINTRCAPI.PIN, "");
		dataMap.put(EXTGWINTRCAPI.LOGINID, "");
		dataMap.put(EXTGWINTRCAPI.PASSWORD, "");
		dataMap.put(EXTGWINTRCAPI.EXTCODE, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String PinRequired = DBHandler.AccessHandler.check_PIN_REQUIRED(PretupsI.PIN_REQUIRED);

		if(PinRequired.equalsIgnoreCase("false")){
			currentNode.skip("PIN is not mandatory for the transaction,Hence test case skipped");
		}
		else {
			String API = EXTGWINTRCAPI.prepareAPI(dataMap);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		}
		
	}


	@Test
	public void TC_InternetRC03_NegativeInternetRCAPI() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC03");
		EXTGW_INTERNETRCAPI EXTGWIntRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		dataMap.put(EXTGWIntRCAPI.EXTNWCODE, "");
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWIntRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_InternetRC04_PositiveInternetRCAPI_withBlankSenderMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC04");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		dataMap.put(EXTGWINTRCAPI.MSISDN, "");
		dataMap.put(EXTGWINTRCAPI.PIN,"");
		dataMap.put(EXTGWINTRCAPI.LOGINID, "");
		dataMap.put(EXTGWINTRCAPI.EXTCODE, "");


		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_InternetRC05_Negative_BlankSubMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC05");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		dataMap.put(EXTGWINTRCAPI.MSISDN2, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	





	@Test
	public void TC_InternetRC06_NegativeInternetRCAPI_InvalidSubMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC06");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		String MSISDN2 = _masterVO.getMasterValue(MasterI.SUBSCRIBER_POSTPAID_PREFIX) +RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
		dataMap.put(EXTGWINTRCAPI.MSISDN2, MSISDN2);

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	





	@Test
	public void TC_InternetRC07_NegativeInternetRCAPI_InvalidLanguageCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC07");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGWINTRCAPI.LANGUAGE2,RandomGeneration.randomNumeric(3));
		dataMap.put(EXTGWINTRCAPI.LANGUAGE1,RandomGeneration.randomNumeric(3));

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_InternetRC08_NegativeInternetRCAPI_InvalidChannelMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC08");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();
		GenerateMSISDN gnMsisdn = new GenerateMSISDN();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();



		String MSISDN = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) +RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
		dataMap.put(EXTGWINTRCAPI.MSISDN, MSISDN);
		dataMap.put(EXTGWINTRCAPI.LOGINID, "");
		dataMap.put(EXTGWINTRCAPI.PASSWORD, "");
		dataMap.put(EXTGWINTRCAPI.EXTCODE,"");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	



	@Test
	public void TC_InternetRC09_NegativeInternetRCAPI_InvalidPin() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC09");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();


		String CorrectPin = dataMap.get(EXTGWINTRCAPI.PIN);

		String InValPin;

		do
		{
			InValPin = RandomGeneration.randomNumeric(4);

		}while(CorrectPin==InValPin);

		dataMap.put(EXTGWINTRCAPI.PIN,InValPin);	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.partialmessageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_InternetRC10_NegativeInternetRCAPI_BlankAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC10");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGWINTRCAPI.AMOUNT,"");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_InternetRC11_NegativeInternetRCAPI_NegAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC11");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGWINTRCAPI.AMOUNT,"-1");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_InternetRC12_NegativeInternetRCAPI_IncorrectSelectorCode() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC12");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		RandomGeneration RandomGeneration = new RandomGeneration();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGWINTRCAPI.SELECTOR,RandomGeneration.randomAlphaNumeric(5));	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






	@Test
	public void TC_InternetRC13_NegativeInternetRCAPI_InvalidNotificationMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC13");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGWINTRCAPI.NOTIFICATION_MSISDN,"abc");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_InternetRC14_PositiveInternetRCAPI_blankNotificationMSISDN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC14");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();


		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);


		dataMap.put(EXTGWINTRCAPI.NOTIFICATION_MSISDN,"");


		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_InternetRC15_PositiveInternetRCAPI_DecimalAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC15");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGWINTRCAPI.AMOUNT,"95.5");	

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_InternetRC16_suspendAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC16");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		String Transfer_ID = null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Suspend Additional Commission Profile slab");

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)
		{			
			if((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(_masterVO.getProperty("InternetRechargeCode"))))
				break;
		}
		service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		Log.info("service is:" +service);

		long time2 = CommissionProfile.suspendAdditionalCommProfileForGivenService(EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName , service,_masterVO.getProperty("InternetRechargeCode"));

		Thread.sleep(time2);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.INTERNET_TRANSACTION_ID);


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
	public void TC_InternetRC17_resumeAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC17");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID1= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		ExtentI.Markup(ExtentColor.TEAL, "Resume Additional Commission Profile slab");

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)
		{			
			if((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(_masterVO.getProperty("InternetRechargeCode"))))
				break;
		}
		String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
		Log.info("service is:" +service);

		long time = CommissionProfile.resumeAdditionalCommProfileForGivenService(EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName , service,_masterVO.getProperty("InternetRechargeCode"));
		Log.info("Wait for Commission Profile Version to be active");
		Thread.sleep(time);

		ExtentI.Markup(ExtentColor.TEAL, "Perform C2S transaction after Resuming Additional Commission Profile slab");


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE);
		Transfer_ID1 = _parser.getTransactionID(Message, PretupsI.INTERNET_TRANSACTION_ID);
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
	public void TC_InternetRC18_NegativeSuspendTCP() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC18");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		TransferControlProfile TCPObj = new TransferControlProfile(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_INTERNETRCDP.getAPIdata();
		String API = EXTGWINTRCAPI.prepareAPI(apiData);

		TCPObj.channelLevelTransferControlProfileSuspend(0, EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.TCPName, null);

		String[] APIResponse = null;

		try{
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}
		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Recharge is not successful with  error message" );

		}

		TCPObj.channelLevelTransferControlProfileActive(0, EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.TCPName, null);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

	}


	@Test
	public void TC_InternetRC19_Negative_SenderOutSuspended() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC19");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_INTERNETRCDP.getAPIdata();

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(EXTGWINTRCAPI.MSISDN));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGW_INTERNETRCDP.CUCategory, channelMap);

		String[] APIResponse = null;
		try{
			String API = EXTGWINTRCAPI.prepareAPI(apiData);
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}

		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Recharge is not successful with  error message" );

		}

		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGW_INTERNETRCDP.CUCategory, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}




	@Test
	public void TC_InternetRC20_Negative__MinResidualBalance() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC20");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		_parser parser = new _parser();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_INTERNETRCDP.getAPIdata();

		String API = EXTGWINTRCAPI.prepareAPI(apiData);


		String balance = DBHandler.AccessHandler.getUserBalance(EXTGW_INTERNETRCDP.ProductCode, EXTGW_INTERNETRCDP.LoginID);
		parser.convertStringToLong(balance).changeDenomation();

		System.out.println("The balance is:" +balance);
		long usrBalance = (long) (parser.getValue()) - 100 + 2;
		System.out.println(usrBalance);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying Minimum Residual Balance in Transfer Control Profile");


		String[] values = {String.valueOf(usrBalance),String.valueOf(usrBalance)};
		String[] parameters = {"minBalance","altBalance"};

		trfCntrlProf.modifyProductValuesInTCP(EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.TCPID, parameters,values , EXTGW_INTERNETRCDP.ProductName, true);

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
		trfCntrlProf.modifyProductValuesInTCP(EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.TCPID, parameters,values , EXTGW_INTERNETRCDP.ProductName, true);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}


	@Test
	public void TC_InternetRC21_Negative__C2SMinAmount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC21");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_INTERNETRCDP.getAPIdata();
		apiData.put(EXTGWINTRCAPI.AMOUNT, "90");
		String API = EXTGWINTRCAPI.prepareAPI(apiData);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SminimumAmt(EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.TCPID, "100","100", EXTGW_INTERNETRCDP.ProductName);

		String[] APIResponse = null;
		try{
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}
		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Gift Recharge is not successful with  error message" );

		}

		ExtentI.Markup(ExtentColor.TEAL, "Updating C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SminimumAmt(EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.TCPID,_masterVO.getProperty("MinimumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), EXTGW_INTERNETRCDP.ProductName);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_InternetRC22_C2STaxAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC22");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");
		String actual = CommissionProfile.getCommissionSlabCount(EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade);
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


		long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName,service,_masterVO.getProperty("InternetRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.INTERNET_TRANSACTION_ID);



		for(int j=0;j<AddSlabCount;j++){
			if(j==0){
				slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))));
				slabMap.put("AddSend"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+(j+1)))));
			}
			else {slabMap.put("AddStart"+j, String.valueOf(Integer.parseInt(AddCommMap.get("B"+j))+1));
			slabMap.put("AddSend"+j, AddCommMap.get("B"+(j+1)));	
			}}

		ExtentI.Markup(ExtentColor.TEAL, "Reverting changed values Additional Commission Profile slab");
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap,EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName,service,_masterVO.getProperty("InternetRechargeCode"));
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
	public void TC_InternetRC23_C2STaxAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC23");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);

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



		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);

		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.INTERNET_TRANSACTION_ID);


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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap,EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName,service,_masterVO.getProperty("InternetRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}







	@Test
	public void TC_InternetRC24_C2STimeSlabAdditionalCommProfile()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC24");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);

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



		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_TimeSlab_ParticularService(slabMap,EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName,service,_masterVO.getProperty("InternetRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.INTERNET_TRANSACTION_ID);



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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName,service,_masterVO.getProperty("InternetRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}






	@Test
	public void TC_InternetRC25_C2SMinTransferValidation()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC25");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);

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

		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName,service,_masterVO.getProperty("InternetRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.INTERNET_TRANSACTION_ID);
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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName,service,_masterVO.getProperty("InternetRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}




	@Test
	public void TC_InternetRC26_SlabToRangeValidation()
			throws InterruptedException, Throwable {


		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC26");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();
		Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
		CommissionProfile CommissionProfile = new CommissionProfile(driver);



		String Transfer_ID= null;
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		String API = EXTGWINTRCAPI.prepareAPI(dataMap);

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


		long time2 =CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName,service,_masterVO.getProperty("InternetRechargeCode"));

		ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
		Thread.sleep(time2);


		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Log.info("The Txn status is " + xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString());


		String Message = xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE);
		Transfer_ID = _parser.getTransactionID(Message, PretupsI.INTERNET_TRANSACTION_ID);



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
		CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap,EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.grade, EXTGW_INTERNETRCDP.CPName,service,_masterVO.getProperty("InternetRechargeCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

	}



	@Test
	public void TC_InternetRC27_PositiveLoginPassword() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC27");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGWINTRCAPI.MSISDN, "");
		dataMap.put(EXTGWINTRCAPI.PIN, "");
		dataMap.put(EXTGWINTRCAPI.EXTCODE, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_InternetRC28_PositiveMSISDNPIN() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC28");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGWINTRCAPI.LOGINID, "");
		dataMap.put(EXTGWINTRCAPI.PASSWORD, "");
		dataMap.put(EXTGWINTRCAPI.EXTCODE, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}



	@Test
	public void TC_InternetRC29_PositiveEXTCODE() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC29");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();

		dataMap.put(EXTGWINTRCAPI.LOGINID, "");
		dataMap.put(EXTGWINTRCAPI.PASSWORD, "");
		dataMap.put(EXTGWINTRCAPI.MSISDN, "");
		dataMap.put(EXTGWINTRCAPI.PIN, "");

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}





	@Test
	public void TC_InternetRC30_NegativeMRPBlockTime() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC30");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		C2STransfer c2STransfer = new C2STransfer(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();



		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);

		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String msisdn = dataMap.get(EXTGWINTRCAPI.MSISDN2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

		c2STransfer.modifyMRPPreference("true",true,"120");
		BigDecimal minutes = new BigDecimal(120).divide(new BigDecimal(60),2, RoundingMode.HALF_UP);

		// Perform Transaction to the same Subscriber

		dataMap.put(EXTGWINTRCAPI.MSISDN2,msisdn);
		String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse1);
		XmlPath xmlPath1 = new XmlPath(CompatibilityMode.HTML, APIResponse1[1]);

		Validator.messageCompare(xmlPath1.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	



/*
	@Test
	public void TC_InternetRC30_NegativeBarredChannelUSer() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC30");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		BarUnbar BarUnbar = new BarUnbar(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();



		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);



		BarUnbar.barringUser(PretupsI.C2S_MODULE, PretupsI.BARRING_SENDER_TYPE, dataMap.get(EXTGWINTRCAPI.MSISDN));
		String actual= new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected= MessagesDAO.prepareMessageByKey("subscriber.barreduser.add.mobile.success",dataMap.get(EXTGWINTRCAPI.MSISDN));
		if(actual.equalsIgnoreCase(expected)){
			ExtentI.Markup(ExtentColor.TEAL, "Channel user is barred");
		}
		// Perform Transaction to the same Subscriber


		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String msisdn = dataMap.get(EXTGWINTRCAPI.MSISDN2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	






	@Test
	public void TC_InternetRC31_NegativeBarredChannelUSer() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC31");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		BarUnbar BarUnbar = new BarUnbar(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();



		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);



		BarUnbar.unBarringUser(PretupsI.C2S_MODULE, PretupsI.BARRING_SENDER_TYPE, dataMap.get(EXTGWINTRCAPI.MSISDN));
		String actual= new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected= MessagesDAO.prepareMessageByKey("subscriber.unbaruser.add.mobile.success",dataMap.get(EXTGWINTRCAPI.MSISDN));
		if(actual.equalsIgnoreCase(expected)){
			ExtentI.Markup(ExtentColor.TEAL, "Channel user is un-barred");
		}
		// Perform Transaction to the same Subscriber


		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String msisdn = dataMap.get(EXTGWINTRCAPI.MSISDN2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	







	@Test
	public void TC_InternetRC32_NegativeBarredSubscriber() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC32");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		BarUnbar BarUnbar = new BarUnbar(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();



		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);



		BarUnbar.barringUser(PretupsI.C2S_MODULE, PretupsI.BARRING_RECEIVER_TYPE, dataMap.get(EXTGWINTRCAPI.MSISDN2));
		String actual= new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected= MessagesDAO.prepareMessageByKey("subscriber.barreduser.add.mobile.success",dataMap.get(EXTGWINTRCAPI.MSISDN));
		if(actual.equalsIgnoreCase(expected)){
			ExtentI.Markup(ExtentColor.TEAL, "Subscriber user is barred");
		}
		// Perform Transaction to the same Subscriber


		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String msisdn = dataMap.get(EXTGWINTRCAPI.MSISDN2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	






	@Test
	public void TC_InternetRC33_NegativeBarredChannelUSer() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC33");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		BarUnbar BarUnbar = new BarUnbar(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> dataMap = EXTGW_INTERNETRCDP.getAPIdata();



		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);



		BarUnbar.unBarringUser(PretupsI.C2S_MODULE, PretupsI.BARRING_RECEIVER_TYPE, dataMap.get(EXTGWINTRCAPI.MSISDN2));
		String actual= new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected= MessagesDAO.prepareMessageByKey("subscriber.unbaruser.add.mobile.success",dataMap.get(EXTGWINTRCAPI.MSISDN));
		if(actual.equalsIgnoreCase(expected)){
			ExtentI.Markup(ExtentColor.TEAL, "Subscriber user is un-barred");
		}
		// Perform Transaction to the same Subscriber


		String API = EXTGWINTRCAPI.prepareAPI(dataMap);
		String msisdn = dataMap.get(EXTGWINTRCAPI.MSISDN2);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}	

*/




	@Test
	public void TC_InternetRC34_Negative__DailyCount() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC34");
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();


		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);			
		HashMap<String, String> apiData = EXTGW_INTERNETRCDP.getAPIdata();
		apiData.put(EXTGWINTRCAPI.AMOUNT, "90");
		String API = EXTGWINTRCAPI.prepareAPI(apiData);

		ExtentI.Markup(ExtentColor.TEAL, "Modifying C2S Min Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SmaximumAmt(EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.TCPID, "80","80", EXTGW_INTERNETRCDP.ProductName);

		String[] APIResponse = null;
		try{
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}
		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "C2S Recharge is not successful with  error message" );

		}

		ExtentI.Markup(ExtentColor.TEAL, "Updating C2S Max Amount in Transfer Control Profile");
		trfCntrlProf.modifyTCPPerC2SmaximumAmt(EXTGW_INTERNETRCDP.Domain, EXTGW_INTERNETRCDP.CUCategory, EXTGW_INTERNETRCDP.TCPID,_masterVO.getProperty("MaximumBalance"),_masterVO.getProperty("AllowedMaxPercentage"), EXTGW_INTERNETRCDP.ProductName);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}
	@Test
	public void TC_InternetRC35_BarredSender() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC35");
		EXTGW_INTERNETRCAPI EXTGWIntRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

			HashMap<String, String> apiData = EXTGW_INTERNETRCDP.getAPIdata();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			BarUnbar BarUnbar = new BarUnbar(driver);
			ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
			BarUnbar.barringUser("C2S","SENDER",apiData.get(EXTGWIntRCAPI.MSISDN));

			String API = EXTGWIntRCAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);


			/*String balance = null;_parser.getPostBalance(xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE).toString());
			System.out.println(balance);

			parser.convertStringToLong(balance).changeDenomation();
			long newBalance = (long)(parser.getValue());

			if(newBalance==postBal){
				currentNode.pass("Balance updated correctly in Database");
			}
			else{
				currentNode.fail("Test Case is fail as the incorrect PostBalance is updated in database as : " +PostBalance);
			}
*/

			Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
			BarUnbar.unBarringUser("C2S","SENDER",apiData.get(EXTGWIntRCAPI.MSISDN));
		}

	@Test
	public void TC_InternetRC36_BarredReceiver() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC36");
		EXTGW_INTERNETRCAPI EXTGWIntRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> apiData = EXTGW_INTERNETRCDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		BarUnbar BarUnbar = new BarUnbar(driver);
		ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
		BarUnbar.barringUser("C2S","RECEIVER",apiData.get(EXTGWIntRCAPI.MSISDN));

		String API = EXTGWIntRCAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);


			/*String balance = null;_parser.getPostBalance(xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE).toString());
			System.out.println(balance);

			parser.convertStringToLong(balance).changeDenomation();
			long newBalance = (long)(parser.getValue());

			if(newBalance==postBal){
				currentNode.pass("Balance updated correctly in Database");
			}
			else{
				currentNode.fail("Test Case is fail as the incorrect PostBalance is updated in database as : " +PostBalance);
			}
*/

		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
		BarUnbar.unBarringUser("C2S","RECEIVER",apiData.get(EXTGWIntRCAPI.MSISDN));
	}

	@Test
	public void TC_InternetRC37_BarredSubscriberReceiver() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC37");
		EXTGW_INTERNETRCAPI EXTGWIntRCAPI = new EXTGW_INTERNETRCAPI();

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		HashMap<String, String> apiData = EXTGW_INTERNETRCDP.getAPIdata();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		BarUnbar BarUnbar = new BarUnbar(driver);
		ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
		BarUnbar.barringUser("C2S","RECEIVER",apiData.get(EXTGWIntRCAPI.MSISDN2));

		String API = EXTGWIntRCAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);


			/*String balance = null;_parser.getPostBalance(xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE).toString());
			System.out.println(balance);

			parser.convertStringToLong(balance).changeDenomation();
			long newBalance = (long)(parser.getValue());

			if(newBalance==postBal){
				currentNode.pass("Balance updated correctly in Database");
			}
			else{
				currentNode.fail("Test Case is fail as the incorrect PostBalance is updated in database as : " +PostBalance);
			}
*/

		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
		BarUnbar.unBarringUser("C2S","RECEIVER",apiData.get(EXTGWIntRCAPI.MSISDN2));
	}
	@Test
	public void TC_InternetRC38_NegativeSuspended() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC38");
		EXTGW_INTERNETRCAPI EXTGWIntRCAPI = new EXTGW_INTERNETRCAPI();
		SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
		ResumeChannelUser CUResume = new ResumeChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

			HashMap<String, String> apiData = EXTGW_INTERNETRCDP.getAPIdata();
			_parser parser = new _parser();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory(extentCategory);
			CUSuspend.suspendChannelUser_MSISDN(apiData.get(EXTGWIntRCAPI.MSISDN), "Suspending channel user");
			CUSuspend.approveCSuspendRequest_MSISDN(apiData.get(EXTGWIntRCAPI.MSISDN), "Approval for suspending channel user");


			String API = EXTGWIntRCAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);


			/*String balance = null;_parser.getPostBalance(xmlPath.get(EXTGW_INTERNETRCAPI.MESSAGE).toString());
			System.out.println(balance);

			parser.convertStringToLong(balance).changeDenomation();
			long newBalance = (long)(parser.getValue());

			if(newBalance==postBal){
				currentNode.pass("Balance updated correctly in Database");
			}
			else{
				currentNode.fail("Test Case is fail as the incorrect PostBalance is updated in database as : " +PostBalance);
			}
*/

			Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			CUResume.resumeChannelUser_MSISDN(apiData.get(EXTGWIntRCAPI.MSISDN), "Resuming channel user");
		}


	@Test
	public void TC_InternetRC39_NegativeINSuspended() throws Exception {

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWINTRC39");
		EXTGW_INTERNETRCAPI EXTGWINTRCAPI = new EXTGW_INTERNETRCAPI();
		ChannelUser ChannelUser = new ChannelUser(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = EXTGW_INTERNETRCDP.getAPIdata();

		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(EXTGWINTRCAPI.MSISDN));
		channelMap.put("inSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(EXTGW_INTERNETRCDP.CUCategory, channelMap);

		String[] APIResponse = null;
		try{
			String API = EXTGWINTRCAPI.prepareAPI(apiData);
			APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		}

		catch(Exception e){

			ExtentI.Markup(ExtentColor.RED, "Internet Recharge is not successful with  error message" );

		}

		channelMap.put("inSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(EXTGW_INTERNETRCDP.CUCategory, channelMap);

		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EXTGW_INTERNETRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	}






}
