package com.testscripts.uap;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import com.Features.O2CTransferRule;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

/**
 * @author krishan.chawla This class contains the Smoke cases related to O2C Transfer Rule Creation
 */
public class UAP_O2CTransferRule extends BaseTest {

	String MasterSheetPath;
	Object[][] TransferRuleCategories;
	String FirstApprovalLimit;
	String SecondApprovalLimit;
	static boolean TestCaseCounter = false;

	@Test(dataProvider = "RequiredTransferRuleCategories")
	public void TC1_InitiateTransferRules(String ToDomain, String ToCategory, String Services) {
		Object Params[];
		O2CTransferRule O2CTransferRule = new O2CTransferRule(driver);
		
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) {
		test = extent.createTest("[UAP]O2C Transfer Rule");
		TestCaseCounter = true;
		}
		
		/*
		 * Case Number 1: To verify Network Admin is able to create O2C Transfer Rule
		 * 				  Code Handles Associate / Initiate both links.
		 */
		currentNode = test.createNode("To verify that Network Admin is able to create O2C Transfer Rule for: " + ToCategory + " category");
		currentNode.assignCategory("UAP");
		Params = O2CTransferRule.createTransferRule(ToDomain, ToCategory, Services, FirstApprovalLimit,	SecondApprovalLimit);

		if (Params[1].equals(false)) {
			if (Params[0].equals(false)) {
			
			/*
			 * Case Number 2: Message Validation
			 */
			currentNode = test.createNode("To verify that proper message is displayed on successful O2C Transfer Rule Initiate");
			currentNode.assignCategory("UAP");
			String Message = MessagesDAO.prepareMessageByKey("channeltrfrule.addtrfrule.msg.addsuccessapprequired", "");
			if (Params[2].equals(Message))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + Params[2] + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
			
			/*
			 * Case Number 3: O2C Transfer Rule Approval
			 */
			currentNode = test.createNode("To verify that Network Admin is able to approve O2C Transfer Rule for: " + ToCategory + " category");
			currentNode.assignCategory("UAP");
			O2CTransferRule.approveTransferRule(ToCategory);
			} else {

			currentNode = test.createNode("To verify that proper message is displayed on successful O2C Transfer Rule Initiate");
			currentNode.assignCategory("UAP");
			String Message = MessagesDAO.prepareMessageByKey("channeltrfrule.addtrfrule.msg.addsuccess", "");
			if (Params[2].equals(Message))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + Params[2] + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}

			currentNode = test.createNode("To verify that Network Admin is able to approve O2C Transfer Rule for: " + ToCategory + " category");
			currentNode.assignCategory("UAP");
			currentNode.skip("O2C Transfer rule for " + ToCategory + " category already exists, hence Skipped");
			}
		}
		
		Log.endTestCase(this.getClass().getName());
	}

	
	@Test
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
		
		Log.startTestCase(this.getClass().getName());
		
		Object Params[];
		O2CTransferRule O2CTransferRule = new O2CTransferRule(driver);
		
		/*
		 * Case Number 1: To verify Network Admin is able to modify O2C Transfer Rule
		 * 				  Code Handles Associate / Initiate both links.
		 */
		currentNode = test.createNode("To verify that Network Admin is able to modify O2C Transfer Rule for: " + ToCategory + " category");
		currentNode.assignCategory("UAP");
		Params = O2CTransferRule.modifyTransferRule(ToDomain, ToCategory, FirstApprovalLimit, SecondApprovalLimit);

		if (Params[0].equals(false)) {
			
			/*
			 * Case Number 3: O2C Transfer Rule Approval
			 */
			currentNode = test.createNode("To verify that Network Admin is able to approve O2C Transfer Rule for: " + ToCategory + " category");
			currentNode.assignCategory("UAP");
			O2CTransferRule.approveTransferRule(ToCategory);
		} 
		
		Log.endTestCase(this.getClass().getName());
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
