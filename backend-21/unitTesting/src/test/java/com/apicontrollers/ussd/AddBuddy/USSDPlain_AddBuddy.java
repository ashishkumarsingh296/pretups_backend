package com.apicontrollers.ussd.AddBuddy;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;

public class USSDPlain_AddBuddy extends BaseTest{
	
	  public static boolean TestCaseCounter = false;
	    private final String extentCategory = "API";
	    String BuddyMSISDN = null;
	    String BuddyName = null;

	    /**
	     * @throws Exception
	     * @testid USSDC2S01
	     * Positive Test Case For TRFCATEGORY: PRC
	     */

	    @Test
	    public void TC_A_PositiveAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY01");
	        USSDPlain_AddBuddyAPI AddBuddyAPI = new USSDPlain_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSDPlain_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        String API = AddBuddyAPI.prepareAPI(dataMap);
	        BuddyName = dataMap.get(AddBuddyAPI.BUDDYNAME);
	        
	        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
	    }

	    
	    
	    @Test
	    public void TC_B_NegAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY02");
	        USSDPlain_AddBuddyAPI AddBuddyAPI = new USSDPlain_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSDPlain_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        dataMap.put(AddBuddyAPI.BUDDYNAME,BuddyName);
	         String API = AddBuddyAPI.prepareAPI(dataMap);
	        
	        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
	    }  
	    
	 
	    
	    
	    @Test
	    public void TC_C_NegAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY03");
	        USSDPlain_AddBuddyAPI AddBuddyAPI = new USSDPlain_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSDPlain_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        dataMap.put(AddBuddyAPI.MSISDN1,"");
	        String API = AddBuddyAPI.prepareAPI(dataMap);
	        
	        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
	    }
	    
	    @Test
	    public void TC_D_NegAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY04");
	        USSDPlain_AddBuddyAPI AddBuddyAPI = new USSDPlain_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSDPlain_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        dataMap.put(AddBuddyAPI.PIN,"");
	        String API = AddBuddyAPI.prepareAPI(dataMap);
	        
	        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
	    
	    }   
	    
	    @Test
	    public void TC_E_NegAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY05");
	        USSDPlain_AddBuddyAPI AddBuddyAPI = new USSDPlain_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSDPlain_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        dataMap.put(AddBuddyAPI.MSISDN2,"");
	        String API = AddBuddyAPI.prepareAPI(dataMap);
	        
	        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
	    }
	    
	  
	    
	    @Test
	    public void TC_F_NegAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY06");
	        USSDPlain_AddBuddyAPI AddBuddyAPI = new USSDPlain_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSDPlain_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        dataMap.put(AddBuddyAPI.BUDDYNAME,"");
	        String API = AddBuddyAPI.prepareAPI(dataMap);
	        
	        String[] APIResponse = _APIUtil.executePlainAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        Validator.APIMultiErrorCodeComapre(APIResponse[2], CaseMaster.getErrorCode());
	    }
	    
	    
	    
}
