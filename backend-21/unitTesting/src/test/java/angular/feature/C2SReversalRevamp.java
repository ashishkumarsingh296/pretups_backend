package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.recharge.Recharges;
import angular.pageobjects.C2SReversal.C2SReversal ;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import restassuredapi.test.C2SReversal;


public class C2SReversalRevamp extends BaseTest {

	public WebDriver driver;
	LoginRevamp login;
	Recharges recharges;
	C2SReversal reversal ;
	SoftAssert SAssert = new SoftAssert();

	/*C2STransferPage C2STransferPage;
	C2SRechargeConfirmPage C2SRechargeConfirmPage;
	C2SRechargeConfirmNotificationPage C2SRechargeConfirmNotificationPage;
	C2SRechargeNotificationDisplayedPage C2SRechargeNotificationDisplayedPage;
	C2STransferSubCategoriesPage C2STransferSubCategoriesPage;
	C2STransactionStatus C2STransactionStatus;
	RandomGeneration randomNum;
	SystemPreferencePage sysPref;
	ServicePreferencePage servPref;
	SuperAdminHomePage suHomepage;
	NetworkAdminHomePage naHomepage;
	PreferenceSubCategories naPref;
	ServiceClassPreference naServPref;
	SelectNetworkPage networkPage;*/

	public C2SReversalRevamp(WebDriver driver) {
		this.driver = driver;
		login = new LoginRevamp();
		recharges = new Recharges(driver);

		reversal = new C2SReversal(driver) ;
		
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


	public void performC2SReversalByMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2SReversalByMsisdn";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

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
					recharges.clickPrepaidRecharge();
				}

				else {
					recharges.clickRechargeHeading();
					//recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
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
				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
				boolean successPopUP = recharges.successPopUPVisibility();
				if (successPopUP == true) {
					transferID = recharges.transferID();
					trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
					transferStatus = recharges.transferStatus();

					if (transferStatus.toUpperCase().contains(successStatus)) {
						ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
						ExtentI.attachCatalinaLogsForSuccess();
						reversal.clickDoneButton();
						if(recharges.isRechargeVisibile()) {
							reversal.clickReversalRecharge();
						}

						else {
							recharges.clickRechargeHeading();
							reversal.clickReversalRecharge();
						}
						reversal.selectSearchBy(_masterVO.getProperty("C2SReversalMobileNumber")) ;
						reversal.enterSearchByValue(SubMSISDN) ;
						reversal.clickProceedButton() ;
						reversal.clickReverseAmountButton() ;
						recharges.enterPin(PIN);
						reversal.clickReverseAmountEnterPinButton();
						
						String actualMsg = reversal.isReversalSuccessfull();
						String msg = "Recharge Reversal Successful";
						
						Log.info("Actual Message: "+ msg);
						Log.info("Message recieved: "+actualMsg );
						
						if (msg.equals(actualMsg)) {
							ExtentI.Markup(ExtentColor.GREEN, "Errors are matched");
							ExtentI.attachCatalinaLogsForSuccess();
						} else {
							currentNode.log(Status.FAIL, "Errors did not match") ;
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();
						}
						
								

					} else {
						currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}

				} else {
					currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);

	}

	public void performC2SReversalByByTransactionId(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2SReversalByMsisdn";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

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
					recharges.clickPrepaidRecharge();
				}

				else {
					recharges.clickRechargeHeading();
					//recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
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
				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
				boolean successPopUP = recharges.successPopUPVisibility();
				if (successPopUP == true) {
					transferID = recharges.transferID();
					trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
					transferStatus = recharges.transferStatus();

					if (transferStatus.toUpperCase().contains(successStatus)) {
						ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
						ExtentI.attachCatalinaLogsForSuccess();
						reversal.clickDoneButton();
						if(recharges.isRechargeVisibile()) {
							reversal.clickReversalRecharge();
						}

						else {
							recharges.clickRechargeHeading();
							reversal.clickReversalRecharge();
						}
						reversal.selectSearchBy(_masterVO.getProperty("C2SReversalTransactionId")) ;
						reversal.enterSearchByValue(transferID) ;
						reversal.clickProceedButton() ;
						reversal.clickReverseAmountButton() ;
						recharges.enterPin(PIN);
						reversal.clickReverseAmountEnterPinButton();
						
						String actualMsg = reversal.isReversalSuccessfull();
						String msg = "Recharge Reversal Successful";
						
						Log.info("Actual Message: "+ msg);
						Log.info("Message recieved: "+actualMsg );
						
						if (msg.equals(actualMsg)) {
							ExtentI.Markup(ExtentColor.GREEN, "Errors are matched");
							ExtentI.attachCatalinaLogsForSuccess();
						} else {
							currentNode.log(Status.FAIL, "Errors did not match") ;
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();
						}
						
								

					} else {
						currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}

				} else {
					currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);

	}
	
	public void performC2SReversalInvalidSearchByMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransfer";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		String transferID = null, transferStatus = null, trf_status = null;
		for (int i = 1; i <= totalRow; i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				if(recharges.isRechargeVisibile()) {
					reversal.clickReversalRecharge();
				}

				else {
					recharges.clickRechargeHeading();
					reversal.clickReversalRecharge();
				}
				reversal.selectSearchBy(_masterVO.getProperty("C2SReversalMobileNumber")) ;
				reversal.enterSearchByValue(SubMSISDN) ;
				reversal.clickProceedButton() ;

				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);
				//C2STransferPage.selectService(serviceName);

				String expectedInvalidMsisdnErrorMessage = "Either Records do not exist or the transaction is too old to reverse." ;
				String actualInvalidMsisdnErrorMessage = reversal.getInvalidMsisdnTransactionErrorMessage() ;
				Log.info("expectedInvalidMsisdnErrorMessage : "+expectedInvalidMsisdnErrorMessage);
				Log.info("actualMessage : "+actualInvalidMsisdnErrorMessage);

				if (expectedInvalidMsisdnErrorMessage.equals(actualInvalidMsisdnErrorMessage)) {
					ExtentI.Markup(ExtentColor.GREEN, "Errors are matched");
					ExtentI.attachCatalinaLogsForSuccess();
				} else {
					currentNode.log(Status.FAIL, "Errors did not match") ;
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);

	}


	public void performC2SReversalBlankSearchByDropdown(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransfer";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		String transferID = null, transferStatus = null, trf_status = null;
		for (int i = 1; i <= totalRow; i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				
				if(recharges.isRechargeVisibile()) {
					reversal.clickReversalRecharge();
				}

				else {
					recharges.clickRechargeHeading();
					reversal.clickReversalRecharge();
				}
				
				reversal.spinnerWait();
				reversal.clickProceedButton() ;
				String expectedErrorMessage = "Search criteria is required." ;
				String actualErrorMessage = reversal.getBlankSearchByErrorMessage() ;
				Log.info("expectedMessage : "+expectedErrorMessage);
				Log.info("actualMessage : "+actualErrorMessage);

				if (expectedErrorMessage.equals(actualErrorMessage)) {
						ExtentI.Markup(ExtentColor.GREEN, "Errors are matched");
						ExtentI.attachCatalinaLogsForSuccess();
					} else {
						currentNode.log(Status.FAIL, "Errors did not match") ;
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
			}
		}
		Log.methodExit(methodname);
	}

	public void performC2SReversalBlankMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransfer";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		String transferID = null, transferStatus = null, trf_status = null;
		for (int i = 1; i <= totalRow; i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				if(recharges.isRechargeVisibile()) {
					reversal.clickReversalRecharge();
				}

				else {
					recharges.clickRechargeHeading();
					reversal.clickReversalRecharge();
				}
				
				reversal.selectSearchBy(_masterVO.getProperty("C2SReversalMobileNumber"));
				reversal.clickProceedButton() ;
				String expectedErrorMessage = "Mobile number/Transaction ID is required." ;
				String actualErrorMessage = reversal.getBlankMsisdnTransactionErrorMessage() ;
				Log.info("expectedMessage : "+expectedErrorMessage);
				Log.info("actualMessage : "+actualErrorMessage);

				if (expectedErrorMessage.equals(actualErrorMessage)) {
					ExtentI.Markup(ExtentColor.GREEN, "Errors are matched");
					ExtentI.attachCatalinaLogsForSuccess();
				} else {
					currentNode.log(Status.FAIL, "Errors did not match") ;
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);
	}


	public void performC2SGiftTransfer(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransfer";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

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
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
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


				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.giftRechargeCheckBox();
				String giftMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterGifterMSISDN(giftMSISDN);
			/*String name=UniqueChecker.UC_GifterName();
			recharges.enterGifterName(name);	*/                //GIFT RECHARGE
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");


				boolean successPopUP = recharges.successPopUPVisibility();
				if (successPopUP == true) {
					transferID = recharges.transferID();
					trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
					transferStatus = recharges.transferStatus();

					if (transferStatus.toUpperCase().contains(successStatus)) {
						ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogsForSuccess();
					} else {
						currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
					recharges.clickDoneButton();
				} else {
					currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);

	}

	public void performC2STransferPostpaid(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransfer";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/
		/*recharges.clickRechargeHeading();
		recharges.clickRecharge();
		recharges.clickPostpaidRecharge();*/
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
					recharges.clickPostpaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPostpaidRecharge();
				}
				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);
				//C2STransferPage.selectService(serviceName);
				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Postpaid");
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


				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");


				boolean successPopUP = recharges.successPopUPVisibility();
				if (successPopUP == true) {
					transferID = recharges.transferID();
					trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
					transferStatus = recharges.transferStatus();

					if (transferStatus.toUpperCase().contains(successStatus)) {
						ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogsForSuccess();
					} else {
						currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
					recharges.clickDoneButton();

				} else {
					currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);

	}

	public void performC2STransferInternet(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransfer";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/
//		if(recharges.isRechargeVisibile()) {
//			recharges.clickRecharge();
//			recharges.clickInternetRecharge();
//		}
//		
//		else {
//			recharges.clickRechargeHeading();
//			recharges.clickRecharge();
//			recharges.clickInternetRecharge();
//		}
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
					recharges.clickInternetRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickInternetRecharge();
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


				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				String notficationMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterNotificationNumber(notficationMSISDN);
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");


				boolean successPopUP = recharges.successPopUPVisibility();
				if (successPopUP == true) {
					transferID = recharges.transferID();
					trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
					transferStatus = recharges.transferStatus();

					if (transferStatus.toUpperCase().contains(successStatus)) {
						ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogsForSuccess();
					} else {
						currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
					recharges.clickDoneButton();

				} else {
					currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);

	}

	public void performC2STransferFixline(String ParentCategory, String FromCategory, String PIN, String service) {

		final String methodname = "performC2STransfer";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

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
					recharges.clickFixlineRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickFixlineRecharge();
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


				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				String notficationMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterNotificationNumber(notficationMSISDN);
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");


				boolean successPopUP = recharges.successPopUPVisibility();
				if (successPopUP == true) {
					transferID = recharges.transferID();
					trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
					transferStatus = recharges.transferStatus();

					if (transferStatus.toUpperCase().contains(successStatus)) {
						ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogsForSuccess();
					} else {
						currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}
					recharges.clickDoneButton();
				} else {
					currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}


			}
		}
		Log.methodExit(methodname);

	}

	public void performC2SReversalInvalidPin(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2SReversalInvalidPin";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

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
					recharges.clickPrepaidRecharge();
				}

				else {
					recharges.clickRechargeHeading();
					//recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
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
				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
				boolean successPopUP = recharges.successPopUPVisibility();
				if (successPopUP == true) {
					transferID = recharges.transferID();
					trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
					transferStatus = recharges.transferStatus();

					if (transferStatus.toUpperCase().contains(successStatus)) {
						ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
						ExtentI.attachCatalinaLogsForSuccess();
						reversal.clickDoneButton();
						if(recharges.isRechargeVisibile()) {
							reversal.clickReversalRecharge();
						}

						else {
							recharges.clickRechargeHeading();
							reversal.clickReversalRecharge();
						}
						reversal.selectSearchBy(_masterVO.getProperty("C2SReversalMobileNumber")) ;
						reversal.enterSearchByValue(SubMSISDN) ;
						reversal.clickProceedButton() ;
						reversal.clickReverseAmountButton() ;
						
						RandomGeneration randomGeneration = new RandomGeneration();
						int pinLength = PIN.length();
					    String randomPin = randomGeneration.randomNumeric(pinLength);
						
						recharges.enterPin(randomPin);
						reversal.clickReverseAmountEnterPinButton();
						
						String expectedInvalidMsisdnErrorMessage = "The PIN you have entered is incorrect." ;
						String actualInvalidMsisdnErrorMessage = reversal.getInvalidPinMessage();
						Log.info("expectedInvalidMsisdnErrorMessage : "+expectedInvalidMsisdnErrorMessage);
						Log.info("actualMessage : "+actualInvalidMsisdnErrorMessage);

						if (expectedInvalidMsisdnErrorMessage.equals(actualInvalidMsisdnErrorMessage)) {
							ExtentI.Markup(ExtentColor.GREEN, "Errors are matched");
							ExtentI.attachCatalinaLogsForSuccess();
						} else {
							currentNode.log(Status.FAIL, "Errors did not match") ;
							ExtentI.attachCatalinaLogs();
							ExtentI.attachScreenShot();
						}
						


					} else {
						currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}

				} else {
					currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);

	}
	
	public void performC2SReversalBlankPin(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2SReversalBlankPin";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

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
					recharges.clickPrepaidRecharge();
				}

				else {
					recharges.clickRechargeHeading();
					//recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
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
				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
				boolean successPopUP = recharges.successPopUPVisibility();
				if (successPopUP == true) {
					transferID = recharges.transferID();
					trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
					transferStatus = recharges.transferStatus();

					if (transferStatus.toUpperCase().contains(successStatus)) {
						ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
						ExtentI.attachCatalinaLogsForSuccess();
						reversal.clickDoneButton();
						if(recharges.isRechargeVisibile()) {
							reversal.clickReversalRecharge();
						}

						else {
							recharges.clickRechargeHeading();
							reversal.clickReversalRecharge();
						}
						reversal.selectSearchBy(_masterVO.getProperty("C2SReversalMobileNumber")) ;
						reversal.enterSearchByValue(SubMSISDN) ;
						reversal.clickProceedButton() ;
						reversal.clickReverseAmountButton() ;
						
						recharges.enterPin("");//blank pin
						reversal.clickReverseAmountEnterPinButton();
						
						Log.info("Checking if we are not able to proceed with blank pin");
						
						if(reversal.isReverseAmountButtonVisible()) {
							ExtentI.Markup(ExtentColor.GREEN, "We are not able to proceed by entering blank pin");
							ExtentI.attachCatalinaLogsForSuccess();
						}else {
							currentNode.log(Status.FAIL, "We were able to proceed with blank pin") ;
							ExtentI.attachCatalinaLogs();
						}			

					} else {
						currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
						ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}

				} else {
					currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + " | TXN ID: " + transferID + " | DB TXN Status: " + trf_status);
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);

	}
	
	public void performC2SReversalTestResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2SReversalBlankPin";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

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
					recharges.clickPrepaidRecharge();
				}

				else {
					recharges.clickRechargeHeading();
					//recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
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
				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
				boolean successPopUP = recharges.successPopUPVisibility();
				if (successPopUP == true) {
					transferID = recharges.transferID();
					trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
					transferStatus = recharges.transferStatus();

					if (transferStatus.toUpperCase().contains(successStatus)) {
						ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: " + transferID + ", hence Transaction Successful");
						ExtentI.attachCatalinaLogsForSuccess();
						reversal.clickDoneButton();
						if(recharges.isRechargeVisibile()) {
							reversal.clickReversalRecharge();
						}

						else {
							recharges.clickRechargeHeading();
							reversal.clickReversalRecharge();
						}
						reversal.selectSearchBy(_masterVO.getProperty("C2SReversalMobileNumber")) ;
						reversal.enterSearchByValue(SubMSISDN) ;
						reversal.clickProceedButton() ;
						reversal.clickResetButton();
						
						if(!reversal.isReversalTableInvisible()) {
							currentNode.log(Status.FAIL, "Reversal table is not being reset properly") ;
							ExtentI.attachCatalinaLogs();
						}
						else if(!reversal.isInputEmpty()) {
							currentNode.log(Status.FAIL, "Input field is not being reset properly") ;
							ExtentI.attachCatalinaLogs();
						}
						else {
							ExtentI.Markup(ExtentColor.GREEN, "Reset button is working properly");
							ExtentI.attachCatalinaLogsForSuccess();
						}
					}
				}
			}
		}
		Log.methodExit(methodname);

	}
	
	
	public void performC2SReversalBlankTransactionID(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2SReversalBlankTransactionID";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		String transferID = null, transferStatus = null, trf_status = null;
		for (int i = 1; i <= totalRow; i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				if(recharges.isRechargeVisibile()) {
					reversal.clickReversalRecharge();
				}

				else {
					recharges.clickRechargeHeading();
					reversal.clickReversalRecharge();
				}
				reversal.selectSearchBy(_masterVO.getProperty("C2SReversalTransactionId"));
				reversal.clickProceedButton() ;
				String expectedErrorMessage = "Mobile number/Transaction ID is required." ;
				String actualErrorMessage = reversal.getBlankMsisdnTransactionErrorMessage() ;
				Log.info("expectedMessage : "+expectedErrorMessage);
				Log.info("actualMessage : "+actualErrorMessage);

				if (expectedErrorMessage.equals(actualErrorMessage)) {
					ExtentI.Markup(ExtentColor.GREEN, "Errors are matched");
					ExtentI.attachCatalinaLogsForSuccess();
				} else {
					currentNode.log(Status.FAIL, "Errors did not match") ;
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);
	}

	
	/**
	 * MRP block time allowed and successive block time
	 *//*
	public void modifyMRPPreference(String trueOrfalse, boolean setduration, String duration){
		Map<String,String> usermap=UserAccess.getUserWithAccess(RolesI.MODIFY_SYSTEM_PRF);
		login1.LoginAsUser(driver, usermap.get("LOGIN_ID"), usermap.get("PASSWORD"));
		String mrpprefernce = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network code"), CONSTANT.MRP_BLOCK_TIME_ALLOWED);
		String preferenceCode1 = DBHandler.AccessHandler.getNamefromSystemPreference(CONSTANT.MRP_BLOCK_TIME_ALLOWED);
		boolean updateCache = false;
		new SelectNetworkPage(driver).selectNetwork();
		suHomepage.clickPreferences();
		sysPref.clickSystemPrefernce();
		
		
		if(!mrpprefernce.toUpperCase().equals(trueOrfalse.toUpperCase())){
		sysPref.selectModule("C2S");
		sysPref.selectSystemPreference();
		sysPref.clickSubmitButton();
		sysPref.setValueofSystemPreference(preferenceCode1, trueOrfalse);
		sysPref.clickModifyBtn();
		sysPref.clickConfirmBtn();
		updateCache=true;
		}
		else{Log.info("Preference for MRP_BLOCK_TIME_ALLOWED is already set as: "+trueOrfalse);}
		if(setduration){
		String preferenceCode2 = DBHandler.AccessHandler.getNamefromSystemPreference(CONSTANT.SUCC_BLOCK_TIME);
		String valuespreftype[] = DBHandler.AccessHandler.getTypeOFPreference("", _masterVO.getMasterValue("Network Code"), CONSTANT.SUCC_BLOCK_TIME);
		suHomepage.clickPreferences();
		sysPref.clickSystemPrefernce();
		sysPref.selectModule("C2S");
		sysPref.selectPreferenceType(valuespreftype[1]);
		sysPref.clickSubmitButton();
		servPref.setValueofServicePreference(preferenceCode2, duration);
		servPref.clickModifyBtn();
		servPref.clickConfirmBtn();
		if(valuespreftype[1].equals(PretupsI.SERVICE_CLASS_PREFERENCE_TYPE)){
			Object[][] serClassIDs = DBHandler.AccessHandler.getServiceClassID("ALL","ALL");
			Map<String,String> usermapO=UserAccess.getUserWithAccess(RolesI.MODSERVICEPREF);
			login1.LoginAsUser(driver, usermapO.get("LOGIN_ID"), usermapO.get("PASSWORD"));
			for(int i=0;i<serClassIDs.length;i++){
				networkPage.selectNetwork();
				naHomepage.clickPreferences();
				naPref.clickServiceClassPreferencelink();
				naServPref.selectServiceClass(serClassIDs[i][0].toString());
				naServPref.clicksubmitBtn();
				servPref.setValueofServicePreference(preferenceCode2, duration);
				servPref.clickModifyBtn();
				servPref.clickConfirmBtn();}
		}
		updateCache = true;}
		if(updateCache){
		new UpdateCache().updateCache();}
	}
	
	public String performC2STransfer(String ParentCategory, String FromCategory,String PIN,String service, String amount, String subMSISDN) throws IOException, InterruptedException {
		String transferID = null;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login1.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		C2S Transfer Page
		CHhomePage.clickC2STransfer();
		C2STransferSubCategoriesPage.clickC2SRecharge();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch service name from Excel
		Map<String,String[]> serviceMap=new HashMap<String,String[]>();
		for(int i=1;i<=totalRow;i++){
			String []values={ExcelUtility.getCellData(0,ExcelI.NAME,i),ExcelUtility.getCellData(0,ExcelI.SELECTOR_NAME,i)};
				serviceMap.put(ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i),values);
			}

		String serviceName= serviceMap.get(service)[0];
		C2STransferPage.selectService(serviceName);
		
		C2STransferPage.enterSubMSISDN(subMSISDN);

		C2STransferPage.enterAmount(amount);
		C2STransferPage.selectSubService(serviceMap.get(service)[1]);	
			
			if(serviceName.contains("Gift")){
				C2STransferPage.enterGifterMSISDN(UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
				if(C2STransferPage.gifterNameVisibility()){
				C2STransferPage.enterGifterName(randomNum.randomAlphabets(7));}
			}

			C2STransferPage.enterPin(PIN);
			C2STransferPage.clickSubmitButton();
			String msg = new AddChannelUserDetailsPage(driver).getActualMessage(); 
			
			if(msg!=null){
			ExtentI.Markup(ExtentColor.RED, msg);
			ExtentI.attachScreenShot();}
			else{
				C2SRechargeConfirmPage.clickSubmitButton();
	
				boolean notificationMsgLink=C2SRechargeConfirmNotificationPage.notificationMsgLinkVisibility(); 
				if (notificationMsgLink==true){
					C2SRechargeConfirmNotificationPage.clickNotificationMsgLink();
				}
				String transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
				transferID = C2SRechargeConfirmNotificationPage.transferID();
				if(transferStatus.equals(successStatus)){
					ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: "+transferID+", hence Transaction Successful");
					ExtentI.attachCatalinaLogsForSuccess();}
				else 
					{currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status : "+transferStatus+" . TXN ID: "+transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
					}
				C2SRechargeConfirmPage.clickSubmitButton();
				String transferStatus;
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
				boolean notificationMsgLink=C2SRechargeConfirmNotificationPage.notificationMsgLinkVisibility(); 
				if (notificationMsgLink==true){
					//C2SRechargeConfirmNotificationPage.clickNotificationMsgLink();
					transferID = C2SRechargeConfirmNotificationPage.transferID();
					transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
					int i=0,timetowait=150,t=0;
					while(!transferStatus.equals(successStatus)){
						try {
							Thread.sleep(timetowait);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						t=timetowait+t;
						C2SRechargeConfirmNotificationPage.clickNotificationMsgLink();
						transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
						i++;
						ExtentI.Markup(ExtentColor.BLUE,"No. of times the Final notification link is clicked : "+i);
						if(i==3){Log.info("No more clicks, now leaving as the click counter reached to "+i+" | Totalwait time : "+t);break;}
					}
				}
				transferID = C2SRechargeConfirmNotificationPage.transferID();
				String trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
				transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
				
				if(transferStatus.equals(successStatus)){
					ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: "+transferID+", hence Transaction Successful");
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogsForSuccess();}
				else 
					{
					Assertion.assertFail("Transaction is not successful. Transfer Status on WEB: "+transferStatus+" | TXN ID: "+transferID+" | DB TXN Status: "+trf_status);
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();}
				
			}
			return transferID;
	}
		*/




	/* NEGATIVE FUNCTIONALITY FOR C2STRASNFERREVAMP */
	public void performC2STransferBlankAmount(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransferBlankAmount";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);


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
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}

				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);

				recharges.enterAmount("");

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
					currentNode.log(Status.FAIL, "Blank Amount Error not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}

			}
		}
		Log.methodExit(methodname);

	}


	public void performC2STransferBlankSubservice(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransferBlankSubservice";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		/*C2S Transfer Page*/


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
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}

				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);

				recharges.clickRechargeIcon();
				List<WebElement> errorMessageCaptured = recharges.blankErrorMessages();
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





	public void performC2STransferAlphaNumericAmount(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performC2STransferAlphaNumericAmount";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String, String[]> serviceMap = new HashMap<String, String[]>();

		for (int i = 1; i <= totalRow; i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
				if(recharges.isRechargeVisibile()) {
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}

				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);

				//String amount = RandomGeneration.randomAlphaNumeric(3);
				String amount = "eeee" ;
				recharges.enterAmount(amount);

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
						currentNode.log(Status.FAIL, "Alphanumeric Error didnt display on GUI or Amount accepted Alphanumeric Value");
						ExtentI.attachCatalinaLogs();
						ExtentI.attachScreenShot();
					}

				Log.methodExit(methodname);
			}
		}
	}



	public void performC2STransferNegativeAmount(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performC2STransferNegativeAmount";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		/*C2S Transfer Page*/

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String,String[]> serviceMap=new HashMap<String,String[]>();
		String transferID = null,transferStatus = null,trf_status = null;
		for(int i=1;i<=totalRow;i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
				if(recharges.isRechargeVisibile()) {
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}

				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);

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


				recharges.enterAmount("" + (-a));

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
					currentNode.log(Status.FAIL, "Negative Amount Error not displayed on GUI");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				Log.methodExit(methodname);
			}
		}
	}




	public void performC2STransferZeroAmount(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performC2STransferZeroAmount";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		/*C2S Transfer Page*/

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String,String[]> serviceMap=new HashMap<String,String[]>();
		String transferID = null,transferStatus = null,trf_status = null;
		for(int i=1;i<=totalRow;i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
				if(recharges.isRechargeVisibile()) {
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}
				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);
				//C2STransferPage.selectService(serviceName);
				String SubMSISDN = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterSubMSISDN(SubMSISDN);
				recharges.enterAmount("0000");
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				recharges.enterPin(PIN);

				recharges.clickRechargeButton();
				String expectedMessage = "ZERO" ;
				String actualMessage = null ;

				boolean flag = false;
				boolean successPopUP = recharges.failedPopUPVisibility();
				if (successPopUP == true) {
					transferStatus = recharges.transferStatusFailed();    //recharge failed
					if (transferStatus.toUpperCase().contains("FAIL")) {
						flag = true ;

					} else {
						flag = false ;
					}
				}

				if(flag)
				{
					actualMessage = recharges.getInvalidTextMessage().toUpperCase() ;
					Assertion.assertContainsEquals(expectedMessage, actualMessage);
					recharges.clicktryAgainForFailRecharges() ;
					ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with ZERO Amount");
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				} else {
					currentNode.log(Status.FAIL, "Transaction went ahead with Zero amount") ;
					/*ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);*/
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}


			}
		}
		Log.methodExit(methodname);
	}




	public void performC2STransferBlankPIN(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performC2STransferBlankPIN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		Map<String,String[]> serviceMap=new HashMap<String,String[]>();
		String transferID = null,transferStatus = null,trf_status = null;
		for(int i=1;i<=totalRow;i++)
		{
			String []values={ExcelUtility.getCellData(0,ExcelI.NAME,i),ExcelUtility.getCellData(0,ExcelI.SELECTOR_NAME,i)};
			if(ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i),values);
				if(recharges.isRechargeVisibile()) {
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}
				String serviceName= serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);
				String SubMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterSubMSISDN(SubMSISDN);
				double b=recharges.getCurrentBalance();
				int a= (int) (b*0.05);
				int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
				int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
				if(minCardSlab < a && a < midCardSlab);
				else if(a > midCardSlab){a = midCardSlab;}
				else if(a < minCardSlab){a = minCardSlab+1;}
				recharges.enterAmount(""+a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				recharges.enterPin("");
				recharges.clickRechargeButton();
				String  successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
				boolean rechargeButtonAvailable=recharges.checkRechargeButtonIsClickable();	//false

				if (!rechargeButtonAvailable){
					ExtentI.Markup(ExtentColor.GREEN, "RECHARGE BUTTON DISABLED WHEN BLANK PIN PROVIDED");
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				}
				else if(rechargeButtonAvailable)
				{
					currentNode.log(Status.FAIL, "RECHARGE BUTTON AVAILABLE AFTER BLANK PIN PROVIDED");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				recharges.clickCloseEnterPINPopup();
			}
		}
		Log.methodExit(methodname);

	}



	public void performC2STransferCloseEnterPINPopup(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performC2STransferCloseEnterPINPopup";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		Map<String,String[]> serviceMap=new HashMap<String,String[]>();
		String transferID = null,transferStatus = null,trf_status = null;
		for(int i=1;i<=totalRow;i++)
		{
			String []values={ExcelUtility.getCellData(0,ExcelI.NAME,i),ExcelUtility.getCellData(0,ExcelI.SELECTOR_NAME,i)};
			if(ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i).equals(service)) {		//equals c2s
				serviceMap.put(ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i),values);
				if(recharges.isRechargeVisibile()) {
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}
				String serviceName= serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);
				String SubMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
				recharges.enterSubMSISDN(SubMSISDN);
				double b=recharges.getCurrentBalance();
				int a= (int) (b*0.05);
				int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
				int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
				if(minCardSlab < a && a < midCardSlab);
				else if(a > midCardSlab){a = midCardSlab;}
				else if(a < minCardSlab){a = minCardSlab+1;}
				recharges.enterAmount(""+a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				recharges.clickCloseEnterPINPopup();

				boolean successPopUP=recharges.RechargeIconVisibility();
				if (successPopUP==true){
					ExtentI.Markup(ExtentColor.GREEN, "Enter PIN Popup Closed Successfully") ;
					ExtentI.attachCatalinaLogsForSuccess();

				}
				else
				{
					currentNode.log(Status.FAIL, "Enter PIN Popup didnt close") ;
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);
	}




	public void performC2STransferInvalidPIN(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performC2STransferInvalidPIN";
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
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
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
				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
				recharges.clickRechargeIcon();
				PIN = RandomGeneration.randomDecimalNumer(1,2);
				recharges.enterPin(PIN);
				Log.info("PIN Entered : "+PIN) ;
				recharges.clickRechargeButton();
				String expectedMessage = "Invalid PIN" ;
				String actualMessage = null ;

				boolean flag = false;
				boolean successPopUP = recharges.failedPopUPVisibility();
				if (successPopUP == true) {
					transferStatus = recharges.transferStatusFailed();    //recharge failed
					if (transferStatus.toUpperCase().contains("FAIL")) {
						flag = true ;

					} else {
						flag = false ;
					}
				}

				if(flag)
				{
					actualMessage = recharges.getInvalidTextMessage() ;
					Assertion.assertContainsEquals(actualMessage, expectedMessage);
					recharges.clicktryAgainForFailRecharges() ;
					ExtentI.Markup(ExtentColor.GREEN, "Transaction Not Successful with Invalid(decimal digit) PIN");
					ExtentI.attachCatalinaLogsForSuccess();
					ExtentI.attachScreenShot();
				} else {
					currentNode.log(Status.FAIL, "Transaction went ahead with Invalid(decimal digit) PIN") ;
						/*ExtentI.getChannelRequestDailyLogs(transferID);
						ExtentI.getOneLineTXNLogsC2S(transferID);*/
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();

				}
			}
		}
		Log.methodExit(methodname);
	}



	public void performC2STransferAlphanumericPIN(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performC2STransferAlphanumericPIN";
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
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
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
				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
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





	/* --------		YASH 		----- */


	public void performC2STransferBlankMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransferBlankMsisdn";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/
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
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}


				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);
				recharges.enterSubMSISDN("");
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
				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
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
					currentNode.log(Status.FAIL, "Blank MSISDN error not shown");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);
	}

	public void performC2STransferInvalidMsisdn(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransferInvalidMSISDN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/
		if(recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			recharges.clickPrepaidRecharge();
		}
		
		else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			recharges.clickPrepaidRecharge();
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		String transferID = null, transferStatus = null, trf_status = null;
		for (int i = 1; i <= totalRow; i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if(ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);

				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);
				String SubMSISDN = null;
				SubMSISDN = RandomGeneration.randomNumeric(5);
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
				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
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
					currentNode.log(Status.FAIL, "Invalid MSISDN error not shown");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);
	}


	public void performC2STransferAlphaNumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransferAlphaNumericMSISDN";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/
		if(recharges.isRechargeVisibile()) {
			recharges.clickRecharge();
			recharges.clickPrepaidRecharge();
		}
		
		else {
			recharges.clickRechargeHeading();
			recharges.clickRecharge();
			recharges.clickPrepaidRecharge();
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		String transferID = null, transferStatus = null, trf_status = null;
		for (int i = 1; i <= totalRow; i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
		}
		String serviceName = serviceMap.get(service)[0];
		Log.info("Service name is =" + serviceName);

		recharges.enterSubMSISDN("eee");
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

		recharges.enterAmount("" + a);
		recharges.selectSubService(serviceMap.get(service)[1]);
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
					currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: " + transferStatus + "  | DB TXN Status: " + trf_status);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}

		Log.methodExit(methodname);
	}

	public void performC2STransferReset(String ParentCategory, String FromCategory, String PIN, String service) {
		final String methodname = "performC2STransferResetButton";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String,String[]> serviceMap=new HashMap<String,String[]>();
		String transferID = null,transferStatus = null,trf_status = null;
		for(int i=1;i<=totalRow;i++) {
			String[] values = {ExcelUtility.getCellData(0, ExcelI.NAME, i), ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i)};
			if (ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i).equals(service)) {
				serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
				if(recharges.isRechargeVisibile()) {
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
				}

				String serviceName = serviceMap.get(service)[0];
				Log.info("Service name is =" + serviceName);
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
				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
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
					currentNode.log(Status.FAIL, "Fields are not blank hence Reset button failed.");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);
	}


	public void performC2STransferCopyButton(String ParentCategory, String FromCategory, String PIN, String service)/* throws IOException, UnsupportedFlavorException*/ {
		final String methodname = "performC2STransferCopyButton";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/
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
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
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

				recharges.enterAmount("" + a);
				recharges.selectSubService(serviceMap.get(service)[1]);
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
					currentNode.log(Status.FAIL, "Transaction ID Copy failed");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
				recharges.clickRechargeDone() ;
			}
		}
		Log.methodExit(methodname);
	}


	public void performC2STransferPrintButton(String ParentCategory, String FromCategory, String PIN, String service)/* throws IOException, UnsupportedFlavorException*/ {
		final String methodname = "performC2STransferPrintButton";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
		/*C2S Transfer Page*/
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
					recharges.clickPrepaidRecharge();
				}
				
				else {
					recharges.clickRechargeHeading();
					recharges.clickRecharge();
					recharges.clickPrepaidRecharge();
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

				recharges.enterAmount("" + a);
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
					currentNode.log(Status.FAIL, "Print click failed");
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
				}
			}
		}
		Log.methodExit(methodname);
	}





}
	

