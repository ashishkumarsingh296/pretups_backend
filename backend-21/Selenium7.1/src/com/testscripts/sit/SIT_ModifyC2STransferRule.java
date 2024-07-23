package com.testscripts.sit;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransferRules;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class SIT_ModifyC2STransferRule extends BaseTest  {
	
	static boolean TestCaseCounter = false;
	String MasterSheetPath;
	Object[][] transferRuleCategories;

	@DataProvider(name = "modifyTransferRuleData")
	public Object[][] modifyData() {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		transferRuleCategories = new Object[1][4];
		for (int i = 1; i < rowCount; i++) {

			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && !FromCategory.equals("Subscriber")) {
				transferRuleCategories[0][0] = ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i);
				transferRuleCategories[0][1] = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				transferRuleCategories[0][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				transferRuleCategories[0][3] = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
				break;
			}
		}
		return transferRuleCategories;
	}

	@Test(dataProvider = "modifyTransferRuleData")
	public void a_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Modify C2S Transfer Rules");
			TestCaseCounter = true;
		}
		
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String actualMsg = c2STransferRules.modifyC2STransferRules(fromDomain, fromCategory, services, requestBearer);

		currentNode = test
				.createNode("To verify that proper Message is displayed on Successful Transfer Rule Modification");
		currentNode.assignCategory("SIT");
		String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
		if (modifyTransferRuleMsg.equals(actualMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");

		Log.endTestCase(this.getClass().getName());
	}
	
	//without selecting any Transfer Rule.
	@Test(dataProvider = "modifyTransferRuleData")
	public void b_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Modify C2S Transfer Rules");
			TestCaseCounter = true;
		}

       C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String actualMsg = c2STransferRules.modifyC2STransferRules_null(fromDomain, fromCategory, services, requestBearer);

		currentNode = test
				.createNode("To verify that Operator is not able to modify C2S Transfer Rule for Category without selecting any Transfer Rule.");
		currentNode.assignCategory("SIT");
		String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.selectrow");
		if (modifyTransferRuleMsg.equals(actualMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");

		Log.endTestCase(this.getClass().getName());
	}
	

	//No status selected
	@Test(dataProvider = "modifyTransferRuleData")
	public void c_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer) {

		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Modify C2S Transfer Rules");
			TestCaseCounter = true;
		}
		
        String status = "Select";
        String type = "Modify";
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String actualMsg = c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer,status, type);

		currentNode = test
				.createNode("To verify that Operator is not able to modify C2S Transfer Rule for Category without selecting status.");
		currentNode.assignCategory("SIT");
		String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.addc2stransferrules.error.required", MessagesDAO.getLabelByKey("trfrule.addc2stransferrules.label.status"),"1");
		if (modifyTransferRuleMsg.equals(actualMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");

		Log.endTestCase(this.getClass().getName());
	}
	
	//Verifying Status.
		@Test(dataProvider = "modifyTransferRuleData")
		public void d_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer) {

			Log.startTestCase(this.getClass().getName());
			if (TestCaseCounter == false) {
				test = extent.createTest("[SIT]Modify C2S Transfer Rules");
				TestCaseCounter = true;
			}
			
	        
	        String status = PretupsI.STATUS_SUSPENDED_LOOKUPS;
			String type = "Modify";
			C2STransferRules c2STransferRules = new C2STransferRules(driver);
			String actualMsg = c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer,status, type);

			currentNode = test
					.createNode("To verify that Operator is able to modify C2S Transfer Rule for Category after verifying its status only.");
			currentNode.assignCategory("SIT");
			String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
			if (modifyTransferRuleMsg.equals(actualMsg))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else
				currentNode.log(Status.FAIL, "Message Validation Unsuccessful");

			Log.endTestCase(this.getClass().getName());
		}
		
				
				//Reset TransferRule.
				@Test(dataProvider = "modifyTransferRuleData")
				public void e_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer) {

					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
						test = extent.createTest("[SIT]Modify C2S Transfer Rules");
						TestCaseCounter = true;
					}

			        String beforeStatus = PretupsI.STATUS_SUSPENDED_LOOKUPS;
			        String type = "reset";
					C2STransferRules c2STransferRules = new C2STransferRules(driver);
					String result = c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer, beforeStatus, type);

					currentNode = test
							.createNode("To verify that Operator is able to reset  while modifying C2S Transfer Rule.");
					currentNode.assignCategory("SIT");
					if (result.equalsIgnoreCase("true"))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else
						currentNode.log(Status.FAIL, "Message Validation Unsuccessful");

					Log.endTestCase(this.getClass().getName());
				}
				
				//Back button in TransferRule.
				@Test(dataProvider = "modifyTransferRuleData")
				public void f_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer) {

					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
						test = extent.createTest("[SIT]Modify C2S Transfer Rules");
						TestCaseCounter = true;
					}

			        String beforeStatus = PretupsI.STATUS_SUSPENDED_LOOKUPS;
			        String type = "back";
					C2STransferRules c2STransferRules = new C2STransferRules(driver);
					String result = c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer,beforeStatus, type);

					currentNode = test
							.createNode("To verify that Operator is able to click back for visiting previous page while modifying C2S Transfer Rule.");
					currentNode.assignCategory("SIT");
					if (result.equals("true"))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else
						currentNode.log(Status.FAIL, "Message Validation Unsuccessful");

					Log.endTestCase(this.getClass().getName());
				}
				
      			//Delete TransferRule.
				@Test(dataProvider = "modifyTransferRuleData")
				public void g_modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer) {

					Log.startTestCase(this.getClass().getName());
					if (TestCaseCounter == false) {
						test = extent.createTest("[SIT]Modify C2S Transfer Rules");
						TestCaseCounter = true;
					}

			        String status = PretupsI.STATUS_SUSPENDED_LOOKUPS;
			        String type = "delete";
					C2STransferRules c2STransferRules = new C2STransferRules(driver);
					String actualMsg = c2STransferRules.modifyC2STransferRules_SIT(fromDomain, fromCategory, services, requestBearer,status, type);

					currentNode = test
							.createNode("To verify that Operator is able to delete C2S Transfer Rule.");
					currentNode.assignCategory("SIT");
					String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.deltrfrule.msg.success");
					if (modifyTransferRuleMsg.equals(actualMsg))
						currentNode.log(Status.PASS, "Message Validation Successful");
					else
						currentNode.log(Status.FAIL, "Message Validation Unsuccessful");

					Log.endTestCase(this.getClass().getName());
				}

}
