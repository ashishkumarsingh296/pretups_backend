package com.testscripts.sit;

import java.util.HashSet;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PReconciliation;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_P2P_RECONCILIATION)
public class SIT_P2PReconciliation extends BaseTest {
	NetworkAdminHomePage networkAdminHomePage;
	String assignCategory="SIT";
	
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
	@TestManager(TestKey = "PRETUPS-1127") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_P2PReconciliation() {
		final String methodName = "Test_P2P_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
		String result = p2PReconciliation.p2PReconciliationlink_fromDateNull();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("p2p.reconciliation.selectservicetype.label.fromdate"));

		if (!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message) && result.equals(message))
			Assertion.assertPass("Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + message + "] but found [" + result + "]");
			Assertion.assertFail("Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// To Date is null
	@Test
	@TestManager(TestKey = "PRETUPS-1128") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_P2PReconciliation() {
		final String methodName = "Test_P2P_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
		String result = p2PReconciliation.p2PReconciliationlink_toDateNull();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("errors.required",
				MessagesDAO.getLabelByKey("p2p.reconciliation.selectservicetype.label.todate"));
		if (!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message) && result.equals(message))
			Assertion.assertPass("Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + message + "] but found [" + result + "]");
			Assertion.assertFail("Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// serviceName is null
    @Test
    @TestManager(TestKey = "PRETUPS-1129") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void c_P2PReconciliation() {
           final String methodName = "Test_P2P_Reconciliation";
           Log.startTestCase(methodName);
           currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE5").getExtentCase());
           currentNode.assignCategory(assignCategory);
           P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
           HashSet<String> hs = new HashSet<String>();
           ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.P2P_SERVICES_SHEET);
           int rowCount = ExcelUtility.getRowCount();
           for(int i=0;i<rowCount;i++)
           {
                  hs.add(ExcelUtility.getCellData(1, ExcelI.SERVICE_TYPE, i));
           }
           int serviceCount = hs.size();
           if(serviceCount<=1)
           {
                  Assertion.assertSkip("Only one service type is Present in System, so skipping this case");
           }
           else
           {
           String result = p2PReconciliation.p2PReconciliationlink_serviceNameNull();

           currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE6").getExtentCase());
           currentNode.assignCategory(assignCategory);
           String message = MessagesDAO.prepareMessageByKey("errors.required",
                         MessagesDAO.getLabelByKey("p2p.reconciliation.selectservicetype.label.servicename"));
           if (!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message) && result.equals(message))
                  Assertion.assertPass("Message Validation Successful");
           else {
                  Assertion.assertFail("Expected [" + message + "] but found [" + result + "]");
                  Assertion.assertFail("Message Validation Failed");
                  ExtentI.attachCatalinaLogs();
                  ExtentI.attachScreenShot();
           }
           }
           Assertion.completeAssertions();
           Log.endTestCase(methodName);
    }


	
	// Difference between from date and to date more than 30 days
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1132") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void e_P2PReconciliation(String serviceType, String selectorType) {
		final String methodName = "Test_P2P_Reconciliation";
		Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
		String result = p2PReconciliation.p2PReconciliationlink_dateDiffMoreThan30Days(serviceType);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = MessagesDAO.prepareMessageByKey("btsl.date.error.datecompare", "30");
		if (!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message) && result.equals(message))
			Assertion.assertPass("Message Validation Successful");
		else {
			Assertion.assertFail("Expected [" + message + "] but found [" + result + "]");
			Assertion.assertFail("Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	/*// Invalid From Date Format
			@Test
			public void f_P2PReconciliation() {
				Log.startTestCase(this.getClass().getName());
				if (TestCaseCounter == false) {
					test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PRECONILE1").getModuleCode());
					TestCaseCounter = true;
				}
				currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE9").getExtentCase());
				currentNode.assignCategory(assignCategory);
				P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
			String result = p2PReconciliation.p2PReconciliationlink_invalidFromDateFormat();

				currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE10").getExtentCase());
				currentNode.assignCategory(assignCategory);
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
							test=extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PRECONILE1").getModuleCode());
							TestCaseCounter = true;
						}
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE11").getExtentCase());
						currentNode.assignCategory(assignCategory);
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
			String result = p2PReconciliation.p2PReconciliationlink_invalidToDateFormat();

						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE12").getExtentCase());
						currentNode.assignCategory(assignCategory);
						String message = MessagesDAO.prepareMessageByKey("btsl.date.error.format",
								MessagesDAO.getLabelByKey("p2p.reconciliation.selectservicetype.label.todate"));
						if (result.equals(message))
							currentNode.log(Status.PASS, "Message Validation Successful");
						else {
							currentNode.log(Status.FAIL, "Expected [" + message + "] but found [" + result + "]");
							currentNode.log(Status.FAIL, "Message Validation Failed");
						}

						Log.endTestCase(this.getClass().getName());
					}*/
					
					// From Date Greater Than Current Date
					@Test
					@TestManager(TestKey = "PRETUPS-1134") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
					public void h_P2PReconciliation() {
						final String methodName = "Test_P2P_Reconciliation";
						Log.startTestCase(methodName);
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE13").getExtentCase());
						currentNode.assignCategory(assignCategory);
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
			String result = p2PReconciliation.p2PReconciliationlink_fromDateGreaterThanCurrDate();

						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE14").getExtentCase());
						currentNode.assignCategory(assignCategory);
						String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.fromdatebeforecurrentdate");
						if (!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message) && result.equals(message))
							Assertion.assertPass("Message Validation Successful");
						else {
							Assertion.assertFail("Expected [" + message + "] but found [" + result + "]");
							Assertion.assertFail("Message Validation Failed");
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();
						}

						Assertion.completeAssertions();
						Log.endTestCase(methodName);
					}
					
					// To Date Greater Than Current Date
					@Test
					@TestManager(TestKey = "PRETUPS-1135") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
					public void i_P2PReconciliation() {
						final String methodName = "Test_P2P_Reconciliation";
						Log.startTestCase(methodName);
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE15").getExtentCase());
						currentNode.assignCategory(assignCategory);
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
			           String result = p2PReconciliation.p2PReconciliationlink_toDateGreaterThanCurrDate();

						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE16").getExtentCase());
						currentNode.assignCategory(assignCategory);
						String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.todatebeforecurrentdate");
						if (!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message) && result.equals(message))
							Assertion.assertPass("Message Validation Successful");
						else {
							Assertion.assertFail("Expected [" + message + "] but found [" + result + "]");
							Assertion.assertFail("Message Validation Failed");
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();
						}

						Assertion.completeAssertions();
						Log.endTestCase(methodName);
					}
					
					// To Date Less Than From Date
					@Test
					@TestManager(TestKey = "PRETUPS-1136") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
					public void j_P2PReconciliation() {
						final String methodName = "Test_P2P_Reconciliation";
						Log.startTestCase(methodName);
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE17").getExtentCase());
						currentNode.assignCategory(assignCategory);
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
			String result = p2PReconciliation.p2Preconciliationpage1Reconciliationlink_toDateLessThanForDate();

						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE18").getExtentCase());
						currentNode.assignCategory(assignCategory);
						String message = MessagesDAO.prepareMessageByKey("btsl.error.msg.fromdatebeforetodate");
						if (!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message) && result.equals(message))
							Assertion.assertPass("Message Validation Successful");
						else {
							Assertion.assertFail("Expected [" + message + "] but found [" + result + "]");
							Assertion.assertFail("Message Validation Failed");
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();
						}

						Assertion.completeAssertions();
						Log.endTestCase(methodName);
					}
					
					// Check Ambiguous
					@Test(dataProvider = "categoryData")
					@TestManager(TestKey = "PRETUPS-1138") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
					public void k_P2PReconciliation(String serviceType, String selectorType) {
						final String methodName = "Test_P2P_Reconciliation";
						Log.startTestCase(methodName);
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE19").getExtentCase());
						currentNode.assignCategory(assignCategory);
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
						String validationValue = MessagesDAO.getLabelByKey("p2p.reconciliation.displayreconlist.msg.nodata");
						boolean[] result = p2PReconciliation.p2PReconciliationlink_checkAmbigious(serviceType, validationValue, selectorType);

						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE20").getExtentCase());
						currentNode.assignCategory(assignCategory);

						if (result[0] == result[1])
							Assertion.assertPass("Message Validation Successful");
						else {
							Assertion.assertFail("Message Validation Failed");
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();
						}

						Assertion.completeAssertions();
						Log.endTestCase(methodName);
					}

				// Success
					@Test(dataProvider = "categoryData")
					@TestManager(TestKey = "PRETUPS-1140") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
					public void l_P2PReconciliation(String serviceType, String selectorType) {
						final String methodName = "Test_P2P_Reconciliation";
						Log.startTestCase(methodName);
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE21").getExtentCase());
						currentNode.assignCategory(assignCategory);
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
						
						String result = p2PReconciliation.p2PReconciliationlink_Success(serviceType, selectorType);
						
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE22").getExtentCase());
						currentNode.assignCategory(assignCategory);
						String message = MessagesDAO.prepareMessageByKey("p2p.reconciliation.displaydetail.updatemsg.success");
						String message1= MessagesDAO.getLabelByKey("p2p.reconciliation.displayreconlist.msg.nodata");
						
						if (!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message) && result.equals(message))
							Assertion.assertPass("Message Validation Successful");
						else if(!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message1) && result.equals(message1)) {
							Assertion.assertSkip("No transfer found for the input values.");
							}
						else
							{Assertion.assertFail("Message Validation Failed");
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();}

						Assertion.completeAssertions();
						Log.endTestCase(methodName);
					}
					
					//DB_Verification
					@Test(dataProvider = "categoryData")
					@TestManager(TestKey = "PRETUPS-1141") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
					public void m_P2PReconciliation(String serviceType, String selectorType) {
						final String methodName = "Test_P2P_Reconciliation";
						Log.startTestCase(methodName);
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE23").getExtentCase());
						currentNode.assignCategory(assignCategory);
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
						
						String result = p2PReconciliation.p2PReconciliationlink_dbVerification(serviceType, selectorType);
						
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE24").getExtentCase());
						currentNode.assignCategory(assignCategory);
						String message1= MessagesDAO.getLabelByKey("p2p.reconciliation.displayreconlist.msg.nodata");
						
						if (!BTSLUtil.isNullString(result) && result == "200")
							Assertion.assertPass("Message Validation Successful");
						else if(!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message1) && result.equals(message1)) {
							Assertion.assertSkip("No transfer found for the input values.");
							}
						else
							{Assertion.assertFail("Message Validation Failed");
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();}

						Assertion.completeAssertions();
						Log.endTestCase(methodName);
					}
					
					// Fail
					@Test(dataProvider = "categoryData")
					@TestManager(TestKey = "PRETUPS-1142") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
					public void n_P2PReconciliation(String serviceType, String selectorType) {
						final String methodName = "Test_P2P_Reconciliation";
						Log.startTestCase(methodName);
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE25").getExtentCase());
						currentNode.assignCategory(assignCategory);
						P2PReconciliation p2PReconciliation = new P2PReconciliation(driver);
						
						String result = p2PReconciliation.p2PReconciliationlink_Success(serviceType, selectorType);
						
						currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PRECONILE26").getExtentCase());
						currentNode.assignCategory(assignCategory);
						String message = MessagesDAO.prepareMessageByKey("p2p.reconciliation.displaydetail.updatemsg.success");
						String message1= MessagesDAO.getLabelByKey("p2p.reconciliation.displayreconlist.msg.nodata");
						
						if (!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message) && result.equals(message))
							Assertion.assertPass("Message Validation Successful");
						else if(!BTSLUtil.isNullString(result) && !BTSLUtil.isNullString(message1) && result.equals(message1)) {
							Assertion.assertSkip("No transfer found for the input values.");
							}
						else
							{Assertion.assertFail("Message Validation Successful");
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();}

						Assertion.completeAssertions();
						Log.endTestCase(methodName);
					}	
}
