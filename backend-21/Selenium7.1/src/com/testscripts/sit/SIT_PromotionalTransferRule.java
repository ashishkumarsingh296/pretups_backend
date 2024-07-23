package com.testscripts.sit;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.HashMap;

import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.Features.PromotionalTransferRule;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class SIT_PromotionalTransferRule extends BaseTest {

	static boolean TestCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;
	Map<String,String> dataMap;

	@BeforeClass
	public void testData() throws SQLException {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
		dataMap = new HashMap<String, String>();
		dataMap.put("domain", ExcelUtility.getCellData(0, "DOMAIN_NAME", 1));
		dataMap.put("category", ExcelUtility.getCellData(0, "CATEGORY_NAME", 1));
		dataMap.put("userName", ExcelUtility.getCellData(0, "USER_NAME", 1));
		dataMap.put("grade", ExcelUtility.getCellData(0, "GRADE", 1));
		ExcelUtility.setExcelFile(MasterSheetPath, "Transfer Rule Sheet");
		int rowCountTransfer = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCountTransfer; i++) {
			String fromCategory = ExcelUtility.getCellData(0, "FROM_CATEGORY", i);
			String toCategory = ExcelUtility.getCellData(0, "TO_CATEGORY", i);
			if ((fromCategory.equals(dataMap.get("category")) && toCategory.equals("Subscriber"))) {
				dataMap.put("serviceCode", ExcelUtility.getCellData(0, "SERVICES", i));
				break;
			}
		}

		ExcelUtility.setExcelFile(MasterSheetPath, "C2S Services Sheet");
		int rowCountService = ExcelUtility.getRowCount();
		for (int j = 1; j <= rowCountService; j++) {
			String serviceType = ExcelUtility.getCellData(0, "SERVICE_TYPE", j);
			String serviceName = ExcelUtility.getCellData(0, "NAME", j);
			String subServiceName = ExcelUtility.getCellData(0, "SELECTOR_NAME", j);
			String cardGroupName = ExcelUtility.getCellData(0, "PROMO_CARDGROUP", j);
			if (serviceType.equals(dataMap.get("serviceCode")) && !serviceName.isEmpty() && !subServiceName.isEmpty() && !cardGroupName.isEmpty()) {
				dataMap.put("serviceName", serviceName);
				dataMap.put("subServiceName", subServiceName);
				dataMap.put("cardGroup", cardGroupName);
				break;
			}
		}
		

		ExcelUtility.setExcelFile(MasterSheetPath, "Geographical Domains");
		dataMap.put("geoDomainType", ExcelUtility.getCellData(0, "DOMAIN_TYPE_NAME", 1));
		dataMap.put("geoDomainName", ExcelUtility.getCellData(0, "DOMAIN_NAME", 1));

		dataMap.put("type", "Date range");
		dataMap.put("TimeType", "Time range");
		dataMap.put("slabType", "Single");
		dataMap.put("MultipleSlabType", "Multiple");
		String productType = "PROMO";
		
		ResultSet queryResult = DBHandler.AccessHandler.getProductNameByType(productType);
		queryResult.last();
		int size = queryResult.getRow();
		queryResult.beforeFirst();
		String[] promotionalLevel = new String[size];
		int i = 0;
		while (queryResult.next()) {
			promotionalLevel[i] = queryResult.getString("LOOKUP_NAME");
			i++;
		}
		dataMap.put("promotionalLevel", promotionalLevel[0]);

	}

	// Promotional Level is null
	@Test
	public void a_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when Promotional Level is not selected.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.a_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.promotionleve");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// Domain is null
	@Test
	public void b_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when Domain is not selected.");
		currentNode.assignCategory("SIT");
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.b_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domain");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// Geographical domain is null
	@Test
	public void c_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when Geographical Domain Type is not selected.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.c_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domaintype");
		if (result.equals(message) && result != null)
			currentNode.log(Status.PASS, "Message Validation Successful");
		else if(result == null){
			currentNode.log(Status.SKIP, "Message Validation Skipped");
		}
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// Grade is null
	@Test
	public void d_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when Grade is not selected.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.d_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.grade");
		if (message.equals(result))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else if(result == null){
			currentNode.log(Status.SKIP, "Message Validation Skipped");
		}
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// User is null
	@Test
	public void e_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when User is not selected.");
		currentNode.assignCategory("SIT");
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.e_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.user");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// Receiver Type is null
	@Test
	public void f_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when Type under Receiver is not selected.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.f_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.error.selectdata");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	 // serviceType is null
	@Test
	public void g_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when serviceType is not selected.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.g_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addc2stransferrules.error.servicetype","1");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	

	// fromDate is null
	@Test
	public void j_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when fromDate is not selected.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.j_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.fromdate", "1");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// fromTime is null
	@Test
	public void k_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when fromTime is not selected.");
		currentNode.assignCategory("SIT");
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.k_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.fromtime", "1");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// tillDate is null
	@Test
	public void l_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when tillDate is not selected.");
		currentNode.assignCategory("SIT");
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.l_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.tilldate", "1");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// tillTime is null
	@Test
	public void m_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when tillTime is not selected.");
		currentNode.assignCategory("SIT");
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.m_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.tilltime", "1");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}




	// fromdateformat notValid
	@Test
	public void n_promotionalTransferRuleCreation(){
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when from date format is not valid.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.n_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.fromdateformat", "1");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// fromTimeformat notValid
	@Test
	public void o_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when from time format is not valid.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.o_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.fromtimefromat", "1");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// tillDateformat notValid
	@Test
	public void p_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when till date format is not valid.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.p_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.tilldateformat", "1");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}
	
	// tillTimeformat notValid
		@Test
		public void q_promotionalTransferRuleCreation() {
			Log.startTestCase(this.getClass().getName());
			if (TestCaseCounter == false) {
				test = extent.createTest("[SIT]Promotional Transfer Rule");
				TestCaseCounter = true;
			}
			currentNode = test.createNode(
					"To verify that Network Admin is Unable to create Promotional Transfer Rule when till time format is not valid.");
			currentNode.assignCategory("SIT");
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.q_addPromotionalTransferRule(dataMap);
			String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.tilltimefromat", "1");
			if (result.equals(message))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}

			Log.endTestCase(this.getClass().getName());
		}
		
		

	// from date time less than current date time
	@Test
	public void r_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when applicable from date and time is less than current Date and Time.");
		currentNode.assignCategory("SIT");
		
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.r_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO
				.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.fromdatetimeerror", "1");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// till date time less than current date time
	@Test
	public void s_promotionalTransferRuleCreation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create Promotional Transfer Rule when applicable till date and time is less than current Date and Time.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.s_addPromotionalTransferRule(dataMap);
		String message = MessagesDAO
				.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.tilldatetimeerror", "1");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}


       // Positive Test Case for Date Range
		@Test
		public void t_promotionalTransferRuleCreation() {
			Log.startTestCase(this.getClass().getName());
			if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
			currentNode = test.createNode(
					"To verify that Network Admin is Able to create Promotional Transfer Rule when  valid data for all fields is given.");
			currentNode.assignCategory("SIT");
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.t_addPromotionalTransferRule(dataMap);
			String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
			String message2 = MessagesDAO.prepareMessageByKey("promotrfrule.operation.msg.alreadyexist", "1");
			if (result.equals(message1))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else if (result.equals(message2))
			currentNode.log(Status.SKIP, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + message1 + "] or [" + message2 + "] but found [" + result + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}

			Log.endTestCase(this.getClass().getName());
		}
		
		// multipleTimeSlab is null
		@Test
		public void u_promotionalTransferRuleCreation() {
			Log.startTestCase(this.getClass().getName());
			if (TestCaseCounter == false) {
				test = extent.createTest("[SIT]Promotional Transfer Rule");
				TestCaseCounter = true;
			}
			currentNode = test.createNode(
					"To verify that Network Admin is Unable to create Promotional Transfer Rule when multiple time slab is null");
			currentNode.assignCategory("SIT");
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.u_addPromotionalTransferRule(dataMap);
			String message = MessagesDAO
					.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.multipleTimeSlabrequired");
			if (result.equals(message))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}

			Log.endTestCase(this.getClass().getName());
		}
		
		// Empty Slabs 
		@Test
		public void v_promotionalTransferRuleCreation() throws InterruptedException {
			Log.startTestCase(this.getClass().getName());
			if (TestCaseCounter == false) {
				test = extent.createTest("[SIT]Promotional Transfer Rule");
				TestCaseCounter = true;
			}
			currentNode = test.createNode(
					"To verify that Network Admin is Unable to create Promotional Transfer Rule when all slabs are empty.");
			currentNode.assignCategory("SIT");
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.v_addPromotionalTransferRule(dataMap);
			String message = MessagesDAO
					.prepareMessageByKey("promotrfrule.addmultipletimeslabs.error.slabentryreq");
			if (result.equals(message))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}

			Log.endTestCase(this.getClass().getName());
		}
		
		// Same Start and End Time Slab
				@Test
				public void w_promotionalTransferRuleCreation() throws InterruptedException {
					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
						test = extent.createTest("[SIT]Promotional Transfer Rule");
						TestCaseCounter = true;
					}
					currentNode = test.createNode(
							"To verify that Network Admin is Unable to create Promotional Transfer Rule when start and end time is same.");
					currentNode.assignCategory("SIT");
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.w_addPromotionalTransferRule(dataMap);
					String message = MessagesDAO
							.prepareMessageByKey("promotrfrule.addmultipleslab.error.endtimefromtimeequal", "1");
					if (result.equals(message))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(this.getClass().getName());
				}
				
			// Same Start and End Time Slab for two rows
				@Test
				public void x_promotionalTransferRuleCreation() throws InterruptedException {
					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
						test = extent.createTest("[SIT]Promotional Transfer Rule");
						TestCaseCounter = true;
					}
					currentNode = test.createNode(
							"To verify that Network Admin is Unable to create Promotional Transfer Rule when start and end time is same for two different rows.");
					currentNode.assignCategory("SIT");
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.x_addPromotionalTransferRule(dataMap);
					String message = MessagesDAO
							.prepareMessageByKey("promotrfrule.addmultipletimeslabs.error.fromtimeinnextrow", "2", "1");
					if (result.equals(message))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(this.getClass().getName());
				}
				
				//  Start Time is less than  End Time  for previous slab
				@Test
				public void y_promotionalTransferRuleCreation() throws InterruptedException {
					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
						test = extent.createTest("[SIT]Promotional Transfer Rule");
						TestCaseCounter = true;
					}
					currentNode = test.createNode(
							"To verify that Network Admin is Unable to create Promotional Transfer Rule when Start Time is less than  End Time  for previous slab");
					currentNode.assignCategory("SIT");
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.y_addPromotionalTransferRule(dataMap);
					String message = MessagesDAO
							.prepareMessageByKey("promotrfrule.addmultipletimeslabs.error.fromtimeinnextrow", "2", "1");
					if (result.equals(message))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(this.getClass().getName());
				}
				
			// Positive flow for Single Time Slab 
				@Test
				public void z_promotionalTransferRuleCreation() {
					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
					test = extent.createTest("[SIT]Promotional Transfer Rule");
					TestCaseCounter = true;
				}
					currentNode = test.createNode(
							"To verify that Network Admin is Able to create Promotional Transfer Rule when  valid data for all fields is given for time slab.");
					currentNode.assignCategory("SIT");
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.z_addPromotionalTransferRule(dataMap);
					String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
					String message2 = MessagesDAO.prepareMessageByKey("promotrfrule.operation.msg.alreadyexist", "1");
					if (result.equals(message1))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else if (result.equals(message2))
					currentNode.log(Status.SKIP, "Message Validation Successful");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message1 + "] or [" + message2 + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(this.getClass().getName());
				}
		
			// Positive flow for Multiple Time Slab 
				@Test
				public void za_promotionalTransferRuleCreation() throws InterruptedException {
					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
					test = extent.createTest("[SIT]Promotional Transfer Rule");
					TestCaseCounter = true;
				}
					currentNode = test.createNode(
							"To verify that Network Admin is Able to create Promotional Transfer Rule when  valid data for all fields is given for multiple time slab.");
					currentNode.assignCategory("SIT");
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.za_addPromotionalTransferRule(dataMap);
					String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addtrfrule.msg.success");
					String message2 = MessagesDAO.prepareMessageByKey("promotrfrule.operation.msg.alreadyexist", "1");
					if (result.equals(message1))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else if (result.equals(message2))
					currentNode.log(Status.SKIP, "Message Validation Successful");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message1 + "] or [" + message2 + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(this.getClass().getName());
				}
		
	// View Promotional Transfer Rule Test Cases

	// Promotional Level is null
	@Test
	public void zb_viewPromotionalTransferRule() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
					test = extent.createTest("[SIT]Promotional Transfer Rule");
					TestCaseCounter = true;
				}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view Promotional Transfer Rule when Promotional Level is not selected.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.zb_viewPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.promotionleve");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}
	
// Domain is null
		@Test
		public void zc_viewPromotionalTransferRule() {
			Log.startTestCase(this.getClass().getName());
			if (TestCaseCounter == false) {
						test = extent.createTest("[SIT]Promotional Transfer Rule");
						TestCaseCounter = true;
					}
			currentNode = test.createNode(
					"To verify that Network Admin is Unable to view Promotional Transfer Rule when Domain is not selected.");
			currentNode.assignCategory("SIT");
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.zc_viewPromotionalTransferRule(dataMap);
			String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domain");
			String message2 = "Domain Field for Promotional level Geography does not exist";
			if (result.equals(message1))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else if (result.equals(message2))
				currentNode.log(Status.SKIP, "Message Validation sKIPPED");
			else {
				currentNode.log(Status.FAIL, "Expected [" + message1 + "] but found [" + result + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}

			Log.endTestCase(this.getClass().getName());
		}
		
		//Geographical domain is null
				@Test
				public void zd_viewPromotionalTransferRule() {
					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
								test = extent.createTest("[SIT]Promotional Transfer Rule");
								TestCaseCounter = true;
							}
					currentNode = test.createNode(
							"To verify that Network Admin is Unable to view Promotional Transfer Rule when Geographical Domain is not selected.");
					currentNode.assignCategory("SIT");
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.zd_viewPromotionalTransferRule(dataMap);
					String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domaintype");
					if (result.equals(message) && result != null){
						currentNode.log(Status.PASS, "Message Validation Successful");
					}
					else if(result == null)
						currentNode.log(Status.SKIP, "Message Validation Skipped");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(this.getClass().getName());
				}
				
				//Grade or User is null
				@Test
				public void ze_viewPromotionalTransferRule() {
					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
								test = extent.createTest("[SIT]Promotional Transfer Rule");
								TestCaseCounter = true;
							}
					currentNode = test.createNode(
							"To verify that Network Admin is Unable to view Promotional Transfer Rule when grade or user is not selected.");
					currentNode.assignCategory("SIT");
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					String result = promotionalTransferRule.ze_viewPromotionalTransferRule(dataMap);
					String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.grade");
					String message2 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.user");
					String message3 = "Test Case only for Promotional Level User and Grade.";
					if (result.equals(message1))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else if (result.equals(message2))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else if(result.equals(message3)) 
					currentNode.log(Status.SKIP, "Message Validation Skipped");
					else {
						currentNode.log(Status.FAIL, "Expected [" + message1 + "] or [" + message2 + "] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(this.getClass().getName());
				}
				
				//Positive Test Case for View
				@Test
				public void zf_viewPromotionalTransferRule() {
					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
								test = extent.createTest("[SIT]Promotional Transfer Rule");
								TestCaseCounter = true;
							}
					currentNode = test.createNode(
							"To verify that Network Admin is able to view Promotional Transfer Rule when all fields are  selected.");
					currentNode.assignCategory("SIT");
					PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
					boolean result = promotionalTransferRule.zf_viewPromotionalTransferRule(dataMap);
					if (result)
						currentNode.log(Status.PASS, "Message Validation Successful");
					else {
						currentNode.log(Status.FAIL, "Expected [True] but found [" + result + "]");
						currentNode.log(Status.FAIL, "Message Validation Failed");
					}

					Log.endTestCase(this.getClass().getName());
				}

			// Modify Promotional Transfer Rule Test Cases

	// Promotional Level is null
	@Test
	public void zg_modifyPromotionalTransferRule() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Promotional Transfer Rule");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to modify Promotional Transfer Rule when Promotional Level is not selected.");
		currentNode.assignCategory("SIT");
		PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
		String result = promotionalTransferRule.zg_modifyPromotionalTransferRule(dataMap);
		String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.promotionleve");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}
	
	// Domain is null
		@Test
		public void zh_modifyPromotionalTransferRule() {
			Log.startTestCase(this.getClass().getName());
			if (TestCaseCounter == false) {
				test = extent.createTest("[SIT]Promotional Transfer Rule");
				TestCaseCounter = true;
			}
			currentNode = test.createNode(
					"To verify that Network Admin is Unable to modify Promotional Transfer Rule when Domain is not selected.");
			currentNode.assignCategory("SIT");
			PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
			String result = promotionalTransferRule.zh_modifyPromotionalTransferRule(dataMap);
			String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domain");
			String message2 = "Domain Field for Promotional level Geography does not exist";
			if (result.equals(message1))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else if (result.equals(message2))
				currentNode.log(Status.SKIP, "Message Validation Skipped");
			else {
				currentNode.log(Status.FAIL, "Expected [" + message1 + "] but found [" + result + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}

			Log.endTestCase(this.getClass().getName());
		}
		

		// Geographical Domain is null
			@Test
			public void zi_modifyPromotionalTransferRule() {
				Log.startTestCase(this.getClass().getName());
				if (TestCaseCounter == false) {
					test = extent.createTest("[SIT]Promotional Transfer Rule");
					TestCaseCounter = true;
				}
				currentNode = test.createNode(
						"To verify that Network Admin is Unable to modify Promotional Transfer Rule when Geographical Domain is not selected.");
				currentNode.assignCategory("SIT");
				PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
				String result = promotionalTransferRule.zi_modifyPromotionalTransferRule(dataMap);
				String message = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.domaintype");
				if (result.equals(message))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else {
					currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
				}

				Log.endTestCase(this.getClass().getName());
			}
			
			//Grade or User is null
			@Test
			public void zj_modifyPromotionalTransferRule() {
				Log.startTestCase(this.getClass().getName());
				if (TestCaseCounter == false) {
							test = extent.createTest("[SIT]Promotional Transfer Rule");
							TestCaseCounter = true;
						}
				currentNode = test.createNode(
						"To verify that Network Admin is Unable to modify Promotional Transfer Rule when grade or user is not selected.");
				currentNode.assignCategory("SIT");
				PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
				String result = promotionalTransferRule.zj_modifyPromotionalTransferRule(dataMap);
				String message1 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.grade");
				String message2 = MessagesDAO.prepareMessageByKey("promotrfrule.addpromoc2stransferrules.error.user");
				String message3 = "Test Case only for Promotional Level User and Grade.";
				if (result.equals(message1))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else if (result.equals(message2))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else if(result.equals(message3)) 
				currentNode.log(Status.SKIP, "Message Validation Skipped");
				else {
					currentNode.log(Status.FAIL, "Expected [" + message1 + "] or [" + message2 + "] but found [" + result + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
				}

				Log.endTestCase(this.getClass().getName());
			}

			
			//When checkboxes are unchecked
			@Test
			public void zk_modifyPromotionalTransferRule() {
				Log.startTestCase(this.getClass().getName());
				if (TestCaseCounter == false) {
							test = extent.createTest("[SIT]Promotional Transfer Rule");
							TestCaseCounter = true;
						}
				currentNode = test.createNode(
						"To verify that Network Admin is Unable to modify Promotional Transfer Rule when checkbox are not selected.");
				currentNode.assignCategory("SIT");
				PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
				String result = promotionalTransferRule.zk_modifyPromotionalTransferRule(dataMap);
				String message = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.selectrow");
				if (result.equals(message))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else {
					currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
				}

				Log.endTestCase(this.getClass().getName());
			}
			
			//Positive Flow for Date Range
			@Test
			public void zl_modifyPromotionalTransferRule() {
				Log.startTestCase(this.getClass().getName());
				if (TestCaseCounter == false) {
							test = extent.createTest("[SIT]Promotional Transfer Rule");
							TestCaseCounter = true;
						}
				currentNode = test.createNode(
						"To verify that Network Admin is able to modify Promotional Transfer Rule when all fields are selected.");
				currentNode.assignCategory("SIT");
				PromotionalTransferRule promotionalTransferRule = new PromotionalTransferRule(driver);
				String result = promotionalTransferRule.zl_modifyPromotionalTransferRule(dataMap);
				String message = MessagesDAO.prepareMessageByKey("promotrfrule.modtrfrule.msg.success");
				
				if (result.equals(message))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else {
					currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
				}

				Log.endTestCase(this.getClass().getName());
			}

	
}
