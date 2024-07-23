package com.apicontrollers.extgw.Loan;

import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.apicontrollers.extgw.changePIN_EXC2SCPNREQ.EXTGWCHANGEPINAPI;
import com.apicontrollers.extgw.changePIN_EXC2SCPNREQ.EXTGWCHANGEPINDP;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import io.restassured.path.xml.XmlPath;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

public class EXTGW_OptIn extends BaseTest {



        public static boolean TestCaseCounter = false;
        private final String extentCategory = "API";

        /**
         * @throws ParseException
         * @throws SQLException
         *  EXTGWCHGPIN01
         * Positive Test Case For Change PIN
         */
       @Test
        public void _001_Optin() throws SQLException, ParseException {

            CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTIN1");
            OptInAPI optInAPI = new OptInAPI();

            if (TestCaseCounter == false) {
                test = extent.createTest(CaseMaster.getModuleCode());
                TestCaseCounter = true;
            }

            currentNode = test.createNode(CaseMaster.getDescription());
            currentNode.assignCategory(extentCategory);

            HashMap<String, String> apiData = OptInDP.getAPIdata();
            String API = optInAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
            Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }



    @Test
    public void _002_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTIN2");
        OptInAPI optInAPI = new OptInAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();

        apiData.put(optInAPI.MSISDN, "");
        apiData.put(optInAPI.PIN, "");
        String API = optInAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void _003_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTIN3");
        OptInAPI optInAPI = new OptInAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();

        apiData.put(optInAPI.MSISDN, "");
        apiData.put(optInAPI.PIN, "");
        apiData.put(optInAPI.LOGINID, "");
        apiData.put(optInAPI.PASSWORD, "");

        String API = optInAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void _004_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTIN4");
        OptInAPI optInAPI = new OptInAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();

        apiData.put(optInAPI.EXTCODE, "");
        apiData.put(optInAPI.PIN, "");
        apiData.put(optInAPI.LOGINID, "");
        apiData.put(optInAPI.PASSWORD, "");

        String API = optInAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void _005_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTIN5");
        OptInAPI optInAPI = new OptInAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();

        apiData.put(optInAPI.MSISDN, "");
        apiData.put(optInAPI.PIN, "");
        apiData.put(optInAPI.PASSWORD, "");
        apiData.put(optInAPI.EXTCODE, "");

        String API = optInAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void _006_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTIN6");
        OptInAPI optInAPI = new OptInAPI();
        SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
        ResumeChannelUser CUResume = new ResumeChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();

        CUSuspend.suspendChannelUser_MSISDN(apiData.get(optInAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");
        CUSuspend.approveCSuspendRequest_MSISDN(apiData.get(optInAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");


        String API = optInAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);

        CUResume.resumeChannelUser_MSISDN(apiData.get(optInAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void _007_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTIN7");
        OptInAPI optInAPI = new OptInAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();
        apiData.put(optInAPI.MSISDN, "abc313");

        String API = optInAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void _008_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTIN8");
        OptInAPI optInAPI = new OptInAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();
        apiData.put(optInAPI.LOGINID, "abc313");
        String API = optInAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }





}
