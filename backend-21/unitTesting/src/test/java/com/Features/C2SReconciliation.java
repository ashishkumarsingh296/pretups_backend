package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.ReconciliationSubCategories;
import com.pageobjects.networkadminpages.reconciliation.C2Sreconciliationpage1;
import com.pageobjects.networkadminpages.reconciliation.C2Sreconciliationpage2;
import com.pageobjects.networkadminpages.reconciliation.C2Sreconciliationpage3;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.BTSLDateUtil;
import com.utils._masterVO;

public class C2SReconciliation {

	public WebDriver driver;
	String MasterSheetPath;
	NetworkAdminHomePage homePage;
	Login login;
	C2Sreconciliationpage1 c2Sreconciliationpage1;
	C2Sreconciliationpage2 c2Sreconciliationpage2;
	C2Sreconciliationpage3 c2Sreconciliationpage3;
	String[] result;
	SelectNetworkPage selectNetworkPage;
	Map<String, String> userAccessMap;
	NetworkAdminHomePage networkAdminHomePage;
	ReconciliationSubCategories reconciliationSubCategories;

	public C2SReconciliation(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		c2Sreconciliationpage1 = new C2Sreconciliationpage1(driver);
		c2Sreconciliationpage2 = new C2Sreconciliationpage2(driver);
		c2Sreconciliationpage3 = new C2Sreconciliationpage3(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
		userAccessMap = new HashMap<String, String>();
		networkAdminHomePage = new NetworkAdminHomePage(driver);
		reconciliationSubCategories = new ReconciliationSubCategories(driver);
	}

	public String c2SReconciliationlink(String serviceType) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -29);
		String toDate = currDate;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.SelectserviceType(serviceType);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		
		String uiMessage = c2Sreconciliationpage1.getActualMsg();
		return uiMessage;

	}

	public String c2SReconciliationlink_fromDateNull() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String fromDate = "";
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = c2Sreconciliationpage1.getActualMsg();
		return uiMessage;

	}

	public String c2SReconciliationlink_toDateNull() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -29);
		String toDate = "";
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = c2Sreconciliationpage1.getActualMsg();
		return uiMessage;

	}

	public String c2SReconciliationlink_serviceNameNull() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -29);
		String toDate = currDate;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = c2Sreconciliationpage1.getActualMsg();
		return uiMessage;

	}

	public String c2SReconciliationlink_dateDiffMoreThan30Days(String serviceType) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -40);
		String toDate = currDate;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.SelectserviceType(serviceType);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = c2Sreconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String c2SReconciliationlink_invalidFromDateFormat() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String fromDate = null;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = c2Sreconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String c2SReconciliationlink_invalidToDateFormat() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -40);
		String toDate = null;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = c2Sreconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String c2SReconciliationlink_fromDateGreaterThanCurrDate() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 40);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 40);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = c2Sreconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String c2SReconciliationlink_toDateGreaterThanCurrDate() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -2);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 40);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = c2Sreconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String c2SReconciliationlink_toDateLessThanForDate() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -23);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = c2Sreconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public boolean[] c2SReconciliationlink_checkAmbigious(String serviceType, String validationValue, String selectorType) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		boolean[] result = new boolean[2];
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate,-30);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.SelectserviceType(serviceType);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		boolean dBResult = DBHandler.AccessHandler.checkAmbiguousTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate), selectorType);
		boolean uiResult = c2Sreconciliationpage2.checkTransaction(validationValue);	
		result[0] = !dBResult;
		result[1] = uiResult;
		return result;
	}
	
	//Success
	public String c2SReconciliationlink_Success(String serviceType, String selectorType) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -30);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.SelectserviceType(serviceType);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		boolean dBResult = DBHandler.AccessHandler.checkAmbiguousTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate), selectorType);
		String uiMessage = null;
		if(!dBResult){
		String transactionID = DBHandler.AccessHandler.fetchAmbiguousTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate), selectorType);
		String lastWord = c2Sreconciliationpage2.getTotalEnteries();
		int enteries = Integer.parseInt(lastWord);
		int enteriesPerPage = Integer.parseInt(_masterVO.getProperty("enteriesPerPage"));
		int totalPages = (int) Math.ceil(enteries/enteriesPerPage);
		boolean isNextExists=c2Sreconciliationpage2.isNextDisplayed();
		
		while(isNextExists == true)
		{
			c2Sreconciliationpage2.getLastPage(enteriesPerPage);
			isNextExists = c2Sreconciliationpage2.isNextDisplayed();
		}
		c2Sreconciliationpage2.clickonradioButton(transactionID);
		c2Sreconciliationpage2.ClickOnbtnSubmit();
		c2Sreconciliationpage3.ClickOnbtnSubmit();
		driver.switchTo().alert().accept();
	    uiMessage  = c2Sreconciliationpage1.getActualMsg();
		}
		else{
		uiMessage = c2Sreconciliationpage1.getnoAmbiguiousMessage();
		}
		
		return uiMessage;

	}
	
	//DB_Verification
	public String c2SReconciliationlink_dbVerification(String serviceType, String selectorType) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String result = null;
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -30);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickC2SReconciliation();
		c2Sreconciliationpage1.EnterfromDate(fromDate);
		c2Sreconciliationpage1.EntertoDate(toDate);
		c2Sreconciliationpage1.SelectserviceType(serviceType);
		c2Sreconciliationpage1.ClickOnbtnSubmit();
		String transferStatus = null;
		boolean dBResult = DBHandler.AccessHandler.checkAmbiguousTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate), selectorType);
		if(!dBResult){
			String transactionID = DBHandler.AccessHandler.fetchAmbiguousTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate), selectorType);
			String lastWord = c2Sreconciliationpage2.getTotalEnteries();
			int enteries = Integer.parseInt(lastWord);
			int enteriesPerPage = Integer.parseInt(_masterVO.getProperty("enteriesPerPage"));
			int totalPages = (int) Math.ceil(enteries/enteriesPerPage);
			boolean isNextExists=c2Sreconciliationpage2.isNextDisplayed();
			
			while(isNextExists == true)
			{
				c2Sreconciliationpage2.getLastPage(enteriesPerPage);
				isNextExists = c2Sreconciliationpage2.isNextDisplayed();
			}
			c2Sreconciliationpage2.clickonradioButton(transactionID);
			c2Sreconciliationpage2.ClickOnbtnSubmit();
			c2Sreconciliationpage3.ClickOnbtnSubmit();
			driver.switchTo().alert().accept();
			/*if(c2Sreconciliationpage3.getActualMsg()!=null)
			{
				result = c2Sreconciliationpage3.getActualMsg();
			}
			else
			{*/
			Thread.sleep(2000);
		    transferStatus = DBHandler.AccessHandler.fetchTransferStatus(transactionID);
		    result = transferStatus;
			//}
			}
		else{
			result = c2Sreconciliationpage1.getnoAmbiguiousMessage();
		}
		return result;
		
	}
	
	//Fail
		public String c2SReconciliationlink_Fail(String serviceType, String selectorType) {

			userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_RECONCILIATION);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			// User Access module ends.
			String currDate = networkAdminHomePage.getDate();
			String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -30);
			String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickReconciliation();
			reconciliationSubCategories.clickC2SReconciliation();
			c2Sreconciliationpage1.EnterfromDate(fromDate);
			c2Sreconciliationpage1.EntertoDate(toDate);
			c2Sreconciliationpage1.SelectserviceType(serviceType);
			c2Sreconciliationpage1.ClickOnbtnSubmit();
			boolean dBResult = DBHandler.AccessHandler.checkAmbiguousTransactions(fromDate, toDate, selectorType);
			String uiMessage = null;
			if(!dBResult){
			String transactionID = DBHandler.AccessHandler.fetchAmbiguousTransactions(fromDate, toDate, selectorType);
			c2Sreconciliationpage2.clickonradioButton(transactionID);
			c2Sreconciliationpage2.ClickOnbtnSubmit();
			c2Sreconciliationpage3.ClickOnbtnFail();
			driver.switchTo().alert().accept();
		    uiMessage  = c2Sreconciliationpage1.getActualMsg();
			}
			else{
			uiMessage = c2Sreconciliationpage1.getnoAmbiguiousMessage();
			}
			
			return uiMessage;

		}


}
