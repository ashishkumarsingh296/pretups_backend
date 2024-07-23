package com.testscripts.prerequisites;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import com.Features.C2STransferRules;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

public class PreRequisite_C2STransferRuleCreation extends BaseTest {

	String MasterSheetPath;
	Object[][] TransferRuleCategories;
	int totalCells = 5;
	static boolean TestCaseCounter = false;

	@Test(dataProvider = "RequiredTransferRuleCategories")
	public void addC2STransferRules(String fromDomain, String fromCategory, String services, String requestBearer,
			int rownum) {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		Log.startTestCase(this.getClass().getName());

		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false) {
			test = extent.createTest("[Pre-Requisite]C2S Transfer Rule Creation");
			TestCaseCounter = true;
		}
				
		currentNode=test.createNode("To verify that Network Admin is able to create C2S Transfer Rules for " + fromCategory + " category");
		currentNode.assignCategory("Pre-Requisite");
		Object[][] cardGroupDataObj;
		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		cardGroupDataObj = c2STransferRules.addC2STransferRule(fromDomain, fromCategory, services, requestBearer, rownum);
		c2STransferRules.writeC2SData(cardGroupDataObj);

		Log.endTestCase(this.getClass().getName());
	}

	@DataProvider(name = "RequiredTransferRuleCategories")
	public Object[][] RequiredTransferRules() {

		int MatrixRow = 0;
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int C2STransferRuleCount = 0;
		for (int i = 1; i < rowCount; i++) {
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && !FromCategory.equals("Subscriber")) {
				C2STransferRuleCount++;
			}
		}

		TransferRuleCategories = new Object[C2STransferRuleCount][totalCells];

		for (int i = 1; i < rowCount; i++) {
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && !FromCategory.equals("Subscriber")) {
				TransferRuleCategories[MatrixRow][0] = ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i);
				TransferRuleCategories[MatrixRow][1] = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				TransferRuleCategories[MatrixRow][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				TransferRuleCategories[MatrixRow][3] = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
				TransferRuleCategories[MatrixRow][4] = i;
				MatrixRow++;
			}
		}

		return TransferRuleCategories;
	}

}
