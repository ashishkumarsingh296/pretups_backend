package com.testscripts.sit;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.Features.C2STransferRules;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.pageobjects.networkadminpages.c2stransferrule.AddC2STransferRulePage1;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_C2S_TRANSFER_RULE)
public class SIT_AddC2STransferRule extends BaseTest {
	NetworkAdminHomePage networkAdminHomePage;
	Map<String, String> dataMap;
	AddC2STransferRulePage1 addC2STransferRulePage1 = new AddC2STransferRulePage1(driver);
	String assignCategory="SIT";
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
	@TestManager(TestKey = "PRETUPS-882") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_addC2STransferRules() {
		final String methodName = "Test_C2S_Transfer_Rule";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2STRFRULE1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		dataMap.put("fromDomain", "");
		dataMap.put("fromCategory", "ALL");
		dataMap.put("fromGrade", "ALL");
		try {
			c2STransferRules.addC2STransferRule_SIT(dataMap);
		} catch (Exception e) {
			String ExpectedMessage = dataMap.get("message");
			String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.sendertyperequired","1");
			Assertion.assertEquals(message, ExpectedMessage);
		}
		
		String ExpectedMessage = dataMap.get("message");
		String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.sendertyperequired","1");
		Assertion.assertEquals(message, ExpectedMessage);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// All null
		@Test
		@TestManager(TestKey = "PRETUPS-883") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		public void b_addC2STransferRules() {
			final String methodName = "Test_C2S_Transfer_Rule";
			Log.startTestCase(methodName);

			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2STRFRULE2").getExtentCase());
			currentNode.assignCategory(assignCategory);
			C2STransferRules c2STransferRules = new C2STransferRules(driver);
				String actualMessage = c2STransferRules.addNull(dataMap);
				String message = MessagesDAO.prepareMessageByKey("trfrule.addtrfrule.error.selectdata");
				Assertion.assertEquals(message, actualMessage);
			
				Assertion.completeAssertions();
				Log.endTestCase(methodName);
		}
		
		// Category is null
		@Test
		@TestManager(TestKey = "PRETUPS-884") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		public void c_addC2STransferRules() {
			final String methodName = "Test_C2S_Transfer_Rule";
			Log.startTestCase(methodName);

			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2STRFRULE3").getExtentCase());
			currentNode.assignCategory(assignCategory);
			if (_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("1")){
			C2STransferRules c2STransferRules = new C2STransferRules(driver);
			dataMap.put("fromCategory", "");
			dataMap.put("fromGrade", "ALL");
			try {
				c2STransferRules.addC2STransferRule_SIT(dataMap);
				String ExpectedMessage = dataMap.get("message");
				String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.categorycodereq","1");
				Assertion.assertEquals(message, ExpectedMessage);
			} catch (Exception e) {
				String ExpectedMessage = addC2STransferRulePage1.checkForError();
				String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.categorycodereq","1");
				Assertion.assertEquals(message, ExpectedMessage);
			}}
			else{
				Assertion.assertSkip("Category is not allowed for selection.");
			}
		
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		// Grade is null
				@Test
				@TestManager(TestKey = "PRETUPS-885") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
				public void d_addC2STransferRules() {
					final String methodName = "Test_C2S_Transfer_Rule";
					Log.startTestCase(methodName);

					currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2STRFRULE4").getExtentCase());
					currentNode.assignCategory(assignCategory);
					if (_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("1")){
					C2STransferRules c2STransferRules = new C2STransferRules(driver);
					dataMap.put("fromGrade", "");
					try {
						c2STransferRules.addC2STransferRule_SIT(dataMap);
						String ExpectedMessage = dataMap.get("message");
						String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.gradecodereq","1");
						Assertion.assertEquals(message, ExpectedMessage);
					} catch (Exception e) {
						String ExpectedMessage = addC2STransferRulePage1.checkForError();
						String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.gradecodereq","1");
						Assertion.assertEquals(message, ExpectedMessage);
					}}
					else{
						Assertion.assertSkip("Grade is not allowed for selection");
					}

					Assertion.completeAssertions();
					Log.endTestCase(methodName);
				}
				
				// Receiver Type is not selected
				@Test
				@TestManager(TestKey = "PRETUPS-886") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
				public void e_addC2STransferRules() {
					final String methodName = "Test_C2S_Transfer_Rule";
					Log.startTestCase(methodName);

					currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2STRFRULE5").getExtentCase());
					currentNode.assignCategory(assignCategory);
					C2STransferRules c2STransferRules = new C2STransferRules(driver);
						c2STransferRules.receiverTypeNotSelected(dataMap);
						String ExpectedMessage = c2STransferRules.receiverTypeNotSelected(dataMap);
						String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.receiverrequired","1");
						Assertion.assertEquals(message, ExpectedMessage);

						Assertion.completeAssertions();
						Log.endTestCase(methodName);
				}
		
		// Service Type is not selected
		@Test
		@TestManager(TestKey = "PRETUPS-887") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		public void f_addC2STransferRules() {
			final String methodName = "Test_C2S_Transfer_Rule";
			Log.startTestCase(methodName);

			currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2STRFRULE6").getExtentCase());
			currentNode.assignCategory(assignCategory);
			C2STransferRules c2STransferRules = new C2STransferRules(driver);
				String ExpectedMessage = c2STransferRules.serviceTypeNotSelected(dataMap);
				String message = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.subservicetyperequired","1");
				Assertion.assertEquals(message, ExpectedMessage);
			
				Assertion.completeAssertions();
				Log.endTestCase(methodName);
		}
}
