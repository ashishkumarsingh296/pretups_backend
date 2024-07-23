package com.apicontrollers.ussd.DataBundleRecharge;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

public class USSDPlain_DBRC extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";


    @Test
    public void _01_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC01");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = new USSDPlainDBRCAPI().prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _02_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC02");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        
        dataMap.put(ussdAPI.MSISDN, UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }

    
    @Test
    public void _03_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC03");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String pin  = dataMap.get(ussdAPI.PIN);
        String invalidPIN = new RandomGeneration().randomNumeric(4);
        while (pin.equals(invalidPIN))
        {
        	pin=new RandomGeneration().randomNumeric(4);
        }
        
        dataMap.put(ussdAPI.PIN,invalidPIN);
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _04_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC04");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String invalidMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
                
        dataMap.put(ussdAPI.MSISDN2,invalidMSISDN);
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _05_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC05");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
                
        dataMap.put(ussdAPI.AMOUNT,"-1");
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _06_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC06");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
                
        dataMap.put(ussdAPI.AMOUNT,"0");
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _07_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC07");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
                
        dataMap.put(ussdAPI.AMOUNT,"30");
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _08_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC08");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
                
        dataMap.put(ussdAPI.SELECTOR,"8");
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _09_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC09");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
                
        dataMap.put(ussdAPI.MSISDN,"");
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _10_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC10");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
                
        dataMap.put(ussdAPI.PIN,"");
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }

    @Test
    public void _11_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC11");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
                
        dataMap.put(ussdAPI.MSISDN2,"");
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _12_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC12");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
                
        dataMap.put(ussdAPI.AMOUNT,"");
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _13_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC13");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
                
        dataMap.put(ussdAPI.SELECTOR,"");
        String API = ussdAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    @Test
    public void _14_dataBundleRecharge() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDDBRC14");
        
        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }
        USSDPlainDBRCAPI ussdAPI = new USSDPlainDBRCAPI();
        HashMap<String, String> dataMap = USSDPlainDBRCDP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);

        String API = ussdAPI.prepareAPI("TYPE=",dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.C2SReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.messageCompare(APIResponse[2], CaseMaster.getErrorCode());
    }
    
    
}
