package com.testscripts.prerequisites;

import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.GradeManagement;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author tinky.sharma
 * @rewritten krishan.chawla Pre-Requisite Class for Grade Creation
 */
public class PreRequisite_GradeCreation extends BaseTest {

	static boolean TestCaseCounter = false;

	// Test Case to Create Grade. The test as well writes the Default Grade to Channel Users Hierarchy
	@Test(dataProvider = "categoryData")
	public void gradeCreation(int rowNum, String domainName, String categoryName) {

		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false) {
			test = extent.createTest("[Pre-Requisite]Grade Creation");
			TestCaseCounter = true;
		}

		// Initializing Grade Management Feature class with current driver
		GradeManagement GradeManagement = new GradeManagement(driver);
		
		Log.startTestCase(this.getClass().getName());
		String DefaultGradeName = GradeManagement.getDefaultGrade(categoryName);
		
		/*
		 * Test Case - To create Grades for respective categories
		 */
		currentNode = test.createNode("To verify that Super Admin is able to perform Grade Creation for " + categoryName + " category");
		currentNode.assignCategory("Pre-Requisite");
		if (DefaultGradeName!=null) {
			GradeManagement.writeGradeToSheet(rowNum, DefaultGradeName);
			currentNode.log(Status.SKIP, "Grade for " + categoryName + " category already exists hence Test Case Skipped"); 
		} else {
		Map<String, String> dataMap = GradeManagement.addGrade(domainName, categoryName);
		String Created_GradeName = dataMap.get("GRADENAME");
		GradeManagement.writeGradeToSheet(rowNum, Created_GradeName);
		}
		
		Log.endTestCase(this.getClass().getName());
	}

	// Data Provider
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] categoryData = new Object[rowCount][3];
		for (int i = 1, j = 0; i <= rowCount; i++, j++) {
			categoryData[j][0] = i;
			categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		}
		return categoryData;
	}
}
