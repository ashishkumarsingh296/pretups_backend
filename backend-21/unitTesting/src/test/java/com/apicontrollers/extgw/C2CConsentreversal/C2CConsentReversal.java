package com.apicontrollers.extgw.C2CConsentreversal;

import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.*;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

public class C2CConsentReversal extends BaseTest {
    CaseMaster CaseMaster = null;
    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void TC1_PositivewithMSISDN_PIN() throws SQLException, ParseException {
        CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent01");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID, data[0]);
        apiData.put(C2CConsentAPI.LOGINID, "");
        apiData.put(C2CConsentAPI.PASSWORD, "");
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        String OTP = DBHandler.AccessHandler.getConsentOTP(apiData.get(C2CConsentAPI.MSISDN),data[1], data[0]);
        Log.info("OTP is: " + OTP);
    }

  @ Test
    public void TC2_Negative_invalidMSISDN()throws SQLException,ParseException
    {
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent02");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID,data[0]);
        apiData.put(C2CConsentAPI.LOGINID,"");
        apiData.put(C2CConsentAPI.PASSWORD,"");
        apiData.put(C2CConsentAPI.MSISDN, RandomGeneration.randomNumeric(13));
        apiData.put(C2CConsentAPI.EXTCODE,"");
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    @ Test
    public void TC3_Negative_BlankMSISDN()throws SQLException,ParseException
    {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent03");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID,data[0]);
        apiData.put(C2CConsentAPI.MSISDN, "");
        apiData.put(C2CConsentAPI.EXTCODE,"");
        apiData.put(C2CConsentAPI.LOGINID,"");
        apiData.put(C2CConsentAPI.PASSWORD,"");
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    @ Test
    public void TC4_Negative_invalidPIN()throws SQLException,ParseException
    {
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent04");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID,data[0]);
        apiData.put(C2CConsentAPI.PIN, RandomGeneration.randomNumeric(4));
        apiData.put(C2CConsentAPI.EXTCODE,"");
        apiData.put(C2CConsentAPI.LOGINID,"");
        apiData.put(C2CConsentAPI.PASSWORD,"");
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    @ Test
    public void TC5_Negative_BlankPIN()throws SQLException,ParseException
    {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent05");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID,data[0]);
        apiData.put(C2CConsentAPI.PIN, "");
        apiData.put(C2CConsentAPI.EXTCODE,"");
        apiData.put(C2CConsentAPI.LOGINID,"");
        apiData.put(C2CConsentAPI.PASSWORD,"");
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    @ Test
    public void TC6_Negative_invalidTXNID()throws SQLException,ParseException
    {
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent06");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID,"CT12334542");
        apiData.put(C2CConsentAPI.EXTCODE,"");
        apiData.put(C2CConsentAPI.LOGINID,"");
        apiData.put(C2CConsentAPI.PASSWORD,"");
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    @ Test
    public void TC7_Negative_blankTXNID()throws SQLException,ParseException
    {
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent07");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID,"");
        apiData.put(C2CConsentAPI.EXTCODE,"");
        apiData.put(C2CConsentAPI.LOGINID,"");
        apiData.put(C2CConsentAPI.PASSWORD,"");
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    @ Test
    public void TC8_NegativeSuspendedUser()throws SQLException,ParseException
    {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent08");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID,data[0]);
        apiData.put(C2CConsentAPI.LOGINID,"");
        apiData.put(C2CConsentAPI.PASSWORD,"");
        SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
        ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
        ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
        suspendCHNLUser.suspendChannelUser_MSISDN(apiData.get(C2CConsentAPI.MSISDN), "CASEA_CuspendedO2C");
        ExtentI.Markup(ExtentColor.TEAL, "Approving Channel User Suspend Request");
        suspendCHNLUser.approveCSuspendRequest_MSISDN(apiData.get(C2CConsentAPI.MSISDN), "CASEA_CuspendedO2C");
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
        resumeCHNLUser.resumeChannelUser_MSISDN(apiData.get(C2CConsentAPI.MSISDN), "Auto Resume Remarks");

    }
    @ Test
    public void TC9_Negative_invalidLoginID()throws SQLException,ParseException
    {
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent09");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID,data[0]);
        apiData.put(C2CConsentAPI.EXTCODE,"");
        apiData.put(C2CConsentAPI.LOGINID,"abc");
        apiData.put(C2CConsentAPI.MSISDN,"");
        apiData.put(C2CConsentAPI.PIN,"");
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    @ Test
    public void TC10_Negative_invalidPassword()throws SQLException,ParseException
    {
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent10");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID,data[0]);
        apiData.put(C2CConsentAPI.EXTCODE,"");
        apiData.put(C2CConsentAPI.MSISDN,"");
        apiData.put(C2CConsentAPI.PIN,"");
        apiData.put(C2CConsentAPI.PASSWORD,RandomGeneration.randomAlphaNumeric(8));
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    @ Test
    public void TC11_Negative_blankLoginID()throws SQLException,ParseException
    {
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent11");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.EXTCODE,"");
        apiData.put(C2CConsentAPI.LOGINID,"");
        apiData.put(C2CConsentAPI.MSISDN,"");
        apiData.put(C2CConsentAPI.PIN,"");
        apiData.put(C2CConsentAPI.TXNID,data[0]);
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    @ Test
    public void TC12_Negative_blankPassword()throws SQLException,ParseException
    {
        RandomGeneration RandomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWC2CConsent12");
        C2CConsentAPI C2CConsentAPI = new C2CConsentAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String[] data = EXTGW_C2CConsentDP.C2Ctransfer();
        HashMap<String, String> apiData = EXTGW_C2CConsentDP.getAPIdata();
        apiData.put(C2CConsentAPI.TXNID,data[0]);
        apiData.put(C2CConsentAPI.EXTCODE,"");
        apiData.put(C2CConsentAPI.PASSWORD,"");
        apiData.put(C2CConsentAPI.MSISDN,"");
        apiData.put(C2CConsentAPI.PIN,"");
        String API = C2CConsentAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(C2CConsentAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
}

