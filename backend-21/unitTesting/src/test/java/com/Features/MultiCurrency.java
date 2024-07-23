package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.networkadminpages.homepage.MultiCurrencySubCategories;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.multicurency.AddCurrencyConfirmationPage;
import com.pageobjects.networkadminpages.multicurency.AddCurrencyPage;
import com.pageobjects.networkadminpages.multicurency.CurrencyApprovalLevelOnePage_1;
import com.pageobjects.networkadminpages.multicurency.CurrencyApprovalLevelOnePage_2;
import com.pageobjects.networkadminpages.multicurency.CurrencyApprovalLevelTwoPage_1;
import com.pageobjects.networkadminpages.multicurency.CurrencyApprovalLevelTwoPage_2;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Assertion;
import com.utils.Log;
import com.utils.RandomGeneration;

public class MultiCurrency extends BaseTest{
	
	WebDriver driver = null;
	Login login;
	NetworkAdminHomePage homePage;
	SelectNetworkPage networkPage;
	MultiCurrencySubCategories multiCurrencySubCategories;
	AddCurrencyPage addCurrencyPage;
	AddCurrencyConfirmationPage addCurrencyConfiramtionPage;
	CurrencyApprovalLevelOnePage_1 currencyApprovalLevelOnePage_1;
	CurrencyApprovalLevelOnePage_2 currencyApprovalLevelOnePage_2;
	CurrencyApprovalLevelTwoPage_1 currencyApprovalLevelTwoPage_1;
	CurrencyApprovalLevelTwoPage_2 currencyApprovalLevelTwoPage_2;
	
	
	
	Map<String, String> userAccessMap = new HashMap<String, String>();
	
	public MultiCurrency(WebDriver driver) {
		this.driver = driver;
		
		
		//Page Initialization
		login = new Login();
		homePage = new NetworkAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		multiCurrencySubCategories = new MultiCurrencySubCategories(driver);
		addCurrencyPage = new AddCurrencyPage(driver);
		addCurrencyConfiramtionPage = new AddCurrencyConfirmationPage(driver);
		
		currencyApprovalLevelOnePage_1 = new CurrencyApprovalLevelOnePage_1(driver);
		currencyApprovalLevelOnePage_2 = new CurrencyApprovalLevelOnePage_2(driver);
		
		currencyApprovalLevelTwoPage_1 = new CurrencyApprovalLevelTwoPage_1(driver);
		currencyApprovalLevelTwoPage_2 = new CurrencyApprovalLevelTwoPage_2(driver);
		
	}
	
	/**
	 * Initiate Add Currency
	 * @param currency
	 */
	
	public String addCurrency(String currency) {
		final String methodname = "addCurrency";
		String message = null;
		Log.methodEntry(methodname, currency);
		
		Log.info("Adding Currency with Currency Code " + currency);
		
		//Operator User Access
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MULTI_CURRENCY);

		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends
		
		networkPage.selectNetwork();
		homePage.clickMultiCurrency();
		multiCurrencySubCategories.clickAddCurrency();
		addCurrencyPage.selectCurrencyCode(currency);
		addCurrencyPage.enterCurrencyName("Automation Test");
		addCurrencyPage.enterConversion(RandomGeneration.randomDecimalNumer(2, 1));
		addCurrencyPage.enterDescription("Automation");
		addCurrencyPage.clickSubmit();
		addCurrencyConfiramtionPage.clickSubmit();
		
		try {
			message = addCurrencyPage.getSuccessMessage();
		}catch(Exception ex) {
			ex.getMessage();
		}
		try {
			message = addCurrencyPage.getErrorMessage();
		}catch(Exception ex) {
			ex.getMessage();
		}
		
		

		Log.methodExit(methodname);
		return message;
	}
	
	/**
	 * Level 1 approval for added currency
	 */
	
	public String approveMultiCurrencyLevel1() {
		final String methodname = "approveMultiCurrencyLevel1";
		Log.methodEntry(methodname);
		
		Log.info("Network admin trying to perform level 1 approval");
		
		// Operator user access
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MULTI_CURRENCY);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends
		
		networkPage.selectNetwork();
		homePage.clickMultiCurrency();
		multiCurrencySubCategories.clickMultiCurrencyApproval1();
		currencyApprovalLevelOnePage_1.selectCurrencyForApproval();
		currencyApprovalLevelOnePage_1.clickApprove();
		currencyApprovalLevelOnePage_2.clickSubmit();
		currencyApprovalLevelOnePage_2.acceptAlert();
		String successMessage = currencyApprovalLevelOnePage_1.getSuccessMeassage();
		
		
		Log.methodExit(methodname);
		return successMessage;
	}
	
	/**
	 * Level 2 approval for added currency
	 */
	
	public String approveMultiCurrencyLevel2() {
		final String methodname = "approveMultiCurrencyLevel1";
		Log.methodEntry(methodname);
		
		Log.info("Network admin trying to perform level 2 approval");
		
		// Operator user access
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MULTI_CURRENCY);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends
		
		networkPage.selectNetwork();
		homePage.clickMultiCurrency();
		multiCurrencySubCategories.clickMultiCurrencyApproval2();
		currencyApprovalLevelTwoPage_1.selectCurrencyForApproval();
		currencyApprovalLevelTwoPage_1.clickApprove();
		currencyApprovalLevelTwoPage_2.clickSubmit();
		currencyApprovalLevelTwoPage_2.acceptAlert();
		String successMessage = currencyApprovalLevelTwoPage_1.getSuccessMeassage();
		

		Log.methodExit(methodname);
		return successMessage;
	}
	
	public String addCurrencyWithInvalidConversionRate(String conversionRate) {
		final String methodname  = "addCurrencyWithInvalidData";
		String errorMessage = null;
		Log.methodEntry(methodname, conversionRate);
		
		Log.info("Trying to verify if currencyaddition fails for invalid conversionRate");

		// Operator user access
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MULTI_CURRENCY);
		
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends
		
		networkPage.selectNetwork();
		homePage.clickMultiCurrency();
		multiCurrencySubCategories.clickAddCurrency();
		addCurrencyPage.selectCurrencyCode();
		addCurrencyPage.enterCurrencyName("Automation test");
		addCurrencyPage.enterConversion(conversionRate);
		addCurrencyPage.enterDescription("Automation");
		addCurrencyPage.clickSubmit();
		errorMessage = addCurrencyPage.getErrorMessage();


		
		return errorMessage;
	}
	
	public String addMulticurrencywithBlankCurrencyCode() {
		final String methodname  = "addCurrencyWithInvalidData";
		String errorMessage = null;
		Log.methodEntry(methodname);
		
		Log.info("Trying to verify if currencyaddition fails for invalid conversionRate");

		// Operator user access
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MULTI_CURRENCY);
		
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends
		
		networkPage.selectNetwork();
		homePage.clickMultiCurrency();
		multiCurrencySubCategories.clickAddCurrency();

		addCurrencyPage.enterCurrencyName("Automation test");
		addCurrencyPage.enterConversion(RandomGeneration.randomDecimalNumer(2, 1));
		addCurrencyPage.enterDescription("Automation");
		addCurrencyPage.clickSubmit();
		errorMessage = addCurrencyPage.getErrorMessage();

		
		return errorMessage;
	}
	
	public String addMulticurrencywithBlankCurrencyName() {
		final String methodname  = "addCurrencyWithInvalidData";
		Log.methodEntry(methodname);
		
		Log.info("Trying to verify if currencyaddition fails for invalid conversionRate");

		// Operator user access
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MULTI_CURRENCY);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends
		
		networkPage.selectNetwork();
		homePage.clickMultiCurrency();
		multiCurrencySubCategories.clickAddCurrency();
		addCurrencyPage.selectCurrencyCode();
		addCurrencyPage.enterConversion(RandomGeneration.randomDecimalNumer(2, 1));
		addCurrencyPage.enterDescription("Automation");
		addCurrencyPage.clickSubmit();
		String errorMessage = addCurrencyPage.getErrorMessage();
		
		return errorMessage;
	}
	
	// Add dummy currency to test validations and currecny rejection
	
	public String addDummyCurrency() {
		final String methodname = "addDummyCurrency";
		String message = null;
		Log.methodEntry(methodname);
		
		Log.info("Adding Dummy Currency");
		//Operator User Access
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MULTI_CURRENCY);
		
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends
		
		networkPage.selectNetwork();
		homePage.clickMultiCurrency();
		multiCurrencySubCategories.clickAddCurrency();
		addCurrencyPage.selectCurrencyCode();
		addCurrencyPage.enterCurrencyName("Automation Test");
		addCurrencyPage.enterConversion(RandomGeneration.randomDecimalNumer(2, 1));
		addCurrencyPage.enterDescription("Automation");
		addCurrencyPage.clickSubmit();
		addCurrencyConfiramtionPage.clickSubmit();
		
		try {
			message = addCurrencyPage.getSuccessMessage();
		}catch(Exception ex) {
			ex.getMessage();
		}
		try {
			message = addCurrencyPage.getErrorMessage();
		}catch(Exception ex) {
			ex.getMessage();
		}
		

		
		
		Log.methodExit(methodname);
		return message;
	}
	
	public String rejectCurrencyLevel1() {
		
		final String methodname = "rejectCurrencyLevel1";
		String successMessage = null;
		Log.methodEntry(methodname);
		
		String addCurrencyMsg = addDummyCurrency();
		if(addCurrencyMsg.equals(MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.approvalpending"))) {
			return addCurrencyMsg;
		}
		multiCurrencySubCategories.clickMultiCurrencyApproval1();
		currencyApprovalLevelOnePage_1.selectCurrencyForApproval();
		currencyApprovalLevelOnePage_1.clickReject();
		currencyApprovalLevelOnePage_1.acceptAlert();
		successMessage = currencyApprovalLevelOnePage_1.getSuccessMeassage();
		
		Log.methodExit(methodname);
		return successMessage;
	}
	
	public String rejectCurrencyLevel2() {
		
		final String methodname = "rejectCurrencyLevel2";
		Log.methodEntry(methodname);
		
		String addCurrencyMsg = addDummyCurrency();
		if(addCurrencyMsg.equals(MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.approvalpending"))) {
			return addCurrencyMsg;
		}
		approveMultiCurrencyLevel1();
		multiCurrencySubCategories.clickMultiCurrencyApproval2();
		currencyApprovalLevelTwoPage_1.selectCurrencyForApproval();
		currencyApprovalLevelTwoPage_1.clickReject();
		currencyApprovalLevelTwoPage_1.acceptAlert();
		String successMessage = currencyApprovalLevelTwoPage_1.getSuccessMeassage();
		
		Log.methodExit(methodname);
		return successMessage;
	}
	
	public String approvalFailLevel1() {
		
		final String methodname = "approvalFailLevel1";
		Log.methodEntry(methodname);
		
		// add dummy currency to approve 
		String addCurrencyMsg = addDummyCurrency();
		if(addCurrencyMsg.equals(MessagesDAO.prepareMessageByKey("currencyconversion.addcurrency.err.msg.approvalpending"))) {
			return addCurrencyMsg;
		}
		
		Log.info("Network admin trying to perform level 1 approval");

		
		multiCurrencySubCategories.clickMultiCurrencyApproval1();
		currencyApprovalLevelOnePage_1.selectCurrencyForApproval();
		currencyApprovalLevelOnePage_1.clickApprove();
		currencyApprovalLevelOnePage_2.enterConversion("Automated test");
		currencyApprovalLevelOnePage_2.clickSubmit();
		currencyApprovalLevelOnePage_2.acceptAlert();
		String errorMessage = currencyApprovalLevelOnePage_1.getErrorMeassage();

		
		Log.methodExit(methodname);
		return errorMessage;
		
	}
	
	public String approvalFailLevel2() {
		
		final String methodname = "approvalFailLevel2";
		Log.methodEntry(methodname);
		
		Log.info("Approving currency for level 2 validation");
		currencyApprovalLevelOnePage_1.clickApprove();
		currencyApprovalLevelOnePage_2.clickSubmit();
		currencyApprovalLevelOnePage_2.acceptAlert();
		
		Log.info("Network admin trying to perform level 2 approval");

		
		multiCurrencySubCategories.clickMultiCurrencyApproval2();
		currencyApprovalLevelTwoPage_1.selectCurrencyForApproval();
		currencyApprovalLevelTwoPage_1.clickApprove();
		currencyApprovalLevelTwoPage_2.enterConversion("Automated test");
		currencyApprovalLevelTwoPage_2.clickSubmit();
		currencyApprovalLevelTwoPage_2.acceptAlert();
		String errorMessage = currencyApprovalLevelOnePage_1.getErrorMeassage();
		currencyApprovalLevelTwoPage_1.clickReject();
		currencyApprovalLevelTwoPage_1.acceptAlert();
		
		Log.methodExit(methodname);
		return errorMessage;
		
	}
	
	
}
