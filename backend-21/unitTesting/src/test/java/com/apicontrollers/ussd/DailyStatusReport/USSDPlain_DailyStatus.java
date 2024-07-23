package com.apicontrollers.ussd.DailyStatusReport;

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

public class USSDPlain_DailyStatus extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void TC1_PositiveSNLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDSR01");
        USSDPlain_DailyStatus_API dailyStatusAPI = new USSDPlain_DailyStatus_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_DailyReport_DP.getAPIdata();

        String API = dailyStatusAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC2_NegativeSNLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDSR02");
        USSDPlain_DailyStatus_API dailyStatusAPI = new USSDPlain_DailyStatus_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_DailyReport_DP.getAPIdata();
        apiData.put(dailyStatusAPI.PIN, "");
        String API = dailyStatusAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC3_PositiveSNLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDSR03");
        USSDPlain_DailyStatus_API dailyStatusAPI = new USSDPlain_DailyStatus_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_DailyReport_DP.getAPIdata();

        String API = dailyStatusAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC4_PositiveSNLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDSR04");
        USSDPlain_DailyStatus_API dailyStatusAPI = new USSDPlain_DailyStatus_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_DailyReport_DP.getAPIdata();

        String API = dailyStatusAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC5_PositiveSNLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDSR05");
        USSDPlain_DailyStatus_API dailyStatusAPI = new USSDPlain_DailyStatus_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_DailyReport_DP.getAPIdata();

        String API = dailyStatusAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC6_NegativeSNLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDSR06");
        USSDPlain_DailyStatus_API dailyStatusAPI = new USSDPlain_DailyStatus_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_DailyReport_DP.getAPIdata();
        apiData.put(dailyStatusAPI.MSISDN1, "");
        String API = dailyStatusAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC7_NegativeSNLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDSR07");
        USSDPlain_DailyStatus_API dailyStatusAPI = new USSDPlain_DailyStatus_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_DailyReport_DP.getAPIdata();
        apiData.put(dailyStatusAPI.MSISDN1, randomGeneration.randomNumeric(20));
        String API = dailyStatusAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC8_NegativeSNLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDSR08");
        USSDPlain_DailyStatus_API dailyStatusAPI = new USSDPlain_DailyStatus_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_DailyReport_DP.getAPIdata();
        apiData.put(dailyStatusAPI.PIN, randomGeneration.randomNumeric(4));
        String API = dailyStatusAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC9_NegativeSNLAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDSR09");
        USSDPlain_DailyStatus_API dailyStatusAPI = new USSDPlain_DailyStatus_API();
        RandomGeneration randomGeneration = new RandomGeneration();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_DailyReport_DP.getAPIdata();
        apiData.put(dailyStatusAPI.MSISDN1, randomGeneration.randomNumeric(10));
        String API = dailyStatusAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }
}
