package com.testscripts.uap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PTransferRules;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

public class UAP_AddP2PTransferRule extends BaseTest {

	public static boolean testCaseCounter = false;
	String MasterSheetPath;
	Object[][] transferRuleCategories;

	@DataProvider(name = "RequiredTransferRuleCategories")
	public Object[][] RequiredTransferRules() {

		String serviceName = null;
		P2PTransferRules p2PTransferRules = new P2PTransferRules(driver);
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		transferRuleCategories = new Object[1][3];

		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && FromCategory.equals("Subscriber")) {
				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				String[] serviceArray = p2PTransferRules.serviceValue(services);
				for (int k = 0; k < serviceArray.length; k++) {
					ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
					int totalRow = ExcelUtility.getRowCount();
					for (int r = 1; r <= totalRow; r++) {
						serviceName = ExcelUtility.getCellData(0, ExcelI.NAME, r);
						if (serviceArray[k].equals(serviceName)) {
							transferRuleCategories[0][0] = serviceArray[k];
							transferRuleCategories[0][1] = ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, r);
							transferRuleCategories[0][2] = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, r);
							break;
						}
					}
				}
			}
		}

		return transferRuleCategories;

	}

	@DataProvider(name = "TransferRuleCategoriesWOCardGr")
	public Object[][] requiredTransferRules() {

		String serviceName = null;
		P2PTransferRules p2PTransferRules = new P2PTransferRules(driver);
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		transferRuleCategories = new Object[1][3];

		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && FromCategory.equals("Subscriber")) {
				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				String[] serviceArray = p2PTransferRules.serviceValue(services);
				for (int k = 0; k < serviceArray.length; k++) {
					ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
					int totalRow = ExcelUtility.getRowCount();
					for (int r = 1; r <= totalRow; r++) {
						serviceName = ExcelUtility.getCellData(0, ExcelI.NAME, r);
						if (serviceArray[k].equals(serviceName)) {
							transferRuleCategories[0][0] = serviceArray[k];
							transferRuleCategories[0][1] = ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, r);
							transferRuleCategories[0][2] = null;
							break;
						}
					}
				}
			}
		}

		return transferRuleCategories;

	}

	@Test(dataProvider = "RequiredTransferRuleCategories")
	public void a_addP2PTransferRules(String service, String subService, String cardGroup) {

		Log.startTestCase(this.getClass().getName());
		if (testCaseCounter == false) {
			test = extent.createTest("[UAP]P2P Transfer Rule Addition");
			testCaseCounter = true;
		}
		currentNode = test.createNode("To verify that Operator is able to create P2P Transfer Rules for " + service
				+ " Service, " + subService + " Sub-Service, " + cardGroup + " Card-Group");
		currentNode.assignCategory("UAP");

		P2PTransferRules p2pTransferRules = new P2PTransferRules(driver);
		String result[] = p2pTransferRules.addP2PTransferRules(service, subService, cardGroup);
		currentNode = test
				.createNode("To verify that proper message is displayed on successful transfer rule creation.");
		currentNode.assignCategory("UAP");
		String addP2PTransferRuleSuccessMsg = MessagesDAO
				.prepareMessageByKey("trfrule.addtrfrule.msg.success");
		String p2pTransferRuleAlreadyExistsMsg = MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist",
				"1");
		if (addP2PTransferRuleSuccessMsg.equals(result[0])) {
			currentNode.log(Status.PASS, "Message Validation Successful");
		} else if (p2pTransferRuleAlreadyExistsMsg.equals(result[0])) {
			currentNode.log(Status.SKIP, "Message Validation Successful");
		} else {
			currentNode.log(Status.FAIL,
					"Expected [" + addP2PTransferRuleSuccessMsg + "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}

	/*@Test(dataProvider = "TransferRuleCategoriesWOCardGr")
	public void b_addP2PTransferRulesNegative(String service, String subService, String cardGroup) {

		Log.startTestCase(this.getClass().getName());
		if (testCaseCounter == false) {
			test = extent.createTest("[UAP]P2P Transfer Rule Addition");
			testCaseCounter = true;
		}
		currentNode = test.createNode("To verify that Operator is unable to create P2P Transfer Rules for " + service
				+ " Service, " + subService + " Sub-Service");
		currentNode.assignCategory("UAP");

		P2PTransferRules p2pTransferRules = new P2PTransferRules(driver);
		String result[] = p2pTransferRules.addP2PTransferRules(service, subService, cardGroup);
		currentNode = test
				.createNode("To verify that proper message is displayed on unsuccessful transfer rule creation.");
		currentNode.assignCategory("UAP");
		String addP2PTransferRuleFailureMsg = MessagesDAO
				.prepareMessageByKey("trfrule.addtrfrule.error.cardgrouprequired", "1");
		if (addP2PTransferRuleFailureMsg.equals(result[0])) {
			currentNode.log(Status.PASS, "Message Validation Successful");
		} else {
			currentNode.log(Status.FAIL,
					"Expected [" + addP2PTransferRuleFailureMsg + "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}
*/
}
