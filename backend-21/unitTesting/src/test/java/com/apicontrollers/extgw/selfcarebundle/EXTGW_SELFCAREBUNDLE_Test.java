package com.apicontrollers.extgw.selfcarebundle;

import com.apicontrollers.extgw.O2CReturn.EXTGWO2CRDP;
import com.apicontrollers.extgw.O2CReturn.EXTGW_O2CDAO;
import com.apicontrollers.extgw.c2sTransfer.c2sGiftRecharge.EXTGW_GRCAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SDP;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGW_C2SDAO;
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

public class EXTGW_SELFCAREBUNDLE_Test extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";


    @Test
    public void TC_A_PositiveSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE01");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty() ) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";



        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);
            for (int j = 0; j < paymentModes.size(); j++) {

                currentNode = test.createNode(CaseMaster.getExtentCase());
                currentNode.assignCategory(extentCategory);
                apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(j),ProductRechargeCode));
                String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
                String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
                _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
                XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

                Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            }


        }
    }


    //EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI=new EXGTW_SELFCAREBUNDLEAPI();

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

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE02");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";



        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

                currentNode = test.createNode(CaseMaster.getExtentCase());
                currentNode.assignCategory(extentCategory);
                apiData.put(exgtw_SELFCAREBUNDLEAPI.EXTCODE, "");
                apiData.put(exgtw_SELFCAREBUNDLEAPI.LOGINID,"");
                apiData.put(exgtw_SELFCAREBUNDLEAPI.PASSWORD,"");

                apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
                String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
                String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
                _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
                XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

                Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            }


        }


    @Test
    public void TC_C_PositiveSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE03");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";



        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

                currentNode = test.createNode(CaseMaster.getExtentCase());
                currentNode.assignCategory(extentCategory);
                apiData.put(exgtw_SELFCAREBUNDLEAPI.EXTCODE, "");
                apiData.put(exgtw_SELFCAREBUNDLEAPI.MSISDN,"");
                apiData.put(exgtw_SELFCAREBUNDLEAPI.PIN,"");

                apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
                String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
                String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
                _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
                XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

                Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            }


        }

    @Test
    public void TC_D_PositiveSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE04");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";



        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

                currentNode = test.createNode(CaseMaster.getExtentCase());
                currentNode.assignCategory(extentCategory);
                apiData.put(exgtw_SELFCAREBUNDLEAPI.MSISDN, "");
                apiData.put(exgtw_SELFCAREBUNDLEAPI.PIN, "");
                apiData.put(exgtw_SELFCAREBUNDLEAPI.LOGINID,"");
                apiData.put(exgtw_SELFCAREBUNDLEAPI.PASSWORD,"");

                apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
                String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
                String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
                _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
                XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

                Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            }


        }

    @Test
    public void TC_E_PositiveSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE05");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";



        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

                currentNode = test.createNode(CaseMaster.getExtentCase());
                currentNode.assignCategory(extentCategory);
                apiData.put(exgtw_SELFCAREBUNDLEAPI.LOGINID,"");
                apiData.put(exgtw_SELFCAREBUNDLEAPI.PASSWORD,"");

                apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
                String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
                String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
                _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
                XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

                Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            }



    }
    @Test
    public void TC_F_PositiveSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE06");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";



        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

                currentNode = test.createNode(CaseMaster.getExtentCase());
                currentNode.assignCategory(extentCategory);
                apiData.put(exgtw_SELFCAREBUNDLEAPI.EXTCODE, "");

                apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
                String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
                String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
                _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
                XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

                Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            }


        }

    @Test
    public void TC_G_PositiveSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE07");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";



        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

                currentNode = test.createNode(CaseMaster.getExtentCase());
                currentNode.assignCategory(extentCategory);
                apiData.put(exgtw_SELFCAREBUNDLEAPI.MSISDN, "");
                apiData.put(exgtw_SELFCAREBUNDLEAPI.PIN,"");

                apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
                String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
                String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
                _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
                XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

                Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
            }


        }

    @Test
    public void TC_H_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE08");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";



        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.EXTCODE, "");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PIN,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.LOGINID,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PASSWORD,"");

            apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());



        }
    }
    @Test
    public void TC_I_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE09");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";

        RandomGeneration RandomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.EXTCODE, "");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.LOGINID,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PASSWORD,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PIN, RandomGeneration.randomNumeric(4));

            apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());



        }
    }
    @Test
    public void TC_J_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE10");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";



        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.EXTCODE, "");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.MSISDN,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PIN,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PASSWORD,"");

            apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());



        }
    }
    @Test
    public void TC_K_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE11");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";
        RandomGeneration RandomGeneration = new RandomGeneration();


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);


            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.EXTCODE, "");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.MSISDN,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PIN,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PASSWORD, RandomGeneration.randomNumeric(8));

            apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());



        }
    }
    @Test
    public void TC_L_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE12");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";



        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.EXTCODE, "");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.LOGINID,"cvcvx");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.MSISDN,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PIN,"");



            apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());



        }
    }

    @Test
    public void TC_M_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE13");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";
        RandomGeneration randomGeneration =new RandomGeneration();


        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.EXTCODE, "");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.LOGINID,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PASSWORD,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.MSISDN,randomGeneration.randomNumeric(10));

            apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.APIMultiErrorCodeComapre(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());



        }
    }
    @Test
    public void TC_N_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE14");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_SELFCAREBUNDLEAPI = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_SELFCAREBUNDLEAPI.TYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_SELFCAREBUNDLEAPI.MSISDN, "");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.LOGINID,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PASSWORD,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.PIN,"");
            apiData.put(exgtw_SELFCAREBUNDLEAPI.EXTCODE,randomGeneration.randomNumeric(8));

            apiData.put(exgtw_SELFCAREBUNDLEAPI.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }

    @Test
    public void TC_O_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE15");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE,typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.FLAG,"");

            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }
    @Test
    public void TC_P_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE16");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE,typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.FLAG,"asdf");

            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }
    @Test
    public void TC_Q_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE17");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="";
        String typeOfRequest="Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE,typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.FLAG,"True");


            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }
    @Test
    public void TC_R_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE18");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="abc";
        String typeOfRequest="Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE,typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.FLAG,"True");


            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }
    @Test
    public void TC_S_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE19");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue="PCT";
        String typeOfRequest="Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE,typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE,typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE,bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.FLAG,"True");
            apiData.put(exgtw_selfcarebundleapi.BONUSAMOUNT,"-5");


            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0),ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }


    }
    @Test
    public void TC_T_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE20");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.FLAG, "True");
            apiData.put(exgtw_selfcarebundleapi.BONUSAMOUNT, "");


            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_U_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE21");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "abc";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.FLAG, "True");



            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_V_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE22");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.FLAG, "True");



            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_W_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE23");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.AMOUNT, "");


            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }

    @Test
    public void TC_X_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE24");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.AMOUNT, "-10");


            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }

    @Test
    public void TC_Y_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE25");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.AMOUNT, "0");


            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_Z_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE26");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);



            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, "");
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_Z1_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE27");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);



            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, "abcdgfuf");
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_Z2_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE28");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.PAYMENTTYPE,"");



            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_Z3_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE29");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.MSISDN2,"");



            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }
    @Test
    public void TC_Z4_NegativeSelfCareAPI() throws Exception {

        String ProductRechargeCode = _masterVO.getProperty("ProductRechargeCode");
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWSELFBUNDLE30");
        String service = null;
        EXGTW_SELFCAREBUNDLEAPI exgtw_selfcarebundleapi = new EXGTW_SELFCAREBUNDLEAPI();
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        Object[] dataObject = EXTGW_SELFCAREBUNDLE_DP.getAPIdataWithAllUsers();


        ArrayList<String> paymentModes = new ArrayList<String>();

        //payment modes
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        for (int rownum = 1; rownum <= rowCount; rownum++) {

            service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
            String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
            if (service.equals(ProductRechargeCode) && !cardGroupName.isEmpty()) {

                paymentModes.add(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum));

            }
        }

        String bonusvalue = "PCT";
        String typeOfRequest = "Bundle";

        RandomGeneration randomGeneration = new RandomGeneration();

        for (int i = 0; i < dataObject.length; i++) {

            EXTGW_SELFCAREBUNDLEDAO APIDAO = (EXTGW_SELFCAREBUNDLEDAO) dataObject[i];
            HashMap<String, String> apiData = APIDAO.getApiData();

            apiData.put(exgtw_selfcarebundleapi.TYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSTYPE, typeOfRequest);
            apiData.put(exgtw_selfcarebundleapi.BONUSVALUE, bonusvalue);

            currentNode = test.createNode(CaseMaster.getExtentCase());
            currentNode.assignCategory(extentCategory);
            apiData.put(exgtw_selfcarebundleapi.PAYMENTTYPE,"To Be Charged");



            apiData.put(exgtw_selfcarebundleapi.PAYMENTMODE, DBHandler.AccessHandler.getSelectorCode(paymentModes.get(0), ProductRechargeCode));
            String API = EXGTW_SELFCAREBUNDLEAPI.prepareAPI(apiData);
            String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, APIResponse[1]);

            Validator.messageCompare(xmlPath.get(EXGTW_SELFCAREBUNDLEAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
        }

    }

}







