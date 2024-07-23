package com.testscripts.sit;

import org.testng.annotations.DataProvider;

import org.testng.annotations.Test;


import com.Features.P2PReconciliation;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExcelUtility;

import com.utils.Log;
import com.utils._masterVO;

public class SIT_P2PReconciliation extends BaseTest {

	static boolean TestCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;

	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "P2P Services Sheet");
		//int j = 0;
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
	public void a_P2PReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]P2PReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view P2P Reconciliation when From Date is not selected.");
		currentNode.assignCategory("SIT");
		P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
		String result = p2PReconciliation.p2PReconciliationlink_fromDateNull();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when From Date is not selected for P2P Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("p2p.reconciliation.selectservicetype.label.fromdate"));

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
	public void b_P2PReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]P2PReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view P2P Reconciliation when To Date is not selected.");
		currentNode.assignCategory("SIT");
		P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
		String result = p2PReconciliation.p2PReconciliationlink_toDateNull();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when To Date is not selected for P2P Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("p2p.reconciliation.selectservicetype.label.todate"));
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
	public void c_P2PReconciliation() {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]P2PReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view P2P Reconciliation when serviceType is not selected.");
		currentNode.assignCategory("SIT");
		P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
		String result = p2PReconciliation.p2PReconciliationlink_serviceNameNull();

		currentNode = test.createNode(
				"To verify that the proper Message is displayed when serviceName is not selected for P2P Reconciliation");
		currentNode.assignCategory("SIT");
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("p2p.reconciliation.selectservicetype.label.servicename"));
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
	public void e_P2PReconciliation(String serviceType, String selectorType) {
		Log.startTestCase(this.getClass().getName());
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]P2PReconciliation");
			TestCaseCounter = true;
		}
		currentNode = test.createNode(
				"To verify that Network Admin is Unable to view P2P Reconciliation when difference between from date and to date is more than 30 days.");
		currentNode.assignCategory("SIT");
		P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
		String result = p2PReconciliation.p2PReconciliationlink_dateDiffMoreThan30Days(serviceType);
		currentNode = test.createNode(
				"To verify that the proper Message is displayed when difference between from date and to date is more than 30 days for P2P Reconciliation");
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
			public void f_P2PReconciliation() {
				Log.startTestCase(this.getClass().getName());
				if (TestCaseCounter == false) {
					test=extent.createTest("[SIT]P2PReconciliation");
					TestCaseCounter = true;
				}
				currentNode = test.createNode(
						"To verify that Network Admin is Unable to view P2P Reconciliation when format for from date is invalid.");
				currentNode.assignCategory("SIT");
				P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
			String result = p2PReconciliation.p2PReconciliationlink_invalidFromDateFormat();

				currentNode = test.createNode(
						"To verify that the proper Message is displayed when format for from date is invalid for P2P Reconciliation");
				currentNode.assignCategory("SIT");
				String message = MessagesDAO.prepareMessageByKey("btsl.date.error.format",
						MessagesDAO.getLabelByKey("p2p.reconciliation.selectservicetype.label.fromdate"));
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
					public void g_P2PReconciliation() {
						Log.startTestCase(this.getClass().getName());
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]P2PReconciliation");
							TestCaseCounter = true;
						}
						currentNode = test.createNode(
								"To verify that Network Admin is Unable to view P2P Reconciliation when format for to date is invalid.");
						currentNode.assignCategory("SIT");
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
			String result = p2PReconciliation.p2PReconciliationlink_invalidToDateFormat();

						currentNode = test.createNode(
								"To verify that the proper Message is displayed when format for to date is invalid for P2P Reconciliation");
						currentNode.assignCategory("SIT");
						String message = MessagesDAO.prepareMessageByKey("btsl.date.error.format",
								MessagesDAO.getLabelByKey("p2p.reconciliation.selectservicetype.label.todate"));
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
					public void h_P2PReconciliation() {
						Log.startTestCase(this.getClass().getName());
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]P2PReconciliation");
							TestCaseCounter = true;
						}
						currentNode = test.createNode(
								"To verify that Network Admin is Unable to view P2P Reconciliation when From Date is greater than Current Date.");
						currentNode.assignCategory("SIT");
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
			String result = p2PReconciliation.p2PReconciliationlink_fromDateGreaterThanCurrDate();

						currentNode = test.createNode(
								"To verify that the proper Message is displayed when  when From Date is greater than Current Date for P2P Reconciliation");
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
					public void i_P2PReconciliation() {
						Log.startTestCase(this.getClass().getName());
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]P2PReconciliation");
							TestCaseCounter = true;
						}
						currentNode = test.createNode(
								"To verify that Network Admin is Unable to view P2P Reconciliation when To Date is greater than Current Date.");
						currentNode.assignCategory("SIT");
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
			           String result = p2PReconciliation.p2PReconciliationlink_toDateGreaterThanCurrDate();

						currentNode = test.createNode(
								"To verify that the proper Message is displayed when  when To Date is greater than Current Date for P2P Reconciliation");
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
					public void j_P2PReconciliation() {
						Log.startTestCase(this.getClass().getName());
						if (TestCaseCounter == false) {
							test=extent.createTest("[SIT]P2PReconciliation");
							TestCaseCounter = true;
						}
						currentNode = test.createNode(
								"To verify that Network Admin is Unable to view P2P Reconciliation when To Date is less than From Date.");
						currentNode.assignCategory("SIT");
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
			String result = p2PReconciliation.p2Preconciliationpage1Reconciliationlink_toDateLessThanForDate();

						currentNode = test.createNode(
								"To verify that the proper Message is displayed when To Date is less than From Date for P2P Reconciliation");
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
					public void k_P2PReconciliation(String serviceType, String selectorType) {
						Log.startTestCase(this.getClass().getName());
						if (TestCaseCounter == false) {
							test = extent.createTest("[SIT]P2PReconciliation");
							TestCaseCounter = true;
						}
						currentNode = test.createNode(
								"To verify that Network Admin is able to view P2P Reconciliation with unAmbigious Transactions when all fields are selected.");
						currentNode.assignCategory("SIT");
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
						String validationValue = MessagesDAO.getLabelByKey("p2p.reconciliation.displayreconlist.msg.nodata");
						boolean[] result = p2PReconciliation.p2PReconciliationlink_checkAmbigious(serviceType, validationValue, selectorType);

						currentNode = test.createNode(
								"To verify that Network Admin is able to view P2P Reconciliation with unAmbigious Transactions when all fields are selected.");
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
					public void l_P2PReconciliation(String serviceType, String selectorType) {
						Log.startTestCase(this.getClass().getName());
						if (TestCaseCounter == false) {
							test = extent.createTest("[SIT]P2PReconciliation");
							TestCaseCounter = true;
						}
						currentNode = test.createNode(
								"To verify that Network Admin is able to view P2P Reconciliation with Ambigious Transactions when all fields are selected.");
						currentNode.assignCategory("SIT");
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
						
						String result = p2PReconciliation.p2PReconciliationlink_Success(serviceType, selectorType);
						
						currentNode = test.createNode(
								"To verify that Network Admin is able to view P2P Reconciliation with Ambigious Transactions when all fields are selected.");
						currentNode.assignCategory("SIT");
						String message = MessagesDAO.prepareMessageByKey("p2p.reconciliation.displaydetail.updatemsg.success");
						String message1= MessagesDAO.getLabelByKey("p2p.reconciliation.displayreconlist.msg.nodata");
						
						if (result.equals(message))
							currentNode.log(Status.PASS, "Message Validation Successful");
						else if(result.equals(message1)) {
							currentNode.log(Status.SKIP, "No transfer found for the input values.");
							}
						else
							currentNode.log(Status.FAIL, "Message Validation Failed");

						Log.endTestCase(this.getClass().getName());
					}
					
					//DB_Verification
					@Test(dataProvider = "categoryData")
					public void m_P2PReconciliation(String serviceType, String selectorType) {
						Log.startTestCase(this.getClass().getName());
						if (TestCaseCounter == false) {
							test = extent.createTest("[SIT]P2PReconciliation");
							TestCaseCounter = true;
						}
						currentNode = test.createNode(
								"To verify that Network Admin is able to change status of Ambigiuous Transaction by clicking on success button.");
						currentNode.assignCategory("SIT");
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
						
						String result = p2PReconciliation.p2PReconciliationlink_dbVerification(serviceType, selectorType);
						
						currentNode = test.createNode(
								"To verify that Network Admin is able to change status of Ambigiuous Transaction by clicking on success button.");
						currentNode.assignCategory("SIT");
						String message1= MessagesDAO.getLabelByKey("p2p.reconciliation.displayreconlist.msg.nodata");
						
						if (result == "200")
							currentNode.log(Status.PASS, "Message Validation Successful");
						else if(result.equals(message1)) {
							currentNode.log(Status.SKIP, "No transfer found for the input values.");
							}
						else
							currentNode.log(Status.FAIL, "Message Validation Failed");

						Log.endTestCase(this.getClass().getName());
					}
					
					// Fail
					@Test(dataProvider = "categoryData")
					public void n_P2PReconciliation(String serviceType, String selectorType) {
						Log.startTestCase(this.getClass().getName());
						if (TestCaseCounter == false) {
							test = extent.createTest("[SIT]P2PReconciliation");
							TestCaseCounter = true;
						}
						currentNode = test.createNode(
								"To verify that Network Admin is able to view P2P Reconciliation with Ambigious Transactions when all fields are selected.");
						currentNode.assignCategory("SIT");
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
						
						String result = p2PReconciliation.p2PReconciliationlink_Success(serviceType, selectorType);
						
						currentNode = test.createNode(
								"To verify that Network Admin is able to view P2P Reconciliation with Ambigious Transactions when all fields are selected.");
						currentNode.assignCategory("SIT");
						String message = MessagesDAO.prepareMessageByKey("p2p.reconciliation.displaydetail.updatemsg.success");
						String message1= MessagesDAO.getLabelByKey("p2p.reconciliation.displayreconlist.msg.nodata");
						
						if (result.equals(message))
							currentNode.log(Status.PASS, "Message Validation Successful");
						else if(result.equals(message1)) {
							currentNode.log(Status.SKIP, "No transfer found for the input values.");
							}
						else
							currentNode.log(Status.FAIL, "Message Validation Failed");

						Log.endTestCase(this.getClass().getName());
					}	
}
