package com.apicontrollers.ussd.GRCTransfer;

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

public class USSD_GRCTransfer extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    /**
     * @throws Exception
     * @testid USSDGRC01
     * Positive Test Case For TRFCATEGORY: C2S
     */
    @Test
    public void TC_A_PositiveGRCAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC01");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = USSDGRCDP.getAPIdataWithAllUsers();


        for (int i = 0; i < dataObject.length; i++) {

            USSD_GRCDAO APIDAO = (USSD_GRCDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();


            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            String API = C2STransferAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
            Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }
    }

    @Test
    public void TC_B_NegativeGRCAPI_NoPIN() throws Exception {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC02");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        dataMap.put(C2STransferAPI.PIN, "");

        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_C_NegativeGRCAPI_withBlankMSISDN_PIN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC03");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        dataMap.put(C2STransferAPI.MSISDN1, "");
        dataMap.put(C2STransferAPI.PIN, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_D_PositiveGRCAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC04");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();
        ChannelUser ChannelUser = new ChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();

        HashMap<String, String> channelMap = new HashMap<String, String>();
        channelMap.put("searchMSISDN", dataMap.get(C2STransferAPI.MSISDN1));
        channelMap.put("inSuspend_chk", "Y");
        ChannelUser.modifyChannelUserDetails(USSDGRCDP.CUCategory, channelMap);

        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        channelMap.put("inSuspend_chk", "N");
        ChannelUser.modifyChannelUserDetails(USSDGRCDP.CUCategory, channelMap);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_E_NegativeGRCAPI_CUOutSuspended() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC05");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();
        ChannelUser ChannelUser = new ChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();

        HashMap<String, String> channelMap = new HashMap<String, String>();
        channelMap.put("searchMSISDN", dataMap.get(C2STransferAPI.MSISDN1));
        channelMap.put("outSuspend_chk", "Y");
        ChannelUser.modifyChannelUserDetails(USSDGRCDP.CUCategory, channelMap);

        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        channelMap.put("outSuspend_chk", "N");
        ChannelUser.modifyChannelUserDetails(USSDGRCDP.CUCategory, channelMap);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_F_NegativeGRCAPI_CUSuspended() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC06");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();
        SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
        ResumeChannelUser CUResume = new ResumeChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();

        CUSuspend.suspendChannelUser_MSISDN(dataMap.get(C2STransferAPI.MSISDN1), "Automated EXTGW O2C API Testing: USSDGRC06");
        CUSuspend.approveCSuspendRequest_MSISDN(dataMap.get(C2STransferAPI.MSISDN1), "Automated EXTGW O2C API Testing: USSDGRC06");

        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        CUResume.resumeChannelUser_MSISDN(dataMap.get(C2STransferAPI.MSISDN1), "Automated EXTGW O2C API Testing: USSDGRC06");

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_I_NegativeGRCAPI_BlankCUMSISDN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC07");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        dataMap.put(C2STransferAPI.MSISDN1, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_J_NegativeGRCAPI_InvalidCUMSISDN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC08");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();
        GenerateMSISDN gnMsisdn = new GenerateMSISDN();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        dataMap.put(C2STransferAPI.MSISDN1, "" + gnMsisdn.generateMSISDN());

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_K_NegativeGRCAPI_InvalidLanguageCode() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC09");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();

        dataMap.put(C2STransferAPI.LANGUAGE1, RandomGeneration.randomNumeric(3));
        dataMap.put(C2STransferAPI.LANGUAGE2, RandomGeneration.randomNumeric(3));

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_L_NegativeGRCAPI_InvalidPin() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC10");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        String CorrectPin = dataMap.get(C2STransferAPI.PIN);
        String InValPin;

        do {
            InValPin = RandomGeneration.randomNumeric(4);
        } while (CorrectPin == InValPin);
        dataMap.put(C2STransferAPI.PIN, InValPin);

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_M_NegativeGRCAPI_BlankAmount() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC11");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        dataMap.put(C2STransferAPI.AMOUNT, "");

        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_Q_NegativeGRCAPI_NegAmount() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC12");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        dataMap.put(C2STransferAPI.AMOUNT, "-1");

        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_R_NegativeGRCAPI_IncorrectSelectorCode() throws Exception {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC13");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        dataMap.put(C2STransferAPI.SELECTOR, "-1");

        String API = C2STransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_S_suspendAdditionalCommProfile()
            throws InterruptedException, Throwable {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC14");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        CommissionProfile CommissionProfile = new CommissionProfile(driver);
        C2STransfer c2STransfer = new C2STransfer(driver);
        String Transfer_ID = null;
        String Transfer_ID1 = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = C2STransferAPI.prepareAPI(dataMap);

        ExtentI.Markup(ExtentColor.TEAL, "Suspend Additional Commission Profile slab");

        long time2 = CommissionProfile.suspendAdditionalCommProfileForGivenService(USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));

        Thread.sleep(time2);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDGRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDGRCAPI.TXNID);

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

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC15");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        CommissionProfile CommissionProfile = new CommissionProfile(driver);

        String Transfer_ID1 = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = C2STransferAPI.prepareAPI(dataMap);
        ExtentI.Markup(ExtentColor.TEAL, "Resume Additional Commission Profile slab");
        long time = CommissionProfile.resumeAdditionalCommProfileForGivenService(USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));
        Log.info("Wait for Commission Profile Version to be active");
        Thread.sleep(time);

        ExtentI.Markup(ExtentColor.TEAL, "Perform C2S transaction after Resuming Additional Commission Profile slab");


        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDGRCAPI.TXNSTATUS).toString());


        Transfer_ID1 = xmlPath.get(USSDGRCAPI.TXNID);
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

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC16");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();
        TransferControlProfile TCPObj = new TransferControlProfile(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDGRCDP.getAPIdata();
        String API = C2STransferAPI.prepareAPI(apiData);

        TCPObj.channelLevelTransferControlProfileSuspend(0, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.TCPName, null);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        TCPObj.channelLevelTransferControlProfileActive(0, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.TCPName, null);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC_W_Negative__MinResidualBalance() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC17");
        TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();
        _parser parser = new _parser();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDGRCDP.getAPIdata();

        String API = C2STransferAPI.prepareAPI(apiData);

        String balance = DBHandler.AccessHandler.getUserBalance(USSDGRCDP.ProductCode, USSDGRCDP.LoginID);
        parser.convertStringToLong(balance).changeDenomation();

        System.out.println("The balance is:" + balance);
        long usrBalance = (long) (parser.getValue()) - 100 + 2;
        System.out.println(usrBalance);

        ExtentI.Markup(ExtentColor.TEAL, "Modifying Minimum Residual Balance in Transfer Control Profile");


        String[] values = {String.valueOf(usrBalance), String.valueOf(usrBalance)};
        String[] parameters = {"minBalance", "altBalance"};

        trfCntrlProf.modifyProductValuesInTCP(USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.TCPID, parameters, values, USSDGRCDP.ProductName, true);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        ExtentI.Markup(ExtentColor.TEAL, "Updating Minimum Residual Balance in Transfer Control Profile");
        values = new String[]{"0", "0"};
        trfCntrlProf.modifyProductValuesInTCP(USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.TCPID, parameters, values, USSDGRCDP.ProductName, true);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void TC_X_Negative__C2SMinAmount() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC18");
        RandomGeneration RandomGeneration = new RandomGeneration();
        TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDGRCDP.getAPIdata();
        apiData.put(C2STransferAPI.AMOUNT, "90");
        String API = C2STransferAPI.prepareAPI(apiData);

        ExtentI.Markup(ExtentColor.TEAL, "Modifying C2S Min Amount in Transfer Control Profile");
        trfCntrlProf.modifyTCPPerC2SminimumAmt(USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.TCPID, "100", "100", USSDGRCDP.ProductName);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        ExtentI.Markup(ExtentColor.TEAL, "Updating C2S Min Amount in Transfer Control Profile");
        trfCntrlProf.modifyTCPPerC2SminimumAmt(USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.TCPID, _masterVO.getProperty("MinimumBalance"), _masterVO.getProperty("AllowedMaxPercentage"), USSDGRCDP.ProductName);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

   @Test
    public void TC_Y_C2STaxAdditionalCommProfile() throws InterruptedException, Throwable {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC19");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
        String API = C2STransferAPI.prepareAPI(dataMap);

        ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");

        String actual = CommissionProfile.getCommissionSlabCount(USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade);
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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDGRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDGRCAPI.TXNID);


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
        CommissionProfile.modifyAdditionalCommissionProfile_SIT(slabMap, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName);
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
    public void TC_Z_C2STaxAdditionalCommProfile() throws InterruptedException, Throwable {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC20");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDGRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDGRCAPI.TXNID);


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
        CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }

    @Test
    public void TC_Z_A_C2STimeSlabAdditionalCommProfile() throws InterruptedException, Throwable {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC21");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_TimeSlab_ParticularService(slabMap, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDGRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDGRCAPI.TXNID);


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
        long time = CommissionProfile.modifyAdditionalCommissionProfile_TimeSlab_ParticularService(slabMap, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }

    @Test
    public void TC_Z_B_C2SMinTransferValidation() throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC22");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();
        USSDGRCDP USSDGRCDP = new USSDGRCDP();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
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

        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDGRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDGRCAPI.TXNID);
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
        long time = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }

    @Test
    public void TC_Z_B_C2S_SlabToRangeValidation() throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC23");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDGRCDP.getAPIdata();
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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDGRCAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDGRCAPI.TXNID);


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
        long time = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDGRCDP.Domain, USSDGRCDP.CUCategory, USSDGRCDP.grade, USSDGRCDP.CPName,USSDGRCDP.serviceName,_masterVO.getProperty("GiftRechargeCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }
    @Test
    public void TC_Z_C_C2S_BarredAsSender() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC24");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

           HashMap<String, String> apiData = USSDGRCDP.getAPIdata();


            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            BarUnbar BarUnbar = new BarUnbar(driver);
            ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
            BarUnbar.barringUser("C2S", "SENDER", apiData.get(C2STransferAPI.MSISDN1));
            String API = C2STransferAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
            Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            BarUnbar.unBarringUser("C2S", "SENDER", apiData.get(C2STransferAPI.MSISDN1));
        }

    @Test
    public void TC_Z_D_C2S_BarredAsReceiver() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC25");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

           HashMap<String, String> apiData = USSDGRCDP.getAPIdata();


            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            BarUnbar BarUnbar = new BarUnbar(driver);
            ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
            BarUnbar.barringUser("C2S", "RECEIVER", apiData.get(C2STransferAPI.MSISDN1));
            String API = C2STransferAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
            Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            BarUnbar.unBarringUser("C2S", "RECEIVER", apiData.get(C2STransferAPI.MSISDN1));
        }
    @Test
    public void TC_Z_E_C2S_BarredAsReceiver() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDGRC26");
        USSDGRCAPI C2STransferAPI = new USSDGRCAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> apiData = USSDGRCDP.getAPIdata();


        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        BarUnbar BarUnbar = new BarUnbar(driver);
        ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
        BarUnbar.barringUser("C2S", "RECEIVER", apiData.get(C2STransferAPI.MSISDN2));
        String API = C2STransferAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDGRCAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        BarUnbar.unBarringUser("C2S", "RECEIVER", apiData.get(C2STransferAPI.MSISDN2));
    }
    }

