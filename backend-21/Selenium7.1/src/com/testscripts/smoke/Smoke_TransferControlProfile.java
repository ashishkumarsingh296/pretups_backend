package com.testscripts.smoke;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.TransferControlProfile;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class Smoke_TransferControlProfile extends BaseTest{
	static boolean TestCaseCounter = false;

	@Test(dataProvider = "dataProvider")
	public void smoke_TransferControlProfileCreation(int rowNum,String domainName, String categoryName) {
		
		Log.startTestCase(this.getClass().getName());
		
		
		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[Smoke]TCP Creation");
			TestCaseCounter = true;
		}
		
		/*
		 * Test Case Number 1 - Category Level TCP Creation
		 */
		currentNode=test.createNode("To verify that Super Admin is able to create Category Level TCP for " + categoryName + " category.");
		currentNode.assignCategory("Smoke");	
		TransferControlProfile.createCategoryLevelTransferControlProfile(rowNum, domainName, categoryName);
		
		/*
		 * Test Case Number 2 - Channel Level TCP Creation
		 */
		currentNode=test.createNode("To verify that Network Admin is able to create Channel Level TCP for " + categoryName + " category.");
		currentNode.assignCategory("Smoke");
		TransferControlProfile.createChannelLevelTransferControlProfile(rowNum, domainName, categoryName);
		
		Log.endTestCase(this.getClass().getName());
	}
	
	@DataProvider(name="dataProvider")
	public Object[][] TestDataFeed() throws IOException{
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		Object [][] categoryData=new Object[1][3];
			categoryData[0][0]=1;
			categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
			categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		return categoryData;
	}
}
