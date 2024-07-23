package com.apicontrollers.extgw.ViewChannelUser;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.*;
import io.restassured.path.xml.XmlPath;
import org.testng.annotations.Test;

import java.util.HashMap;

public class XMLGW_VIEWCUSERTEST extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";
    
    @Test
    public void TC_01_PositiveViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER01");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_02_PositiveViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER02");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.DATE,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_03_NegativeViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER03");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.DATE,"assfgh");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_04_NegativeViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER04");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.EXTNWCODE,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_05_NegativeViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER05");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.EXTNWCODE,"SC");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_06_NegativeViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER06");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.EMPCODE,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_07_NegativeViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER07");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.EMPCODE,"SC");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_08_NegativeViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER08");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.LOGINID,"");
        dataMap.put(XMLGW_VIEWCUSERAPI.EMPCODE,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_09_NegativeViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER09");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.LOGINID,"sdfdg");
        dataMap.put(XMLGW_VIEWCUSERAPI.EMPCODE,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_10_NegativeViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER10");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.PASSWORD,"");
        dataMap.put(XMLGW_VIEWCUSERAPI.EMPCODE,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_11_NegativeViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER11");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.PASSWORD,"sdfdg");
        dataMap.put(XMLGW_VIEWCUSERAPI.EMPCODE,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_12_PositiveViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER12");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.USERLOGINID,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_13_NegativeViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER13");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.USERLOGINID,"sdfdg");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_14_PositiveViewChannelUserAPI() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER14");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.MSISDN,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_15_NegativeViewChannelUserAPI() throws Exception {
        RandomGeneration randomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER15");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.MSISDN,randomGeneration.randomNumeric(10));
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.APIMultiErrorCodeComapre(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_16_NegativeViewChannelUserAPI() throws Exception {
        RandomGeneration randomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER16");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCounter = ExcelUtility.getRowCount();
        for (int k = 0; k <= rowCounter; k++) {
            String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k);
            String UserCategory = "NWADM";

            if (excelCategory.equals(UserCategory)) {
                dataMap.put(XMLGW_VIEWCUSERAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k));
                dataMap.put(XMLGW_VIEWCUSERAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, k)));
                dataMap.put(XMLGW_VIEWCUSERAPI.CATCODE, ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k));
                dataMap.put(XMLGW_VIEWCUSERAPI.EMPCODE, DBHandler.AccessHandler.getEmpCode(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k)));
                break;
            }
        }
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_17_NegativeViewChannelUserAPI() throws Exception {
        RandomGeneration randomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER17");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCounter = ExcelUtility.getRowCount();
        for (int k = 0; k <= rowCounter; k++) {
            String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k);
            String UserCategory = "SUADM";

            if (excelCategory.equals(UserCategory)) {
                dataMap.put(XMLGW_VIEWCUSERAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k));
                dataMap.put(XMLGW_VIEWCUSERAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, k)));
                dataMap.put(XMLGW_VIEWCUSERAPI.CATCODE, ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k));
                dataMap.put(XMLGW_VIEWCUSERAPI.EMPCODE, DBHandler.AccessHandler.getEmpCode(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k)));
                break;
            }
        }
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_18_NegativeViewChannelUserAPI() throws Exception {
        RandomGeneration randomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER18");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(XMLGW_VIEWCUSERAPI.CATCODE,"");
        dataMap.put(XMLGW_VIEWCUSERAPI.LOGINID,"");
        dataMap.put(XMLGW_VIEWCUSERAPI.PASSWORD,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_19_NegativeViewChannelUserAPI() throws Exception {
        RandomGeneration randomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER19");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCounter = ExcelUtility.getRowCount();
        for (int k = 0; k <= rowCounter; k++) {
            String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k);
            String UserCategory = "CCE";

            if (excelCategory.equals(UserCategory)) {
                dataMap.put(XMLGW_VIEWCUSERAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k));
                dataMap.put(XMLGW_VIEWCUSERAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, k)));
                dataMap.put(XMLGW_VIEWCUSERAPI.CATCODE, ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k));
                dataMap.put(XMLGW_VIEWCUSERAPI.EMPCODE, DBHandler.AccessHandler.getEmpCode(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k)));
                break;
            }
        }
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
    @Test
    public void TC_20_NegativeViewChannelUserAPI() throws Exception {
        RandomGeneration randomGeneration = new RandomGeneration();
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("XMLGWVIEWCUSER20");
        XMLGW_VIEWCUSERDP viewcuserdp = new XMLGW_VIEWCUSERDP();
        XMLGW_VIEWCUSERAPI viewcuserapi = new XMLGW_VIEWCUSERAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        HashMap<String, String> dataMap = viewcuserdp.getAPIdata();
        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCounter = ExcelUtility.getRowCount();
        for (int k = 0; k <= rowCounter; k++) {
            String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k);
            String UserCategory = "CCE";

            if (excelCategory.equals(UserCategory)) {
                dataMap.put(XMLGW_VIEWCUSERAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k));
                dataMap.put(XMLGW_VIEWCUSERAPI.PASSWORD, (ExcelUtility.getCellData(0, ExcelI.PASSWORD, k)));
                dataMap.put(XMLGW_VIEWCUSERAPI.CATCODE, ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, k));
                dataMap.put(XMLGW_VIEWCUSERAPI.EMPCODE, DBHandler.AccessHandler.getEmpCode(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, k)));
                break;
            }
        }
        dataMap.put(XMLGW_VIEWCUSERAPI.EXTREFNUM,"");
        String API = viewcuserapi.prepareAPI(dataMap);
        System.out.println(API);
        String[] APIResponse = _APIUtil.executeAPI(GatewayI.XMLGW, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
        Validator.messageCompare(xmlPath.get(viewcuserapi.TXNSTATUS).toString(), CaseMaster.getErrorCode());

    }
}

