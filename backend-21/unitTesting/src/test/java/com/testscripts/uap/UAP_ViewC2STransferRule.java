package com.testscripts.uap;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransferRules;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.UAP_VIEW_C2S_TRANSFER_RULE)
public class UAP_ViewC2STransferRule extends BaseTest {
	public static boolean testCaseCounter = false;
	String MasterSheetPath;
	Object[][] transferRuleCategories;
	String assignCategory="UAP";
	
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
	 @TestManager(TestKey = "PRETUPS-303") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void viewC2STransferRule(String fromDomain, String fromCategory, String services, String requestBearer, int rownum) {

		final String methodName = "Test_viewC2STransferRule";
        Log.startTestCase(methodName);
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UVIEWC2STRFRULE1").getExtentCase(),services,fromDomain,fromCategory));
		currentNode.assignCategory(assignCategory);

		C2STransferRules c2STransferRules = new C2STransferRules(driver);
		boolean preRequisite = false;
		Object addResult[][] = c2STransferRules.addC2STransferRule(fromDomain, fromCategory, services, requestBearer, rownum, preRequisite);
		boolean result = c2STransferRules.viewC2STransferRules(fromDomain, fromCategory, (String)addResult[0][3], requestBearer,(String)addResult[0][4]);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UVIEWC2STRFRULE2").getExtentCase());
		currentNode.assignCategory(assignCategory);

		if (result) {
			Assertion.assertPass("Transfer rule displayed");
		} else {
			Assertion.assertFail("Transfer rule is not displayed");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
