package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.o2cwithdraw.O2CWithdrawPage1;
import com.pageobjects.channeladminpages.o2cwithdraw.O2CWithdrawPage2;
import com.pageobjects.channeladminpages.o2cwithdraw.O2CWithdrawPage3;
import com.pageobjects.channeladminpages.o2cwithdraw.O2CWithdrawPage4;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils._masterVO;
import com.utils._parser;

public class O2CWithdraw extends BaseTest {
	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	O2CWithdrawPage1 o2cWithdrawPage1;
	O2CWithdrawPage2 o2cWithdrawPage2;
	O2CWithdrawPage3 o2cWithdrawPage3;
	O2CWithdrawPage4 o2cWithdrawPage4;
	SelectNetworkPage ntwrkPage;
	Map<String, String> userAccessMap;

	public O2CWithdraw(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		o2cWithdrawPage1 = new O2CWithdrawPage1(driver);
		o2cWithdrawPage2 = new O2CWithdrawPage2(driver);
		o2cWithdrawPage3 = new O2CWithdrawPage3(driver);
		o2cWithdrawPage4 = new O2CWithdrawPage4(driver);
		userAccessMap = new HashMap<String, String>();
		ntwrkPage = new SelectNetworkPage(driver);

	}

	public HashMap<String, String> o2cWithdraw2qty(String MobileNumber, String ProductType,String productName) {
		
		HashMap<String, String> transactionMap = new HashMap<String, String>();
		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_WITHDRAWAL_ROLECODE); // Getting User with Access to Add
																						// Channel Users
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickWithdrawal();
		caHomepage.clickWithdraw();

		o2cWithdrawPage1.enterMobileNumber(MobileNumber);
		boolean selectDropdownVisible = o2cWithdrawPage1.isSelectProductTypeVisible() ;
		if(selectDropdownVisible==true) {
			o2cWithdrawPage1.selectProductType(ProductType);
		}
		
		o2cWithdrawPage1.clickSubmitBtn();

		/*
		 * Multiple Wallet preference
		 */

		String MultiWalletPreferenceValue = DBHandler.AccessHandler
				.getSystemPreference(CONSTANT.MULTIWALLET_SYSTEM_STATUS).toUpperCase();
		if (MultiWalletPreferenceValue.equals("TRUE")) {
			o2cWithdrawPage2.selectWalletType(PretupsI.SALE_WALLET_LOOKUP);
			o2cWithdrawPage2.clickSubmitBtn();
		}
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		/*
		 * Quantity for O2CWithdraw.
		 */
		transactionMap.put("InitiatedQty", "" + o2cWithdrawPage3.enterQuantityforO2CWithdraw1(productName));
		o2cWithdrawPage3.enterRemarks("Automation :O2C Withdraw.");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCnt = ExcelUtility.getRowCount();
		String PIN = null;

		/*
		 * Preference to check if pin need to be entered for O2C Withdraw
		 */
		String pinAllowed = DBHandler.AccessHandler.pinPreferenceForTXN(userAccessMap.get("CATEGORY_NAME"));

		if (pinAllowed.equals("Y")) {
			for (int x = 1; x <= rowCnt; x++) {
				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, x).equals(userAccessMap.get("CATEGORY_NAME"))) {
					PIN = ExcelUtility.getCellData(0, ExcelI.PIN, x);
					break;
				}
			}
			o2cWithdrawPage3.enterPIN(PIN);
		}
		o2cWithdrawPage3.clickSubmitBtn();
		o2cWithdrawPage4.clickConfirmButton();
		
		String message = o2cWithdrawPage1.getMessage();
		
		transactionMap.put("Message", message);
		transactionMap.put("TransactionID", _parser.getTransactionID(message, PretupsI.CHANNEL_WITHDRAW_O2C_ID));
		
		return transactionMap;
	}
	
public HashMap<String, String> o2cWithdraw(String MobileNumber, String ProductType, String amount) {
		
		HashMap<String, String> transactionMap = new HashMap<String, String>();
		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_WITHDRAWAL_ROLECODE); // Getting User with Access to Add
																						// Channel Users
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		ntwrkPage.selectNetwork();
		caHomepage.clickWithdrawal();
		caHomepage.clickWithdraw();

		o2cWithdrawPage1.enterMobileNumber(MobileNumber);
		o2cWithdrawPage1.selectProductType(ProductType);
		o2cWithdrawPage1.clickSubmitBtn();

		/*
		 * Multiple Wallet preference
		 */
		String MultiWalletPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.MULTIWALLET_SYSTEM_STATUS).toUpperCase();
		if (MultiWalletPreferenceValue.equals("TRUE")) {
			o2cWithdrawPage2.selectWalletType(PretupsI.SALE_WALLET_LOOKUP);
			o2cWithdrawPage2.clickSubmitBtn();
		}

		o2cWithdrawPage3.enterQuantityforO2CWithdraw(amount);
		transactionMap.put("InitiatedQty",amount);
		o2cWithdrawPage3.enterRemarks("Automation :O2C Withdraw.");
		
		String PIN = null;

		String pinAllowed = DBHandler.AccessHandler.pinPreferenceForTXN(userAccessMap.get("CATEGORY_NAME"));
		if (pinAllowed.equals("Y")) {
			int rowNum = ExtentI.combinationExistAtRow(new String[]{ExcelI.PARENT_NAME,ExcelI.CATEGORY_NAME},new String[]{userAccessMap.get("PARENT_NAME"),userAccessMap.get("CATEGORY_NAME")} ,ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			PIN = ExtentI.fetchValuefromDataProviderSheet(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PIN, rowNum);
			o2cWithdrawPage3.enterPIN(PIN);
		}
		o2cWithdrawPage3.clickSubmitBtn();
		o2cWithdrawPage4.clickConfirmButton();
		
		String message = new AddChannelUserDetailsPage(driver).getActualMessage();
		
		transactionMap.put("Message", message);
		transactionMap.put("TransactionID", _parser.getTransactionID(message, PretupsI.CHANNEL_WITHDRAW_O2C_ID));
		
		return transactionMap;
	}
}
