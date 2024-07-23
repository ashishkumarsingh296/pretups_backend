package com.testscripts.sit;

import java.text.MessageFormat;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.GradeManagement;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_GRADE_CREATION)
public class SIT_GradeCreation extends BaseTest{
	String gradeName;
	String gradeCode;
	String assignCategory="SIT";
	static String moduleCode;

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
	@TestManager(TestKey = "PRETUPS-912") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_DefaultgradeCreation(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_Grade_Creation";
		Log.startTestCase(methodName);
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITGRADECREATION1").getModuleCode();

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITGRADECREATION1").getExtentCase(),categoryName));
		currentNode.assignCategory(assignCategory);
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
			Assertion.assertPass("Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + Message + "] but found [" + actual + "]");
			Assertion.assertFail("Message Validation Failed");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-913") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_DefaultgradeDeletion(int rowNum, String domainName, String categoryName) {
		final String methodName = "Test_Grade_Creation";
		Log.startTestCase(methodName);

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITGRADECREATION2").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String DefaultGradeName = GradeManagement.getDefaultGrade(categoryName);
		System.out.println("Default Grade Value is: " + DefaultGradeName);

		String actual = GradeManagement.deleteDefaultGrade(domainName, categoryName, DefaultGradeName);

		String Message = MessagesDAO.prepareMessageByKey("domains.deletegrade.deletenotsuccess");
		if (actual.equals(Message))
			Assertion.assertPass("Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + Message + "] but found [" + actual + "]");
			Assertion.assertFail("Message Validation Failed");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-914") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void c_GradeCreationWithDuplicateGradeCode(int rowNum, String domainName, String categoryName) {
		final String methodName = "Test_Grade_Creation";
		Log.startTestCase(methodName);

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITGRADECREATION3").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Log.info("The gradeName is:"+gradeName);
		Log.info("The grade code is:" +gradeCode);

		Map<String, String> dataMap = GradeManagement.AddGrade_DuplicategradeCodeValidation(domainName, categoryName, gradeCode);
		
		String actual = dataMap.get("ACTUALMESSAGE");

		
		String Message = MessagesDAO.prepareMessageByKey("domain.addgrade.error.gradecode.alreadyexists");
		if (actual.equals(Message))
			Assertion.assertPass("Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + Message + "] but found [" + actual + "]");
			Assertion.assertFail("Message Validation Failed");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-915") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void d_GradeCreationWithDuplicateGradeName(int rowNum, String domainName, String categoryName) {
		final String methodName = "Test_Grade_Creation";
		Log.startTestCase(methodName);

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITGRADECREATION4").getExtentCase());
		currentNode.assignCategory(assignCategory);

		Log.info("The gradeName is:"+gradeName);
		Log.info("The grade code is:" +gradeCode);

		Map<String, String> dataMap = GradeManagement.AddGrade_DuplicategradeNameValidation(domainName, categoryName, gradeName);

		String actual = dataMap.get("ACTUALMESSAGE");

		
		String Message = MessagesDAO.prepareMessageByKey("domain.addgrade.error.gradename.alreadyexists");
		if (actual.equals(Message))
			Assertion.assertPass("Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + Message + "] but found [" + actual + "]");
			Assertion.assertFail("Message Validation Failed");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}




	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-916") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void e_GradeCreationWithBlankGradeCode(int rowNum, String domainName, String categoryName) {
		final String methodName = "Test_Grade_Creation";
		Log.startTestCase(methodName);

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITGRADECREATION5").getExtentCase());
		currentNode.assignCategory(assignCategory);



		Map<String, String> dataMap = GradeManagement.AddGradeWithBlankGradeCode(domainName, categoryName);

		String actual = dataMap.get("ACTUALMESSAGE");

		
		String Message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("domain.viewgradedetails.label.gradecode")); 
		if (actual.equals(Message))
			Assertion.assertPass("Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + Message + "] but found [" + actual + "]");
			Assertion.assertFail("Message Validation Failed");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-917") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void f_GradeCreationWithBlankGradeName(int rowNum, String domainName, String categoryName) {
		final String methodName = "Test_Grade_Creation";
		Log.startTestCase(methodName);

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITGRADECREATION6").getExtentCase());
		currentNode.assignCategory(assignCategory);



		Map<String, String> dataMap = GradeManagement.AddGradeWithBlankGradeName(domainName, categoryName);

		String actual = dataMap.get("ACTUALMESSAGE");


		String Message = MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("domain.viewgradedetails.label.gradename")); 
		if (actual.equals(Message))
			Assertion.assertPass("Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + Message + "] but found [" + actual + "]");
			Assertion.assertFail("Message Validation Failed");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
