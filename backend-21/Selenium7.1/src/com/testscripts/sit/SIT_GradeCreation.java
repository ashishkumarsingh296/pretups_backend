package com.testscripts.sit;

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

public class SIT_GradeCreation extends BaseTest{

	static boolean TestCaseCounter = false;
	String gradeName;
	String gradeCode;


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


	@Test(dataProvider = "categoryData")
	public void a_DefaultgradeCreation(int rowNum, String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Grade Creation");
			TestCaseCounter = true;
		}

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode("To verify that Super Admin is able to perform Default Grade Creation for " + categoryName + " category");
		currentNode.assignCategory("SIT");
		Map<String, String> dataMap = GradeManagement.AddDefaultGrade(domainName, categoryName);


		String DefaultGradeName = GradeManagement.getDefaultGrade(categoryName);

		System.out.println("Default Grade Value is: " + DefaultGradeName);

		gradeName= dataMap.get("GRADENAME");

		System.out.println("The Created Grade is:" +gradeName);

		String actual = dataMap.get("ACTUALMESSAGE");
		gradeName= dataMap.get("GRADENAME");
		Log.info("The grade Name is:" +gradeName);
		gradeCode = dataMap.get("GRADECODE");
		Log.info("The grade Name is:" +gradeCode);

		
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
	public void b_DefaultgradeDeletion(int rowNum, String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Grade Creation");
			TestCaseCounter = true;
		}

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode("To verify that Super Admin is not able to delete a default grade");
		currentNode.assignCategory("SIT");

		String DefaultGradeName = GradeManagement.getDefaultGrade(categoryName);
		System.out.println("Default Grade Value is: " + DefaultGradeName);

		String actual = GradeManagement.deleteDefaultGrade(domainName, categoryName, DefaultGradeName);


		
		String Message = MessagesDAO.prepareMessageByKey("domains.deletegrade.deletenotsuccess");
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}




	@Test(dataProvider = "categoryData")
	public void c_GradeCreationWithDuplicateGradeCode(int rowNum, String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Grade Creation");
			TestCaseCounter = true;
		}

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode("To verify that Super Admin is not able to create a grade with Duplicate Grade Code");
		currentNode.assignCategory("SIT");

		Log.info("The gradeName is:"+gradeName);
		Log.info("The grade code is:" +gradeCode);

		Map<String, String> dataMap = GradeManagement.AddGrade_DuplicategradeCodeValidation(domainName, categoryName, gradeCode);
		
		String actual = dataMap.get("ACTUALMESSAGE");

		
		String Message = MessagesDAO.prepareMessageByKey("domain.addgrade.error.gradecode.alreadyexists");
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}



	@Test(dataProvider = "categoryData")
	public void d_GradeCreationWithDuplicateGradeName(int rowNum, String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Grade Creation");
			TestCaseCounter = true;
		}

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode("To verify that Super Admin is not able to create a grade with Duplicate Grade Name");
		currentNode.assignCategory("SIT");

		Log.info("The gradeName is:"+gradeName);
		Log.info("The grade code is:" +gradeCode);

		Map<String, String> dataMap = GradeManagement.AddGrade_DuplicategradeNameValidation(domainName, categoryName, gradeName);

		String actual = dataMap.get("ACTUALMESSAGE");

		
		String Message = MessagesDAO.prepareMessageByKey("domain.addgrade.error.gradename.alreadyexists");
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}




	@Test(dataProvider = "categoryData")
	public void e_GradeCreationWithBlankGradeCode(int rowNum, String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Grade Creation");
			TestCaseCounter = true;
		}

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode("To verify that Super Admin is not able to create a grade without Grade Code");
		currentNode.assignCategory("SIT");



		Map<String, String> dataMap = GradeManagement.AddGradeWithBlankGradeCode(domainName, categoryName);

		String actual = dataMap.get("ACTUALMESSAGE");

		
		String Message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("domain.viewgradedetails.label.gradecode")); 
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}



	@Test(dataProvider = "categoryData")
	public void f_GradeCreationWithBlankGradeName(int rowNum, String domainName, String categoryName) {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]Grade Creation");
			TestCaseCounter = true;
		}

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode("To verify that Super Admin is not able to create a grade without Grade Name");
		currentNode.assignCategory("SIT");



		Map<String, String> dataMap = GradeManagement.AddGradeWithBlankGradeName(domainName, categoryName);

		String actual = dataMap.get("ACTUALMESSAGE");


		String Message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("domain.viewgradedetails.label.gradename")); 
		if (actual.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

}
