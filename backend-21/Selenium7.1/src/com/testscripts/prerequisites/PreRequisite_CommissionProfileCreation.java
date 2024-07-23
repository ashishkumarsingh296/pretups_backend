package com.testscripts.prerequisites;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.CommissionProfile;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class PreRequisite_CommissionProfileCreation extends BaseTest{
		
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider="categoryData")
	public void createCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		
		//Pushing Start Test Case to Logger
		Log.startTestCase(this.getClass().getName());
		
		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false) {
			test = extent.createTest("[Pre-Requisite]Commission Profile Creation");
			TestCaseCounter = true;
		}

		//Initializing Commission Profile Feature class with current driver
		CommissionProfile CommissionProfile = new CommissionProfile(driver);
		
		/*
		 * Test Case - Creating Commission Profile as per the DataProvider details
		 */
		currentNode=test.createNode("To verify that Network Admin is able to create Commission Profile for " + categoryName + " category");
		currentNode.assignCategory("Pre-Requisite");
		String[] result = CommissionProfile.addCommissionProfile(domainName, categoryName, grade);
		CommissionProfile.writeCommissionProfileToExcel(rowNum, result);
		
		//Pushing end test case to the Logger
		Log.endTestCase(this.getClass().getName());
	}
	
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[rowCount][4];
		for (int i = 1, j = 0; i <= rowCount; i++, j++) {
			categoryData[j][0] = i;
			categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
		}
		return categoryData;
	}
}
