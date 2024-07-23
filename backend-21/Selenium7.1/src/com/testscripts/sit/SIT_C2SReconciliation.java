package com.testscripts.sit;

import org.testng.annotations.DataProvider;

import org.testng.annotations.Test;

import com.Features.C2SReconciliation;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;

import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExcelUtility;

import com.utils.Log;

import com.utils._masterVO;

public class SIT_C2SReconciliation extends BaseTest {

	static boolean TestCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;

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
	public void a_C2SReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view C2S Reconciliation when From Date is not selected.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_fromDateNull();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when From Date is not selected for C2S Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("c2s.reports.transferreconciliation.label.fromdate"));
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// To Date is null
	@Test
	public void b_C2SReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view C2S Reconciliation when To Date is not selected.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_toDateNull();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when To Date is not selected for C2S Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("c2s.reports.transferreconciliation.label.todate"));
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// serviceName is null
	@Test
	public void c_C2SReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view C2S Reconciliation when serviceType is not selected.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_serviceNameNull();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when serviceName is not selected for C2S Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("c2s.reports.transferreconciliation.label.servicename"));
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	
	// Difference between from date and to date more than 30 days
	@Test(dataProvider = "categoryData")
	public void e_C2SReconciliation(String serviceType, String selectorType) {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view C2S Reconciliation when difference between from date and to date is more than 30 days.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_dateDiffMoreThan30Days(serviceType);

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when difference between from date and to date is more than 30 days for C2S Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("btsl.date.error.datecompare", "30");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// Invalid From Date Format
	@Test
	public void f_C2SReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view C2S Reconciliation when format for from date is invalid.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_invalidFromDateFormat();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when format for from date is invalid for C2S Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("btsl.date.error.format",
				MessagesDAO.getLabelByKey("c2s.reports.transferreconciliation.label.fromdate"));
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// Invalid To Date Format
	@Test
	public void g_C2SReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view C2S Reconciliation when format for to date is invalid.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_invalidToDateFormat();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when format for to date is invalid for C2S Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("btsl.date.error.format",
				MessagesDAO.getLabelByKey("c2s.reports.transferreconciliation.label.todate"));
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// From Date Greater Than Current Date
	@Test
	public void h_C2SReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view C2S Reconciliation when From Date is greater than Current Date.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_fromDateGreaterThanCurrDate();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when  when From Date is greater than Current Date for C2S Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.fromdatebeforecurrentdate");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// To Date Greater Than Current Date
	@Test
	public void i_C2SReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view C2S Reconciliation when To Date is greater than Current Date.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_toDateGreaterThanCurrDate();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when  when To Date is greater than Current Date for C2S Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.todatebeforecurrentdate");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// To Date Less Than From Date
	@Test
	public void j_C2SReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view C2S Reconciliation when To Date is less than From Date.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String result = c2SReconciliation.c2SReconciliationlink_toDateLessThanForDate();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when  when To Date is less than From Date for C2S Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.fromdatebeforetodate");
		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// Check Ambiguous
	@Test(dataProvider = "categoryData")
	public void k_C2SReconciliation(String serviceType, String selectorType) {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is able to check Ambigious Transactions for C2S Reconciliation.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);
		String validationValue = MessagesDAO.getLabelByKey("c2s.reconciliation.displayreconlist.msg.nodata");
		boolean[] result = c2SReconciliation.c2SReconciliationlink_checkAmbigious(serviceType, validationValue, selectorType);

		currentNode = test.createNode(
				"To verify that Network Admin is able to view C2S Reconciliation with Ambigious Transactions when all fields are selected.");
		currentNode.assignCategory("SIT");

		if (result[0] == result[1])
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		Log.endTestCase(this.getClass().getName());
	}

	// Success
	@Test(dataProvider = "categoryData")
	public void l_C2SReconciliation(String serviceType, String selectorType) {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is able to make Ambigious Transactions successful for  C2S Reconciliation.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);

		String result = c2SReconciliation.c2SReconciliationlink_Success(serviceType, selectorType);

		currentNode = test.createNode(
				"To verify that Network Admin is able to view C2S Reconciliation with Ambigious Transactions when all fields are selected.");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("p2p.reconciliation.displaydetail.updatemsg.success");
		String message1 = MessagesDAO.getLabelByKey("c2s.reconciliation.displayreconlist.msg.nodata");

		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else if (result.equals(message1)) {
			currentNode.log(Status.SKIP, "No transfer found for the input values.");
		} else
			currentNode.log(Status.FAIL, "Message Validation Failed");

		Log.endTestCase(this.getClass().getName());
	}

	// DB_Verification
	@Test(dataProvider = "categoryData")
	public void m_C2SReconciliation(String serviceType, String selectorType) {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is able verify status change of Ambigious Transaction after making it success.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);

		String result = c2SReconciliation.c2SReconciliationlink_dbVerification(serviceType, selectorType);

		currentNode = test.createNode(
				"To verify that Network Admin is able to change status of Ambigiuous Transaction by clicking on success button.");
		currentNode.assignCategory("SIT");
		String message1 = MessagesDAO.getLabelByKey("c2s.reconciliation.displayreconlist.msg.nodata");

		if (result == "200")
			currentNode.log(Status.PASS, "Message Validation Successful");
		else if (result.equals(message1)) {
			currentNode.log(Status.SKIP, "No transfer found for the input values.");
		} else
			currentNode.log(Status.FAIL, "Message Validation Failed");

		Log.endTestCase(this.getClass().getName());
	}

	// Fail
	@Test(dataProvider = "categoryData")
	public void n_C2SReconciliation(String serviceType, String selectorType) {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]C2SReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is able to fail Ambigious Transactions for  C2S Reconciliation.");
		currentNode.assignCategory("SIT");
		C2SReconciliation c2SReconciliation = new C2SReconciliation(driver);

		String result = c2SReconciliation.c2SReconciliationlink_Success(serviceType, selectorType);

		currentNode = test.createNode(
				"To verify that Network Admin is able to view C2S Reconciliation with Ambigious Transactions when all fields are selected.");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("p2p.reconciliation.displaydetail.updatemsg.success");
		String message1 = MessagesDAO.getLabelByKey("c2s.reconciliation.displayreconlist.msg.nodata");

		if (result.equals(message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else if (result.equals(message1)) {
			currentNode.log(Status.SKIP, "No transfer found for the input values.");
		} else
			currentNode.log(Status.FAIL, "Message Validation Failed");

		Log.endTestCase(this.getClass().getName());
	}

}
