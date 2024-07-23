package com.testscripts.sit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.Features.O2CTransfer;
import com.Features.VMS;
import com.Features.mapclasses.OperatorToChannelMap;
import com.Features.mapclasses.VMSMap;
import com.apicontrollers.extgw.VMS.EXTGW_VOUCHEREXPIRYEXTENSION;
import com.apicontrollers.extgw.VMS.EXTGW_VOUCHEREXPIRYEXT_DP;
import com.apicontrollers.extgw.VMS.EXTGW_VoucherConsumption_API;
import com.apicontrollers.extgw.VMS.EXTGW_VoucherConsumption_DP;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Decrypt;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_Voucher_Expiry_Extension)
public class SIT_VoucherExpiryExtension extends BaseTest {
	String MasterSheetPath;
	static String directO2CPreference;
	static String moduleCode;
	
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

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA314_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXPGE");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		int size=0;
		int i=1;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{initiateMap.get("voucherType")});
		initiateMap.put("type", typeVoucher);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
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
		}
		
		//initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();
		initiateMap = vms.voucherExpiryExtension(initiateMap);
		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));
		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(fromSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("newExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA315_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXPPE");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		int size=0;
		int i=1;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{initiateMap.get("voucherType")});
		initiateMap.put("type", typeVoucher);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		
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
		}
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();
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
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();
		initiateMap = vms.voucherExpiryExtension(initiateMap);
		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "PE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));
		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(fromSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("newExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA316_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXPWH");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);
		int size=0;
		int i=0;
		List<String> values = new ArrayList<String>();
		size = vms.voucherTypeCount();
		values=vms.voucherTypeList();
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		String typeVoucher=ExtentI.getValueofCorrespondingColumns(ExcelI.VOMS_DENOM_PROFILE, ExcelI.VOMS_TYPE, new String[]{ExcelI.VOMS_VOUCHER_TYPE}, new String[]{initiateMap.get("voucherType")});
		initiateMap.put("type", typeVoucher);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
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
		}
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();
		initiateMap = vms.createBatchForVoucherDownload(initiateMap, "");
		initiateMap = vms.changeOtherStatus(initiateMap, "");
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();
		initiateMap = vms.voucherExpiryExtension(initiateMap);
		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "WH";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));
		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(fromSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("newExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA317_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXPEN");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();
		initiateMap = vms.createBatchForVoucherDownload(initiateMap, "");
		initiateMap = vms.changeOtherStatus(initiateMap, "");

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
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "WH";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productIDDB, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productIDDB, status);
		initiateMap.put("fromSerialNumber", fromSerialNumber);
		initiateMap.put("toSerialNumber", toSerialNumber);
		transferMap = o2CTransfer.initiateVoucherO2CTransfer(transferMap, initiateMap);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
			o2CTransfer.performingLevel1ApprovalVoucher(transferMap, initiateMap, "");
		long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
		if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				&& netPayableAmount > firstApprov)
			o2CTransfer.performingLevel2ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"),
					transferMap.get("NetPayableAmount"), transferMap, "");
		if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				&& netPayableAmount > secondApprov)
			o2CTransfer.performingLevel3ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"),
					transferMap.get("NetPayableAmount"), "");
		initiateMap = vms.checkO2CStatus(initiateMap);
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		status = "EN";
		fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(fromSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("newExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA318_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXPCU");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();
		initiateMap = vms.createBatchForVoucherDownload(initiateMap, "");
		initiateMap = vms.changeOtherStatus(initiateMap, "");

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
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "WH";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productIDDB, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productIDDB, status);
		initiateMap.put("fromSerialNumber", fromSerialNumber);
		initiateMap.put("toSerialNumber", toSerialNumber);
		transferMap = o2CTransfer.initiateVoucherO2CTransfer(transferMap, initiateMap);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
			o2CTransfer.performingLevel1ApprovalVoucher(transferMap, initiateMap, "");
		long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
		if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				&& netPayableAmount > firstApprov)
			o2CTransfer.performingLevel2ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"),
					transferMap.get("NetPayableAmount"), transferMap, "");
		if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				&& netPayableAmount > secondApprov)
			o2CTransfer.performingLevel3ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"),
					transferMap.get("NetPayableAmount"), "");

		initiateMap = vms.checkO2CStatus(initiateMap);
		status = "EN";

		HashMap<String, String> apiData = EXTGW_VoucherConsumption_DP.getAPIdata();
		EXTGW_VoucherConsumption_API voucherConsumptionAPI = new EXTGW_VoucherConsumption_API();
		apiData.put(voucherConsumptionAPI.LOGINID, "");
		apiData.put(voucherConsumptionAPI.PASSWORD, "");
		apiData.put(voucherConsumptionAPI.AMOUNT, "100");
		apiData.put(voucherConsumptionAPI.EXTCODE, "");
		apiData.put(voucherConsumptionAPI.MSISDN2, "");
		apiData.put(voucherConsumptionAPI.PIN, "");
		apiData.put(voucherConsumptionAPI.SELECTOR, "");
		productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
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
		status = "CU";

		HashMap<String, String> apiData2 = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));

		fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData2.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData2.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData2.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData2.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData2.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));

		API = voucherExpiryExtAPI.prepareAPI(apiData2);
		APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(fromSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("oldExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA319_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXPS");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();
		initiateMap = vms.createBatchForVoucherDownload(initiateMap, "");
		initiateMap = vms.changeOtherStatus(initiateMap, "");
		initiateMap.put("voucherStatus", PretupsI.SUSPENDED);
		initiateMap = vms.changeOtherStatus(initiateMap, "");
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "S";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(fromSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("newExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA320_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXPOH");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");

		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();
		initiateMap = vms.createBatchForVoucherDownload(initiateMap, "");
		initiateMap = vms.changeOtherStatus(initiateMap, "");

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
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "WH";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productIDDB, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productIDDB, status);
		initiateMap.put("fromSerialNumber", fromSerialNumber);
		initiateMap.put("toSerialNumber", toSerialNumber);
		transferMap = o2CTransfer.initiateVoucherO2CTransfer(transferMap, initiateMap);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
			o2CTransfer.performingLevel1ApprovalVoucher(transferMap, initiateMap, "");
		long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
		if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				&& netPayableAmount > firstApprov)
			o2CTransfer.performingLevel2ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"),
					transferMap.get("NetPayableAmount"), transferMap, "");
		if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				&& netPayableAmount > secondApprov)
			o2CTransfer.performingLevel3ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"),
					transferMap.get("NetPayableAmount"), "");

		initiateMap = vms.checkO2CStatus(initiateMap);

		initiateMap.put("voucherStatus", PretupsI.ONHOLD);
		initiateMap = vms.changeOtherStatus(initiateMap, "");
		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		status = "OH";
		fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(fromSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("oldExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA321_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXDIS");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");

		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();
		initiateMap = vms.createBatchForVoucherDownload(initiateMap, "");
		initiateMap = vms.changeOtherStatus(initiateMap, "");

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
		String productIDDB = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "WH";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productIDDB, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productIDDB, status);
		initiateMap.put("fromSerialNumber", fromSerialNumber);
		initiateMap.put("toSerialNumber", toSerialNumber);
		transferMap = o2CTransfer.initiateVoucherO2CTransfer(transferMap, initiateMap);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
			o2CTransfer.performingLevel1ApprovalVoucher(transferMap, initiateMap, "");
		long netPayableAmount = _parser.getSystemAmount(transferMap.get("NetPayableAmount"));
		if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				&& netPayableAmount > firstApprov)
			o2CTransfer.performingLevel2ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"),
					transferMap.get("NetPayableAmount"), transferMap, "");
		if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				&& netPayableAmount > secondApprov)
			o2CTransfer.performingLevel3ApprovalVoucher(transferMap.get("TO_MSISDN"), transferMap.get("TRANSACTION_ID"),
					transferMap.get("NetPayableAmount"), "");

		initiateMap = vms.checkO2CStatus(initiateMap);
		initiateMap.put("voucherStatus", PretupsI.DISABLED);

		initiateMap = vms.changeOtherStatus(initiateMap, "");

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		status = "DA";
		fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(fromSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("oldExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA322_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXP1");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, "");
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(toSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("oldExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA323_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXP2");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, "");
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(fromSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("oldExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA324_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXP3");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, "");
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(toSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("oldExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA325_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXP4");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, "");
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(toSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("newExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA326_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXP5");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, "");

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(toSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("oldExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA327_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXP6");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));
		apiData.put(voucherExpiryExtAPI.EXTNWCODE, "");

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(toSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("newExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA328_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXP7");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));
		apiData.put(voucherExpiryExtAPI.EXTREFNUM, "");

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);
		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(toSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("newExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA329_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXP8");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));
		apiData.put(voucherExpiryExtAPI.EXPIRY_CHANGE_REASON, "");

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(toSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("oldExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA330_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXP9");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));
		apiData.put(voucherExpiryExtAPI.LOGINID, "");

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(toSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("oldExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEZZZZZZA331_VoucherExpiryExtension() throws Exception {

		final String methodName = "Test_VoucherExpiryExtension";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHEREXPIRYEXP10");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		VMSMap vmsMap = new VMSMap();
		RandomGeneration randomGeneration = new RandomGeneration();
		initiateMap = vmsMap.defaultMap();
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp = UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile + "(" + denomination + ")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap = vms.voucherDenominationNegative(initiateMap, "");
		initiateMap = vms.addVoucherProfileNegative(initiateMap);
		initiateMap = vms.addActiveVoucherProfileNegative(initiateMap);
		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");
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
		
		if(maxApprovalLevel == 0)
		{
    		Log.info("Voucher Initiated at initiate level");
		}
    	if(maxApprovalLevel == 1)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
		}
    	else if(maxApprovalLevel == 2)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		}
    	else if(maxApprovalLevel == 3)
		{
    		initiateMap = vms.voucherGenerationApproval1(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval2(initiateMap, "");
    		initiateMap = vms.voucherGenerationApproval3(initiateMap, "");
    		}
		vms.voucherGenerationScriptExecution();

		HashMap<String, String> apiData = EXTGW_VOUCHEREXPIRYEXT_DP.getAPIdata();
		EXTGW_VOUCHEREXPIRYEXTENSION voucherExpiryExtAPI = new EXTGW_VOUCHEREXPIRYEXTENSION();

		initiateMap = vms.voucherExpiryExtension(initiateMap);

		String productID2 = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status = "GE";
		String fromSerialNumber = DBHandler.AccessHandler.getMinSerialNumber(productID2, status);
		String toSerialNumber = DBHandler.AccessHandler.getMaxSerialNumber(productID2, status);
		apiData.put(voucherExpiryExtAPI.FROM_SERIALNO, fromSerialNumber);
		apiData.put(voucherExpiryExtAPI.TO_SERIALNO, toSerialNumber);
		apiData.put(voucherExpiryExtAPI.VOUCHER_TYPE, initiateMap.get("voucherType"));
		apiData.put(voucherExpiryExtAPI.DATE, initiateMap.get("currDate"));
		apiData.put(voucherExpiryExtAPI.NEW_EXPIRY_DATE, initiateMap.get("newExpiryDate"));
		apiData.put(voucherExpiryExtAPI.PASSWORD, "");

		String API = voucherExpiryExtAPI.prepareAPI(apiData);
		String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.VomsReceiver, API);

		String newExpiryDate = DBHandler.AccessHandler.getExpiryDate(toSerialNumber);
		// input format: dd/MM/yy
		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
		// output format: yyyy-MM-dd
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(parser.parse(initiateMap.get("oldExpiryDate"))); // 2017-11-01
		Assertion.assertContainsEquals(date, newExpiryDate);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}

}
