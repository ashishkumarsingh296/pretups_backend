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

public class USSDPlain_C2SLastTransfer extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void TC1_PositiveAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDC2SLT01");
        USSDPlain_C2SLT_API c2sLastTransfer = new USSDPlain_C2SLT_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_C2SLT_DP.getAPIdata();

        String API = c2sLastTransfer.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC2_NegativeAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDC2SLT02");
        USSDPlain_C2SLT_API c2sLastTransfer = new USSDPlain_C2SLT_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_C2SLT_DP.getAPIdata();
        apiData.put(c2sLastTransfer.PIN, "");
        String API = c2sLastTransfer.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC3_NegativeAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDC2SLT03");
        USSDPlain_C2SLT_API c2sLastTransfer = new USSDPlain_C2SLT_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_C2SLT_DP.getAPIdata();
        apiData.put(c2sLastTransfer.PIN, "0000");
        String API = c2sLastTransfer.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC4_NegativeAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDC2SLT04");
        USSDPlain_C2SLT_API c2sLastTransfer = new USSDPlain_C2SLT_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_C2SLT_DP.getAPIdata();
        apiData.put(c2sLastTransfer.MSISDN1, "");
        String API = c2sLastTransfer.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }

    @Test
    public void TC5_NegativeAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDC2SLT05");
        USSDPlain_C2SLT_API c2sLastTransfer = new USSDPlain_C2SLT_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_C2SLT_DP.getAPIdata();
        apiData.put(c2sLastTransfer.PIN, randomGeneration.randomNumeric(4));
        String API = c2sLastTransfer.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());

    }
}
