package com.apicontrollers.ussd.BalanceEnquiry;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

public class USSDPlain_BalanceEnquiry extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void TC1_PositiveBEAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDBE01");
        USSDPlain_BEAPI balanceEnquiryAPI = new USSDPlain_BEAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_BEDP.getAPIdata();

        String API = balanceEnquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC2_NegativeBEAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDBE02");
        USSDPlain_BEAPI balanceEnquiryAPI = new USSDPlain_BEAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_BEDP.getAPIdata();
        apiData.put(balanceEnquiryAPI.MSISDN1, "");
        
        String API = balanceEnquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC3_PositiveBEAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDBE03");
        USSDPlain_BEAPI balanceEnquiryAPI = new USSDPlain_BEAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_BEDP.getAPIdata();
        apiData.put(balanceEnquiryAPI.PIN, "");
        String API = balanceEnquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC4_NegativeBEAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDBE04");
        USSDPlain_BEAPI balanceEnquiryAPI = new USSDPlain_BEAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_BEDP.getAPIdata();
        apiData.put(balanceEnquiryAPI.MSISDN1, GenerateMSISDN.generateRandomMSISDNWithinNetwork(PretupsI.PREPAID_LOOKUP));
        String API = balanceEnquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC5_NegativeBEAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDBE05");
        USSDPlain_BEAPI balanceEnquiryAPI = new USSDPlain_BEAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_BEDP.getAPIdata();
        apiData.put(balanceEnquiryAPI.PIN, RandomGeneration.randomNumeric(4));
        String API = balanceEnquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC6_NegativeBEAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDBE06");
        USSDPlain_BEAPI balanceEnquiryAPI = new USSDPlain_BEAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSDPlain_BEDP.getAPIdata();
        apiData.put(balanceEnquiryAPI.MSISDN1, "");
        apiData.put(balanceEnquiryAPI.PIN, "");
        String API = balanceEnquiryAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }
}
