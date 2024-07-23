package com.testscripts.sit;

import java.sql.SQLException;
//Clear VOMS_BUNDLE sheet in DataProvider Excel file before running this class
//Bundles are generated according to the number of voucher profiles available in denom_profile sheet in DataProvider Excel file
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.VMS;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.databuilder.BuilderLogic;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
//jj
@ModuleManager(name = Module.SIT_VMS_BUNDLE)
public class SIT_VMS_BUNDLE extends BaseTest {

	static String moduleCode;
	final String skipDetail = "VMS Bundle Management not enabled"; 

	@TestManager(TestKey = "PRETUPS-421") 
		@Test(dataProvider="VOMSDENOMINATIONS")// TO BE UNCOMMENTED WITH JIRA TEST ID
		public void A0001_VoucherDenomination(HashMap<String, String> initiateMap, int dataCounter) {
			
			final String methodName = "Test_VoucherDenomination";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERDENOM");
			moduleCode = CaseMaster1.getModuleCode();
			VMS vms = new VMS(driver);                        
			
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
			currentNode.assignCategory("SIT");
			
			if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
				initiateMap = vms.voucherDenomination(initiateMap, "");
				if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
					vms.writeDenomination(initiateMap, dataCounter);
					
					//Message Validation Here
				} else 
					Assertion.assertFail("Add Voucher Denomination Failure with Following Message: " + initiateMap.get("Message"));
			}else {
				Assertion.assertSkip(skipDetail);
			}
			
			Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		@TestManager(TestKey = "PRETUPS-423") 
		@Test(dataProvider="VOMSDENOMPROFILES")// TO BE UNCOMMENTED WITH JIRA TEST ID
		public void A0002_VoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {
			
			final String methodName = "Test_VoucherProfile";
	        Log.startTestCase(methodName);
	        
			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERPROF");
			moduleCode = CaseMaster1.getModuleCode();
			VMS vms = new VMS(driver);
			
			
			currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"), initiateMap.get("service"), initiateMap.get("subService"),initiateMap.get("categoryName")));
			currentNode.assignCategory("SIT");
			if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
				initiateMap = vms.addVoucherProfile(initiateMap, "");
				
				if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
					vms.writeProfile(initiateMap, dataCounter);
					
					//Message Validation Here
				} else 
					Assertion.assertFail("Add Voucher Profile Failure with Following Message: " + initiateMap.get("Message"));
			}else {
				Assertion.assertSkip(skipDetail);
			}	
			Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
	@TestManager(TestKey = "PRETUPS-3001")
	@Test
	public void A001_AddVB() {
		final String methodName = "Bundle_Add";
		Log.startTestCase(methodName);
		
		HashMap<String,String> initiateMap = new HashMap<String,String>();

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSBUNDLEADD");
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),"login"));
		currentNode.assignCategory("VMS");
		if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap.put("rowCountBundle",_masterVO.getClientDetail("BUNDLE_CREATION_ROWS"));
			initiateMap = vms.addVoucherBundle();
			
			if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
				Assertion.assertPass(initiateMap.get("Message"));	
				//Message Validation Here
			} else 
				Assertion.assertFail("Add Voucher Bundle Failure with Following Message: " + initiateMap.get("Message"));
		}else {
			Assertion.assertSkip(skipDetail);
		}	
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-3101")
	@Test(dataProvider = "VOMSBUNDLES")
	public void A002_ViewVB(HashMap<String, String> initiateMap, int dataCounter) throws SQLException {
		final String methodName = "Bundle_View";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSBUNDLEVIEW");
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),initiateMap.get("voucherBundleName")));
		currentNode.assignCategory("VMS");
		if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap = vms.viewVoucherBundle(initiateMap);
			
			if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
				Assertion.assertPass(initiateMap.get("Message"));	
				//Message Validation Here
			} else 
				Assertion.assertFail("Add Voucher Bundle Failure with Following Message: " + initiateMap.get("Message"));
		}else {
			Assertion.assertSkip(skipDetail);
		}	
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

	@TestManager(TestKey = "PRETUPS-3201")
	@Test(dataProvider = "VOMSBUNDLES")
	public void A003_ModifyVB(HashMap<String, String> initiateMap, int dataCounter) {
		final String methodName = "Bundle_Modify";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSBUNDLEMODIFY");
		VMS vms = new VMS(driver);
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),initiateMap.get("voucherBundleName")));
		currentNode.assignCategory("VMS");
		if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap = vms.modifyVoucherBundle(initiateMap);
			
			if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
				Assertion.assertPass(initiateMap.get("Message"));	
				//Message Validation Here
			} else 
				Assertion.assertFail("Modify Voucher Bundle Failure with Following Message: " + initiateMap.get("Message"));
		}else {
			Assertion.assertSkip(skipDetail);
		}	
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-3051")
	@Test()
	public void A011_AddVBNEG1() {
		final String methodName = "Bundle_Add_Neg";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSBUNDLEADDNEG1");
		VMS vms = new VMS(driver);
		HashMap<String,String> initiateMap = new HashMap<String,String>();
		if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap.put("voucherBundleName", "");
			initiateMap.put("voucherBundlePrefix", UniqueChecker.UC_VBPREFIX());
			
			
			currentNode = test.createNode(CaseMaster1.getExtentCase());
			currentNode.assignCategory("VMS");
			initiateMap = vms.addVoucherBundleNEGATIVE1(initiateMap);
		}else {
			Assertion.assertSkip(skipDetail);
		}	

		String message = MessagesDAO.prepareMessageByKey("voucherbundle.addvoucherbundle.err.msg.bundlenamenotnull");
//		String message = "Unique bundle name required";
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-3052")
	@Test(dataProvider = "VOMSBUNDLES")
	public void A012_AddVBNEG2(HashMap<String, String> initiateMap, int dataCounter) {
		final String methodName = "Bundle_Add_Neg";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSBUNDLEADDNEG2");
		VMS vms = new VMS(driver);
			
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),initiateMap.get("voucherBundleName")));
		currentNode.assignCategory("VMS");
		if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap = vms.addVoucherBundleNEGATIVE2(initiateMap);
			
			String message = MessagesDAO.prepareMessageByKey("voucherbundle.addvoucherbundle.existingdetails");
	//		String message = "Bundle name or prefix already existing";
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else {
			Assertion.assertSkip(skipDetail);
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-3053")
	@Test()
	public void A013_AddVBNEG3() {
		final String methodName = "Bundle_Add_Neg";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSBUNDLEADDNEG3");
		VMS vms = new VMS(driver);
		HashMap<String,String> initiateMap = new HashMap<String,String>();
		initiateMap.put("voucherBundleName", "");
		initiateMap.put("voucherBundlePrefix", "abcd");
		
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("VMS");
		if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap = vms.addVoucherBundleNEGATIVE1(initiateMap);
		}else {
			Assertion.assertSkip(skipDetail);
		}

		String message = MessagesDAO.prepareMessageByKey("voucherbundle.addvoucherbundle.err.msg.bundlenamenotnull");
		Assertion.assertEquals(initiateMap.get("Message"), message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-3054")
	@Test()
	public void A014_AddVBNEG4() {
		final String methodName = "Bundle_Add_Neg";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSBUNDLEADDNEG4");
		VMS vms = new VMS(driver);
		HashMap<String,String> initiateMap = new HashMap<String,String>();
		initiateMap.put("voucherBundleName", "");
		initiateMap.put("voucherBundlePrefix", UniqueChecker.UC_VBPREFIX());
		
		
		currentNode = test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("VMS");
		if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap = vms.addVoucherBundleNEGATIVE1(initiateMap);
			
	
			String message = MessagesDAO.prepareMessageByKey("voucherbundle.addvoucherbundle.err.msg.bundlenamenotnull");
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-3055")
	@Test(dataProvider = "VOMSBUNDLES")
	public void A015_AddVBNEG5(HashMap<String, String> initiateMap, int dataCounter) {
		final String methodName = "Bundle_Add_Neg";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSBUNDLEADDNEG5");
		VMS vms = new VMS(driver);
			
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),initiateMap.get("voucherBundleName")));
		currentNode.assignCategory("VMS");
		if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap.put("quantity","5.5");
			initiateMap = vms.addVoucherBundleNEGATIVE3(initiateMap);
			
			String message = "Quantity must be a positive integer";
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@TestManager(TestKey = "PRETUPS-3056")
	@Test(dataProvider = "VOMSBUNDLES")
	public void A016_AddVBNEG6(HashMap<String, String> initiateMap, int dataCounter) {
		final String methodName = "Bundle_Add_Neg";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSBUNDLEADDNEG6");
		VMS vms = new VMS(driver);
			
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),initiateMap.get("voucherBundleName")));
		currentNode.assignCategory("VMS");
		if(_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap.put("quantity", "-10");
			initiateMap = vms.addVoucherBundleNEGATIVE3(initiateMap);
			
			String message = "Quantity must be a positive integer";
			Assertion.assertEquals(initiateMap.get("Message"), message);
		}else {
			Assertion.assertSkip(skipDetail);
		}
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
//			VomsData.put("subService", String.valueOf(VOMSData[j][3]));
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
		VOMSDenomSheetBuilder.prepareVOMSProfileSheet(dataObj);
		
		return dataObj;
	}
	@DataProvider(name="VOMSBUNDLES")
	public Object[][] VOMSBUNDLES() {
		
		BuilderLogic VBSheet = new BuilderLogic();
		VBSheet.prepareVOMSBundleSheet();
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] dataObj = new Object[rowCount][2];
		int objCounter = 0;
		for (int i = 1; i <= rowCount; i++) {
			HashMap<String, String> VomsData = new HashMap<String, String>();
			VomsData.put("voucherBundleName", ExcelUtility.getCellData(0, ExcelI.VOMS_BUNDLE_NAME, i));
			VomsData.put("voucherBundlePrefix", ExcelUtility.getCellData(0, ExcelI.VOMS_BUNDLE_PREFIX, i));
	
			dataObj[objCounter][0] = VomsData.clone();
			dataObj[objCounter][1] = ++objCounter;
		}
		
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
