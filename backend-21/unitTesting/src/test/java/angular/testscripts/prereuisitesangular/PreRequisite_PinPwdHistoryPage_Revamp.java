package angular.testscripts.prereuisitesangular;

import java.text.MessageFormat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

import angular.feature.PinPwdHistoryPageRevamp;

@ModuleManager(name = Module.PREREQUISITE_PINPWDHISTORYPAGE_REVAMP)
public class PreRequisite_PinPwdHistoryPage_Revamp extends BaseTest {

	public PreRequisite_PinPwdHistoryPage_Revamp() {
		CHROME_OPTIONS = CONSTANT.CHROME_OPTION_C2SBULKTRANSFER;
	}

	/* ----------------------- D A T A P R O V I D E R ---------------------- */
	/*
	 * -----------------------------------------------------------------------------
	 * --------------------
	 */

	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed1() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();
		System.out.println("Total row count: " + chnlCount);
		Object[][] data = new Object[chnlCount-4][3];
		for (int i = 1; i <= 1; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			for (int j = 0; j < 3; j++) {
				data[i - 1][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				data[i - 1][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				data[i - 1][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			}

		}
		return data;
	}

	/* -------------------TEST CASES FOR PIN HISTORY---------- */

	@Test(dataProvider = "categoryData" , priority = 1)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_01_Test_LoginWithValidCredentials(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_01_Test_LoginWithValidCredentials";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY1");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.performLogin(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 2)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_02_Test_VerifyPinPasswordLinkVisibility(String ParentCategory, String FromCategory , String msisdn)
			throws InterruptedException {
		final String methodName = "TC_02_Test_VerifyPinPasswordLinkVisibility";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY2");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatePinPasswordLinkVisibility(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 3)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_03_Test_VerifyNavigatingToPinPasswordHistoryPage(String ParentCategory, String FromCategory , String msisdn)
			throws InterruptedException {
		final String methodName = "TC_03_Test_VerifyNavigatingToPinPasswordHistoryPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY3");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateNavigatingToPinPasswordHistoryPage(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData"  , priority = 4)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_04_Test_VerifyPinMenuSelectedByDefault(String ParentCategory, String FromCategory , String msisdn)
			throws InterruptedException {
		final String methodName = "TC_04_Test_VerifyPinMenuSelectedByDefault";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY4");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatePinMenuSelectedByDefault(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData"  , priority = 5)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_05_Test_VerifyAllFieldsVisibilityInPinMenuPage(String ParentCategory, String FromCategory , String msisdn)
			throws InterruptedException {
		final String methodName = "TC_05_Test_VerifyAllFieldsVisibilityInPinMenuPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY5");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateVisibilityOfAllFields(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 6)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_06_Test_VerifyDefaultValueSelectedForDomainDropdownInPinPage(String ParentCategory, String FromCategory , String msisdn)
			throws InterruptedException {
		final String methodName = "TC_06_Test_VerifyDefaultValueSelectedForDomainDropdownInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY6");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateDefaultSelectedOptionForDomainDd(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData"  , priority = 7)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_07_Test_VerifyHideLinkInPinPage(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_07_Test_VerifyHideLinkInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY7");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateHideLink(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 8)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_08_Test_VerifyResetButtoInPinPage(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_08_Test_VerifyResetButtonInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY8");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateResetButton(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 9)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_09_Test_VerifyResetButtonWithDefaultValuesInPinPage(String ParentCategory, String FromCategory , String msisdn)
			throws InterruptedException {
		final String methodName = "TC_09_Test_VerifyResetButtonWithDefaultValuesInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY9");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateResetButtonWithDefaultValues(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 10)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_10_Test_VerifySelectingDateRangePerSysPrefInPinPage(String ParentCategory, String FromCategory , String msisdn)
			throws Exception {
		final String methodName = "TC_10_Test_VerifySelectingDateRangePerSysPrefInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY10");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateSelectingDateRangePerSys(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 11)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_11_Test_VerifySelectingDateRangeWithoutSysPrefInPinPage(String ParentCategory, String FromCategory , String msisdn)
			throws Exception {
		final String methodName = "TC_11_Test_VerifySelectingDateRangeWithoutSysPrefInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY11");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateSelectingDateRangeWithoutSysPref(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 12)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_12_Test_VerifySelectingOnlyFromDateInPinPage(String ParentCategory, String FromCategory , String msisdn) throws Exception {
		final String methodName = "TC_12_Test_VerifySelectingOnlyFromDateInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY12");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateSelectingOnlyFromDate(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 13)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_13_Test_VerifyDownloadButtonPresenceInPinPage(String ParentCategory, String FromCategory , String msisdn) throws Exception {
		final String methodName = "TC_13_Test_VerifyDownloadButtonPresenceInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY13");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateDownloadButtonPresence(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 14)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_14_Test_VerifyDownloadingFileInPinPage(String ParentCategory, String FromCategory , String msisdn) throws Exception {
		final String methodName = "TC_14_Test_VerifyDownloadingFileInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY14");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateDownloadingFile(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 15)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_15_Test_VerifySearchFilterWithValidColumnInPinPage(String ParentCategory, String FromCategory, String msisdn)
			throws Exception {
		final String methodName = "TC_15_Test_VerifySearchFilterWithValidColumnInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY15");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatesearchFilterWithValidColumn(ParentCategory, FromCategory, msisdn , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 16)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_16_Test_VerifySearchFilterWithInvalidColumnInPinPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC_16_Test_VerifySearchFilterWithInvalidColumnInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY16");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatesearchFilterWithInValidColumn(ParentCategory, FromCategory, msisdn , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "categoryData" , priority = 17)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC17_Test_VerifyPaginationClickingSpecificPageInPinPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC17_Test_VerifyPaginationClickingSpecificPageInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY17");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatePagination(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 18)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC18_Test_VerifyPreviousPaginationLinkInPinPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC18_Test_VerifyPreviousPaginationLinkInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY18");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatePreviousPagination(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 19)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC19_Test_VerifyNextPaginationLinkInPinPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC19_Test_VerifyNextPaginationLinkInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY19");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateNextPagination(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 20)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC20_Test_VerifyPaginationEnteringNumberInPinPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC20_Test_VerifyPaginationEnteringNumberInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY20");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatePaginationEnteringNumber(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 21)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC21_Test_VerifyPaginationEnteringNegativeNumberInPinPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC20_Test_VerifyPaginationEnteringNegativeNumberInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY21");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatePaginationEnteringNegativeNumber(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "categoryData" , priority = 22)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC22_Test_VerifyDataIsPopulatedForAllUsersInPinPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC22_Test_VerifyDataIsPopulatedForAllUsersInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY22");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateDataIsPopulatedForAllUsers(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	/* -------------------TEST CASES FOR PASSWORD HISTORY ---------- */
	
	
	
	
	@Test(dataProvider = "categoryData" , priority = 23)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_23_Test_VerifyAllFieldsVisibilityInPasswordPage(String ParentCategory, String FromCategory , String msisdn)
			throws InterruptedException {
		String methodName = "TC_23_Test_VerifyAllFieldsVisibilityInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY23");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateVisibilityOfAllFields(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "categoryData" , priority = 24)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_24_Test_VerifyDefaultValueSelectedForDomainDropdownInPasswordPage(String ParentCategory, String FromCategory , String msisdn)
			throws InterruptedException {
		final String methodName = "TC_24_Test_VerifyDefaultValueSelectedForDomainDropdownInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY24");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateDefaultSelectedOptionForDomainDd(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 25)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_25_Test_VerifySelectingDateRangePerSysPrefInPasswordPage(String ParentCategory, String FromCategory , String msisdn)
			throws Exception {
		final String methodName = "TC_25_Test_VerifySelectingDateRangePerSysPrefInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY25");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateSelectingDateRangePerSys(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 26)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_26_Test_VerifySelectingDateRangeWithoutSysPrefInPasswordPage(String ParentCategory, String FromCategory , String msisdn)
			throws Exception {
		final String methodName = "TC_26_Test_VerifySelectingDateRangeWithoutSysPrefInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY26");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateSelectingDateRangeWithoutSysPref(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 27)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_27_Test_VerifySelectingOnlyFromDateInPasswordPage(String ParentCategory, String FromCategory , String msisdn) throws Exception {
		final String methodName = "TC_27_Test_VerifySelectingOnlyFromDateInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY27");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateSelectingOnlyFromDate(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData" , priority = 28)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_28_Test_VerifyHideLinkInPasswordPage(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_28_Test_VerifyHideLinkInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY28");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateHideLink(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 29)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_29_Test_VerifyResetButtoInPasswordPage(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_29_Test_VerifyResetButtoInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY29");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateResetButton(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "categoryData" , priority = 30)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_30_Test_VerifyResetButtonWithDefaultValuesInPasswordPage(String ParentCategory, String FromCategory , String msisdn)
			throws InterruptedException {
		final String methodName = "TC_30_Test_VerifyResetButtonWithDefaultValuesInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY30");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateResetButtonWithDefaultValues(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "categoryData" , priority = 31)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_31_Test_VerifyDownloadButtonPresenceInPasswordPage(String ParentCategory, String FromCategory , String msisdn) throws Exception {
		final String methodName = "TC_31_Test_VerifyDownloadButtonPresenceInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY31");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateDownloadButtonPresence(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 32)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_32_Test_VerifyDownloadingFileInPasswordPage(String ParentCategory, String FromCategory , String msisdn) throws Exception {
		final String methodName = "TC_26_Test_VerifyDownloadingFileInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY32");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateDownloadingFile(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "categoryData" , priority = 33)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_33_Test_VerifySearchFilterWithValidColumnInPasswordPage(String ParentCategory, String FromCategory, String msisdn)
			throws Exception {
		final String methodName = "TC_33_Test_VerifySearchFilterWithValidColumnInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY33");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatesearchFilterWithValidColumn(ParentCategory, FromCategory, msisdn , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData" , priority = 34)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_34_Test_VerifySearchFilterWithInvalidColumnInPasswordPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC_34_Test_VerifySearchFilterWithInvalidColumnInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY34");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatesearchFilterWithInValidColumn(ParentCategory, FromCategory, msisdn , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "categoryData" , priority = 35)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC35_Test_VerifyPaginationClickingSpecificPageInPasswordPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC35_Test_VerifyPaginationClickingSpecificPageInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY35");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatePagination(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 36)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC36_Test_VerifyPreviousPaginationLinkInPasswordPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC36_Test_VerifyPreviousPaginationLinkInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY36");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatePreviousPagination(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 37)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC37_Test_VerifyNextPaginationLinkInPasswordPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC37_Test_VerifyNextPaginationLinkInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY37");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateNextPagination(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 38)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC38_Test_VerifyPaginationEnteringNumberInPasswordPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC38_Test_VerifyPaginationEnteringNumberInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY38");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatePaginationEnteringNumber(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 39)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC39_Test_VerifyPaginationEnteringNegativeNumberInPasswordPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC39_Test_VerifyPaginationEnteringNegativeNumberInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY39");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validatePaginationEnteringNegativeNumber(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	
	@Test(dataProvider = "categoryData" , priority = 40)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC40_Test_VerifyDataIsPopulatedForAllUsersInPasswordPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC40_Test_VerifyDataIsPopulatedForAllUsersInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY40");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.validateDataIsPopulatedForAllUsers(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

	@Test(dataProvider = "categoryData" , priority = 41)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC41_Test_VerifyShowEntriesDropdownValuesInPasswordPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC41_Test_VerifyShowEntriesDropdownInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY41");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.showEntriesDdValues(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 42)
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC42_Test_VerifyShowEntriesDropdownValuesInPinPage(String ParentCategory, String FromCategory,
			String msisdn) throws Exception {
		final String methodName = "TC42_Test_VerifyShowEntriesDropdownValuesInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory, msisdn);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPINPWDHISTORY42");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		PinPwdHistoryPageRevamp PinPwdHistoryPage = new PinPwdHistoryPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			PinPwdHistoryPage.showEntriesDdValues(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
}