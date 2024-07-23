package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.recharge.Recharges;
//import angular.pageobjects.c2cvouchertransfer.Vouchers;
import angular.pageobjects.voucher.Vouchers ;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.asserts.SoftAssert;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class EVDRechargeRevamp extends BaseTest {

	public WebDriver driver;
	LoginRevamp login;
	Recharges recharges;
	Vouchers vouchers;
	SoftAssert SAssert = new SoftAssert();

	/*C2STransferPage C2STransferPage;
	C2SRechargeConfirmPage C2SRechargeConfirmPage;
	C2SRechargeConfirmNotificationPage C2SRechargeConfirmNotificationPage;
	C2SRechargeNotificationDisplayedPage C2SRechargeNotificationDisplayedPage;
	C2STransferSubCategoriesPage `SubCategoriesPage;
	C2STransactionStatus C2STransactionStatus;
	RandomGeneration randomNum;
	SystemPreferencePage sysPref;
	ServicePreferencePage servPref;
	SuperAdminHomePage suHomepage;
	NetworkAdminHomePage naHomepage;
	PreferenceSubCategories naPref;
	ServiceClassPreference naServPref;
	SelectNetworkPage networkPage;*/

	public EVDRechargeRevamp(WebDriver driver) {
		this.driver = driver;
		login = new LoginRevamp();
		recharges = new Recharges(driver);
		vouchers = new Vouchers(driver);


		
		/*C2STransferPage =new C2STransferPage(driver);
		C2SRechargeConfirmPage =new C2SRechargeConfirmPage(driver);
		C2SRechargeConfirmNotificationPage =new C2SRechargeConfirmNotificationPage(driver);
		C2SRechargeNotificationDisplayedPage =new C2SRechargeNotificationDisplayedPage(driver);
		C2STransferSubCategoriesPage =new C2STransferSubCategoriesPage(driver);
		C2STransactionStatus=new C2STransactionStatus();
		randomNum = new RandomGeneration();
		sysPref = new SystemPreferencePage(driver);
		servPref = new ServicePreferencePage(driver);
		suHomepage = new SuperAdminHomePage(driver);
		naHomepage = new NetworkAdminHomePage(driver);
		naPref = new PreferenceSubCategories(driver);
		naServPref = new ServiceClassPreference(driver);
		networkPage = new SelectNetworkPage(driver);*/
	}


	public void performEVDRecharge(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRecharge";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		String transferID = null, transferStatus = null, trf_status = null;
		String successStatus;
		/*if(recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading() ;
		}
		else {
			recharges.clickRechargeHeading();
			//recharges.clickRecharge();
			vouchers.clickEVDHeading() ;
		}*/

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {

			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("EVDRecharge"))) {
				vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
				vomsList.put("type", ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
				vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
				vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
				vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));

				if (recharges.isRechargeVisibile()) {
					recharges.clickRecharge();
					vouchers.clickEVDHeading();
				} else {
					recharges.clickRechargeHeading();
					//recharges.clickRecharge();
					vouchers.clickEVDHeading();
				}

				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterSubMSISDN(SubMSISDN);
				vouchers.enterDenomination(vomsList.get("vomsMRP"));

				//int mvd_min_voucher_number = Integer.parseInt(MVD_MIN_VOUCHER);
				String DBMRP = vomsList.get("vomsMRP") + "00";
				Log.info(" Denomination : " + DBMRP);
				boolean checkEVDVoucherAvailable = DBHandler.AccessHandler.checkEnabledElectronicVoucherAvailable(DBMRP);
				if (checkEVDVoucherAvailable) {
					recharges.clickRechargeIcon();
					recharges.enterPin(PIN);
					recharges.clickRechargeButton();
					String EVDTransactionID = null;
					int EVDTransferStatus = 0;
					boolean successPopUP = recharges.successPopUPVisibility();
					if (successPopUP) {
						String EVDTransferSuccessful = vouchers.getEVDTransferSuccessful();
						EVDTransactionID = vouchers.getEVDTransactionID();
						EVDTransferStatus = Integer.parseInt(DBHandler.AccessHandler.fetchTransferStatus(EVDTransactionID));

						//if (EVDTransferStatus == 200 && EVDTransferSuccessful.contains("Recharge Successful")) {
						if (EVDTransferStatus == 200) {
							ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + EVDTransferStatus + " with TXN ID: " + EVDTransactionID + ", hence Transaction Successful");
							ExtentI.attachCatalinaLogsForSuccess();
						} else {
							ExtentI.Markup(ExtentColor.RED, "Transaction is not successful. Transfer Status on WEB: " + EVDTransferStatus + " | TXN ID: " + EVDTransactionID);
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();
							vouchers.VOMSClickTryAgain();
							vouchers.EVDClickResetButton();
						}
						recharges.clickDoneButton();
					} else {
						ExtentI.Markup(ExtentColor.RED, "Transaction failed");
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
						vouchers.VOMSClickTryAgain();
						vouchers.EVDClickResetButton();
					}
				} else {
					ExtentI.Markup(ExtentColor.RED, "Voucher not available");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
					vouchers.VOMSClickTryAgain();
					vouchers.EVDClickResetButton();
				}
			}
		}
		Log.methodExit(methodname);
	}


	/* NEGATIVE FUNCTIONALITY FOR C2STRASNFERREVAMP */
	public void performEVDRechargeBlankDenomination(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeBlankDenomination";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		String transferID = null, transferStatus = null, trf_status = null;

		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}


		vouchers.enterDenomination("");

		recharges.clickRechargeIcon();

		List<WebElement> errorMessageCaptured = recharges.blankErrorMessages();
		String actualMessage = null;
		String expectedMessage = "Amount is required.";
		boolean flag = false;
		for (WebElement ele : errorMessageCaptured) {
			actualMessage = ele.getText();
			if (expectedMessage.equals(actualMessage)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			Assertion.assertContainsEquals(actualMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Blank Amount Error not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);

	}

	public void performEVDRechargeAlphaNumericDenomination(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeAlphaNumericDenomination";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}


		//String amount = RandomGeneration.randomAlphaNumeric(3);
		String amount = "eeee";
		vouchers.enterDenomination(amount);

		recharges.clickRechargeIcon();

		List<WebElement> errorMessageCaptured = recharges.blankErrorMessages();
		String actualMessage = null;
		//String expectedMessage = "Amount is required.";
		String expectedMessage = "Amount cannot be alphanumeric.";
		boolean flag = false;
		for (WebElement ele : errorMessageCaptured) {
			actualMessage = ele.getText();
			if (expectedMessage.equals(actualMessage)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			Assertion.assertContainsEquals(actualMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Alphanumeric Error didnt display on GUI or Amount accepted Alphanumeric Value");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}


	public void performEVDRechargeNegativeDenomination(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeNegativeDenomination";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}
		String MRP = null;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				Log.info("MRP : " + MRP);
				break;
			}
		}

		String evd_value_str = "-" + MRP;
		vouchers.enterDenomination(evd_value_str);

		recharges.clickRechargeIcon();
		List<WebElement> errorMessageCaptured = recharges.blankErrorMessages();
		String actualMessage = null;
		String expectedMessage = "Amount cannot be alphanumeric.";
		boolean flag = false;
		for (WebElement ele : errorMessageCaptured) {
			actualMessage = ele.getText();
			if (expectedMessage.equals(actualMessage)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			Assertion.assertContainsEquals(actualMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Negative Amount Error not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}


	public void performEVDRechargeZeroDenomination(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeZeroDenomination";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		String transferID = null, transferStatus = null, trf_status = null;

		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}

		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		vouchers.enterDenomination("0000");

		recharges.clickRechargeIcon();
		recharges.enterPin(PIN);

		recharges.clickRechargeButton();
		String expectedMessage = "ZERO";
		String actualMessage = null;

		boolean flag = false;
		boolean successPopUP = recharges.failedPopUPVisibility();
		if (successPopUP == true) {
			transferStatus = recharges.transferStatusFailed();    //recharge failed
			if (transferStatus.toUpperCase().contains("FAIL")) {
				flag = true;

			} else {
				flag = false;
			}
		}

		if (flag) {
			actualMessage = recharges.getInvalidTextMessage().toUpperCase();
			Assertion.assertContainsEquals(expectedMessage, actualMessage);
			recharges.clicktryAgainForFailRecharges();
			ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with ZERO Amount");
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Zero amount");
			/*ExtentI.getChannelRequestDailyLogs(transferID);
			ExtentI.getOneLineTXNLogsC2S(transferID);*/
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}


	public void performEVDRechargeBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeBlankPIN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}

		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		String MRP = null;

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			if (services.equals(_masterVO.getProperty("EVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				vouchers.enterDenomination(MRP);
				break;
			}
		}

		recharges.clickRechargeIcon();
		recharges.enterPin("");
		recharges.clickRechargeButton();
		String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
		boolean rechargeButtonAvailable = recharges.checkRechargeButtonIsClickable();    //false

		if (!rechargeButtonAvailable) {
			ExtentI.Markup(ExtentColor.GREEN, "RECHARGE BUTTON DISABLED WHEN BLANK PIN PROVIDED");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else if (rechargeButtonAvailable) {
			ExtentI.Markup(ExtentColor.RED, "RECHARGE BUTTON AVAILABLE AFTER BLANK PIN PROVIDED");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		recharges.clickCloseEnterPINPopup();
		Log.methodExit(methodname);

	}


	public void performEVDRechargeCloseEnterPINPopup(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeCloseEnterPINPopup";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		String MRP = null;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("EVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				Log.info("MRP : " + MRP);
				vouchers.enterDenomination(MRP);
				break;
			}
		}
		recharges.clickRechargeIcon();
		recharges.clickCloseEnterPINPopup();

		boolean successPopUP = recharges.RechargeIconVisibility();
		if (successPopUP == true) {
			ExtentI.Markup(ExtentColor.GREEN, "Enter PIN Popup Closed Successfully");
			ExtentI.attachCatalinaLogsForSuccess();

		} else {
			ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup didnt close");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}


	public void performEVDRechargeInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeInvalidPIN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		String transferID = null, transferStatus = null, trf_status = null;
		RandomGeneration RandomGeneration = new RandomGeneration();


		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}

		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		String MRP = null;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("EVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		vouchers.enterDenomination(MRP);

		recharges.clickRechargeIcon();
		PIN = RandomGeneration.randomDecimalNumer(1, 2);
		recharges.enterPin(PIN);
		Log.info("PIN Entered : " + PIN);
		recharges.clickRechargeButton();
		String expectedMessage = "Invalid PIN";
		String actualMessage = null;

		boolean flag = false;
		boolean successPopUP = recharges.failedPopUPVisibility();
		if (successPopUP == true) {
			transferStatus = recharges.transferStatusFailed();    //recharge failed
			if (transferStatus.toUpperCase().contains("FAIL")) {
				flag = true;

			} else {
				flag = false;
			}
		}

		if (flag) {
			actualMessage = recharges.getInvalidTextMessage();
			Assertion.assertContainsEquals(actualMessage, expectedMessage);
			recharges.clicktryAgainForFailRecharges();
			ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Invalid(decimal digit) PIN");
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Invalid(decimal digit) PIN");
				/*ExtentI.getChannelRequestDailyLogs(transferID);
				ExtentI.getOneLineTXNLogsC2S(transferID);*/
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}


	public void performEVDRechargeAlphanumericPIN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeAlphanumericPIN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		String transferID = null, transferStatus = null, trf_status = null;
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		String MRP = null;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {
			vomsList = new HashMap<String, String>();
			if (service.equals(_masterVO.getProperty("EVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		vouchers.enterDenomination(MRP);

		recharges.clickRechargeIcon();
		PIN = RandomGeneration.randomAlphaNumeric(4);
		recharges.enterPin(PIN);
		recharges.clickRechargeButton();

		boolean flag = false;
		boolean successPopUP = recharges.failedPopUPVisibility();
		if (successPopUP == true) {
			transferStatus = recharges.transferStatusFailed();    //recharge failed
			if (transferStatus.toUpperCase().contains("FAIL")) {
				ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Alphanumeric PIN");
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			} else {
				ExtentI.Markup(ExtentColor.RED, "Transaction went ahead with Alphanumeric PIN");
				ExtentI.getChannelRequestDailyLogs(transferID);
				ExtentI.getOneLineTXNLogsC2S(transferID);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
		}
		Log.methodExit(methodname);
	}





	/* --------		YASH 		----- */


	public void performEVDRechargeBlankMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeBlankMsisdn";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}

		recharges.enterSubMSISDN("");
		String MRP = null;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {
			vomsList = new HashMap<String, String>();
			if (service.equals(_masterVO.getProperty("EVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		vouchers.enterDenomination(MRP);
		recharges.clickRechargeIcon();
		List<WebElement> errorMessageCaptured = recharges.blankErrorMessages();
		String actualMessage = null;
		String expectedMessage = "Mobile number is required.";
		boolean flag = false;
		for (WebElement ele : errorMessageCaptured) {
			actualMessage = ele.getText();
			if (expectedMessage.equals(actualMessage)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			Assertion.assertContainsEquals(actualMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Blank MSISDN error not shown");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void performEVDRechargeInvalidMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeInvalidMSISDN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}

		String SubMSISDN = null;
		SubMSISDN = RandomGeneration.randomNumeric(5);
		recharges.enterSubMSISDN(SubMSISDN);
		String MRP = null;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {
			vomsList = new HashMap<String, String>();
			if (service.equals(_masterVO.getProperty("EVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		vouchers.enterDenomination(MRP);

		recharges.clickRechargeIcon();
		List<WebElement> errorMessageCaptured = recharges.blankErrorMessages();
		String actualMessage = null;
		String expectedMessage = "Please enter valid Mobile Number.";
		boolean flag = false;
		for (WebElement ele : errorMessageCaptured) {
			actualMessage = ele.getText();
			if (expectedMessage.equals(actualMessage)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			Assertion.assertContainsEquals(actualMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Invalid MSISDN error not shown");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}


	public void performEVDRechargeAlphaNumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeAlphaNumericMSISDN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/
		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}

		recharges.enterSubMSISDN("eee");

		recharges.clickRechargeIcon();
		List<WebElement> errorMessageCaptured = recharges.blankErrorMessages();
		String actualMessage = null;
		String expectedMessage = "Please enter valid Mobile Number.";
		for (WebElement ele : errorMessageCaptured) {
			actualMessage = ele.getText();
			Assertion.assertContainsEquals(actualMessage, expectedMessage);

			if (expectedMessage.equals(actualMessage)) {
				ExtentI.Markup(ExtentColor.GREEN, "Validation Error Status Found as: " + actualMessage);
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
				break;
			} else {
				ExtentI.Markup(ExtentColor.RED, "Transaction is not successful");
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
		}

		Log.methodExit(methodname);
	}

	public void performEVDRechargeReset(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRechargeResetButton";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		String MRP = null;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			if (services.equals(_masterVO.getProperty("EVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				vouchers.enterDenomination(MRP);
				break;
			}
		}
		recharges.clickResetButton();
		String blankMSISDN = recharges.getblanksubMSISDN();
		String blankAmount = recharges.getblanksubAmount();

		Boolean checkMSISDN = blankMSISDN.equals("");
		Boolean checkAmount = blankAmount.equals("");
		if (checkAmount && checkMSISDN) {
			ExtentI.Markup(ExtentColor.GREEN, "All fields are blank hence Reset button click successful");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Fields are not blank hence Reset button failed.");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void performEVDRechargeCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRecharge";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		String transferID = null, transferStatus = null, trf_status = null;
		String successStatus;
		/*if(recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading() ;
		}
		else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading() ;
		}*/

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {

			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("EVDRecharge"))) {
				vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
				vomsList.put("type", ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
				vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
				vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
				vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));

				if (recharges.isRechargeVisibile()) {
					recharges.clickRecharge();
					vouchers.clickEVDHeading();
				} else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					vouchers.clickEVDHeading();
				}

				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterSubMSISDN(SubMSISDN);
				vouchers.enterDenomination(vomsList.get("vomsMRP"));

				//int mvd_min_voucher_number = Integer.parseInt(MVD_MIN_VOUCHER);
				String DBMRP = vomsList.get("vomsMRP") + "00";
				Log.info(" Denomination : " + DBMRP);
				boolean checkEVDVoucherAvailable = DBHandler.AccessHandler.checkEnabledElectronicVoucherAvailable(DBMRP);
				if (checkEVDVoucherAvailable) {
					recharges.clickRechargeIcon();
					recharges.enterPin(PIN);
					recharges.clickRechargeButton();
					vouchers.clickCopyButton();
					Actions actions = new Actions(driver);
					actions.sendKeys(Keys.chord(Keys.CONTROL, "v")).build().perform();
					String clipboard = null;
					try {
						clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					transferID = vouchers.transferID();
					Log.info("Copied Transfer ID fetched as : " + clipboard);
					if (clipboard.contains(transferID)) {
						ExtentI.Markup(ExtentColor.GREEN, "Transaction ID Copied");
						ExtentI.attachCatalinaLogsForSuccess();
						ExtentI.attachScreenShot();
					} else {
						currentNode.log(Status.FAIL, "Transaction ID Copy failed");
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
					vouchers.clickDoneButton();
				}
			}
		}
		Log.methodExit(methodname);
	}




	/*public void performEVDRechargePINIsEmpty(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performEVDRecharge";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		String MRP = null;
		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading();
		}

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {
			vomsList = new HashMap<String, String>();
			if (service.equals(_masterVO.getProperty("EVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		vouchers.enterDenomination(MRP);

		recharges.clickRechargeIcon();
		recharges.enterPin(PIN);

		recharges.clickRechargeButton();
		//vouchers.clickDoneAfterEVDRecharge() ;
		*//* -- CHECK IF PIN IS BLANK AFTER CLICKING CANCEL RECHARGE -- *//*
		boolean successPopUP = recharges.successPopUPVisibility();
		if (successPopUP == true) {
			vouchers.VOMSClickTryAgain();
			String blankMSISDN = recharges.getblanksubMSISDN();
			String blankAmount = recharges.getblanksubAmount();

			Boolean checkMSISDN = blankMSISDN.equals("");
			Boolean checkAmount = blankAmount.equals("");
			if (checkAmount && checkMSISDN) {
				ExtentI.Markup(ExtentColor.GREEN, "All fields are blank hence Reset button click successful");
				ExtentI.attachCatalinaLogsForSuccess();
				ExtentI.attachScreenShot();
			} else {
				ExtentI.Markup(ExtentColor.RED, "Fields are not blank hence Reset button failed.");
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
			Log.methodExit(methodname);
		}
	}*/


		/*public void performEVDRechargeCopyButton(String ParentCategory, String FromCategory, String PIN, String service)*//* throws IOException, UnsupportedFlavorException*//* {
		final String methodname = "performEVDRechargeCopyButton";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		String transferID = null, transferStatus = null, trf_status = null;
		if(recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickEVDHeading() ;
		}

		else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickEVDHeading() ;
		}

		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		String MRP = null ;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				vouchers.enterDenomination(MRP) ;
				break;
			}
		}
		recharges.clickRechargeIcon();
		recharges.enterPin(PIN);
		recharges.clickRechargeButton();
		recharges.clickCopyButton();
		Actions actions = new Actions(driver);
		actions.sendKeys(Keys.chord(Keys.CONTROL, "v")).build().perform();
		String clipboard = null;
		try {
			clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		transferID = recharges.transferID();
		Log.info("Copied Transfer ID fetched as : "+clipboard);
		if(clipboard.equals(transferID))
		{
			ExtentI.Markup(ExtentColor.GREEN, "Transaction ID Copied");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Transaction ID Copy failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		recharges.clickRechargeDone() ;
		Log.methodExit(methodname);
	}*/




/*
	public void performEVDRechargePrintButton(String ParentCategory, String FromCategory, String PIN, String service)*//* throws IOException, UnsupportedFlavorException*//* {
		final String methodname = "performEVDRechargePrintButton";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		*//*C2S Transfer Page*//*
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		String transferID = null, transferStatus = null, trf_status = null;
		for (int i = 1; i <= totalRow; i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
				if(recharges.isRechargeVisibile()) {
					recharges.clickRecharge();
					vouchers.clickEVDHeading() ;
				}

				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					vouchers.clickEVDHeading() ;
				}

				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);
				//C2STransferPage.selectService(serviceName);
				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterSubMSISDN(SubMSISDN);
				double b = recharges.getCurrentBalance();
				int a = (int) (b * 0.05);
				int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
				int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
				if (minCardSlab < a && a < midCardSlab) ;
				else if (a > midCardSlab) {
					a = midCardSlab;
				} else if (a < minCardSlab) {
					a = minCardSlab + 1;
				}

				vouchers.enterDenomination("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				recharges.clickPrintButton();
				recharges.printButton();
				//Switch to Print dialog
				Set<String> windowHandles= driver.getWindowHandles();
				if(!((Set) windowHandles).isEmpty());{
					driver.switchTo().window((String)windowHandles.toArray()[windowHandles.size()-1]);
				}
				driver.findElement(By.className("cancel")).click();
				if(!recharges.printButton())
				{
					ExtentI.Markup(ExtentColor.GREEN, "Print clicked successfully");
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				} else {
					ExtentI.Markup(ExtentColor.RED, "Print click failed");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);
	}*/
}
	

