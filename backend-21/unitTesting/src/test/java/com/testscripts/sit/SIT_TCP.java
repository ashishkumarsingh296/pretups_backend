package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.Map_TCPValues;
import com.Features.TransferControlProfile;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_TCP)
public class SIT_TCP extends BaseTest {


	static boolean TestCaseCounter = false;
	String TCPName;
	String profile_ID;
	String assignCategory="SIT";
	static String moduleCode;
	HashMap<String, String> dataMap;



	//Map_TCPValues Map_TCPValues = new Map_TCPValues(driver);

	Map<String, String> TCP_CatLevel_Map;
	@DataProvider(name="categoryData")
	public Object[][] TestDataFeed_SIT() throws IOException{
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		Object [][] categoryData=new Object[1][3];
		categoryData[0][0]=1;
		categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		return categoryData;
	}


	// SIT Test Case: Blank Minimum Residual Balance
	@TestManager(TestKey = "PRETUPS-1010") 
	@Test(dataProvider = "categoryData")
	public void h_Neg_CategoryLevelTCP_BlankMinResidualBalance(int rowNum,String domainName, String categoryName) {
		 final String methodName = "h_Neg_CategoryLevelTCP_BlankMinResidualBalance";
		  Log.startTestCase(methodName);

		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITTRFCNTRLPRF1").getModuleCode();

		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF1").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MinResidualBalance1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MinResidualBalance1"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String prodCode = TCP_CatLevel_Map.get("prodCode");

		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod",MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.minimumbalance"),
				prodCode); 

     
		Assertion.assertEquals(actual, message);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}		

	//SIT Test Case: Max Residual Balance
	@TestManager(TestKey = "PRETUPS-1158") 
	@Test(dataProvider = "categoryData")
	public void i_Neg_CategoryLevelTCP_BlankMaxResidualBalance(int rowNum,String domainName, String categoryName) {

		 final String methodName = "i_Neg_CategoryLevelTCP_BlankMaxResidualBalance";
         Log.startTestCase(methodName);

		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF2").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MaximumResidualBalance1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MaximumResidualBalance1"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String prodCode = TCP_CatLevel_Map.get("prodCode");
		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.maximumbalance"),prodCode); 


		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}


	//SIT Test case: Per C2S Txn MinimumAmount
	@TestManager(TestKey = "PRETUPS-1014") 
	@Test(dataProvider = "categoryData")
	public void j_Neg_CategoryLevelTCP_BlankC2SMinimumAmount(int rowNum,String domainName, String categoryName) {

		 final String methodName = "j_Neg_CategoryLevelTCP_BlankC2SMinimumAmount";
         Log.startTestCase(methodName);
		

		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


	

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF3").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MinimumBalance1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MinimumBalance1"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String prodCode = TCP_CatLevel_Map.get("prodCode");
		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod", 
				MessagesDAO.getLabelByKey("error.profile.transferProfileDetail.label.min"),prodCode); 

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		



	}


	//SIT Test Case; Per C2S Txn Max Amount
	@TestManager(TestKey = "PRETUPS-1015") 
	@Test(dataProvider = "categoryData")
	public void k_Neg_CategoryLevelTCP_BlankC2SMaxBalance(int rowNum,String domainName, String categoryName) {

		
		 final String methodName = "k_Neg_CategoryLevelTCP_BlankC2SMaxBalance";
         Log.startTestCase(methodName);
		
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF4").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MaximumBalance1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MaximumBalance1"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String prodCode = TCP_CatLevel_Map.get("prodCode");
		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod", 
				MessagesDAO.getLabelByKey("error.profile.transferProfileDetail.label.max"),prodCode); 


		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}




	//SIT Test case: Blank Alerting Value	
	@TestManager(TestKey = "PRETUPS-1016") 
	@Test(dataProvider = "categoryData")
	public void l_Neg_CategoryLevelTCP_BlankAlertingBalance(int rowNum,String domainName, String categoryName) {
		 final String methodName = "l_Neg_CategoryLevelTCP_BlankAlertingBalance";
         Log.startTestCase(methodName);
	
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


	

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF5").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("AlertingBalance1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("AlertingBalance1"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String prodCode = TCP_CatLevel_Map.get("prodCode");
		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.alertingbalance"),prodCode); 


		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	//SIT Test Case: Blank Allowed Max Percentage
	@TestManager(TestKey = "PRETUPS-1017") 
	@Test(dataProvider = "categoryData")
	public void m_Neg_CategoryLevelTCP_BlankAllowedMaxPct(int rowNum,String domainName, String categoryName) {
		final String methodName = "m_Neg_CategoryLevelTCP_BlankAllowedMaxPct";
        Log.startTestCase(methodName);
	
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF6").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("AllowedMaxPercentage1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("AllowedMaxPercentage1"));


		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String prodCode = TCP_CatLevel_Map.get("prodCode");
		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.maxpercentage"),prodCode); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}


	/*
	 * SIT Validation Test cases for Daily Count and Values
	 */

	@TestManager(TestKey = "PRETUPS-1018") 
	@Test(dataProvider = "categoryData")
	public void n1_Neg_CategoryLevelTCP_BlankDailyInCount(int rowNum,String domainName, String categoryName) {


		final String methodName = "n1_Neg_CategoryLevelTCP_BlankDailyInCount";
        Log.startTestCase(methodName);
		
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF7").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyInCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyInCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferincount")); 
		Log.info(message);


		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}


	@TestManager(TestKey = "PRETUPS-1019") 
	@Test(dataProvider = "categoryData")
	public void n2_Neg_CategoryLevelTCP_BlankDailyInAlertingCount(int rowNum,String domainName, String categoryName) {
	
		final String methodName = "n2_Neg_CategoryLevelTCP_BlankDailyInAlertingCount";
        Log.startTestCase(methodName);
	
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


	

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF8").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyInAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyInAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferinaltcount")); 
		Log.info(message);
		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1020") 
	@Test(dataProvider = "categoryData")
	public void n3_Neg_CategoryLevelTCP_BlankDailyInTransferValue(int rowNum,String domainName, String categoryName) {


		final String methodName = "n3_Neg_CategoryLevelTCP_BlankDailyInTransferValue";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF9").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyInTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyInTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferinvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1021") 
	@Test(dataProvider = "categoryData")
	public void n4_Neg_CategoryLevelTCP_BlankDailyInAlertingValue(int rowNum,String domainName, String categoryName) {
		final String methodName = "n4_Neg_CategoryLevelTCP_BlankDailyInAlertingValue";
        Log.startTestCase(methodName);
	
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


	

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF10").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyInAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyInAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferinaltvalue")); 
		Log.info(message);


		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1022") 
	@Test(dataProvider = "categoryData")
	public void n5_Neg_CategoryLevelTCP_BlankDailyOutCount(int rowNum,String domainName, String categoryName) {

		final String methodName = "n5_Neg_CategoryLevelTCP_BlankDailyOutCount";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF11").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferoutcount")); 
		Log.info(message);


		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@TestManager(TestKey = "PRETUPS-1023") 
	@Test(dataProvider = "categoryData")
	public void n6_Neg_CategoryLevelTCP_BlankDailyOutAlertingCount(int rowNum,String domainName, String categoryName) {

		final String methodName = "n6_Neg_CategoryLevelTCP_BlankDailyOutAlertingCount";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF12").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferoutaltcount")); 
		Log.info(message);
		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1024") 
	@Test(dataProvider = "categoryData")
	public void n7_Neg_CategoryLevelTCP_BlankDailyOutTransferValue(int rowNum,String domainName, String categoryName) {

		final String methodName = "n7_Neg_CategoryLevelTCP_BlankDailyOutTransferValue";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF13").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyOutTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyOutTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferoutvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1025") 
	@Test(dataProvider = "categoryData")
	public void n8_Neg_CategoryLevelTCP_BlankDailyOutAlertingValue(int rowNum,String domainName, String categoryName) {

		final String methodName = "n8_Neg_CategoryLevelTCP_BlankDailyOutAlertingValue";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


	
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF14").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferoutaltvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1026") 
	@Test(dataProvider = "categoryData")
	public void n9_Neg_CategoryLevelTCP_BlankDailySubscriberInCount(int rowNum,String domainName, String categoryName) {

		final String methodName = "n9_Neg_CategoryLevelTCP_BlankDailySubscriberInCount";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


	

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF15").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			currentNode.log(Status.SKIP, "Subscriber Daily In Count is not available");

		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("DailySubscriberInCount", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberInCount"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required", 
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferincount")); 
			Log.info(message);

			Assertion.assertEquals(actual, message);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	}
	@TestManager(TestKey = "PRETUPS-1027") 
	@Test(dataProvider = "categoryData")
	public void n10_Neg_CategoryLevelTCP_BlankDailySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

		final String methodName = "n10_Neg_CategoryLevelTCP_BlankDailySubscriberInAlertingCount";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF16").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			Assertion.assertSkip("Subscriber Daily In Alerting Count is not available");
		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("DailySubscriberInAlertingCount", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberInAlertingCount"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required", 
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferinaltcount")); 
			Log.info(message);
			Assertion.assertEquals(actual, message);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}

	}




	@TestManager(TestKey = "PRETUPS-1028") 
	@Test(dataProvider = "categoryData")
	public void n11_Neg_CategoryLevelTCP_BlankDailySubscriberInValue(int rowNum,String domainName, String categoryName) {

		final String methodName = "n11_Neg_CategoryLevelTCP_BlankDailySubscriberInValue";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF17").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
				Assertion.assertSkip("Subscriber Daily In Value is not available");

		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("DailySubscriberTransferInValue", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferInValue"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required", 
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferinvalue")); 
			Log.info(message);

			Assertion.assertEquals(actual, message);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	}


	@TestManager(TestKey = "PRETUPS-1029") 
    @Test(dataProvider = "categoryData")
	public void n12_Neg_CategoryLevelTCP_BlankDailySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

		final String methodName = "n12_Neg_CategoryLevelTCP_BlankDailySubscriberTransferInAlertingValue";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


	

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF18").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			Assertion.assertSkip("Subscriber Daily In Transfer Value is not available");
		
		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("DailySubscriberTransferInAlertingValue", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferInAlertingValue"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required", 
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferinaltvalue")); 
			Log.info(message);

			Assertion.assertEquals(actual, message);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	}



	@TestManager(TestKey = "PRETUPS-1030") 
	@Test(dataProvider = "categoryData")
	public void n13_Neg_CategoryLevelTCP_BlankDailySubscriberOutCount(int rowNum,String domainName, String categoryName) {

		final String methodName = "n13_Neg_CategoryLevelTCP_BlankDailySubscriberOutCount";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF19").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberTransferOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferoutcount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1031") 
	@Test(dataProvider = "categoryData")
	public void n14_Neg_CategoryLevelTCP_BlankDailySubscriberOutAlertingCount(int rowNum,String domainName, String categoryName) {

		final String methodName = "n14_Neg_CategoryLevelTCP_BlankDailySubscriberOutAlertingCount";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF20").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberTransferOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferoutaltcount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}


	@TestManager(TestKey = "PRETUPS-1032") 
	@Test(dataProvider = "categoryData")
	public void n15_Neg_CategoryLevelTCP_BlankDailySubscriberOutValue(int rowNum,String domainName, String categoryName) {

		final String methodName = "n15_Neg_CategoryLevelTCP_BlankDailySubscriberOutValue";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF21").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberTransferOutValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferOutValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferoutvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}




	@TestManager(TestKey = "PRETUPS-1033") 
	@Test(dataProvider = "categoryData")
	public void n16_Neg_CategoryLevelTCP_BlankDailySubscriberOutAlertingValue(int rowNum,String domainName, String categoryName) {

		final String methodName = "n16_Neg_CategoryLevelTCP_BlankDailySubscriberOutAlertingValue";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF22").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberTransferOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferoutaltvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	/*
	 * SIT Validation Test cases for Weekly Count and Values
	 */

	@TestManager(TestKey = "PRETUPS-1034") 
	@Test(dataProvider = "categoryData")
	public void o1_Neg_CategoryLevelTCP_BlankWeeklyInCount(int rowNum,String domainName, String categoryName) {

		final String methodName = "o1_Neg_CategoryLevelTCP_BlankWeeklyInCount";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF23").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyInCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyInCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferincount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1035") 
    @Test(dataProvider = "categoryData")
	public void o2_Neg_CategoryLevelTCP_BlankWeeklyInAlertingCount(int rowNum,String domainName, String categoryName) {

		final String methodName = "o2_Neg_CategoryLevelTCP_BlankWeeklyInAlertingCount";
        Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF24").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyInAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyInAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferinaltcount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1037") 
	@Test(dataProvider = "categoryData")
	public void o3_Neg_CategoryLevelTCP_BlankWeeklyInTransferValue(int rowNum,String domainName, String categoryName) {

	final String methodName="o3_Neg_CategoryLevelTCP_BlankWeeklyInTransferValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF25").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyInTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyInTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferinvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}

	@TestManager(TestKey = "PRETUPS-1038") 
	@Test(dataProvider = "categoryData")
	public void o4_Neg_CategoryLevelTCP_BlankWeeklyInAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="o4_Neg_CategoryLevelTCP_BlankWeeklyInAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF26").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyInAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyInAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferinaltvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1040") 
	@Test(dataProvider = "categoryData")
	public void o5_Neg_CategoryLevelTCP_BlankWeeklyOutCount(int rowNum,String domainName, String categoryName) {

	final String methodName="o5_Neg_CategoryLevelTCP_BlankWeeklyOutCount";
	Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF27").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferoutcount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}


	@TestManager(TestKey = "PRETUPS-1041") 
	@Test(dataProvider = "categoryData")
	public void o6_Neg_CategoryLevelTCP_BlankWeeklyOutAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="o6_Neg_CategoryLevelTCP_BlankWeeklyOutAlertingCount";
	Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF28").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferoutaltcount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1043") 
	@Test(dataProvider = "categoryData")
	public void o7_Neg_CategoryLevelTCP_BlankWeeklyOutTransferValue(int rowNum,String domainName, String categoryName) {

	final String methodName="o7_Neg_CategoryLevelTCP_BlankWeeklyOutTransferValue";
	Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF29").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyOutTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyOutTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferoutvalue")); 
		Log.info(message);
		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1044") 
	@Test(dataProvider = "categoryData")
	public void o8_Neg_CategoryLevelTCP_BlankWeeklyOutAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="o8_Neg_CategoryLevelTCP_BlankWeeklyOutAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF30").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferoutaltvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1045") 
	@Test(dataProvider = "categoryData")
	public void o9_Neg_CategoryLevelTCP_BlankWeeklySubscriberInCount(int rowNum,String domainName, String categoryName) {

	final String methodName="o9_Neg_CategoryLevelTCP_BlankWeeklySubscriberInCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF31").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			currentNode.log(Status.SKIP, "Subscriber Weekly In Count is not available");

		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("WeeklySubscriberInCount", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberInCount"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

			String message = MessagesDAO.prepareMessageByKey("errors.required", 
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferincount")); 
			Log.info(message);

			Assertion.assertEquals(actual, message);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	}

	@TestManager(TestKey = "PRETUPS-1047") 
	@Test(dataProvider = "categoryData")
	public void o10_Neg_CategoryLevelTCP_BlankWeeklySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="o10_Neg_CategoryLevelTCP_BlankWeeklySubscriberInAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF32").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			currentNode.log(Status.SKIP, "Subscriber Weekly In Alerting Count is not available");

		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("WeeklySubscriberInAlertingCount", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberInAlertingCount"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required",
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferinaltcount")); 
			Log.info(message);

			Assertion.assertEquals(actual, message);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
	}



	@TestManager(TestKey = "PRETUPS-1048") 
	@Test(dataProvider = "categoryData")
	public void o11_Neg_CategoryLevelTCP_BlankWeeklySubscriberInValue(int rowNum,String domainName, String categoryName) {

	final String methodName="o11_Neg_CategoryLevelTCP_BlankWeeklySubscriberInValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF33").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			currentNode.log(Status.SKIP, "Subscriber Weekly In Value is not available");

		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("WeeklySubscriberTransferInValue", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferInValue"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required",
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferinvalue")); 
			Log.info(message);

			Assertion.assertEquals(actual, message);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	}



	@TestManager(TestKey = "PRETUPS-1050") 
	@Test(dataProvider = "categoryData")
	public void o12_Neg_CategoryLevelTCP_BlankWeeklySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="o12_Neg_CategoryLevelTCP_BlankWeeklySubscriberTransferInAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		
			
			
	
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF34").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			currentNode.log(Status.SKIP, "Subscriber Weekly In Alerting Value is not available");

		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("WeeklySubscriberTransferInAlertingValue", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferInAlertingValue"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required", 
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferinaltvalue")); 
			Log.info(message);

			Assertion.assertEquals(actual, message);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
	}



	@TestManager(TestKey = "PRETUPS-1052") 
	@Test(dataProvider = "categoryData")
	public void o13_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutCount(int rowNum,String domainName, String categoryName) {

	final String methodName="o13_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF35").getExtentCase());
		currentNode.assignCategory(assignCategory);


		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferoutcount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@TestManager(TestKey = "PRETUPS-1057") 
	@Test(dataProvider = "categoryData")
	public void o14_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="o14_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF36").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferoutaltcount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1058") 

	@Test(dataProvider = "categoryData")
	public void o15_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutValue(int rowNum,String domainName, String categoryName) {

	final String methodName="o15_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutValue"; 
	Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF37").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferoutvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}



	@TestManager(TestKey = "PRETUPS-1059") 

	@Test(dataProvider = "categoryData")
	public void o16_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="o16_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF38").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferoutaltvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}







	/*
	 * SIT Validation Test cases for Monthly Count and Values
	 */
	@TestManager(TestKey = "PRETUPS-1060") 
	@Test(dataProvider = "categoryData")
	public void p1_Neg_CategoryLevelTCP_BlankMonthlyInCount(int rowNum,String domainName, String categoryName) {

	final String methodName="p1_Neg_CategoryLevelTCP_BlankMonthlyInCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF39").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyInCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyInCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferincount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1062") 

	@Test(dataProvider = "categoryData")
	public void p2_Neg_CategoryLevelTCP_BlankMonthlyInAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="p2_Neg_CategoryLevelTCP_BlankMonthlyInAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF40").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyInAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyInAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferinaltcount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1063") 
	@Test(dataProvider = "categoryData")
	public void p3_Neg_CategoryLevelTCP_BlankMonthlyInTransferValue(int rowNum,String domainName, String categoryName) {

	final String methodName="p3_Neg_CategoryLevelTCP_BlankMonthlyInTransferValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF41").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyInTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyInTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferinvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1064") 
	@Test(dataProvider = "categoryData")
	public void p4_Neg_CategoryLevelTCP_BlankMonthlyInAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="p4_Neg_CategoryLevelTCP_BlankMonthlyInAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF42").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyInAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyInAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferinaltvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1065") 
	@Test(dataProvider = "categoryData")
	public void p5_Neg_CategoryLevelTCP_BlankMonthlyOutCount(int rowNum,String domainName, String categoryName) {

	final String methodName="p5_Neg_CategoryLevelTCP_BlankMonthlyOutCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF43").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferoutcount")); 
		Log.info(message);


		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}


	@TestManager(TestKey = "PRETUPS-1066") 
	@Test(dataProvider = "categoryData")
	public void p6_Neg_CategoryLevelTCP_BlankMonthlyOutAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="p6_Neg_CategoryLevelTCP_BlankMonthlyOutAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);



		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF44").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferoutaltcount")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1068") 
	@Test(dataProvider = "categoryData")
	public void p7_Neg_CategoryLevelTCP_BlankMonthlyOutTransferValue(int rowNum,String domainName, String categoryName) {

	final String methodName="p7_Neg_CategoryLevelTCP_BlankMonthlyOutTransferValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF45").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyOutTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyOutTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferoutvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@TestManager(TestKey = "PRETUPS-1070") 

	@Test(dataProvider = "categoryData")
	public void p8_Neg_CategoryLevelTCP_BlankMonthlyOutAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="p8_Neg_CategoryLevelTCP_BlankMonthlyOutAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF46").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferoutaltvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1072") 
	@Test(dataProvider = "categoryData")
	public void p9_Neg_CategoryLevelTCP_BlankMonthlySubscriberInCount(int rowNum,String domainName, String categoryName) {

	final String methodName="p9_Neg_CategoryLevelTCP_BlankMonthlySubscriberInCount"; 
	Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF47").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			currentNode.log(Status.SKIP, "Subscriber Monthly In Count is not available");

		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("MonthlySubscriberInCount", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberInCount"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required", 
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferincount")); 
			Log.info(message);

			Assertion.assertEquals(actual, message);

			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	}
	@TestManager(TestKey = "PRETUPS-1074") 
	@Test(dataProvider = "categoryData")
	public void p10_Neg_CategoryLevelTCP_BlankMonthlySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="p10_Neg_CategoryLevelTCP_BlankMonthlySubscriberInAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);



		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF48").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			Assertion.assertSkip("Subscriber Daily In Alerting Count is not available");
		

		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("MonthlySubscriberInAlertingCount", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberInAlertingCount"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required", 
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferinaltcount")); 
			Log.info(message);

			Assertion.assertEquals(actual, message);

			
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}




	@TestManager(TestKey = "PRETUPS-1075") 
	@Test(dataProvider = "categoryData")
	public void p11_Neg_CategoryLevelTCP_BlankMonthlySubscriberInValue(int rowNum,String domainName, String categoryName) {

	final String methodName="p11_Neg_CategoryLevelTCP_BlankMonthlySubscriberInValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF49").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
		Assertion.assertSkip("Subscriber Monthly In Value is not available");

		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("MonthlySubscriberTransferInValue", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferInValue"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required", 
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferinvalue")); 
			Log.info(message);


			Assertion.assertEquals(actual, message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@TestManager(TestKey = "PRETUPS-1077") 
	@Test(dataProvider = "categoryData")
	public void p12_Neg_CategoryLevelTCP_BlankMonthlySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="p12_Neg_CategoryLevelTCP_BlankMonthlySubscriberTransferInAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF50").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			
Assertion.assertSkip("Subscriber Monthly In Alerting value is not available");
		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
			TCP_CatLevel_Map.put("MonthlySubscriberTransferInAlertingValue", "");

			Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferInAlertingValue"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
			String message = MessagesDAO.prepareMessageByKey("errors.required", 
					MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferinaltvalue")); 
			Log.info(message);
			Assertion.assertEquals(actual, message);
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@TestManager(TestKey = "PRETUPS-1036") 
	@Test(dataProvider = "categoryData")
	public void p13_Neg_CategoryLevelTCP_BlankMonthlySubscriberOutCount(int rowNum,String domainName, String categoryName) {

	final String methodName="p13_Neg_CategoryLevelTCP_BlankMonthlySubscriberOutCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF51").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferoutcount")); 
		Log.info(message);
		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1039") 
	@Test(dataProvider = "categoryData")
	public void p14_Neg_CategoryLevelTCP_BlankMonthlySubscriberOutAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="p14_Neg_CategoryLevelTCP_BlankMonthlySubscriberOutAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF52").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferoutaltcount")); 
		Log.info(message);
		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	

	}



	@TestManager(TestKey = "PRETUPS-1042") 
	@Test(dataProvider = "categoryData")
	public void p15_Neg_CategoryLevelTCP_BlankMonthlySubscriberOutValue(int rowNum,String domainName, String categoryName) {

	final String methodName="p15_Neg_CategoryLevelTCP_BlankMonthlySubscriberOutValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF53").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferoutvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}



	@TestManager(TestKey = "PRETUPS-1046") 
	@Test(dataProvider = "categoryData")
	public void p16_Neg_CategoryLevelTCP_BlankMonthlySubscriberTransferOutAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="p16_Neg_CategoryLevelTCP_BlankMonthlySubscriberTransferOutAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF54").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferoutaltvalue")); 
		Log.info(message);

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	

	}




	/*
	 * Daily-Weekly Validations	
	 */





	@TestManager(TestKey = "PRETUPS-1049") 
	@Test(dataProvider = "categoryData")
	public void q_01_Neg_CategoryLevelTCP_DailyWeeklyTransferInCountValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="q_01_Neg_CategoryLevelTCP_DailyWeeklyTransferInCountValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF55").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyInCount");
		Log.info("The value of Weekly In Count is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklyInCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyInCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyInCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailyweeklyincount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	

	}		

	@TestManager(TestKey = "PRETUPS-1051") 
	@Test(dataProvider = "categoryData")
	public void q_02_Neg_CategoryLevelTCP_DailyWeeklyTransferInAlertingCountValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="q_02_Neg_CategoryLevelTCP_DailyWeeklyTransferInAlertingCountValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF56").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyInAlertingCount");
		Log.info("The value of WeeklyInAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklyInAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyInAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyInAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailyweeklyinaltcount"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1053") 
	@Test(dataProvider = "categoryData")
	public void q_03_Neg_CategoryLevelTCP_DailyWeeklyWeeklyInTransferValueValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="q_03_Neg_CategoryLevelTCP_DailyWeeklyWeeklyInTransferValueValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF57").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyInTransferValue");
		Log.info("The value of WeeklyInTransferValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklyInTransferValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyInTransferValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyInTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailyweeklyinvalue"); 



		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1054") 
	@Test(dataProvider = "categoryData")
	public void q_04_Neg_CategoryLevelTCP_DailyWeeklyInAlertingValueValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="q_04_Neg_CategoryLevelTCP_DailyWeeklyInAlertingValueValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF58").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyInAlertingValue");
		Log.info("The value of WeeklyInAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklyInAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyInAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyInAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailyweeklyinaltvalue"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}


	@TestManager(TestKey = "PRETUPS-1055") 
	@Test(dataProvider = "categoryData")
	public void q_05_Neg_CategoryLevelTCP_DailyWeeklyOutCountValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="q_05_Neg_CategoryLevelTCP_DailyWeeklyOutCountValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF59").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyOutCount");
		Log.info("The value of WeeklyOutCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklyOutCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyOutCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailyweeklyoutcount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1056") 
	@Test(dataProvider = "categoryData")
	public void q_06_Neg_CategoryLevelTCP_DailyWeeklyOutAlertingCountn(int rowNum,String domainName, String categoryName) {

	final String methodName="q_06_Neg_CategoryLevelTCP_DailyWeeklyOutAlertingCountn"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF60").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyOutAlertingCount");
		Log.info("The value of WeeklyOutAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklyOutAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyOutAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailyweeklyoutaltcount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}






	@TestManager(TestKey = "PRETUPS-1061") 
	@Test(dataProvider = "categoryData")
	public void q_07_Neg_CategoryLevelTCP_DailyWeeklyOutTransferValue(int rowNum,String domainName, String categoryName) {

	final String methodName="q_07_Neg_CategoryLevelTCP_DailyWeeklyOutTransferValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF61").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyOutTransferValue");
		Log.info("The value of WeeklyOutTransferValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklyOutTransferValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyOutTransferValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyOutTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailyweeklyoutvalue"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}







	@TestManager(TestKey = "PRETUPS-1067") 
	@Test(dataProvider = "categoryData")
	public void q_08_Neg_CategoryLevelTCP_DailyWeeklyOutAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="q_08_Neg_CategoryLevelTCP_DailyWeeklyOutAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF62").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyOutAlertingValue");
		Log.info("The value of WeeklyOutAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklyOutAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyOutAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailyweeklyoutaltvalue"); 



		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1069") 
	@Test(dataProvider = "categoryData")
	public void q_09_Neg_CategoryLevelTCP_DailyWeeklySubscriberInCount(int rowNum,String domainName, String categoryName) {

	final String methodName="q_09_Neg_CategoryLevelTCP_DailyWeeklySubscriberInCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF63").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			
Assertion.assertSkip("Subscriber Daily In Count is not available");
		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


			String w1=TCP_CatLevel_Map.get("WeeklySubscriberInCount");
			Log.info("The value of WeeklySubscriberInCount is:" +w1);
			int a=Integer.parseInt(w1);
			int b= --a;

			String w2= String.valueOf(b);

			Log.info("The decremented value of WeeklySubscriberInCount to be put in Map:" +w2);

			TCP_CatLevel_Map.put("WeeklySubscriberInCount",w2);


			Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberInCount"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

			String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailyweeklyincount"); 


			Assertion.assertEquals(actual, message);
			
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1071") 
	@Test(dataProvider = "categoryData")
	public void q_10_Neg_CategoryLevelTCP_DailyWeeklySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="q_10_Neg_CategoryLevelTCP_DailyWeeklySubscriberInAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF64").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			
Assertion.assertSkip("Subscriber Daily In Alerting Count is not available");
		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


			String w1=TCP_CatLevel_Map.get("WeeklySubscriberInAlertingCount");
			Log.info("The value of WeeklySubscriberInAlertingCount is:" +w1);
			int a=Integer.parseInt(w1);
			int b= --a;

			String w2= String.valueOf(b);

			Log.info("The decremented value of WeeklySubscriberInAlertingCount to be put in Map:" +w2);

			TCP_CatLevel_Map.put("WeeklySubscriberInAlertingCount",w2);


			Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberInAlertingCount"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

			String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailyweeklyinaltcount"); 


			Assertion.assertEquals(actual, message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1073") 
	@Test(dataProvider = "categoryData")
	public void q_11_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferInValue(int rowNum,String domainName, String categoryName) {

	final String methodName="q_11_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferInValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);



		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF65").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
	
Assertion.assertSkip("Subscriber Daily In Transfer Value is not available");
		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


			String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferInValue");
			Log.info("The value of WeeklySubscriberTransferInValue is:" +w1);
			int a=Integer.parseInt(w1);
			int b= --a;

			String w2= String.valueOf(b);

			Log.info("The decremented value of WeeklySubscriberTransferInValue to be put in Map:" +w2);

			TCP_CatLevel_Map.put("WeeklySubscriberTransferInValue",w2);


			Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferInValue"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

			String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailyweeklyinvalue"); 


			Assertion.assertEquals(actual, message);

		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1076") 
	@Test(dataProvider = "categoryData")
	public void q_12_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="q_12_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferInAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF66").getExtentCase());
		currentNode.assignCategory(assignCategory);
		int TCP_ClientVer = Integer.parseInt(_masterVO.getClientDetail("TCP_VER"));
		if (TCP_ClientVer == 0) {
			currentNode.log(Status.SKIP, "Subscriber Daily In Alerting Value is not available");
      Assertion.assertSkip("Subscriber Daily In Alerting Value is not available");
		}
		else {

			TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


			String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferInAlertingValue");
			Log.info("The value of WeeklySubscriberTransferInAlertingValue is:" +w1);
			int a=Integer.parseInt(w1);
			int b= --a;

			String w2= String.valueOf(b);

			Log.info("The decremented value of WeeklySubscriberTransferInAlertingValue to be put in Map:" +w2);

			TCP_CatLevel_Map.put("WeeklySubscriberTransferInAlertingValue",w2);


			Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferInAlertingValue"));

			TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
			String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

			String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailyweeklyinaltvalue"); 


			Assertion.assertEquals(actual, message);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@TestManager(TestKey = "PRETUPS-1078") 
	@Test(dataProvider = "categoryData")
	public void q_13_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutCount(int rowNum,String domainName, String categoryName) {

	final String methodName="q_13_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF67").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferOutCount");
		Log.info("The value of WeeklySubscriberTransferOutCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklySubscriberTransferOutCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailyweeklyoutcount"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1079") 
	@Test(dataProvider = "categoryData")
	public void q_14_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="q_14_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF68").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingCount");
		Log.info("The value of WeeklySubscriberTransferOutAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklySubscriberTransferOutAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailyweeklyoutaltcount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1081") 
	@Test(dataProvider = "categoryData")
	public void q_15_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutValue(int rowNum,String domainName, String categoryName) {

	final String methodName="q_15_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF69").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferOutValue");
		Log.info("The value of WeeklySubscriberTransferOutValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklySubscriberTransferOutValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailyweeklyoutvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1082") 
	@Test(dataProvider = "categoryData")
	public void q_16_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="q_16_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF70").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingValue");
		Log.info("The value of WeeklySubscriberTransferOutAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of WeeklySubscriberTransferOutAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailyweeklyoutaltvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}







	/*
	 * Daily Monthly Validations
	 */






	@TestManager(TestKey = "PRETUPS-1086") 
	@Test(dataProvider = "categoryData")
	public void r_01_Neg_CategoryLevelTCP_DailyMonthlyTransferInCountValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="r_01_Neg_CategoryLevelTCP_DailyMonthlyTransferInCountValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF71").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlyInCount");
		Log.info("The value of Monthly In Count is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlyInCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlyInCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlyInCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailymonthlyincount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}		

	@TestManager(TestKey = "PRETUPS-1088") 
	@Test(dataProvider = "categoryData")
	public void r_02_Neg_CategoryLevelTCP_DailyMonthlyTransferInAlertingCountValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="r_02_Neg_CategoryLevelTCP_DailyMonthlyTransferInAlertingCountValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF72").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlyInAlertingCount");
		Log.info("The value of MonthlyInAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlyInAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlyInAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlyInAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailymonthlyinaltcount"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1089") 
	@Test(dataProvider = "categoryData")
	public void r_03_Neg_CategoryLevelTCP_DailyMonthlyMonthlyInTransferValueValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="r_03_Neg_CategoryLevelTCP_DailyMonthlyMonthlyInTransferValueValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF73").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlyInTransferValue");
		Log.info("The value of MonthlyInTransferValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlyInTransferValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlyInTransferValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlyInTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailymonthlyinvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1090") 
	@Test(dataProvider = "categoryData")
	public void r_04_Neg_CategoryLevelTCP_DailyMonthlyInAlertingValueValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="r_04_Neg_CategoryLevelTCP_DailyMonthlyInAlertingValueValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);



		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF74").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlyInAlertingValue");
		Log.info("The value of MonthlyInAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlyInAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlyInAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlyInAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailymonthlyinaltvalue"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}


	@TestManager(TestKey = "PRETUPS-1092") 
	@Test(dataProvider = "categoryData")
	public void r_05_Neg_CategoryLevelTCP_DailyMonthlyOutCountValidation(int rowNum,String domainName, String categoryName) {

	final String methodName=""; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF75").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlyOutCount");
		Log.info("The value of MonthlyOutCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlyOutCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlyOutCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlyOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailymonthlyoutcount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1094") 
	@Test(dataProvider = "categoryData")
	public void r_06_Neg_CategoryLevelTCP_DailyMonthlyOutAlertingCountn(int rowNum,String domainName, String categoryName) {

	final String methodName="r_06_Neg_CategoryLevelTCP_DailyMonthlyOutAlertingCountn"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF76").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlyOutAlertingCount");
		Log.info("The value of MonthlyOutAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlyOutAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlyOutAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlyOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailymonthlyoutaltcount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}






	@TestManager(TestKey = "PRETUPS-1097") 
	@Test(dataProvider = "categoryData")
	public void r_07_Neg_CategoryLevelTCP_DailyMonthlyOutTransferValue(int rowNum,String domainName, String categoryName) {

	final String methodName="r_07_Neg_CategoryLevelTCP_DailyMonthlyOutTransferValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF77").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlyOutTransferValue");
		Log.info("The value of MonthlyOutTransferValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlyOutTransferValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlyOutTransferValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlyOutTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailymonthlyoutvalue"); 
	Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}







	@TestManager(TestKey = "PRETUPS-1099") 
	@Test(dataProvider = "categoryData")
	public void r_08_Neg_CategoryLevelTCP_DailyMonthlyOutAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="r_08_Neg_CategoryLevelTCP_DailyMonthlyOutAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF78").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlyOutAlertingValue");
		Log.info("The value of MonthlyOutAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlyOutAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlyOutAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlyOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.dailymonthlyoutaltvalue"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1100") 
	@Test(dataProvider = "categoryData")
	public void r_09_Neg_CategoryLevelTCP_DailyMonthlySubscriberInCount(int rowNum,String domainName, String categoryName) {

	final String methodName="r_09_Neg_CategoryLevelTCP_DailyMonthlySubscriberInCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF79").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlySubscriberInCount");
		Log.info("The value of MonthlySubscriberInCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlySubscriberInCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlySubscriberInCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlySubscriberInCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailymonthlyincount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1103") 
	@Test(dataProvider = "categoryData")
	public void r_10_Neg_CategoryLevelTCP_DailyMonthlySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="r_10_Neg_CategoryLevelTCP_DailyMonthlySubscriberInAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF80").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlySubscriberInAlertingCount");
		Log.info("The value of MonthlySubscriberInAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlySubscriberInAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlySubscriberInAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlySubscriberInAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailymonthlyinaltcount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1104") 
	@Test(dataProvider = "categoryData")
	public void r_11_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferInValue(int rowNum,String domainName, String categoryName) {

	final String methodName="r_11_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferInValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF81").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlySubscriberTransferInValue");
		Log.info("The value of MonthlySubscriberTransferInValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlySubscriberTransferInValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlySubscriberTransferInValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferInValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailymonthlyinvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1106") 
	@Test(dataProvider = "categoryData")
	public void r_12_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="r_12_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferInAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF82").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlySubscriberTransferInAlertingValue");
		Log.info("The value of MonthlySubscriberTransferInAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlySubscriberTransferInAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlySubscriberTransferInAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferInAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailymonthlyinaltvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}


	@TestManager(TestKey = "PRETUPS-1107") 
	@Test(dataProvider = "categoryData")
	public void r_13_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutCount(int rowNum,String domainName, String categoryName) {

	final String methodName="r_13_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF83").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlySubscriberTransferOutCount");
		Log.info("The value of MonthlySubscriberTransferOutCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlySubscriberTransferOutCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailymonthlyoutcount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1109") 
	@Test(dataProvider = "categoryData")
	public void r_14_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="r_14_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF84").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingCount");
		Log.info("The value of MonthlySubscriberTransferOutAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlySubscriberTransferOutAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailymonthlyoutaltcount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1112") 
	@Test(dataProvider = "categoryData")
	public void r_15_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutValue(int rowNum,String domainName, String categoryName) {

	final String methodName="r_15_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);



		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF85").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlySubscriberTransferOutValue");
		Log.info("The value of MonthlySubscriberTransferOutValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlySubscriberTransferOutValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailymonthlyoutvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}

	@TestManager(TestKey = "PRETUPS-1080") 
	@Test(dataProvider = "categoryData")
	public void r_16_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="r_16_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF86").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingValue");
		Log.info("The value of MonthlySubscriberTransferOutAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= --a;

		String w2= String.valueOf(b);

		Log.info("The decremented value of MonthlySubscriberTransferOutAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberdailymonthlyoutaltvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}





	/*
	 * Weekly-Monthly Validations	
	 */




	@TestManager(TestKey = "PRETUPS-1083") 
	@Test(dataProvider = "categoryData")
	public void s_01_Neg_CategoryLevelTCP_weeklyMonthlyTransferInCountValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="s_01_Neg_CategoryLevelTCP_weeklyMonthlyTransferInCountValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF87").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyInCount");
		Log.info("The value of Weekly In Count is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklyInCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyInCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyInCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.weeklymonthlyincount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}		
	@TestManager(TestKey = "PRETUPS-1084") 

	@Test(dataProvider = "categoryData")
	public void s_02_Neg_CategoryLevelTCP_weeklyMonthlyTransferInAlertingCountValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="s_02_Neg_CategoryLevelTCP_weeklyMonthlyTransferInAlertingCountValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF88").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyInAlertingCount");
		Log.info("The value of WeeklyInAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklyInAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyInAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyInAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.weeklymonthlyinaltcount"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@TestManager(TestKey = "PRETUPS-1085") 
	@Test(dataProvider = "categoryData")
	public void s_03_Neg_CategoryLevelTCP_weeklyMonthlyMonthlyInTransferValueValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="s_03_Neg_CategoryLevelTCP_weeklyMonthlyMonthlyInTransferValueValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF89").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyInTransferValue");
		Log.info("The value of WeeklyInTransferValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklyInTransferValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyInTransferValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyInTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.weeklymonthlyinvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1087") 
	@Test(dataProvider = "categoryData")
	public void s_04_Neg_CategoryLevelTCP_weeklyMonthlyInAlertingValueValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="s_04_Neg_CategoryLevelTCP_weeklyMonthlyInAlertingValueValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF90").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyInAlertingValue");
		Log.info("The value of WeeklyInAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklyInAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyInAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyInAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.weeklymonthlyinaltvalue"); 



		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@TestManager(TestKey = "PRETUPS-1091") 
	@Test(dataProvider = "categoryData")
	public void s_05_Neg_CategoryLevelTCP_weeklyMonthlyOutCountValidation(int rowNum,String domainName, String categoryName) {

	final String methodName="s_05_Neg_CategoryLevelTCP_weeklyMonthlyOutCountValidation"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF91").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyOutCount");
		Log.info("The value of WeeklyOutCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklyOutCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyOutCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.weeklymonthlyoutcount"); 



		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1093") 
	@Test(dataProvider = "categoryData")
	public void s_06_Neg_CategoryLevelTCP_weeklyMonthlyOutAlertingCountn(int rowNum,String domainName, String categoryName) {

	final String methodName="s_06_Neg_CategoryLevelTCP_weeklyMonthlyOutAlertingCountn"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF92").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyOutAlertingCount");
		Log.info("The value of WeeklyOutAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklyOutAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyOutAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.weeklymonthlyoutaltcount"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}






	@TestManager(TestKey = "PRETUPS-1095") 
	@Test(dataProvider = "categoryData")
	public void s_07_Neg_CategoryLevelTCP_weeklyMonthlyOutTransferValue(int rowNum,String domainName, String categoryName) {

	final String methodName="s_07_Neg_CategoryLevelTCP_weeklyMonthlyOutTransferValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF93").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyOutTransferValue");
		Log.info("The value of WeeklyOutTransferValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklyOutTransferValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyOutTransferValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyOutTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.weeklymonthlyoutvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}







	@TestManager(TestKey = "PRETUPS-1096") 
	@Test(dataProvider = "categoryData")
	public void s_08_Neg_CategoryLevelTCP_weeklyMonthlyOutAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="s_08_Neg_CategoryLevelTCP_weeklyMonthlyOutAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF94").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklyOutAlertingValue");
		Log.info("The value of WeeklyOutAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklyOutAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklyOutAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklyOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.weeklymonthlyoutaltvalue"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1098") 
	@Test(dataProvider = "categoryData")
	public void s_09_Neg_CategoryLevelTCP_weeklyMonthlySubscriberInCount(int rowNum,String domainName, String categoryName) {

	final String methodName="s_09_Neg_CategoryLevelTCP_weeklyMonthlySubscriberInCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF95").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberInCount");
		Log.info("The value of WeeklySubscriberInCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklySubscriberInCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberInCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberInCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberweeklymonthlyincount"); 

		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-1101") 
	@Test(dataProvider = "categoryData")
	public void s_10_Neg_CategoryLevelTCP_weeklyMonthlySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="s_10_Neg_CategoryLevelTCP_weeklyMonthlySubscriberInAlertingCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF96").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberInAlertingCount");
		Log.info("The value of WeeklySubscriberInAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklySubscriberInAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberInAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberInAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberweeklymonthlyinaltcount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@TestManager(TestKey = "PRETUPS-1102") 
	@Test(dataProvider = "categoryData")
	public void s_11_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferInValue(int rowNum,String domainName, String categoryName) {

	final String methodName="s_11_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferInValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver

		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF97").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferInValue");
		Log.info("The value of WeeklySubscriberTransferInValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklySubscriberTransferInValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberTransferInValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferInValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberweeklymonthlyinvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}



	@TestManager(TestKey = "PRETUPS-1105") 
	@Test(dataProvider = "categoryData")
	public void s_12_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="s_12_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferInAlertingValue"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF98").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferInAlertingValue");
		Log.info("The value of WeeklySubscriberTransferInAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklySubscriberTransferInAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberTransferInAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferInAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberweeklymonthlyinaltvalue"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}


	@TestManager(TestKey = "PRETUPS-1108") 
	@Test(dataProvider = "categoryData")
	public void s_13_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutCount(int rowNum,String domainName, String categoryName) {

	final String methodName="s_13_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutCount"; Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF99").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferOutCount");
		Log.info("The value of WeeklySubscriberTransferOutCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklySubscriberTransferOutCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberweeklymonthlyoutcount"); 


		Assertion.assertEquals(actual, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@TestManager(TestKey = "PRETUPS-1110") 
	@Test(dataProvider = "categoryData")
	public void s_14_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutAlertingCount(int rowNum,String domainName, String categoryName) {

	final String methodName="s_14_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutAlertingCount";
	Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF100").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingCount");
		Log.info("The value of WeeklySubscriberTransferOutAlertingCount is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklySubscriberTransferOutAlertingCount to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutAlertingCount",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberweeklymonthlyoutaltcount"); 


		 Assertion.assertEquals(actual, message);

	}

	@TestManager(TestKey = "PRETUPS-1111") 
	@Test(dataProvider = "categoryData")
	public void s_15_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutValue(int rowNum,String domainName, String categoryName) {

	final String methodName="s_15_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutValue";
	Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF101").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferOutValue");
		Log.info("The value of WeeklySubscriberTransferOutValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklySubscriberTransferOutValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberweeklymonthlyoutvalue"); 

		 Assertion.assertEquals(actual, message);
		
	}

	@TestManager(TestKey = "PRETUPS-1113") 
	@Test(dataProvider = "categoryData")
	public void s_16_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutAlertingValue(int rowNum,String domainName, String categoryName) {

	final String methodName="s_16_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutAlertingValue"; 
	Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF102").getExtentCase());
		currentNode.assignCategory(assignCategory);

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();


		String w1=TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingValue");
		Log.info("The value of WeeklySubscriberTransferOutAlertingValue is:" +w1);
		int a=Integer.parseInt(w1);
		int b= ++a;

		String w2= String.valueOf(b);

		Log.info("The incremented value of WeeklySubscriberTransferOutAlertingValue to be put in Map:" +w2);

		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutAlertingValue",w2);


		Log.info("Final Value: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("error.profile.transferprofiledetail.subscriberweeklymonthlyoutaltvalue"); 


		 Assertion.assertEquals(actual, message);

	}






	@TestManager(TestKey = "PRETUPS-1114") 
	@Test(dataProvider = "categoryData")
	public void s_17_Neg_CategoryLevelTCP_DuplicateName(int rowNum,String domainName, String categoryName) {

	final String methodName="s_17_Neg_CategoryLevelTCP_DuplicateName"; 
	Log.startTestCase(methodName);
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITTRFCNTRLPRF103").getExtentCase());
		currentNode.assignCategory(assignCategory);

		//TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
				
		
		TCPName = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, 1);
		
		Map <String,String> TCP_CatLevel_Map = TransferControlProfile.createChannelLevelTransferControlProfile_Neg(rowNum, domainName, categoryName,TCPName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.getLabelByKey("profile.error.shortnameexist"); 

		Assertion.assertEquals(actual, message);
 
	}



}
