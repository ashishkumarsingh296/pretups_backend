package com.testscripts.uap;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PTransferRules;
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


@ModuleManager(name = Module.UAP_ADD_P2P_TRANSFER_RULE)
public class UAP_AddP2PTransferRule extends BaseTest {

	public static boolean testCaseCounter = false;
	String MasterSheetPath;
	Object[][] transferRuleCategories;
	String assignCategory="UAP";
	
	@DataProvider(name = "RequiredTransferRuleCategories")
	public Object[][] RequiredTransferRules() {

		String serviceName = null;
		String csvSplit = ",";
		int MatrixRow = 0;
		P2PTransferRules p2PTransferRules = new P2PTransferRules(driver);
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int P2PTransferRuleCount = 0;
		
		for (int i = 1; i <= rowCount; i++) {
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && FromCategory.equals("Subscriber")) {
				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				String[] serviceArray = p2PTransferRules.serviceValue(services);
				int length = serviceArray.length;
				P2PTransferRuleCount=length;
			}
		}
		transferRuleCategories = new Object[P2PTransferRuleCount][4];

		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (toCategory.equals("Subscriber") && FromCategory.equals("Subscriber")) {
				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				String requestBearer = ExcelUtility.getCellData(0, ExcelI.ACCESS_BEARER, i);
				String[] serviceArray = p2PTransferRules.serviceValue(services);
				String[] requestArray = requestBearer.split(csvSplit);
				for (int k = 0; k < serviceArray.length; k++) {
					ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
					int totalRow = ExcelUtility.getRowCount();
					for (int r = 1; r <= totalRow; r++) {
						serviceName = ExcelUtility.getCellData(0, ExcelI.NAME, r);
						if (serviceArray[k].equals(serviceName)) {
							transferRuleCategories[MatrixRow][0] = serviceArray[k];
							transferRuleCategories[MatrixRow][1] = ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, r);
							transferRuleCategories[MatrixRow][2] = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, r);
							transferRuleCategories[MatrixRow][3] = requestArray[0];
							MatrixRow++;
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
	@TestManager(TestKey = "PRETUPS-338") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_addP2PTransferRules(String service, String subService, String cardGroup, String requestBearer) {

		final String methodName = "Test_NetworkStockaddP2PTransferRules";
        Log.startTestCase(methodName);
        
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PRETUPS-162").getExtentCase(),service,subService,cardGroup,requestBearer));
		currentNode.assignCategory(assignCategory);
        boolean uap = true;
		P2PTransferRules p2pTransferRules = new P2PTransferRules(driver);
		String result[] = p2pTransferRules.addP2PTransferRules(service, subService, cardGroup, uap, requestBearer);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UP2PTRFRULE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String addP2PTransferRuleSuccessMsg = MessagesDAO.prepareMessageByKey("trfrule.addtrfrule.msg.success");
		String p2pTransferRuleAlreadyExistsMsg = MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist",
				"1");
		if (p2pTransferRuleAlreadyExistsMsg.equals(result[0])) {
			Assertion.assertSkip("Message Validation Successful");
		}
		else {
			Assertion.assertEquals(addP2PTransferRuleSuccessMsg, result[0]);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
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
