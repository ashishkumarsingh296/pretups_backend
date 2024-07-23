package com.testscripts.sit;

import java.util.HashMap;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.Features.C2STransferRules;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.pageobjects.networkadminpages.c2stransferrule.AddC2STransferRulePage1;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_AddC2STransferRule extends BaseTest {

	static boolean TestCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;
	Map<String, String> dataMap;
	AddC2STransferRulePage1 addC2STransferRulePage1 = new AddC2STransferRulePage1(driver);
	@BeforeMethod
	public void testData() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);

		dataMap = new HashMap<String, String>();
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {

			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && !FromCategory.equals("Subscriber")) {
				dataMap.put("fromDomain", ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i));
				dataMap.put("fromCategory", ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
				dataMap.put("services", ExcelUtility.getCellData(0, ExcelI.SERVICES, i));
				dataMap.put("requestBearer", ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i));
				break;
			}
		}

	}

	// Domain is null
	@Test
	public void a_addC2STransferRules() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Add C2S Transfer Rules");
			TestCaseCounter = true;
		}

		currentNode = test.createNode(
				"To verify that Network Admin is Unable to create C2S Transfer Rule when Domain is not selected.");
		currentNode.assignCategory("SIT");
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		dataMap.put("fromDomain", "");
		dataMap.put("fromCategory", "ALL");
		dataMap.put("fromGrade", "ALL");
		try {
			c2STransferRules.addC2STransferRule_SIT(dataMap);
		} catch (Exception e) {
			String ExpectedMessage = dataMap.get("message");
			String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.sendertyperequired","1");
			Validator.messageCompare(message, ExpectedMessage);
		}
		
		String ExpectedMessage = dataMap.get("message");
		String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.sendertyperequired","1");
		Validator.messageCompare(message, ExpectedMessage);

		Log.endTestCase(this.getClass().getName());
	}
	
	// All null
		@Test
		public void b_addC2STransferRules() {
			Log.startTestCase(this.getClass().getName());
			if (TestCaseCounter == false) {
				test = extent.createTest("[SIT]Add C2S Transfer Rules");
				TestCaseCounter = true;
			}

			currentNode = test.createNode(
					"To verify that Network Admin is Unable to create C2S Transfer Rule when Nothing is selected.");
			currentNode.assignCategory("SIT");
			C2STransferRules c2STransferRules = new C2STransferRules(driver);
				String actualMessage = c2STransferRules.addNull(dataMap);
				String message = MessagesDAO.prepareMessageByKey("trfrule.addtrfrule.error.selectdata");
				Validator.messageCompare(message, actualMessage);
			

			Log.endTestCase(this.getClass().getName());
		}
		
		// Category is null
		@Test
		public void c_addC2STransferRules() {
			Log.startTestCase(this.getClass().getName());
			if (TestCaseCounter == false) {
				test = extent.createTest("[SIT]Add C2S Transfer Rules");
				TestCaseCounter = true;
			}

			currentNode = test.createNode(
					"To verify that Network Admin is Unable to create C2S Transfer Rule when Category is not selected.");
			currentNode.assignCategory("SIT");
			C2STransferRules c2STransferRules = new C2STransferRules(driver);
			dataMap.put("fromCategory", "");
			dataMap.put("fromGrade", "ALL");
			try {
				c2STransferRules.addC2STransferRule_SIT(dataMap);
				String ExpectedMessage = dataMap.get("message");
				String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.categorycodereq","1");
				Validator.messageCompare(message, ExpectedMessage);
			} catch (Exception e) {
				String ExpectedMessage = addC2STransferRulePage1.checkForError();
				String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.categorycodereq","1");
				Validator.messageCompare(message, ExpectedMessage);
			}
		
			Log.endTestCase(this.getClass().getName());
		}
		
		// Grade is null
				@Test
				public void d_addC2STransferRules() {
					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
						test = extent.createTest("[SIT]Add C2S Transfer Rules");
						TestCaseCounter = true;
					}

					currentNode = test.createNode(
							"To verify that Network Admin is Unable to create C2S Transfer Rule when Grade is not selected.");
					currentNode.assignCategory("SIT");
					C2STransferRules c2STransferRules = new C2STransferRules(driver);
					dataMap.put("fromGrade", "");
					try {
						c2STransferRules.addC2STransferRule_SIT(dataMap);
						String ExpectedMessage = dataMap.get("message");
						String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.gradecodereq","1");
						Validator.messageCompare(message, ExpectedMessage);
					} catch (Exception e) {
						String ExpectedMessage = addC2STransferRulePage1.checkForError();
						String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.gradecodereq","1");
						Validator.messageCompare(message, ExpectedMessage);
					}

					Log.endTestCase(this.getClass().getName());
				}
				
				// Receiver Type is not selected
				@Test
				public void e_addC2STransferRules() {
					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
						test = extent.createTest("[SIT]Add C2S Transfer Rules");
						TestCaseCounter = true;
					}

					currentNode = test.createNode(
							"To verify that Network Admin is Unable to create C2S Transfer Rule when Receiver Type is not selected.");
					currentNode.assignCategory("SIT");
					C2STransferRules c2STransferRules = new C2STransferRules(driver);
						c2STransferRules.receiverTypeNotSelected(dataMap);
						String ExpectedMessage = c2STransferRules.receiverTypeNotSelected(dataMap);
						String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.receiverrequired","1");
						Validator.messageCompare(message, ExpectedMessage);
					

					Log.endTestCase(this.getClass().getName());
				}
		
		// Service Type is not selected
		@Test
		public void f_addC2STransferRules() {
			Log.startTestCase(this.getClass().getName());
			if (TestCaseCounter == false) {
				test = extent.createTest("[SIT]Add C2S Transfer Rules");
				TestCaseCounter = true;
			}

			currentNode = test.createNode(
					"To verify that Network Admin is Unable to create C2S Transfer Rule when Service Type is not selected.");
			currentNode.assignCategory("SIT");
			C2STransferRules c2STransferRules = new C2STransferRules(driver);
				String ExpectedMessage = c2STransferRules.serviceTypeNotSelected(dataMap);
				String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.subservicetyperequired","1");
				Validator.messageCompare(message, ExpectedMessage);
			

			Log.endTestCase(this.getClass().getName());
		}
				
		



}
