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

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class USSD_AddBuddy extends BaseTest{
	
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
	        USSD_AddBuddyAPI AddBuddyAPI = new USSD_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSD_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        String API = AddBuddyAPI.prepareAPI(dataMap);
	        BuddyName = dataMap.get(AddBuddyAPI.BUDDYNAME);
	        
	        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
	        Validator.messageCompare(xmlPath.get(AddBuddyAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	    }

	    
	    
	    @Test
	    public void TC_B_NegAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY02");
	        USSD_AddBuddyAPI AddBuddyAPI = new USSD_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSD_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        dataMap.put(AddBuddyAPI.BUDDYNAME,BuddyName);
	         String API = AddBuddyAPI.prepareAPI(dataMap);
	        
	        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
	        Validator.messageCompare(xmlPath.get(AddBuddyAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	    }  
	    
	 
	    
	    
	    @Test
	    public void TC_C_NegAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY03");
	        USSD_AddBuddyAPI AddBuddyAPI = new USSD_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSD_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        dataMap.put(AddBuddyAPI.MSISDN1,"");
	        String API = AddBuddyAPI.prepareAPI(dataMap);
	        
	        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
	        Validator.messageCompare(xmlPath.get(AddBuddyAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	    }
	    
	    @Test
	    public void TC_D_NegAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY04");
	        USSD_AddBuddyAPI AddBuddyAPI = new USSD_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSD_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        dataMap.put(AddBuddyAPI.PIN,"");
	        String API = AddBuddyAPI.prepareAPI(dataMap);
	        
	        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
	        Validator.messageCompare(xmlPath.get(AddBuddyAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	    
	    }   
	    
	    @Test
	    public void TC_E_NegAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY05");
	        USSD_AddBuddyAPI AddBuddyAPI = new USSD_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSD_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        dataMap.put(AddBuddyAPI.MSISDN2,"");
	        String API = AddBuddyAPI.prepareAPI(dataMap);
	        
	        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
	        Validator.messageCompare(xmlPath.get(AddBuddyAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	    }
	    
	  
	    
	    @Test
	    public void TC_F_NegAddBuddyAPI() throws Exception {

	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USSDADDBUDDY06");
	        USSD_AddBuddyAPI AddBuddyAPI = new USSD_AddBuddyAPI();

	        if (TestCaseCounter == false) {
	            test = extent.createTest(CaseMaster.getModuleCode());
	            TestCaseCounter = true;
	        }

	        HashMap<String, String> dataMap = USSD_AddBuddyDP.getAPIdata();

	        currentNode = test.createNode(CaseMaster.getExtentCase());
	        currentNode.assignCategory(extentCategory);
	        dataMap.put(AddBuddyAPI.BUDDYNAME,"");
	        String API = AddBuddyAPI.prepareAPI(dataMap);
	        
	        String[] APIResponse = _APIUtil.executeAPI(GatewayI.USSD, ServicesControllerI.P2PReceiver, API);
	        _APIUtil.addExecutionRecord(CaseMaster, APIResponse);
	        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
	        Validator.messageCompare(xmlPath.get(AddBuddyAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
	    }
	    
	    
	    
}
