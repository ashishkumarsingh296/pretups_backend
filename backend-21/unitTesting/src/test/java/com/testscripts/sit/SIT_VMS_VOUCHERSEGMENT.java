package com.testscripts.sit;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.VMS;
import com.Features.mapclasses.VMSMap;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_VMS_NA)
public class SIT_VMS_VOUCHERSEGMENT extends BaseTest {
	String MasterSheetPath;
	static String directO2CPreference;
	static String moduleCode;
	private static String NetworkADM_Name = null;
	private static String SuperADM_Name = null;
	private static int NetworkAdminDataSheetRowNum = 0;
	
	@Test
	@TestManager(TestKey = "PRETUPS-462") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_01_VoucherDenomination() throws InterruptedException {

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

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("categoryName", "NWADM");
		initiateMap.put("segment", "National");
		initiateMap = vms.voucherDenominationNegativeNASegment(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpexists");
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-462") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_02_VoucherDenomination() throws InterruptedException {

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

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("segment", "National");
		initiateMap = vms.voucherDenominationNegativeNASegment(initiateMap, "");

		String message = MessagesDAO.prepareMessageByKey("vmcategory.addsubcategoryforvoms.err.msg.mrpexists");
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	@Test
	@TestManager(TestKey = "PRETUPS-462") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_03_VoucherDenomination() throws InterruptedException {

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

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("segment", "National");
		if (vms.getNationVoucher() == 1) {
		initiateMap = vms.voucherDenominationNegativeNAwithoutSegment(initiateMap, "");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("vmcategory.addsubcategoryforvoms.label.segment"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else {
			Assertion.assertSkip("Not Valid Scenario");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-462") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_04_VoucherDenomination() throws InterruptedException {

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

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("segment", "National");
		initiateMap.put("scenario", "");
		if (vms.getNationVoucher() == 1) {
		initiateMap = vms.addVoucherProfileNegativeNASegment(initiateMap);
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("vmcategory.addsubcategoryforvoms.label.segment"));
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else {
			Assertion.assertSkip("Not Valid Scenario");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-462") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void Test_05_VoucherDenomination() throws InterruptedException {

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

		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		initiateMap.put("categoryName", "SUADM");
		initiateMap.put("segment", "National");
		initiateMap.put("scenario", "");
		if (vms.getNationVoucher() == 1) {
			initiateMap = vms.voucherGenerationNegativeNASegment(initiateMap);
			String message = MessagesDAO.prepareMessageByKey("vomsproduct.segment.blank");
		Assertion.assertEquals(initiateMap.get("Message"), message);
		}
		else {
			Assertion.assertSkip("Not Valid Scenario");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
