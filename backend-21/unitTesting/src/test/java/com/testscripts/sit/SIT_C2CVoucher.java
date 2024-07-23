package com.testscripts.sit;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CTransfer;
import com.Features.ChannelUser;
import com.Features.O2CTransfer;
import com.Features.P2PCardGroup;
import com.Features.P2PTransferRules;
import com.Features.VMS;
import com.Features.mapclasses.OperatorToChannelMap;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.databuilder.BuilderLogic;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SIT_VMS)
public class SIT_C2CVoucher extends BaseTest {
	
	String MasterSheetPath;
	static String directO2CPreference;
	static String moduleCode;
	HashMap<String, String> c2cMap = new HashMap<String, String>();
    HashMap<String, String> channelMap = new HashMap<>();
	
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
				vms.writeDenominationC2C(initiateMap, dataCounter);
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
				vms.writeProfileC2C(initiateMap, dataCounter);
				
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

			vms.voucherGenerationScriptExecution();
//			Thread.sleep(9000);
			String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String batchType = DBHandler.AccessHandler.fetchBatchType(productID);
			
			Assertion.assertEquals(batchType, "GE");
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
			initiateMap.put("fromSerialNumber", fromSerialNumber);
			initiateMap.put("toSerialNumber", toSerialNumber);
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
					Assertion.assertEquals(initiateMap.get("currentStatus"), "EN");
				}
				else {
					Assertion.assertSkip("Not a valid case for this scenario");
				}
			}
			Assertion.completeAssertions();

		Log.endTestCase(methodName);
		}
		
		public void createVoucherCardGroup(HashMap<String, String> initiateMap) throws InterruptedException{
			String serviceName=ExtentI.getValueofCorrespondingColumns(ExcelI.P2P_SERVICES_SHEET_VOUCHER, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{initiateMap.get("service")});
			String subService =initiateMap.get("subService");
	        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
	        HashMap<String, String> mapInfo = (HashMap<String, String>) p2pCardGroup.P2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
	        boolean uap = true;
	 		P2PTransferRules p2pTransferRules = new P2PTransferRules(driver);
	 		String result[] = p2pTransferRules.addP2PTransferRules(serviceName, subService, mapInfo.get("CARDGROUPNAME"), uap, "ALL");
	 		/*currentNode = test.createNode(_masterVO.getCaseMasterByID("UP2PTRFRULE2").getExtentCase());
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
		
		@DataProvider(name="VOMSDENOMINATIONS")
		public Object[][] VOMSDenominationDP() {
			
			int VOMS_DATA_COUNT = Integer.parseInt(_masterVO.getProperty("vms.voms.profiles.count"));
			
			
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = (Arrays.asList(type));
				
				
			Object[][] VOMSData = DBHandler.AccessHandler.getVOMSDetailsC2C(al);
			
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
				VomsData.put("quantity", "10");
				VomsData.put("expiryPeriod", "90");
				dataObj[objCounter][0] = VomsData.clone();
				dataObj[objCounter][1] = ++objCounter;
				}
				}
			}
			
			BuilderLogic VOMSDenomSheetBuilder = new BuilderLogic();
			VOMSDenomSheetBuilder.prepareVOMSProfileSheetC2C(dataObj);
			
			return dataObj;
		}
		
		@DataProvider(name="VOMSDENOMPROFILES")
		public Object[][] VOMSDenominationProfilesDP() {
			
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE_C2C);
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
				VomsData.put("quantity", "10");
				VomsData.put("expiryPeriod", "90");
				VomsData.put("batchType", "printing");
				VomsData.put("voucherStatus", PretupsI.WAREHOUSE);
				VomsData.put("viewBatchFor", "N");
				dataObj[objCounter][0] = VomsData.clone();
				dataObj[objCounter][1] = ++objCounter;
			}
			
			return dataObj;
		}	

		
		@Test(dataProvider = "categoryData")
	    @TestManager(TestKey = "PRETUPS-313") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void A_16__C2CTransfer(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, String loginID, int RowNum) throws InterruptedException {
	        final String methodName = "Test_C2CTransfer";
	        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PC2CTRF1");
	        C2CTransfer c2cTransfer = new C2CTransfer(driver);
	        ChannelUser channelUser = new ChannelUser(driver);
	        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
	        String value = DBHandler.AccessHandler.getPreference(catCode, networkCode, PretupsI.MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER);
	        int maxApprovalLevel=0;
	        
	      
			maxApprovalLevel = Integer.parseInt(value);
	        if (CommonUtils.roleCodeExistInLinkSheet(RolesI.C2CTRF_ROLECODE, FromCategory)) {
	            if (FromCategory.equals(ToCategory)) {
	                channelMap = channelUser.channelUserInitiate(RowNum, Domain, ParentCategory, ToCategory, geoType);
	                String APPLEVEL = DBHandler.AccessHandler.getSystemPreference(UserAccess.userapplevelpreference());
	                if (APPLEVEL.equals("2")) {
	                    channelUser.approveLevel1_ChannelUser();
	                    channelUser.approveLevel2_ChannelUser();
	                } else if (APPLEVEL.equals("1")) {
	                    channelUser.approveLevel1_ChannelUser();
	                } else {
	                    Log.info("Approval not required.");
	                }
	                toMSISDN = channelMap.get("MSISDN");
	            }
	            else {
	            	String parent= ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.PARENT_CATEGORY_NAME, new String[] {ExcelI.LOGIN_ID} , new String[] {loginID});     
	            	loginID =ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.LOGIN_ID, new String[] {ExcelI.CATEGORY_NAME} , new String[] {parent});     
	      	        	        
	            }
	            Object[][] dataObj= VOMSDenominationProfilesDP();
	            c2cMap = c2cTransfer.channel2channelVocuherTransfer(FromCategory, ToCategory, toMSISDN, FromPIN,dataObj,loginID);
	            if(BTSLUtil.isNullString(value)) {
	            	Log.info("C2C vocuher transfer Approval level is not Applicable");
	        		}
	            else {
	            	if(maxApprovalLevel == 0)
	        		{
	            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
	        		}
	            	if(maxApprovalLevel == 1)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1ApprovalVoucher(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 2)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1ApprovalVoucher(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"));
	            		c2cMap=c2cTransfer.performingLevel2ApprovalVoucher(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"));
	        		}
	            	else if(maxApprovalLevel == 3)
	        		{
	            		c2cMap=c2cTransfer.performingLevel1ApprovalVoucher(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"));
	            		c2cMap=c2cTransfer.performingLevel2ApprovalVoucher(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"));
	            		c2cMap=c2cTransfer.performingLevel3ApprovalVoucher(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TransactionID"));
	        		}
	        } 
	        }  
	        else {
	            Assertion.assertSkip("Channel to Channel transfer link is not available to Category[" + FromCategory + "]");
	        }  
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }

	    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
	    /* ------------------------------------------------------------------------------------------------- */

	    @DataProvider(name = "categoryData")
	    public Object[][] TestDataFeed1() {
	        String C2CTransferCode = _masterVO.getProperty("C2CVoucherTransferCode");
	        String MasterSheetPath = _masterVO.getProperty("DataProvider");
	        
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	        int rowCount = ExcelUtility.getRowCount();
	        /*
	         * Array list to store Categories for which C2C withdraw is allowed
	         */
	        ArrayList<String> alist1 = new ArrayList<String>();
	        ArrayList<String> alist2 = new ArrayList<String>();
	        ArrayList<String> categorySize = new ArrayList<String>();
	        ArrayList<String> transfer_rule_type = new ArrayList<String>();
	        for (int i = 1; i <= rowCount; i++) {
	            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
	            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
	            if (aList.contains(C2CTransferCode)) {
	                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	                alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
	                alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
	                if (ExcelUtility.getCellData(0, ExcelI.TRF_RULE_TYPE, i).equals(""))
	                    transfer_rule_type.add("D");
	                else
	                    transfer_rule_type.add(ExcelUtility.getCellData(0, ExcelI.TRF_RULE_TYPE, i));
	            }
	        }

	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	        int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

	        /*
	         * Calculate the Count of Users for each category
	         */
	        int totalObjectCounter = 0;
	        for (int i = 0; i < alist1.size(); i++) {
 	            int categorySizeCounter = 0;
	            for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
	                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(i))) {
	                    categorySizeCounter++;
	                    break;
	                }
	            }
	            categorySize.add("" + categorySizeCounter);
	            totalObjectCounter = totalObjectCounter + categorySizeCounter;
	        }

	        /*
	         * Counter to count number of users exists in channel users hierarchy sheet
	         * of Categories for which C2C Withdraw is allowed
	         */

	        Object[][] Data = new Object[totalObjectCounter][10];

	        for (int j = 0, k = 0; j < alist1.size(); j++) {

	            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	            int excelRowSize = ExcelUtility.getRowCount();
	            String ChannelUserPIN = null;
	            for (int i = 1; i <= excelRowSize; i++) {
	                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist2.get(j))) {
	                    ChannelUserPIN = ExcelUtility.getCellData(0, ExcelI.PIN, i);
	                    break;
	                }
	            }

	            for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
	                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(j))) {
	                    Data[k][0] = alist2.get(j);
	                    Data[k][1] = alist1.get(j);
	                    Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
	                    Data[k][3] = ChannelUserPIN;
	                    Data[k][4] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, excelCounter);
	                    Data[k][5] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
	                    Data[k][6] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, excelCounter);
	                    Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
	                    Data[k][8] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
	                    Data[k][9] = excelCounter;
	                    k++;
	                    break;
	                }
	            }

	        }

	        /*
	         * Clean data on the basis of transfer rules
	         */
	        String trfUserLevelAlllow = DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRF_RULE_USER_LEVEL_ALLOW);
	        if (trfUserLevelAlllow.equalsIgnoreCase("FALSE")) {
	            int q = 0;
	            ArrayList<Integer> removeData = new ArrayList<Integer>();
	            for (int i = 0; i < alist1.size(); i++) {
	                if (transfer_rule_type.get(i).equals("P")) {
	                    Log.info("From: " + alist2.get(i) + "| To: " + alist1.get(i) + "| TYPE: " + transfer_rule_type.get(i));

	                    for (int p = 0; p < Data.length; p++) {
	                        if (Data[p][0].equals(alist2.get(i)) && Data[p][1].equals(alist1.get(i)) && !Data[p][5].equals(alist2.get(i))) {
	                            Log.info("Data to be removed:[" + p + "]");
	                            q++;
	                            removeData.add(p);
	                        }
	                    }
	                }

	                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	                int excelRowSize = ExcelUtility.getRowCount();

	                if (transfer_rule_type.get(i).equals("O")) {
	                    Log.info("From: " + alist2.get(i) + "| To: " + alist1.get(i) + "| TYPE: " + transfer_rule_type.get(i));
	                    for (int p = 0; p < Data.length; p++) {
	                        int k;
	                        for (k = 1; k <= excelRowSize; k++) {
	                            if (ExcelUtility.getCellData(0, ExcelI.SEQUENCE_NO, k).equals("1") && ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, k).equals(Data[p][4])) {
	                                break;
	                            }
	                        }

	                        if ((Data[p][0].equals(alist2.get(i)) || Data[p][0].equals(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, k)))
	                                && Data[p][1].equals(alist1.get(i)) && !Data[p][5].equals(alist2.get(i))) {
	                            Log.info("Data to be removed:[" + p + "]");
	                            q++;
	                            removeData.add(p);
	                        }
	                    }
	                }
	            }

	            int newObj = Data.length - q;
	            Object[][] Data1 = new Object[newObj][10];
	            for (int l = 0, m = 0; l < Data.length; l++) {
	                if (!removeData.contains(l)) {
	                    for (int x = 0; x < 10; x++) {
	                        Data1[m][x] = Data[l][x];
	                    }
	                    Log.info(Data1);
	                    m++;
	                }
	            }


	            return Data1;
	        } else {
	            return Data;
	        }
	    }

	    /* ----------------------------------------------------------------------------------------------- */
	}
