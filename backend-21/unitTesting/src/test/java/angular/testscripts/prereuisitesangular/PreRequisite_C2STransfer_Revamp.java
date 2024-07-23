package angular.testscripts.prereuisitesangular;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import com.commons.EventsI;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import angular.feature.C2STransferRevamp;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.commons.EventsI;
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

@ModuleManager(name = Module.PREREQUISITE_C2S_TRANSFER_REVAMP)
public class PreRequisite_C2STransfer_Revamp extends BaseTest {

	@Test(dataProvider = "categoryData")
	//@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_01_Test_C2SRecharge(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_01_Test_C2SRecharge";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF1");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransfer(ParentCategory, FromCategory, PIN, service);
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
	public void A_02_performC2STransferBlankAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_02_performC2STransferBlankAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF2");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheet(RolesI.C2SRECHARGE, FromCategory)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferBlankAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_03_performC2STransferAlphaNumericAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_03_performC2STransferAlphaNumericAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF3");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferAlphaNumericAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_04_performC2STransferNegativeAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_04_performC2STransferNegativeAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF4");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferNegativeAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_05_performC2STransferZeroAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_05_performC2STransferZeroAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF5");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferZeroAmount(ParentCategory, FromCategory, PIN, service);
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
	public void A_06_performC2STransferBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_06_performC2STransferBlankPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF6");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferBlankPIN(ParentCategory, FromCategory, PIN, service);
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
	public void A_07_performC2STransferInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_07_performC2STransferInvalidPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF7");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInvalidPIN(ParentCategory, FromCategory, PIN, service);
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
	public void A_08_performC2STransferCloseEnterPINPopup(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_08_performC2STransferCloseEnterPINPopup";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF8");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferCloseEnterPINPopup(ParentCategory, FromCategory, PIN, service);
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
	public void A_09_performC2STransferBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_09_performC2STransferBlankMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF9");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferBlankMsisdn(ParentCategory, FromCategory, PIN, service);
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
	public void A_10_performC2STransferInvalidMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_10_performC2STransferInvalidMsisdn";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF10");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferInvalidMsisdn(ParentCategory, FromCategory, PIN, service);
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
	public void A_11_performC2STransferAlphaNumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_11_performC2STransferAlphaNumericMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF11");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferAlphaNumericMSISDN(ParentCategory, FromCategory, PIN, service);
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
	public void A_12_performC2STransferResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_12_performC2STransferResetButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF12");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferReset(ParentCategory, FromCategory, PIN, service);
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
	public void A_13_performC2STransferCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_13_performC2STransferCopyButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF13");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferCopyButton(ParentCategory, FromCategory, PIN, service);
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
	public void A_14_performC2STransferBlankSubservice(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_14_performC2STransferBlankSubservice";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF14");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2STransfer.performC2STransferBlankSubservice(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
//	@Test(dataProvider = "categoryData")
//	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
//	public void A_15_performC2STransferPrintButton(String ParentCategory, String FromCategory, String PIN, String service) {
//		final String methodName = "A_15_performC2STransferPrintButton";
//		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2STRF15");
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
//		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
//			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
//			C2STransferRevamp C2STransfer = new C2STransferRevamp(driver);
//
//			if (webAccessAllowed.equals("Y")) {
//				C2STransfer.performC2STransferPrintButton(ParentCategory, FromCategory, PIN, service);
//			} else {
//				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
//			}
//		} else {
//			Assertion.assertSkip("C2S Recharge is not allowed to category[" + FromCategory + "].");
//		}

//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}




	/* ---------------- CODE ---------------*/


/*


*/
















	/* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
	    /* ------------------------------------------------------------------------------------------------- */

	    @DataProvider(name = "categoryData")
	    public Object[][] TestDataFeed1() {
	        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
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
	        int chnlCount = ExcelUtility.getRowCount() ;
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
