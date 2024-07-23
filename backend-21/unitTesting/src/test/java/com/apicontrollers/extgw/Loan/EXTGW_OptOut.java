package com.apicontrollers.extgw.Loan;

import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
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

public class EXTGW_OptOut extends BaseTest {


    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";



    @Test
    public void _001_Optout() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTOUT1");
        OptOutAPI optOutAPI = new OptOutAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();
        String API = optOutAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }




    @Test
    public void _002_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTOUT2");
        OptOutAPI optOutAPI = new OptOutAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();

        apiData.put(optOutAPI.MSISDN, "");
        apiData.put(optOutAPI.PIN, "");
        String API = optOutAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void _003_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTOUT3");
        OptOutAPI optOutAPI = new OptOutAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();

        apiData.put(optOutAPI.MSISDN, "");
        apiData.put(optOutAPI.PIN, "");
        apiData.put(optOutAPI.LOGINID, "");
        apiData.put(optOutAPI.PASSWORD, "");

        String API = optOutAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void _004_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTOUT4");
        OptOutAPI optOutAPI = new OptOutAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();

        apiData.put(optOutAPI.PIN, "");
        apiData.put(optOutAPI.EXTCODE, "");
        apiData.put(optOutAPI.LOGINID, "");
        apiData.put(optOutAPI.PASSWORD, "");

        String API = optOutAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void _005_Optin() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTOUT5");
        OptOutAPI optOutAPI = new OptOutAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();

        apiData.put(optOutAPI.MSISDN, "");
        apiData.put(optOutAPI.PIN, "");
        apiData.put(optOutAPI.PASSWORD, "");
        apiData.put(optOutAPI.EXTCODE, "");

        String API = optOutAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }



    @Test
    public void _006_Optout() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTOUT6");
        OptOutAPI optOutAPI = new OptOutAPI();
        SuspendChannelUser CUSuspend = new SuspendChannelUser(driver);
        ResumeChannelUser CUResume = new ResumeChannelUser(driver);

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();

        CUSuspend.suspendChannelUser_MSISDN(apiData.get(optOutAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");
        CUSuspend.approveCSuspendRequest_MSISDN(apiData.get(optOutAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");


        String API = optOutAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        CUResume.resumeChannelUser_MSISDN(apiData.get(optOutAPI.MSISDN), "Automated EXTGW O2C API Testing: EXTGWO2C09");
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }



    @Test
    public void _007_Optout() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTOUT7");
        OptOutAPI optOutAPI = new OptOutAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();
        apiData.put(optOutAPI.MSISDN, "abc313");
        String API = optOutAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }


    @Test
    public void _008_Optout() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWOPTOUT8");
        OptOutAPI optOutAPI = new OptOutAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);

        HashMap<String, String> apiData = OptInDP.getAPIdata();
        apiData.put(optOutAPI.LOGINID, "abc313");
        String API = optOutAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(OptInAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

}


