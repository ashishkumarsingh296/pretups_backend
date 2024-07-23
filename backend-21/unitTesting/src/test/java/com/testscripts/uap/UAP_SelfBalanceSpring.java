package com.testscripts.uap;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.Enquiries.SelfBalanceSpring;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class UAP_SelfBalanceSpring extends BaseTest{
	static boolean TestCaseCounter = false;

	@Test(dataProvider = "categoryData")
	public void selfBalanceTC(String parentCategory, String category) throws InterruptedException, IOException	{
		Log.startTestCase("Self Balance");

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP] Self Balance");
			TestCaseCounter = true;
		}


		currentNode = test.createNode("To verify that Self Balance Enquiry is available for category(parent category) "+category+"("+parentCategory+")");
		currentNode.assignCategory("UAP");
		SelfBalanceSpring selfBalanceEnquiry = new SelfBalanceSpring(driver);
		String ScreenshotPath = selfBalanceEnquiry.validateSelfBalancesEnquiry(parentCategory, category);
		currentNode.addScreenCaptureFromPath(ScreenshotPath);
	}



	/**
	 * DataProvider for Operator to Channel transfer
	 * @return Object
	 */
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");


		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object data[][] = new Object[rowCount][2];
		for(int i = 0; i<rowCount; i++){
			data[i][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i+1);
			data[i][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i+1);
		}

		return data;
	}



}
