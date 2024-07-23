package com.testscripts.sit;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.VMS;
import com.Features.mapclasses.OperatorToChannelMap;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.AutomationException;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_VMS_Negative)
public class SIT_VMS_Negative extends BaseTest {
	
	public static boolean testCaseCounter = false;
	String MasterSheetPath;
	static String directO2CPreference;
	static String moduleCode;
	
	@TestManager(TestKey = "PRETUPS-837")
	@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEA_AddVoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		final String methodName="CASEA_AddVoucherProfile";Log.startTestCase(methodName);
		
	    CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG1");
		//moduleCode = CaseMaster1.getModuleCode();
		
		VMS vms = new VMS(driver);
		
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1));
		
		try {
			vms.validatingMRPBasedOnType(initiateMap, "physical");
			Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-838")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASEB_AddVoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		final String methodName="CASEB_AddVoucherProfile";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG2");
			//moduleCode = CaseMaster1.getModuleCode();
			
		VMS vms = new VMS(driver);
		
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1));
		
		try {
			vms.validatingMRPBasedOnType(initiateMap, "electronic");
			Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-839")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEC_ModifyVoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		final String methodName="CASEC_ModifyVoucherProfile";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG3");
		//moduleCode = CaseMaster1.getModuleCode();
			
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1));
		
		try {
			vms.modifyVoucherProfileValidatingMRPBasedOnType(initiateMap, "", "physical");
			Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-840")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASED_ModifyVoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
		final String methodName="CASED_ModifyVoucherProfile";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG4");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver); 
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1));
		
		try {
			vms.modifyVoucherProfileValidatingMRPBasedOnType(initiateMap, "", "electronic");
			Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-841")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEE_ViewVoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEE_ViewVoucherProfile";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG5");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
		
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1));
		initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1));
		
		try {
		initiateMap = vms.viewVoucherProfileValidatingMRPBasedOnType(initiateMap, "physical");
		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-842")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASEF_ViewVoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEF_ViewVoucherProfile";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG6");
			//moduleCode = CaseMaster1.getModuleCode();
			
		VMS vms = new VMS(driver);
		
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1));
		
		try {
			initiateMap = vms.viewVoucherProfileValidatingMRPBasedOnType(initiateMap, "electronic");
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
			} catch (AutomationException ex) {
				currentNode.pass(ex.toString());
			}
		
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-843")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEG_AddActiveVoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEG_AddActiveVoucherProfile";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG7");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1));
		
		try {
		initiateMap = vms.addActiveVoucherProfileValidatingMRPBasedOnType(initiateMap, "physical");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-1174")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASEH_AddActiveVoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEH_AddActiveVoucherProfile";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG8");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);

		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1));
		
		try {
			initiateMap = vms.addActiveVoucherProfileValidatingMRPBasedOnType(initiateMap, "electronic");
			Assertion.assertFail("MRP getting  displayed.");
			} catch (AutomationException ex) {
				currentNode.pass(ex.toString());
			}
		
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-844")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEI_VoucherOrderInitiate(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEI_VoucherOrderInitiate";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG9");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		
		try {
		initiateMap = vms.voucherGenerationInitiateValidatingMRPBasedOnType(initiateMap, "physical");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-845")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASEJ_VoucherOrderInitiate(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEJ_VoucherOrderInitiate";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG10");
			//moduleCode = CaseMaster1.getModuleCode();
			
		VMS vms = new VMS(driver);
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		
		try {
		initiateMap = vms.voucherGenerationInitiateValidatingMRPBasedOnType(initiateMap, "electronic");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-846")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEK_VoucherOrderApproval1(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEK_VoucherOrderApproval1";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG11");
			//moduleCode = CaseMaster1.getModuleCode();
			
		VMS vms = new VMS(driver);
		
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		
		try {
		initiateMap = vms.voucherGenerationApproval1ValidatingMRPBasedOnType(initiateMap, "physical");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-847")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASEL_VoucherOrderApproval1(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEL_VoucherOrderApproval1";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG12");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
		
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		
		try {
		initiateMap = vms.voucherGenerationApproval1ValidatingMRPBasedOnType(initiateMap, "electronic");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-848")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEM_VoucherOrderApproval2(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEM_VoucherOrderApproval2";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG13");
			//moduleCode = CaseMaster1.getModuleCode();
			
		VMS vms = new VMS(driver);
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		
		try {
		initiateMap = vms.voucherGenerationApproval2ValidatingMRPBasedOnType(initiateMap, "physical");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-849")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASEN_VoucherOrderApproval2(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEN_VoucherOrderApproval2";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG14");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		
		try {
		initiateMap = vms.voucherGenerationApproval2ValidatingMRPBasedOnType(initiateMap, "electronic");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-850")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEO_VoucherOrderApproval3(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEO_VoucherOrderApproval3";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG15");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
		
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		
		try {
		initiateMap = vms.voucherGenerationApproval3ValidatingMRPBasedOnType(initiateMap, "physical");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-851")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASEP_VoucherOrderApproval3(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEP_VoucherOrderApproval3";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG16");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);

		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		
		try {
		initiateMap = vms.voucherGenerationApproval3ValidatingMRPBasedOnType(initiateMap, "electronic");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-852")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEQ_ViewVoucherDenomination(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEQ_ViewVoucherDenomination";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG17");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1));
		
		initiateMap = vms.viewVoucherDenominationValidatingMRPBasedOnType(initiateMap, "physical");
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Assertion.assertPass("Mrp not found, hence test case succesful.");
			//Message Validation Here
		} else
			Assertion.assertFail("Mrp found, hence test case unsuccesful.");
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-853")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASER_ViewVoucherDenomination(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASER_ViewVoucherDenomination";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG18");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1));
		
		initiateMap = vms.viewVoucherDenominationValidatingMRPBasedOnType(initiateMap, "electronic");
		if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
			Assertion.assertPass("Mrp not found, hence test case succesful.");
			//Message Validation Here
		} else
			Assertion.assertFail("Mrp found, hence test case unsuccesful.");
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
		
	} 
	
	@TestManager(TestKey = "PRETUPS-855")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASES_ModifyVoucherDenomination(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASES_ModifyVoucherDenomination";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG19");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		initiateMap.put("denominationName", ExcelUtility.getCellData(0, ExcelI.VOMS_DENOMINATION_NAME, 1));
		
		try {
		initiateMap = vms.modifyVoucherDenominationValidatingMRPBasedOnType(initiateMap, "physical");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.getMessage());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-856")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASET_ModifyVoucherDenomination(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASET_ModifyVoucherDenomination";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG20");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
		
	
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		initiateMap.put("denominationName", ExcelUtility.getCellData(0, ExcelI.VOMS_DENOMINATION_NAME, 1));
		
		try {
		initiateMap = vms.modifyVoucherDenominationValidatingMRPBasedOnType(initiateMap, "electronic");
		Assertion.assertFail("Denomination getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.getMessage());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-857")
	@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEU_CreateBatch(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEU_CreateBatch";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG21");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		//String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1);
		
		try {
		initiateMap = vms.createBatchForVoucherDownloadValidatingMRPBasedOnType(initiateMap, "physical");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-858")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASEV_CreateBatch(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEV_CreateBatch";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG22");
			//moduleCode = CaseMaster1.getModuleCode();
			
		VMS vms = new VMS(driver);
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, 1);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		//String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1);
		
		try {
		initiateMap = vms.createBatchForVoucherDownloadValidatingMRPBasedOnType(initiateMap, "electronic");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		}
	
	/*@TestManager(TestKey = "PRETUPS-858")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEW_VomsDownload(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEW_VomsDownload";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG23");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
		
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT]VMS_NEGATIVE");
			testCaseCounter = true;
		} 
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1));
		
		try {
		initiateMap = vms.vomsVoucherDownloadValidatingMRPBasedOnType(initiateMap, "physical");
		Log.failNode("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	

	@TestManager(TestKey = "PRETUPS-859")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASEX_VomsDownload(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEX_VomsDownload";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG24");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
		
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT]VMS_NEGATIVE");
			testCaseCounter = true;
		} 
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1));
		
		try {
		initiateMap = vms.vomsVoucherDownloadValidatingMRPBasedOnType(initiateMap, "electronic");
		Log.failNode("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.toString());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	*/
	@TestManager(TestKey = "PRETUPS-859")@Test(dataProvider="PHYSICALVOMSDENOMPROFILES")
	public void CASEY_VoucherO2C(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEY_VoucherO2C";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG25");
			//moduleCode = CaseMaster1.getModuleCode();
			
		VMS vms = new VMS(driver);
		
	
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1));
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP,1));
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap.put("TO_STOCK_TYPE", PretupsI.O2C_VOUCHER_TYPE_LOOKUP);
		try {
		initiateMap = vms.initiateVoucherO2CTransfer(transferMap,initiateMap, "physical");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.getMessage());
		}
		
		Assertion.completeAssertions();Log.endTestCase(methodName);
		
	} 
	
	@TestManager(TestKey = "PRETUPS-861")@Test(dataProvider="ELECTRONICVOMSDENOMPROFILES")
	public void CASEZ_VoucherO2C(HashMap<String, String> initiateMap, int dataCounter) throws Exception {
		final String methodName="CASEZ_VoucherO2C";Log.startTestCase(methodName);
		
		  CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSNEG26");
			//moduleCode = CaseMaster1.getModuleCode();
			
		
		VMS vms = new VMS(driver);
		
		
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("SIT");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		initiateMap.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, 1));
		initiateMap.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP,1));
		
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap.put("TO_STOCK_TYPE", PretupsI.O2C_VOUCHER_TYPE_LOOKUP);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if(BTSLUtil.isNullString(value)) {		
		
		try {
		initiateMap = vms.initiateVoucherO2CTransfer(transferMap,initiateMap, "electronic");
		Assertion.assertFail("MRP getting  displayed.");
		} catch (AutomationException ex) {
			currentNode.pass(ex.getMessage());
		}
		}
		else {
			Assertion.assertSkip("Not a valid senario for the Pretups Version");
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
	@DataProvider(name="PHYSICALVOMSDENOMPROFILES")
	public Object[][] PhysicalVOMSDenominationProfilesDP() {
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] dataObj = new Object[rowCount][2];
		int objCounter = 0;
		
		for (int i = 1; i <= rowCount; i++) {
			HashMap<String, String> VomsData = new HashMap<String, String>();
			VomsData.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
			VomsData.put("service", ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i));
			VomsData.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
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
	
	@DataProvider(name="ELECTRONICVOMSDENOMPROFILES")
	public Object[][] ElectronicVOMSDenominationProfilesDP() {
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] dataObj = new Object[rowCount][2];
		int objCounter = 0;
		
		for (int i = 1; i <= rowCount; i++) {
			HashMap<String, String> VomsData = new HashMap<String, String>();
			VomsData.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
			VomsData.put("service", ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i));
			VomsData.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
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
}
