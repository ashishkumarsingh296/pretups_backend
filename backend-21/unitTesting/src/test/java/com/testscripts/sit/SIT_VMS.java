
package com.testscripts.sit;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransfer;
import com.Features.P2PCardGroup;
import com.Features.P2PTransferRules;
import com.Features.VMS;
import com.Features.mapclasses.OperatorToChannelMap;
import com.Features.mapclasses.VMSMap;
import com.apicontrollers.extgw.VMS.EXTGW_VoucherConsumption_API;
import com.apicontrollers.extgw.VMS.EXTGW_VoucherConsumption_DP;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.commons.ServicesControllerI;
import com.databuilder.BuilderLogic;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.VMS.CreateBatchForVoucherDownload;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

@ModuleManager(name = Module.SIT_VMS)
public class SIT_VMS extends BaseTest {

	String MasterSheetPath;
	static String directO2CPreference;
	static String moduleCode;
	static String skipForNetworkIN = "Skipped for IN network";
	static String skipActiveProfile = "Skipping active profile cases";
	private static String payableAmountAbsent = "Payable amount disabled";

	public String chooseMap;
	 @TestManager(TestKey = "PRETUPS-421") 
	@Test(dataProvider="VOMSDENOMINATIONS")// TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_01_CASEA1_VoucherDenomination(HashMap<String, String> initiateMap, int dataCounter) {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERDENOM");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);                        
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		initiateMap = vms.voucherDenomination(initiateMap, "");
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			vms.writeDenomination(initiateMap, dataCounter);
			
			//Message Validation Here
		} else 
			Assertion.assertFail("Add Voucher Denomination Failure with Following Message: " + initiateMap.get("Message"));
			
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-423") 
	@Test(dataProvider="VOMSDENOMPROFILES")// TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_02_CASEB2_VoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERPROF");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		initiateMap = vms.addVoucherProfile(initiateMap, "");
		
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			vms.writeProfile(initiateMap, dataCounter);
			
			//Message Validation Here
		} else 
			Assertion.assertFail("Add Voucher Profile Failure with Following Message: " + initiateMap.get("Message"));
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-426") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_03_CASEA11_viewVoucherDenomination(HashMap<String, String> initiateMap, int dataCounter) throws Exception  {
		
		final String methodName = "Test_viewVoucherDenomination";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVIEWVOUCHERDENOM");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		
		initiateMap=vms.viewVoucherDenomination(initiateMap,"");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-427") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_04_CASEB22_viewVoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		
		final String methodName = "Test_viewVoucherProfile";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVIEWVOUCHERPROF");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		
		initiateMap=vms.viewVoucherProfile(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-429") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_05_CASEC3_VoucherActiveProfile(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_VoucherActiveProfile";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITACTIVEVOUCHERPROF");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		//initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter));
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.addActiveVoucherProfile(initiateMap,"");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.addActiveVoucherProfile(initiateMap,"");
				Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
		
	}
	
	/**
     *  
     * @param screen
     * @return
     */
    public static String[] getAllowedVoucherTypesForScreen(String screen) {
     
        HashMap<String, String[]> screenWiseAllowedVoucherTypeMap = new HashMap<String, String[]>();
        String[] allowedVoucherTypes = {PretupsI.VOUCHER_TYPE_DIGITAL, PretupsI.VOUCHER_TYPE_TEST_DIGITAL, 
        		PretupsI.VOUCHER_TYPE_ELECTRONIC, PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC, PretupsI.VOUCHER_TYPE_PHYSICAL, PretupsI.VOUCHER_TYPE_TEST_PHYSICAL}; 
        
        populateScreenWiseAllowedVoucherTypesMap(screen, screenWiseAllowedVoucherTypeMap);
        
        String[] tempAllowedVoucherTypes = screenWiseAllowedVoucherTypeMap.get(screen);
        if(tempAllowedVoucherTypes != null) {
            allowedVoucherTypes = tempAllowedVoucherTypes;
        }
                
        return allowedVoucherTypes;
    }
    
    /**
     * DENO:D,DT,E,ET,P,PT;PROF:D,DT,E,ET,P,PT;ACTIVE_PROF:E,ET;VOUC_GEN:D,DT,E,ET,P,PT;VOUC_APP:D,DT,E,ET,P,PT;VOUC_DOWN:P,PT;CHAN_STATUS:D,DT,E,ET,P,PT;O2C:D,DT,P,PT
     * @param screen
     * @param screenWiseAllowedVoucherTypeMap
     */
    public static void populateScreenWiseAllowedVoucherTypesMap(String screen, HashMap<String, String[]> screenWiseAllowedVoucherTypeMap) {
     
        String screenWiseAllowedVoucherTypePref = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
        if(BTSLUtil.isNullString(screenWiseAllowedVoucherTypePref)) {
                     return;
        }
       
        String[] screens = screenWiseAllowedVoucherTypePref.split(";");
        for (int i = 0; i < screens.length; i++) {
            if(BTSLUtil.isNullString(screens[i])) {
                  return;
            }
            String[] screenWiseAllowedVoucherType = screens[i].split(PretupsI.COLON);
            screenWiseAllowedVoucherTypeMap.put(screenWiseAllowedVoucherType[0], screenWiseAllowedVoucherType[1].split(PretupsI.COMMA));
        }
      
    }

  	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-431") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_06_CASEC33_ViewVoucherActiveProfile(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_ViewVoucherActiveProfile";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVIEWACTIVEVOUCHERPROF");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"), initiateMap.get("activeProfile"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		//initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter));
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.viewActiveProfile(initiateMap,"");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.viewActiveProfile(initiateMap,"");
				Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-432") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_07_CASED4_VoucherOrderInitiate(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_VoucherOrderInitiate";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERORDERINIT");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-434") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_08_CASEE5_VoucherOrderApproval1(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		int maxApprovalLevel=0;
		if(initiateMap.get("categoryName").equals("SUADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		else if(initiateMap.get("categoryName").equals("NWADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		else if(initiateMap.get("categoryName").equals("SSADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		else if(initiateMap.get("categoryName").equals("SUNWADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}

		final String methodName = "Test_VoucherOrderApproval1";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERAPPROV1");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		if(maxApprovalLevel > 0)
		{
			initiateMap = vms.voucherGenerationApproval1(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		
	  else
		  Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-435") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_09_CASEF6_VoucherOrderApproval2(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		int maxApprovalLevel=0;
		if(initiateMap.get("categoryName").equals("SUADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		else if(initiateMap.get("categoryName").equals("NWADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		else if(initiateMap.get("categoryName").equals("SSADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		else if(initiateMap.get("categoryName").equals("SUNWADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		final String methodName = "Test_VoucherOrderApproval2";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERAPPROV2");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");


		if(maxApprovalLevel > 1)
		{
		initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else
			 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-436") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_10_CASEG7_VoucherOrderApproval3(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		int maxApprovalLevel=0;
		if(initiateMap.get("categoryName").equals("SUADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		else if(initiateMap.get("categoryName").equals("NWADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		else if(initiateMap.get("categoryName").equals("SSADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		else if(initiateMap.get("categoryName").equals("SUNWADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}

		final String methodName = "Test_VoucherOrderApproval3";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERAPPROV3");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 2)
		{
		initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		
	  else
		  Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
	
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-437") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_11_CASEH8_VoucherGenerationScript(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_VoucherGenerationScript";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERGENSCRIPT");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		
		int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
		System.out.println("HCPT_VMS: " + HCPT_VMS);
		if( HCPT_VMS == 1) {
			Assertion.assertSkip(skipForNetworkIN);
		}else{
			vms.voucherGenerationScriptExecution();
	//		Thread.sleep(9000);
			String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String batchType = DBHandler.AccessHandler.fetchBatchType(productID);
			
			Assertion.assertEquals(batchType, "GE");
		} 
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-438") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_12_CASEI9_CreateBatch(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_CreateBatch";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITCREATBATCH");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		
		int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
		if( HCPT_VMS == 1) {
			Assertion.assertSkip(skipForNetworkIN);
		}else{
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
				Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
				}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
					Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		}
		Assertion.completeAssertions();
		
	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-439") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_13_CASEJ10_VoucherDownload(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_VoucherDownload";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERDOWNLOAD");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		
		int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
		if(HCPT_VMS == 1) {
			Assertion.assertSkip(skipForNetworkIN);
		}else{
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.vomsVoucherDownload(initiateMap,"");
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
			}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.vomsVoucherDownload(initiateMap,"");
					Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		}
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-440") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_14_CASEK11_ChangeStatus(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_ChangeStatus";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITCHANGESTATUS");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.changeOtherStatus(initiateMap,"");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatus(initiateMap,"");
				Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
			}
			else if (initiateMap.get("type").equals("E") || initiateMap.get("type").equals("ET")) {
				initiateMap = vms.changeGeneratedStatusElectronic(initiateMap,"");
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		
		Assertion.completeAssertions();

		
	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-437") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_14_CASEK12_VoucherChangeStatusScript(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_VoucherChangeStatusScript";
       Log.startTestCase(methodName);
       
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITCHANGESTATUSSCRIPT");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
		if( HCPT_VMS == 1 ) {
			String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			if(DBHandler.AccessHandler.isVomsBatchWithStatusPresent(productID, PretupsI.WAREHOUSE, "SC")) {
					vms.voucherChangeStatusScriptExecution();
				//		Thread.sleep(9000);
					
				//		String batchType = DBHandler.AccessHandler.fetchBatchType(productID);					
				//		Assertion.assertEquals(batchType, PretupsI.WAREHOUSE);
					if(DBHandler.AccessHandler.isVomsBatchWithStatusPresent(productID, PretupsI.WAREHOUSE, "EX"))
									Assertion.assertPass("Batch with WH type of product "+ productID +" is in EX status");
							else
								Assertion.assertFail("Batch with WH type of product "+ productID +" is not in EX status");
			}else
				Assertion.assertSkip("No scheduled batches found");
		}else{
			
			Assertion.assertSkip(skipForNetworkIN);
		} 
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-441") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_15_CASEL12_O2CTransfer(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_O2CTransfer";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITO2C");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
  		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		O2CTransfer o2CTransfer = new O2CTransfer(driver);
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"), _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);
		transferMap.put("TO_STOCK_TYPE", PretupsI.O2C_VOUCHER_TYPE_LOOKUP);
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status ="WH";
		if(initiateMap.get("type").equals("D") || initiateMap.get("type").equals("DT")) {
		status = "GE";
		}
		else {
		status = "WH";
		}
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID,status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID,status);
		long fromnumber=Long.parseLong(fromSerialNumber);
		long tonumber=Long.parseLong(toSerialNumber);
		int numberOfo2cVouchers= Integer.parseInt(_masterVO.getProperty("numberofo2c"));
		long tonumbermid=fromnumber+numberOfo2cVouchers-1;
		
		
		initiateMap.put("fromSerialNumber", fromSerialNumber);
	
		initiateMap.put("toSerialNumber", String.valueOf((Math.min(tonumber,tonumbermid))));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
		transferMap = o2CTransfer.initiateVoucherO2CTransfer(transferMap, initiateMap);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
			o2CTransfer.performingLevel1ApprovalVoucher(transferMap,initiateMap,"");
		    long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>firstApprov)
		    o2CTransfer.performingLevel2ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"),transferMap,"");
		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>secondApprov)
		    o2CTransfer.performingLevel3ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"),"");
			
		initiateMap = vms.checkO2CStatus(initiateMap);
		Assertion.assertEquals(initiateMap.get("currentStatus"), "EN");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.O2C);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				transferMap = o2CTransfer.initiateVoucherO2CTransfer(transferMap, initiateMap);
				if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
					o2CTransfer.performingLevel1ApprovalVoucher(transferMap,initiateMap,"");
				    long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
				if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>firstApprov)
				    o2CTransfer.performingLevel2ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"),transferMap,"");
				if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>secondApprov)
				    o2CTransfer.performingLevel3ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"),"");
					
				initiateMap = vms.checkO2CStatus(initiateMap);
				int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));				
				Assertion.assertEquals(initiateMap.get("currentStatus"), "EN");
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	/*@Test(dataProvider="VOMSDENOMPROFILES")
	 @TestManager(TestKey = "PRETUPS-440") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_15_CASEL13_ChangeStatus(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		
		final String methodName = "Test_ChangeStatus";
      Log.startTestCase(methodName);
      
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITCHANGESTATUS");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.changeOtherStatus(initiateMap,"");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
		int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
		if(HCPT_VMS == 1) {
				initiateMap.put("voucherStatus", PretupsI.ENABLE);
				initiateMap = vms.changeOtherStatus(initiateMap,"");
				Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}else {
			Assertion.assertSkip(skipForNetworkIN);
		}
			}
			else if (initiateMap.get("type").equals("E") || initiateMap.get("type").equals("ET")) {
				initiateMap = vms.changeGeneratedStatusElectronic(initiateMap,"");
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		
		Assertion.completeAssertions();

		
	Log.endTestCase(methodName);
	}*/
	
	public void createVoucherCardGroup(HashMap<String, String> initiateMap) throws InterruptedException{
		String serviceName=ExtentI.getValueofCorrespondingColumns(ExcelI.P2P_SERVICES_SHEET_VOUCHER, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"VCN"});
		String serviceType=ExtentI.getValueofCorrespondingColumns(ExcelI.P2P_SERVICES_SHEET_VOUCHER, ExcelI.SERVICE_TYPE, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"VCN"});
				
		String subService =ExtentI.getValueofCorrespondingColumns(ExcelI.P2P_SERVICES_SHEET_VOUCHER, ExcelI.SELECTOR_NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{serviceType});
        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
        HashMap<String, String> mapInfo = (HashMap<String, String>) p2pCardGroup.P2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
        boolean uap = true;
 		P2PTransferRules p2pTransferRules = new P2PTransferRules(driver);
 		String result[] = p2pTransferRules.addP2PTransferRules(serviceName, subService, mapInfo.get("CARDGROUPNAME"), uap, "ALL");
 		/*currentNode = test.createNode(_masterVO .getCaseMasterByID("UP2PTRFRULE2").getExtentCase());
 		currentNode.assignCategory(assignCategory);*/
 	 	String addP2PTransferRuleSuccessMsg = MessagesDAO.prepareMessageByKey("trfrule.addtrfrule.msg.success");
 		String p2pTransferRuleAlreadyExistsMsg = MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist","1");
 		if (p2pTransferRuleAlreadyExistsMsg.equals(result[0])) {
 			String result2[]=p2pTransferRules.modifyP2PTransferRulesVoucher("ALL",serviceName, subService,PretupsI.STATUS_ACTIVE_LOOKUPS, mapInfo.get("CARDGROUPNAME"), mapInfo.get("datetime"));
 			String modifyP2PTransferRuleSuccessMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
 			Assertion.assertEquals(result2[0], modifyP2PTransferRuleSuccessMsg);
 		}
 		else {
 			Assertion.assertEquals(addP2PTransferRuleSuccessMsg, result[0]);
 		}
 		
 		Assertion.completeAssertions();
 		//Thread.sleep(120000);
	}
	

	@Test(dataProvider="VOMSDENOMPROFILES")
    @TestManager(TestKey = "PRETUPS-442") // TO BE UNCOMMENTED WITH JIRA TEST ID
   public void A_16_CASEM13_VoucherConsumption(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
         
         final String methodName = "Test_VoucherConsumption";
         Log.startTestCase(methodName);
         CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERCONS");
         moduleCode = CaseMaster1.getModuleCode();
         currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
         currentNode.assignCategory("SIT");
         VMS vms = new VMS(driver);
         if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
         createVoucherCardGroup(initiateMap);
                   
         HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
         EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
         RandomGeneration randomGeneration = new RandomGeneration();
         apiData.put(voucherConsumptionAPI.LOGINID, "");
         apiData.put(voucherConsumptionAPI.PASSWORD, "");
         apiData.put(voucherConsumptionAPI.AMOUNT, "100");
         apiData.put(voucherConsumptionAPI.EXTCODE, "");
         apiData.put(voucherConsumptionAPI.MSISDN2, "");
         apiData.put(voucherConsumptionAPI.PIN, "");
         apiData.put(voucherConsumptionAPI.SELECTOR, "");
         String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
         String status = "EN";
         int numberOfVouchers= Integer.parseInt(_masterVO.getProperty("numberOfVouchersForConsumption"));
         for(int i=0;i<numberOfVouchers;i++)
   {
               String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
               String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
               String pin = Decrypt.decryption(encryptedPin);
               String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
               apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
               apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
               apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
               String API = voucherConsumptionAPI.prepareAPI(apiData);
               String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
               initiateMap.put("serialNumber", serialNumber);
               initiateMap = vms.voucherConsumption(initiateMap);
               Assertion.assertEquals(initiateMap.get("currentStatus"), "CU");
               Assertion.completeAssertions();
   }

   Log.endTestCase(methodName);
   }

	@Test
	 @TestManager(TestKey = "PRETUPS-446") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEP16_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS16");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
        String scenario = "systemPreferenceVerification";
        
		initiateMap = vms.autoVoucherPreference(scenario);
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-447") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEQ17_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS17");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
        String scenario = "isModifyAllowed";
        
		initiateMap = vms.autoVoucherPreference(scenario);
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-448") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASER18_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS18");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
        String scenario = "modifySystemPreference";
        
		initiateMap = vms.autoVoucherPreference(scenario);
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-449") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASES19_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS19");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
        String scenario = "modifyAlphaNumericValue";
        
		initiateMap = vms.autoVoucherPreference(scenario);
		String preferenceCode = DBHandler.AccessHandler.getNamefromSystemPreference(CONSTANT.VMS_AUTO_VOUCHER_CRTN_ALWD);
		String message = MessagesDAO.prepareMessageByKey("preference.selectsystempreference.error.defaultvalue", MessagesDAO.getLabelByKey("preference.displaynetworkpreferencedetail.label.value"), preferenceCode);

		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Assertion.assertEquals(initiateMap.get("Message"), message);
		} else 
		Assertion.assertFail("System preference VMS_AUTO_VOUCHER_CRTN_ALWD getting modified even with alphanumeric value");
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-450") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASET20_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS20");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
        
		initiateMap = vms.autoVoucher(initiateMap);
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-451") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEU21_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS21");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		vms.enableAutoVoucher();
		
		initiateMap = vms.autoVoucher(initiateMap);
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-452") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEV22_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS22");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		String scenario = "preference";
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap = vms.modifyVoucherProfile(initiateMap, scenario,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-454") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEW23_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS23");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		String newThreshold = randomGeneration.randomNumeric(12);
		initiateMap.put("scenario","");
		initiateMap.put("threshold", "999999999999");
		
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addproduct.err.msg.voucherThreshold");
				
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Assertion.assertPass(" Threshold value is displayed only till 10 digits");
			//Message Validation Here
		} else if(initiateMap.get("MessageStatus").equalsIgnoreCase("N") && initiateMap.get("Message").equals(message))
			Assertion.assertSkip(message);
		else
			Assertion.assertSkip(" Threshold value is exceeding maxLimit");
		
        Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-455") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEX24_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS24");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		String newQuantity = randomGeneration.randomNumeric(12);
		initiateMap.put("scenario","");
		initiateMap.put("quantity", "999999999999");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addproduct.err.msg.voucherGenerateQuantity");
		
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Assertion.assertPass("Quantity value is displayed only till 10 digits");
			Log.info("Quantity value is displayed only till 10 digits");
		}	
			else if(initiateMap.get("MessageStatus").equalsIgnoreCase("N") && initiateMap.get("Message").equals(message))
				Assertion.assertSkip(message);
			//Message Validation Here
		 else 
			 Assertion.assertSkip(" Quantity value is exceeding maxLimit");
			
	        Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-456") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEY25_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS25");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		String newThreshold = randomGeneration.randomAlphaNumeric(10);
		initiateMap.put("scenario","");
		initiateMap.put("threshold", newThreshold);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addproduct.err.msg.voucherThreshold");
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("N") && initiateMap.get("Message").equalsIgnoreCase(message)) {
			Assertion.assertPass("Message Validation successful  with Following Message: " + initiateMap.get("Message"));
			//Message Validation Here
		} else 
			Assertion.assertFail("Special and AlphaNumeric characters are getting allowed in Threshold field.");
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-457") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZ26_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS26");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		String newQuantity = randomGeneration.randomAlphaNumeric(10);
		initiateMap.put("scenario","");
		initiateMap.put("quantity", newQuantity);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addproduct.err.msg.voucherGenerateQuantity");
		
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("N") && initiateMap.get("Message").equalsIgnoreCase(message)) {
			Assertion.assertPass("Message Validation successful  with Following Message: " + initiateMap.get("Message"));
			//Message Validation Here
		} else 
			Assertion.assertFail("Special and AlphaNumeric characters are getting allowed in Quantity field.");
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-458") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZA27_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS27");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		String scenario = "fieldValidation";
		
		initiateMap = vmsMap.defaultMap();
		String newThreshold = randomGeneration.randomAlphaNumeric(10);
		
		initiateMap.put("threshold", newThreshold);
		initiateMap = vms.modifyVoucherProfile(initiateMap,scenario,"");
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addproduct.err.msg.voucherThreshold");
		
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("N") && initiateMap.get("Message").equalsIgnoreCase(message)) {
			Assertion.assertPass("Message Validation successful  with Following Message: " + initiateMap.get("Message"));
			//Message Validation Here
		} else 
			Assertion.assertFail("Special and AlphaNumeric characters are getting allowed in Threshold field.");
		
		Assertion.completeAssertions();

	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-459") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZB28_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS28");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		String scenario = "fieldValidation";
		
		initiateMap = vmsMap.defaultMap();
		String newQuantity = randomGeneration.randomAlphaNumeric(10);
		
		initiateMap.put("quantity", newQuantity);
		initiateMap = vms.modifyVoucherProfile(initiateMap,scenario,"");
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addproduct.err.msg.voucherGenerateQuantity");
		
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("N") && initiateMap.get("Message").equalsIgnoreCase(message)) {
			Assertion.assertPass("Message Validation successful  with Following Message: " + initiateMap.get("Message"));
			//Message Validation Here
		} else 
			Assertion.assertFail("Special and AlphaNumeric characters are getting allowed in Quantity field.");
		
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-460") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZC29_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS29");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		String scenario = "fieldValidation";
		
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("threshold", "");
		initiateMap = vms.modifyVoucherProfile(initiateMap,scenario,"");
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addproduct.err.msg.voucherThreshold");
		
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("N") && initiateMap.get("Message").equalsIgnoreCase(message)) {
			Assertion.assertPass("Message Validation successful  with Following Message: " + initiateMap.get("Message"));
			//Message Validation Here
		} else 
			Assertion.assertFail("Blank values are getting allowed in Threshold field even if auto-generate is 'Yes'.");
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-461") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZD30_AutoVoucher() throws InterruptedException {
		
		final String methodName = "Test_AutoVoucher";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS30");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		String scenario = "fieldValidation";
		
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("quantity", "");
		initiateMap = vms.modifyVoucherProfile(initiateMap,scenario,"");
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addproduct.err.msg.voucherGenerateQuantity");
		
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("N") && initiateMap.get("Message").equalsIgnoreCase(message)) {
			Assertion.assertPass("Message Validation successful  with Following Message: " + initiateMap.get("Message"));
			//Message Validation Here
		} else 
			Assertion.assertFail("Blank values are getting allowed in Quantity field even if auto-generate is 'Yes'.");
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-462") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZE31_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS31");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vmcategory.selectcategoryforvoms.label.denomname"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-463") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZF32_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS32");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("shortName", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vmcategory.addsubcategoryforvoms.label.shortname"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-464") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZG33_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS33");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("mrp", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vmcategory.modifydenomination.label.mrp"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-465") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZH34_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS34");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME"))) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			
			initiateMap = vmsMap.defaultMap();
			
			initiateMap.put("payableAmount", "");
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.payamtreq");
			
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-466") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZI35_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS35");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.catnameexists");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-467") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZJ36_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS36");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.catshortnameexists");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-468") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZK37_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS37");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpexists");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-469") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZL38_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS38");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("mrp", randomGeneration.randomAlphabets(3));
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-470") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZM39_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS39");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("mrp", "-10");
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-471") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZN40_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS40");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME"))) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			RandomGeneration randomGeneration = new RandomGeneration();
			initiateMap = vmsMap.defaultMap();
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
			initiateMap.put("payableAmount", randomGeneration.randomAlphaNumeric(4));
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("vomsproduct.modifyproduct.err.msg.talktimeisnum", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
			
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-472") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZO41_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS41");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME"))) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			RandomGeneration randomGeneration = new RandomGeneration();
			initiateMap = vmsMap.defaultMap();
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
			initiateMap.put("payableAmount", "-10");
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
			
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-473") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZP42_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS42");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("mrp", randomGeneration.randomNumeric(1)+" "+randomGeneration.randomNumeric(1));
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-474") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZQ43_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS43");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME"))) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			RandomGeneration randomGeneration = new RandomGeneration();
			initiateMap = vmsMap.defaultMap();
			
			initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
			initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
			initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
			initiateMap.put("payableAmount", randomGeneration.randomNumeric(1)+" "+randomGeneration.randomNumeric(1));
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-475") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZR44_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS44");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("denominationName", randomGeneration.randomAlphabets(1) +"@"+randomGeneration.randomAlphaNumeric(3));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-476") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZS45_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS45");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("mrp", randomGeneration.randomNumeric(3)+"."+randomGeneration.randomNumeric(4));
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-477") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZT46_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS46");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("mrp", randomGeneration.randomNumeric(3)+"."+randomGeneration.randomNumeric(2));
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-478") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZU47_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS47");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("mrp", "-"+randomGeneration.randomNumeric(3)+"."+randomGeneration.randomNumeric(2));
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-479") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZV48_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS48");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME"))) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			RandomGeneration randomGeneration = new RandomGeneration();
			initiateMap = vmsMap.defaultMap();
			
			initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
			initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
			String mrp = UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("mrp", mrp);
			String payableAmount = mrp + 1;
			initiateMap.put("payableAmount", payableAmount);
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");
			
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-480") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZW49_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS49");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("mrp", randomGeneration.randomNumeric(1)+"@!");
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-481") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZX50_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS50");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("mrp", randomGeneration.randomNumeric(1)+"@"+randomGeneration.randomAlphabets(1));
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-482") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZY51_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS51");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
		if(HCPT_VMS == 1) {
			Assertion.assertSkip(skipForNetworkIN);
		}else{
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			RandomGeneration randomGeneration = new RandomGeneration();
			initiateMap = vmsMap.defaultMap();
			
			initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
			initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
			initiateMap.put("mrp", "0");
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("vmcategory.modifycategoryforvoms.err.msg.mrpnotzero");
			
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-483") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZ52_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS52");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME"))) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			RandomGeneration randomGeneration = new RandomGeneration();
			initiateMap = vmsMap.defaultMap();
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
			initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits());
			initiateMap.put("payableAmount", randomGeneration.randomNumeric(3)+"."+randomGeneration.randomNumeric(4));
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");
			
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-484") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZA53_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS53");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME"))) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			RandomGeneration randomGeneration = new RandomGeneration();
			initiateMap = vmsMap.defaultMap();
			
			initiateMap.put("denominationName",UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
			initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
			initiateMap.put("payableAmount", randomGeneration.randomNumeric(3)+"."+randomGeneration.randomNumeric(2));
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-485") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZB54_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS54");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME"))) {
			initiateMap.put("denominationName",UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
			initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits());
			initiateMap.put("payableAmount", randomGeneration.randomNumeric(1)+"@!"+randomGeneration.randomNumeric(1));
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
			
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-486") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZC55_VoucherDenomination() throws InterruptedException {
		
		final String methodName = "Test_VoucherDenomination";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS55");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME"))) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			RandomGeneration randomGeneration = new RandomGeneration();
			initiateMap = vmsMap.defaultMap();
			
			initiateMap.put("denominationName",UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
			initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits());
			initiateMap.put("payableAmount", randomGeneration.randomAlphaNumeric(4));
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-487") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZD56_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS56");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("activeProfile", "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vomsproduct.viewactiveprofile.label.profilename"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-488") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZE57_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS57");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("shortName", "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.profileshortname"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-489") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZF58_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS58");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("VOMS_PROFILE_DEF_MINMAXQTY system preference is true hence this case is skip");
		}
		else {
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("minQuantity", "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.minqty"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-490") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZG59_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS59");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("VOMS_PROFILE_DEF_MINMAXQTY system preference is true hence this case is skip");
		}
		else {
		initiateMap.put("scenario", "");
		initiateMap.put("maxQuantity", "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.maxqty"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-491") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZH60_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS60");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("talkTime", "");
		String vomsProfileTalktime = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROF_TALKTIME_MANDATORY);
		if(vomsProfileTalktime.equalsIgnoreCase("true")) {
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vomsproduct.confirmaddactiveproduct.label.talktime"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else {
			Assertion.assertSkip("VOMS_PROF_TALKTIME_MANDATORY system preference is false hence this case is skip");
		}
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-492") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZI61_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS61");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("validity", "");
		String vomsProfileValidity = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROF_VALIDITY_MANDATORY);
		if(vomsProfileValidity.equalsIgnoreCase("true")) {
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.validity"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else {
			Assertion.assertSkip("VOMS_PROF_VALIDITY_MANDATORY system preference is false hence this case is skip");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-493") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZJ62_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS62");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("expiryPeriod", "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addproduct.err.msg.expiryperioddatenull");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-494") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZK63_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS63");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
//		initiateMap.put("denominationName",UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
	//	initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
	//	initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits());
		initiateMap.put("minQuantity", "-100");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.minqty"));
		
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}
		else {
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-495") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZL64_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS64");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
	//  initiateMap.put("denominationName",UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
	//  initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
	//	initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits())
		initiateMap.put("maxQuantity", "-100");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.maxqty"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}
		else {
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-496") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZM65_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS65");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("denominationName",UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits());
		initiateMap.put("categoryName","SUADM");
		vms.voucherDenomination(initiateMap,"");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("talkTime", "-100");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("vomsproduct.viewProfile.label.talktime"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-497") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZN66_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS66");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("denominationName",UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits());
		initiateMap.put("categoryName","SUADM");
		vms.voucherDenomination(initiateMap,"");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("validity", "-90");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.validity"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-498") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZO67_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS67");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("denominationName",UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits());
		initiateMap.put("categoryName","SUADM");
		vms.voucherDenomination(initiateMap,"");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("expiryPeriod", "-90");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.expiryperiod"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-499") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZP68_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS68");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
//	initiateMap.put("denominationName",UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
	//	initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
	//	initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits());
		initiateMap.put("minQuantity", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.minqty"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}else {
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-500") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZQ69_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS69");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
	//initiateMap.put("denominationName",UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
	//	initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
	//	initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits());
		initiateMap.put("maxQuantity", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.maxqty"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}else {
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-507") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZR70_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS70");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("talkTime", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.viewProfile.label.talktime"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-508") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZS71_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS71");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("validity", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.validity"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-509") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZT72_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS72");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("expiryPeriod", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.expiryperiod"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-511") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZU73_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS73");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario","");
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName()+"!@");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.succ.msg.profileaddsucc");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-512") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZV74_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS74");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("activeProfile", randomGeneration.randomAlphabets(30));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.succ.msg.profileaddsucc");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-514") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZW75_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS75");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario","");
		initiateMap.put("mrp", "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-515") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZX76_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS76");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario","");
		initiateMap.put("shortName", "ssk");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.err.msg.shortnamealpha", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.profileshortname"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-516") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZY77_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS77");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario","");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("shortName", randomGeneration.randomAlphabets(4)+"ABCi");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.err.msg.shortnamealpha", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.profileshortname"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-518") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZ78_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS78");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario","");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("shortName", randomGeneration.randomNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.succ.msg.profileaddsucc");
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-527") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZA79_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS79");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario","");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("shortName", "sss"+randomGeneration.randomNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.err.msg.shortnamealpha", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.profileshortname"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-529") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZB80_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS80");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario","");
		initiateMap.put("shortName", "4R o");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.err.msg.shortnamealpha", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.profileshortname"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-530") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZC81_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS81");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario","");
		initiateMap.put("shortName", "5Y 1");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.err.msg.shortnamealpha", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.profileshortname"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-531") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZD82_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS82");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario","");
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(3)+"@!#$");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.err.msg.shortnamealpha", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.profileshortname"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-532") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZE83_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS83");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario","");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("minQuantity", "0");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.succ.msg.profileaddsucc");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-533") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZF84_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS84");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		int performanceIndicatorQuantity = Integer.parseInt(initiateMap.get("maxQuantity"));
		String minQuantity = String.valueOf(performanceIndicatorQuantity+10);
		initiateMap.put("minQuantity", minQuantity);
		initiateMap.put("scenario", "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.modifyactiveproduct.err.msg.minqtygreaterthanmaxqty");
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}else {
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	/*@Test
	public void CASEZZZG85_VoucherProfile() throws InterruptedException {
		
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS85");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT]VMS");
			testCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("minQuantity", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
		
		if(initiateMap.get("Message").equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + initiateMap.get("Message") + "]");
		    currentNode.log(Status.FAIL, "Message Validation Failed");
		}
	Log.endTestCase(methodName);
	}*/
	
	@Test
	@TestManager(TestKey = "PRETUPS-535") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZH86_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS86");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("minQuantity", "!@#$"+randomGeneration.randomNumeric(1));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.minqty"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}else {
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-536") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZI87_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS87");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		String maxQuantity = initiateMap.get("maxQuantity");
		initiateMap.put("minQuantity", maxQuantity);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.modifyactiveproduct.err.msg.minqtygreaterthanmaxqty");
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}else {
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-537") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZJ88_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS88");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("minQuantity", randomGeneration.randomNumeric(1)+"."+randomGeneration.randomNumeric(2));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.minqty"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}else {
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-538") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZK89_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS89");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("maxQuantity", randomGeneration.randomNumeric(1)+"."+randomGeneration.randomNumeric(5));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.maxqty"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}else {
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-540") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZL90_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS90");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("maxQuantity", "0");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.modifyactiveproduct.err.msg.minqtygreaterthanmaxqty");
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}else {
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-542") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZM91_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS91");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_ProfileName());
		int minQuantity = 100;
		initiateMap.put("minQuantity", "100");
		String performanceIndicatorQuantity = String.valueOf(minQuantity-10);
		initiateMap.put("maxQuantity", performanceIndicatorQuantity);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.modifyactiveproduct.err.msg.minqtygreaterthanmaxqty");
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}else {
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	/*@Test
	public void CASEZZZN92_VoucherProfile() throws InterruptedException {
		
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS92");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT]VMS");
			testCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("maxQuantity", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
		
		if(initiateMap.get("Message").equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + initiateMap.get("Message") + "]");
		    currentNode.log(Status.FAIL, "Message Validation Failed");
		}
	Log.endTestCase(methodName);
	}
*/	
	@Test
	@TestManager(TestKey = "PRETUPS-543") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZO93_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS93");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("maxQuantity", randomGeneration.randomNumeric(1)+"!@#$");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.maxqty"));
		String vomsProfileDefMinMaxQty = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROFILE_DEF_MINMAXQTY);
		if(vomsProfileDefMinMaxQty.equalsIgnoreCase("true")) {
			Assertion.assertSkip("Not Valid Secnario");
		}else {
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-544") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZP94_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS94");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("talkTime", "0");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.succ.msg.profileaddsucc");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-545") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZQ95_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS95");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("talkTime", "AUT" + randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsreport.voucherenquiry.label.talktime"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-546") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZR96_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS96");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("talkTime", randomGeneration.randomNumeric(1)+"!@#$");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsreport.voucherenquiry.label.talktime"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-547") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZS97_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS97");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("talkTime", randomGeneration.randomNumeric(1)+"."+randomGeneration.randomNumeric(2));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.succ.msg.profileaddsucc");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-549") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZT98_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS98");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("validity", randomGeneration.randomNumeric(1)+"."+randomGeneration.randomNumeric(2));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.validity"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-550") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZU99_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS99");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("validity", randomGeneration.randomNumeric(1)+"!@#$");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.validity"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-551") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZV100_VoucherProfile() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS100");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("validity", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.validity"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-552") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZW101_VoucherProfile() throws InterruptedException {
		
	  final String methodName = "Test_VoucherProfile";
	  Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS101");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("expiryPeriod", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.expiryperiod"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-553") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZX102_VoucherProfile() throws InterruptedException {
		
			final String methodName = "Test_VoucherProfile";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS102");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("expiryPeriod", randomGeneration.randomNumeric(1)+"!@#$");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.expiryperiod"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-554") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZY103_VoucherProfile() throws InterruptedException {
		
			final String methodName = "Test_VoucherProfile";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS103");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("expiryPeriod", randomGeneration.randomNumeric(1)+"."+randomGeneration.randomNumeric(2));
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.expiryperiod"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-555") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZ104_VoucherProfile() throws InterruptedException {
		
			final String methodName = "Test_VoucherProfile";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS104");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		String scenario = "ExpiryPeriod equal to Current Date";
		initiateMap.put("scenario", scenario);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.expiryperiod"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-556") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZA105_VoucherProfile() throws InterruptedException {
		
			final String methodName = "Test_VoucherProfile";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS105");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		
		String scenario = "ExpiryPeriod less than Current Date";
		initiateMap.put("scenario", scenario);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber", MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.expiryperiod"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-557") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZB106_AddActiveVoucherProfile() throws InterruptedException {
		
			final String methodName = "Test_AddActiveVoucherProfile";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS106");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		int ACTIVE_PROFILE = Integer.parseInt(_masterVO.getClientDetail("ACTIVE_PROFILE"));
		if(ACTIVE_PROFILE == 1) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			RandomGeneration randomGeneration = new RandomGeneration();
			
			initiateMap = vmsMap.defaultMap();
			initiateMap.put("addApplicableDate", "");
			
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
			}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		
			String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vomsproduct.addactiveproduct.label.applicablefrom"));
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else {Assertion.assertSkip(skipActiveProfile);}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-558") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZC107_AddActiveVoucherProfile() throws InterruptedException {
		
			final String methodName = "Test_AddActiveVoucherProfile";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS107");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		int ACTIVE_PROFILE = Integer.parseInt(_masterVO.getClientDetail("ACTIVE_PROFILE"));
		if(ACTIVE_PROFILE == 1) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
			RandomGeneration randomGeneration = new RandomGeneration();
			
			initiateMap = vmsMap.defaultMap();
			
			initiateMap.put("scenario", "");
			initiateMap.put("selectProfile", "");
			initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
			
			String message = MessagesDAO.prepareMessageByKey("vomsproduct.addactiveproduct.err.msg.selectprofile");
			
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else {Assertion.assertSkip(skipActiveProfile);}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-559") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZD108_AddActiveVoucherProfile() throws InterruptedException {
		
			final String methodName = "Test_AddActiveVoucherProfile";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS108");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		int ACTIVE_PROFILE = Integer.parseInt(_masterVO.getClientDetail("ACTIVE_PROFILE"));
		if(ACTIVE_PROFILE == 1) {
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
	
		initiateMap.put("scenario", "Applicable Date less than Current Date");
		initiateMap.put("selectProfile", "");
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addactiveproduct.err.msg.appdatenotlesscurr");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}else {Assertion.assertSkip(skipActiveProfile);}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-560") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZE109_InitiateOrder() throws InterruptedException {
		
			final String methodName = "Test_InitiateOrder";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS109");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("voucherType", "");
		initiateMap.put("categoryName","SUADM");
		initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("vomsbatches.vouchertype.notselected");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-561") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZF110_InitiateOrder() throws InterruptedException {
		
			final String methodName = "Test_InitiateOrder";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS110");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("denomination", "");
		initiateMap.put("quantity", "");
		initiateMap.put("categoryName","SUADM");
		initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.enterdata");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-562") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZG111_InitiateOrder() throws InterruptedException {
		
			final String methodName = "Test_InitiateOrder";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS111");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("denomination", "");
		initiateMap.put("categoryName","SUADM");
		initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.enterdata");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-564") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZH112_InitiateOrder() throws InterruptedException {
		
			final String methodName = "Test_InitiateOrder";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS112");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("quantity", "");
		initiateMap.put("categoryName","SUADM");
		initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantlesszero", "1");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-565") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZI113_InitiateOrder() throws InterruptedException {
		
			final String methodName = "Test_InitiateOrder";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS113");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("quantity", "0");
		initiateMap.put("categoryName","SUADM");
		initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantlesszero", "1");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-566") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZJ114_Approval1() throws InterruptedException {
		
			final String methodName = "Test_Approval1";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS114");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("voucherType", "");
		initiateMap.put("productID", "");
		initiateMap.put("categoryName","SUADM");
		initiateMap = vms.voucherGenerationApproval1(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("batcho2c.selectcategoryforbatcho2c.label.producttype"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	/*@Test
	public void CASEZZZZK115_Approval1() throws InterruptedException {
		
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS115");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT]VMS");
			testCaseCounter = true;
		}
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("voucherType", "");
		initiateMap.put("categoryName","SUADM");
		initiateMap = vms.voucherGenerationApproval1(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vmcategory.selectcategoryforvoms.label.denomcat"));
		
		if(initiateMap.get("Message").equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + initiateMap.get("Message") + "]");
		    currentNode.log(Status.FAIL, "Message Validation Failed");
		}
	Log.endTestCase(methodName);
	}
	*/
	@Test
	@TestManager(TestKey = "PRETUPS-569") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZL116_Approval1() throws InterruptedException {
		
			final String methodName = "Test_Approval1";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS116");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("productID", "");
		initiateMap.put("categoryName","SUADM");
		initiateMap = vms.voucherGenerationApproval1(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("batcho2c.selectcategoryforbatcho2c.label.producttype"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-570") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZM117_Approval1() throws InterruptedException {
		
			final String methodName = "Test_Approval1";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS117");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		initiateMap.put("quantity", "");
		initiateMap.put("remarks", "");
		initiateMap = vms.voucherGenerationApproval1(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.quantityrequired");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-571") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZN118_Approval1() throws InterruptedException {
		
			final String methodName = "Test_Approval1";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS118");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		initiateMap.put("quantity", "");
		initiateMap = vms.voucherGenerationApproval1(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.quantityrequired");
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-572") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZO119_Approval1() throws InterruptedException {
		
			final String methodName = "Test_Approval1";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS119");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		initiateMap.put("quantity", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.voucherGenerationApproval1(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone.approve");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-573") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZP120_Approval1() throws InterruptedException {
		
			final String methodName = "Test_Approval1";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS120");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		initiateMap.put("quantity", randomGeneration.randomNumeric(3)+"!@#$");
		initiateMap = vms.voucherGenerationApproval1(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone.approve");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-574") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZQ121_Approval1() throws InterruptedException {
		
		final String methodName = "Test_Approval1";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS121");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		String minQuantity = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_PROFILE_MIN_REORDERQTY);
		initiateMap.put("quantity", minQuantity);
		initiateMap.put("remarks", randomGeneration.randomNumeric(1)+"!@#$");
		initiateMap = vms.voucherGenerationApproval1(initiateMap,"");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-575") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZR122_Approval1() throws InterruptedException {
		
		final String methodName = "Test_Approval1";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS122");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		String minQuantity = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_PROFILE_MIN_REORDERQTY);
		initiateMap.put("quantity", minQuantity);
		initiateMap.put("remarks", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.voucherGenerationApproval1(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-576") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZS123_Approval1() throws InterruptedException {
		
			final String methodName = "Test_Approval1";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS123");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		String minQuantity = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_PROFILE_MIN_REORDERQTY);
		initiateMap.put("quantity", minQuantity);
		initiateMap.put("remarks", "");
		initiateMap = vms.voucherGenerationApproval1(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-577") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZT124_Approval2() throws InterruptedException {
		
			final String methodName = "Test_Approval2";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS124");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("voucherType", "");
		initiateMap.put("productID", "");
		initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("batcho2c.selectcategoryforbatcho2c.label.producttype"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-578") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZU125_Approval2() throws InterruptedException {
		
			final String methodName = "Test_Approval2";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS125");
		moduleCode = CaseMaster1.getModuleCode();

		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("productID", "");
		initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("batcho2c.selectcategoryforbatcho2c.label.producttype"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-579") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZV126_Approval2() throws InterruptedException {
		
			final String methodName = "Test_Approval2";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS126");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("quantity2", "");
		initiateMap.put("remarks", "");
		int maxApprovalLevel=0;
		if(initiateMap.get("categoryName").equals("SUADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
	
		if(maxApprovalLevel > 1)
		{
			initiateMap = vms.voucherGenerationApproval2Negative(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.quantityrequired");
			
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else
			 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		
		
		
		
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-581") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZW127_Approval2() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
			final String methodName = "Test_Approval2";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS127");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		 if(maxApprovalLevel > 1)
			{
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("quantity2", "");
		initiateMap = vms.voucherGenerationApproval2Negative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.quantityrequired");
		
		if(initiateMap.get("Message").equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + initiateMap.get("Message") + "]");
		    currentNode.log(Status.FAIL, "Message Validation Failed");
		}
	Assertion.assertEquals(initiateMap.get("Message"), message);
			}
		 else
				Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
	Assertion.completeAssertions();
	
	
    Log.endTestCase(methodName);
    	
		
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-582") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZX128_Approval2() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
			final String methodName = "Test_Approval2";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS128");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		 if(maxApprovalLevel > 1)
			{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("quantity2", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.voucherGenerationApproval2Negative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone.approve");
		
		Log.endTestCase(methodName);Assertion.assertEquals(initiateMap.get("Message"), message);
			}
		 else
				Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-583") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZY129_Approval2() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		final String methodName = "Test_Approval2";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS129");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 1)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("quantity2", randomGeneration.randomNumeric(1)+"!@#$");
		initiateMap = vms.voucherGenerationApproval2Negative(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone.approve");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		 else
				Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-584") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZ130_Approval2() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		final String methodName = "Test_Approval2";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS130");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 1)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		String minQuantity = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_PROFILE_MIN_REORDERQTY);
		initiateMap.put("quantity", minQuantity);
		initiateMap.put("remarks", randomGeneration.randomNumeric(1)+"!@#$");
		initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else
			Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
		
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-585") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZA131_Approval2() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		final String methodName = "Test_Approval2";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS131");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		if(maxApprovalLevel > 1)
		{
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		vms.voucherGenerationApproval1(initiateMap,"");
		String minQuantity = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_PROFILE_MIN_REORDERQTY);
		initiateMap.put("quantity", minQuantity);
		initiateMap.put("remarks", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else
			Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-586") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZB132_Approval2() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
			final String methodName = "Test_Approval2";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS132");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		if(maxApprovalLevel > 1)
		{
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		vms.voucherGenerationApproval1(initiateMap,"");
		String minQuantity = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_PROFILE_MIN_REORDERQTY);
		initiateMap.put("quantity", minQuantity);
		initiateMap.put("remarks", "");
		initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else
			Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		Assertion.completeAssertions();
		
	    Log.endTestCase(methodName);
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-587") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZC133_Approval3() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS133");
		moduleCode = CaseMaster1.getModuleCode();
			final String methodName = "Test_Approval3";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 2)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("voucherType", "");
		initiateMap.put("productID", "");
		initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("batcho2c.selectcategoryforbatcho2c.label.producttype"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		
		}
		else
			Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-588") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZD134_Approval3() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS134");
		moduleCode = CaseMaster1.getModuleCode();
			final String methodName = "Test_Approval3";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 2)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("productID", "");
		initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("batcho2c.selectcategoryforbatcho2c.label.producttype"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else
			Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-589") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZE135_Approval3() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS135");
		moduleCode = CaseMaster1.getModuleCode();
			final String methodName = "Test_Approval3";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 2)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("quantity", "");
		initiateMap.put("remarks", "");
		initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.quantityrequired");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else
          Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-590") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZF136_Approval3() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS136");
		moduleCode = CaseMaster1.getModuleCode();
			final String methodName = "Test_Approval3";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 2)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("quantity", "");
		initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.quantityrequired");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else
			  Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		
		Assertion.completeAssertions();
	    Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-591") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZG137_Approval3() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS137");
		moduleCode = CaseMaster1.getModuleCode();
			final String methodName = "Test_Approval3";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 2)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("quantity", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
				
		String message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone.approve");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else
         Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-592") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZH138_Approval3() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS138");
		moduleCode = CaseMaster1.getModuleCode();
			final String methodName = "Test_Approval3";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 2)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("quantity", randomGeneration.randomNumeric(1)+"!@#$");
		initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
		
		String message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone.approve");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else
        Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-593") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZI139_Approval3() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS139");
		moduleCode = CaseMaster1.getModuleCode();
			final String methodName = "Test_Approval3";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 2)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("quantity", "1");
		initiateMap.put("remarks", randomGeneration.randomNumeric(1)+"!@#$");
		initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else
		Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-594") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZJ140_Approval3() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS140");
		moduleCode = CaseMaster1.getModuleCode();
			final String methodName = "Test_Approval3";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 2)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		vms.voucherGenerationApproval1(initiateMap,"");
		vms.voucherGenerationApproval2(initiateMap,"");
		initiateMap.put("quantity", "1");
		initiateMap.put("remarks", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else
			  Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		
					Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-595") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZK141_Approval3() throws InterruptedException {
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS141");
		moduleCode = CaseMaster1.getModuleCode();
			final String methodName = "Test_Approval3";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 2)
		{
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		vms.voucherGenerationInitiate(initiateMap,"");
		vms.voucherGenerationApproval1(initiateMap,"");
		vms.voucherGenerationApproval2(initiateMap,"");
		initiateMap.put("quantity", "1");
		initiateMap.put("remarks", "");
		initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else
			  Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
     Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-596") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZL142_CreateBatch() throws InterruptedException {
		
			final String methodName = "Test_CreateBatch";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS142");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		CreateBatchForVoucherDownload createBatchForVoucherDownload = new CreateBatchForVoucherDownload(driver);
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("voucherType", "");
		initiateMap.put("denomination", "");
		initiateMap.put("quantity", "");
		initiateMap.put("remarks", "");
		initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
		String message="";
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		boolean flag =createBatchForVoucherDownload.isVoucherTypeAvailable();
		if(defaultValue.equalsIgnoreCase("true")) {
			message = MessagesDAO.prepareMessageByKey("voucher.download.label.voucher.type.required");
		}
		else if (!flag) {
		message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.enterdata");
		}
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-597") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZM143_CreateBatch() throws InterruptedException {
		
			final String methodName = "Test_CreateBatch";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS143");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("denomination", "");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
			}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
		String message="";
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		if(defaultValue.equalsIgnoreCase("true")) {
			message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.enterdata1");
		}
		else {
		message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.enterdata");
		}
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-598") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZN144_CreateBatch() throws InterruptedException {
		
			final String methodName = "Test_CreateBatch";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS144");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("quantity", "");
		String message="";
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		if(defaultValue.equalsIgnoreCase("false")) {
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
				message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.enterdata");
				Assertion.assertEquals(initiateMap.get("Message"), message);
				}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
					message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.enterdata");
					Assertion.assertEquals(initiateMap.get("Message"), message);
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		}
		else {
			Assertion.assertSkip("Not valid case");
		}
		
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-599") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZO145_CreateBatch() throws InterruptedException {
		
			final String methodName = "Test_CreateBatch";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS145");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("quantity", "0");
		String message="";
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		if(defaultValue.equalsIgnoreCase("false")) {
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
				message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantlesszero","1");
				Assertion.assertEquals(initiateMap.get("Message"), message);
				}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
					message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantlesszero","1");
					Assertion.assertEquals(initiateMap.get("Message"), message);
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		}
		
		else {
			Assertion.assertSkip("Not valid case");
		}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-600") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZO146_CreateBatch() throws InterruptedException {
		
		final String methodName = "Test_CreateBatch";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS146");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("quantity", randomGeneration.randomNumeric(1)+"."+randomGeneration.randomNumeric(4));
		String message="";
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		if(defaultValue.equalsIgnoreCase("false")) {
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
				message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone","1");
				Assertion.assertEquals(initiateMap.get("Message"), message);
				}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
					message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone","1");
					Assertion.assertEquals(initiateMap.get("Message"), message);
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		}
		else {
			Assertion.assertSkip("Not valid case");
		}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1163") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZP147_CreateBatch() throws InterruptedException {
		
		final String methodName = "Test_CreateBatch";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS147");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("quantity", randomGeneration.randomNumeric(1)+"."+randomGeneration.randomNumeric(2));
		String message="";
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		if(defaultValue.equalsIgnoreCase("false")) {
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
				message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone","1");
				Assertion.assertEquals(initiateMap.get("Message"), message);
				}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
					message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone","1");
					Assertion.assertEquals(initiateMap.get("Message"), message);
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		}
		else {
			Assertion.assertSkip("Not Valid Case");
		}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-601") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZQ148_CreateBatch() throws InterruptedException {
		
		final String methodName = "Test_CreateBatch";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS148");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("quantity", "-"+randomGeneration.randomNumeric(2));
		String message="";
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		if(defaultValue.equalsIgnoreCase("false")) {
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
				message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantlesszero","1");
				Assertion.assertEquals(initiateMap.get("Message"), message);
				}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
					message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantlesszero","1");
					Assertion.assertEquals(initiateMap.get("Message"), message);
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}	
		}
		else{
			Assertion.assertSkip("Not Valid Case");
		}Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-602") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZR149_CreateBatch() throws InterruptedException {
		
		final String methodName = "Test_CreateBatch";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS149");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("quantity", "-"+randomGeneration.randomNumeric(1)+"."+randomGeneration.randomNumeric(2));
		String message="";
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		if(defaultValue.equalsIgnoreCase("false")) {
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
				message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone","1");
				Assertion.assertEquals(initiateMap.get("Message"), message);
				}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
					message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone","1");
					Assertion.assertEquals(initiateMap.get("Message"), message);
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		
		}
		else {
			Assertion.assertSkip("Not Valid Case");
		}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-603") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZS150_CreateBatch() throws InterruptedException {
		
		final String methodName = "Test_CreateBatch";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS150");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("quantity", randomGeneration.randomAlphaNumeric(4));
		String message="";
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		if(defaultValue.equalsIgnoreCase("false")) {
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
				message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone","1");
				Assertion.assertEquals(initiateMap.get("Message"), message);
				}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
					message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone","1");
					Assertion.assertEquals(initiateMap.get("Message"), message);
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}				
		}
		else {
			Assertion.assertSkip("Not Valid Case");
		}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-604") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZT151_CreateBatch() throws InterruptedException {
		
		final String methodName = "Test_CreateBatch";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS151");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("quantity", randomGeneration.randomNumeric(2)+"!@#$");
		String message="";
		String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DOWNLD_BATCH_BY_BATCHID);
		if(defaultValue.equalsIgnoreCase("false")) {
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
				message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone","1");
				Assertion.assertEquals(initiateMap.get("Message"), message);
				}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.createBatchForVoucherDownload(initiateMap,"");
					message = MessagesDAO.prepareMessageByKey("voms.orderinitiate.error.quantumericone","1");
					Assertion.assertEquals(initiateMap.get("Message"), message);
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}					
		}
		else {
			Assertion.assertSkip("Not a valid case");
		}
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-605") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZU152_VoucherDownload() throws InterruptedException {
			final String methodName = "Test_VoucherDownload";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS152");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("downLoadFromDate", "VoucherNotSelected");
		initiateMap.put("downLoadToDate", "VoucherNotSelected");
		initiateMap.put("viewBatchFor", "");
		initiateMap = vms.vomsVoucherDownloadNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("viewbatches.msg.view.batches.required");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-606") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZV153_VoucherDownload() throws InterruptedException {
			final String methodName = "Test_VoucherDownload";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS153");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("downLoadFromDate", "");
		initiateMap.put("downLoadToDate", "From Date Null");
		initiateMap = vms.vomsVoucherDownloadNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("voms.validateviewbatch.error.fromDate");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-607") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZW154_VoucherDownload() throws InterruptedException {
		
			final String methodName = "Test_VoucherDownload";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS154");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "");
		initiateMap.put("downLoadFromDate", "To Date is null");
		initiateMap.put("downLoadToDate", "");
		initiateMap = vms.vomsVoucherDownloadNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("voms.validateviewbatch.error.toDate");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-608") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZX155_VoucherDownload() throws InterruptedException {
			final String methodName = "Test_VoucherDownload";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS155");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		
		initiateMap.put("scenario", "From date greater than to Date");
		initiateMap.put("downLoadFromDate", "From date greater than to Date");
		initiateMap.put("downLoadToDate", "From date greater than to Date");
		initiateMap = vms.vomsVoucherDownloadNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("pretups.btsl.error.msg.fromdatebeforetotdate");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-609") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZY156_VoucherDownload() throws InterruptedException {
			final String methodName = "Test_VoucherDownload";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS156");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "To date greater than to Current Date");
		initiateMap.put("downLoadFromDate", "To date greater than to Current Date");
		initiateMap.put("downLoadToDate", "To date greater than to Current Date");
		initiateMap = vms.vomsVoucherDownloadNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.todatebeforecurrentdate");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-610") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ157_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS157");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("voucherType", "");
		initiateMap.put("fromSerialNumber", "");
		initiateMap.put("toSerialNumber", "");
		initiateMap.put("numberOfVouchers", "");
		initiateMap.put("mrp", "");
		initiateMap.put("activeProfile", "");
		initiateMap.put("voucherStatus", "");
		initiateMap = vms.changeOtherStatusNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vmvoms.selectcategory.label.denominationcat"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-611") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ158_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS158");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("fromSerialNumber", "");
		initiateMap.put("toSerialNumber", "");
		initiateMap.put("numberOfVouchers", "");
		initiateMap.put("mrp", "");
		initiateMap.put("activeProfile", "");
		initiateMap.put("voucherStatus", "");
		initiateMap = vms.changeOtherStatusNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.fromserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-612") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ159_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS159");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("toSerialNumber", "");
		initiateMap.put("numberOfVouchers", "");
		initiateMap.put("mrp", "");
		initiateMap.put("voucherStatus", "");
		initiateMap = vms.changeOtherStatusNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.toserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-613") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ160_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS160");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("numberOfVouchers", "");
		initiateMap.put("mrp", "");
		initiateMap.put("voucherStatus", "");
		initiateMap = vms.changeOtherStatusNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.noofvoucher"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	   Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-614") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ161_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS161");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("mrp", "");
		initiateMap.put("voucherStatus", "");
		initiateMap = vms.changeOtherStatusNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-615") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ162_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS161");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("voucherStatus", "");
		initiateMap = vms.changeOtherStatusNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.status"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-616") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ163_ChangeOtherStatus() throws InterruptedException {
		final String methodName = "Test_ChangeOtherStatus";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS163");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("fromSerialNumber", randomGeneration.randomAlphaNumeric(6));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.fromserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-617") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ164_ChangeOtherStatus() throws InterruptedException {
		final String methodName = "Test_ChangeOtherStatus";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS164");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("fromSerialNumber", randomGeneration.randomNumeric(4)+" "+randomGeneration.randomNumeric(4));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.fromserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-618") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ165_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS165");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("fromSerialNumber", randomGeneration.randomNumeric(4)+"."+randomGeneration.randomNumeric(4));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}	
	
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.fromserial"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-619") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ166_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS166");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("fromSerialNumber", randomGeneration.randomNumeric(4)+"!@#$");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}	
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.fromserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-620") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ167_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS167");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("fromSerialNumber", randomGeneration.randomAlphabets(6));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}	
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.fromserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-621") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ168_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS168");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		String minSerialNumberLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_SNO_MIN_LENGTH);
		String maxSerialNumberLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_SNO_MAX_LENGTH);
		initiateMap = vmsMap.defaultMap();
		int minLength = Integer.parseInt(minSerialNumberLength);
		initiateMap.put("fromSerialNumber", randomGeneration.randomNumberWithoutZero(minLength - 1));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}	
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.fromseriallength", minSerialNumberLength, maxSerialNumberLength);
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-635") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ169_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS169");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		String minSerialNumberLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_SNO_MIN_LENGTH);
		initiateMap = vmsMap.defaultMap();
		int minLength = Integer.parseInt(minSerialNumberLength);
		initiateMap.put("fromSerialNumber", randomGeneration.randomNumberWithoutZero(minLength));
		initiateMap.put("toSerialNumber", randomGeneration.randomNumberWithoutZero(minLength+1));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}	
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.fromtoseriallength");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-636") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ170_ChangeOtherStatus() throws InterruptedException {
		final String methodName = "Test_ChangeOtherStatus";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS170");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		String minSerialNumberLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_SNO_MIN_LENGTH);
		String maxSerialNumberLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_SNO_MAX_LENGTH);
		if(minSerialNumberLength.equals(maxSerialNumberLength))
			currentNode.log(Status.PASS, "MIN AND MAX SERIAL NUMBER LENGTH IS SAME");
		else
		{
		initiateMap = vmsMap.defaultMap();
		int minLength = Integer.parseInt(minSerialNumberLength);
		String toSerialNumber = randomGeneration.randomNumberWithoutZero(minLength);
		Long toSerialNo = Long.parseLong(toSerialNumber);
		long fromSerialNo = toSerialNo + 1;
		String fromSerialNumber = Long.toString(fromSerialNo);
		initiateMap.put("scenario", "Invalid From Serial Number");
		initiateMap.put("fromSerialNumber", fromSerialNumber);
		initiateMap.put("toSerialNumber", toSerialNumber);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}	
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.toserialless");
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-637") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ171_ChangeOtherStatus() throws InterruptedException {
		final String methodName = "Test_ChangeOtherStatus";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS171");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "Alphanumeric To Serial Number");
		initiateMap.put("toSerialNumber", randomGeneration.randomAlphaNumeric(6));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}	
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.toserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-638") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ172_ChangeOtherStatus() throws InterruptedException {
		final String methodName = "Test_ChangeOtherStatus";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS172");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "Space in To Serial Number");
		initiateMap.put("toSerialNumber", randomGeneration.randomNumeric(4)+" "+randomGeneration.randomNumeric(4));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}	
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.toserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-639") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ173_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS173");
		moduleCode = CaseMaster1.getModuleCode();
		
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "Decimal in To Serial Number");
		initiateMap.put("toSerialNumber", randomGeneration.randomNumeric(4)+"."+randomGeneration.randomNumeric(4));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}	
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.toserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-640") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ174_ChangeOtherStatus() throws InterruptedException {
		final String methodName = "Test_ChangeOtherStatus";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS174");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("scenario", "Special Characters in To Serial Number");
		initiateMap.put("toSerialNumber", randomGeneration.randomNumeric(4)+"!@#$");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.toserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-641") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ175_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS175");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("toSerialNumber", randomGeneration.randomAlphabets(6));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.toserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-642") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ176_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS176");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		String minSerialNumberLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_SNO_MIN_LENGTH);
		String maxSerialNumberLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_SNO_MAX_LENGTH);
		initiateMap = vmsMap.defaultMap();
		int minLength = Integer.parseInt(minSerialNumberLength);
		initiateMap.put("toSerialNumber", randomGeneration.randomNumberWithoutZero(minLength - 1));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}					
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.toseriallength", minSerialNumberLength, maxSerialNumberLength);
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-643") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ177_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS177");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("numberOfVouchers", randomGeneration.randomAlphaNumeric(6));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}					
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.noofvoucher"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-644") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ178_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS178");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("numberOfVouchers", randomGeneration.randomNumeric(4)+" "+randomGeneration.randomNumeric(4));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.noofvoucher"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-645") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ179_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS179");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("numberOfVouchers", randomGeneration.randomNumeric(4)+"."+randomGeneration.randomNumeric(4));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}				
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.noofvoucher"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-646") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ180_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS180");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("numberOfVouchers", randomGeneration.randomNumeric(4)+"!@#$");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.noofvoucher"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-647") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ181_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS181");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("numberOfVouchers", randomGeneration.randomAlphabets(6));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.noofvoucher"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-648") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ182_ChangeOtherStatus() throws InterruptedException {
		final String methodName = "Test_ChangeOtherStatus";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS182");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		String voucherNumber = initiateMap.get("numberOfVouchers");
		initiateMap.put("numberOfVouchers", "-"+voucherNumber);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("voms.changevoucherstatus.label.noofvoucher"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-649") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ183_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS183");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		String fromSerialNumber = initiateMap.get("fromSerialNumber");
		String toSerialNumber = initiateMap.get("toSerialNumber");
		long fromSrNo = Long.parseLong(fromSerialNumber);
   		long toSrNo = Long.parseLong(toSerialNumber);
   		long vouchers = (toSrNo - fromSrNo) + 6;
   	    String numberOfVouchers = Long.toString(vouchers);
		initiateMap.put("numberOfVouchers", numberOfVouchers);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.totalvouchnotequal");
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-650") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ184_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS184");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("mrp", randomGeneration.randomAlphaNumeric(6));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}					
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-651") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ185_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS185");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("mrp", randomGeneration.randomNumeric(4)+" "+randomGeneration.randomNumeric(4));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-652") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ186_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS186");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("mrp", randomGeneration.randomNumeric(4)+"."+randomGeneration.randomNumeric(4));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.nobatchfound");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-653") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ187_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS187");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("mrp", randomGeneration.randomNumeric(4)+"!@#$");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-654") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ188_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS188");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("mrp", randomGeneration.randomAlphabets(6));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-655") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ189_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS189");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		String mrp = initiateMap.get("mrp");
		initiateMap.put("mrp", "-"+mrp);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-656") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ190_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS190");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		String fromSerialNumber = initiateMap.get("fromSerialNumber");
		initiateMap.put("fromSerialNumber", "-"+fromSerialNumber);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.fromserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-657") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ191_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS191");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		
		initiateMap = vmsMap.defaultMap();
		String toSerialNumber = initiateMap.get("toSerialNumber");
		initiateMap.put("toSerialNumber", "-"+toSerialNumber);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.toserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-658") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ192_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS192");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("mrp", "-"+randomGeneration.randomNumeric(2)+"."+randomGeneration.randomNumeric(2));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.mrp"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-659") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ193_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS193");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("fromSerialNumber", "-"+randomGeneration.randomNumeric(2)+"."+randomGeneration.randomNumeric(2));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.fromserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1164") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ194_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS194");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("toSerialNumber", "-"+randomGeneration.randomNumeric(2)+"."+randomGeneration.randomNumeric(2));
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
		String message = MessagesDAO.prepareMessageByKey("voms.changestatus.error.msg.numeric", MessagesDAO.getLabelByKey("voms.confirmchangevoucherstatus.label.toserial"));
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-660") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ195_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_ChangeOtherStatus";
			Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS195");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String product = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(product,"CU");
   		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(product,"CU");
   		long fromSrNo = Long.parseLong(fromSerialNumber);
   		long toSrNo = Long.parseLong(toSerialNumber);
   		long vouchers = (toSrNo - fromSrNo) + 1;
   		String numberOfVouchers = Long.toString(vouchers);
   		initiateMap.put("fromSerialNumber", fromSerialNumber);
   		initiateMap.put("toSerialNumber", toSerialNumber);
   		initiateMap.put("numberOfVouchers", numberOfVouchers);
   		
   		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else if (initiateMap.get("type").equals("DT") || initiateMap.get("type").equals("D")) {
				initiateMap.put("voucherStatus",PretupsI.STOLEN);
				initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}			
  		String voucherStatus = DBHandler.AccessHandler.getVoucherStatus(fromSerialNumber);
		Assertion.assertEquals(voucherStatus, "CU");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-661") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ196_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC01");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, "");
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
            Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-662") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ197_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC02");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, randomGeneration.randomAlphaNumeric(6));
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
            Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-663") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ198_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC03");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+" "+ randomGeneration.randomNumeric(6);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-664") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZ199_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC04");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+"."+ randomGeneration.randomNumeric(6);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-665") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA200_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC05");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn ="-"+_masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+randomGeneration.randomNumeric(6);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-666") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA201_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC06");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn ="-"+_masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+"."+randomGeneration.randomNumeric(5);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-667") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA202_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC07");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, randomGeneration.randomAlphabets(6));
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-668") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA203_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC08");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+"@$"+randomGeneration.randomNumeric(4);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-669") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA204_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC09");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
		    String prefix = null;
		    while(true)
		    {
		    	prefix = randomGeneration.randomNumeric(2);
		    	if(prefix!=_masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX))
		    		break;
		    }
			String msisdn = prefix+randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertContainsEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-670") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA205_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC10");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertContainsEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-671") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA206_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC11");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		String minLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.MIN_MSISDN_LENGTH);
		int minMSISDNLength = Integer.parseInt(minLength);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + randomGeneration.randomNumeric(minMSISDNLength - 4);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-672") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA207_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC12");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		String maxLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.MAX_MSISDN_LENGTH);
		int maxMSISDNLength = Integer.parseInt(maxLength);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + randomGeneration.randomNumeric(maxMSISDNLength + 4);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
            Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-673") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA208_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC13");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
	
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		  if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
		         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
		
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			apiData.put(voucherConsumptionAPI.EXTNWCODE, "");
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-674") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA209_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC14");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		  if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
		         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			apiData.put(voucherConsumptionAPI.EXTNWCODE, randomGeneration.randomAlphabets(3));
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
			Assertion.completeAssertions();
			
            Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-675") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA210_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC15");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		  if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
		         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			apiData.put(voucherConsumptionAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE)+" "+randomGeneration.randomAlphabets(1));
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);

			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-676") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA211_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC16");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			apiData.put(voucherConsumptionAPI.EXTNWCODE, randomGeneration.randomAlphabets(1)+"$"+randomGeneration.randomAlphabets(1));
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
			Assertion.completeAssertions();
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-677") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA212_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC17");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		apiData.put(voucherConsumptionAPI.TYPE, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
						Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
						Assertion.completeAssertions();
						DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-678") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA213_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC18");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		apiData.put(voucherConsumptionAPI.TYPE, randomGeneration.randomAlphabets(6));
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
						Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
						Assertion.completeAssertions();
						DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-679") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA214_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC19");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		apiData.put(voucherConsumptionAPI.EXTREFNUM, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
						Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
						Assertion.completeAssertions();
						DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-680") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA215_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC20");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		apiData.put(voucherConsumptionAPI.EXTREFNUM, randomGeneration.randomAlphaNumeric(6));
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
						Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
						Assertion.completeAssertions();
						DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-681") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA216_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC21");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		  if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
		         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
						Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
						Assertion.completeAssertions();
						DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-682") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA217_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC22");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		  if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
		         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		apiData.put(voucherConsumptionAPI.PIN, randomGeneration.randomNumeric(6));
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
						Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
						Assertion.completeAssertions();
						DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);

               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-683") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA218_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC23");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		  if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
		         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
						Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
						Assertion.completeAssertions();
						DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	//@Test
	@TestManager(TestKey = "PRETUPS-684") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA219_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC24");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			  String prefix = null;
			    while(true)
			    {
			    	prefix = randomGeneration.randomNumeric(2);
			    	if(prefix!=_masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX))
			    		break;
			    }
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			apiData.put(voucherConsumptionAPI.MSISDN2, prefix+randomGeneration.randomNumeric(8));
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertContainsEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-685") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA220_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC25");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherConsumptionAPI";
			Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, "");
			apiData.put(voucherConsumptionAPI.MSISDN2, "");
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
						Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
						Assertion.completeAssertions();
			
              Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-686") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA221_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC26");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			String minLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.MIN_MSISDN_LENGTH);
			int minMSISDNLength = Integer.parseInt(minLength);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			apiData.put(voucherConsumptionAPI.MSISDN2, _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(minMSISDNLength - 4));
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			
            Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-687") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA222_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC27");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			String maxLength = DBHandler.AccessHandler.getSystemPreference(PretupsI.MAX_MSISDN_LENGTH);
			int maxMSISDNLength = Integer.parseInt(maxLength);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			apiData.put(voucherConsumptionAPI.MSISDN2, _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(maxMSISDNLength + 4));
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
						Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-688") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA223_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC28");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		  if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
		         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-689") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA224_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC29");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			apiData.put(voucherConsumptionAPI.EXTREFNUM, "");
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-690") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA225_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC30");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, "");
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-691") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA226_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC31");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);

		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, randomGeneration.randomNumeric(10));
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-692") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA227_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC32");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);

		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "CU";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-693") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA228_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC33");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);

		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			if(pin==null)
				Assertion.assertSkip("No Voucher Exist in this State.");
			else
			{
				Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			}
			
			Assertion.completeAssertions();
            Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-694") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA229_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC34");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);

		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "WH";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			if(pin==null)
				Assertion.assertSkip("No Voucher Exist in this State.");
		   else
				Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-695") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA230_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC35");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "ST";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			if(pin==null)
				Assertion.assertSkip("No Voucher Exist in this State.");
			else
				Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			Assertion.completeAssertions();
			
            Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-697") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA231_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC36");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "OH";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			if(pin==null)
				Assertion.assertSkip("No Voucher Exist in this State.");
		    else
				Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-698") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA232_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC37");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);

		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "DA";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			if(pin==null)
				Assertion.assertSkip("No Voucher Exist in this State.");
		    else
				Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			Assertion.completeAssertions();
			
            Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-699") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA233_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC38");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);

		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "PE";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			if(pin==null)
				Assertion.assertSkip("No Voucher Exist in this State.");
		    else
		    	Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-700") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA234_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC39");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "PA";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			if(pin==null)
				Assertion.assertSkip("No Voucher Exist in this State.");
		    else
				Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			
			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-701") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA235_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC40");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "S";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			if(pin==null)
				Assertion.assertSkip("No Voucher Exist in this State.");
		    else
				Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-703") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA236_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC41");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberForExpiredDate(status);
			if(serialNumber==null)
			{
				Assertion.assertSkip("No Voucher is Expired in DataBase.");
			}
			else
			{
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertContainsEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			}
			Assertion.completeAssertions();
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-704") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA237_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC42");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);

		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, "");
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-705") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA238_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC43");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String sno = DBHandler.AccessHandler.getSerialNumberFromStatus("WH");
			if(sno==null)
			{
				Assertion.assertSkip("No valid Serial Number in System");
			} else {
				String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
				apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
				apiData.put(voucherConsumptionAPI.SERIALNUMBER, sno);
				apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
				String API = voucherConsumptionAPI.prepareAPI(apiData);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
				_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			}
			Assertion.completeAssertions();
            Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-706") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA239_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC44");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		apiData.put(voucherConsumptionAPI.LANGUAGE1, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
				Assertion.completeAssertions();
				DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-707") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA240_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC45");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
	
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		apiData.put(voucherConsumptionAPI.LANGUAGE2, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-708") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA241_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC46");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		apiData.put(voucherConsumptionAPI.LANGUAGE1, randomGeneration.randomNumeric(3));
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-709") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA242_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC47");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		apiData.put(voucherConsumptionAPI.LANGUAGE2, randomGeneration.randomNumeric(3));
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-710") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA243_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC48");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
			               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-711") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA244_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC49");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		apiData.put(voucherConsumptionAPI.INFO1, "");
		apiData.put(voucherConsumptionAPI.INFO2, "");
		apiData.put(voucherConsumptionAPI.INFO3, "");
		apiData.put(voucherConsumptionAPI.INFO4, "");
		apiData.put(voucherConsumptionAPI.INFO5, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-712") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA245_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC50");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		 if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-713") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA246_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC51");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
	         createVoucherCardGroup(initiateMap);
		
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		String serialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String damagedPin = pin.substring(5, 9);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, damagedPin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			DBHandler.AccessHandler.changeStatusSerialNumber(serialNumber, status);
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-714") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA247_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC52");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String damagedPin = pin.substring(5, 9);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, damagedPin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, randomGeneration.randomNumberWithoutZero(16));
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-715") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA247A_VoucherConsumptionAPI() throws InterruptedException {
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("EXTGWVC53");
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		
			String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatus(status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String minPinLengthForDamagedVoucher = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_DAMG_PIN_LNTH_ALLOW);
			int minLength = Integer.parseInt(minPinLengthForDamagedVoucher);
			String damagedPin = pin.substring(5, 6);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX)+ randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, damagedPin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, "");
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			_APIUtil.addExecutionRecord(CaseMaster, APIResponse);
			XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
			Assertion.assertEquals(xmlPath.get(voucherConsumptionAPI.TXNSTATUS).toString(), CaseMaster.getErrorCode());
			Assertion.completeAssertions();
			
               Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-716") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA248_CreateBatch() throws InterruptedException {
		
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS248");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
		if(HCPT_VMS == 1) {
			Assertion.assertSkip(skipForNetworkIN);
		}else{
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("batchType", "thirdparty");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			vms.addActiveVoucherProfileNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				vms.addActiveVoucherProfileNegative(initiateMap);
			}
		}			
		vms.voucherGenerationInitiate(initiateMap,"");
		vms.voucherGenerationApproval1(initiateMap,"");
		vms.voucherGenerationApproval2(initiateMap,"");
		vms.voucherGenerationApproval3(initiateMap,"");
		vms.voucherGenerationScriptExecution();
		if(BTSLUtil.isNullString(value)) {
			vms.createBatchForVoucherDownload(initiateMap,"");
			vms.vomsVoucherDownloadNegative(initiateMap);
			String product = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String fromSerialNumber = DBHandler.AccessHandler.getSerialNumber(product); 
			
			String expectedVoucherStatus = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_THIRDPARTY_STATUS);
			
			String currentStatus = DBHandler.AccessHandler.getVoucherStatus(fromSerialNumber);
			
			Assertion.assertEqualsIgnoreCase(expectedVoucherStatus, currentStatus);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				vms.createBatchForVoucherDownload(initiateMap,"");
				vms.vomsVoucherDownloadNegative(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
			String product = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String fromSerialNumber = DBHandler.AccessHandler.getSerialNumber(product); 
			
			String expectedVoucherStatus = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_THIRDPARTY_STATUS);
			
			String currentStatus = DBHandler.AccessHandler.getVoucherStatus(fromSerialNumber);
			
			Assertion.assertEqualsIgnoreCase(expectedVoucherStatus, currentStatus);
		}
		/*String product = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String fromSerialNumber = DBHandler.AccessHandler.getSerialNumber(product); 
		
		String expectedVoucherStatus = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOUCHER_THIRDPARTY_STATUS);
		
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(fromSerialNumber);
		
		Assertion.assertEqualsIgnoreCase(expectedVoucherStatus, currentStatus);*/
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-717") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA249_VoucherDenomination() throws InterruptedException {
		
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS249");
		moduleCode = CaseMaster1.getModuleCode();

		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		RandomGeneration randomGeneration = new RandomGeneration();
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("batchType", "thirdparty");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegative(initiateMap);
		initiateMap.put("activeProfile", randomGeneration.randomAlphaNumeric(6));
		initiateMap=vms.addVoucherProfileNegative(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.addnewproduct.err.msg.profileidexists");
		
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
	
	public static String[] getStatusBasedOnVoucherType(String type, String curStatus) {
        String allStatus = null;
        ArrayList<String> statusList = new ArrayList<String>();
        String[] array = {};
        try {
    		allStatus = getSystemPrefrenceOfVoucher(type);
    		if(!BTSLUtil.isNullString(allStatus)) {
    			statusList = new ArrayList<String>();
                String[] allStatusSplit = allStatus.split(PretupsI.COMMA);
                for(int i=0;i<allStatusSplit.length;i++) {
                	String pair = allStatusSplit[i];
                    String[] fromTo = pair.split(PretupsI.COLON);
                    if(fromTo[0].equals(curStatus)) {
                    	statusList.add(fromTo[1]);
                    }
                }
                if(statusList.size() > 0) {
                	array = statusList.toArray(new String[statusList.size()]);
                    return array;
                }
    		}
        } catch (Exception e) {
        
        }
        return array;
    }
    


   public static String getSystemPrefrenceOfVoucher(String voucherTypeCode) {
        HashMap<String,String> prefrencemap=new HashMap<String,String>();
        prefrencemap.put(PretupsI.VOUCHER_TYPE_PHYSICAL, DBHandler.AccessHandler.getSystemPreference(PretupsI.VMS_P_STATUS_CHANGE_MAP));
        prefrencemap.put(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL, DBHandler.AccessHandler.getSystemPreference(PretupsI.VMS_P_STATUS_CHANGE_MAP));
        prefrencemap.put(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC, DBHandler.AccessHandler.getSystemPreference(PretupsI.VMS_E_STATUS_CHANGE_MAP));
        prefrencemap.put(PretupsI.VOUCHER_TYPE_ELECTRONIC, DBHandler.AccessHandler.getSystemPreference(PretupsI.VMS_E_STATUS_CHANGE_MAP));
        prefrencemap.put(PretupsI.VOUCHER_TYPE_DIGITAL, DBHandler.AccessHandler.getSystemPreference(PretupsI.VMS_D_STATUS_CHANGE_MAP));
        prefrencemap.put(PretupsI.VOUCHER_TYPE_TEST_DIGITAL, DBHandler.AccessHandler.getSystemPreference(PretupsI.VMS_D_STATUS_CHANGE_MAP));
        return prefrencemap.get(voucherTypeCode);
    }
   
   public static boolean isValidCase(String type, String currentStatus, String toStatus) {
       String statusList[] = getStatusBasedOnVoucherType(type,currentStatus);
       List<String> statusArrayList = Arrays.asList(statusList);
       if(statusArrayList != null) {
    	   return statusArrayList.contains(toStatus);
       }
       return false;
   }

	
	@Test
	@TestManager(TestKey = "PRETUPS-718") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA250_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS250");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "ST";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status ST in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
				chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
				chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
			chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;
	
		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,"WH")) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status="WH";
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, status);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-719") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA251_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS251");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "EN";
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status EN in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.STOLEN);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);

		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-720") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA252_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS252");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "GE";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status GE in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.DISABLED);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeGeneratedStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeGeneratedStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		
		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-721") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA253_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS253");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status EN in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.DISABLED);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-722") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA254_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS254");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status EN in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.ONHOLD);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-723") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA255_ChangeOtherStatus() throws InterruptedException {
		final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS255");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "OH";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status OH in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.STOLEN);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-724") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA256_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS256");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "OH";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status OH in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.ENABLE);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-725") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA257_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS257");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "WH";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status WH in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.SUSPENDED);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-726") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA258_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS258");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status EN in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.SUSPENDED);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		
		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-727") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA259_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS259");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "OH";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status OH in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.SUSPENDED);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		if(currentStatus.equals(initiateMap.get("voucherStatus")))
			currentNode.log(Status.PASS, "Voucher status changed from OnHold to Suspended");
		else {
		    currentNode.log(Status.FAIL, "Unable to change Voucher status from OnHold to Suspended");
		}
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-728") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA260_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS260");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "OH";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status OH in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.DISABLED);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-729") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA261_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS261");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "WH";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status WH in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.DISABLED);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
//	@Test case is commented as this is not feasible as per the code of roadmap we can not change PE to GE
	//@TestManager(TestKey = "PRETUPS-730") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA262_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS262");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "PE";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status PE in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.GENERATED);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);

		Assertion.assertEquals(currentStatus, status);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-731") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA263_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS263");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "EN";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status EN in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.WAREHOUSE);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);

		Assertion.assertEquals(currentStatus, status);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-732") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA264_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS264");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "ST";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status ST in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.ONHOLD);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);

		Assertion.assertEquals(currentStatus, status);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-733") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA265_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS265");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "ST";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status ST in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.ENABLE);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		Assertion.assertEquals(currentStatus, status);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-734") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA266_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS266");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "S";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status S in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.WAREHOUSE);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);

		Assertion.assertEquals(currentStatus, status);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-736") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA267_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS267");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "S";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status S in Database");
		}
		else
		{
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.ENABLE);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
			Assertion.assertEquals(currentStatus, PretupsI.ENABLE);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);

					Assertion.assertEquals(currentStatus, PretupsI.ENABLE);
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		Thread.sleep(1000);
		
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-737") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA268_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS268");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "S";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status S in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.ONHOLD);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		if(currentStatus.equals(status))
			currentNode.log(Status.PASS, "Voucher status cannot be changed from S to OH or it can be change S to OH if MAP prefernce has this scenario");
		else {
		    currentNode.log(Status.FAIL, "Voucher status changed from S to OH");
		}
		
		Assertion.assertEquals(currentStatus, status);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-739") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA269_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS269");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "DA";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status DA in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.WAREHOUSE);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);
		
		if(currentStatus.equals(status))
			currentNode.log(Status.PASS, "Voucher status cannot be changed from DA to WH or it can be change DA to WH if MAP prefernce has this scenario");
		else {
		    currentNode.log(Status.FAIL, "Voucher status changed from DA to WH");
		}
		
		Assertion.assertEquals(currentStatus, status);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-740") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA270_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS270");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "DA";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status DA in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.ENABLE);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);

		Assertion.assertEquals(currentStatus, status);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-742") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA271_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS271");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "DA";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status DA in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.ONHOLD);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);

		Assertion.assertEquals(currentStatus, status);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-743") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA272_ChangeOtherStatus() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS272");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		String status = "PA";
		
		String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndVoucherType(status,initiateMap.get("voucherType"));
		if(serialNumber==null)
		{
			Assertion.assertSkip("No Voucher exists with status PA in Database");
		}
		else
		{
		initiateMap.put("fromSerialNumber", serialNumber);
		initiateMap.put("toSerialNumber", serialNumber);
		initiateMap.put("numberOfVouchers", "1");
		String productID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "PRODUCT_ID")[0];
		String profileName = DBHandler.AccessHandler.getProductNameFromVOMSProduct(productID);
		String MRP = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(serialNumber, "MRP")[0];
		long mrp = Long.parseLong(MRP)/100;
		String mrp1 =  Long.toString(mrp);
		initiateMap.put("activeProfile", profileName);
		initiateMap.put("mrp", mrp1);
		initiateMap.put("voucherStatus", PretupsI.ENABLE);
		initiateMap = vmsMap.defaultMapWithVoucherType(initiateMap,mrp1);
		if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_PHYSICAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_PHYSICAL))
			chooseMap = PretupsI.VMS_P_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_ELECTRONIC))
			chooseMap = PretupsI.VMS_E_STATUS_CHANGE_MAP;
		else if(initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_DIGITAL)||initiateMap.get("type").equalsIgnoreCase(PretupsI.VOUCHER_TYPE_TEST_DIGITAL))
		chooseMap = PretupsI.VMS_D_STATUS_CHANGE_MAP;

		String value = DBHandler.AccessHandler.getSystemPreference(chooseMap);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatusNegative(initiateMap);
			}
			else {
				if(isValidCase(initiateMap.get("type"),status,initiateMap.get("voucherStatus"))) {
					initiateMap = vms.changeOtherStatusNegative(initiateMap);
					status=initiateMap.get("voucherStatus");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
		Thread.sleep(1000);
		String currentStatus = DBHandler.AccessHandler.getVoucherStatus(serialNumber);

		Assertion.assertEquals(currentStatus, initiateMap.get("voucherStatus"));
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-745") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA273_ModifyDenomForActiveProf() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS273");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		int ACTIVE_PROFILE = Integer.parseInt(_masterVO.getClientDetail("ACTIVE_PROFILE"));
		if(ACTIVE_PROFILE == 1) {
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("scenario", "");
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			vms.addActiveVoucherProfileNegative(initiateMap);
			initiateMap=vms.modifyVoucherDenominationmrp(initiateMap);
			if(initiateMap.get("MessageStatus").equalsIgnoreCase("N"))
				currentNode.log(Status.PASS, "Denomination cannot be modified if voucher profile is already added in add active profile details.");
			else {
			    currentNode.log(Status.FAIL, "Denomination getting modified");
			}
			Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "N");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				vms.addActiveVoucherProfileNegative(initiateMap);
				initiateMap=vms.modifyVoucherDenominationmrp(initiateMap);
				if(initiateMap.get("MessageStatus").equalsIgnoreCase("N"))
					currentNode.log(Status.PASS, "Denomination cannot be modified if voucher profile is already added in add active profile details.");
				else {
				    currentNode.log(Status.FAIL, "Denomination getting modified");
				}
				Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "N");
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		/*if(initiateMap.get("MessageStatus").equalsIgnoreCase("N"))
			currentNode.log(Status.PASS, "Denomination cannot be modified if voucher profile is already added in add active profile details.");
		else {
		    currentNode.log(Status.FAIL, "Denomination getting modified");
		}
		Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "N");*/
		}else {Assertion.assertSkip(skipActiveProfile);}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-746") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA274_ModifyDenomForUniqueMRP() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS274");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
		initiateMap=vms.modifyVoucherDenomination(initiateMap);
		String message = MessagesDAO.prepareMessageByKey("vmcategory.modifysubcategoryforvoms.succ.msg.catmodisucc");
		if(initiateMap.get("MessageStatus").equalsIgnoreCase("Y")&& initiateMap.get("Message").equals(message))
			Assertion.assertPass("Denomination modified with Unique MRP");
		else {
			Assertion.assertFail("Denomination not getting modified");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-748") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA275_RejectAtApproval1() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS275");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap.put("scenario", "");
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			vms.addActiveVoucherProfileNegative(initiateMap);
			vms.voucherGenerationInitiate(initiateMap,"");
			initiateMap=vms.voucherGenerationrejectApproval1(initiateMap);
			Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				vms.addActiveVoucherProfileNegative(initiateMap);
				vms.voucherGenerationInitiate(initiateMap,"");
				initiateMap=vms.voucherGenerationrejectApproval1(initiateMap);
				Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
			}
			else {
				vms.voucherGenerationInitiate(initiateMap,"");
				initiateMap=vms.voucherGenerationrejectApproval1(initiateMap);
				Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
			}
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-749") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA276_AmmendRejectedApproval1() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS276");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			vms.addActiveVoucherProfileNegative(initiateMap);
			vms.voucherGenerationInitiate(initiateMap,"");
			vms.voucherGenerationrejectApproval1(initiateMap);
			initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
			
			String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.nobatchesfound");

			Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				vms.addActiveVoucherProfileNegative(initiateMap);
				vms.voucherGenerationInitiate(initiateMap,"");
				vms.voucherGenerationrejectApproval1(initiateMap);
				initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
				
				String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.nobatchesfound");

				Assertion.assertEquals(initiateMap.get("Message"), message);
			}
			else {
				vms.voucherGenerationInitiate(initiateMap,"");
				vms.voucherGenerationrejectApproval1(initiateMap);
				initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
				
				String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.nobatchesfound");

				Assertion.assertEquals(initiateMap.get("Message"), message);
			}
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-750") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA277_ModifyAtApproval1() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS277");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap.put("categoryName","SUADM");
		String minQuantity = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_PROFILE_MIN_REORDERQTY);
		int minQuantity1 = Integer.parseInt(minQuantity);
		String minNewQuanitiy =Integer.toString(minQuantity1 + 1);
		initiateMap.put("quantity", minNewQuanitiy);
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegative(initiateMap);
		int maxApprovalLevel=0;
		if(initiateMap.get("categoryName").equals("SUADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
	
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			vms.addActiveVoucherProfileNegative(initiateMap);
			vms.voucherGenerationInitiate(initiateMap,"");
			String quantity = initiateMap.get("quantity");
			int quantity1 = Integer.parseInt(quantity);
			String newQuantity = Integer.toString(quantity1 - 1);
			initiateMap.put("quantity", newQuantity);
			
			if(maxApprovalLevel > 0)
			{
				vms.voucherGenerationApproval1(initiateMap,"");
				Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
				if(maxApprovalLevel > 1)
				{
					initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
					Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
				}
				else
					 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			}
			else
				 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				vms.addActiveVoucherProfileNegative(initiateMap);
				vms.voucherGenerationInitiate(initiateMap,"");
				String quantity = initiateMap.get("quantity");
				int quantity1 = Integer.parseInt(quantity);
				String newQuantity = Integer.toString(quantity1 - 1);
				initiateMap.put("quantity", newQuantity);
				if(maxApprovalLevel > 0)
				{
					vms.voucherGenerationApproval1(initiateMap,"");
					Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
					if(maxApprovalLevel > 1)
					{
						initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
						Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
					}
					else
						 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
				}
				else
					 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			}
			else {
				vms.voucherGenerationInitiate(initiateMap,"");
				String quantity = initiateMap.get("quantity");
				int quantity1 = Integer.parseInt(quantity);
				String newQuantity = Integer.toString(quantity1 - 1);
				initiateMap.put("quantity", newQuantity);
				if(maxApprovalLevel > 0)
				{
					vms.voucherGenerationApproval1(initiateMap,"");
					Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
					if(maxApprovalLevel > 1)
					{
						initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
						Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
					}
					else
						 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
				}
				else
					 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			}
		}	
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-752") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA278_RejectApproval2() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS278");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		String minQuantity = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_PROFILE_MIN_REORDERQTY);
		int minQuantity1 = Integer.parseInt(minQuantity);
		String minNewQuanitiy =Integer.toString(minQuantity1 + 1);
		initiateMap.put("quantity", minNewQuanitiy);
		int maxApprovalLevel=0;
		if(initiateMap.get("categoryName").equals("SUADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			vms.addActiveVoucherProfileNegative(initiateMap);
			vms.voucherGenerationInitiate(initiateMap,"");
			if(maxApprovalLevel > 0)
			{
				vms.voucherGenerationApproval1(initiateMap,"");
				Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
				if(maxApprovalLevel > 1)
				{
					initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
					Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
				}
				else
					 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			}
			else
				 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				vms.addActiveVoucherProfileNegative(initiateMap);
				vms.voucherGenerationInitiate(initiateMap,"");
				if(maxApprovalLevel > 0)
				{
					vms.voucherGenerationApproval1(initiateMap,"");
					Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
					if(maxApprovalLevel > 1)
					{
						initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
						Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
					}
					else
						 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
				}
				else
					 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			}
			else {
				vms.voucherGenerationInitiate(initiateMap,"");
				if(maxApprovalLevel > 0)
				{
					vms.voucherGenerationApproval1(initiateMap,"");
					Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
					if(maxApprovalLevel > 1)
					{
						initiateMap = vms.voucherGenerationApproval2(initiateMap,"");
						Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
					}
					else
						 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
				}
				else
					 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			}
		}	
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-753") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA279_ModifyAtApproval2() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS279");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			vms.addActiveVoucherProfileNegative(initiateMap);
			vms.voucherGenerationInitiate(initiateMap,"");
			vms.voucherGenerationApproval1(initiateMap,"");
			String quantity = initiateMap.get("quantity");
			int quantity1 = Integer.parseInt(quantity);
			String newQuantity = Integer.toString(quantity1 - 1);
			vms.voucherGenerationApproval2(initiateMap,"");
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			int maxApprovalLevel = Integer.parseInt(approvalLevel);
			if(maxApprovalLevel > 2)
			{
				initiateMap.put("quantity", newQuantity);
				initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
				Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
				
			}
			else
			{
				Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			}
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				vms.addActiveVoucherProfileNegative(initiateMap);
				vms.voucherGenerationInitiate(initiateMap,"");
				vms.voucherGenerationApproval1(initiateMap,"");
				String quantity = initiateMap.get("quantity");
				int quantity1 = Integer.parseInt(quantity);
				String newQuantity = Integer.toString(quantity1 - 1);
				vms.voucherGenerationApproval2(initiateMap,"");
				String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
				int maxApprovalLevel = Integer.parseInt(approvalLevel);
				if(maxApprovalLevel > 2)
				{
					initiateMap.put("quantity", newQuantity);
					initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
					Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
					
				}
				else
				{
					Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
				}
			}
			else {
				vms.voucherGenerationInitiate(initiateMap,"");
				vms.voucherGenerationApproval1(initiateMap,"");
				String quantity = initiateMap.get("quantity");
				int quantity1 = Integer.parseInt(quantity);
				String newQuantity = Integer.toString(quantity1 - 1);
				vms.voucherGenerationApproval2(initiateMap,"");
				String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
				int maxApprovalLevel = Integer.parseInt(approvalLevel);
				if(maxApprovalLevel > 2)
				{
					initiateMap.put("quantity", newQuantity);
					initiateMap = vms.voucherGenerationApproval3(initiateMap,"");
					Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
					
				}
				else
				{
					Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
				}
			}
		}	
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-755") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA301_modifyVoucherDenomination() throws Exception {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS301");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITVMS500");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		initiateMap = vms.modifyVoucherDenomination(initiateMap);
		
		String message = MessagesDAO.prepareMessageByKey("vmcategory.modifysubcategoryforvoms.succ.msg.catmodisucc");
		
		if(initiateMap.get("MessageStatus").equals("Y") && (initiateMap.get("Message").equals(message)))
			Assertion.assertPass("Modification of Denomination is successful");
		else {
			Assertion.assertFail("Modification of Denomination is not successful");
		}
		
		currentNode = test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory("SIT");
		
		initiateMap=vms.viewModifyVoucherDenomination(initiateMap,"");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y"); 
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	} 
	
	@Test
	@TestManager(TestKey = "PRETUPS-757") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA302_modifyVoucherProfile() throws Exception {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS302");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITVMS501");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("scenario", "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.modifyVoucherProfile(initiateMap, "fieldValidation","");
		String message = MessagesDAO.prepareMessageByKey("vomsproduct.selectproductformodify.succ.msg.productupdatesucc");
		
		if(initiateMap.get("MessageStatus").equals("Y") && (initiateMap.get("Message").equals(message)))
			Assertion.assertPass("Modification of Voucher Profile is successful");
		else {
			Assertion.assertFail("Modification of Voucher Profile is not successful");
		}
		
		currentNode = test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory("SIT");
		
		initiateMap=vms.viewVoucherProfile(initiateMap,"");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y"); 
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	} 
	
	@Test
	@TestManager(TestKey = "PRETUPS-759") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA303_modifyActiveVoucherProfile() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS303");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		int size=0;
		int i=0;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		
		for(i=1;i<=size-1;i++) {
			
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), values.get(i)));
			currentNode.assignCategory("SIT");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
		initiateMap.put("voucherType", values.get(i));
		String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{values.get(i)});
		
		initiateMap.put("type", typeVoucher);
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("scenario", "Applicable Date greater than Current Date");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
			initiateMap = vms.modifyActiveProfile(initiateMap);
			String message = MessagesDAO.prepareMessageByKey("vomsproduct.selappfromtomodifyvomsactprd.succ.msg.activeprofiledelsucc");
			
			if(initiateMap.get("MessageStatus").equals("Y") && (initiateMap.get("Message").equals(message)))
				Assertion.assertPass("Modification of Active Profile is successful");
			else {
				Assertion.assertFail("Modification of Active Profile is not successful");
			}
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
				initiateMap = vms.modifyActiveProfile(initiateMap);
				String message = MessagesDAO.prepareMessageByKey("vomsproduct.selappfromtomodifyvomsactprd.succ.msg.activeprofiledelsucc");
				
				if(initiateMap.get("MessageStatus").equals("Y") && (initiateMap.get("Message").equals(message)))
					Assertion.assertPass("Modification of Active Profile is successful");
				else {
					Assertion.assertFail("Modification of Active Profile is not successful");
				}
			}
			else {
				Assertion.assertSkip("Not a valid case with this Voucher Type");
			}
		}			
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
	} 
	@Test
	@TestManager(TestKey = "PRETUPS-760") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA304_deleteVoucherProfile() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS304");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		int size=0;
		int i=0;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
				
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
				
		for(i=1;i<=size-1;i++) {
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), values.get(i)));
			currentNode.assignCategory("SIT");
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
			initiateMap.put("voucherType", values.get(i));
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
			initiateMap.put("scenario", "");
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			initiateMap = vms.deleteVoucherProfile(initiateMap, "fieldValidation");
						
			String message = MessagesDAO.prepareMessageByKey("vomsproduct.selectproductformodify.succ.msg.productdelsucc");
			if(initiateMap.get("MessageStatus").equals("Y") && (initiateMap.get("Message").equals(message)))
				Assertion.assertPass("Modification of Denomination is successful");
			else {
				Assertion.assertFail("Modification of Denomination is not successful");
			}
			Assertion.completeAssertions();
			Log.endTestCase(methodName);		
		}
		
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-762") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA305_deleteVoucherProfile() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS305");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		int size=0;
		int i=0;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
				
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
				
		for(i=1;i<=size-1;i++) {
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), values.get(i)));
			currentNode.assignCategory("SIT");
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			String mrp=UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("mrp", mrp);
			initiateMap.put("voucherType", values.get(i));
			String denomination = mrp + ".0";
			initiateMap.put("denomination", denomination);
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
			initiateMap.put("scenario", "");
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			//initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter));
			if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
			}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
				}
			}
			
			initiateMap = vms.deleteVoucherProfile(initiateMap, "fieldValidation");
			initiateMap = vms.voucherGenerationInitiateNegative(initiateMap);		
		
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "N");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);		
		}
	}

	@Test
	@TestManager(TestKey = "PRETUPS-763") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA306_deleteVoucherProfile() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS306");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		int size=0;
		int i=0;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
				
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
				
		for(i=1;i<=size-1;i++) {
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), values.get(i)));
			currentNode.assignCategory("SIT");
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			String mrp=UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("categoryName","SUADM");
			initiateMap.put("mrp", mrp);
			initiateMap.put("voucherType", values.get(i));
			String denomination = mrp + ".0";
			initiateMap.put("denomination", denomination);
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			String activeProfile=UniqueChecker.UC_VOMS_ProfileName();
			initiateMap.put("activeProfile", activeProfile);
			initiateMap.put("scenario", "");
			String productID = activeProfile+"("+denomination+")";
			initiateMap.put("productID", productID);
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			//initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter));
			if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
			}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
				}
			}
			initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
			initiateMap = vms.deleteVoucherProfile(initiateMap, "fieldValidation");
			initiateMap = vms.voucherGenerationApproval1Negative(initiateMap);

			Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "N");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);		
		}
	}
	
	/*@Test
	public void CASEZZZZZZA307_viewVoucherBatchList() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS307");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT]VMS");
			testCaseCounter = true;
		}
		
		int size=0;
		int i=0;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
				
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		
		if(size==0) {
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType")));
			currentNode.assignCategory("SIT");
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			String mrp=UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("mrp", mrp);
			initiateMap.put("voucherType", values.get(i));
			String denomination = mrp + ".0";
			initiateMap.put("denomination", denomination);
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			String activeProfile=UniqueChecker.UC_VOMS_ProfileName();
			initiateMap.put("activeProfile", activeProfile);
			initiateMap.put("scenario", "");
			String productID = activeProfile+"("+denomination+")";
			initiateMap.put("productID", productID);
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
			initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
			String product = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String status = "IN";
			String batchNumber = DBHandler.AccessHandler.getBatchNumber(product, status);
			initiateMap = vms.viewVoucherBatchList(initiateMap,batchNumber);
			
			
			if(initiateMap.get("MessageStatus").equals("Y"))
				currentNode.log(Status.PASS, "Batch Number and status is available in Batch List");
			else {
			    currentNode.log(Status.FAIL, "Batch Number and status is not found in Batch list");
			}
		Log.endTestCase(methodName);		
		}
		else {
			
		for(i=1;i<=size-1;i++) {
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), values.get(i)));
			currentNode.assignCategory("SIT");
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			String mrp=UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("mrp", mrp);
			initiateMap.put("voucherType", values.get(i));
			String denomination = mrp + ".0";
			initiateMap.put("denomination", denomination);
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			String activeProfile=UniqueChecker.UC_VOMS_ProfileName();
			initiateMap.put("activeProfile", activeProfile);
			initiateMap.put("scenario", "");
			String productID = activeProfile+"("+denomination+")";
			initiateMap.put("productID", productID);
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
			initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
			String product = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String status = "IN";
			String batchNumber = DBHandler.AccessHandler.getBatchNumber(product, status);
			initiateMap = vms.viewVoucherBatchList(initiateMap,batchNumber);
			
			
			if(initiateMap.get("MessageStatus").equals("Y"))
				currentNode.log(Status.PASS, "Batch Number and status is available in Batch List");
			else {
			    currentNode.log(Status.FAIL, "Batch Number and status is not found in Batch list");
			}
		Log.endTestCase(methodName);		
		}
	}
	}*/
	
	@Test
	@TestManager(TestKey = "PRETUPS-765") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA308_suspendedVoucherProfile() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS308");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		int size=0;
		int i=0;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
				
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
			
		if(size==0) {
			
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType")));
			currentNode.assignCategory("SIT"); 
			
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			String mrp=UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("mrp", mrp);
			initiateMap.put("voucherType", values.get(i));
			String denomination = mrp + ".0";
			initiateMap.put("denomination", denomination);
			String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{values.get(i)});
			initiateMap.put("type", typeVoucher);
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
			initiateMap.put("scenario", "");
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			//initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter));
			if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
			}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
				}
			}
			initiateMap = vms.modifyVoucherProfileSuspended(initiateMap, "suspended","");
			String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.noactiveproduct.denomination", initiateMap.get("denomination") );
			
			if(initiateMap.get("MessageStatus").equals("N") && (initiateMap.get("Message").equals("Product ID not found")))
				Assertion.assertPass("Voucher Profile is successful Suspended");
			else {
				Assertion.assertFail("Voucher Profile is not successful Suspended");
			}
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			}
		else {
		for(i=1;i<=size-1;i++) {
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), values.get(i)));
		currentNode.assignCategory("SIT"); 
		
		
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp=UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", values.get(i));
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{values.get(i)});
		initiateMap.put("type", typeVoucher);
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		initiateMap.put("activeProfile", UniqueChecker.UC_VOMS_ProfileName());
		initiateMap.put("scenario", "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		//initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter));
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
			}
		}
		initiateMap = vms.modifyVoucherProfileSuspended(initiateMap, "suspended","");
		String message = MessagesDAO.prepareMessageByKey("voms.orderapprove.error.noactiveproduct.denomination", initiateMap.get("denomination") );
		
		if(initiateMap.get("MessageStatus").equals("N") && (initiateMap.get("Message").equals("Product ID not found")))
			Assertion.assertPass("Voucher Profile is successful Suspended");
		else {
			Assertion.assertFail("Voucher Profile is not successful Suspended");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
	} 
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-766") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA309_suspendedVoucherProfileLevel1Approval() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS309");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		int size=0;
		int i=0;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
				
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		if(size==0) {
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType")));
			currentNode.assignCategory("SIT"); 
			
		
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			String mrp=UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("mrp", mrp);
			initiateMap.put("voucherType", values.get(i));
			String denomination = mrp + ".0";
			initiateMap.put("denomination", denomination);
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			String activeProfile =UniqueChecker.UC_VOMS_ProfileName();
			initiateMap.put("activeProfile", activeProfile);
			initiateMap.put("scenario", "");
			String productID = activeProfile+"("+denomination+")";
			initiateMap.put("productID", productID);
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			//initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter));
			if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
			}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
				}
			}
			initiateMap = vms.modifyVoucherProfileSuspended(initiateMap, "suspended","");
			initiateMap = vms.voucherGenerationApproval1Negative(initiateMap);
			
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "N");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			}
		else {
		for(i=1;i<=size-1;i++) {
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), values.get(i)));
		currentNode.assignCategory("SIT"); 
		
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp=UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", values.get(i));
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap = vms.voucherDenominationNegative(initiateMap,"");
		String activeProfile =UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		initiateMap.put("scenario", "");
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		//initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, dataCounter));
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
			}
		}
		initiateMap = vms.modifyVoucherProfileSuspended(initiateMap, "suspended","");
		initiateMap = vms.voucherGenerationApproval1Negative(initiateMap);
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "N");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
		}
	} 
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-767") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA310_multipleDenominationOrderGeneration() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS310");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		int size = 0;
		int i = 0;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values = vms.voucherTypeList();

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		HashMap<String, String> initiateMap2 = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		initiateMap2 = vmsmap.defaultMap();
		if (size == 0) {
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType")));
			currentNode.assignCategory("SIT");

			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap2.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			initiateMap2.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			String mrp = UniqueChecker.UC_VOMS_MRP();
			String mrp2 = UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("mrp", mrp);
			initiateMap2.put("mrp", mrp2);
			initiateMap.put("voucherType", values.get(i));
			initiateMap2.put("voucherType", values.get(i));
			String denomination = mrp + ".0";
			String denomination2 = mrp2 + ".0";
			initiateMap.put("denomination", denomination);
			initiateMap2.put("denomination", denomination2);
			String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{values.get(i)});
			initiateMap.put("type", typeVoucher);
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			initiateMap2 = vms.voucherDenominationNegative(initiateMap2,"");
			String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
			String activeProfile2 = UniqueChecker.UC_VOMS_ProfileName();
			initiateMap.put("activeProfile", activeProfile);
			initiateMap2.put("activeProfile", activeProfile2);
			initiateMap.put("scenario", "");
			initiateMap2.put("scenario", "");
			String productID = activeProfile + "(" + denomination + ")";
			String productID2 = activeProfile2 + "(" + denomination2 + ")";
			initiateMap.put("productID", productID);
			initiateMap2.put("productID", productID2);
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			initiateMap2 = vms.addVoucherProfileNegative(initiateMap2);
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
				initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
				initiateMap2 = vms.addActiveVoucherProfileNegative(initiateMap2);
				initiateMap = vms.vomsOrderInititate(initiateMap);
				initiateMap = vms.voucherGenerationInitiateMultipleDenominationEnter(initiateMap, initiateMap2);
			}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
					initiateMap2 = vms.addActiveVoucherProfileNegative(initiateMap2);
					initiateMap = vms.vomsOrderInititate(initiateMap);
					initiateMap = vms.voucherGenerationInitiateMultipleDenominationEnter(initiateMap, initiateMap2);
				}
				else {
					initiateMap = vms.vomsOrderInititate(initiateMap);
					initiateMap = vms.voucherGenerationInitiateMultipleDenominationEnter(initiateMap, initiateMap2);
				}
			}			
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		} else {
			for (i = 1; i <= size - 1; i++) {
				currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), values.get(i)));
				currentNode.assignCategory("SIT");

				initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
				initiateMap2.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
				initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
				initiateMap2.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
				String mrp = UniqueChecker.UC_VOMS_MRP();
				String mrp2 = UniqueChecker.UC_VOMS_MRP();
				initiateMap.put("mrp", mrp);
				initiateMap2.put("mrp", mrp2);
				initiateMap.put("voucherType", values.get(i));
				initiateMap2.put("voucherType", values.get(i));
				String denomination = mrp + ".0";
				String denomination2 = mrp2 + ".0";
				initiateMap.put("denomination", denomination);
				initiateMap2.put("denomination", denomination2);
				String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{values.get(i)});
				initiateMap.put("type", typeVoucher);
				initiateMap = vms.voucherDenominationNegative(initiateMap,"");
				initiateMap2 = vms.voucherDenominationNegative(initiateMap2,"");
				String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
				String activeProfile2 = UniqueChecker.UC_VOMS_ProfileName();
				initiateMap.put("activeProfile", activeProfile);
				initiateMap2.put("activeProfile", activeProfile2);
				initiateMap.put("scenario", "");
				initiateMap2.put("scenario", "");
				String productID = activeProfile + "(" + denomination + ")";
				String productID2 = activeProfile2 + "(" + denomination2 + ")";
				initiateMap.put("productID", productID);
				initiateMap2.put("productID", productID2);
				initiateMap = vms.addVoucherProfileNegative(initiateMap);
				initiateMap2 = vms.addVoucherProfileNegative(initiateMap2);
				String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
				if(BTSLUtil.isNullString(value)) {
					initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
					initiateMap2 = vms.addActiveVoucherProfileNegative(initiateMap2);
					initiateMap = vms.vomsOrderInititate(initiateMap);
					initiateMap = vms.voucherGenerationInitiateMultipleDenominationEnter(initiateMap, initiateMap2);
				}
				else {
					String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
					List al = Arrays.asList(type);
					if(al.contains(initiateMap.get("type"))) {
						initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
						initiateMap2 = vms.addActiveVoucherProfileNegative(initiateMap2);
						initiateMap = vms.vomsOrderInititate(initiateMap);
						initiateMap = vms.voucherGenerationInitiateMultipleDenominationEnter(initiateMap, initiateMap2);
					}
					else {
						initiateMap = vms.vomsOrderInititate(initiateMap);
						initiateMap = vms.voucherGenerationInitiateMultipleDenominationEnter(initiateMap, initiateMap2);
					}
				}			
				Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
				Assertion.completeAssertions();
				Log.endTestCase(methodName);
			}
		}
	}
	
	/*@Test
	public void CASEZZZZZZA311_viewVoucherBatchListAllAttributes() throws InterruptedException {
			final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS311");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT]VMS");
			testCaseCounter = true;
		}
		
		int size=0;
		int i=0;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
				
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		
		if(size==0) {
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType")));
			currentNode.assignCategory("SIT");
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			String mrp=UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("mrp", mrp);
			initiateMap.put("voucherType", values.get(i));
			String denomination = mrp + ".0";
			initiateMap.put("denomination", denomination);
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			String activeProfile=UniqueChecker.UC_VOMS_ProfileName();
			initiateMap.put("activeProfile", activeProfile);
			initiateMap.put("scenario", "");
			String productID = activeProfile+"("+denomination+")";
			initiateMap.put("productID", productID);
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
			initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
			String product = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String status = "IN";
			String batchNumber = DBHandler.AccessHandler.getBatchNumber(product, status);
			initiateMap = vms.viewVoucherBatchListAllAttributes(initiateMap,batchNumber);
			
			
			if(initiateMap.get("MessageStatus").equals("Y"))
				currentNode.log(Status.PASS, "Voucher batch has all attributes");
			else {
			    currentNode.log(Status.FAIL, "Voucher batch has not all attributes");
			}
		Log.endTestCase(methodName);		
		}
		else {
			
		for(i=1;i<=size-1;i++) {
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),  values.get(i)));
			currentNode.assignCategory("SIT");
			
			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			String mrp=UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("mrp", mrp);
			initiateMap.put("voucherType", values.get(i));
			String denomination = mrp + ".0";
			initiateMap.put("denomination", denomination);
			initiateMap = vms.voucherDenominationNegative(initiateMap,"");
			String activeProfile=UniqueChecker.UC_VOMS_ProfileName();
			initiateMap.put("activeProfile", activeProfile);
			initiateMap.put("scenario", "");
			String productID = activeProfile+"("+denomination+")";
			initiateMap.put("productID", productID);
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
			initiateMap = vms.voucherGenerationInitiate(initiateMap,"");
			String product = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String status = "IN";
			String batchNumber = DBHandler.AccessHandler.getBatchNumber(product, status);
			initiateMap = vms.viewVoucherBatchListAllAttributes(initiateMap,batchNumber);
			
			
			if(initiateMap.get("MessageStatus").equals("Y"))
				currentNode.log(Status.PASS, "Voucher batch has all attributes");
			else {
			    currentNode.log(Status.FAIL, "Voucher batch has not all attributes");
			}
		Log.endTestCase(methodName);		
		}
	}
	}*/
	
	@Test
	@TestManager(TestKey = "PRETUPS-768") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA312_ModifyAtApproval2() throws InterruptedException {
	   final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS312");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{initiateMap.get("voucherType")});
		initiateMap.put("type", typeVoucher);
		String minQuantity = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_PROFILE_MIN_REORDERQTY);
		int minQuantity1 = Integer.parseInt(minQuantity);
		String minNewQuanitiy =Integer.toString(minQuantity1 + 1);
		initiateMap.put("quantity", minNewQuanitiy);
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			vms.addActiveVoucherProfileNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				vms.addActiveVoucherProfileNegative(initiateMap);
			}
		}			
		vms.voucherGenerationInitiate(initiateMap,"");
		String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);
		if(maxApprovalLevel > 0) {
			vms.voucherGenerationApproval1(initiateMap,"");
			Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
			if(maxApprovalLevel > 1)
			{
			
			String quantity = initiateMap.get("quantity");
			int quantity1 = Integer.parseInt(quantity);
			String newQuantity = Integer.toString(quantity1 - 1);
			initiateMap.put("quantity2", newQuantity);
			initiateMap = vms.voucherGenerationApproval2ModiftyQuantity(initiateMap);
			Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
			}
			else
			{
				Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
			}
		}
		else
		{
			Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		}
		
		
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1775") 
	public void CASEZZZZZZA313_UploadDocument() throws InterruptedException {
		int maxApprovalLevel=0;
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		
		final String methodName = "Test_UploadDocument";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS313");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{initiateMap.get("voucherType")});
		initiateMap.put("type", typeVoucher);
		String minQuantity = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_PROFILE_MIN_REORDERQTY);
		int minQuantity1 = Integer.parseInt(minQuantity);
		String minNewQuanitiy =Integer.toString(minQuantity1 + 1);
		initiateMap.put("quantity", minNewQuanitiy);
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			vms.addActiveVoucherProfileNegative(initiateMap);
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				vms.addActiveVoucherProfileNegative(initiateMap);
			}
		}			
		
		vms.voucherGenerationInitiate(initiateMap,"");
		if(maxApprovalLevel == 1)
		{initiateMap=vms.voucherGenerationApproval1uploadDoc(initiateMap,"");
		}
		else if (maxApprovalLevel ==2 )
		{vms.voucherGenerationApproval1(initiateMap,"");
		initiateMap=vms.voucherGenerationApproval2uploadDoc(initiateMap,"");
		}
		else if (maxApprovalLevel ==3 )
		{vms.voucherGenerationApproval1(initiateMap,"");
		vms.voucherGenerationApproval2(initiateMap,"");
		initiateMap=vms.voucherGenerationApproval3uploadDoc(initiateMap, "");
		}
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Log.endTestCase(methodName);	
		}
	
	@Test
	@TestManager(TestKey = "PRETUPS-768") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA333_VoucherProfileTalktimeValidity() throws InterruptedException {
	   final String methodName = "Test_VoucherProfile";Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMS333");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		vms.voucherDenominationNegative(initiateMap,"");
		vms.addVoucherProfileNegativeTalktime(initiateMap);
		Assertion.assertEqualsIgnoreCase(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);	
	}
	
	@DataProvider(name="VOMSDENOMINATIONS")
	public Object[][] VOMSDenominationDP() {
		
		int VOMS_DATA_COUNT = Integer.parseInt(_masterVO.getProperty("vms.voms.profiles.count"));
		Object[][] VOMSData = DBHandler.AccessHandler.getVOMSDetails();
		
		int objCounter = 0;
		ArrayList<String> categoryList =UserAccess.getCategoryWithAccess(RolesI.ADD_VOUCHER_DENOMINATION);
		
		if(categoryList.contains("SSADM"))
		{
			categoryList.remove(categoryList.indexOf("SSADM"));
		}
		
		if(categoryList.contains("SUNADM"))
		{
			categoryList.remove(categoryList.indexOf("SUNADM"));
		}
		
		int categorySize=categoryList.size();
		Object[][] dataObj = new Object[VOMS_DATA_COUNT * VOMSData.length*categorySize][2];
		
		for (int i = 0; i < VOMS_DATA_COUNT; i++) {
			for (int j = 0; j<VOMSData.length; j++) {
				for(int k=0;k<categorySize;k++) {
					
			HashMap<String, String> VomsData = new HashMap<String, String>();
			VomsData.put("voucherType", String.valueOf(VOMSData[j][0]));
			VomsData.put("type", String.valueOf(VOMSData[j][1]));
			VomsData.put("service", String.valueOf(VOMSData[j][2]));
			VomsData.put("subService", String.valueOf(VOMSData[j][3]));
			VomsData.put("categoryName",categoryList.get(k));
			VomsData.put("payableAmount", String.valueOf(10));
			VomsData.put("description", "Automation Testing");
			VomsData.put("minQuantity", "1");
			VomsData.put("maxQuantity", "60");
			VomsData.put("talkTime", "5");
			VomsData.put("validity", "80");
			VomsData.put("threshold", "10");
			VomsData.put("quantity", "50");
			VomsData.put("expiryPeriod", "90");
			dataObj[objCounter][0] = VomsData.clone();
			dataObj[objCounter][1] = ++objCounter;
			}
			}
		}
		
		BuilderLogic VOMSDenomSheetBuilder = new BuilderLogic();
		VOMSDenomSheetBuilder.prepareVOMSProfileSheet(dataObj);
		
		return dataObj;
	}
	
	@DataProvider(name="VOMSDENOMPROFILES")
	public Object[][] VOMSDenominationProfilesDP() {
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] dataObj = new Object[rowCount][2];
		int objCounter = 0;
		for (int i = 1; i <= rowCount; i++) {
			HashMap<String, String> VomsData = new HashMap<String, String>();
			VomsData.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
			VomsData.put("type", ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
			VomsData.put("service", ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i));
			VomsData.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
			VomsData.put("categoryName",ExcelUtility.getCellData(0, ExcelI.VOMS_USER_CATEGORY_NAME, i));
			VomsData.put("denominationName", ExcelUtility.getCellData(0, ExcelI.VOMS_DENOMINATION_NAME, i));
			VomsData.put("shortName", ExcelUtility.getCellData(0, ExcelI.VOMS_SHORT_NAME, i));
			VomsData.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
			String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
			VomsData.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
			String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i);
			String denomination = mrp + ".0";
			VomsData.put("denomination", denomination);
			String productID = activeProfile+"("+denomination+")";
			VomsData.put("productID", productID);
			VomsData.put("payableAmount", String.valueOf(10));
			VomsData.put("description", "Automation Testing");
			VomsData.put("remarks", "Automation Testing");
			VomsData.put("minQuantity", "1");
			VomsData.put("maxQuantity", "60");
			VomsData.put("talkTime", "5");
			VomsData.put("validity", "80");
			VomsData.put("threshold", "10");
			VomsData.put("quantity", "50");
			VomsData.put("expiryPeriod", "90");
			VomsData.put("batchType", "printing");
			VomsData.put("voucherStatus", PretupsI.WAREHOUSE);
			VomsData.put("viewBatchFor", "N");
			dataObj[objCounter][0] = VomsData.clone();
			dataObj[objCounter][1] = ++objCounter;
		}
		
		return dataObj;
	}	
}
