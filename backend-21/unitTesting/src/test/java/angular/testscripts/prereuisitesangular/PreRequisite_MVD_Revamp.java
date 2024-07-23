package angular.testscripts.prereuisitesangular;

import angular.feature.MVDRechargeRevamp;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.*;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

@ModuleManager(name = Module.PREREQUISITE_MVD_VOUCHER_REVAMP)
public class PreRequisite_MVD_Revamp extends BaseTest {

	public PreRequisite_MVD_Revamp() {
		CHROME_OPTIONS = CONSTANT.CHROME_OPTION_MVD ;
	}

	@Test(dataProvider = "categoryData")
	//@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_01_Test_MVDRecharge(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_01_Test_MVDRecharge";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC1");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRecharge(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}







	/* -----------------------     NEGATIVE TEST CASE       ---------------------- */
	/* ------------------------------------------------------------------------------------------------- */

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_02_performMVDRechargeBlankDenomination(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_02_performMVDRechargeBlankDenomination";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC2");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeBlankDenomination(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_03_performMVDRechargeBlankNoOfVouchers(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_02_performMVDRechargeBlankNoOfVouchers";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC3");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheet(RolesI.C2SRECHARGE, FromCategory)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeBlankNoOfVouchers(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_04_performMVDRechargeAlphaNumericNoOfVouchers(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_04_performMVDRechargeAlphaNumericNoOfVouchers";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC4");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeAlphaNumericNoOfVouchers(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_05_performMVDRechargeNegativeNoOfVouchers(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_05_performMVDRechargeNegativeAmount";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC5");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeNegativeNoOfVouchers(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}





	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_06_performMVDRechargeZeroNoOfVouchers(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_06_performMVDRechargeZeroNoOfVouchers";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC6");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeZeroNoOfVouchers(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_07_performMVDRechargeBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_07_performMVDRechargeBlankPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC7");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeBlankPIN(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_08_performMVDRechargeInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_08_performMVDRechargeInvalidPIN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC8");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeInvalidPIN(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_09_performMVDRechargeCloseEnterPINPopup(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_09_performMVDRechargeCloseEnterPINPopup";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC9");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeCloseEnterPINPopup(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	/* ------------- YASH  ------------*/
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_10_performMVDRechargeBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_10_performMVDRechargeBlankMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC10");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeBlankMsisdn(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_11_performMVDRechargeInvalidMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_11_performMVDRechargeInvalidMsisdn";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC11");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeInvalidMsisdn(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_12_performMVDRechargeResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_12_performMVDRechargeResetButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC12");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeReset(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_13_performMVDRechargeGreaterThanMaxLimit(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_13_performMVDRechargeCopyButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC13");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeGreaterThanMaxLimit(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_14_performMVDRechargeLessThanMinLimit(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_14_performMVDRechargeLessThanMinLimit";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC14");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeLessThanMinLimit(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	//@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_15_Test_MVDRechargeDownloadInvoice(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_15_Test_MVDRechargeDownloadInvoice";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC15");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDDownloadMVDInvoice(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

/*
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_15_performMVDRechargeCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_15_performMVDRechargeCopyButton";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC15");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeCopyButton(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}*/


	
	
	
	/*
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
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_11_performMVDRechargeAlphaNumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_11_performC2STransferAlphaNumericMSISDN";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC11");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRechargeAlphaNumericMSISDN(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
*/
	
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
//			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
//		}
//
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}






	/* ---------------- CODE ---------------*/


/*
@Test(dataProvider = "categoryData")
	//@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_02_Test_MVDRechargeDownloadInvoice(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodName = "A_01_Test_MVDRecharge";
		Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPMVDRC1");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			MVDRechargeRevamp MVDRecharge = new MVDRechargeRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				MVDRecharge.performMVDRecharge(ParentCategory, FromCategory, PIN, service);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("MVD Recharge is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

*/
















	/* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
	    /* ------------------------------------------------------------------------------------------------- */

	    @DataProvider(name = "categoryData")
	    public Object[][] TestDataFeed1() {
	        String CustomerRechargeCode = _masterVO.getProperty("MVDRecharge");
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
			//int chnlCount = 1 ;

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
