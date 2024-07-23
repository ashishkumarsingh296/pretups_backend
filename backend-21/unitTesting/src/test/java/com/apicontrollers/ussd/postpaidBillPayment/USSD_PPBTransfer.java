package com.apicontrollers.ussd.postpaidBillPayment;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.Map_CommissionProfile;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
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

public class USSD_PPBTransfer extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    /**
     * @throws Exception
     * @testid USSDPPB01
     * Positive Test Case For TRFCATEGORY: Postpaid Bill Payment
     */
    @Test
    public void TC_A_PositivePPBAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB01");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = USSDPPBDP.getAPIdataWithAllUsers();


        for (int i = 0; i < dataObject.length; i++) {

            USSD_PPBDAO APIDAO = (USSD_PPBDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();


            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            String API = PPBTransferAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
            Validator.messageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }
    }

    @Test
    public void TC_B_NegativePPBAPI_NoPIN() throws Exception {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB02");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        dataMap.put(PPBTransferAPI.PIN, "");

        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_C_NegativePPBAPI_withBlankMSISDN_PIN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB03");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        dataMap.put(PPBTransferAPI.MSISDN1, "");
        dataMap.put(PPBTransferAPI.PIN, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_D_PositivePPBAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB04");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();
        ChannelUser ChannelUser = new ChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();

        HashMap<String, String> channelMap = new HashMap<String, String>();
        channelMap.put("searchMSISDN", dataMap.get(PPBTransferAPI.MSISDN1));
        channelMap.put("inSuspend_chk", "Y");
        ChannelUser.modifyChannelUserDetails(USSDPPBDP.CUCategory, channelMap);

        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        channelMap.put("inSuspend_chk", "N");
        ChannelUser.modifyChannelUserDetails(USSDPPBDP.CUCategory, channelMap);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.partialmessageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_E_NegativePPBAPI_CUOutSuspended() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB05");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();
        ChannelUser ChannelUser = new ChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();

        HashMap<String, String> channelMap = new HashMap<String, String>();
        channelMap.put("searchMSISDN", dataMap.get(PPBTransferAPI.MSISDN1));
        channelMap.put("outSuspend_chk", "Y");
        ChannelUser.modifyChannelUserDetails(USSDPPBDP.CUCategory, channelMap);

        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        channelMap.put("outSuspend_chk", "N");
        ChannelUser.modifyChannelUserDetails(USSDPPBDP.CUCategory, channelMap);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.partialmessageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_F_NegativePPBAPI_CUSuspended() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB06");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();
        SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
        ResumeChannelUser CUResume = new ResumeChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();

        CUSuspend.suspendChannelUser_MSISDN(dataMap.get(PPBTransferAPI.MSISDN1), "Automated EXTGW O2C API Testing: USSDPPB06");
        CUSuspend.approveCSuspendRequest_MSISDN(dataMap.get(PPBTransferAPI.MSISDN1), "Automated EXTGW O2C API Testing: USSDPPB06");

        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        CUResume.resumeChannelUser_MSISDN(dataMap.get(PPBTransferAPI.MSISDN1), "Automated EXTGW O2C API Testing: USSDPPB06");

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.partialmessageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_I_NegativePPBAPI_BlankCUMSISDN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB07");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        dataMap.put(PPBTransferAPI.MSISDN1, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.partialmessageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_J_NegativePPBAPI_InvalidCUMSISDN() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB08");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();
        GenerateMSISDN gnMsisdn = new GenerateMSISDN();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        dataMap.put(PPBTransferAPI.MSISDN1, "" + gnMsisdn.generateMSISDN());

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.partialmessageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_K_NegativePPBAPI_InvalidLanguageCode() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB09");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();

        dataMap.put(PPBTransferAPI.LANGUAGE1, RandomGeneration.randomNumeric(3));
        dataMap.put(PPBTransferAPI.LANGUAGE2, RandomGeneration.randomNumeric(3));

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.partialmessageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_L_NegativePPBAPI_InvalidPin() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB10");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        String CorrectPin = dataMap.get(PPBTransferAPI.PIN);
        String InValPin;

        do {
            InValPin = RandomGeneration.randomNumeric(4);
        } while (CorrectPin == InValPin);
        dataMap.put(PPBTransferAPI.PIN, InValPin);

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_M_NegativePPBAPI_BlankAmount() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB11");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        dataMap.put(PPBTransferAPI.AMOUNT, "");

        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_Q_NegativePPBAPI_NegAmount() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB12");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        dataMap.put(PPBTransferAPI.AMOUNT, "-1");

        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_R_NegativePPBAPI_IncorrectSelectorCode() throws Exception {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB13");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        dataMap.put(PPBTransferAPI.SELECTOR, "-1");

        String API = PPBTransferAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_S_suspendAdditionalCommProfile()
            throws InterruptedException, Throwable {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB14");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        CommissionProfile CommissionProfile = new CommissionProfile(driver);
        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);

        ExtentI.Markup(ExtentColor.TEAL, "Suspend Additional Commission Profile slab");

        long time2 = CommissionProfile.suspendAdditionalCommProfileForGivenService(USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));

        Thread.sleep(time2);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPPBAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPPBAPI.TXNID);

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

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB15");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        CommissionProfile CommissionProfile = new CommissionProfile(driver);

        String Transfer_ID1 = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);
        ExtentI.Markup(ExtentColor.TEAL, "Resume Additional Commission Profile slab");
        long time = CommissionProfile.resumeAdditionalCommProfileForGivenService(USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));
        Log.info("Wait for Commission Profile Version to be active");
        Thread.sleep(time);

        ExtentI.Markup(ExtentColor.TEAL, "Perform C2S transaction after Resuming Additional Commission Profile slab");


        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPPBAPI.TXNSTATUS).toString());


        Transfer_ID1 = xmlPath.get(USSDPPBAPI.TXNID);
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

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB16");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();
        TransferControlProfile TCPObj = new TransferControlProfile(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPPBDP.getAPIdata();
        String API = PPBTransferAPI.prepareAPI(apiData);

        TCPObj.channelLevelTransferControlProfileSuspend(0, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.TCPName, null);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        TCPObj.channelLevelTransferControlProfileActive(0, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.TCPName, null);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC_W_Negative__MinResidualBalance() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB17");
        TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();
        _parser parser = new _parser();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPPBDP.getAPIdata();

        String API = PPBTransferAPI.prepareAPI(apiData);

        String balance = DBHandler.AccessHandler.getUserBalance(USSDPPBDP.ProductCode, USSDPPBDP.LoginID);
        parser.convertStringToLong(balance).changeDenomation();

        System.out.println("The balance is:" + balance);
        long usrBalance = (long) (parser.getValue()) - 100 + 2;
        System.out.println(usrBalance);

        ExtentI.Markup(ExtentColor.TEAL, "Modifying Minimum Residual Balance in Transfer Control Profile");


        String[] values = {String.valueOf(usrBalance), String.valueOf(usrBalance)};
        String[] parameters = {"minBalance", "altBalance"};

        trfCntrlProf.modifyProductValuesInTCP(USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.TCPID, parameters, values, USSDPPBDP.ProductName, true);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        ExtentI.Markup(ExtentColor.TEAL, "Updating Minimum Residual Balance in Transfer Control Profile");
        values = new String[]{"0", "0"};
        trfCntrlProf.modifyProductValuesInTCP(USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.TCPID, parameters, values, USSDPPBDP.ProductName, true);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void TC_X_Negative__C2SMinAmount() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB18");
        RandomGeneration RandomGeneration = new RandomGeneration();
        TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPPBDP.getAPIdata();
        apiData.put(PPBTransferAPI.AMOUNT, "90");
        String API = PPBTransferAPI.prepareAPI(apiData);

        ExtentI.Markup(ExtentColor.TEAL, "Modifying C2S Min Amount in Transfer Control Profile");
        trfCntrlProf.modifyTCPPerC2SminimumAmt(USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.TCPID, "100", "100", USSDPPBDP.ProductName);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        ExtentI.Markup(ExtentColor.TEAL, "Updating C2S Min Amount in Transfer Control Profile");
        trfCntrlProf.modifyTCPPerC2SminimumAmt(USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.TCPID, _masterVO.getProperty("MinimumBalance"), _masterVO.getProperty("AllowedMaxPercentage"), USSDPPBDP.ProductName);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDPPBAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_Y_C2STaxAdditionalCommProfile() throws InterruptedException, Throwable {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB19");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        String API = PPBTransferAPI.prepareAPI(dataMap);

        ExtentI.Markup(ExtentColor.TEAL, "Modifying From Range of  Additional Commission Profile slab");

        String actual = CommissionProfile.getCommissionSlabCount(USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade);
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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPPBAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPPBAPI.TXNID);


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
        CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));
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

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB20");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);

        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);

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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);

        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPPBAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPPBAPI.TXNID);


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
        CommissionProfile.modifyAdditionalCommissionProfile_SITService(slabMap, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }

    @Test
    public void TC_Z_A_C2STimeSlabAdditionalCommProfile() throws InterruptedException, Throwable {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB21");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);

        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);

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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_TimeSlab_ParticularService(slabMap, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPPBAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPPBAPI.TXNID);


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
        long time = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }

    @Test
    public void TC_Z_B_C2SMinTransferValidation() throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB22");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();
        USSDPPBDP USSDPPBDP = new USSDPPBDP();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);

        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);

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

        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPPBAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPPBAPI.TXNID);
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
        long time = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }

    @Test
    public void TC_Z_B_C2S_SlabToRangeValidation() throws InterruptedException, Throwable {


        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPPB23");
        USSDPPBAPI PPBTransferAPI = new USSDPPBAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSDPPBDP.getAPIdata();
        Map_CommissionProfile Map_CommProfile = new Map_CommissionProfile(driver);
        CommissionProfile CommissionProfile = new CommissionProfile(driver);


        String Transfer_ID = null;
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = PPBTransferAPI.prepareAPI(dataMap);

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


        long time2 = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));

        ExtentI.Markup(ExtentColor.TEAL, "Modified Additional Commission Profile slab");
        Thread.sleep(time2);


        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Log.info("The Txn status is " + xmlPath.get(USSDPPBAPI.TXNSTATUS).toString());


        Transfer_ID = xmlPath.get(USSDPPBAPI.TXNID);


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
        long time = CommissionProfile.modifyAdditionalCommissionProfile_SITService(AddCommMap, USSDPPBDP.Domain, USSDPPBDP.CUCategory, USSDPPBDP.grade, USSDPPBDP.CPName,USSDPPBDP.serviceName,_masterVO.getProperty("PostpaidBillPaymentCode"));
        ExtentI.Markup(ExtentColor.TEAL, "Reverted values Additional Commission Profile slab");

    }

}
