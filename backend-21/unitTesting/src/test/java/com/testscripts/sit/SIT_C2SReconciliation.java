package com.testscripts.sit;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SReconciliation;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_C2S_RECONCILIATION)
public class SIT_C2SReconciliation extends BaseTest {
	NetworkAdminHomePage networkAdminHomePage;
	String assignCategory="SIT";
	
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "C2S Services Sheet");
		// int j = 0;
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[1][2];
		for (int i = 1; i <= rowCount;) {
			categoryData[0][0] = ExcelUtility.getCellData(0, "NAME", i);
			categoryData[0][1] = ExcelUtility.getCellData(0, "SERVICE_TYPE", i);
			break;
		}

		return categoryData;
	}

	// From Date is null
	@Test
	@TestManager(TestKey = "PRETUPS-1116") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_C2SReconciliation() {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_fromDateNull();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("c2s.reports.transferreconciliation.label.fromdate"));

		Assertion.assertEquals(result, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// To Date is null
	@Test
	@TestManager(TestKey = "PRETUPS-1117") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_C2SReconciliation() {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_toDateNull();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("c2s.reports.transferreconciliation.label.todate"));

		Assertion.assertEquals(result, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// serviceName is null
	@Test
	@TestManager(TestKey = "PRETUPS-1118") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void c_C2SReconciliation() {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
		int rowCount=ExcelUtility.getRowCount();
		if(rowCount>1){
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_serviceNameNull();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("c2s.reports.transferreconciliation.label.servicename"));
		Assertion.assertEquals(result, message);
		Assertion.completeAssertions();
		}
		
		Log.endTestCase(methodName);
	}

	
	// Difference between from date and to date more than 30 days
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1119") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void e_C2SReconciliation(String serviceType, String selectorType) {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_dateDiffMoreThan30Days(serviceType);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("btsl.date.error.datecompare", "30");

		Assertion.assertEquals(result, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	/*// Invalid From Date Format
	@Test
	@TestManager(TestKey = "PRETUPS-815") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void f_C2SReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SRECONCILE1").getModuleCode());
			TestCaseCounter = true;
		}
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_invalidFromDateFormat();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("btsl.date.error.format",
				MessagesDAO.getLabelByKey("c2s.reports.transferreconciliation.label.fromdate"));
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		Validator.messageCompare(result, message);

		Log.endTestCase(methodName);
	}*/

	/*// Invalid To Date Format
	@Test
	@TestManager(TestKey = "PRETUPS-815") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void g_C2SReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITC2SRECONCILE1").getModuleCode());
			TestCaseCounter = true;
		}
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_invalidToDateFormat();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE12").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("btsl.date.error.format",
				MessagesDAO.getLabelByKey("c2s.reports.transferreconciliation.label.todate"));
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		Validator.messageCompare(result, message);
		Log.endTestCase(methodName);
	}
*/
	// From Date Greater Than Current Date
	@Test
	@TestManager(TestKey = "PRETUPS-1120") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void h_C2SReconciliation() {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_fromDateGreaterThanCurrDate();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.fromdatebeforecurrentdate");

		Assertion.assertEquals(result, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// To Date Greater Than Current Date
	@Test
	@TestManager(TestKey = "PRETUPS-1121") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void i_C2SReconciliation() {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE15").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_toDateGreaterThanCurrDate();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE16").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.todatebeforecurrentdate");

		Assertion.assertEquals(result, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// To Date Less Than From Date
	@Test
	@TestManager(TestKey = "PRETUPS-1122") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void j_C2SReconciliation() {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE17").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_toDateLessThanForDate();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE18").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.fromdatebeforetodate");

		Assertion.assertEquals(result, message);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Check Ambiguous
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1123") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void k_C2SReconciliation(String serviceType, String selectorType) {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE19").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String validationValue = MessagesDAO.getLabelByKey("c2s.reconciliation.displayreconlist.msg.nodata");
		boolean[] result = c2SReconciliation.c2SReconciliationlink_checkAmbigious(serviceType, validationValue, selectorType);

		Assertion.assertEquals(String.valueOf(result[1]), String.valueOf(result[0]));
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Success
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1124") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void l_C2SReconciliation(String serviceType, String selectorType) {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE21").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);

		String result = c2SReconciliation.c2SReconciliationlink_Success(serviceType, selectorType);

		String message = MessagesDAO.prepareMessageByKey("c2s.reconciliation.displaydetail.updatemsg.success");
		String message1 = MessagesDAO.getLabelByKey("c2s.reconciliation.displayreconlist.msg.nodata");
		String message2 = MessagesDAO.getLabelByKey("9007");
		
		if (result.equals(message))
			Assertion.assertPass("Message Validation Successful");
		else if (result.equals(message1)) {
			Assertion.assertSkip("No transfer found for the input values.");
		}else if (result.equals(message2)) {
			Assertion.assertSkip("No latest transfer found for the input values.");
		}else{
			Assertion.assertFail("Message Validation Failed");
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// DB_Verification
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1125") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void m_C2SReconciliation(String serviceType, String selectorType) throws InterruptedException {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE22").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);

		String result = c2SReconciliation.c2SReconciliationlink_dbVerification(serviceType, selectorType);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE23").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message1 = MessagesDAO.getLabelByKey("c2s.reconciliation.displayreconlist.msg.nodata");
		String message2 = MessagesDAO.getLabelByKey("9007");
		if (result.equals("200"))
			Assertion.assertPass("Message Validation Successful");
		else if (result.equals(message1)) {
			Assertion.assertSkip("No transfer found for the input values.");
		}else if (result.equals(message2)) {
			Assertion.assertSkip("No latest transfer found for the input values.");
		} else{
			Assertion.assertFail("Message Validation Failed");
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Fail
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1126") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void n_C2SReconciliation(String serviceType, String selectorType) {
		final String methodName = "Test_C2S_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITC2SRECONCILE24").getExtentCase());
		currentNode.assignCategory(assignCategory);
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);

		String result = c2SReconciliation.c2SReconciliationlink_Success(serviceType, selectorType);

		String message = MessagesDAO.prepareMessageByKey("c2s.reconciliation.displaydetail.updatemsg.success");
		String message1 = MessagesDAO.getLabelByKey("c2s.reconciliation.displayreconlist.msg.nodata");
		String message2 = MessagesDAO.getLabelByKey("9007");
		
		if (result.equals(message))
			Assertion.assertPass("Message Validation Successful");
		else if (result.equals(message1)) {
			Assertion.assertSkip("No transfer found for the input values.");
		} else if (result.equals(message2)) {
			Assertion.assertSkip("No latest transfer found for the input values.");
		} else
		{
			Assertion.assertFail("Message Validation Failed");
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
