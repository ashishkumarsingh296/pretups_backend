package angular.testscripts.prereuisitesangular;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;


import com.commons.EventsI;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;

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


import angular.feature.InternetRechargeRevamp;


@ModuleManager(name = Module.PREREQUISITE_INTERNET_REVAMP)
public class PreRequisite_Internet_Revamp extends BaseTest {

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_01_Test_C2SRecharge(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRecharge";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE1");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternet(ParentCategory, FromCategory, PIN, service);
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
	public void A_02_performC2STransferInternetBlankAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeInternetBlankAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE2");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetBlankAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_03_performC2STransferInternetAlphaNumericAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeInternetAlphaNumericAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE3");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetAlphaNumericAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_04_performC2STransferInternetNegativeAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeInternetNegativeAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE4");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetNegativeAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_05_performC2STransferInternetZeroAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "performC2STransferInternetZeroAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE5");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);
			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetZeroAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_06_performC2STransferInternetBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "performC2STransferInternetBlankPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE6");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetBlankPIN(ParentCategory, FromCategory, PIN, service);
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
	public void A_07_performC2STransferInternetInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "performC2STransferInternetInvalidPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE7");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetInvalidPIN(ParentCategory, FromCategory, PIN, service);
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
	public void A_08_performC2STransferInternetCloseEnterPINPopup(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "performC2STransferCloseEnterPINPopup";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE8");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetCloseEnterPINPopup(ParentCategory, FromCategory, PIN, service);
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
	public void A_09_performC2STransferInternetBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeInternetBlankMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE9");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetBlankMSISDN(ParentCategory, FromCategory, PIN, service);
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
	public void A_10_performC2STransferInternetInvalidMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeInternetInvalidMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE10");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetInvalidMSISDN(ParentCategory, FromCategory, PIN, service);
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
	public void A_11_performC2STransferInternetAlphaNumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeAlphaNumericMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE11");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetAlphaNumericMSISDN(ParentCategory, FromCategory, PIN, service);
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
	public void A_12_performC2STransferInternetResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeInternetResetButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE12");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetResetButton(ParentCategory, FromCategory, PIN, service);
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
	public void A_13_performC2STransferInternetCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeInternetCopyButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE13");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetCopyButton(ParentCategory, FromCategory, PIN, service);
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
	public void A_14_performC2STransferInternetBlankSubservice(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeInternetBlankSubservice";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE14");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetBlankSubservice(ParentCategory, FromCategory, PIN, service);
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
	public void A_15_performC2STransferInternetPrintButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeInternetPrintButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTERNETRECHARGE15");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			InternetRechargeRevamp C2STransfer = new InternetRechargeRevamp(driver);
			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInternetPrint(ParentCategory, FromCategory, PIN, service);
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








	/* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
	/* ------------------------------------------------------------------------------------------------- */

	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed1() {
		String CustomerRechargeCode = _masterVO.getProperty("InternetRechargeCode");
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

