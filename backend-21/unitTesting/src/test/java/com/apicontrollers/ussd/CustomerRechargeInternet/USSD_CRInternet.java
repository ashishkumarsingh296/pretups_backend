package com.apicontrollers.ussd.CustomerRechargeInternet;

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

public class USSD_CRInternet extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void TCA1_PositiveFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN01");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();

        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCB2_PositiveFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN02");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.LANGUAGE1, "");
        apiData.put(internetRecharge.LANGUAGE2, "");
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCC3_PositiveFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN03");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();

        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCD4_NegativeFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN04");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.MSISDN1, "");
        apiData.put(internetRecharge.MSISDN2, "");
        apiData.put(internetRecharge.NOTIFICATION_MSISDN, "");
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCE5_NegativeFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN05");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.PIN, "");
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCF6_NegativeFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN06");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.AMOUNT, "");
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCG7_PositiveFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN07");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.SELECTOR, "");
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCH8_NegativeFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN08");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.MSISDN2, "");
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCI9_NegativeFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN09");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.NOTIFICATION_MSISDN, "");
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCJ10_NegativeFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN10");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.AMOUNT, "-100");
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCK11_NegativeFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN11");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.SELECTOR, "-100");
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCL12_NegativeFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN12");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.MSISDN1, randomGeneration.randomNumeric(10));
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCL13_NegativeFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN13");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.NOTIFICATION_MSISDN, randomGeneration.randomNumeric(10));
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCL14_PositiveFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN14");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.PIN, randomGeneration.randomNumeric(4));
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCL15_NegativeFLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDIN15");
        USSD_CRInternet_API internetRecharge = new USSD_CRInternet_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_CRInternet_DP.getAPIdata();
        apiData.put(internetRecharge.SELECTOR, randomGeneration.randomNumeric(4));
        String API = internetRecharge.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(internetRecharge.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }


}
