package com.testscripts.sit;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.Features.OperatorUser;
import com.Features.VMS;
import com.Features.mapclasses.VMSMap;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.databuilder.BuilderLogic;
import com.dbrepository.DBHandler;
import com.dbrepository.DBInterface;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SIT_VMS_NA)
public class SIT_VMS_NETWORKADMIN extends BaseTest {

	String MasterSheetPath;
	static String directO2CPreference;
	static String moduleCode;
	private static String NetworkADM_Name = null;
	private static String SuperADM_Name = null;
	private static int NetworkAdminDataSheetRowNum = 0;
	private static String payableAmountAbsent = "Payable amount disabled";
	static String skipActiveProfile = "Skipping active profile cases";
	static String skipForNetworkIN = "Skipped for IN network";

	/**
	 * @author Chetan.chawla This test fetches Network Admin Users from Database &
	 *         write them in Operator Users Hierarchy Sheet as per VMS Network Code
	 * @throws SQLException
	 * @throws Exception    Dependencies: Database Connectivity / BuilderLogic.java
	 *                      for Functional Logic.
	 **/
	@Test(description = "Fetch Operator User Hierarchy from database & add them to Operator Users Hierarchy Sheet")
	public void Test_01_FetchOperatorUsers_VMS() throws SQLException {
		BuilderLogic OperatorHierarchy = new BuilderLogic();
		OperatorHierarchy.WriteOperatorUserstoExcelVMSNetworkAdmin();
	}

	@Test
	@TestManager(TestKey = "PRETUPS-404") // TO BE UNCOMMENTED BY WITH JIRA TEST CASE ID
	public void Test_02_CreateNetworkAdmin() throws InterruptedException {
		final String methodName = "Test_CreateNetworkAdmin";
		Log.startTestCase(methodName);

		OperatorUser optUsrCreation = new OperatorUser(driver);
		initializeTestData();

		// Test Case Number 1: Network Admin Creation
		currentNode = test
				.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PNETWORKADMINCREATION1").getExtentCase(),
						SuperADM_Name, NetworkADM_Name))
				.assignCategory(TestCategory.PREREQUISITE);
		HashMap<String, String> optMap = optUsrCreation.operatorUserInitiateVMS(SuperADM_Name, NetworkADM_Name);
		Assertion.assertNotNull(optMap.get("initiateMsg"));

		// Test Case Number 2: Network Admin Approval
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PNETWORKADMINCREATION2").getExtentCase())
				.assignCategory(TestCategory.PREREQUISITE);
		optUsrCreation.approveUserVMS(SuperADM_Name);

		// Test Case Number 3: Network Admin Password Change
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PNETWORKADMINCREATION3").getExtentCase())
				.assignCategory(TestCategory.PREREQUISITE);
		optUsrCreation.changeUserFirstTimePassword();

		String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected = MessagesDAO.getLabelByKey("login.changeCommonLoginPassword.updatesuccessmessage");

		boolean assertStatus = Assertion.assertEquals(actual, expected);

		if (assertStatus) {
			optUsrCreation.writeOperatorUserDataVMS(NetworkAdminDataSheetRowNum);
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	/* ----------------------- H E L P E R M E T H O D S ------------------ 
	
	 * -----------------------------------------------------------------------------
	 * --------------------*/
	 
	private void initializeTestData() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_NETWORK_ADMIN_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		while (NetworkAdminDataSheetRowNum <= rowCount) {
			String ParentCategoryCode = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE,
					NetworkAdminDataSheetRowNum);
			String CategoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, NetworkAdminDataSheetRowNum);
			if (ParentCategoryCode.equals(PretupsI.SUPERADMIN_CATCODE)
					&& CategoryCode.equals(PretupsI.NETWORKADMIN_CATCODE)) {
				NetworkADM_Name = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, NetworkAdminDataSheetRowNum);
				SuperADM_Name = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, NetworkAdminDataSheetRowNum);
				break;
			}

			NetworkAdminDataSheetRowNum++;
		}
	}

	@Test
	@TestManager(TestKey = "PRETUPS-767") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_03_VomsDenomination() throws Exception {
		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA61");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		HashMap<String, String> initiateMap2 = new HashMap<String, String>();

		currentNode = test
				.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType")));
		currentNode.assignCategory("SIT");

		if(DBHandler.AccessHandler.isMultipleNetworkEnabled()) {
		int size = 0;
		int i = 1;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values = vms.voucherTypeList();
		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		initiateMap2 = vmsmap.defaultMap();

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());

		initiateMap2.put("denominationName", initiateMap.get("denominationName"));
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());

		initiateMap2.put("shortName", initiateMap.get("shortName"));
		String mrp = UniqueChecker.UC_VOMS_MRP();
		String mrp2 = mrp;
		initiateMap.put("mrp", mrp);
		initiateMap2.put("mrp", mrp2);
		if(size!=0) {
		initiateMap.put("voucherType", values.get(i));
		initiateMap2.put("voucherType", values.get(i));
		}
		else {
			String text =vms.voucherType();
			text=text.toLowerCase();
			initiateMap.put("voucherType", text);
			initiateMap2.put("voucherType", text);
		}
		String denomination = mrp + ".0";
		String denomination2 = mrp2 + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap2.put("denomination", denomination2);
		initiateMap.put("categoryName", "NWADM");
		initiateMap2.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
		initiateMap = vms.viewVoucherDenominationOtherNetworkAdmin(initiateMap, "");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "N");
		}else {
			Assertion.assertSkip("multiple network does not exist");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-767") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_04_vomsProfile() throws Exception {
		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA62");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		HashMap<String, String> initiateMap2 = new HashMap<String, String>();

		currentNode = test
				.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType")));
		currentNode.assignCategory("SIT");

		if(DBHandler.AccessHandler.isMultipleNetworkEnabled()) {
		int size = 0;
		int i = 1;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values = vms.voucherTypeList();

		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		initiateMap2 = vmsmap.defaultMap();

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());

		initiateMap2.put("denominationName", initiateMap.get("denominationName"));
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());

		initiateMap2.put("shortName", initiateMap.get("shortName"));
		String mrp = UniqueChecker.UC_VOMS_MRP();
		String mrp2 = mrp;
		initiateMap.put("mrp", mrp);
		initiateMap2.put("mrp", mrp2);
		initiateMap.put("voucherType", values.get(i));
		initiateMap2.put("voucherType", values.get(i));
		String denomination = mrp + ".0";
		String denomination2 = mrp2 + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap2.put("denomination", denomination2);
		initiateMap.put("categoryName", "NWADM");
		initiateMap2.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		String activeProfile2 = activeProfile;
		initiateMap.put("activeProfile", activeProfile);
		initiateMap2.put("activeProfile", activeProfile2);
		initiateMap.put("scenario", "");
		initiateMap2.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		String productID2 = activeProfile2 + "(" + denomination2 + ")";
		initiateMap.put("productID", productID);
		initiateMap2.put("productID", productID2);
		initiateMap = vms.addVoucherProfileNegativeNA(initiateMap);
		initiateMap = vms.viewVoucherProfileNegativeNA(initiateMap, "");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "N");
		}else {
			Assertion.assertSkip("multiple network does not exist");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-767") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_05_vomsActiveProfile() throws Exception {
		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA63");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		HashMap<String, String> initiateMap2 = new HashMap<String, String>();

		currentNode = test
				.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType")));
		currentNode.assignCategory("SIT");
		
		if(DBHandler.AccessHandler.isMultipleNetworkEnabled()) {
		int size = 0;
		int i = 1;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values = vms.voucherTypeList();

		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		initiateMap2 = vmsmap.defaultMap();

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());

		initiateMap2.put("denominationName", initiateMap.get("denominationName"));
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());

		initiateMap2.put("shortName", initiateMap.get("shortName"));
		String mrp = UniqueChecker.UC_VOMS_MRP();
		String mrp2 = mrp;
		initiateMap.put("mrp", mrp);
		initiateMap2.put("mrp", mrp2);
		initiateMap.put("voucherType", values.get(i));
		initiateMap2.put("voucherType", values.get(i));
		String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{values.get(i)});
		initiateMap.put("type", typeVoucher);
		initiateMap2.put("type", typeVoucher);
		String denomination = mrp + ".0";
		String denomination2 = mrp2 + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap2.put("denomination", denomination2);
		initiateMap.put("categoryName", "NWADM");
		initiateMap2.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		String activeProfile2 = activeProfile;
		initiateMap.put("activeProfile", activeProfile);
		initiateMap2.put("activeProfile", activeProfile2);
		initiateMap.put("scenario", "");
		initiateMap2.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		String productID2 = activeProfile2 + "(" + denomination2 + ")";
		initiateMap.put("productID", productID);
		initiateMap2.put("productID", productID2);
		initiateMap = vms.addVoucherProfileNegativeNA(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.addActiveVoucherProfile(initiateMap, "");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.addActiveVoucherProfile(initiateMap, "");
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		
		vms.viewActiveProfileNA(initiateMap, "");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "N");
		}
		else {
			Assertion.assertSkip("Not a valid case for this scenario");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-767") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_06_vomsGenerationApproval1() throws Exception {
		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA64");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		HashMap<String, String> initiateMap2 = new HashMap<String, String>();

		currentNode = test
				.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType")));
		currentNode.assignCategory("SIT");

		if(DBHandler.AccessHandler.isMultipleNetworkEnabled()) {
		int size = 0;
		int i = 1;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values = vms.voucherTypeList();

		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		initiateMap2 = vmsmap.defaultMap();
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());

		initiateMap2.put("denominationName", initiateMap.get("denominationName"));
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());

		initiateMap2.put("shortName", initiateMap.get("shortName"));
		String mrp = UniqueChecker.UC_VOMS_MRP();
		String mrp2 = mrp;
		initiateMap.put("mrp", mrp);
		initiateMap2.put("mrp", mrp2);
		initiateMap.put("voucherType", values.get(i));
		initiateMap2.put("voucherType", values.get(i));
		String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{values.get(i)});
		initiateMap.put("type", typeVoucher);
		initiateMap2.put("type", typeVoucher);
		String denomination = mrp + ".0";
		String denomination2 = mrp2 + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap2.put("denomination", denomination2);
		initiateMap.put("categoryName", "NWADM");
		initiateMap2.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		String activeProfile2 = activeProfile;
		initiateMap.put("activeProfile", activeProfile);
		initiateMap2.put("activeProfile", activeProfile2);
		initiateMap.put("scenario", "");
		initiateMap2.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		String productID2 = activeProfile2 + "(" + denomination2 + ")";
		initiateMap.put("productID", productID);
		initiateMap2.put("productID", productID2);
		initiateMap = vms.addVoucherProfileNegativeNA(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.addActiveVoucherProfile(initiateMap, "");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.addActiveVoucherProfile(initiateMap, "");
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		initiateMap = vms.vomsOrderInititateNegativeNA(initiateMap);
		initiateMap = vms.voucherGenerationApproval1NegativeNA(initiateMap);
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}
		else {
			Assertion.assertSkip("Not a valid case for this scenario");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

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
	@Test
	@TestManager(TestKey = "PRETUPS-767") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_07_vomsGenerationApproval2() throws Exception {
		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA65");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		HashMap<String, String> initiateMap2 = new HashMap<String, String>();

		currentNode = test
				.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType")));
		currentNode.assignCategory("SIT");
		
		if(DBHandler.AccessHandler.isMultipleNetworkEnabled()) {
		int size = 0;
		int i = 1;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values = vms.voucherTypeList();

		VMSMap vmsmap = new VMSMap();
		initiateMap = vmsmap.defaultMap();
		initiateMap2 = vmsmap.defaultMap();

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());

		initiateMap2.put("denominationName", initiateMap.get("denominationName"));
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());

		initiateMap2.put("shortName", initiateMap.get("shortName"));
		String mrp = UniqueChecker.UC_VOMS_MRP();
		String mrp2 = mrp;
		initiateMap.put("mrp", mrp);
		initiateMap2.put("mrp", mrp2);
		initiateMap.put("voucherType", values.get(i));
		initiateMap2.put("voucherType", values.get(i));
		String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{values.get(i)});
		initiateMap.put("type", typeVoucher);
		initiateMap2.put("type", typeVoucher);
		String denomination = mrp + ".0";
		String denomination2 = mrp2 + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap2.put("denomination", denomination2);
		initiateMap.put("categoryName", "NWADM");
		initiateMap2.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		String activeProfile2 = activeProfile;
		initiateMap.put("activeProfile", activeProfile);
		initiateMap2.put("activeProfile", activeProfile2);
		initiateMap.put("scenario", "");
		initiateMap2.put("scenario", "");
		String productID = activeProfile + "(" + denomination + ")";
		String productID2 = activeProfile2 + "(" + denomination2 + ")";
		initiateMap.put("productID", productID);
		initiateMap2.put("productID", productID2);
		initiateMap = vms.addVoucherProfileNegativeNA(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {
			initiateMap = vms.addActiveVoucherProfile(initiateMap, "");
		}
		else {
			String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
			List al = Arrays.asList(type);
			if(al.contains(initiateMap.get("type"))) {
				initiateMap = vms.addActiveVoucherProfile(initiateMap, "");
			}
			else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		initiateMap = vms.vomsOrderInititateNegativeNA(initiateMap);
		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		initiateMap = vms.voucherGenerationApproval2NegativeNA(initiateMap2, "");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "N");
		}
		else

			Assertion.assertSkip("multiple network does not exist");
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-462") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_08_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA31");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();

		initiateMap = vmsMap.defaultMap();

		initiateMap.put("denominationName", "");
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("vmcategory.selectcategoryforvoms.label.denomname"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-463") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_09_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA32");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();

		initiateMap = vmsMap.defaultMap();

		initiateMap.put("shortName", "");
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("vmcategory.addsubcategoryforvoms.label.shortname"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-464") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_10_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA33");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();

		initiateMap = vmsMap.defaultMap();

		initiateMap.put("mrp", "");
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("vmcategory.modifydenomination.label.mrp"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-465") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_11_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA34");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME"))) {
			HashMap<String, String> initiateMap = new HashMap<String, String>();
			VMSMap vmsMap = new VMSMap();
	
			initiateMap = vmsMap.defaultMap();
	
			initiateMap.put("payableAmount", "");
			initiateMap.put("categoryName", "NWADM");
			initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
	
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.payamtreq");
	
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-466") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_12_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA35");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();

		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "NWADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp=UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap= vms.voucherDenomination(initiateMap, "");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.catnameexists");

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-467") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_13_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA36");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "NWADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp=UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		initiateMap= vms.voucherDenomination(initiateMap, "");
		initiateMap.put("denominationName", randomGeneration.randomAlphaNumeric(4));
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.catshortnameexists");

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-468") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_14_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA37");
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
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpexists");

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-469") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_15_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA38");
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
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber",
				MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-470") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_16_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA39");
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
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number",
				MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-471") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_17_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA40");
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
			initiateMap.put("categoryName", "NWADM");
			initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
	
			String message = MessagesDAO.prepareMessageByKey("vomsproduct.modifyproduct.err.msg.talktimeisnum",
					MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
	
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-472") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_18_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA41");
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
			initiateMap.put("categoryName", "NWADM");
			initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
	
			String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number",
					MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
	
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-473") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_19_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA42");
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
		initiateMap.put("mrp", randomGeneration.randomNumeric(1) + " " + randomGeneration.randomNumeric(1));
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber",
				MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-474") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_20_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA43");
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
			initiateMap.put("payableAmount", randomGeneration.randomNumeric(1) + " " + randomGeneration.randomNumeric(1));
			initiateMap.put("categoryName", "NWADM");
			initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
	
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber",
					MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-475") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_21_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA44");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("denominationName",
				randomGeneration.randomAlphabets(1) + "@" + randomGeneration.randomAlphaNumeric(3));
		initiateMap.put("shortName", randomGeneration.randomAlphaNumeric(4));
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-476") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_22_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA45");
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
		initiateMap.put("mrp", randomGeneration.randomNumeric(3) + "." + randomGeneration.randomNumeric(4));
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-477") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_23_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA46");
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
		initiateMap.put("mrp", randomGeneration.randomNumeric(3) + "." + randomGeneration.randomNumeric(2));
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-478") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_24_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA47");
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
		initiateMap.put("mrp", "-" + randomGeneration.randomNumeric(3) + "." + randomGeneration.randomNumeric(2));
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("btsl.positive.error.number",
				MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-479") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_25_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA48");
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
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String payableAmount = mrp + 1;
		initiateMap.put("payableAmount", payableAmount);
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-480") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_26_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA49");
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
		initiateMap.put("mrp", randomGeneration.randomNumeric(1) + "@!");
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber",
				MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-481") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_27_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA50");
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
		initiateMap.put("mrp", randomGeneration.randomNumeric(1) + "@" + randomGeneration.randomAlphabets(1));
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber",
				MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.mrp"));

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-482") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_28_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA51");
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
			initiateMap.put("categoryName", "NWADM");
			initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
	
			String message = MessagesDAO.prepareMessageByKey("vmcategory.modifycategoryforvoms.err.msg.mrpnotzero");
	
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-483") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_29_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA52");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP_2Digits());
		initiateMap.put("payableAmount", randomGeneration.randomNumeric(3) + "." + randomGeneration.randomNumeric(4));
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");

		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-484") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_30_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA53");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME_2NumericDigits());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME_2NumericDigits());
		initiateMap.put("mrp", UniqueChecker.UC_VOMS_MRP());
		initiateMap.put("payableAmount", randomGeneration.randomNumeric(3) + "." + randomGeneration.randomNumeric(2));
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.succ.msg.cataddsucc");
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-485") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_31_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA54");
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
			initiateMap.put("payableAmount", randomGeneration.randomNumeric(1) + "@!" + randomGeneration.randomNumeric(1));
			initiateMap.put("categoryName", "NWADM");
			initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
	
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber",
					MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
	
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-486") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_32_VoucherDenomination() throws InterruptedException {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA55");
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
			initiateMap.put("payableAmount", randomGeneration.randomAlphaNumeric(4));
			initiateMap.put("categoryName", "NWADM");
			initiateMap = vms.voucherDenominationNegativeNA(initiateMap, "");
	
			String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpisnumber",
					MessagesDAO.getLabelByKey("vmcategory.viewsubcategoryforvoms.label.payamount"));
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else
			Assertion.assertSkip(payableAmountAbsent);
		
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-487") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_33_VoucherProfile() throws InterruptedException {

		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA56");
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
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.addVoucherProfileNegativeNA(initiateMap);

		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("vomsproduct.viewactiveprofile.label.profilename"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-488") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_34_VoucherProfile() throws InterruptedException {

		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA57");
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
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.addVoucherProfileNegativeNA(initiateMap);

		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.profileshortname"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-489") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_35_VoucherProfile() throws InterruptedException {

		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA58");
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
		initiateMap.put("minQuantity", "");
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.addVoucherProfileNegativeNA(initiateMap);

		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("vomsproduct.modifyproduct.label.minqty"));

		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-490") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_36_VoucherProfile() throws InterruptedException {

		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA59");
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
		initiateMap.put("categoryName", "NWADM");
		initiateMap = vms.addVoucherProfileNegativeNA(initiateMap);

		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("vomsproduct.addnewproduct.label.maxqty"));

		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-491") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_37_VoucherProfile() throws InterruptedException {

		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNA60");
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
		initiateMap.put("categoryName", "NWADM");
		String vomsProfileTalktime = DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_PROF_TALKTIME_MANDATORY);
		if(vomsProfileTalktime.equalsIgnoreCase("true")) {
		initiateMap = vms.addVoucherProfileNegativeNA(initiateMap);

		String message = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("vomsproduct.confirmaddactiveproduct.label.talktime"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else {
			Assertion.assertSkip("VOMS_PROF_TALKTIME_MANDATORY system preference is false hence this case is skip");
		}
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

}
