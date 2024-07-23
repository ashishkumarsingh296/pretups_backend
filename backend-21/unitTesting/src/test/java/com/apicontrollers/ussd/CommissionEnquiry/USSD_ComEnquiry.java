package com.apicontrollers.ussd.CommissionEnquiry;


import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.apicontrollers.ussd.BalanceEnquiry.USSD_BEAPI;
import com.apicontrollers.ussd.BalanceEnquiry.USSD_BEDP;
import com.dbrepository.DBHandler;
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

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_ComEnquiry extends BaseTest{
    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test(priority=1)
    public void TC1_PositiveCEAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CE1");
        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;

            currentNode = test.createNode(CaseMaster.getDescription());
            currentNode.assignCategory(extentCategory);
            HashMap<String, String> apiData = USSD_ComEnquiry_DP.getAPIdata();
            String API = CE.prepareAPI(apiData);

            String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

            Validator.APIMultiErrorCodeComapre(xmlPath.get(CE.TXNSTATUS).toString(), CaseMaster.getErrorCode());


        }


    }


    @Test(priority=2)
    public void TC2_PositiveCEAPI() throws SQLException, ParseException {

        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();
            CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CE2");
        //    USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();

        RandomGeneration RandomGeneration = new RandomGeneration();
        GenerateMSISDN gnMsisdn = new GenerateMSISDN();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }


        HashMap<String, String> dataMap = USSD_ComEnquiry_DP.getAPIdata();
        String MSISDN1 = RandomGeneration.randomAlphabets(2) +RandomGeneration.randomNumeric(8);
      dataMap.put(CE.MSISDN1, MSISDN1);

        //dataMap.put(USSD_ComEnquiry_API.MSISDN1, "" + gnMsisdn.generateMSISDN());



        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = CE.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CE.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test(priority=3)
    public void TC3_PositiveCEAPI() throws SQLException, ParseException {

        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CE3");

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSD_ComEnquiry_DP.getAPIdata();
        dataMap.put(CE.MSISDN1, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = CE.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CE.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }


    @Test(priority=4)
    public void TC4_PositiveCEAPI() throws SQLException, ParseException {

        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CE4");

        RandomGeneration RandomGeneration = new RandomGeneration();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = USSD_ComEnquiry_DP.getAPIdata();
        String InValPin;

        InValPin = RandomGeneration.randomAlphaNumeric(4);
        dataMap.put(CE.PIN, InValPin);


        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = CE.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CE.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }



    @Test(priority=5)
    public void TC5_PositiveCEAPI() throws SQLException, ParseException {

        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CE5");

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSD_ComEnquiry_DP.getAPIdata();
        dataMap.put(CE.PIN, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = CE.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CE.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }


    @Test(priority=6)
    public void TC6_PositiveCEAPI() throws SQLException, ParseException {

        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CE6");

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSD_ComEnquiry_DP.getAPIdata();
        dataMap.put(CE.NO_OF_DAYS, "");

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = CE.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CE.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }



    @Test(priority=7)
    public void TC7_PositiveCEAPI() throws SQLException, ParseException {


        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CE7");


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }


        HashMap<String, String> dataMap = USSD_ComEnquiry_DP.getAPIdata();

        String no_of_days = dataMap.get(CE.NO_OF_DAYS);
        int n1=Integer.parseInt(no_of_days);
        int no= n1-5;
        String n2=Integer.toString(no);
        dataMap.put(CE.NO_OF_DAYS, n2);


     /*
        int n1;
      do {
          String no = RandomGeneration.randomNumeric(2);
           n1=Integer.parseInt(no);
      } while(n1<n2);


*/

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = CE.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CE.TXNSTATUS).toString(), CaseMaster.getErrorCode());


    }

    @Test(priority=8)
    public void TC8_PositiveCEAPI() throws SQLException, ParseException {

        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CE8");
        RandomGeneration RandomGeneration = new RandomGeneration();


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }


        String days=  RandomGeneration.randomAlphabets(2);
        HashMap<String, String> dataMap = USSD_ComEnquiry_DP.getAPIdata();
        dataMap.put(CE.NO_OF_DAYS, days);
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = CE.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CE.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }


    @Test(priority=9)
    public void TC9_PositiveCEAPI() throws SQLException, ParseException {

        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CE9");


        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }


        HashMap<String, String> dataMap = USSD_ComEnquiry_DP.getAPIdata();
        // String no = RandomGeneration.randomNumeric(2);
        String no_of_days = dataMap.get(CE.NO_OF_DAYS);
        int n1=Integer.parseInt(no_of_days);
        int no= n1+5;
        String n2=Integer.toString(no);
        dataMap.put(CE.NO_OF_DAYS, n2);


        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = CE.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CE.TXNSTATUS).toString(), CaseMaster.getErrorCode());


    }


    @Test(priority=10)
    public void TC10_PositiveCEAPI() throws SQLException, ParseException {

        USSD_ComEnquiry_API CE = new USSD_ComEnquiry_API();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CE10");

        SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
        ResumeChannelUser CUResume = new ResumeChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

       currentNode = test.createNode(CaseMaster.getExtentCase());
       currentNode.assignCategory(extentCategory);
        HashMap<String, String> dataMap = USSD_ComEnquiry_DP.getAPIdata();
        CUSuspend.suspendChannelUser_MSISDN(dataMap.get(CE.MSISDN1), "Automated commission enquiry API Testing: CE10");
        CUSuspend.approveCSuspendRequest_MSISDN(dataMap.get(CE.MSISDN1), "Automated commission enquiry API Testing: CE10");

      // currentNode = test.createNode(CaseMaster.getExtentCase());
     //   currentNode.assignCategory(extentCategory);
        String API = CE.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        CUResume.resumeChannelUser_MSISDN(dataMap.get(CE.MSISDN1), "Automated commission enquiry API Testing: CE10");


        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(CE.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }


    }









