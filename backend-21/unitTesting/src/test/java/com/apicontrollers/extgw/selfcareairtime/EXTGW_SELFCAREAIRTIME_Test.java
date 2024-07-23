package com.apicontrollers.extgw.selfcareairtime;


import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.*;
import io.restassured.path.xml.XmlPath;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class EXTGW_SELFCAREAIRTIME_Test extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";


    @Test
    public void TC_A_PositiveSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME01");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);
            for (int j = 0; j < paymentModes.size(); j++) {

                currentNode = test.createNode(CaseMaster.getExtentCase());
                currentNode.assignCategory(extentCategory);
                apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(j), CustomerRechargeCode));
                String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
                String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
                _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
                XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

                Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            }


        }
    }


    //EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi=new EXGTW_SELFCAREAIRTIMEAPI();

        /*
        EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();



        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGWC2SDP.getAPIdataWithAllUsers();


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_C2SDAO APIDAO = (EXTGW_C2SDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();


            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            String API = C2STransferAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);
            Validator.messageCompare(xmlPath.get(EXTGWC2SAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }
        */


    @Test
    public void TC_B_PositiveSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME02");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.EXTCODE, "");
            apiData.put(exgtw_selfcareairtimeapi.LOGINID, "");
            apiData.put(exgtw_selfcareairtimeapi.PASSWORD, "");

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }


    @Test
    public void TC_C_PositiveSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME03");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.EXTCODE, "");
            apiData.put(exgtw_selfcareairtimeapi.MSISDN, "");
            apiData.put(exgtw_selfcareairtimeapi.PIN, "");

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_D_PositiveSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME04");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.MSISDN, "");
            apiData.put(exgtw_selfcareairtimeapi.PIN, "");
            apiData.put(exgtw_selfcareairtimeapi.LOGINID, "");
            apiData.put(exgtw_selfcareairtimeapi.PASSWORD, "");

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_E_PositiveSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME05");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.LOGINID, "");
            apiData.put(exgtw_selfcareairtimeapi.PASSWORD, "");

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_F_PositiveSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME06");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.EXTCODE, "");

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_G_PositiveSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME07");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);


            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.MSISDN, "");
            apiData.put(exgtw_selfcareairtimeapi.PIN, "");

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_H_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME08");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.EXTCODE, "");
            apiData.put(exgtw_selfcareairtimeapi.PIN, "");
            apiData.put(exgtw_selfcareairtimeapi.LOGINID, "");
            apiData.put(exgtw_selfcareairtimeapi.PASSWORD, "");

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());


        }
    }

    @Test
    public void TC_I_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME09");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration RandomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.EXTCODE, "");
            apiData.put(exgtw_selfcareairtimeapi.LOGINID, "");
            apiData.put(exgtw_selfcareairtimeapi.PASSWORD, "");
            apiData.put(exgtw_selfcareairtimeapi.PIN, RandomGeneration.randomNumeric(4));

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());


        }
    }

    @Test
    public void TC_J_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME10");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.EXTCODE, "");
            apiData.put(exgtw_selfcareairtimeapi.MSISDN, "");
            apiData.put(exgtw_selfcareairtimeapi.PIN, "");
            apiData.put(exgtw_selfcareairtimeapi.PASSWORD, "");

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());


        }
    }

    @Test
    public void TC_K_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME11");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";
        RandomGeneration RandomGeneration = new RandomGeneration();


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);


            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.EXTCODE, "");
            apiData.put(exgtw_selfcareairtimeapi.MSISDN, "");
            apiData.put(exgtw_selfcareairtimeapi.PIN, "");
            apiData.put(exgtw_selfcareairtimeapi.PASSWORD, RandomGeneration.randomNumeric(8));

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());


        }
    }

    @Test
    public void TC_L_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME12");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.EXTCODE, "");
            apiData.put(exgtw_selfcareairtimeapi.LOGINID, "cvcvx");
            apiData.put(exgtw_selfcareairtimeapi.MSISDN, "");
            apiData.put(exgtw_selfcareairtimeapi.PIN, "");


            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());


        }
    }

    @Test
    public void TC_M_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME13");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";
        RandomGeneration randomGeneration = new RandomGeneration();


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.EXTCODE, "");
            apiData.put(exgtw_selfcareairtimeapi.LOGINID, "");
            apiData.put(exgtw_selfcareairtimeapi.PASSWORD, "");
            apiData.put(exgtw_selfcareairtimeapi.MSISDN, randomGeneration.randomNumeric(10));
            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.APIMultiErrorCodeComapre(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());


        }
    }

    @Test
    public void TC_N_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME14");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.MSISDN, "");
            apiData.put(exgtw_selfcareairtimeapi.LOGINID, "");
            apiData.put(exgtw_selfcareairtimeapi.PASSWORD, "");
            apiData.put(exgtw_selfcareairtimeapi.PIN, "");
            apiData.put(exgtw_selfcareairtimeapi.EXTCODE, randomGeneration.randomNumeric(8));

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_O_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME15");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.FLAG, "");

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_P_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME16");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.FLAG, "asdf");

            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_Q_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME17");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.FLAG, "True");


            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_R_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME18");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "abc";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.FLAG, "True");


            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_S_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME19");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.FLAG, "True");
            apiData.put(exgtw_selfcareairtimeapi.BONUSAMOUNT, "-5");


            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_T_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME20");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.FLAG, "True");
            apiData.put(exgtw_selfcareairtimeapi.BONUSAMOUNT, "");


            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }

    @Test
    public void TC_U_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME21");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "abc";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.FLAG, "True");


            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }

    @Test
    public void TC_V_PositiveSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME22");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, "Bundle");
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.FLAG, "True");


            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }

    @Test
    public void TC_W_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME23");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.AMOUNT, "");


            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }

    @Test
    public void TC_X_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME24");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.AMOUNT, "-10");


            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }

    @Test
    public void TC_Y_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME25");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.AMOUNT, "0");


            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_Z_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME26");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);



            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, "");
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_Z1_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME27");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);



            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, "abcdgfuf");
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_Z2_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME28");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.PAYMENTTYPE,"");



            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_Z3_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME29");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.MSISDN2,"");



            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_Z4_NegativeSelfCareAPI() throws Exception {

        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFAIRTIME30");
        String service = null;
        EXGTW_SELFCAREAIRTIMEAPI exgtw_selfcareairtimeapi = new EXGTW_SELFCAREAIRTIMEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREAIRTIME_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(CustomerRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Airtime";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREAIRTIMEDAO APIDAO = (EXTGW_SELFCAREAIRTIMEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcareairtimeapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcareairtimeapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcareairtimeapi.PAYMENTTYPE,"To Be Charged");



            apiData.put(exgtw_selfcareairtimeapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), CustomerRechargeCode));
            String API = EXGTW_SELFCAREAIRTIMEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREAIRTIMEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
}

