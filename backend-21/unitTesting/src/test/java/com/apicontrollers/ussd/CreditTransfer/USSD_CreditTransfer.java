package com.apicontrollers.ussd.CreditTransfer;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.BarUnbar;
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


public class USSD_CreditTransfer extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    /**
     * @throws Exception
     * @testid USSDC2S01
     * Positive Test Case For TRFCATEGORY: PRC
     */

    @Test
    public void TC_A_PositiveCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC01");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void TC_B_PositiveCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC02");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.AMOUNT, "");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void TC_C_PositiveCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC03");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.PIN, "");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void TC_D_PositiveCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC04");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.MSISDN1, "");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_E_PositiveCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC05");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.MSISDN2, "");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_F_NegativeCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC06");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.AMOUNT, "s1");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_G_NegativeCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC07");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.AMOUNT, "-1");
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_H_NegativeCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC08");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.MSISDN1, RandomGeneration.randomAlphaNumeric(9));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_I_NegativeCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC09");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.MSISDN2, RandomGeneration.randomAlphaNumeric(9));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_J_NegativeCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC10");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String CorrectPin = dataMap.get(CreditTransAPI.PIN);

        String InValPin;

        do {
            InValPin = RandomGeneration.randomNumeric(4);

        } while (CorrectPin == InValPin);

        dataMap.put(CreditTransAPI.PIN, InValPin);

        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_K_NegativeCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC11");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.LANGUAGE1, RandomGeneration.randomAlphaNumeric(5));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_L_NegativeCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC12");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.LANGUAGE2, RandomGeneration.randomAlphaNumeric(4));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_M_NegativeCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC13");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.SELECTOR, RandomGeneration.randomAlphaNumeric(4));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC_N_NegativeCreditTransferAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC14");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(CreditTransAPI.INFO1, RandomGeneration.randomAlphaNumeric(4));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    //To Verify that CP2P transaction is not successful 
//    (Pre-paid to Pre-paid) through USSD if Sender is barred

    @Test
    public void  TC_O_NegativeCreditTransferAPI() throws Exception {

       CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC15");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
       if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
       currentNode = test.createNode(CaseMaster.getExtentCase());
       currentNode.assignCategory(extentCategory);
       BarUnbar BarUnbar = new BarUnbar(driver);
        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();
        HashMap<String, String> preapiData1 =new HashMap<String, String>();
    	BarUnbar.barringUser("P2P", "SENDER", dataMap.get("MSISDN1"));
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
     _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        BarUnbar.unBarringUser("P2P", "SENDER", dataMap.get("MSISDN1"));
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }
    //To Verify that CP2P transaction is not successful 
  //  (Pre-paid to Pre-paid) through USSD if receiver is barred
   @Test
    public void  TC_P_NegativeCreditTransferAPI() throws Exception {

       CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDPRC16");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
        BarUnbar BarUnbar = new BarUnbar(driver);
       if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
       currentNode = test.createNode(CaseMaster.getExtentCase());
       currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();
    	BarUnbar.barringUser("P2P", "RECEIVER", dataMap.get("MSISDN2"));
     
        String API = CreditTransAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        BarUnbar.unBarringUser("P2P", "RECEIVER", dataMap.get("MSISDN2"));
        Validator.messageCompare(xmlPath.get(CreditTransAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    
    
    

}
