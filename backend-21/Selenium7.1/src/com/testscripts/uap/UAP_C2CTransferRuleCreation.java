package com.testscripts.uap;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import com.Features.C2CTransferRule;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

/**
 * @author lokesh.kontey This class is created for C2C Transfer Rule Creation
 */
public class UAP_C2CTransferRuleCreation extends BaseTest {

	public static String RuleType;
	static boolean TestCaseCounter = false;

	String MasterSheetPath;
	Object[][] TransferRuleCategories;
	
	@Test(dataProvider = "RequiredTransferRuleCategories")
	public void c2cTrfRuleCreation(String ToDomain, String ToCategory, String Services, String FromCategory, String FromDomain) {

		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) {
		test = extent.createTest("[UAP]C2C Transfer Rule Creation");
		TestCaseCounter = true;
		}
		
				
		C2CTransferRule c2ctrfrule=new C2CTransferRule(driver);
		
		currentNode=test.createNode("Description: To verify that Operator is able to create C2C Transfer Rule from " + FromCategory
				+ " category to " + ToCategory + " category.");
		currentNode.assignCategory("UAP");
		RuleType = Services;
		
		c2ctrfrule.channeltochannelTrfRule(ToDomain, ToCategory, Services, FromCategory, FromDomain);
	}

	@DataProvider(name = "RequiredTransferRuleCategories")
	public Object[][] RequiredTransferRules() {

		int MatrixRow = 0;
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int C2CTransferRuleCount = 0;
		for (int i = 1; i < rowCount; i++) {
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String ToCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (!FromCategory.equals("Operator") && !ToCategory.equals("Subscriber")) {
				C2CTransferRuleCount++;
			}

		}System.out.println(" Count value :: "+C2CTransferRuleCount);
		TransferRuleCategories = new Object[C2CTransferRuleCount][5];

		for (int i = 1; i < rowCount; i++) {
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			String ToCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
			if (!FromCategory.equals("Operator") && !ToCategory.equals("Subscriber")) {
				TransferRuleCategories[MatrixRow][0] = ExcelUtility.getCellData(0, ExcelI.TO_DOMAIN, i);
				TransferRuleCategories[MatrixRow][1] = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
				TransferRuleCategories[MatrixRow][2] = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				TransferRuleCategories[MatrixRow][3] = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
				TransferRuleCategories[MatrixRow][4] = ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i);
				System.out.println(" "+TransferRuleCategories[MatrixRow][0]+" "+TransferRuleCategories[MatrixRow][1]+" "+TransferRuleCategories[MatrixRow][2]+" "+TransferRuleCategories[MatrixRow][3]+" "+TransferRuleCategories[MatrixRow][4]);
				MatrixRow++;
			}
		}
		return TransferRuleCategories;
	}
}
