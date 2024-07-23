package com.testscripts.uap;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransferRule;
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

/**
 * @author krishan.chawla This class contains the Smoke cases related to O2C Transfer Rule Creation
 */
@ModuleManager(name = Module.UAP_O2C_TRANSFER_RULE)
public class UAP_O2CTransferRule extends BaseTest {

	String MasterSheetPath;
	Object[][] TransferRuleCategories;
	String FirstApprovalLimit;
	String SecondApprovalLimit;
	static boolean TestCaseCounter = false;
	String assignCategory="UAP";
	
	@Test(dataProvider = "RequiredTransferRuleCategories")
    @TestManager(TestKey = "PRETUPS-279") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void TC1_InitiateTransferRules(String ToDomain, String ToCategory, String Services) {
		Object Params[];
		O2CTransferRule O2CTransferRule = new O2CTransferRule(driver);
		
		final String methodName = "Test_InitiateTransferRules";
        Log.startTestCase(methodName);
		
		/*
		 * Case Number 1: To verify Network Admin is able to create O2C Transfer Rule
		 * 				  Code Handles Associate / Initiate both links.
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRFRULE1").getExtentCase(), ToCategory));
		currentNode.assignCategory(assignCategory);
		Params = O2CTransferRule.createTransferRule(ToDomain, ToCategory, Services, FirstApprovalLimit,	SecondApprovalLimit);

		if (Params[1].equals(false)) {
			if (Params[0].equals(false)) {
			
			/*
			 * Case Number 2: Message Validation
			 */
			currentNode = test.createNode(_masterVO.getCaseMasterByID("SO2CTRFRULE1").getExtentCase());
			currentNode.assignCategory(assignCategory);
			String Message = MessagesDAO.prepareMessageByKey("channeltrfrule.addtrfrule.msg.addsuccessapprequired", "");
			Assertion.assertEquals(Params[2].toString(), Message);
			
			/*
			 * Case Number 3: O2C Transfer Rule Approval
			 */
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRFRULE2").getExtentCase(), ToCategory));
			currentNode.assignCategory(assignCategory);
			O2CTransferRule.approveTransferRule(ToCategory);
			} else {

			currentNode = test.createNode(_masterVO.getCaseMasterByID("SO2CTRFRULE2").getExtentCase());
			currentNode.assignCategory(assignCategory);
			String Message = MessagesDAO.prepareMessageByKey("channeltrfrule.addtrfrule.msg.addsuccess", "");
			Assertion.assertEquals(Params[2].toString(), Message);

			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRFRULE2").getExtentCase(), ToCategory));
			currentNode.assignCategory(assignCategory);
			Assertion.assertSkip("O2C Transfer rule for " + ToCategory + " category already exists, hence Skipped");
			}
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	@Test
	 @TestManager(TestKey = "PRETUPS-280") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void TC2_ModifyTransferRule() {
		
		/*
		 * Data Object for Modification
		 */
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		String ToDomain = ExcelUtility.getCellData(0, ExcelI.TO_DOMAIN, 1);
		String ToCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, 1);
		FirstApprovalLimit = _masterVO.getProperty("O2CFirstApprovalLimit");
		SecondApprovalLimit = _masterVO.getProperty("O2CSecondApprovalLimit");
		// Data Objects ends
		
		final String methodName = "Test_ModifyTransferRule";
        Log.startTestCase(methodName);
		
		Object Params[];
		O2CTransferRule O2CTransferRule = new O2CTransferRule(driver);
		
		/*
		 * Case Number 1: To verify Network Admin is able to modify O2C Transfer Rule
		 * 				  Code Handles Associate / Initiate both links.
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SO2CTRFRULE3").getExtentCase(), ToCategory));
		currentNode.assignCategory(assignCategory);
		Params = O2CTransferRule.modifyTransferRule(ToDomain, ToCategory, FirstApprovalLimit, SecondApprovalLimit);

		if (Params[0].equals(false)) {
			
			/*
			 * Case Number 3: O2C Transfer Rule Approval
			 */
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SO2CTRFRULE4").getExtentCase(), ToCategory));
			currentNode.assignCategory(assignCategory);
			O2CTransferRule.approveTransferRule(ToCategory);
		} 
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	/*
	 * DataProvider for O2C Transfer Rule Creation Test Case
	 */
	@DataProvider(name = "RequiredTransferRuleCategories")
	public Object[][] RequiredTransferRules() {

		FirstApprovalLimit = _masterVO.getProperty("O2CFirstApprovalLimit");
		SecondApprovalLimit = _masterVO.getProperty("O2CSecondApprovalLimit");
		int MatrixRow = 0;
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int O2CTransferRuleCount = 0;
		for (int i = 1; i < rowCount; i++) {
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			if (FromCategory.equals("Operator")) {
				O2CTransferRuleCount++;
			}
		}

		TransferRuleCategories = new Object[O2CTransferRuleCount][3];

		for (int i = 1; i < rowCount; i++) {
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			if (FromCategory.equals("Operator")) {
				TransferRuleCategories[MatrixRow][0] = ExcelUtility.getCellData(0, ExcelI.TO_DOMAIN, i);
				TransferRuleCategories[MatrixRow][1] = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
				TransferRuleCategories[MatrixRow][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				MatrixRow++;
			}
		}
		return TransferRuleCategories;
	}
}
