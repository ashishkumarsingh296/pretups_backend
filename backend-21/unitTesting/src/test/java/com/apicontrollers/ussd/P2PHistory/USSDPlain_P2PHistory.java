package com.apicontrollers.ussd.P2PHistory;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

public class USSDPlain_P2PHistory extends BaseTest {

    public static boolean TestCaseCounter = false;
    private final String extentCategory = "API";

    /**
     * @throws Exception
     * @testid USSDC2S01
     * Positive Test Case For TRFCATEGORY: PRC
     */
    @Test
    public void TC_A_PositiveUSSD_ACCINFO_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDHIST01");
        USSDPlain_P2PHistoryAPI P2PHistoryAPI = new USSDPlain_P2PHistoryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_P2PHistory_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        String API = P2PHistoryAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_A_NegativeUSSD_ACCINFO_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDHIST02");
        USSDPlain_P2PHistoryAPI P2PHistoryAPI = new USSDPlain_P2PHistoryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_P2PHistory_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(P2PHistoryAPI.MSISDN1, "");
        String API = P2PHistoryAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


    @Test
    public void TC_C_PositiveUSSD_ACCINFO_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDHIST03");
        USSDPlain_P2PHistoryAPI P2PHistoryAPI = new USSDPlain_P2PHistoryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_P2PHistory_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        
        dataMap.put(P2PHistoryAPI.PIN, "");
        
        String PinRequired = DBHandler.AccessHandler.check_PIN_REQUIRED("PIN_REQUIRED_P2P");
        
        if(PinRequired.equalsIgnoreCase("false")){
        	String API = P2PHistoryAPI.prepareAPI(dataMap);
            String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
            _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
            Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
            
        }
        
        else{
        	currentNode.skip("PIN_REQUIRED_P2P preference is 'true' in DB. So this test case is skipped");
        }
    }

    @Test
    public void TC_D_PositiveUSSD_ACCINFO_API() throws Exception {

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDHIST04");
        USSDPlain_P2PHistoryAPI P2PHistoryAPI = new USSDPlain_P2PHistoryAPI();

        if (TestCaseCounter == false) {
            test = extent.createTest(CaseMaster.getModuleCode());
            TestCaseCounter = true;
        }

        HashMap<String, String> dataMap = USSDPlain_P2PHistory_DP.getAPIdata();

        currentNode = test.createNode(CaseMaster.getExtentCase());
        currentNode.assignCategory(extentCategory);
        dataMap.put(P2PHistoryAPI.LANGUAGE1, "");
        String API = P2PHistoryAPI.prepareAPI(dataMap);
        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
    }


}
