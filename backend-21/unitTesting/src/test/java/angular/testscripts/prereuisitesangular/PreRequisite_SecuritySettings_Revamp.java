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

import angular.feature.SecuritySettingsPageRevamp;

@ModuleManager(name = Module.PREREQUISITE_SECURITYSETTINGSPAGE_REVAMP)
public class PreRequisite_SecuritySettings_Revamp extends BaseTest {

	public PreRequisite_SecuritySettings_Revamp() {
		CHROME_OPTIONS = CONSTANT.CHROME_OPTION_C2SBULKTRANSFER;
	}

	/* ----------------------- D A T A P R O V I D E R ---------------------- */

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

	
	/******************* TEST CASES For SECURITY SETTINGS*********************/
	
	@Test(dataProvider = "categoryData" , priority = 1)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_01_Test_LoginWithValidCredentials(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_01_Test_LoginWithValidCredentials";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS1");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.performLogin(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 2)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_02_Test_VerifyVisibilityOfSecuritySettingsLink(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_02_Test_VerifyVisibilityOfSecuritySettingsLink";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS2");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.securitySettingsLink(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 3)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_03_Test_VerifyNavigatingToSecuritySettingsPage(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_03_Test_VerifyNavigatingToSecuritySettingsPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS3");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.navigateToSecuritySettingsPage(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 4)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_04_Test_VerifyAllFieldsVisibleInPinPage(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_04_Test_VerifyAllFieldsVisibleInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS4");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.visibilityOfAllFieldsInPinSection(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 5)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_05_Test_VerifyAllFieldsVisibleInPasswordPage(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_05_Test_VerifyAllFieldsVisibleInPasswordPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS5");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.visibilityOfAllFieldsInPasswordSection(ParentCategory, FromCategory , methodName);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 6)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_06_Test_VerifyChangingPin(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_06_Test_VerifyChangingPin";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS6");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.changePin(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 7)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_07_Test_VerifyChangingPinWithWrongOldPin(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_07_Test_VerifyChangingPinWithWrongOldPin";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS7");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.changePinWithWrongOldPin(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 8)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_08_Test_VerifyChangingPinWithNewAndConfirmNewPin(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_08_Test_VerifyChangingPinWithNewAndConfirmNewPin";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS8");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.changePinWithIncorrectNewAndConfirmNewPin(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 9)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_09_Test_VerifyChangingPinWithBlankInputInPinPage(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_09_Test_VerifyChangingPinWithBlankInputInPinPage";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS9");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.changePinWithBlankInput(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "categoryData" , priority = 13)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_10_Test_VerifyChangingPassword(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_10_Test_VerifyChangingPassword";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS10");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.changePassword(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 10)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_11_Test_VerifyChangingPwdWithIncorrectOldPwd(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_10_Test_VerifyChangingPassword";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS11");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.changePasswordWithIncorrectOldPwd(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 11)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_12_Test_VerifyChangingPwdWithIncorrectNewAndConfirmNewPwd(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_12_Test_VerifyChangingPwdWithIncorrectNewAndConfirmNewPwd";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS12");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.changePwdWithIncorrectNewAndConfirmNewPwd(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData" , priority = 12)
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void TC_13_Test_VerifyEyeIcon(String ParentCategory, String FromCategory , String msisdn) throws InterruptedException {
		final String methodName = "TC_13_Test_VerifyEyeIcon";
		Log.startTestCase(methodName, ParentCategory, FromCategory);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPSECSTTNGS13");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory))
				.assignCategory(TestCategory.PREREQUISITE);

		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		SecuritySettingsPageRevamp securitySettingsPage = new SecuritySettingsPageRevamp(driver);

		if (webAccessAllowed.equals("Y")) {
			securitySettingsPage.validateEyeIcon(ParentCategory, FromCategory);
		} else {
			Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
