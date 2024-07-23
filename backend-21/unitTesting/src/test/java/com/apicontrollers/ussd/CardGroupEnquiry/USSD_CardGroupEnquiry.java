package com.apicontrollers.ussd.CardGroupEnquiry;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.apicontrollers.extgw.cardGroupEnquiry.EXTGW_CardGroupENQ_DP;
import com.apicontrollers.extgw.cardGroupEnquiry.EXTGW_CrdGrpENQ_DAO;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.ExtentI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;

import io.restassured.path.xml.XmlPath;

public class USSD_CardGroupEnquiry extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    /**
     * @testid USSDCGENQ01
     * Positive Test Case For TRFCATEGORY: Card Group Enquiry
     */
    @Test
    public void TC_01_PositiveCardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ01");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = USSD_CardGroupEnquiry_DP.getAPIdataWithAllUsers();


        for (int i = 0; i < dataObject.length; i++) {
            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            EXTGW_CrdGrpENQ_DAO APIDAO = (EXTGW_CrdGrpENQ_DAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            String API = CardGroupAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
            Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }
    }

    @Test
    public void TC_02_CardGrpEnqAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ02");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
        apiData.put(CardGroupAPI.MSISDN1, "");
        apiData.put(CardGroupAPI.PIN, "");

        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_03_CardGrpEnqAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ03");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = EXTGW_CardGroupENQ_DP.getAPIdata();
        apiData.put(CardGroupAPI.PIN, "");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_04_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ04");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();
        RandomGeneration Random = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.PIN, Random.randomNumeric(4));
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_05_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ05");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();
        RandomGeneration Random = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.MSISDN1, UniqueChecker.UC_MSISDN());
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_06_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ06");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.MSISDN2, "INVALID MSISDN");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_07_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ07");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.MSISDN2, "");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_08_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ08");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.SERVICETYPE, _masterVO.getProperty("PostpaidBillPaymentCode"));
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_09_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ09");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.SERVICETYPE, "INVALID SERVICETYPE");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_10_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ10");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.SERVICETYPE, "");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_11_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ11");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.SUBSERVICE, "");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_12_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ12");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.SUBSERVICE, "INVALID SUBSERVICE");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_13_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ13");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.AMOUNT, "");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_14_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ14");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        apiData.put(CardGroupAPI.AMOUNT, "-100");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_15_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ15");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        String MinRange = DBHandler.AccessHandler.getCardGroupStartRange(USSD_CardGroupEnquiry_DP.CrdGrp);
        long Amount = Long.parseLong(_parser.getDisplayAmount(Long.parseLong(MinRange))) - 1;
        apiData.put(CardGroupAPI.AMOUNT, String.valueOf(Amount));
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_16_CardGrpEnqAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ16");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();
        ChannelUser ChannelUser = new ChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();

        HashMap<String, String> channelMap = new HashMap<String, String>();
        channelMap.put("searchMSISDN", apiData.get(CardGroupAPI.MSISDN1));
        channelMap.put("outSuspend_chk", "Y");
        ChannelUser.modifyChannelUserDetails(USSD_CardGroupEnquiry_DP.CUCategory, channelMap);

        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        channelMap.put("outSuspend_chk", "N");
        ChannelUser.modifyChannelUserDetails(USSD_CardGroupEnquiry_DP.CUCategory, channelMap);

        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_17_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ17");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();
        ChannelUser ChannelUser = new ChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        String DeletedMSISDN = DBHandler.AccessHandler.deletedMSISDN();
        apiData.put(CardGroupAPI.MSISDN1, DeletedMSISDN);

        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_18_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ18");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();
        SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
        ResumeChannelUser resumeChnluser = new ResumeChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();

        ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
        suspendChnluser.suspendChannelUser_MSISDN(apiData.get(CardGroupAPI.MSISDN1), "Automated Suspend For Test Case USSDCGENQ18");
        String SuspendApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_SUS_APP").toUpperCase();
        if (SuspendApprovalReq.equals("TRUE"))
            suspendChnluser.approveCSuspendRequest_LoginID(apiData.get(CardGroupAPI.MSISDN1), "Automated Suspend Approval For Test Case USSDCGENQ18");

        ExtentI.Markup(ExtentColor.TEAL, "Performing Test Case");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_19_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ19");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();
        ResumeChannelUser resumeChnluser = new ResumeChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();

        ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
        resumeChnluser.resumeChannelUser_MSISDN(apiData.get(CardGroupAPI.MSISDN1), "Automated Resume For Test Case USSDCGENQ18");

        ExtentI.Markup(ExtentColor.TEAL, "Performing Test Case");
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_20_CardGrpEnqAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDCGENQ20");
        USSD_CardGroupEnquiryAPI CardGroupAPI = new USSD_CardGroupEnquiryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CardGroupEnquiry_DP.getAPIdata();
        String maxRange = DBHandler.AccessHandler.getCardGroupEndRange(EXTGW_CardGroupENQ_DP.CrdGrp);
        long Amount = Long.parseLong(_parser.getDisplayAmount(Long.parseLong(maxRange))) + 1;
        apiData.put(CardGroupAPI.AMOUNT, String.valueOf(Amount));
        String API = CardGroupAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CardGroupAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

}
