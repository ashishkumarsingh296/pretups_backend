package com.testscripts.sit;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PTransferRules;
import com.Features.P2PTransferRulesNegative;
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

@ModuleManager(name = Module.SIT_Modify_P2P_Transfer_Rule)
public class SIT_ModifyP2PTransferRule extends BaseTest {

	static boolean TestCaseCounter = false;
	String MasterSheetPath;
	Object[][] transferRuleCategories;
	String assignCategory = "SIT";
	public static boolean testCaseCounter = false;

	@DataProvider(name = "RequiredTransferRuleCategories")
	public Object[][] RequiredTransferRules() {

		String serviceName = null;
		P2PTransferRules p2PTransferRules = new P2PTransferRules(driver);
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		transferRuleCategories = new Object[1][4];

		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && FromCategory.equals("Subscriber")) {
				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				String[] serviceArray = p2PTransferRules.serviceValue(services);
				String requestBearer = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
				// String[] serviceArray = p2PTransferRules.serviceValue(services);
				String[] requestArray = requestBearer.split(",");
				for (int k = 0; k < serviceArray.length; k++) {
					ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
					int totalRow = ExcelUtility.getRowCount();
					for (int r = 1; r <= totalRow; r++) {
						serviceName = ExcelUtility.getCellData(0, ExcelI.NAME, r);
						if (serviceArray[k].equals(serviceName)) {
							transferRuleCategories[0][0] = serviceArray[k];
							transferRuleCategories[0][1] = ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, r);
							transferRuleCategories[0][2] = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, r);
							transferRuleCategories[0][3] = requestArray[0];
							// transferRuleCategories[0][4] = ExcelUtility.getCellData(0,
							// ExcelI.CARDGROUP_NAME, r + 1);
							break;
						}
					}
				}
			}
		}

		return transferRuleCategories;

	}

	// negative nothing is selected
	@Test
	@TestManager(TestKey = "PRETUPS-169") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_modifyP2PTransferRule() {
		final String methodName = "Test_ModifyP2PTransferRule";
        Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-169").getExtentCase());
		currentNode.assignCategory(assignCategory);
		System.out.println(assignCategory);
		P2PTransferRulesNegative p2ptransrulenegative = new P2PTransferRulesNegative(driver);
		String msg = p2ptransrulenegative.modifyP2PNothingSelected();
		String modifyP2PTransferRuleFailureMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.selectrow");
		if (msg.equalsIgnoreCase("P2P_Transfer Rules for this version of Pretups not exists")) {
			Assertion.assertSkip("Transfer rule not exists for this version");
		} else {
			Assertion.assertEquals(msg, modifyP2PTransferRuleFailureMsg);
		}
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
	}

//negative
	@Test(dataProvider = "RequiredTransferRuleCategories")
	@TestManager(TestKey = "PRETUPS-170") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_modifyP2PTransferRule(String service, String subService, String cardGroup, String requestBearer) {
		final String methodName = "Test_ModifyP2PTransferRule";
        Log.startTestCase(methodName);
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PRETUPS-170").getExtentCase(),
				service, subService, cardGroup, requestBearer));
		currentNode.assignCategory(assignCategory);
		System.out.println(assignCategory);
		P2PTransferRulesNegative p2ptransrulenegative = new P2PTransferRulesNegative(driver);
		String msg = p2ptransrulenegative.modifyP2PStatusNotSelected(service, subService, cardGroup, requestBearer);
		String modifyP2PTransferRuleFailureMsg = MessagesDAO.prepareMessageByKey("trfrule.addtrfrule.error.required",
				"Status", "1");
		if (msg.equalsIgnoreCase("P2P_Transfer Rules for this version of Pretups not exists")) {
			Assertion.assertSkip("Transfer rule not exists for this version");
		} else {
			Assertion.assertEquals(msg, modifyP2PTransferRuleFailureMsg);
		}
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());

	}

}
