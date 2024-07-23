package angular.feature;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.ReusableMethods;
import com.utils._masterVO;

import angular.classes.LoginRevamp;
import angular.pageobjects.Home.CUHomePage;
import angular.pageobjects.securitySettings.SecuritySettingsPage;

public class SecuritySettingsPageRevamp extends BaseTest {

	public WebDriver driver;
	LoginRevamp login;
	CUHomePage cuHomePage;
	ReusableMethods rm;
	SecuritySettingsPage ssPage;

	public SecuritySettingsPageRevamp(WebDriver driver) {
		this.driver = driver;
		login = new LoginRevamp();
		cuHomePage = new CUHomePage(driver);
		rm = new ReusableMethods(driver);
		ssPage = new SecuritySettingsPage(driver);
	}

	RandomGeneration randomGeneration = new RandomGeneration();

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

	public void securitySettingsLink(String ParentCategory, String FromCategory) throws InterruptedException {

		final String methodname = "securitySettingsLink";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		if (cuHomePage.isSecuritySettingsLinkVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, "Security Settings Link is Visible for user: " + FromCategory);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Security Settings Link is not Visible for user: " + FromCategory);
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}

	public void navigateToSecuritySettingsPage(String ParentCategory, String FromCategory) throws InterruptedException {

		final String methodname = "navigateToSecuritySettingsPage";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		if (ssPage.isMobileNumberLblVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, "Successfully able to navigate to security settings page");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Unable to navigate to security settings page");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}

	public void visibilityOfAllFieldsInPinSection(String ParentCategory, String FromCategory, String mName)
			throws InterruptedException {

		final String methodname = "visibilityOfAllFieldsInPinSection";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		if (ssPage.isMobileNumberLblVisible() && ssPage.isnewPinInputBoxVisible() && ssPage.isoldPinInputBoxVisible()
				&& ssPage.isconfirmPinInputBoxVisible() && ssPage.isRemarksInputVisible()
				&& ssPage.isChangePINSubmitBtnVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, "All the fields are visible");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "All the fields are not visible");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}
	
	public void visibilityOfAllFieldsInPasswordSection(String ParentCategory, String FromCategory, String mName)
			throws InterruptedException {

		final String methodname = "visibilityOfAllFields";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		ssPage.clickOnPasswordMenu();
		if (ssPage.isnewPinInputBoxVisible() && ssPage.isoldPinInputBoxVisible()
				&& ssPage.isconfirmPinInputBoxVisible()) {
			ExtentI.Markup(ExtentColor.GREEN, "All the fields are visible");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "All the fields are not visible");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}

	public void changePin(String ParentCategory, String FromCategory) throws InterruptedException {
		final String methodname = "changePin";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String pin = randomGeneration.randomNumeric(4);
		Log.info("Generated PIN: " + pin);
		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		ssPage.clickOnOldPinInputBox();
		String userPIN = DBHandler.AccessHandler.fetchUserPIN("rarya_staff", "72147852369");
		Log.info("Fetched the PIN from Db: " + userPIN);
		ssPage.enterTextInOldPinInputBox(userPIN);
		ssPage.clickOnNewPinInputBox();
		ssPage.enterTextInNewPinInputBox(pin);
		ssPage.clickOnConfirmPinInputBox();
		ssPage.enterTextInConfirmPinInputBox(pin);
		ssPage.clickOnRemarksInputBox();
		ssPage.enterTextInRemarksInputBox("Testing");
		ssPage.clickOnChangePINSubmitBtn();
		ssPage.clickOnYesConfirmationBtn();
		rm.waitFor(2000);
		if (ssPage.changePINSuccessMessage().contains("Change PIN Successful")) {
			ExtentI.Markup(ExtentColor.GREEN, "Successfully able to change the PIN");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Unable to change the PIN");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}
	
	public void changePinWithWrongOldPin(String ParentCategory, String FromCategory) throws InterruptedException {
		final String methodname = "changePinWithWrongOldPin";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String pin = randomGeneration.randomNumeric(4);
		Log.info("Random Generated PIN: " + pin);
		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		ssPage.clickOnOldPinInputBox();
		ssPage.enterTextInOldPinInputBox(randomGeneration.randomNumeric(4));
		ssPage.clickOnNewPinInputBox();
		ssPage.enterTextInNewPinInputBox(pin);
		ssPage.clickOnConfirmPinInputBox();
		ssPage.enterTextInConfirmPinInputBox(pin);
		ssPage.clickOnRemarksInputBox();
		ssPage.enterTextInRemarksInputBox("Testing");
		ssPage.clickOnChangePINSubmitBtn();
		ssPage.clickOnYesConfirmationBtn();
		rm.waitFor(2000);
		if (ssPage.oldPinMismatchErrorMessage().contains("Old PIN is not valid.")) {
			ExtentI.Markup(ExtentColor.GREEN, "Unable to change the PIN with entering wrong Old PIN");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Able to change the PIN with wrong Old PIN");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}
	
	public void changePinWithIncorrectNewAndConfirmNewPin(String ParentCategory, String FromCategory) throws InterruptedException {
		final String methodname = "changePinWithIncorrectNewAndConfirmNewPin";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		ssPage.clickOnOldPinInputBox();
		String userPIN = DBHandler.AccessHandler.fetchUserPIN("rarya_staff", "72147852369");
		Log.info("Fetched the PIN from Db: " + userPIN);
		ssPage.enterTextInOldPinInputBox(userPIN);
		ssPage.clickOnNewPinInputBox();
		ssPage.enterTextInNewPinInputBox(randomGeneration.randomNumeric(4));
		ssPage.clickOnConfirmPinInputBox();
		ssPage.enterTextInConfirmPinInputBox(randomGeneration.randomNumeric(4));
		ssPage.clickOnRemarksInputBox();
		ssPage.enterTextInRemarksInputBox("Testing");
		ssPage.clickOnChangePINSubmitBtn();
		ssPage.clickOnYesConfirmationBtn();
		rm.waitFor(2000);
		if (ssPage.newAndconfirmNewPinMismatchErrorMessage().contains("New PIN and confirm PIN are not same")) {
			ExtentI.Markup(ExtentColor.GREEN, "Unable to change the PIN with entering incorrect new and confirm new PIN");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Able to change the PIN with with entering incorrect new and confirm new PIN");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}
	
	
	public void changePinWithBlankInput(String ParentCategory, String FromCategory) throws InterruptedException {
		final String methodname = "changePinWithIncorrectNewAndConfirmNewPin";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		ssPage.clickOnOldPinInputBox();
		ssPage.enterTextInOldPinInputBox(randomGeneration.randomNumeric(4));
		ssPage.clickOnNewPinInputBox();
		ssPage.enterTextInNewPinInputBox(randomGeneration.randomNumeric(4));
		ssPage.clickOnConfirmPinInputBox();
		ssPage.enterTextInConfirmPinInputBox(randomGeneration.randomNumeric(4));
		ssPage.clickOnChangePINSubmitBtn();
		ssPage.clickOnYesConfirmationBtn();
		rm.waitFor(2000);
		if (ssPage.getErrorMessage().contains("Remarks Required.")) {
			ExtentI.Markup(ExtentColor.GREEN, "Error message is displayed to enter value in missing fields");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Error message is not displayed to enter value in missing fields");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}
	
	public void changePassword(String ParentCategory, String FromCategory) throws InterruptedException {
		final String methodname = "changePin";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String password = randomGeneration.randomNumeric(4);
		Log.info("Generated PIN: " + password);
		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		ssPage.clickOnPasswordMenu();
		ssPage.clickOnOldPinInputBox();
		String userPassword = DBHandler.AccessHandler.fetchUserPassword("rarya_staff");
		Log.info("Fetched the password from Db: " + userPassword);
		ssPage.enterTextInOldPinInputBox(userPassword);
		ssPage.clickOnNewPinInputBox();
		ssPage.enterTextInNewPinInputBox(password);
		ssPage.clickOnConfirmPinInputBox();
		ssPage.enterTextInConfirmPinInputBox(password);
		ssPage.clickOnChangePINSubmitBtn();
		ssPage.clickOnYesConfirmationBtn();
		rm.waitFor(2000);
		if (ssPage.changePasswordSuccessMessage().contains(" Change Password Successful ")) {
			ExtentI.Markup(ExtentColor.GREEN, "Successfully able to change the PIN");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Unable to change the PIN");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}
	
	public void changePasswordWithIncorrectOldPwd(String ParentCategory, String FromCategory) throws InterruptedException {
		final String methodname = "changePin";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String password = randomGeneration.randomNumeric(4);
		Log.info("Generated PIN: " + password);
		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		ssPage.clickOnPasswordMenu();
		ssPage.clickOnOldPinInputBox();
		ssPage.enterTextInOldPinInputBox(password);
		ssPage.clickOnNewPinInputBox();
		ssPage.enterTextInNewPinInputBox(password);
		ssPage.clickOnConfirmPinInputBox();
		ssPage.enterTextInConfirmPinInputBox(password);
		ssPage.clickOnChangePINSubmitBtn();
		ssPage.clickOnYesConfirmationBtn();
		rm.waitFor(2000);
		if (ssPage.oldPasswordMismatchErrorMessage().contains("Old password entered is wrong.")) {
			ExtentI.Markup(ExtentColor.GREEN, "Unable to change the password with wrong old password");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Able to change the password with wrong old password");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}
	
	public void changePwdWithIncorrectNewAndConfirmNewPwd(String ParentCategory, String FromCategory) throws InterruptedException {
		final String methodname = "changePwdWithIncorrectNewAndConfirmNewPwd";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		ssPage.clickOnPasswordMenu();
		ssPage.clickOnOldPinInputBox();
		ssPage.enterTextInOldPinInputBox(randomGeneration.randomNumeric(4));
		ssPage.clickOnNewPinInputBox();
		ssPage.enterTextInNewPinInputBox(randomGeneration.randomNumeric(4));
		ssPage.clickOnConfirmPinInputBox();
		ssPage.enterTextInConfirmPinInputBox(randomGeneration.randomNumeric(4));
		ssPage.clickOnChangePINSubmitBtn();
		ssPage.clickOnYesConfirmationBtn();
		if (ssPage.newAndconfirmNewPwdMismatchErrorMessage().contains("New and confirm password does not match.")) {
			ExtentI.Markup(ExtentColor.GREEN, "Unable to change the Password with entering incorrect new and confirm new Password");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Able to change the Password with with entering incorrect new and confirm new Password");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}
	
	public void validateEyeIcon(String ParentCategory, String FromCategory) throws InterruptedException {
		final String methodname = "validateEyeIcon";
		Log.methodEntry(methodname, ParentCategory, FromCategory);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String loginID = login.getUserLoginID(driver, "ChannelUser", FromCategory);
		Log.info("LOGINID : " + loginID);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		cuHomePage.clickProfileButton();
		cuHomePage.clickOnSecuritySettingsLink();
		ssPage.clickOnPasswordMenu();
		ssPage.clickOnOldPinInputBox();
		String password = randomGeneration.randomNumeric(4);
		ssPage.enterTextInOldPinInputBox(password);
		ssPage.clickOnEyeIcon();
		if (ssPage.fetchValueFromOldPinInputBox().contains(password)) {
			ExtentI.Markup(ExtentColor.GREEN, "Entered value is decrypted as: " + password);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			currentNode.log(Status.FAIL, "Entered value is not decrypted");
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}
}
