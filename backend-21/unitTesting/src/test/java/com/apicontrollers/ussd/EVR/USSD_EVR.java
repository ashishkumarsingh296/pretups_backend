package com.apicontrollers.ussd.EVR;

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

public class USSD_EVR extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    @Test
    public void TC1_PositiveEVRAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR01");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();

        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC2_NegativeEVRAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR02");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.PIN, "");
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC3_NegativeEVRAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR03");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.PIN, randomGeneration.randomNumeric(4));
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC4_NegativeEVRAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR04");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.MSISDN2, "");
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC5_NegativeEVRAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR05");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.AMOUNT, "");
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC6_NegativeEVRAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR06");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.SELECTOR, "");
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC7_NegativeEVRAPI() throws SQLException, ParseException {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR07");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.AMOUNT, "-100");
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.get(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TC8_NegativeEVRAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR08");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.LANGUAGE1, "-100");
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TC9_NegativeAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR09");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.LANGUAGE2, "-100");
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }

    @Test
    public void TCJ10_NegativeAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR10");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.SELECTOR, "-1");
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TCK11_NegativeAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR11");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.SELECTOR, randomGeneration.randomNumeric(3));
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TCL12_NegativeAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR12");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.LANGUAGE1, randomGeneration.randomNumeric(4));
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
    }

    @Test
    public void TCM13_NegativeAPI() throws SQLException, ParseException {
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR13");
        USSD_EVR_API EVRAPI = new USSD_EVR_API();
        RandomGeneration randomGeneration = new RandomGeneration();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getDescription());
            TestCaseCounter = true;
        }

        currentNode = test.createNode(CaseMaster.getDescription());
        currentNode.assignCategory(extentCategory);
        HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
        apiData.put(EVRAPI.LANGUAGE2, randomGeneration.randomNumeric(4));
        String API = EVRAPI.prepareAPI(apiData);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);

        Validator.APIMultiErrorCodeComapre(xmlPath.getString(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
	
	/*@Test
	public void TC8_NegativeEVRAPI() throws SQLException, ParseException, InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDEVR08");
		USSD_EVR_API EVRAPI = new USSD_EVR_API();
		ChannelUser ChannelUser = new ChannelUser(driver);
		
		if (TestCaseCounter == false) {
			test = extent.createTest(CaseMaster.getModuleCode());
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster.getDescription());
		currentNode.assignCategory(extentCategory);
		HashMap<String, String> apiData = USSD_EVR_DP.getAPIdata();
		
		HashMap<String, String> channelMap = new HashMap<String, String>();
		channelMap.put("searchMSISDN", apiData.get(EVRAPI.MSISDN1));
		channelMap.put("outSuspend_chk", "Y");
		ChannelUser.modifyChannelUserDetails(USSD_EVR_DP.TO_Category, channelMap);
		
		String API = EVRAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
		_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
		
		
		channelMap.put("outSuspend_chk", "N");
		ChannelUser.modifyChannelUserDetails(USSD_TRANSFER_DP.TO_Category, channelMap);
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
		Validator.messageCompare(xmlPath.get(EVRAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
		
	}*/


}
