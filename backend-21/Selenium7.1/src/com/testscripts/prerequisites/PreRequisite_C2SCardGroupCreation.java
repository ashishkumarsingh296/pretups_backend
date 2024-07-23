package com.testscripts.prerequisites;

import org.testng.annotations.Test;

import java.util.HashMap;

import org.testng.annotations.DataProvider;

import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;
import com.Features.C2SCardGroup;
import com.classes.BaseTest;
import com.commons.ExcelI;

public class PreRequisite_C2SCardGroupCreation extends BaseTest {
	
	static boolean TestCaseCounter = false;

	@Test(dataProvider="serviceData")
	public void c2sCardGroupGroupCreation(int rowNum, String serviceName, String subService) throws InterruptedException{
		
		// Pushing Start Test case to Logger
		Log.startTestCase(this.getClass().getName());

		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false) {
			test = extent.createTest("[Pre-Requisite]C2S Card Group Creation");
			TestCaseCounter = true;
		}

		/*
		 * Test Case - To create C2S Card Group through Network Admin as per the DataProvider
		 */
		currentNode = test.createNode("To verify that Network Admin is able to create C2S Card Group for " + serviceName + " service & " + subService + " sub-service");
		currentNode.assignCategory("Pre-Requisite");
		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		HashMap<String, String> mapInfo = (HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		String cardGroupName= mapInfo.get("CARDGROUPNAME");
		c2sCardGroup.writeCardGroupToExcel(cardGroupName, rowNum);
		
		//Pushing End Test Case to Logger
		Log.endTestCase(this.getClass().getName());
	}
	
	@DataProvider(name = "serviceData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[rowCount][3];
		for (int i = 1, j = 0; i <= rowCount; i++, j++) {
			categoryData[j][0] = i;
			categoryData[j][1] = ExcelUtility.getCellData(i, 1);
			categoryData[j][2] = ExcelUtility.getCellData(i, 2);
		}
		return categoryData;
	}

}
