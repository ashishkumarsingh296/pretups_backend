package com.apicontrollers.ussd.internetRecharge;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.Map_CommissionProfile;
import com.Features.TransferControlProfile;
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

public class USSDPlain_INTERNETRC extends BaseTest {


    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";
    String service = null;
    String serviceClass = null;


    @Test
    public void TC_InternetRC01_Positive() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC01");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = USSDPlain_INTERNETRCDP.getAPIdataWithAllUsers();

        for (int i = 0; i < dataObject.length; i++) {

            USSDPlain_INTERNETRCDAO APIDAO = (USSDPlain_INTERNETRCDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);

            String API = USSDINTRCAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
        }
    }


    @Test
    public void TC_InternetRC02_NegativeInternetRCAPI_BlankPin() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC02");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        dataMap.put(USSDINTRCAPI.PIN, "");
        dataMap.put(USSDINTRCAPI.LOGINID, "");
        dataMap.put(USSDINTRCAPI.PASSWORD, "");
        dataMap.put(USSDINTRCAPI.EXTCODE, "");

        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

//Case commented because EXTNWCODE does not exist in InternetRCAPI
  //  @Test
    public void TC_InternetRC03_NegativeInternetRCAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC03");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        dataMap.put(USSDINTRCAPI.EXTNWCODE, "");
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC04_PositiveInternetRCAPI_withBlankSenderMSISDN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC04");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        dataMap.put(USSDINTRCAPI.MSISDN1, "");
        dataMap.put(USSDINTRCAPI.PIN, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC05_Negative_BlankSubMSISDN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC05");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        dataMap.put(USSDINTRCAPI.MSISDN2, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC06_NegativeInternetRCAPI_InvalidSubMSISDN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC06");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();
        GenerateMSISDN gnMsisdn = new GenerateMSISDN();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        String MSISDN2 = _masterVO.getMasterValue(MasterI.SUBSCRIBER_POSTPAID_PREFIX) + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
        dataMap.put(USSDINTRCAPI.MSISDN2, MSISDN2);

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC07_NegativeInternetRCAPI_InvalidLanguageCode() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC07");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();

        dataMap.put(USSDINTRCAPI.LANGUAGE2, RandomGeneration.randomNumeric(3));
        dataMap.put(USSDINTRCAPI.LANGUAGE1, RandomGeneration.randomNumeric(3));

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC08_NegativeInternetRCAPI_InvalidChannelMSISDN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC08");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();
        GenerateMSISDN gnMsisdn = new GenerateMSISDN();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();


        String MSISDN = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX) + RandomGeneration.randomNumeric(gnMsisdn.generateMSISDN());
        dataMap.put(USSDINTRCAPI.MSISDN1, MSISDN);
        dataMap.put(USSDINTRCAPI.LOGINID, "");
        dataMap.put(USSDINTRCAPI.EXTCODE, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC09_NegativeInternetRCAPI_InvalidPin() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC09");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();


        String CorrectPin = dataMap.get(USSDINTRCAPI.PIN);

        String InValPin;

        do {
            InValPin = RandomGeneration.randomNumeric(4);

        } while (CorrectPin == InValPin);

        dataMap.put(USSDINTRCAPI.PIN, InValPin);

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC10_NegativeInternetRCAPI_BlankAmount() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC10");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();

        dataMap.put(USSDINTRCAPI.AMOUNT, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC11_NegativeInternetRCAPI_NegAmount() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC11");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();

        dataMap.put(USSDINTRCAPI.AMOUNT, "-1");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC12_NegativeInternetRCAPI_IncorrectSelectorCode() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC12");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();

        dataMap.put(USSDINTRCAPI.SELECTOR, RandomGeneration.randomAlphaNumeric(5));

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC13_NegativeInternetRCAPI_InvalidNotificationMSISDN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC13");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();

        dataMap.put(USSDINTRCAPI.NOTIFICATION_MSISDN, "abc");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC14_PositiveInternetRCAPI_blankNotificationMSISDN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC14");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();


        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);


        dataMap.put(USSDINTRCAPI.NOTIFICATION_MSISDN, "");


        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC15_PositiveInternetRCAPI_DecimalAmount() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC15");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();

        dataMap.put(USSDINTRCAPI.AMOUNT, "95.5");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC16_suspendAdditionalCommProfile()
            throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC16");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        CommissionProfile CommissionProfile = new CommissionProfile(driver);
        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);

        ExtentI.Markup(ExtentColor.TEAL, "Suspend Additional Commission Profile slab");

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow1 = ExcelUtility.getRowCount();

        int i = 1;
        for (i = 1; i <= totalRow1; i++) {
            if ((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(_masterVO.getProperty("CustomerRechargeCode"))))
                break;
        }
        service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
        Log.info("service is:" + service);

        long time2 = CommissionProfile.suspendAdditionalCommProfileForGivenService(USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));

        Thread.sleep(time2);

        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPlain_INTERNETRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPlain_INTERNETRCAPI.TXNID);


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
    public void TC_InternetRC17_resumeAdditionalCommProfile()
            throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC17");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        CommissionProfile CommissionProfile = new CommissionProfile(driver);

        String Transfer_ID1 = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);
        ExtentI.Markup(ExtentColor.TEAL, "Resume Additional Commission Profile slab");

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
        int totalRow1 = ExcelUtility.getRowCount();

        int i = 1;
        for (i = 1; i <= totalRow1; i++) {
            if ((ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).matches(_masterVO.getProperty("CustomerRechargeCode"))))
                break;
        }
        String service = ExcelUtility.getCellData(0, ExcelI.NAME, i);
        Log.info("service is:" + service);

        long time = CommissionProfile.resumeAdditionalCommProfileForGivenService(USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));
        Log.info("Wait for Commission Profile Version to be active");
        Thread.sleep(time);

        ExtentI.Markup(ExtentColor.TEAL, "Perform C2S transaction after Resuming Additional Commission Profile slab");


        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPlain_INTERNETRCAPI.TXNSTATUS).toString());


        Transfer_ID1 = xmlPath.get(USSDPlain_INTERNETRCAPI.TXNID);
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
    public void TC_InternetRC18_NegativeSuspendTCP() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC18");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        TransferControlProfile TCPObj = new TransferControlProfile(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_INTERNETRCDP.getAPIdata();
        String API = USSDINTRCAPI.prepareAPI(apiData);

        TCPObj.channelLevelTransferControlProfileSuspend(0, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.TCPName, null);

        String[] APIResponse = null;

        try {
            APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        } catch (Exception e) {

            ExtentI.Markup(ExtentColor.RED, "Recharge is not successful with  error message");

        }

        TCPObj.channelLevelTransferControlProfileActive(0, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.TCPName, null);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }


    @Test
    public void TC_InternetRC19_Negative_SenderOutSuspended() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC19");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();
        ChannelUser ChannelUser = new ChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_INTERNETRCDP.getAPIdata();

        HashMap<String, String> channelMap = new HashMap<String, String>();
        channelMap.put("searchMSISDN", apiData.get(USSDINTRCAPI.MSISDN1));
        channelMap.put("outSuspend_chk", "Y");
        ChannelUser.modifyChannelUserDetails(USSDPlain_INTERNETRCDP.CUCategory, channelMap);

        String[] APIResponse = null;
        try {
            String API = USSDINTRCAPI.prepareAPI(apiData);
            APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        } catch (Exception e) {

            ExtentI.Markup(ExtentColor.RED, "Recharge is not successful with  error message");

        }

        channelMap.put("outSuspend_chk", "N");
        ChannelUser.modifyChannelUserDetails(USSDPlain_INTERNETRCDP.CUCategory, channelMap);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC20_Negative__MinResidualBalance() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC20");
        TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();
        _parser parser = new _parser();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_INTERNETRCDP.getAPIdata();

        String API = USSDINTRCAPI.prepareAPI(apiData);


        String balance = DBHandler.AccessHandler.getUserBalance(USSDPlain_INTERNETRCDP.ProductCode, USSDPlain_INTERNETRCDP.LoginID);
        parser.convertStringToLong(balance).changeDenomation();

        System.out.println("The balance is:" + balance);
        long usrBalance = (long) (parser.getValue()) - 100 + 2;
        System.out.println(usrBalance);

        ExtentI.Markup(ExtentColor.TEAL, "Modifying Minimum Residual Balance in Transfer Control Profile");


        String[] values = {String.valueOf(usrBalance), String.valueOf(usrBalance)};
        String[] parameters = {"minBalance", "altBalance"};

        trfCntrlProf.modifyProductValuesInTCP(USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.TCPID, parameters, values, USSDPlain_INTERNETRCDP.ProductName, true);

        String[] APIResponse = null;

        try {
            APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        } catch (Exception e) {

            ExtentI.Markup(ExtentColor.RED, "Gift Recharge is not successful with  error message");

        }

        ExtentI.Markup(ExtentColor.TEAL, "Updating Minimum Residual Balance in Transfer Control Profile");
        values = new String[]{"0", "0"};
        trfCntrlProf.modifyProductValuesInTCP(USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.TCPID, parameters, values, USSDPlain_INTERNETRCDP.ProductName, true);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC21_Negative__C2SMinAmount() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC21");
        TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_INTERNETRCDP.getAPIdata();
        apiData.put(USSDINTRCAPI.AMOUNT, "90");
        String API = USSDINTRCAPI.prepareAPI(apiData);

        ExtentI.Markup(ExtentColor.TEAL, "Modifying C2S Min Amount in Transfer Control Profile");
        trfCntrlProf.modifyTCPPerC2SminimumAmt(USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.TCPID, "100", "100", USSDPlain_INTERNETRCDP.ProductName);

        String[] APIResponse = null;
        try {
            APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        } catch (Exception e) {

            ExtentI.Markup(ExtentColor.RED, "Gift Recharge is not successful with  error message");

        }

        ExtentI.Markup(ExtentColor.TEAL, "Updating C2S Min Amount in Transfer Control Profile");
        trfCntrlProf.modifyTCPPerC2SminimumAmt(USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.TCPID, _masterVO.getProperty("MinimumBalance"), _masterVO.getProperty("AllowedMaxPercentage"), USSDPlain_INTERNETRCDP.ProductName);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_InternetRC22_C2STaxAdditionalCommProfile()
            throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC22");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);


        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);

        ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");
        String actual = CommissionProfile.getCommissionSlabCount(USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade);
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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPlain_INTERNETRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPlain_INTERNETRCAPI.TXNID);

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
        CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));
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
    public void TC_InternetRC23_C2STaxAdditionalCommProfile()
            throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC23");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);


        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);

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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);

        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPlain_INTERNETRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPlain_INTERNETRCAPI.TXNID);


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
        CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }


    @Test
    public void TC_InternetRC24_C2STimeSlabAdditionalCommProfile()
            throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC24");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);

        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);

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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_TimeSlab_ParticularService(slabMap, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPlain_INTERNETRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPlain_INTERNETRCAPI.TXNID);


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
        CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }


    @Test
    public void TC_InternetRC25_C2SMinTransferValidation()
            throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC25");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);

        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);

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

        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPlain_INTERNETRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPlain_INTERNETRCAPI.TXNID);
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
        CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }


    @Test
    public void TC_InternetRC26_SlabToRangeValidation()
            throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDINTRC26");
        USSDPlain_INTERNETRCAPI USSDINTRCAPI = new USSDPlain_INTERNETRCAPI();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPlain_INTERNETRCDP.getAPIdata();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);


        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = USSDINTRCAPI.prepareAPI(dataMap);

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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPlain_INTERNETRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPlain_INTERNETRCAPI.TXNID);
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
        CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPlain_INTERNETRCDP.Domain, USSDPlain_INTERNETRCDP.CUCategory, USSDPlain_INTERNETRCDP.grade, USSDPlain_INTERNETRCDP.CPName, service,_masterVO.getProperty("InternetRechargeCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }


}
