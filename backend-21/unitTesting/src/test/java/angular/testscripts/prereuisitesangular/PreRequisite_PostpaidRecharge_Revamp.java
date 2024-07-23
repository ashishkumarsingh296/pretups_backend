package angular.testscripts.prereuisitesangular;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import com.commons.EventsI;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;


import angular.feature.PostpaidRechargeRevamp ;


@ModuleManager(name = Module.PREREQUISITE_POSTPAID_REVAMP)
public class PreRequisite_PostpaidRecharge_Revamp extends BaseTest {



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_01_Test_C2SPostpaid(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_0_Test_C2SPostpaid";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID1");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {


			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaid(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	/* -----------------------     NEGATIVE TEST CASE       ---------------------- */
	/* ------------------------------------------------------------------------------------------------- */


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_02_performC2SPostpaidBlankAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_1_performC2SPostpaidBlankAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID2");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidBlankAmount(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}






	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_03_performC2SPostpaidAlphaNumericAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_2_performC2SPostpaidAlphaNumericAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID3");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidAlphaNumericAmount(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_04_performC2SPostpaidNegativeAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_3_performC2SPostpaidNegativeAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID4");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidNegativeAmount(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}





	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_05_performC2SPostpaidZeroAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_4_performC2SPostpaidZeroAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID5");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidZeroAmount(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_06_performC2SPostpaidBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_5_performC2SPostpaidBlankPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID6");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidBlankPIN(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_07_performC2SPostpaidInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_07_performC2SPostpaidInvalidPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID7");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidInvalidPIN(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}








	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_08_performC2SPostpaidCloseEnterPINPopup(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_7_performC2SPostpaidCloseEnterPINPopup";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID8");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidCloseEnterPINPopup(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}




	/* ------------- YASH  ------------*/
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_09_performC2SPostpaidBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_8_performC2SPostpaidBlankMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID9");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidBlankMSISDN(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_10_performC2SPostpaidInvalidMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_9_performC2SPostpaidInvalidMsisdn";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID10");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidInvalidMSISDN(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_11_performC2SPostpaidAlphaNumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_10_performC2SPostpaidAlphaNumericMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID11");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidAlphaNumericMSISDN(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_12_performC2SPostpaidResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_11_performC2SPostpaidResetButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID12");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidResetButton(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_13_performC2SPostpaidCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_12_performC2SPostpaidCopyButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID13");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidCopyButton(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

/*
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_14_performC2SPostpaidBlankSubservice(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_14_performC2SPostpaidBlankSubservice";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID14");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidBlankSubservice(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
*/
	/*
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_15_performC2SPostpaidPrintButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_13_performC2SPostpaidPrintButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID15");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidPrintButton(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}




	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_07_performC2SPostpaidAlphanumericPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_6_performC2SPostpaidAlphanumericPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID7");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidAlphanumericPIN(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

*/


	/* ---------------- CODE ---------------*/






	/*
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_16_performC2SPostpaidInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "a_15_performC2SPostpaidInvalidPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPOSTPAID16");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			PostpaidRechargeRevamp C2STransfer = new PostpaidRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferPostpaidInvalidPIN(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


*/












	/* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
	    /* ------------------------------------------------------------------------------------------------- */

	    @DataProvider(name = "categoryData")
	    public Object[][] TestDataFeed1() {
	        String CustomerRechargeCode = _masterVO.getProperty("PostpaidBillPaymentCode");
	        String MasterSheetPath = _masterVO.getProperty("DataProvider");

	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	        int rowCount = ExcelUtility.getRowCount();
	        /*
	         * Array list to store Categories for which Customer Recharge is allowed
	         */
	        ArrayList<String> alist1 = new ArrayList<String>();
	        for (int i = 1; i <= rowCount; i++) {
	            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
	            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
	            if (aList.contains(CustomerRechargeCode)) {
	                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	                alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
	            }
	        }

	        /*
	         * Counter to count number of users exists in channel users hierarchy sheet
	         * of Categories for which O2C transfer is allowed
	         */
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	        int chnlCount = ExcelUtility.getRowCount();
	        int userCounter = 0;
	        for (int i = 1; i <= chnlCount; i++) {
	            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
	                userCounter++;
	            }
	        }

	        /*
	         * Store required data of 'O2C transfer allowed category' users in Object
	         */
	        Object[][] Data = new Object[userCounter][4];
	        for (int i = 1, j = 0; i <= chnlCount; i++) {
	            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
	                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
	                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
	                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
	                Data[j][3] = CustomerRechargeCode;
	                j++;
	            }
	        }

	        return Data;
	    }

	    /* ------------------------------------------------------------------------------------------------ */

	
}

