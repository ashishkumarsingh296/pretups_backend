package com.testscripts.sit;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransferRules;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class SIT_ViewC2STransferRule extends BaseTest {
	

	public static boolean testCaseCounter = false;
	String MasterSheetPath;
	Object[][] transferRuleCategories;

	@DataProvider(name = "RequiredTransferRuleCategories")
	public Object[][] modifyData() {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		transferRuleCategories = new Object[1][5];
		for (int i = 1; i < rowCount; i++) {

			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && !FromCategory.equals("Subscriber")) {
				transferRuleCategories[0][0] = ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i);
				transferRuleCategories[0][1] = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				transferRuleCategories[0][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				transferRuleCategories[0][3] = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
				transferRuleCategories[0][4] = i;
				break;
			}
		}
		return transferRuleCategories;
	}
	
	
	@Test(dataProvider = "RequiredTransferRuleCategories")
	public void viewC2STransferRule(String fromDomain, String fromCategory, String services, String requestBearer, int rownum) {

		Log.startTestCase(this.getClass().getName());
		if (testCaseCounter == false) {
			test = extent.createTest("[SIT]View C2S Transfer Rule");
			testCaseCounter = true;
		}
		currentNode = test.createNode("To verify that Operator is able to view C2S Transfer Rules for " + services
				+ " Service, " + fromDomain + " Domain, " + fromCategory + " Category");
		currentNode.assignCategory("SIT");

		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		Object addResult[][] = c2STransferRules.addC2STransferRule(fromDomain, fromCategory, services, requestBearer, rownum);
		boolean result = c2STransferRules.viewC2STransferRules(fromDomain, fromCategory, (String)addResult[0][3], requestBearer);
		currentNode = test.createNode("To verify that created transfer rule is displayed");
		currentNode.assignCategory("SIT");

		if (result) {
			currentNode.log(Status.PASS, "Transfer rule displayed");
		} else {
			currentNode.log(Status.FAIL, "Transfer rule is not displayed");
		}
		Log.endTestCase(this.getClass().getName());
	}



}
