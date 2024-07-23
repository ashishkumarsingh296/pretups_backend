package com.testscripts.smoke;

import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.GradeManagement;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author tinky.sharma
 * @rewritten krishan.chawla
 * Smoke Class for Grade Creation
 */
public class Smoke_GradeCreation extends BaseTest {
		
	static boolean TestCaseCounter = false;
	
	/*
	 * Test Case to Create Grade.
	 */
	@Test(dataProvider = "categoryData")
	public void gradeCreation(int rowNum, String domainName, String categoryName) {
		
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) {
			test = extent.createTest("[Smoke]Grade Creation");
			TestCaseCounter = true;
		}
		
		GradeManagement GradeManagement = new GradeManagement(driver);
		
		currentNode=test.createNode("To verify that Super Admin is able to perform Grade Creation for " + categoryName + " category");
		currentNode.assignCategory("Smoke");
        Map<String, String> dataMap = GradeManagement.addGrade(domainName, categoryName);
		
		String DefaultGradeName = GradeManagement.getDefaultGrade(categoryName);
		
		System.out.println("Default Grade Value is: " + DefaultGradeName);
		
		String createdGrade= dataMap.get("GRADENAME");
		
		System.out.println("The Created Grade is:" +createdGrade);
		
		String actual = dataMap.get("ACTUALMESSAGE");
		
		currentNode = test
				.createNode("To verify that the proper Message is displayed on successful Grade Creation");
		String Message = MessagesDAO.prepareMessageByKey("domain.addgrade.message.success");
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		
		Log.endTestCase(this.getClass().getName());
	}
	
	//Data Provider
		@DataProvider(name = "categoryData")
		public Object[][] TestDataFeed() {

			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int rowCount = ExcelUtility.getRowCount();

			Object[][] categoryData = new Object[1][3];
			
				categoryData[0][0] = 1;
				categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
				categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
			
			return categoryData;
		}
		
}
