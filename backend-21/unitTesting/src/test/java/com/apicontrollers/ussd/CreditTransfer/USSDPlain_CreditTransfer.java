package com.apicontrollers.ussd.CreditTransfer;

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


public class USSDPlain_CreditTransfer extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    /**
     * @throws Exception
     * @testid USSDC2S01
     * Positive Test Case For TRFCATEGORY: PRC
     */

    @Test
    public void TC_A_PositiveUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC01");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_B_PositiveUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC02");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.AMOUNT, "");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_C_PositiveUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC03");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.PIN, "");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_D_PositiveUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC04");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.MSISDN1, "");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC_E_PositiveUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC05");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.MSISDN2, "");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC_F_NegativeUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC06");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.AMOUNT, "s1");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC_G_NegativeUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC07");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.AMOUNT, "-1");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC_H_NegativeUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC08");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.MSISDN1, RandomGeneration.randomAlphaNumeric(9));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC_I_NegativeUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC09");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.MSISDN2, RandomGeneration.randomAlphaNumeric(9));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC_J_NegativeUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC10");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String CorrectPin = dataMap.get(CreditTransAPI.PIN);

        String InValPin;

        do {
            InValPin = RandomGeneration.randomNumeric(4);

        } while (CorrectPin == InValPin);

        dataMap.put(CreditTransAPI.PIN, InValPin);

        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC_K_NegativeUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC11");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.LANGUAGE1, RandomGeneration.randomAlphaNumeric(5));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC_L_NegativeUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC12");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.LANGUAGE2, RandomGeneration.randomAlphaNumeric(4));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC_M_NegativeUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC13");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.SELECTOR, RandomGeneration.randomAlphaNumeric(4));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void TC_N_NegativeUSSDPlainCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC14");
        USSDPlainCreditTransferAPI CreditTransAPI = new USSDPlainCreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainCreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.INFO1, RandomGeneration.randomAlphaNumeric(4));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


}
