package com.testscripts.prerequisites;

import java.io.IOException;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.TransferControlProfile;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class PreRequisite_TCPCreation extends BaseTest{
	
	static boolean TestCaseCounter = false;

	@Test(dataProvider = "categoryData")
	public void categoryLevelTransferControlProfileCreation(int rowNum,String domainName, String categoryName) {
		
		Log.startTestCase(this.getClass().getName());
		
		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false) {
			test = extent.createTest("[Pre-Requisite]TCP Creation");
			TestCaseCounter = true;
		}
		
		// Initializing the TCP Feature class with current driver
		TransferControlProfile TransferControlProfile = new TransferControlProfile(driver);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		
		/*
		 * Test Case Number 1 - Category Level TCP Creation
		 */
		currentNode=test.createNode("To verify that Super Admin is able to create Category Level TCP for " + categoryName + " category.");
		currentNode.assignCategory("Pre-Requisite");	
		Map<String, String> dataMap1 = TransferControlProfile.createCategoryLevelTransferControlProfile(rowNum, domainName, categoryName);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		ExcelUtility.setCellData(0, ExcelI.SA_TCP_NAME, rowNum, dataMap1.get("CatTCP"));
		ExcelUtility.setCellData(0, ExcelI.SA_TCP_PROFILE_ID, rowNum, dataMap1.get("profile_ID"));
		
		/*
		 * Test Case Number 2 - Channel Level TCP Creation
		 */
		currentNode=test.createNode("To verify that Network Admin is able to create Channel Level TCP for " + categoryName + " category.");
		currentNode.assignCategory("Pre-Requisite");
		Map<String, String> dataMap = TransferControlProfile.createChannelLevelTransferControlProfile(rowNum, domainName, categoryName);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		ExcelUtility.setCellData(0, ExcelI.NA_TCP_NAME,rowNum, dataMap.get("TCP_Name"));
		ExcelUtility.setCellData(0, ExcelI.NA_TCP_PROFILE_ID, rowNum, dataMap.get("profile_ID"));
		
		Log.endTestCase(this.getClass().getName());
	}
	
	@DataProvider(name="categoryData")
	public Object[][] TestDataFeed() throws IOException{
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object [][] categoryData=new Object[rowCount][3];
		for (int i=1, j=0;i<=rowCount;i++, j++){
			categoryData[j][0]=i;
			categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		}
		return categoryData;
	}
}
