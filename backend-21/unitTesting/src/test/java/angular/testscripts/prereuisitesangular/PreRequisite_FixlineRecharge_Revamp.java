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

import angular.feature.FixlineRechargeRevamp;

@ModuleManager(name = Module.PREREQUISITE_FIXLINE_REVAMP)
public class PreRequisite_FixlineRecharge_Revamp extends BaseTest {

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_01_Test_FixLineRecharge(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_01_Test_FixLineRecharge";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE1");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixline(ParentCategory, FromCategory, PIN, service);
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
	public void A_02_performC2STransferFixlineBlankAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeFixlineBlankAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE2");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineBlankAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_03_performC2STransferFixlineAlphaNumericAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeFixlineAlphaNumericAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE3");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineAlphaNumericAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_04_performC2STransferFixlineNegativeAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeFixlineNegativeAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE4");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineNegativeAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_05_performC2STransferFixlineZeroAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "performC2STransferFixlineZeroAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE5");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);
			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineZeroAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_06_performC2STransferFixlineBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "performC2STransferFixlineBlankPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE6");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineBlankPIN(ParentCategory, FromCategory, PIN, service);
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
	public void A_07_performC2STransferFixlineInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "performC2STransferFixlineInvalidPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE7");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineInvalidPIN(ParentCategory, FromCategory, PIN, service);
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
	public void A_08_performC2STransferFixlineCloseEnterPINPopup(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "performC2STransferCloseEnterPINPopup";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE8");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineCloseEnterPINPopup(ParentCategory, FromCategory, PIN, service);
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
	public void A_09_performC2STransferFixlineBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeFixlineBlankMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE9");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineBlankMsisdn(ParentCategory, FromCategory, PIN, service);
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
	public void A_10_performC2STransferFixlineInvalidMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeFixlineInvalidMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE10");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineInvalidMSISDN(ParentCategory, FromCategory, PIN, service);
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
	public void A_11_performC2STransferFixlineAlphaNumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeAlphaNumericMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE11");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineAlphaNumericMSISDN(ParentCategory, FromCategory, PIN, service);
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
	public void A_12_performC2STransferFixlineResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeFixlineResetButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE12");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineResetButton(ParentCategory, FromCategory, PIN, service);
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
	public void A_13_performC2STransferFixlineCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeFixlineCopyButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE13");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineCopyButton(ParentCategory, FromCategory, PIN, service);
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
	public void A_14_performC2STransferFixlineBlankSubservice(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeFixlineBlankSubservice";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE14");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlineBlankSubservice(ParentCategory, FromCategory, PIN, service);
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
	public void A_15_performC2STransferFixlinePrintButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "Test_C2SRechargeFixlinePrintButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFIXLINERECHARGE15");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			FixlineRechargeRevamp C2STransfer = new FixlineRechargeRevamp(driver);
			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferFixlinePrint(ParentCategory, FromCategory, PIN, service);
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
		String CustomerRechargeCode = _masterVO.getProperty("FixLineRechargeCode");
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

