package com.apicontrollers.ussd.C2SLastXTransferReport;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_C2SLastTransfer extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void TC1_PositiveAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDC2SLT01");
        USSD_C2SLT_API c2sLastTransfer = new USSD_C2SLT_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_C2SLT_DP.getAPIdata();

        String API = c2sLastTransfer.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(c2sLastTransfer.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC2_NegativeAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDC2SLT02");
        USSD_C2SLT_API c2sLastTransfer = new USSD_C2SLT_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_C2SLT_DP.getAPIdata();
        apiData.put(c2sLastTransfer.PIN, "");
        String API = c2sLastTransfer.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(c2sLastTransfer.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC3_NegativeAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDC2SLT03");
        USSD_C2SLT_API c2sLastTransfer = new USSD_C2SLT_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_C2SLT_DP.getAPIdata();
        apiData.put(c2sLastTransfer.PIN, "0000");
        String API = c2sLastTransfer.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(c2sLastTransfer.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC4_NegativeAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDC2SLT04");
        USSD_C2SLT_API c2sLastTransfer = new USSD_C2SLT_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_C2SLT_DP.getAPIdata();
        apiData.put(c2sLastTransfer.MSISDN1, "");
        String API = c2sLastTransfer.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(c2sLastTransfer.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC5_NegativeAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDC2SLT05");
        USSD_C2SLT_API c2sLastTransfer = new USSD_C2SLT_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_C2SLT_DP.getAPIdata();
        apiData.put(c2sLastTransfer.PIN, randomGeneration.randomNumeric(4));
        String API = c2sLastTransfer.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(c2sLastTransfer.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
}
