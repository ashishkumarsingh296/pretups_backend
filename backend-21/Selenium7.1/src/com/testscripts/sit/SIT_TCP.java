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
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_TCP extends BaseTest {


	static boolean TestCaseCounter = false;
	String TCPName;
	String profile_ID;

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
	@Test(dataProvider = "categoryData")
	public void h_Neg_CategoryLevelTCP_BlankMinResidualBalance(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank Minimum Residual Balance");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MinResidualBalance1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MinResidualBalance1"));

		 TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String prodCode = TCP_CatLevel_Map.get("prodCode");

		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod",MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.minimumbalance"),
				prodCode); 


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}		

     //SIT Test Case: Max Residual Balance

	@Test(dataProvider = "categoryData")
	public void i_Neg_CategoryLevelTCP_BlankMaxResidualBalance(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank Maximum Residual Balance");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MaximumResidualBalance1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MaximumResidualBalance1"));

		 TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String prodCode = TCP_CatLevel_Map.get("prodCode");
		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.maximumbalance"),prodCode); 


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
//SIT Test case: Per C2S Txn MinimumAmount
	@Test(dataProvider = "categoryData")
	public void j_Neg_CategoryLevelTCP_BlankC2SMinimumAmount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank Per C2S transaction Minimum Amount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MinimumBalance1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MinimumBalance1"));

		 TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		 String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		 String prodCode = TCP_CatLevel_Map.get("prodCode");
		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod", 
				MessagesDAO.getLabelByKey("error.profile.transferProfileDetail.label.min"),prodCode); 


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}


		

	}


//SIT Test Case; Per C2S Txn Max Amount
	@Test(dataProvider = "categoryData")
	public void k_Neg_CategoryLevelTCP_BlankC2SMaxBalance(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank Per C2S transaction Maximum Amount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MaximumBalance1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MaximumBalance1"));

		 TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		 String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		 String prodCode = TCP_CatLevel_Map.get("prodCode");
		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod", 
				MessagesDAO.getLabelByKey("error.profile.transferProfileDetail.label.max"),prodCode); 


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}



	
//SIT Test case: Blank Alerting Value	
	@Test(dataProvider = "categoryData")
	public void l_Neg_CategoryLevelTCP_BlankAlertingBalance(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank Alerting Balance");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("AlertingBalance1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("AlertingBalance1"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String prodCode = TCP_CatLevel_Map.get("prodCode");
		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.alertingbalance"),prodCode); 


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

//SIT Test Case: Blank Allowed Max Percentage
	@Test(dataProvider = "categoryData")
	public void m_Neg_CategoryLevelTCP_BlankAllowedMaxPct(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank Allowed Max Percentage");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("AllowedMaxPercentage1", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("AllowedMaxPercentage1"));
		

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String prodCode = TCP_CatLevel_Map.get("prodCode");
		String message = MessagesDAO.prepareMessageByKey("error.profile.transferprofiledetail.requiredforprod", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.maxpercentage"),prodCode); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
/*
 * SIT Validation Test cases for Daily Count and Values
 */
	
	
	@Test(dataProvider = "categoryData")
	public void n1_Neg_CategoryLevelTCP_BlankDailyInCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailyInCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyInCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyInCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferincount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	@Test(dataProvider = "categoryData")
	public void n2_Neg_CategoryLevelTCP_BlankDailyInAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailyInAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyInAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyInAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferinaltcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider = "categoryData")
	public void n3_Neg_CategoryLevelTCP_BlankDailyInTransferValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailyInTransferValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyInTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyInTransferValue"));

		 TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		 String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferinvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void n4_Neg_CategoryLevelTCP_BlankDailyInAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailyInAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyInAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyInAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferinaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	@Test(dataProvider = "categoryData")
	public void n5_Neg_CategoryLevelTCP_BlankDailyOutCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailyOutCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferoutcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	@Test(dataProvider = "categoryData")
	public void n6_Neg_CategoryLevelTCP_BlankDailyOutAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailyOutAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyOutAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferoutaltcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void n7_Neg_CategoryLevelTCP_BlankDailyOutTransferValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailyOutTransferValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyOutTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyOutTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferoutvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void n8_Neg_CategoryLevelTCP_BlankDailyOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailyOutAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailyOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailyOutAlertingValue"));

		 TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		 String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailytransferoutaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void n9_Neg_CategoryLevelTCP_BlankDailySubscriberInCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailySubscriberInCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberInCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberInCount"));

       TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

       String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferincount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	@Test(dataProvider = "categoryData")
	public void n10_Neg_CategoryLevelTCP_BlankDailySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailySubscriberInAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberInAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberInAlertingCount"));

		 TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		 String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferinaltcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	
	@Test(dataProvider = "categoryData")
	public void n11_Neg_CategoryLevelTCP_BlankDailySubscriberInValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailySubscriberTransferInValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberTransferInValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferInValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferinvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void n12_Neg_CategoryLevelTCP_BlankDailySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailySubscriberTransferInAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberTransferInAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferInAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferinaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	@Test(dataProvider = "categoryData")
	public void n13_Neg_CategoryLevelTCP_BlankDailySubscriberOutCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailySubscriberOutCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberTransferOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferOutCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferoutcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void n14_Neg_CategoryLevelTCP_BlankDailySubscriberOutAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailySubscriberTransferOutAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberTransferOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferOutAlertingCount"));

		 TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		 String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferoutaltcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	@Test(dataProvider = "categoryData")
	public void n15_Neg_CategoryLevelTCP_BlankDailySubscriberOutValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailySubscriberTransferOutValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberTransferOutValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferOutValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferoutvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	
	@Test(dataProvider = "categoryData")
	public void n16_Neg_CategoryLevelTCP_BlankDailySubscriberOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank DailySubscriberTransferOutAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("DailySubscriberTransferOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("DailySubscriberTransferOutAlertingValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.dailysubscribertransferoutaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	



	
	
	
	
	
	
	
	
	
	
	
/*
 * SIT Validation Test cases for Weekly Count and Values
 */
	

	@Test(dataProvider = "categoryData")
	public void o1_Neg_CategoryLevelTCP_BlankWeeklyInCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklyInCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyInCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyInCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferincount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	@Test(dataProvider = "categoryData")
	public void o2_Neg_CategoryLevelTCP_BlankWeeklyInAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklyInAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyInAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyInAlertingCount"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferinaltcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider = "categoryData")
	public void o3_Neg_CategoryLevelTCP_BlankWeeklyInTransferValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklyInTransferValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyInTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyInTransferValue"));

		TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferinvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void o4_Neg_CategoryLevelTCP_BlankWeeklyInAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklyInAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyInAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyInAlertingValue"));

		 TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		 String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferinaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	@Test(dataProvider = "categoryData")
	public void o5_Neg_CategoryLevelTCP_BlankWeeklyOutCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklyOutCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyOutCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferoutcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	@Test(dataProvider = "categoryData")
	public void o6_Neg_CategoryLevelTCP_BlankWeeklyOutAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklyOutAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyOutAlertingCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferoutaltcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void o7_Neg_CategoryLevelTCP_BlankWeeklyOutTransferValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklyOutTransferValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyOutTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyOutTransferValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferoutvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void o8_Neg_CategoryLevelTCP_BlankWeeklyOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklyOutAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklyOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklyOutAlertingValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklytransferoutaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void o9_Neg_CategoryLevelTCP_BlankWeeklySubscriberInCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklySubscriberInCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberInCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberInCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);
		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");

		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferincount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	@Test(dataProvider = "categoryData")
	public void o10_Neg_CategoryLevelTCP_BlankWeeklySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklySubscriberInAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberInAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberInAlertingCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferinaltcount")); 
        Log.info(message);

        Validator.messageCompare(actual, message);

	}

	
	
	
	
	@Test(dataProvider = "categoryData")
	public void o11_Neg_CategoryLevelTCP_BlankWeeklySubscriberInValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklySubscriberTransferInValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberTransferInValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferInValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferinvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void o12_Neg_CategoryLevelTCP_BlankWeeklySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklySubscriberTransferInAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberTransferInAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferInAlertingValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferinaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	@Test(dataProvider = "categoryData")
	public void o13_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklySubscriberOutCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferoutcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void o14_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklySubscriberTransferOutAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferoutaltcount")); 
        Log.info(message);

        Validator.messageCompare(actual, message);

	}
	
	
	
	@Test(dataProvider = "categoryData")
	public void o15_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklySubscriberTransferOutValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferoutvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	
	@Test(dataProvider = "categoryData")
	public void o16_Neg_CategoryLevelTCP_BlankWeeklySubscriberOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank WeeklySubscriberTransferOutAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("WeeklySubscriberTransferOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("WeeklySubscriberTransferOutAlertingValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.weeklysubscribertransferoutaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	

	
	
	
	
	
/*
 * SIT Validation Test cases for Monthly Count and Values
 */
		
	@Test(dataProvider = "categoryData")
	public void p1_Neg_CategoryLevelTCP_BlankMonthlyInCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlyInCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyInCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyInCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferincount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	@Test(dataProvider = "categoryData")
	public void p2_Neg_CategoryLevelTCP_BlankMonthlyInAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlyInAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyInAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyInAlertingCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferinaltcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider = "categoryData")
	public void p3_Neg_CategoryLevelTCP_BlankMonthlyInTransferValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlyInTransferValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyInTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyInTransferValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferinvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void p4_Neg_CategoryLevelTCP_BlankMonthlyInAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlyInAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyInAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyInAlertingValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferinaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	@Test(dataProvider = "categoryData")
	public void p5_Neg_CategoryLevelTCP_BlankMonthlyOutCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlyOutCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyOutCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferoutcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	@Test(dataProvider = "categoryData")
	public void p6_Neg_CategoryLevelTCP_BlankMonthlyOutAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlyOutAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyOutAlertingCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferoutaltcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void p7_Neg_CategoryLevelTCP_BlankMonthlyOutTransferValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlyOutTransferValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyOutTransferValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyOutTransferValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferoutvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void p8_Neg_CategoryLevelTCP_BlankMonthlyOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlyOutAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlyOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlyOutAlertingValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlytransferoutaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void p9_Neg_CategoryLevelTCP_BlankMonthlySubscriberInCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlySubscriberInCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberInCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberInCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferincount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	@Test(dataProvider = "categoryData")
	public void p10_Neg_CategoryLevelTCP_BlankMonthlySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlySubscriberInAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberInAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberInAlertingCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferinaltcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	
	@Test(dataProvider = "categoryData")
	public void p11_Neg_CategoryLevelTCP_BlankMonthlySubscriberInValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlySubscriberTransferInValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberTransferInValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferInValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferinvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void p12_Neg_CategoryLevelTCP_BlankMonthlySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlySubscriberTransferInAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberTransferInAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferInAlertingValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferinaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	@Test(dataProvider = "categoryData")
	public void p13_Neg_CategoryLevelTCP_BlankMonthlySubscriberOutCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlySubscriberOutCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferoutcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void p14_Neg_CategoryLevelTCP_BlankMonthlySubscriberOutAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlySubscriberTransferOutAlertingCount");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutAlertingCount", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingCount"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferoutaltcount")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void p15_Neg_CategoryLevelTCP_BlankMonthlySubscriberOutValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlySubscriberTransferOutValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferoutvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	@Test(dataProvider = "categoryData")
	public void p16_Neg_CategoryLevelTCP_BlankMonthlySubscriberTransferOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering blank MonthlySubscriberTransferOutAlertingValue");
		currentNode.assignCategory("SIT");

		TCP_CatLevel_Map=mp1.DataMap_TCPCategoryLevel();
		TCP_CatLevel_Map.put("MonthlySubscriberTransferOutAlertingValue", "");

		Log.info("Value:: "+TCP_CatLevel_Map.get("MonthlySubscriberTransferOutAlertingValue"));

		  TransferControlProfile.CategoryLevelTCP_SITValidations(TCP_CatLevel_Map, rowNum, domainName, categoryName);

		  String actual = TCP_CatLevel_Map.get("ACTUALMESSAGE");
		String message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("profile.transferProfileDetail.label.monthlysubscribertransferoutaltvalue")); 
        Log.info(message);

		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	


/*
 * Daily-Weekly Validations	
 */
	
	
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void q_01_Neg_CategoryLevelTCP_DailyWeeklyTransferInCountValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering Daily TransferInCount greater than Weekly Transfer In Count");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}		

	
	@Test(dataProvider = "categoryData")
	public void q_02_Neg_CategoryLevelTCP_DailyWeeklyTransferInAlertingCountValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering Daily TransferInAlertingCount greater than Weekly Transfer Alerting In Count");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	


	
	@Test(dataProvider = "categoryData")
	public void q_03_Neg_CategoryLevelTCP_DailyWeeklyWeeklyInTransferValueValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering Daily InTransferValue greater than WeeklyInTransferValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	@Test(dataProvider = "categoryData")
	public void q_04_Neg_CategoryLevelTCP_DailyWeeklyInAlertingValueValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyInAlertingValue greater than WeeklyInAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	@Test(dataProvider = "categoryData")
	public void q_05_Neg_CategoryLevelTCP_DailyWeeklyOutCountValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyOutCount greater than WeeklyOutCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	@Test(dataProvider = "categoryData")
	public void q_06_Neg_CategoryLevelTCP_DailyWeeklyOutAlertingCountn(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyOutAlertingCount greater than WeeklyOutAlertingCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void q_07_Neg_CategoryLevelTCP_DailyWeeklyOutTransferValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyOutTransferValue greater than WeeklyOutTransferValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void q_08_Neg_CategoryLevelTCP_DailyWeeklyOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyOutAlertingValue greater than WeeklyOutAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider = "categoryData")
	public void q_09_Neg_CategoryLevelTCP_DailyWeeklySubscriberInCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberInCount greater than WeeklySubscriberInCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void q_10_Neg_CategoryLevelTCP_DailyWeeklySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberInAlertingCount greater than WeeklySubscriberInAlertingCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void q_11_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferInValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberTransferInValue greater than WeeklySubscriberTransferInValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	
	@Test(dataProvider = "categoryData")
	public void q_12_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyWSubscriberTransferInAlertingValue greater than WeeklySubscriberTransferInAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	@Test(dataProvider = "categoryData")
	public void q_13_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberTransferOutCount greater than WeeklySubscriberTransferOutCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}



	
	@Test(dataProvider = "categoryData")
	public void q_14_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberTransferOutAlertingCount greater than WeeklySubscriberTransferOutAlertingCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void q_15_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberTransferOutValue greater than WeeklySubscriberTransferOutValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider = "categoryData")
	public void q_16_Neg_CategoryLevelTCP_DailyWeeklySubscriberTransferOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberTransferOutAlertingValue greater than WeeklySubscriberTransferOutAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	

	
	
	/*
	 * Daily Monthly Validations
	 */
	
	
	

	
	
	
	@Test(dataProvider = "categoryData")
	public void r_01_Neg_CategoryLevelTCP_DailyMonthlyTransferInCountValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering Daily TransferInCount greater than Monthly Transfer In Count");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}		

	
	@Test(dataProvider = "categoryData")
	public void r_02_Neg_CategoryLevelTCP_DailyMonthlyTransferInAlertingCountValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering Daily TransferInAlertingCount greater than Monthly Transfer Alerting In Count");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	


	
	@Test(dataProvider = "categoryData")
	public void r_03_Neg_CategoryLevelTCP_DailyMonthlyMonthlyInTransferValueValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering Daily InTransferValue greater than MonthlyInTransferValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	@Test(dataProvider = "categoryData")
	public void r_04_Neg_CategoryLevelTCP_DailyMonthlyInAlertingValueValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyInAlertingValue greater than MonthlyInAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	@Test(dataProvider = "categoryData")
	public void r_05_Neg_CategoryLevelTCP_DailyMonthlyOutCountValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyOutCount greater than MonthlyOutCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	@Test(dataProvider = "categoryData")
	public void r_06_Neg_CategoryLevelTCP_DailyMonthlyOutAlertingCountn(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyOutAlertingCount greater than MonthlyOutAlertingCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void r_07_Neg_CategoryLevelTCP_DailyMonthlyOutTransferValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyOutTransferValue greater than MonthlyOutTransferValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void r_08_Neg_CategoryLevelTCP_DailyMonthlyOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyOutAlertingValue greater than MonthlyOutAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider = "categoryData")
	public void r_09_Neg_CategoryLevelTCP_DailyMonthlySubscriberInCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberInCount greater than MonthlySubscriberInCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void r_10_Neg_CategoryLevelTCP_DailyMonthlySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberInAlertingCount greater than MonthlySubscriberInAlertingCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void r_11_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferInValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberTransferInValue greater than MonthlySubscriberTransferInValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	
	@Test(dataProvider = "categoryData")
	public void r_12_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailyWSubscriberTransferInAlertingValue greater than MonthlySubscriberTransferInAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	@Test(dataProvider = "categoryData")
	public void r_13_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberTransferOutCount greater than MonthlySubscriberTransferOutCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}



	
	@Test(dataProvider = "categoryData")
	public void r_14_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberTransferOutAlertingCount greater than MonthlySubscriberTransferOutAlertingCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void r_15_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberTransferOutValue greater than MonthlySubscriberTransferOutValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider = "categoryData")
	public void r_16_Neg_CategoryLevelTCP_DailyMonthlySubscriberTransferOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering DailySubscriberTransferOutAlertingValue greater than MonthlySubscriberTransferOutAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	
/*
 * Weekly-Monthly Validations	
 */
	
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void s_01_Neg_CategoryLevelTCP_weeklyMonthlyTransferInCountValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weekly TransferInCount greater than Monthly Transfer In Count");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}		

	
	@Test(dataProvider = "categoryData")
	public void s_02_Neg_CategoryLevelTCP_weeklyMonthlyTransferInAlertingCountValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weekly TransferInAlertingCount greater than Monthly Transfer Alerting In Count");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	


	
	@Test(dataProvider = "categoryData")
	public void s_03_Neg_CategoryLevelTCP_weeklyMonthlyMonthlyInTransferValueValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weekly InTransferValue greater than MonthlyInTransferValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	@Test(dataProvider = "categoryData")
	public void s_04_Neg_CategoryLevelTCP_weeklyMonthlyInAlertingValueValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklyInAlertingValue greater than MonthlyInAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}
	
	
	
	@Test(dataProvider = "categoryData")
	public void s_05_Neg_CategoryLevelTCP_weeklyMonthlyOutCountValidation(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklyOutCount greater than MonthlyOutCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	
	
	@Test(dataProvider = "categoryData")
	public void s_06_Neg_CategoryLevelTCP_weeklyMonthlyOutAlertingCountn(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklyOutAlertingCount greater than MonthlyOutAlertingCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void s_07_Neg_CategoryLevelTCP_weeklyMonthlyOutTransferValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklyOutTransferValue greater than MonthlyOutTransferValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	
	
	
	
	
	@Test(dataProvider = "categoryData")
	public void s_08_Neg_CategoryLevelTCP_weeklyMonthlyOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklyOutAlertingValue greater than MonthlyOutAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider = "categoryData")
	public void s_09_Neg_CategoryLevelTCP_weeklyMonthlySubscriberInCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklySubscriberInCount greater than MonthlySubscriberInCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void s_10_Neg_CategoryLevelTCP_weeklyMonthlySubscriberInAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklySubscriberInAlertingCount greater than MonthlySubscriberInAlertingCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void s_11_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferInValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklySubscriberTransferInValue greater than MonthlySubscriberTransferInValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	
	@Test(dataProvider = "categoryData")
	public void s_12_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferInAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklyWSubscriberTransferInAlertingValue greater than MonthlySubscriberTransferInAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	
	@Test(dataProvider = "categoryData")
	public void s_13_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklySubscriberTransferOutCount greater than MonthlySubscriberTransferOutCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}



	
	@Test(dataProvider = "categoryData")
	public void s_14_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutAlertingCount(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklySubscriberTransferOutAlertingCount greater than MonthlySubscriberTransferOutAlertingCount");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	
	@Test(dataProvider = "categoryData")
	public void s_15_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklySubscriberTransferOutValue greater than MonthlySubscriberTransferOutValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}


	@Test(dataProvider = "categoryData")
	public void s_16_Neg_CategoryLevelTCP_weeklyMonthlySubscriberTransferOutAlertingValue(int rowNum,String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());
		Map_TCPValues mp1 = new Map_TCPValues();

		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);


		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]Transfer Control Profile");
			TestCaseCounter = true;
		}

		currentNode = test.createNode("To verify that proper Error message is getting displayed on entering weeklySubscriberTransferOutAlertingValue greater than MonthlySubscriberTransferOutAlertingValue");
		currentNode.assignCategory("SIT");

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


		if (actual.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

	}

	

}
