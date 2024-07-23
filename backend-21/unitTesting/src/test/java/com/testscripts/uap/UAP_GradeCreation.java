package com.testscripts.uap;

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

@ModuleManager(name = Module.UAP_GRADE_CREATION)
public class UAP_GradeCreation extends BaseTest{

	static boolean TestCaseCounter = false;
	String gradeName;
	String assignCategory="UAP";
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



	/*
	 * Test Case to Create Grade.
	 */
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-290") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_gradeCreation(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_gradeCreation";
        Log.startTestCase(methodName);
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("UGRADECREATION1").getModuleCode();

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PGRADECREATION1").getExtentCase(),categoryName));
		currentNode.assignCategory(assignCategory);
		Map<String, String> dataMap = GradeManagement.addGrade(domainName, categoryName);
		
		
		String DefaultGradeName = GradeManagement.getDefaultGrade(categoryName);

		System.out.println("Default Grade Value is: " + DefaultGradeName);

		gradeName= dataMap.get("GRADENAME");

		System.out.println("The Created Grade is:" +gradeName);

		String actual = dataMap.get("ACTUALMESSAGE");

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SGRADECREATION1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("domain.addgrade.message.success");
		Assertion.assertEquals(actual, Message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-291") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_gradeModification(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_gradeModification";
        Log.startTestCase(methodName);

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UGRADECREATION1").getExtentCase(),categoryName));
		currentNode.assignCategory(assignCategory);
		
		System.out.println("The created Grade Name in the above TestCase is:" +gradeName);
		
		Map<String, String> dataMap = GradeManagement.modifyGrade(domainName, categoryName, gradeName);
		
		
		
		gradeName = dataMap.get("GRADENAME");
		System.out.println("The new gradeName is :" +gradeName);
		String actual = dataMap.get("ACTUALMESSAGE");

		currentNode = test.createNode(_masterVO.getCaseMasterByID("UGRADECREATION2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("domains.modifygrade.success");
		Assertion.assertEquals(actual, Message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-294") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void c_gradeDeletion(int rowNum, String domainName, String categoryName) {

		final String methodName = "Test_gradeDeletion";
        Log.startTestCase(methodName);

		GradeManagement GradeManagement = new GradeManagement(driver);

		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UGRADECREATION3").getExtentCase(),categoryName));
		currentNode.assignCategory(assignCategory);
		
		System.out.println("The created Grade Name in the above TestCase is:" +gradeName);
		
		String actual = GradeManagement.deleteGrade(domainName, categoryName, gradeName);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UGRADECREATION4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("domains.deletegrade.deletesuccess");
		Assertion.assertEquals(actual, Message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

}
