package com.testscripts.smoke;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransferRules;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.pageobjects.networkadminpages.c2stransferrule.ModifyC2STransferRulePage3;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

public class Smoke_ModifyC2STransferRule extends BaseTest {

	String MasterSheetPath;
	Object[][] transferRuleCategories;

	@DataProvider(name = "modifyTransferRuleData")
	public Object[][] modifyData() {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		transferRuleCategories = new Object[1][4];
		for (int i = 1; i <= rowCount; i++) {

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
	public void modifyC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer) {

		Log.startTestCase(this.getClass().getName());

		test = extent.createTest("[Smoke]C2S Transfer Rule Modification");
		currentNode = test.createNode("To verify that Operator is able to modify C2S Transfer Rule for Category.");
		currentNode.assignCategory("Smoke");

		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		String actualMsg = c2STransferRules.modifyC2STransferRules(fromDomain, fromCategory, services, requestBearer);

		currentNode = test
				.createNode("To verify that proper Message is displayed on Successful Transfer Rule Modification");
		currentNode.assignCategory("Smoke");
		String modifyTransferRuleMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
		if (modifyTransferRuleMsg.equals(actualMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");

		Log.endTestCase(this.getClass().getName());
	}
}
