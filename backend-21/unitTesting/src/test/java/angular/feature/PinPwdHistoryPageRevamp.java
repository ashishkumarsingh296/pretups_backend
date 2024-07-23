package angular.feature;

import java.util.ArrayList;
import java.util.Arrays;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.ReusableMethods;
import com.utils._masterVO;

import angular.classes.LoginRevamp;
import angular.pageobjects.Home.CUHomePage;
import angular.pageobjects.PinPwdHistory.PinPwdHistoryPage;

public class PinPwdHistoryPageRevamp extends BaseTest {

	public WebDriver driver;
	LoginRevamp login;
	PinPwdHistoryPage pinPwdHistoryPage;
	CUHomePage cuHomePage;
	ReusableMethods rm;

	public PinPwdHistoryPageRevamp(WebDriver driver) {
		this.driver = driver;
		login = new LoginRevamp();
		cuHomePage = new CUHomePage(driver);
		pinPwdHistoryPage = new PinPwdHistoryPage(driver);
		rm = new ReusableMethods(driver);
	}

	public void performLogin(String ParentCategory, String FromCategory) throws InterruptedException {

		final String methodname = "performLogin";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		if (cuHomePage.isHomeScreenVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, FromCategory + "user is successfully able to login");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, FromCategory + "user is unable to login");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}

	public void validatePinPasswordLinkVisibility(String ParentCategory, String FromCategory)
			throws InterruptedException {

		final String methodname = "validatePinPasswordLinkVisibility";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		if (cuHomePage.isPinPwdHistoryTextVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, "Pin and Password History link is displayed for " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Pin and Password History link is not displayed for " + FromCategory);
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}

	public void validateNavigatingToPinPasswordHistoryPage(String ParentCategory, String FromCategory)
			throws InterruptedException {

		final String methodname = "validateNavigatingToPinPasswordHistoryPage";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (pinPwdHistoryPage.isProceedButtonVisible()) {
			ExtentI.Markup(ExtentColor.GREEN,
					FromCategory + " user is successfully able to navigate to PIN and Password page");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, FromCategory + " user is unable to navigate to PIN and Password page");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}

	public void validatePinMenuSelectedByDefault(String ParentCategory, String FromCategory)
			throws InterruptedException {

		final String methodname = "validatePinMenuSelectedByDefault";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (pinPwdHistoryPage.isPinMenuSelected()) {
			ExtentI.Markup(ExtentColor.GREEN, "PIN menu is selected by default for: " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "PIN menu is not selected by default for: " + FromCategory);
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}

	public void validateVisibilityOfAllFields(String ParentCategory, String FromCategory, String mName)
			throws InterruptedException {

		final String methodName = "validateVisibilityOfAllFields";
		Log.methodEntry(methodName, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		if (pinPwdHistoryPage.isUserTypeDropdownVisible() && pinPwdHistoryPage.isDomainDropdownVisible()
				&& pinPwdHistoryPage.isCategoryDropdownVisible() && pinPwdHistoryPage.isDateRangeFieldVisible()
				&& pinPwdHistoryPage.isResetButtonVisible() && pinPwdHistoryPage.isProceedButtonVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, "All Fields are visible for : " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "All Fields under are not visible for : " + FromCategory);
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodName);
	}

	public void validateDefaultSelectedOptionForDomainDd(String ParentCategory, String FromCategory, String mName)
			throws InterruptedException {
		final String methodname = "validateDefaultSelectedOptionForDomainDd";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String actual = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		Log.info("Domain name found as: " + actual);
		String expeceted = pinPwdHistoryPage.defaultValueSelectedForDomainDd();
		if (actual.equalsIgnoreCase(expeceted)) {
			ExtentI.Markup(ExtentColor.GREEN,
					"Default selected option in domain dropdown is : " + actual + " for " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL,
					"Default selected option in domain dropdown is not :" + actual + " for " + FromCategory);
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}

	public void validateHideLink(String ParentCategory, String FromCategory, String mName) throws InterruptedException {
		final String methodname = "validateHideLink";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickHidelink();
		if (pinPwdHistoryPage.isShowTextVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, "Hide link is working for " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Hide link is not working for " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validateResetButton(String ParentCategory, String FromCategory, String mName)
			throws InterruptedException {
		final String methodname = "validateResetButton";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickUserTypeDropdown();
		pinPwdHistoryPage.selectOptionFromUserTypeDd();
		pinPwdHistoryPage.clickResetButton();
		if (pinPwdHistoryPage.defaultValueSelectedInUserTypeDd().equalsIgnoreCase("All")) {
			ExtentI.Markup(ExtentColor.GREEN, "Reset button is working as expected for " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Reset button is not working as expected for " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validateResetButtonWithDefaultValues(String ParentCategory, String FromCategory, String mName)
			throws InterruptedException {
		final String methodname = "validateResetButtonWithDefaultValues";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickResetButton();
		if (pinPwdHistoryPage.defaultValueSelectedInUserTypeDd().equalsIgnoreCase("All")
				&& pinPwdHistoryPage.defaultValueSelectedForDomainDd().equalsIgnoreCase("Distributor")
				&& pinPwdHistoryPage.defaultValueSelectedInCategoryDd().equalsIgnoreCase("All")) {
			ExtentI.Markup(ExtentColor.GREEN,
					"Reset button is not changing the default selected values for " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Reset button is changing the default selected values for " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validateSelectingDateRangePerSys(String ParentCategory, String FromCategory, String mName)
			throws Exception {
		final String methodname = "validateSelectingDateRangePerSys";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("29-July-2021");
		rm.selectToDate("12-August-2021");
		if (!pinPwdHistoryPage.isDateSysPrefErrorMsgVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, "Able to selecting date range per system preference for " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Unable to select date range per system preference for " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validateSelectingDateRangeWithoutSysPref(String ParentCategory, String FromCategory, String mName)
			throws Exception {
		final String methodname = "validateSelectingDateRangeWithoutSysPref";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("15-July-2021");
		rm.selectToDate("20-August-2021");
		if (pinPwdHistoryPage.isDateErrorMsgVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, "Unable to select date range per system preference for " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Able to select date range per system preference for " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validateSelectingOnlyFromDate(String ParentCategory, String FromCategory, String mName)
			throws Exception {
		final String methodname = "validateSelectingOnlyFromDate";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("15-July-2021");
		pinPwdHistoryPage.clickProceedButton();
		if (pinPwdHistoryPage.isDateErrorMsgVisible()) {
			ExtentI.Markup(ExtentColor.GREEN,
					"System is asking the user to select both from and to date for user " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL,
					"System is not asking the user to select both from and to date for user " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validateDownloadButtonPresence(String ParentCategory, String FromCategory, String mName)
			throws Exception {
		final String methodname = "validateDownloadButtonPresence";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("1-June-2021");
		rm.selectToDate("30-June-2021");
		rm.waitFor(3000);
		pinPwdHistoryPage.clickProceedButton();
		if (pinPwdHistoryPage.isdownloadButtonVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, "Download button is visible for user " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Download button is not visible for user " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validateDownloadingFile(String ParentCategory, String FromCategory, String mName) throws Exception {
		final String methodname = "validateDownloadingFile";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("1-June-2021");
		rm.selectToDate("30-June-2021");
		rm.waitFor(3000);
		pinPwdHistoryPage.clickProceedButton();
		pinPwdHistoryPage.clickOnDownload();
		rm.waitFor(5000);
		if (pinPwdHistoryPage.isFileDownloaded(".\\src\\test\\resources\\UploadDocuments\\C2S_Bulk_Transfer\\")) {
			ExtentI.Markup(ExtentColor.GREEN, "Successfully able to download file for user " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Unable to download file for user " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validatesearchFilterWithValidColumn(String ParentCategory, String FromCategory, String msisdn,
			String mName) throws Exception {
		final String methodname = "validatesearchFilterWithValidColumn";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("1-June-2021");
		rm.selectToDate("30-June-2021");
		rm.waitFor(3000);
		pinPwdHistoryPage.clickProceedButton();
		rm.waitFor(5000);
		pinPwdHistoryPage.clickOnSearhBox();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.enterValueInSearhBox("newstaff1");
		} else {
			pinPwdHistoryPage.enterValueInSearhBox(msisdn);
		}
		rm.waitFor(3000);
		if (pinPwdHistoryPage.checkDataDisplayedPerSearchFilter(2).contains(msisdn)
				|| pinPwdHistoryPage.checkDataDisplayedPerSearchFilter(2).contains("newstaff1")) {
			ExtentI.Markup(ExtentColor.GREEN,
					"Search filter is only displaying the values entered in search box for user: " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Search filter is not working correctly for:  " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validatesearchFilterWithInValidColumn(String ParentCategory, String FromCategory, String msisdn,
			String mName) throws Exception {
		final String methodname = "validatesearchFilterWithValidColumn";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("1-June-2021");
		rm.selectToDate("30-June-2021");
		rm.waitFor(1000);
		pinPwdHistoryPage.clickProceedButton();
		rm.waitFor(2000);
		pinPwdHistoryPage.clickOnSearhBox();
		pinPwdHistoryPage.enterValueInSearhBox("HelloTesting");
		rm.waitFor(1000);
		if (pinPwdHistoryPage.isNoRecordsErrorMsgVisible()) {
			ExtentI.Markup(ExtentColor.GREEN,
					"No records error message is displayed while searching with invalid field for user: "
							+ FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL,
					"No records error message is not displayed while searching with invalid field for:  "
							+ FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validateDataIsPopulatedForAllUsers(String ParentCategory, String FromCategory, String mName)
			throws Exception {
		final String methodname = "validateDataIsPopulatedForAllUsers";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			rm.waitFor(1000);
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		ArrayList<String> userType = new ArrayList<String>(Arrays.asList("All", "Channel", "Staff"));
		ArrayList<String> category = new ArrayList<String>(Arrays.asList("All", "Dealer"));
		for (int i = 0; i < userType.size(); i++) {
			pinPwdHistoryPage.clickUserTypeDropdown();
			pinPwdHistoryPage.selectOptionFromDropdown(userType.get(i), "UserType");
			for (int j = 0; j < category.size(); j++) {
				pinPwdHistoryPage.clickOnCategoryDropdown();
				pinPwdHistoryPage.selectOptionFromDropdown(category.get(j), "Category");
				pinPwdHistoryPage.clickDateRangeField();
				rm.selectFromDate("1-March-2021");
				rm.selectToDate("30-March-2021");
				pinPwdHistoryPage.clickProceedButton();
				rm.waitFor(5000);
				if (pinPwdHistoryPage.isdataPopulated()) {
					ExtentI.Markup(ExtentColor.GREEN, "Reports data is populated when selected the User Type as: "
							+ userType.get(i) + " and Category as: " + category.get(j) + "");
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				} else {
					currentNode.log(Status.FAIL, "Reports data is not populated when selected the User Type as: "
							+ userType.get(i) + " and Category as: " + category.get(j) + "");
					ExtentI.attachScreenShot();
				}
			}

		}
		Log.methodExit(methodname);
	}

	public void validatePagination(String ParentCategory, String FromCategory, String mName) throws Exception {
		final String methodname = "validatePagination";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			rm.waitFor(3000);
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("1-June-2021");
		rm.selectToDate("30-June-2021");
		rm.waitFor(2000);
		pinPwdHistoryPage.clickProceedButton();
		rm.waitFor(3000);
		pinPwdHistoryPage.clickOnPageNumber(2);
		rm.waitFor(2000);
		if (pinPwdHistoryPage.pageNumConfirmation().contains("11")) {
			ExtentI.Markup(ExtentColor.GREEN, "Pagination is working for user: " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Pagination is not working for user: " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validatePreviousPagination(String ParentCategory, String FromCategory, String mName) throws Exception {
		final String methodname = "validatePreviousPagination";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			rm.waitFor(3000);
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("1-June-2021");
		rm.selectToDate("30-June-2021");
		rm.waitFor(3000);
		pinPwdHistoryPage.clickProceedButton();
		rm.waitFor(3000);
		pinPwdHistoryPage.clickOnPageNumber(2);
		rm.waitFor(3000);
		pinPwdHistoryPage.clickOnPreviousPagination();
		if (pinPwdHistoryPage.pageNumConfirmation().contains("1")) {
			ExtentI.Markup(ExtentColor.GREEN, "Sucessfully able to click on previous page: " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Unable to click on previous page: " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validateNextPagination(String ParentCategory, String FromCategory, String mName) throws Exception {
		final String methodname = "validateNextPagination";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			rm.waitFor(3000);
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("1-June-2021");
		rm.selectToDate("30-June-2021");
		rm.waitFor(2000);
		pinPwdHistoryPage.clickProceedButton();
		rm.waitFor(5000);
		pinPwdHistoryPage.clickOnNextPagination();
		if (pinPwdHistoryPage.pageNumConfirmation().contains("11")) {
			ExtentI.Markup(ExtentColor.GREEN, "Sucessfully able to click on next page: " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Unable to click on next page: " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validatePaginationEnteringNumber(String ParentCategory, String FromCategory, String mName)
			throws Exception {
		final String methodname = "validatePaginationEnteringNumber";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			rm.waitFor(3000);
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("1-June-2021");
		rm.selectToDate("30-June-2021");
		rm.waitFor(2000);
		pinPwdHistoryPage.clickProceedButton();
		rm.waitFor(2000);
		pinPwdHistoryPage.goToPageNumber("2");
		pinPwdHistoryPage.clickOnGoButton();
		if (pinPwdHistoryPage.pageNumConfirmation().contains("11")) {
			ExtentI.Markup(ExtentColor.GREEN,
					"Pagination is working when entered a particular number: " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Pagination is not working when entered a particular number: " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void validatePaginationEnteringNegativeNumber(String ParentCategory, String FromCategory, String mName)
			throws Exception {
		final String methodname = "validatePaginationEnteringNegativeNumber";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			rm.waitFor(3000);
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("1-June-2021");
		rm.selectToDate("30-June-2021");
		rm.waitFor(2000);
		pinPwdHistoryPage.clickProceedButton();
		rm.waitFor(2000);
		pinPwdHistoryPage.goToPageNumber("-2");
		pinPwdHistoryPage.clickOnGoButton();
		if (pinPwdHistoryPage.pageNumConfirmation().contains("1")) {
			ExtentI.Markup(ExtentColor.GREEN,
					"Pagination is not working is entered a negative number: " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Pagination is working if entered a negative number " + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void showEntriesDdValues(String ParentCategory, String FromCategory, String mName) throws Exception {
		final String methodname = "showEntriesDdValues";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickCUHomeHeading();
		cuHomePage.clickOnPinPwdHistoryText();
		if (mName.endsWith("PasswordPage")) {
			rm.waitFor(3000);
			pinPwdHistoryPage.clickOnPasswordMenu();
		}
		pinPwdHistoryPage.clickDateRangeField();
		rm.selectFromDate("1-June-2021");
		rm.selectToDate("30-June-2021");
		rm.waitFor(2000);
		pinPwdHistoryPage.clickProceedButton();
		rm.waitFor(3000);
		ArrayList<String> actualValues = new ArrayList<String>(Arrays.asList("10", "25", "50", "100"));
		if (pinPwdHistoryPage.fetchValuesFromShwoEntriesDd().containsAll(actualValues)) {
			ExtentI.Markup(ExtentColor.GREEN,
					"Expected values to limit the entries are available in the show entries dropdown for user"
							+ FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL,
					"Correct values are not available in the show entries dropdown for user" + FromCategory);
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

}