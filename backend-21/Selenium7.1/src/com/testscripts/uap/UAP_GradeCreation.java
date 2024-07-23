package com.testscripts.uap;

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

public class UAP_GradeCreation extends BaseTest{

	static boolean TestCaseCounter = false;
	String gradeName;


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



	/*
	 * Test Case to Create Grade.
	 */
	@Test(dataProvider = "categoryData")
	public void a_gradeCreation(int rowNum, String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Grade Creation");
			TestCaseCounter = true;
		}

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode("To verify that Super Admin is able to perform Grade Creation for " + categoryName + " category");
		currentNode.assignCategory("UAP");
		Map<String, String> dataMap = GradeManagement.addGrade(domainName, categoryName);
		
		
		String DefaultGradeName = GradeManagement.getDefaultGrade(categoryName);

		System.out.println("Default Grade Value is: " + DefaultGradeName);

		gradeName= dataMap.get("GRADENAME");

		System.out.println("The Created Grade is:" +gradeName);

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



	@Test(dataProvider = "categoryData")
	public void b_gradeModification(int rowNum, String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Grade Creation");
			TestCaseCounter = true;
		}

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode("To verify that Super Admin is able to modify Grade for " + categoryName + " category");
		currentNode.assignCategory("UAP");
		
		System.out.println("The created Grade Name in the above TestCase is:" +gradeName);
		
		Map<String, String> dataMap = GradeManagement.modifyGrade(domainName, categoryName, gradeName);
		
		
		
		gradeName = dataMap.get("GRADENAME");
		System.out.println("The new gradeName is :" +gradeName);
		String actual = dataMap.get("ACTUALMESSAGE");

		currentNode = test
				.createNode("To verify that the proper Message is displayed on successful Grade Modification");
		String Message = MessagesDAO.prepareMessageByKey("domains.modifygrade.success");
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}
	
	
	
	@Test(dataProvider = "categoryData")
	public void c_gradeDeletion(int rowNum, String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Grade Creation");
			TestCaseCounter = true;
		}

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode("To verify that Super Admin is able to delete Grade for " + categoryName + " category");
		currentNode.assignCategory("UAP");
		
		System.out.println("The created Grade Name in the above TestCase is:" +gradeName);
		
		String actual = GradeManagement.deleteGrade(domainName, categoryName, gradeName);
		
		
		
		
		currentNode = test.createNode("To verify that the proper Message is displayed on successful Grade Deletion");
		String Message = MessagesDAO.prepareMessageByKey("domains.deletegrade.deletesuccess");
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}
	

}
