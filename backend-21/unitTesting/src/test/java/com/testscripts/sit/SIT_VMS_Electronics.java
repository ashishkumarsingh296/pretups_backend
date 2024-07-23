package com.testscripts.sit;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.O2CTransfer;
import com.Features.P2PCardGroup;
import com.Features.P2PTransferRules;
import com.Features.VMS;
import com.Features.VoucherOrderInitiate;
import com.Features.mapclasses.ChannelUserMap;
import com.Features.mapclasses.OperatorToChannelMap;
import com.Features.mapclasses.VMSMap;
import com.apicontrollers.extgw.VMS.EXTGW_VoucherConsumption_API;
import com.apicontrollers.extgw.VMS.EXTGW_VoucherConsumption_DP;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.AutomationException;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.commons.ServicesControllerI;
import com.commons.SystemPreferences;
import com.databuilder.BuilderLogic;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_VMS_Electronics)
public class SIT_VMS_Electronics extends BaseTest {

	public static boolean testCaseCounter = false;
	String MasterSheetPath;
	static String directO2CPreference;
	static String moduleCode;
	String sheetToRefer = ExcelI.ELC_OPERATOR_USERS_HIERARCHY_SHEET;
	String voucherType = "Electronic";
	static String voucherTypeAllowed;

   /* @Test
	public void A_01_operatorUserFile() throws SQLException {
		voucherTypeAllowed = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue(MasterI.NETWORK_CODE),
				CONSTANT.USER_VOUCHERTYPE_ALLOWED);
		if (voucherTypeAllowed.equalsIgnoreCase("TRUE"))
			new VomsOperatorUsers()._01_fetchOperatorUsers_custom(voucherType);
	}

     @Test(dataProvider = "optusrcreationdataElc")
	public void A_02_operatorUserCreation(int RowNum, String ParentUser, String LoginUser, String sheetTorefer,
			String vouchertype) throws InterruptedException {
		new VomsOperatorUsers()._02_operatorUserCreation(RowNum, ParentUser, LoginUser, sheetTorefer, vouchertype);
	}

	@DataProvider(name = "optusrcreationdataElc")
	public Object[][] optdataElc() throws IOException {
		if (voucherTypeAllowed.equalsIgnoreCase("TRUE")) {
			Object[][] dataOpt = new VomsOperatorUsers().DomainCategoryProvider(sheetToRefer, voucherType);
			return dataOpt;
		}
		return null;
	}
*/
	@TestManager(TestKey = "PRETUPS-811")
	@Test(dataProvider = "VOMSDENOMINATIONS")
	public void _01_VoucherDenomination(HashMap<String, String> initiateMap, int dataCounter) {

		final String methodName="_01_VoucherDenomination";Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERDENOM");
		//moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
		initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		initiateMap = vms.voucherDenomination(initiateMap, "electronic");

		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			vms.writeDenominationElectronics(initiateMap, dataCounter);
		} else
			Assertion.assertFail("Add Voucher Denomination Failure with Following Message: " + initiateMap.get("Message"));

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-812")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _02_viewVoucherDenomination(HashMap<String, String> initiateMap, int dataCounter) throws Exception {

		final String methodName="_02_viewVoucherDenomination";Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELEVIEWVOUCHERDENOM");
		//moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

	
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		initiateMap = vms.viewVoucherDenomination(initiateMap, "electronic");

		if (initiateMap.get("MessageStatus").equals("Y"))
			currentNode.log(Status.PASS, "Added Voucher Denomination Found");
		else {
			Assertion.assertFail( "Added Voucher Denomination Not Found");
			Assertion.completeAssertions();
		}
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-814")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _03_VoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {

		final String methodName="_03_VoucherProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERPROF");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

	
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		initiateMap = vms.addVoucherProfile(initiateMap, "electronic");

		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			vms.writeProfileElectronic(initiateMap, dataCounter);

		} else{
			Assertion.assertFail( "Add Voucher Profile Failure with Following Message: " + initiateMap.get("Message"));
			Assertion.completeAssertions();
		}
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-816")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _04_viewVoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="_04_viewVoucherProfile";Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVIEWVOUCHERPROF");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		initiateMap = vms.viewVoucherProfile(initiateMap, "electronic");

		if (initiateMap.get("MessageStatus").equals("Y"))
			currentNode.log(Status.PASS, "Added Voucher Denomination Found");
		else {
			Assertion.assertFail(  "Added Voucher Denomination Not Found");
			Assertion.completeAssertions();
		}
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-817")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _05_VoucherActiveProfile(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {

		final String methodName="_05_VoucherActiveProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECACTIVEVOUCHERPROF");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		initiateMap = vms.addActiveVoucherProfile(initiateMap, "electronic");

		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Log.info("Add Active Voucher Profile Successful  with Following Message: " + initiateMap.get("Message"));

		} else{
			Assertion.assertFail( "Add Voucher Profile Failure with Following Message: " + initiateMap.get("Message"));

		Assertion.completeAssertions();}
	Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-818")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _06_ViewVoucherActiveProfile(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {

		final String methodName="_06_ViewVoucherActiveProfile";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVIEWACTIVEVOUCHERPROF");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
	

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"), initiateMap.get("activeProfile"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		initiateMap = vms.viewActiveProfile(initiateMap, "electronic");

		if (initiateMap.get("MessageStatus").equals("Y"))
			currentNode.log(Status.PASS, "Added Active Profile Found");
		else {
			Assertion.assertFail(  "Added Active Profile Not Found");
			Assertion.completeAssertions();
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-819")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _07_VoucherOrderInitiate(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {

		final String methodName="_07_VoucherOrderInitiate";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERORDERINIT");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);


		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		initiateMap = vms.voucherGenerationInitiate(initiateMap, "electronic");

		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Log.info(" Voucher Initiate Successful  with Following Message: " + initiateMap.get("Message"));

		} else{
			Assertion.assertFail( "Add Voucher Initiate Failure with Following Message: " + initiateMap.get("Message"));

		Assertion.completeAssertions();}Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-820")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _08_VoucherOrderApproval1(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {
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
		final String methodName="_08_VoucherOrderApproval1";Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERAPPROV1");
		moduleCode = CaseMaster1.getModuleCode();

		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		
		initiateMap = vms.voucherGenerationApproval1(initiateMap, "electronic");
		if(maxApprovalLevel > 0)
		{
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Log.info(" Voucher Order Approval1 Successful  with Following Message: " + initiateMap.get("Message"));

		} else{
			Assertion.assertFail( "Voucher Order Approval1 Failure with Following Message: " + initiateMap.get("Message"));
		}
		}
		else
			  Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-821")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _09_VoucherOrderApproval2(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {
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
		final String methodName="_09_VoucherOrderApproval2";Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERAPPROV2");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		if(maxApprovalLevel > 1)
		{
		initiateMap = vms.voucherGenerationApproval2(initiateMap, "electronic");

		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Log.info(" Voucher Order Approval2 Successful  with Following Message: " + initiateMap.get("Message"));

		} else{
			Assertion.assertFail( "Voucher Order Approval2 Failure with Following Message: " + initiateMap.get("Message"));
		}
		}
		else
			 Assertion.assertSkip("Max Approval Level is: "+ maxApprovalLevel);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-822")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _10_VoucherOrderApproval3(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {
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
		/*String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
		int maxApprovalLevel = Integer.parseInt(approvalLevel);*/
		
		final String methodName="_10_VoucherOrderApproval3";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERAPPROV3");
//		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

	

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		if (maxApprovalLevel > 2) {
			initiateMap = vms.voucherGenerationApproval3(initiateMap, "electronic");

			if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
				Log.info(" Voucher Order Approval3 Successful  with Following Message: " + initiateMap.get("Message"));

			} else
				Assertion.assertFail( "Voucher Order Approval3 Failure with Following Message: " + initiateMap.get("Message"));
		}

		else
			Assertion.assertSkip("Max Approval Level is: " + maxApprovalLevel);
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-823")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _11_VoucherGenerationScript(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {

		final String methodName="_11_VoucherGenerationScript";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERGENSCRIPT");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		vms.voucherGenerationScriptExecution();

		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String batchType = DBHandler.AccessHandler.fetchBatchType(productID);

		if (batchType.equalsIgnoreCase("GE")) {
			Log.info(" Voucher Generation Script Successful");

			// Message Validation Here
		} else
			Assertion.assertFail("Voucher Generation Script Failed to change Status from IN to GE");

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-824")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _12_CreateBatch(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {

		final String methodName="_12_CreateBatch";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECCREATBATCH");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);


		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
		initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
			if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
				Log.info(" Batch Created Successfully  with Following Message: " + initiateMap.get("Message"));

				// Message Validation Here
			} else
				Assertion.assertFail("Batch Creation Failed with Following Message: " + initiateMap.get("Message"));

			}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
				if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
					Log.info(" Batch Created Successfully  with Following Message: " + initiateMap.get("Message"));

					// Message Validation Here
				} else
					Assertion.assertFail("Batch Creation Failed with Following Message: " + initiateMap.get("Message"));

			}
			else {
				Assertion.assertSkip("Not a valid scenario");
			}
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
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
    

	@TestManager(TestKey = "PRETUPS-825")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _13_VoucherDownload(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {

		final String methodName="_13_VoucherDownload";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERDOWNLOAD");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
			if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
				Log.info(" Batch Downloaded Successfully  with Following Message: " + initiateMap.get("Message"));

				// Message Validation Here
			} else
				Assertion.assertFail("Batch Download Failed with Following Message: " + initiateMap.get("Message"));

			}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
				if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
					Log.info(" Batch Downloaded Successfully  with Following Message: " + initiateMap.get("Message"));

					// Message Validation Here
				} else
					Assertion.assertFail("Batch Download Failed with Following Message: " + initiateMap.get("Message"));

			}
			else {
				Assertion.assertSkip("Not a valid scenario");
			}
		}
			
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-826")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _14_ChangeStatus(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {

		final String methodName="_14_ChangeStatus";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECCHANGESTATUS");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);


		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatus(initiateMap, "electronic");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatus(initiateMap, "electronic");
			}
			else if (initiateMap.get("type").equals("E") || initiateMap.get("type").equals("ET")) {
				initiateMap = vms.changeGeneratedStatusElectronic(initiateMap,"");
			}
		}
		
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Log.info(" Batch Downloaded Successfully  with Following Message: " + initiateMap.get("Message"));

			// Message Validation Here
		} else
			Assertion.assertFail("Batch Download Failed with Following Message: " + initiateMap.get("Message"));

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-827")@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _15_O2CTransfer(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {

		final String methodName="_15_O2CTransfer";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECO2C");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

	
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator
				.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		O2CTransfer o2CTransfer = new O2CTransfer(driver);
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"),
				_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);
		transferMap.put("TO_STOCK_TYPE", PretupsI.O2C_VOUCHER_TYPE_LOOKUP);
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "WH";
		if(initiateMap.get("type").equals("D") || initiateMap.get("type").equals("DT")) {
			status = "GE";
			}
			else {
			status = "WH";
			}
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID, status);
		initiateMap.put("fromSerialNumber", fromSerialNumber);
		initiateMap.put("toSerialNumber", toSerialNumber);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {		
		transferMap = o2CTransfer.initiateVoucherO2CTransfer(transferMap, initiateMap);
		
		  if (directO2CPreference == null ||
		  !directO2CPreference.equalsIgnoreCase("true"))
		  o2CTransfer.performingLevel1ApprovalVoucher(transferMap,initiateMap,"electronic"); long netPayableAmount =
		  _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
		  if((directO2CPreference == null ||
		  !directO2CPreference.equalsIgnoreCase("true")) &&
		  netPayableAmount>firstApprov)
		  o2CTransfer.performingLevel2ApprovalVoucher(transferMap.get("TO_MSISDN"),
		  transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"),transferMap,"electronic");
		  if((directO2CPreference == null ||
		  !directO2CPreference.equalsIgnoreCase("true")) &&
		  netPayableAmount>secondApprov)
		  o2CTransfer.performingLevel3ApprovalVoucher(transferMap.get("TO_MSISDN"),
		  transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"),"electronic");
		 

		initiateMap = vms.checkO2CStatus(initiateMap);
		}
		
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.O2C);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				transferMap = o2CTransfer.initiateVoucherO2CTransfer(transferMap, initiateMap);
				
				  if (directO2CPreference == null ||
				  !directO2CPreference.equalsIgnoreCase("true"))
				  o2CTransfer.performingLevel1ApprovalVoucher(transferMap,initiateMap,"electronic"); long netPayableAmount =
				  _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
				  if((directO2CPreference == null ||
				  !directO2CPreference.equalsIgnoreCase("true")) &&
				  netPayableAmount>firstApprov)
				  o2CTransfer.performingLevel2ApprovalVoucher(transferMap.get("TO_MSISDN"),
				  transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"),transferMap,"electronic");
				  if((directO2CPreference == null ||
				  !directO2CPreference.equalsIgnoreCase("true")) &&
				  netPayableAmount>secondApprov)
				  o2CTransfer.performingLevel3ApprovalVoucher(transferMap.get("TO_MSISDN"),
				  transferMap.get("TRANSACTION_ID"), transferMap.get("NetPayableAmount"),"electronic");
				  initiateMap = vms.checkO2CStatus(initiateMap);
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		
		if (initiateMap.get("currentStatus").equalsIgnoreCase("EN")) {
			Log.info(" Status Changed to " + initiateMap.get("currentStatus"));

			// Message Validation Here
		} else
			Assertion.assertFail("Current Status: " + initiateMap.get("currentStatus"));

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	public void createVoucherCardGroup(HashMap<String, String> initiateMap) throws InterruptedException{
		String serviceName=ExtentI.getValueofCorrespondingColumns(ExcelI.P2P_SERVICES_SHEET_VOUCHER, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"VCN"});
		String serviceType=ExtentI.getValueofCorrespondingColumns(ExcelI.P2P_SERVICES_SHEET_VOUCHER, ExcelI.SERVICE_TYPE, new String[]{ExcelI.SERVICE_TYPE}, new String[]{"VCN"});	
		String subService =ExtentI.getValueofCorrespondingColumns(ExcelI.P2P_SERVICES_SHEET_VOUCHER, ExcelI.SELECTOR_NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{serviceType});
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
	@TestManager(TestKey = "PRETUPS-828")
	@Test(dataProvider = "VOMSDENOMPROFILES")
	public void _16_VoucherConsumption(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {

		final String methodName="_16_VoucherConsumption";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERCONS");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		  if (_masterVO.getClientDetail("Voucher_Card_Group").equalsIgnoreCase("1"))
		         createVoucherCardGroup(initiateMap);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		RandomGeneration randomGeneration = new RandomGeneration();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "-100");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "EN";
		int numberOfVouchers = Integer.parseInt(_masterVO.getProperty("numberOfVouchersForConsumption"));
		for (int i = 0; i < numberOfVouchers; i++) {
			String serialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID, status);
			String encryptedPin = DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber);
			String pin = Decrypt.decryption(encryptedPin);
			String msisdn = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX) + randomGeneration.randomNumeric(8);
			apiData.put(voucherConsumptionAPI.VOUCHERCODE, pin);
			apiData.put(voucherConsumptionAPI.SERIALNUMBER, serialNumber);
			apiData.put(voucherConsumptionAPI.MSISDN, msisdn);
			String API = voucherConsumptionAPI.prepareAPI(apiData);
			String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.P2PReceiver, API);
			initiateMap.put("serialNumber", serialNumber);
			initiateMap = vms.voucherConsumption(initiateMap);
			if (initiateMap.get("currentStatus").equalsIgnoreCase("CU")) {
				Log.info(" Status Changed to " + initiateMap.get("currentStatus"));

				// Message Validation Here
			} else
				Assertion.assertFail("Current Status: " + initiateMap.get("currentStatus"));
		}

		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-829")
	@Test(dataProvider = "Domain&CategoryProvider_validations")
	public void _17_voucherOrderInitiateByChannelUser(String Domain, String Parent,String Category,String geotype, HashMap<String, String> mapParam) throws InterruptedException {
		
		final String methodName="_17_voucherOrderInitiateByChannelUser";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERINIT");
		VMS vms = new VMS(driver);
		HashMap<String, String> channelresultMap = null;
		AddChannelUserDetailsPage adChnlUserDetailsPage = new AddChannelUserDetailsPage(driver);
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		VoucherOrderInitiate voi = new VoucherOrderInitiate(driver);
		O2CTransfer o2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator
				.getOperatorToChannelMapWithOperatorDetailsVoucher(_masterVO.getProperty("O2CTransferCode"),"electronic");
		
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"),
				_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);
		
		initiateMap = vmsmap.defaultMap();
		
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), "electronic"));
			currentNode.assignCategory("SIT");

			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
			String mrp = UniqueChecker.UC_VOMS_MRP();
			initiateMap.put("mrp", mrp);
			initiateMap.put("voucherType", "electronic");
			String denomination = mrp + ".0";
			initiateMap.put("denomination", denomination);
			initiateMap.put("categoryName","SUADM");
			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.voucherDenominationNegative(initiateMap,"electronic");
			String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
			initiateMap.put("activeProfile", activeProfile);
			initiateMap.put("scenario", "");
			String productID = activeProfile + "(" + denomination + ")";
			initiateMap.put("productID", productID);
			
			initiateMap = vms.addVoucherProfileNegative(initiateMap);
			initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
			initiateMap = vms.voucherGenerationInitiate(initiateMap,"electronic");
			initiateMap = vms.voucherGenerationApproval1(initiateMap,"electronic");
			initiateMap = vms.voucherGenerationApproval2(initiateMap,"electronic");
			initiateMap = vms.voucherGenerationApproval3(initiateMap,"electronic");
			vms.voucherGenerationScriptExecution();
			String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			String batchType = DBHandler.AccessHandler.fetchBatchType(productIDDB);
			if (batchType.equalsIgnoreCase("GE")) {
				Log.info(" Voucher Generation Script Successful");
				initiateMap = vms.createBatchForVoucherDownload(initiateMap,"electronic");
				initiateMap = vms.vomsVoucherDownload(initiateMap,"electronic");
				initiateMap = vms.changeOtherStatus(initiateMap,"electronic");
				String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productIDDB);
				String fromSerialNumber = VOMSData[0];
				String toSerialNumber = VOMSData[1];
				String numberOfVouchers = VOMSData[2];
				initiateMap.put("fromSerialNumber",fromSerialNumber);
				initiateMap.put("toSerialNumber",toSerialNumber);
				initiateMap.put("numberOfVouchers",numberOfVouchers);
				ChannelUser channelUserLogic= new ChannelUser(driver);
				try{
					channelresultMap=channelUserLogic.channelUserInitiateVoucher(1,Domain, Parent, Category, geotype,mapParam, "electronic");
			     }
				catch(Exception e){
				Log.writeStackTrace(e);
				String actual = adChnlUserDetailsPage.getActualMessage();
				String expected = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
				Assertion.assertEquals(actual, expected);
				}
				String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
				String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
				String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
				if(APPLEVEL.equals("2")) {
					channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
					channelUserLogic.approveLevel2_ChannelUserVoucher("electronic");
					
				} else if(APPLEVEL.equals("1")) {
				
				   channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");	
				} else
					Log.info("Approval not required.");	

				channelUserLogic.changeUserFirstTimePassword();
				voi.VoucherOrderRequest(channelresultMap,initiateMap,mapParam);
				
				
				channelresultMap.put("TRANSACTION_ID", initiateMap.get("TRANSACTION_ID"));
				channelresultMap.put("TO_MSISDN", channelresultMap.get("MSISDN"));
				 if (directO2CPreference == null ||
				  !directO2CPreference.equalsIgnoreCase("true"))
				  o2CTransfer.performingLevel1ApprovalVoucher(channelresultMap,initiateMap,"electronic"); 
				 long netPayableAmount = _parser.getSystemAmount(initiateMap.get("NetPayableAmount"));
				  if((directO2CPreference == null ||
				  !directO2CPreference.equalsIgnoreCase("true")) &&
				  netPayableAmount>firstApprov)
				  o2CTransfer.performingLevel2ApprovalVoucher(channelresultMap.get("TO_MSISDN"),channelresultMap.get("TRANSACTION_ID"), initiateMap.get("NetPayableAmount"),channelresultMap,"electronic");
				  if((directO2CPreference == null ||
				  !directO2CPreference.equalsIgnoreCase("true")) &&
				  netPayableAmount>secondApprov)
				  o2CTransfer.performingLevel3ApprovalVoucher(channelresultMap.get("TO_MSISDN"),channelresultMap.get("TRANSACTION_ID"), initiateMap.get("NetPayableAmount"),"electronic");
				 

					initiateMap = vms.checkO2CStatus(initiateMap);
		 
			
			}
			else
				Assertion.assertFail("Voucher Generation Script Failed to change Status from IN to GE");
			
			if (initiateMap.get("MessageStatus").equals("Y"))
				currentNode.log(Status.PASS, "Voucher Order Initiation is successful");
			else 
				Assertion.assertFail( "Voucher Order Initiation is not successfull");
			}
			else {
			Assertion.assertSkip("NOt a valid Scenario");
			}
	
					
					Assertion.completeAssertions();Log.endTestCase(methodName);
				}

	@TestManager(TestKey = "PRETUPS-830")@Test(dataProvider = "Domain&CategoryProvider_validations_Negative")
	public void _18_voucherOrderInitiateByChannelUser(String Domain, String Parent, String Category, String geotype,
			HashMap<String, String> mapParam) throws InterruptedException {

		final String methodName="_18_voucherOrderInitiateByChannelUser";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERREQMRP");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		HashMap<String, String> channelresultMap = null;

		AddChannelUserDetailsPage adChnlUserDetailsPage = new AddChannelUserDetailsPage(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		VoucherOrderInitiate voi = new VoucherOrderInitiate(driver);
		O2CTransfer o2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetailsVoucher(
				_masterVO.getProperty("O2CTransferCode"), "electronic");

		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"),
				_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		initiateMap = vmsmap.defaultMap();

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), "electronic"));
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrpopposite = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denominationopposite = mrpopposite + ".0";
		initiateMap.put("mrpopposite", denominationopposite);
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		initiateMap.put("voucherType2", "physical");
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap.put("categoryName","SUADM");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.voucherDenominationNegative(initiateMap, "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		initiateMap.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval1(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval2(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval3(initiateMap, "electronic");
		vms.voucherGenerationScriptExecution();
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String batchType = DBHandler.AccessHandler.fetchBatchType(productIDDB);
		if (batchType.equalsIgnoreCase("GE")) {
			Log.info(" Voucher Generation Script Successful");
			initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.vomsVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.changeOtherStatus(initiateMap, "electronic");
			String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productIDDB);
			String fromSerialNumber = VOMSData[0];
			String toSerialNumber = VOMSData[1];
			String numberOfVouchers = VOMSData[2];
			initiateMap.put("fromSerialNumber", fromSerialNumber);
			initiateMap.put("toSerialNumber", toSerialNumber);
			initiateMap.put("numberOfVouchers", numberOfVouchers);
			ChannelUser channelUserLogic = new ChannelUser(driver);
			try {
				channelresultMap = channelUserLogic.channelUserInitiateVoucher(1, Domain, Parent, Category, geotype,
						mapParam, "electronic");
			} catch (Exception e) {
				Log.writeStackTrace(e);
				String actual = adChnlUserDetailsPage.getActualMessage();
				String expected = MessagesDAO.prepareMessageByKey("errors.required",
						MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
				Assertion.assertEquals(actual, expected);
			
			}
			String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
			String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0], networkCode,
					UserAccess.userapplevelpreference());
			if (APPLEVEL.equals("2")) {
				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
				channelUserLogic.approveLevel2_ChannelUserVoucher("electronic");

			} else if (APPLEVEL.equals("1")) {

				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
			} else
				Log.info("Approval not required.");

			channelUserLogic.changeUserFirstTimePassword();
			try {
				voi.VoucherOrderRequestMRPCheck(channelresultMap, initiateMap, mapParam);
				Assertion.assertFail("MRP getting  displayed.");
				
			} catch (AutomationException ex) {
				currentNode.pass(ex.toString());
			}
		} else
			Assertion.assertFail("Voucher Generation Script Failed to change Status from IN to GE");

		if (initiateMap.get("MessageStatus").equals("Y"))
			currentNode.log(Status.PASS, "MRP not displayed");
		else
			Assertion.assertFail( "MRP is displayed");
		}
		else {
			Assertion.assertSkip("Not a valid Scenario");
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-831")@Test(dataProvider = "Domain&CategoryProvider_validations_Negative")
	public void _19_voucherOrderInitiateByChannelUserNoVoucherType(String Domain, String Parent, String Category, String geotype,
			HashMap<String, String> mapParam) throws InterruptedException {

		final String methodName="_19_voucherOrderInitiateByChannelUserNoVoucherType";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERREQVT");
		//moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);


		HashMap<String, String> channelresultMap = null;

		AddChannelUserDetailsPage adChnlUserDetailsPage = new AddChannelUserDetailsPage(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		VoucherOrderInitiate voi = new VoucherOrderInitiate(driver);
		O2CTransfer o2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetailsVoucher(
				_masterVO.getProperty("O2CTransferCode"), "electronic");

		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"),
				_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		initiateMap = vmsmap.defaultMap();

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), "electronic"));
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrpopposite = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denominationopposite = mrpopposite + ".0";
		initiateMap.put("mrpopposite", denominationopposite);
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		initiateMap.put("voucherType2", "physical");
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap.put("categoryName","SUADM");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.voucherDenominationNegative(initiateMap, "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		initiateMap.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval1(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval2(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval3(initiateMap, "electronic");
		vms.voucherGenerationScriptExecution();
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String batchType = DBHandler.AccessHandler.fetchBatchType(productIDDB);
		if (batchType.equalsIgnoreCase("GE")) {
			Log.info(" Voucher Generation Script Successful");
			initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.vomsVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.changeOtherStatus(initiateMap, "electronic");
			String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productIDDB);
			String fromSerialNumber = VOMSData[0];
			String toSerialNumber = VOMSData[1];
			String numberOfVouchers = VOMSData[2];
			initiateMap.put("fromSerialNumber", fromSerialNumber);
			initiateMap.put("toSerialNumber", toSerialNumber);
			initiateMap.put("numberOfVouchers", numberOfVouchers);
			ChannelUser channelUserLogic = new ChannelUser(driver);
			try {
				channelresultMap = channelUserLogic.channelUserInitiateVoucherNoVoucherType(1, Domain, Parent, Category, geotype,
						mapParam, "electronic");
			} catch (Exception e) {
				Log.writeStackTrace(e);
				String actual = adChnlUserDetailsPage.getActualMessage();
				String expected = MessagesDAO.prepareMessageByKey("errors.required",
						MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
				Assertion.assertEquals(actual, expected);
			
			}
			String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
			String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0], networkCode,
					UserAccess.userapplevelpreference());
			if (APPLEVEL.equals("2")) {
				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
				channelUserLogic.approveLevel2_ChannelUserVoucher("electronic");

			} else if (APPLEVEL.equals("1")) {

				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
			} else
				Log.info("Approval not required.");

			channelUserLogic.changeUserFirstTimePassword();
			try {
				voi.VoucherOrderRequestNoVoucher(channelresultMap, initiateMap, mapParam);
				Assertion.assertFail( "Voucher Type is available");
				
			} catch (AutomationException ex) {
				currentNode.pass(ex.toString());
			}
		} else
			Assertion.assertFail("Voucher Generation Script Failed to change Status from IN to GE");

		if (initiateMap.get("MessageStatus").equals("Y"))
			currentNode.log(Status.PASS, "Voucher Type Not displayed");
		else
			Assertion.assertFail( "Voucher Type is displayed");
		}
		else {
			Assertion.assertSkip("Not a valid Scenario");
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-832")@Test(dataProvider = "Domain&CategoryProvider_validations_Negative")
	public void _20_voucherOrderInitiateByChannelUserNoOrderRequest(String Domain, String Parent, String Category, String geotype,
			HashMap<String, String> mapParam) throws InterruptedException {

		final String methodName="_20_voucherOrderInitiateByChannelUserNoOrderRequest";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERREQVO");
		//moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);


		HashMap<String, String> channelresultMap = null;

		AddChannelUserDetailsPage adChnlUserDetailsPage = new AddChannelUserDetailsPage(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		VoucherOrderInitiate voi = new VoucherOrderInitiate(driver);
		O2CTransfer o2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetailsVoucher(
				_masterVO.getProperty("O2CTransferCode"), "electronic");

		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"),
				_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		initiateMap = vmsmap.defaultMap();

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), "electronic"));
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrpopposite = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denominationopposite = mrpopposite + ".0";
		initiateMap.put("mrpopposite", denominationopposite);
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		initiateMap.put("voucherType2", "physical");
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap.put("categoryName","SUADM");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.voucherDenominationNegative(initiateMap, "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		initiateMap.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval1(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval2(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval3(initiateMap, "electronic");
		vms.voucherGenerationScriptExecution();
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String batchType = DBHandler.AccessHandler.fetchBatchType(productIDDB);
		if (batchType.equalsIgnoreCase("GE")) {
			Log.info(" Voucher Generation Script Successful");
			initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.vomsVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.changeOtherStatus(initiateMap, "electronic");
			String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productIDDB);
			String fromSerialNumber = VOMSData[0];
			String toSerialNumber = VOMSData[1];
			String numberOfVouchers = VOMSData[2];
			initiateMap.put("fromSerialNumber", fromSerialNumber);
			initiateMap.put("toSerialNumber", toSerialNumber);
			initiateMap.put("numberOfVouchers", numberOfVouchers);
			ChannelUser channelUserLogic = new ChannelUser(driver);
			try {
				channelresultMap = channelUserLogic.channelUserInitiateVoucherNoOrderRequest(1, Domain, Parent, Category, geotype,
						mapParam, "electronic");
			} catch (Exception e) {
				Log.writeStackTrace(e);
				String actual = adChnlUserDetailsPage.getActualMessage();
				String expected = MessagesDAO.prepareMessageByKey("errors.required",
						MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
				Assertion.assertEquals(actual, expected)	;		}
			String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
			String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0], networkCode,
					UserAccess.userapplevelpreference());
			if (APPLEVEL.equals("2")) {
				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
				channelUserLogic.approveLevel2_ChannelUserVoucher("electronic");

			} else if (APPLEVEL.equals("1")) {

				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
			} else
				Log.info("Approval not required.");

			channelUserLogic.changeUserFirstTimePassword();
			try {
				voi.VoucherOrderRequestNoOrderRequest(channelresultMap, initiateMap, mapParam);
				Assertion.assertFail("Voucher Order Request Link is found");
				
			} catch (AutomationException ex) {
				currentNode.pass(ex.toString());
			}
		} else
			Assertion.assertFail("Voucher Generation Script Failed to change Status from IN to GE");

		if (initiateMap.get("MessageStatus").equals("Y"))
			currentNode.log(Status.PASS, "Voucher Order Request Link not found");
		else
			Assertion.assertFail("Voucher Order Request Link is found");
		}
		else {
			Assertion.assertSkip("Not a valid Scenario");
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-833")@Test(dataProvider = "Domain&CategoryProvider_validations_Negative")
	public void _21_voucherOrderInitiateByChannelUserNoData(String Domain, String Parent, String Category, String geotype,
			HashMap<String, String> mapParam) throws InterruptedException {

		final String methodName="_21_voucherOrderInitiateByChannelUserNoData";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERREQNODENOM");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		HashMap<String, String> channelresultMap = null;

		AddChannelUserDetailsPage adChnlUserDetailsPage = new AddChannelUserDetailsPage(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		VoucherOrderInitiate voi = new VoucherOrderInitiate(driver);
		O2CTransfer o2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetailsVoucher(
				_masterVO.getProperty("O2CTransferCode"), "electronic");

		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"),
				_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		initiateMap = vmsmap.defaultMap();

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), "electronic"));
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrpopposite = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denominationopposite = mrpopposite + ".0";
		initiateMap.put("mrpopposite", denominationopposite);
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		initiateMap.put("voucherType2", "physical");
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap.put("categoryName","SUADM");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.voucherDenominationNegative(initiateMap, "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		initiateMap.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval1(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval2(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval3(initiateMap, "electronic");
		vms.voucherGenerationScriptExecution();
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String batchType = DBHandler.AccessHandler.fetchBatchType(productIDDB);
		if (batchType.equalsIgnoreCase("GE")) {
			Log.info(" Voucher Generation Script Successful");
			initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.vomsVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.changeOtherStatus(initiateMap, "electronic");
			String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productIDDB);
			String fromSerialNumber = VOMSData[0];
			String toSerialNumber = VOMSData[1];
			String numberOfVouchers = VOMSData[2];
			initiateMap.put("fromSerialNumber", fromSerialNumber);
			initiateMap.put("toSerialNumber", toSerialNumber);
			initiateMap.put("numberOfVouchers", numberOfVouchers);
			ChannelUser channelUserLogic = new ChannelUser(driver);
			try {
				channelresultMap = channelUserLogic.channelUserInitiateVoucher(1, Domain, Parent, Category, geotype,
						mapParam, "electronic");
			} catch (Exception e) {
				Log.writeStackTrace(e);
				String actual = adChnlUserDetailsPage.getActualMessage();
				String expected = MessagesDAO.prepareMessageByKey("errors.required",
						MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
				Assertion.assertEquals(actual, expected);
				
			}
			String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
			String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0], networkCode,
					UserAccess.userapplevelpreference());
			if (APPLEVEL.equals("2")) {
				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
				channelUserLogic.approveLevel2_ChannelUserVoucher("electronic");

			} else if (APPLEVEL.equals("1")) {

				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
			} else
				Log.info("Approval not required.");

			channelUserLogic.changeUserFirstTimePassword();
			voi.VoucherOrderRequestNoData(channelresultMap, initiateMap, mapParam);
			
		
		
		String message = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetails.error.enterdata");
		
		if(initiateMap.get("Message").equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			Assertion.assertFail( "Expected [" + message + "] but found [" + initiateMap.get("Message") + "]");
		    
		}
		
		}
		
		else {
			Assertion.assertFail("Voucher Generation Script Failed to change Status from IN to GE");
			
		}
		}
		else {
			Assertion.assertSkip("Not a valid Scenatio");
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-834")@Test(dataProvider = "Domain&CategoryProvider_validations_Negative")
	public void _22_voucherOrderInitiateByChannelUserNoRemark(String Domain, String Parent, String Category, String geotype,
			HashMap<String, String> mapParam) throws InterruptedException {

		final String methodName="_22_voucherOrderInitiateByChannelUserNoRemark";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERREQNOREMARK");
		//moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

	
		HashMap<String, String> channelresultMap = null;

		AddChannelUserDetailsPage adChnlUserDetailsPage = new AddChannelUserDetailsPage(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		VoucherOrderInitiate voi = new VoucherOrderInitiate(driver);
		O2CTransfer o2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetailsVoucher(
				_masterVO.getProperty("O2CTransferCode"), "electronic");

		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"),
				_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		initiateMap = vmsmap.defaultMap();

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), "electronic"));
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrpopposite = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denominationopposite = mrpopposite + ".0";
		initiateMap.put("mrpopposite", denominationopposite);
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		initiateMap.put("voucherType2", "physical");
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap.put("categoryName","SUADM");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.voucherDenominationNegative(initiateMap, "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		initiateMap.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval1(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval2(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval3(initiateMap, "electronic");
		vms.voucherGenerationScriptExecution();
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String batchType = DBHandler.AccessHandler.fetchBatchType(productIDDB);
		if (batchType.equalsIgnoreCase("GE")) {
			Log.info(" Voucher Generation Script Successful");
			initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.vomsVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.changeOtherStatus(initiateMap, "electronic");
			String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productIDDB);
			String fromSerialNumber = VOMSData[0];
			String toSerialNumber = VOMSData[1];
			String numberOfVouchers = VOMSData[2];
			initiateMap.put("fromSerialNumber", fromSerialNumber);
			initiateMap.put("toSerialNumber", toSerialNumber);
			initiateMap.put("numberOfVouchers", numberOfVouchers);
			ChannelUser channelUserLogic = new ChannelUser(driver);
			try {
				channelresultMap = channelUserLogic.channelUserInitiateVoucher(1, Domain, Parent, Category, geotype,
						mapParam, "electronic");
			} catch (Exception e) {
				Log.writeStackTrace(e);
				String actual = adChnlUserDetailsPage.getActualMessage();
				String expected = MessagesDAO.prepareMessageByKey("errors.required",
						MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
				Assertion.assertEquals(actual, expected);
			}
			String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
			String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0], networkCode,
					UserAccess.userapplevelpreference());
			if (APPLEVEL.equals("2")) {
				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
				channelUserLogic.approveLevel2_ChannelUserVoucher("electronic");

			} else if (APPLEVEL.equals("1")) {

				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
			} else
				Log.info("Approval not required.");

			channelUserLogic.changeUserFirstTimePassword();
			voi.VoucherOrderRequestNoRemark(channelresultMap, initiateMap, mapParam);
			
		
		
		String message = MessagesDAO.prepareMessageByKey("user.addchanneluser.error.remarkrequired");
		
		if(initiateMap.get("Message").equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + message + "] but found [" + initiateMap.get("Message") + "]");
		    
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
		}
		
		else {
			Assertion.assertFail("Voucher Generation Script Failed to change Status from IN to GE");
			
		}
		}
		else {
			Assertion.assertSkip("Not a valid Scenario");
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-835")@Test(dataProvider = "Domain&CategoryProvider_validations_Negative")
	public void _23_voucherOrderInitiateByChannelUserNoPayINST(String Domain, String Parent, String Category, String geotype,
			HashMap<String, String> mapParam) throws InterruptedException {

		final String methodName="_23_voucherOrderInitiateByChannelUserNoPayINST";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERREQNOPAYTYPE");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

	

		HashMap<String, String> channelresultMap = null;

		AddChannelUserDetailsPage adChnlUserDetailsPage = new AddChannelUserDetailsPage(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		VoucherOrderInitiate voi = new VoucherOrderInitiate(driver);
		O2CTransfer o2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetailsVoucher(
				_masterVO.getProperty("O2CTransferCode"), "electronic");

		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"),
				_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		initiateMap = vmsmap.defaultMap();

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), "electronic"));
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrpopposite = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denominationopposite = mrpopposite + ".0";
		initiateMap.put("mrpopposite", denominationopposite);
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		initiateMap.put("voucherType2", "physical");
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap.put("categoryName","SUADM");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.voucherDenominationNegative(initiateMap, "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		initiateMap.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval1(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval2(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval3(initiateMap, "electronic");
		vms.voucherGenerationScriptExecution();
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String batchType = DBHandler.AccessHandler.fetchBatchType(productIDDB);
		if (batchType.equalsIgnoreCase("GE")) {
			Log.info(" Voucher Generation Script Successful");
			initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.vomsVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.changeOtherStatus(initiateMap, "electronic");
			String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productIDDB);
			String fromSerialNumber = VOMSData[0];
			String toSerialNumber = VOMSData[1];
			String numberOfVouchers = VOMSData[2];
			initiateMap.put("fromSerialNumber", fromSerialNumber);
			initiateMap.put("toSerialNumber", toSerialNumber);
			initiateMap.put("numberOfVouchers", numberOfVouchers);
			ChannelUser channelUserLogic = new ChannelUser(driver);
			try {
				channelresultMap = channelUserLogic.channelUserInitiateVoucher(1, Domain, Parent, Category, geotype,
						mapParam, "electronic");
			} catch (Exception e) {
				Log.writeStackTrace(e);
				String actual = adChnlUserDetailsPage.getActualMessage();
				String expected = MessagesDAO.prepareMessageByKey("errors.required",
						MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
				Validator.messageCompare(actual, expected);
			}
			String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
			String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0], networkCode,
					UserAccess.userapplevelpreference());
			if (APPLEVEL.equals("2")) {
				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
				channelUserLogic.approveLevel2_ChannelUserVoucher("electronic");

			} else if (APPLEVEL.equals("1")) {

				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
			} else
				Log.info("Approval not required.");

			channelUserLogic.changeUserFirstTimePassword();
			voi.VoucherOrderRequestNoPayINST(channelresultMap, initiateMap, mapParam);
			
		
		
		String message = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetails.error.required.paymentinstrumenttype");
		
		if(initiateMap.get("Message").equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + message + "] but found [" + initiateMap.get("Message") + "]");
		  
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
		}
		
		else {
			Assertion.assertFail("Voucher Generation Script Failed to change Status from IN to GE");
			
		}
		}
		else {
			Assertion.assertSkip("Not a valid Scenario");
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-836")@Test(dataProvider = "Domain&CategoryProvider_validations_Negative")
	public void _24_voucherOrderInitiateByChannelUserNoPayINSTDATE(String Domain, String Parent, String Category, String geotype,
			HashMap<String, String> mapParam) throws InterruptedException {

		final String methodName="_24_voucherOrderInitiateByChannelUserNoPayINSTDATE";Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITELECVOUCHERREQNOPAYDATE");
	//	moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		
		HashMap<String, String> channelresultMap = null;

		AddChannelUserDetailsPage adChnlUserDetailsPage = new AddChannelUserDetailsPage(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsmap = new VMSMap();
		VoucherOrderInitiate voi = new VoucherOrderInitiate(driver);
		O2CTransfer o2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetailsVoucher(
				_masterVO.getProperty("O2CTransferCode"), "electronic");

		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"),
				_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		initiateMap = vmsmap.defaultMap();

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), "electronic"));
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrpopposite = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denominationopposite = mrpopposite + ".0";
		initiateMap.put("mrpopposite", denominationopposite);
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		initiateMap.put("voucherType2", "physical");
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap.put("categoryName","SUADM");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
		initiateMap = vms.voucherDenominationNegative(initiateMap, "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		initiateMap.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval1(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval2(initiateMap, "electronic");
		initiateMap = vms.voucherGenerationApproval3(initiateMap, "electronic");
		vms.voucherGenerationScriptExecution();
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String batchType = DBHandler.AccessHandler.fetchBatchType(productIDDB);
		if (batchType.equalsIgnoreCase("GE")) {
			Log.info(" Voucher Generation Script Successful");
			initiateMap = vms.createBatchForVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.vomsVoucherDownload(initiateMap, "electronic");
			initiateMap = vms.changeOtherStatus(initiateMap, "electronic");
			String[] VOMSData = DBHandler.AccessHandler.getVoucherBatchDetails(productIDDB);
			String fromSerialNumber = VOMSData[0];
			String toSerialNumber = VOMSData[1];
			String numberOfVouchers = VOMSData[2];
			initiateMap.put("fromSerialNumber", fromSerialNumber);
			initiateMap.put("toSerialNumber", toSerialNumber);
			initiateMap.put("numberOfVouchers", numberOfVouchers);
			ChannelUser channelUserLogic = new ChannelUser(driver);
			try {
				channelresultMap = channelUserLogic.channelUserInitiateVoucher(1, Domain, Parent, Category, geotype,
						mapParam, "electronic");
			} catch (Exception e) {
				Log.writeStackTrace(e);
				String actual = adChnlUserDetailsPage.getActualMessage();
				String expected = MessagesDAO.prepareMessageByKey("errors.required",
						MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
				Assertion.assertEquals(actual, expected);
			}
			String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
			String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0], networkCode,
					UserAccess.userapplevelpreference());
			if (APPLEVEL.equals("2")) {
				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
				channelUserLogic.approveLevel2_ChannelUserVoucher("electronic");

			} else if (APPLEVEL.equals("1")) {

				channelUserLogic.approveLevel1_ChannelUserVoucher("electronic");
			} else
				Log.info("Approval not required.");

			channelUserLogic.changeUserFirstTimePassword();
			voi.VoucherOrderRequestNoPayINSTDATE(channelresultMap, initiateMap, mapParam);
			
		
		
		String message = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetails.error.required.paymentinstrumentdate");
		
		if(initiateMap.get("Message").equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
        Assertion.assertFail( "Expected [" + message + "] but found [" + initiateMap.get("Message") + "]");
		    
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
		}
		
		else {
			   Assertion.assertFail( "Voucher Generation Script Failed to change Status from IN to GE");
			
		}
		}
		else {
			Assertion.assertSkip("Not a valid Scenario");
		}
		Assertion.completeAssertions();Log.endTestCase(methodName);
	}

	/*@Test
	@TestManager(TestKey = "PRETUPS-1780")
	public void _25_UploadDocumentDoc() throws InterruptedException {
		final String methodName = "Test_UploadDocumentDocElectronic";
		 Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSELEDOCUPLOAD");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap.put("categoryName","SUADM");
		vms.voucherDenominationNegative(initiateMap,"electronic");
		vms.addVoucherProfileNegative(initiateMap);
		vms.addActiveVoucherProfileNegative(initiateMap);
		vms.voucherGenerationInitiate(initiateMap,"electronic");
		vms.voucherGenerationApproval1(initiateMap,"electronic");
		vms.voucherGenerationApproval2(initiateMap, "electronic");
		vms.voucherGenerationApproval3uploadDoc(initiateMap, "electronic");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Log.endTestCase(methodName);	
		}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-1781")
	public void _26_UploadDocumentDocx() throws InterruptedException {
		final String methodName = "Test_UploadDocumentDocxElectronic";
		 Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSELEDOCXUPLOAD");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap.put("categoryName","SUADM");
		vms.voucherDenominationNegative(initiateMap,"electronic");
		vms.addVoucherProfileNegative(initiateMap);
		vms.addActiveVoucherProfileNegative(initiateMap);
		vms.voucherGenerationInitiate(initiateMap,"electronic");
		vms.voucherGenerationApproval1(initiateMap,"electronic");
		vms.voucherGenerationApproval2(initiateMap, "electronic");
		vms.voucherGenerationApproval3uploadDocx(initiateMap, "electronic");
			
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Log.endTestCase(methodName);
		}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1782")
	public void _27_UploadDocumentPdf() throws InterruptedException {
		final String methodName = "Test_UploadDocumentPdfElectronic";
		 Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSELEPDFUPLOAD");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap.put("categoryName","SUADM");
		vms.voucherDenominationNegative(initiateMap,"electronic");
		vms.addVoucherProfileNegative(initiateMap);
		vms.addActiveVoucherProfileNegative(initiateMap);
		vms.voucherGenerationInitiate(initiateMap,"electronic");
		vms.voucherGenerationApproval1(initiateMap,"electronic");
		vms.voucherGenerationApproval2(initiateMap, "electronic");
		vms.voucherGenerationApproval3uploadPdf(initiateMap, "electronic");
		
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Log.endTestCase(methodName);
		}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1783")
	public void _28_UploadDocumentNegative() throws InterruptedException {
		final String methodName = "Test_UploadDocumentNegativeElectronic";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSELEFUPLOADNEGATIVE");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		initiateMap.put("voucherType", "electronic");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap.put("categoryName","SUADM");
		vms.voucherDenominationNegative(initiateMap,"electronic");
		vms.addVoucherProfileNegative(initiateMap);
		vms.addActiveVoucherProfileNegative(initiateMap);
		vms.voucherGenerationInitiate(initiateMap,"electronic");
		vms.voucherGenerationApproval1(initiateMap,"electronic");
		vms.voucherGenerationApproval2(initiateMap, "electronic");
		vms.voucherGenerationApproval3uploadGIF(initiateMap, "electronic");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "N");
		Log.endTestCase(methodName);
		}

*/
	@DataProvider(name = "Domain&CategoryProvider_validations_Negative")
	public Object[][] DomainCategoryProvider_validations_Negative() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
		
		ChannelUserMap chnlUserMap = new ChannelUserMap();
		RandomGeneration randGen = new RandomGeneration();
		
		String[] userDetailsHL = new String[5];
		
		userDetailsHL[0] = ExcelUtility.getCellData(0, "DOMAIN_NAME", 1);
		userDetailsHL[1] = ExcelUtility.getCellData(0, "PARENT_CATEGORY_NAME", 1);
		userDetailsHL[2] = ExcelUtility.getCellData(0, "CATEGORY_NAME", 1);
		userDetailsHL[3] = ExcelUtility.getCellData(0, "GRPH_DOMAIN_TYPE", 1);	
		
	
		int minPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_LOGIN_PWD_LENGTH"));
		int maxPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MAX_LOGIN_PWD_LENGTH"));
		int minMSISDNLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_MSISDN_LENGTH"));
		int remainingMSISDN = minMSISDNLength-SystemPreferences.MSISDN_PREFIX_LENGTH-1;
		
		String minPaswd = CommonUtils.generatePassword(minPaswdLength-2)+"@";
		
		String prefix = _masterVO.getMasterValue("Prepaid MSISDN Prefix");
		String minMSISDN = prefix + randGen.randomNumberWithoutZero(remainingMSISDN);
		Object[][] categoryData;
		   categoryData = new Object[][]{{userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],chnlUserMap.getChannelUserMap("paymentType","CASH","documentType","PAN")},null};

		   return categoryData;
	}

	@DataProvider(name = "Domain&CategoryProvider_validations")
	public Object[][] DomainCategoryProvider_validations() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
		
		ChannelUserMap chnlUserMap = new ChannelUserMap();
		RandomGeneration randGen = new RandomGeneration();
		
		String loginID=null;
		String mobileNumber=null;
		
		String[] userDetailsHL = new String[5];
		
		userDetailsHL[0] = ExcelUtility.getCellData(0, "DOMAIN_NAME", 1);
		userDetailsHL[1] = ExcelUtility.getCellData(0, "PARENT_CATEGORY_NAME", 1);
		userDetailsHL[2] = ExcelUtility.getCellData(0, "CATEGORY_NAME", 1);
		userDetailsHL[3] = ExcelUtility.getCellData(0, "GRPH_DOMAIN_TYPE", 1);	
		
	
		int minPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_LOGIN_PWD_LENGTH"));
		int maxPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MAX_LOGIN_PWD_LENGTH"));
		int minMSISDNLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_MSISDN_LENGTH"));
		int remainingMSISDN = minMSISDNLength-SystemPreferences.MSISDN_PREFIX_LENGTH-1;
		
		String minPaswd = CommonUtils.generatePassword(minPaswdLength-2)+"@";
		
		String prefix = _masterVO.getMasterValue("Prepaid MSISDN Prefix");
		String minMSISDN = prefix + randGen.randomNumberWithoutZero(remainingMSISDN);
		Object[][] categoryData;
		   categoryData = new Object[][]{{userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],chnlUserMap.getChannelUserMap("paymentType","CASH","documentType","PAN")},null};

		   return categoryData;
		
	}
	
	
	@DataProvider(name = "VOMSDENOMINATIONS")
	public Object[][] VOMSDenominationDP() {

		int VOMS_DATA_COUNT = Integer.parseInt(_masterVO.getProperty("vms.voms.profiles.count"));
		String voucherCode = "E";
		String voucherType = DBHandler.AccessHandler.getVoucherType(voucherCode);
		Object[][] VOMSData = DBHandler.AccessHandler.getVOMSDetailsBasedOnVoucherType(voucherType);
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
			for (int j = 0; j < VOMSData.length; j++) {
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
		VOMSDenomSheetBuilder.prepareVOMSProfileSheetForElectronic(dataObj);

		return dataObj;
	}

	@DataProvider(name = "VOMSDENOMPROFILES")
	public Object[][] VOMSDenominationProfilesDP() {

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
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
			String productID = activeProfile + "(" + denomination + ")";
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

}
