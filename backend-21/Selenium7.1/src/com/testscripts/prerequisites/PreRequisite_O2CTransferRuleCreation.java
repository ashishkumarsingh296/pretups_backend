package com.testscripts.prerequisites;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import com.Features.O2CTransferRule;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

/**
 * @author krishan.chawla
 * This class is created for O2C Transfer Rule Creation
 */
public class PreRequisite_O2CTransferRuleCreation extends BaseTest{

	String MasterSheetPath;
	Object[][] TransferRuleCategories; 
	String FirstApprovalLimit;
	String SecondApprovalLimit;
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider="RequiredTransferRuleCategories")
	public void Prerequisite_O2CTransferRuleCreation(String ToDomain, String ToCategory, String Services ) {
		Object Params[];
		O2CTransferRule O2CTransferRule = new O2CTransferRule(driver);

		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false) {
			test = extent.createTest("[Pre-Requisite]O2C Transfer Rule Creation");
			TestCaseCounter = true;
		}
		
		/*
		 * Test Case - O2C Transfer Role Creation
		 */
		currentNode = test.createNode("To verify that Network Admin is able to create O2C Transfer Rule for: " + ToCategory + " category");
		currentNode.assignCategory("Pre-Requisite");
		Params = O2CTransferRule.createTransferRule(ToDomain, ToCategory, Services, FirstApprovalLimit, SecondApprovalLimit);
		
		if (Params[0].equals(false) && Params[1].equals(false)) {
			currentNode = test.createNode("To verify that Network Admin is able to approve O2C Transfer Rule for: " + ToCategory + " category");
			currentNode.assignCategory("Pre-Requisite");
			O2CTransferRule.approveTransferRule(ToCategory);
		}
		else {
			currentNode = test.createNode("To verify that Network Admin is able to approve O2C Transfer Rule for: " + ToCategory + " category");
			currentNode.assignCategory("Pre-Requisite");
			currentNode.skip("O2C Transfer rule for " + ToCategory + " category already exists, hence Skipped");
		}
		
		Log.endTestCase(this.getClass().getName());
	}
	
	@DataProvider(name="RequiredTransferRuleCategories")
	public Object[][] RequiredTransferRules() {
		
		FirstApprovalLimit = _masterVO.getProperty("O2CFirstApprovalLimit");
		SecondApprovalLimit = _masterVO.getProperty("O2CSecondApprovalLimit");
		int MatrixRow = 0;
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int O2CTransferRuleCount = 0;
		for (int i=1; i<rowCount; i++) {
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			if (FromCategory.equals("Operator")) {
				O2CTransferRuleCount++; 
			}		
		}
		
		TransferRuleCategories = new Object[O2CTransferRuleCount][3];
		
		for (int i=1; i<rowCount; i++) {
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
