package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.recharge.Recharges;
import angular.pageobjects.c2cvouchertransfer.Vouchers;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.PretupsI;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


public class MVDRechargeRevamp extends BaseTest {

	public WebDriver driver;
	LoginRevamp login;
	Recharges recharges;
	Vouchers vouchers ;
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

	public MVDRechargeRevamp(WebDriver driver) {
		this.driver = driver;
		login = new LoginRevamp();
		recharges = new Recharges(driver);
		vouchers = new Vouchers(driver) ;


		
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


	public void performMVDRecharge(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRecharge";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		Date d = null , d1 = null;
		String transferID = null, transferStatus = null;
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			//vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				//ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("DT")|| ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("D")
				vouchers.clickMVDHeading();
				vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
				vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
				vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
				vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
				String expDate = DBHandler.AccessHandler.getVomsProductExpiry(vomsList.get("vomsProfileName"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
				Date date = new Date();
				String currentDate = sdf.format(date);
				try {
					d = sdf.parse(expDate);
					d1 = sdf.parse(currentDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(d1.compareTo(d) <0)
				{
				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterSubMSISDN(SubMSISDN);
				vouchers.selectDenomination(vomsList.get("vomsMRP"));
				String MVD_MIN_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.MVD_MIN_VOUCHER);

					vouchers.enterNoOfVouchers(MVD_MIN_VOUCHER);
					int mvd_min_voucher_number = Integer.parseInt(MVD_MIN_VOUCHER);
					String DBMRP = vomsList.get("vomsMRP") + "00";
					Log.info(" Denomination : " + DBMRP);
					//String productID = DBHandler.AccessHandler.getProductIDOfVoucherProfile(vomsList.get("vomsProfileName")) ;
					//List<String> noOfVouchersDB = DBHandler.AccessHandler.getMultipleEnabledVoucherSerialNumber(DBMRP, mvd_min_voucher_number,productID);
					List<String> noOfVouchersDB = DBHandler.AccessHandler.getMultipleEnabledVoucherSerialNumber(DBMRP, mvd_min_voucher_number);

					if (noOfVouchersDB != null) {
						for (int j = 0; j < mvd_min_voucher_number; j++) {
							Log.info(noOfVouchersDB.get(j));
						}
						recharges.clickRechargeIcon();
						recharges.enterPin(PIN);
						recharges.clickRechargeButton();
						String successStatus = "Recharge Successful";
						List<String> voucherSerialNumbersGUI;
						List<String> MVDTransactionNumbers = null;
						boolean successPopUP = recharges.successPopUPVisibility();
						if (successPopUP == true) {
							transferStatus = recharges.transferStatus();
							int MVDTransferStatus = 0;
							boolean flag = false;

							if (transferStatus.contains(successStatus)) {
								MVDTransactionNumbers = vouchers.getMVDVoucherTransactionNumbers();
								for (int j = 0; j < MVDTransactionNumbers.size(); j++) {
									Log.info("MVD Transaction ID on GUI : " + MVDTransactionNumbers.get(j));
									MVDTransferStatus = Integer.parseInt(DBHandler.AccessHandler.fetchTransferStatus(MVDTransactionNumbers.get(j)));
									flag = true;
									if (MVDTransferStatus != 200) {
										flag = false;
										break;
									}
								}
								if (flag) {
									if (MVDTransferStatus == 200) {
										ExtentI.Markup(ExtentColor.GREEN, "MVD is successful");
										ExtentI.attachCatalinaLogsForSuccess();
										//vouchers.clickMVDHeading();
									} else {
										ExtentI.Markup(ExtentColor.RED, "MVD is not successful");
										ExtentI.attachCatalinaLogs();
										ExtentI.attachScreenShot();
										vouchers.VOMSClickTryAgain();
										vouchers.MVDClickResetButton();
										//vouchers.clickMVDHeading();
									}
								}
							} else {
								ExtentI.Markup(ExtentColor.RED, "Success status displays FAIL");
								ExtentI.attachCatalinaLogs();
								ExtentI.attachScreenShot();
							}
							recharges.clickDoneButton();
						}
					} else {
						//ExtentI.Markup(ExtentColor.RED,"Voucher not available");
						Assertion.assertSkip("Voucher not available");
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
				}
			}
		}
		Log.methodExit(methodname);
	}


	public void performMVDDownloadMVDInvoice(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRecharge";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		Date d = null , d1 = null;
		String transferID = null, transferStatus = null;
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String, String> vomsList;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				vomsList.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
				vomsList.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
				vomsList.put("vomsMRP", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
				vomsList.put("vomsProfileName", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
				String expDate = DBHandler.AccessHandler.getVomsProductExpiry(vomsList.get("vomsProfileName"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
				Date date = new Date();
				String currentDate = sdf.format(date);
				try {
					d = sdf.parse(expDate);
					d1 = sdf.parse(currentDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(d1.compareTo(d) <0) {
					String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
					recharges.enterSubMSISDN(SubMSISDN);
					vouchers.selectDenomination(vomsList.get("vomsMRP"));
					String MVD_MIN_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.MVD_MIN_VOUCHER);
					vouchers.enterNoOfVouchers(MVD_MIN_VOUCHER);
					int mvd_min_voucher_number = Integer.parseInt(MVD_MIN_VOUCHER);
					String DBMRP = vomsList.get("vomsMRP") + "00";
					Log.info(" Denomination : " + DBMRP);
					//	String productID = DBHandler.AccessHandler.getProductIDOfVoucherProfile(vomsList.get("vomsProfileName")) ;
					List<String> noOfVouchersDB = DBHandler.AccessHandler.getMultipleEnabledVoucherSerialNumber(DBMRP, mvd_min_voucher_number);
					for (int j = 0; j < mvd_min_voucher_number; j++) {
						Log.info(noOfVouchersDB.get(j));
					}
					if (noOfVouchersDB != null) {
						recharges.clickRechargeIcon();
						recharges.enterPin(PIN);
						recharges.clickRechargeButton();
						String successStatus = "Recharge Successful";
						List<String> voucherSerialNumbersGUI;
						List<String> MVDTransactionNumbers = null;
						boolean successPopUP = recharges.successPopUPVisibility();
						if (successPopUP == true) {
							transferStatus = recharges.transferStatus();
							int MVDTransferStatus = 0;
							boolean flag = false;

							if (transferStatus.contains(successStatus)) {
								MVDTransactionNumbers = vouchers.getMVDVoucherTransactionNumbers();
								for (int j = 0; j < MVDTransactionNumbers.size(); j++) {
									MVDTransferStatus = Integer.parseInt(DBHandler.AccessHandler.fetchTransferStatus(MVDTransactionNumbers.get(j)));
									Log.info("MVD Transaction Number : " + DBHandler.AccessHandler.fetchTransferStatus(MVDTransactionNumbers.get(j)));
									flag = true;
									if (MVDTransferStatus != 200) {
										flag = false;
										break;
									}
								}
								if (flag) {
									if (MVDTransferStatus == 200) {
										ExtentI.Markup(ExtentColor.GREEN, "MVD is successful");
										ExtentI.attachCatalinaLogsForSuccess();
										String downloadDirPath = _masterVO.getProperty("MVDInvoicePath");
										int NoOfFilesBefore = vouchers.noOfFilesInDownloadedDirectory(downloadDirPath);
										vouchers.clickDownloadTransactionID();
										int NoOfFilesAfter = vouchers.noOfFilesInDownloadedDirectory(downloadDirPath);
										if (NoOfFilesBefore < NoOfFilesAfter) {
											ExtentI.Markup(ExtentColor.GREEN, "MVD Invoice Downloaded Successfully");
											ExtentI.attachCatalinaLogsForSuccess();
											String PathOfFile = _masterVO.getProperty("MVDInvoicePath");
											ExcelUtility.deleteFiles(PathOfFile);
										} else {
											ExtentI.Markup(ExtentColor.RED, "MVD Invoice Download Failed");
											ExtentI.attachCatalinaLogs();
											ExtentI.attachScreenShot();
										}
									} else {
										ExtentI.Markup(ExtentColor.RED, "MVD is not successful");
										ExtentI.attachCatalinaLogs();
										ExtentI.attachScreenShot();
										vouchers.VOMSClickTryAgain();
										vouchers.MVDClickResetButton();
										//vouchers.clickMVDHeading();
									}
								}
							} else {
								ExtentI.Markup(ExtentColor.RED, "Success status displays FAIL");
								ExtentI.getChannelRequestDailyLogs(transferID);
								ExtentI.getOneLineTXNLogsC2S(transferID);
								ExtentI.attachCatalinaLogs();
								ExtentI.attachScreenShot();
							}
							vouchers.clickMVDHeading();

						}
					} else {
						ExtentI.Markup(ExtentColor.RED, "Voucher not available");
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
				}
				break ;
			}
		}

		Log.methodExit(methodname);

	}


	/* NEGATIVE FUNCTIONALITY FOR C2STRASNFERREVAMP */



	public void performMVDRechargeBlankDenomination(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRechargeBlankAmount";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		String transferID = null, transferStatus = null, trf_status = null;
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		String MRP = null ;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}

		recharges.clickRechargeIcon();

		List<WebElement> errorMessageCaptured = vouchers.blankErrorMessagesOnGUI();
		String actualMessage = null;
		String expectedMessage = "Denomination is required.";
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
			ExtentI.Markup(ExtentColor.RED, "Blank Denomination Error not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}




	public void performMVDRechargeBlankNoOfVouchers(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRechargeBlankNoOfVouchers";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);


		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		String transferID = null, transferStatus = null, trf_status = null;
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}

		vouchers.enterNoOfVouchers("");

		recharges.clickRechargeIcon();

		List<WebElement> errorMessageCaptured = vouchers.blankErrorMessagesOnGUI();
		String actualMessage = null;
		String expectedMessage = "No. of Vouchers is required." ;
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
				ExtentI.Markup(ExtentColor.RED, "No. of Vouchers Error not displayed on GUI");
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
		Log.methodExit(methodname);
	}


	public void performMVDRechargeAlphaNumericNoOfVouchers(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performMVDRechargeAlphaNumericNoOfVouchers";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		String amount = "eeee" ;
		vouchers.enterNoOfVouchers(amount);
		recharges.clickRechargeIcon();
		List<WebElement> errorMessageCaptured = vouchers.blankErrorMessagesOnGUI();
		String actualMessage = null;
		String expectedMessage = "No. of Vouchers cannot be alphanumeric." ;
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
			ExtentI.Markup(ExtentColor.RED, "Alphanumeric Error didnt display on GUI or No of voucher accepted Alphanumeric Value");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}



	public void performMVDRechargeNegativeNoOfVouchers(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performMVDRechargeNegativeAmount";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		String MRP = null ;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		String MVD_MIN_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceMINValue(PretupsI.MVD_MIN_VOUCHER) ;
		int mvd_min_value_int = Integer.parseInt(MVD_MIN_VOUCHER) ;
		mvd_min_value_int = -(mvd_min_value_int) ;
		String mvd_max_value_str = Integer.toString(mvd_min_value_int) ;
		vouchers.enterNoOfVouchers(mvd_max_value_str) ;

		recharges.clickRechargeIcon();
		List<WebElement> errorMessageCaptured = vouchers.blankErrorMessagesOnGUI();
		String actualMessage = null;
		String expectedMessage = "No. of Vouchers cannot be alphanumeric." ;
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
			ExtentI.Markup(ExtentColor.RED, "Negative No of voucher Error not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}




	public void performMVDRechargeZeroNoOfVouchers(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performMVDRechargeZeroNoOfVouchers";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);

		String transferID = null,transferStatus = null,trf_status = null;
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		String MRP = null ;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		vouchers.selectDenomination(MRP);

		vouchers.enterNoOfVouchers("0000");

		recharges.clickRechargeIcon();
		recharges.enterPin(PIN);

		recharges.clickRechargeButton();

		boolean successPopUP = recharges.failedPopUPVisibility();
		if (successPopUP == true) {
			transferStatus = recharges.transferStatusFailed();    //recharge failed
			if (transferStatus.toUpperCase().contains("FAIL")) {
				String failExpectedReason = "You can download only 2 to 10 number of vouchers in a request." ;
				String failActualReason = vouchers.VoucherRechargeFailedReason() ;
				if(failExpectedReason.toUpperCase().contentEquals(failActualReason))
				{
					recharges.clicktryAgainForFailRecharges() ;
					ExtentI.Markup(ExtentColor.GREEN, "Voucher recharge Not Successful with zero voucher");
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
			} else {
				ExtentI.Markup(ExtentColor.RED, "Voucher recharge went ahead with zero voucher") ;
			/*ExtentI.getChannelRequestDailyLogs(transferID);
			ExtentI.getOneLineTXNLogsC2S(transferID);*/
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
		}
		else
		{
			ExtentI.Markup(ExtentColor.RED, "Voucher recharge Failed with zero voucher") ;
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}




	public void performMVDRechargeBlankPIN(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performMVDRechargeBlankPIN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		String transferID = null,transferStatus = null,trf_status = null;
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		String MRP = null ;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		vouchers.selectDenomination(MRP);
		String MVD_MIN_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceMINValue(PretupsI.MVD_MIN_VOUCHER) ;
		vouchers.enterNoOfVouchers(MVD_MIN_VOUCHER);

		recharges.clickRechargeIcon();
		recharges.enterPin("");
		recharges.clickRechargeButton();
		//String  successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
		boolean rechargeButtonAvailable=recharges.checkRechargeButtonIsClickable();	//false

		if (!rechargeButtonAvailable){
			ExtentI.Markup(ExtentColor.GREEN, "RECHARGE BUTTON DISABLED WHEN BLANK PIN PROVIDED");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else if(rechargeButtonAvailable)
		{
			ExtentI.Markup(ExtentColor.RED, "RECHARGE BUTTON AVAILABLE AFTER BLANK PIN PROVIDED");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		vouchers.clickCloseEnterPINPopup() ;

		Log.methodExit(methodname);

	}



	public void performMVDRechargeCloseEnterPINPopup(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performMVDRechargeCloseEnterPINPopup";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);

		String transferID = null,transferStatus = null,trf_status = null;

		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		String MRP = null ;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		vouchers.selectDenomination(MRP);
		String MVD_MIN_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceMINValue(PretupsI.MVD_MIN_VOUCHER) ;
		vouchers.enterNoOfVouchers(MVD_MIN_VOUCHER);
		recharges.clickRechargeIcon();
		vouchers.clickCloseEnterPINPopup() ;

		boolean successPopUP=recharges.RechargeIconVisibility();
		if (successPopUP==true){
			ExtentI.Markup(ExtentColor.GREEN, "Enter PIN Popup Closed Successfully") ;
			ExtentI.attachCatalinaLogsForSuccess();

		}
		else
		{
			ExtentI.Markup(ExtentColor.RED, "Enter PIN Popup didnt close") ;
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}




	public void performMVDRechargeInvalidPIN(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performMVDRechargeInvalidPIN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);

		String transferID = null,transferStatus = null,trf_status = null;
		RandomGeneration RandomGeneration = new RandomGeneration();
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		String MRP = null ;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		vouchers.selectDenomination(MRP);
		String MVD_MIN_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceMINValue(PretupsI.MVD_MIN_VOUCHER) ;
		vouchers.enterNoOfVouchers(MVD_MIN_VOUCHER);
		recharges.clickRechargeIcon();
		PIN = RandomGeneration.randomDecimalNumer(1,2);
		recharges.enterPin(PIN);
		Log.info("PIN Entered : "+PIN) ;
		recharges.clickRechargeButton();

		boolean successPopUP = recharges.failedPopUPVisibility();
		if (successPopUP == true) {
			transferStatus = recharges.transferStatusFailed();    //recharge failed
			if (transferStatus.toUpperCase().contains("FAIL")) {
				String failExpectedReason = "Invalid PIN" ;
				String failActualReason = vouchers.VoucherRechargeFailedReason() ;
				if(failExpectedReason.toUpperCase().contentEquals(failActualReason))
				{
					recharges.clicktryAgainForFailRecharges() ;
					ExtentI.Markup(ExtentColor.GREEN, "Voucher recharge Not Successful with Invalid PIN");
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
			} else {
				ExtentI.Markup(ExtentColor.RED, "Voucher recharge went ahead with Invalid PIN") ;
			/*ExtentI.getChannelRequestDailyLogs(transferID);
			ExtentI.getOneLineTXNLogsC2S(transferID);*/
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
		}
		else
		{
			ExtentI.Markup(ExtentColor.RED, "Voucher recharge Failed with Invalid PIN") ;
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname);
	}





	/* --------		YASH 		----- */


	public void performMVDRechargeBlankMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRechargeBlankMsisdn";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		String transferID = null, transferStatus = null, trf_status = null;

		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		String MRP = null ;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}

		recharges.enterSubMSISDN("");


		recharges.clickRechargeIcon();
		List<WebElement> errorMessageCaptured = vouchers.blankErrorMessagesOnGUI();
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

	public void performMVDRechargeInvalidMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRechargeInvalidMSISDN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		String SubMSISDN = null;
		SubMSISDN = RandomGeneration.randomNumeric(5);
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
				break;
			}
		}
		vouchers.selectDenomination(MRP);
		String MVD_MIN_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.MVD_MIN_VOUCHER);
		int mvd_max_value_int = Integer.parseInt(MVD_MIN_VOUCHER) ;
		String mvd_max_value_str = Integer.toString(mvd_max_value_int) ;
		vouchers.enterNoOfVouchers(mvd_max_value_str);

		recharges.clickRechargeIcon();
		List<WebElement> errorMessageCaptured = vouchers.blankErrorMessagesOnGUI();
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


	public void performMVDRechargeAlphaNumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRechargeAlphaNumericMSISDN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		String transferID = null, transferStatus = null, trf_status = null;
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}

		recharges.enterSubMSISDN("eee");

		recharges.clickRechargeIcon();
		List<WebElement> errorMessageCaptured = vouchers.blankErrorMessagesOnGUI();
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
					ExtentI.Markup(ExtentColor.RED, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + "  | DB TXN Status: " + trf_status);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}

		Log.methodExit(methodname);
	}

	public void performMVDRechargeReset(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRechargeResetButton";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);

		String transferID = null,transferStatus = null,trf_status = null;

		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}

		String MRP = null ;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		vouchers.selectDenomination(MRP);
		String MVD_MIN_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceMINValue(PretupsI.MVD_MIN_VOUCHER) ;
		vouchers.enterNoOfVouchers(MVD_MIN_VOUCHER);

		recharges.clickResetButton();
		String blankMSISDN = recharges.getblanksubMSISDN();
		String blankAmount = recharges.getblanksubAmount();

		Boolean checkMSISDN = blankMSISDN.equals("");
		Boolean checkAmount = blankAmount.equals("");
		if(checkAmount&&checkMSISDN)
		{
			ExtentI.Markup(ExtentColor.GREEN, "All fields are blank hence Reset button click successful");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		}
		else{
			ExtentI.Markup(ExtentColor.RED, "Fields are not blank hence Reset button failed.");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

	Log.methodExit(methodname);
	}

	public void performMVDRechargeGreaterThanMaxLimit(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRecharge";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		String MRP = null ;

		String transferID = null, transferStatus = null, trf_status = null;
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		vouchers.selectDenomination(MRP);
		String MVD_MAX_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceMAXValue(PretupsI.MVD_MAX_VOUCHER) ;
		//Log.info("MVD_MAX_VOUCHER : "+MVD_MAX_VOUCHER) ;
		int mvd_max_value_int = Integer.parseInt(MVD_MAX_VOUCHER) ;
		mvd_max_value_int += 20 ;
		String mvd_max_value_str = Integer.toString(mvd_max_value_int) ;
		vouchers.enterNoOfVouchers(mvd_max_value_str);

		recharges.clickRechargeIcon();
		recharges.enterPin(PIN);
		recharges.clickRechargeButton();

		boolean failPopUP = vouchers.failPopUPVisibility();
		if (failPopUP == true) {
			String failedHeading = vouchers.MVDRechargeFailHeading() ;
			if (failedHeading.toUpperCase().contains("FAIL")) {
				String expectedFailedMVDReason = " You can download only 2 to 10 number of vouchers in a request. " ;
				String failedMVDReason = vouchers.VoucherRechargeFailedReason() ;
				if(failedMVDReason.equals(expectedFailedMVDReason))
				{
					ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogsForSuccess();
				}
			} else {
				ExtentI.Markup(ExtentColor.RED, "No of vouchers exceeding Max Limit is successful") ;
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
			vouchers.VOMSClickTryAgain() ;
		}
		Log.methodExit(methodname);

	}

	public void performMVDRechargeLessThanMinLimit(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRecharge";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		String MRP = null ;

		String transferID = null, transferStatus = null, trf_status = null;
		if (vouchers.isRechargeVisible()) {
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			vouchers.clickRechargeHeading();
			vouchers.clickRecharge();
			vouchers.clickMVDHeading();
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		vouchers.selectDenomination(MRP);
		String MVD_MIN_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceMINValue(PretupsI.MVD_MIN_VOUCHER) ;
		//Log.info("MVD_MAX_VOUCHER : "+MVD_MAX_VOUCHER) ;
		int mvd_min_value_int = Integer.parseInt(MVD_MIN_VOUCHER) ;
		mvd_min_value_int -= 2 ;
		if(mvd_min_value_int < 0)
		{
			mvd_min_value_int = 1 ;
		}
		String mvd_min_value_str = Integer.toString(mvd_min_value_int) ;
		vouchers.enterNoOfVouchers(mvd_min_value_str);

		recharges.clickRechargeIcon();
		recharges.enterPin(PIN);
		recharges.clickRechargeButton();

		boolean failPopUP = vouchers.failPopUPVisibility();
		if (failPopUP == true) {
			String failedHeading = vouchers.MVDRechargeFailHeading() ;
			if (failedHeading.toUpperCase().contains("FAIL")) {
				String expectedFailedMVDReason = " You can download only 2 to 10 number of vouchers in a request. " ;
				String failedMVDReason = vouchers.VoucherRechargeFailedReason() ;
				if(failedMVDReason.equals(expectedFailedMVDReason))
				{
					ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogsForSuccess();
				}
			} else {
				ExtentI.Markup(ExtentColor.RED, "No of vouchers exceeding Max Limit is successful") ;
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
			vouchers.VOMSClickTryAgain() ;
		}
		Log.methodExit(methodname);

	}

/*

	public void performMVDRechargeCopyButton(String ParentCategory, String FromCategory, String PIN, String service)*/
/* throws IOException, UnsupportedFlavorException*//*
 {
		final String methodname = "performMVDRechargeCopyButton";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		String transferID = null, transferStatus = null, trf_status = null;

		if (recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			vouchers.clickMVDHeading();
		} else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			vouchers.clickMVDHeading();
		}

		String MRP = null ;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		HashMap<String,String> vomsList ;
		for (int i = 1; i <= rowCount; i++) {
			String services = ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i);
			vomsList = new HashMap<String, String>();
			if (services.equals(_masterVO.getProperty("MVDRecharge"))) {
				MRP = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
				break;
			}
		}
		String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		recharges.enterSubMSISDN(SubMSISDN);
		vouchers.selectDenomination(MRP);
		String MVD_MIN_VOUCHER = DBHandler.AccessHandler.getSystemPreferenceMINValue(ExcelI.MVD_MIN_VOUCHER) ;
		vouchers.enterNoOfVouchers(MVD_MIN_VOUCHER);
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
		Log.info("Copied Transfer ID fetched as : " + clipboard);
		if (clipboard.equals(transferID)) {
			ExtentI.Markup(ExtentColor.GREEN, "Transaction ID Copied");
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Transaction ID Copy failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		recharges.clickRechargeDone();
		Log.methodExit(methodname);
	}

*/


/*
	public void performMVDRechargeBlankSubservice(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performMVDRechargeBlankSubservice";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		*/
/*C2S Transfer Page*//*



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
					vouchers.clickMVDHeading() ;
				}

				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					vouchers.clickMVDHeading() ;
				}

				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);

				recharges.clickRechargeIcon();
				List<WebElement> errorMessageCaptured = vouchers.blankErrorMessagesOnGUI();
				String actualMessage = null;
				String expectedMessage = "Sub service is required.";

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
					ExtentI.Markup(ExtentColor.GREEN, "Validation Error Status Found as: " + actualMessage);
					ExtentI.attachCatalinaLogsForSuccess();
				} else {
					currentNode.log(Status.FAIL, "Blank SubService Error not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}

				Log.methodExit(methodname);

			}
		}
	}




	public void performMVDRechargeAlphanumericPIN(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performMVDRechargeAlphanumericPIN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String,String[]> serviceMap=new HashMap<String,String[]>();
		String transferID = null,transferStatus = null,trf_status = null;
		RandomGeneration RandomGeneration = new RandomGeneration();

		for(int i=1;i<=totalRow;i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
				if(recharges.isRechargeVisibile()) {
					recharges.clickRecharge();
					vouchers.clickMVDHeading() ;
				}

				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					vouchers.clickMVDHeading() ;
				}

				//C2STransferPage.selectService(serviceName);
				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterSubMSISDN(SubMSISDN);
				vouchers.selectDenomination("204") ;
				 
				vouchers.enterNoOfVouchers("2") ;
				 
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
						currentNode.log(Status.FAIL, "Transaction went ahead with Alphanumeric PIN") ;
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
				}
			}
		}
		Log.methodExit(methodname);
	}




*/
/*voucherSerialNumbersGUI = vouchers.getVoucherSerialNumber();
							for (String serialNumber : voucherSerialNumbersGUI) {
								ExtentI.Markup(ExtentColor.GREEN, "serialNumber : " + serialNumber);
								String MVDTransactionID = DBHandler.AccessHandler.getMVDTransactionID(serialNumber);
								Log.info("MVDTransactionID : "+MVDTransactionID) ;
							}*/


}
	

