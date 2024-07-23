package com.apicontrollers.ussd.EVD;

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

public class USSD_EVD extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void TC1_PositiveEVDAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD01");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();

        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC2_NegativeEVDAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD02");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.PIN, "");
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC3_NegativeEVDAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD03");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.PIN, randomGeneration.randomNumeric(4));
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC4_NegativeEVDAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD04");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.MSISDN2, "");
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC5_NegativeEVDAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD05");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.AMOUNT, "");
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC6_NegativeEVDAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD06");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.SELECTOR, "");
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC7_NegativeEVDAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD07");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.AMOUNT, "-100");
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC8_NegativeEVDAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD08");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.LANGUAGE1, "-100");
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC9_NegativeAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD09");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.LANGUAGE2, "-100");
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCJ10_NegativeAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD10");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.SELECTOR, "-1");
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TCK11_NegativeAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD11");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.SELECTOR, randomGeneration.randomNumeric(3));
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TCL12_NegativeAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD12");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.LANGUAGE1, randomGeneration.randomNumeric(4));
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TCM13_NegativeAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD13");
        USSD_EVD_API EVDAPI = new USSD_EVD_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getDescription());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
        apiData.put(EVDAPI.LANGUAGE2, randomGeneration.randomNumeric(4));
        String API = EVDAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
	
	/*@Test
	public void TC8_NegativeEVDAPI() throws SQLException, ParseException, InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVD08");
		USSD_EVD_API EVDAPI = new USSD_EVD_API();
		ChannelUser ChannelUser = new ChannelUser(driver);
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_EVD_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(EVDAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(USSD_EVD_DP.TO_Category, channelMap);
		
		String API = EVDAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(USSD_TRANSFER_DP.TO_Category, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EVDAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}*/

}
