package com.apicontrollers.ussd.resume;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_RESUME extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void _001_resumeAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRESREQ01");
        USSDRESUMEAPI suspendAPI = new USSDRESUMEAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = USSDRESUMEDP.getAPIdata();
        apiData.put(suspendAPI.PIN, new RandomGeneration().randomNumberWithoutZero(5));
        String API = suspendAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void _002_resumeAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRESREQ02");
        USSDRESUMEAPI suspendAPI = new USSDRESUMEAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = USSDRESUMEDP.getAPIdata();
        apiData.put(suspendAPI.PIN, "");
        String API = suspendAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void _003_resumeAPI() {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRESREQ03");
        USSDRESUMEAPI suspendAPI = new USSDRESUMEAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = USSDRESUMEDP.getAPIdata();
        String MSISDN = UniqueChecker.UC_MSISDN();
        apiData.put(suspendAPI.MSISDN1, MSISDN);
        String API = suspendAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void _004_resumeAPI() throws SQLException, ParseException, IOException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDRESREQ04");
        USSDRESUMEAPI suspendAPI = new USSDRESUMEAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = USSDRESUMEDP.getAPIdata();
        String API = suspendAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(USSDRESUMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

     /*   String SuspendApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_SUS_APP").toUpperCase();
        SuspendChannelUser suspendChnluser = new SuspendChannelUser(driver);
        if (SuspendApprovalReq.equals("TRUE")) {
            currentNode = test.createNode("To verify that operator user is able to approve suspend channel user request using LoginID.");
            currentNode.assignCategory(extentCategory);
            int rownum = ExcelUtility.searchStringRowNum(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, apiData.get(suspendAPI.MSISDN1));
            ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            String actualMessage = suspendChnluser.approveCSuspendRequest_LoginID(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, rownum), "Suspend Request Approved");

            //Message Validation
            currentNode = test.createNode("To verify that proper message appear on Web after approving suspend channel user request.");
            currentNode.assignCategory(extentCategory);
            String expectedMessage = MessagesDAO.prepareMessageByKey("user.viewdsapprovalusersview.suspendsuccessmessage", "");
            suspendChnluser.messageCompare(actualMessage, expectedMessage);
        }
*/
    }
}