package com.apicontrollers.ussd.GMB;

import com.Features.LMB;
import com.apicontrollers.ussd.CreditTransfer.CreditTransferAPI;
import com.apicontrollers.ussd.CreditTransfer.CreditTransfer_DP;
import com.apicontrollers.ussd.resume.USSDRESUMEAPI;
import com.apicontrollers.ussd.suspend.USSDSUSPENDAPI;
import com.apicontrollers.ussd.suspend.USSDSUSPENDDP;
import com.apicontrollers.ussd.GMB.USSD_GMB_DP;
import com.apicontrollers.ussd.GMB.USSD_GMB_API;
import com.Features.BarUnbar;
import com.classes.BaseTest;
import com.apicontrollers.ussd.resume.USSDRESUMEDP;
import com.apicontrollers.ussd.resume.USSDRESUMEAPI;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import com.utils.GenerateMSISDN;
import com.utils.RandomGeneration;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_GMB extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";
    public String stat;
    public String MSISDN0;


    /**
     * @throws Exception
     * @testid EXTGWC2S01
     * Positive Test Case For TRFCATEGORY: PRC
     */
    @Test(priority=1)

    public void TC_A_PositiveEXTGW_GMB_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GMB1");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
        USSD_GMB_API GMB = new USSD_GMB_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

        }


        HashMap<String, String> dataMap = CreditTransfer_DP.getAPIdata();
        HashMap<String, String> apiData = USSD_GMB_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API0 = CreditTransAPI.prepareAPI(dataMap);
        String API = GMB.prepareAPI(apiData);
        String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API0);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
        XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
        stat = xmlPath0.get(CreditTransAPI.TXNSTATUS);
        int status = Integer.parseInt(stat);


        if (status == 200) {

            MSISDN0 = dataMap.get(CreditTransAPI.MSISDN1);
            apiData.put(GMB.MSISDN1, MSISDN0);

            HashMap<String, String> apiData1 = USSD_GMB_DP.getAPIdata();
            String API1 = GMB.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);

            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

            Validator.APIMultiErrorCodeComapre(xmlPath.get(GMB.TXNSTATUS).toString(), CaseMaster.getErrorCode());


        }

    }
        @Test(priority=2)
    public void TC_A_NegativeEXTGW_GMB_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GMB2");
        CreditTransferAPI CreditTransAPI = new CreditTransferAPI();
        USSD_GMB_API GMB = new USSD_GMB_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

        }
        HashMap<String, String> dataMap =USSD_GMB_DP.getAPIdata();
        dataMap.put(GMB.MSISDN1, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API1 = GMB.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);

        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(GMB.TXNSTATUS).toString(), CaseMaster.getErrorCode());


    }


    @Test(priority=3)
    public void TC_B_NegativeEXTGW_GMB_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GMB3");
        USSD_GMB_API GMB = new USSD_GMB_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

        }
        HashMap<String, String> dataMap = USSD_GMB_DP.getAPIdata();
        dataMap.put(GMB.MSISDN2, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API1 = GMB.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);

        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(GMB.TXNSTATUS).toString(), CaseMaster.getErrorCode());


    }


    @Test(priority=4)
    public void TC_C_NegativeEXTGW_GMB_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GMB4");
        USSD_GMB_API GMB = new USSD_GMB_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

        }
        HashMap<String, String> dataMap = USSD_GMB_DP.getAPIdata();
        dataMap.put(GMB.AMOUNT, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API1 = GMB.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);

        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(GMB.TXNSTATUS).toString(), CaseMaster.getErrorCode());


    }


    @Test(priority=5)
    public void TC_D_NegativeEXTGW_GMB_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GMB5");
        USSD_GMB_API GMB = new USSD_GMB_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

        }
        HashMap<String, String> dataMap = USSD_GMB_DP.getAPIdata();
        dataMap.put(GMB.LANGUAGE1, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API1 = GMB.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);

        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(GMB.TXNSTATUS).toString(), CaseMaster.getErrorCode());


    }


    @Test(priority=6)
    public void TC_E_NegativeEXTGW_GMB_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GMB6");
        USSD_GMB_API GMB = new USSD_GMB_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

        }
        HashMap<String, String> dataMap = USSD_GMB_DP.getAPIdata();
        dataMap.put(GMB.LANGUAGE2, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API1 = GMB.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);

        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(GMB.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }


    @Test(priority=7)
    public void TC_F_NegativeEXTGW_GMB_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GMB7");
        USSD_GMB_API GMB = new USSD_GMB_API();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

        }
        HashMap<String, String> dataMap = USSD_GMB_DP.getAPIdata();
        String MSISDN1 = RandomGeneration.randomAlphabets(2) + RandomGeneration.randomNumeric(8);
        dataMap.put(GMB.MSISDN1, MSISDN1);

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API1 = GMB.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);

        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(GMB.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test(priority=8)
    public void TC_G_NegativeEXTGW_GMB_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GMB8");
        USSD_GMB_API GMB = new USSD_GMB_API();
        RandomGeneration RandomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

        }
        HashMap<String, String> dataMap = USSD_GMB_DP.getAPIdata();
        String MSISDN2 = RandomGeneration.randomAlphabets(2) + RandomGeneration.randomNumeric(8);
        dataMap.put(GMB.MSISDN2, MSISDN2);

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API1 = GMB.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);

        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(GMB.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test(priority=9)
    public void TC_H_NegativeEXTGW_GMB_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GMB9");
        USSDSUSPENDAPI USSDSUSPENDAPI = new USSDSUSPENDAPI();
        USSDRESUMEAPI RESUMEAPI = new USSDRESUMEAPI();
        USSD_GMB_API GMB = new USSD_GMB_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

        }


        HashMap<String, String> dataMap = USSDSUSPENDDP.getAPIdata();
        HashMap<String, String> apiData = USSD_GMB_DP.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API0 = USSDSUSPENDAPI.prepareAPI(dataMap);
        String API = GMB.prepareAPI(apiData);
        String[] APIResponse0 = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API0);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse0);
        XmlPath xmlPath0 = new XmlPath(CompatibilityMode.HTML, APIResponse0[1]);
        stat = xmlPath0.get(USSDSUSPENDAPI.TXNSTATUS);
        int status = Integer.parseInt(stat);

        if (status == 200) {

            MSISDN0 = dataMap.get(USSDSUSPENDAPI.MSISDN1);
            apiData.put(GMB.MSISDN1, MSISDN0);

            HashMap<String, String> apiData1 = USSD_GMB_DP.getAPIdata();
            String API1 = GMB.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);

            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

            Validator.APIMultiErrorCodeComapre(xmlPath.get(GMB.TXNSTATUS).toString(), CaseMaster.getErrorCode());


        }

        HashMap<String, String> apiData2 = USSDRESUMEDP.getAPIdata();
        String API2 = RESUMEAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API2);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);



    }


    @Test(priority=10)
    public void TC_I_NegativeEXTGW_GMB_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GMB10");
        USSD_GMB_API GMB = new USSD_GMB_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

        }
        HashMap<String, String> dataMap = USSD_GMB_DP.getAPIdata();
        MSISDN0 = dataMap.get(GMB.MSISDN1);
        dataMap.put(GMB.MSISDN2,MSISDN0);

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API1 = GMB.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API1);

        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(GMB.TXNSTATUS).toString(), CaseMaster.getErrorCode());


    }

}