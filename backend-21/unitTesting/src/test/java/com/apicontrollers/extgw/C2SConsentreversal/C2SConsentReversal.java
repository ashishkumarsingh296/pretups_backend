package com.apicontrollers.extgw.C2SConsentreversal;

import com.Features.BarUnbar;
import com.Features.ChannelUser;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.c2scardgroup.C2Scardgroupstatuspage1;
import com.utils.*;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

public class C2SConsentReversal extends BaseTest {

    CaseMaster CaseMaster = null;
    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void TC01_PositiveMSISDN_PIN() throws SQLException, ParseException{
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent01");
    C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.LOGINID, "");
        apiData.put(C2SConsentAPI.PASSWORD, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

    @Test
    public void TC02_PositiveLoginID_Pswd() throws SQLException, ParseException{
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent02");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.MSISDN, "");
        apiData.put(C2SConsentAPI.PIN, "");
        apiData.put(C2SConsentAPI.EXTCODE, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);

    }

    @Test
    public void TC03_PositiveEXTCODE() throws SQLException, ParseException {
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent03");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.MSISDN, "");
        apiData.put(C2SConsentAPI.PIN, "");
        apiData.put(C2SConsentAPI.LOGINID, "");
        apiData.put(C2SConsentAPI.PASSWORD, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);

    }

    @Test
    public void TC04_InvalidMSISDN() throws SQLException, ParseException{
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent04");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
        test = extent.createTest(CaseMaster.getModuleCode());
        TestCaseCounter = true;
          }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.MSISDN, RandomGeneration.randomNumeric(10));
        apiData.put(C2SConsentAPI.LOGINID, "");
        apiData.put(C2SConsentAPI.PASSWORD, "");
        apiData.put(C2SConsentAPI.EXTCODE, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

    @Test
    public void TC05_BlankMSISDN() throws SQLException, ParseException{
       CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent05");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.MSISDN, "");
        apiData.put(C2SConsentAPI.LOGINID, "");
        apiData.put(C2SConsentAPI.PASSWORD, "");
        apiData.put(C2SConsentAPI.EXTCODE, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

    @Test
    public void TC06_InvalidPIN() throws SQLException, ParseException{
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent06");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.PIN, RandomGeneration.randomNumeric(4));
        apiData.put(C2SConsentAPI.LOGINID, "");
        apiData.put(C2SConsentAPI.PASSWORD, "");
        apiData.put(C2SConsentAPI.EXTCODE, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

    @Test
    public void TC07_BlankPIN() throws SQLException, ParseException{
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent07");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.PIN, "");
        apiData.put(C2SConsentAPI.LOGINID, "");
        apiData.put(C2SConsentAPI.PASSWORD, "");
        apiData.put(C2SConsentAPI.EXTCODE, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }
    @Test
    public void TC08_InvalidTXNID() throws SQLException, ParseException{
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent08");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, "R"+RandomGeneration.randomNumeric(6)+"."+RandomGeneration.randomNumeric(4)+"."+RandomGeneration.randomNumeric(6));
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

    @Test
    public void TC09_BlankTXNID() throws SQLException, ParseException{
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent09");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC10_NegativeSuspendedUser() throws SQLException, ParseException{
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent10");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID,data[0]);
        SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
        ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
        ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
        suspendCHNLUser.suspendChannelUser_MSISDN(apiData.get(C2SConsentAPI.MSISDN), "CASEA_CuspendedO2C");
        ExtentI.Markup(ExtentColor.TEAL, "Approving Channel User Suspend Request");
        suspendCHNLUser.approveCSuspendRequest_MSISDN(apiData.get(C2SConsentAPI.MSISDN), "CASEA_CuspendedO2C");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
        ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
        resumeCHNLUser.resumeChannelUser_MSISDN(apiData.get(C2SConsentAPI.MSISDN), "Auto Resume Remarks");

    }

    @Test
    public void TC11_InvalidLoginID() throws SQLException, ParseException{
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent11");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.PIN, "");
        apiData.put(C2SConsentAPI.LOGINID, RandomGeneration.randomAlphaNumeric(10));
        apiData.put(C2SConsentAPI.MSISDN, "");
        apiData.put(C2SConsentAPI.EXTCODE, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

    @Test
    public void TC12_InvalidPassword() throws SQLException, ParseException{
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent12");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.PIN, "");
        apiData.put(C2SConsentAPI.PASSWORD, "cOm@0000");
        apiData.put(C2SConsentAPI.MSISDN, "");
        apiData.put(C2SConsentAPI.EXTCODE, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

    @Test
    public void TC13_BlankLoginID() throws SQLException, ParseException{
       CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent13");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.PIN, "");
        apiData.put(C2SConsentAPI.LOGINID, "");
        apiData.put(C2SConsentAPI.MSISDN, "");
        apiData.put(C2SConsentAPI.EXTCODE, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

    @Test
    public void TC14_BlankPassword() throws SQLException, ParseException{
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent14");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.PIN, "");
        apiData.put(C2SConsentAPI.PASSWORD, "");
        apiData.put(C2SConsentAPI.MSISDN, "");
        apiData.put(C2SConsentAPI.EXTCODE, "");
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

    @Test
    public void TC15_InvalidEXTCODE() throws SQLException, ParseException{
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent15");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        apiData.put(C2SConsentAPI.PIN, "");
        apiData.put(C2SConsentAPI.PASSWORD, "");
        apiData.put(C2SConsentAPI.MSISDN, "");
        apiData.put(C2SConsentAPI.LOGINID, "");
        apiData.put(C2SConsentAPI.EXTCODE, RandomGeneration.randomNumeric(10));
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

    @Test
    public void TC16_SenderBarred() throws SQLException, ParseException{
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent16");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        BarUnbar BarUnbar = new BarUnbar(driver);
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
        BarUnbar.barringUser("C2S", "SENDER", apiData.get(C2SConsentAPI.MSISDN));
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
        ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
        BarUnbar.unBarringUser("C2S", "SENDER", apiData.get(C2SConsentAPI.MSISDN));
    }

    @Test
    public void TC17_ReceiverBarred() throws SQLException, ParseException{
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent17");
        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();
        BarUnbar BarUnbar = new BarUnbar(driver);
        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        ExtentI.Markup(ExtentColor.TEAL, "Barring Channel User");
        BarUnbar.barringUser("C2S", "RECEIVER", apiData.get(C2SConsentAPI.MSISDN));
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
        ExtentI.Markup(ExtentColor.TEAL, "Unbarring Channel User");
        BarUnbar.unBarringUser("C2S", "RECEIVER", apiData.get(C2SConsentAPI.MSISDN));
    }

    @Test
    public void TC18_Resend_OTP() throws SQLException, ParseException{
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent18");

        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();

            HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
            apiData.put(C2SConsentAPI.TXNID, data[0]);
            String API = C2SConsentAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), "200" );
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
        String[] APIResponse1 = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse1);

        XmlPath xmlPath1 = new XmlPath(CompatibilityMode.HTML, APIResponse1[1]);
        Validator.messageCompare(xmlPath1.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode() );



    }

    @Test
    public void TC19_INSuspend() throws SQLException, ParseException, InterruptedException {
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent19");
        ChannelUser ChannelUser = new ChannelUser(driver);
        EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();


        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();

        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        HashMap<String, String> channelMap = new HashMap<String, String>();
        channelMap.put("searchMSISDN", apiData.get(C2STransferAPI.MSISDN));
        channelMap.put("inSuspend_chk", "Y");
        ChannelUser.modifyChannelUserDetails(EXTGW_C2SConsentDP.CUCategory, channelMap);
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
        apiData.put("inSuspend_chk", "N");
        channelMap.put("inSuspend_chk", "N");
        ChannelUser.modifyChannelUserDetails(EXTGW_C2SConsentDP.CUCategory, channelMap);
        System.out.println("Value of cu "+EXTGW_C2SConsentDP.CUCategory);
    }

    @Test
    public void TC20_OUTSuspend() throws SQLException, ParseException, InterruptedException {
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2SConsent20");
        ChannelUser ChannelUser = new ChannelUser(driver);
        EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();


        C2SConsentAPI C2SConsentAPI= new C2SConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2SConsentDP.C2Stransfer();

        HashMap<String, String> apiData = EXTGW_C2SConsentDP.getAPIdata();
        HashMap<String, String> channelMap = new HashMap<String, String>();
        channelMap.put("searchMSISDN", apiData.get(C2STransferAPI.MSISDN));
        channelMap.put("outSuspend_chk", "Y");
        ChannelUser.modifyChannelUserDetails(EXTGW_C2SConsentDP.CUCategory, channelMap);
        apiData.put(C2SConsentAPI.TXNID, data[0]);
        String API = C2SConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2SConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2SConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
        apiData.put("outSuspend_chk", "N");
        channelMap.put("outSuspend_chk", "N");
        ChannelUser.modifyChannelUserDetails(EXTGW_C2SConsentDP.CUCategory, channelMap);
        System.out.println("Value of cu "+EXTGW_C2SConsentDP.CUCategory);
        }

}
