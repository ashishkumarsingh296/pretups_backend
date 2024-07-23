package com.testscripts.uap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PTransferRules;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

public class UAP_ModifyP2PTransferRule extends BaseTest {

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
		transferRuleCategories = new Object[1][4];

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
							transferRuleCategories[0][3] = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, r + 1);
							break;
						}
					}
				}
			}
		}

		return transferRuleCategories;

	}

	@Test(dataProvider = "RequiredTransferRuleCategories")
	public void a_modifyP2PTransferRulesStatus(String service, String subService, String cardGroup,
			String updatedCardGroup) {

		Log.startTestCase(this.getClass().getName());
		if (testCaseCounter == false) {
			test = extent.createTest("[UAP]P2P Transfer Rule Modification");
			testCaseCounter = true;
		}
		currentNode = test.createNode("To verify that Operator is able to modify P2P Transfer Rules (Status) for "
				+ service + " Service, " + subService + " Sub-Service, " + cardGroup + " Card-Group");
		currentNode.assignCategory("UAP");

		//String updatedStatus = "Suspended";
		P2PTransferRules p2pTransferRules = new P2PTransferRules(driver);
		String addResult[] = p2pTransferRules.addP2PTransferRules(service, subService, cardGroup);
		String result[] = p2pTransferRules.modifyP2PTransferRules(service, subService, cardGroup, addResult[4],
				PretupsI.STATUS_SUSPENDED_LOOKUPS, cardGroup);
		currentNode = test.createNode(
				"To verify that proper message is displayed on successful Transfer Rule Modification(Status)");
		currentNode.assignCategory("UAP");
		String modifyP2PTransferRuleSuccessMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
		if (modifyP2PTransferRuleSuccessMsg.equals(result[0])) {
			currentNode.log(Status.PASS, "Message Validation Successful");
		} else {
			currentNode.log(Status.FAIL,
					"Expected [" + modifyP2PTransferRuleSuccessMsg + "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}

	@Test(dataProvider = "RequiredTransferRuleCategories")
	public void b_modifyP2PTransferRulesCardGroup(String service, String subService, String cardGroup,
			String updatedCardGroup) {

		Log.startTestCase(this.getClass().getName());
		if (testCaseCounter == false) {
			test = extent.createTest("[UAP]P2P Transfer Rule Modification");
			testCaseCounter = true;
		}
		currentNode = test.createNode("To verify that Operator is able to modify P2P Transfer Rules (Card Group) for "
				+ service + " Service, " + subService + " Sub-Service, " + cardGroup + " Card-Group");
		currentNode.assignCategory("UAP");

		P2PTransferRules p2pTransferRules = new P2PTransferRules(driver);
		String addResult[] = p2pTransferRules.addP2PTransferRules(service, subService, cardGroup);
		String result[] = p2pTransferRules.modifyP2PTransferRules(service, subService, cardGroup, addResult[4],
				addResult[4], updatedCardGroup);
		currentNode = test.createNode(
				"To verify that proper message is displayed on successful Transfer Rule Modification(Card Group)");
		currentNode.assignCategory("UAP");
		String modifyP2PTransferRuleSuccessMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
		if (modifyP2PTransferRuleSuccessMsg.equals(result[0])) {
			currentNode.log(Status.PASS, "Message Validation Successful");
		} else {
			currentNode.log(Status.FAIL,
					"Expected [" + modifyP2PTransferRuleSuccessMsg + "] but found [" + result[0] + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		Log.endTestCase(this.getClass().getName());
	}

}
