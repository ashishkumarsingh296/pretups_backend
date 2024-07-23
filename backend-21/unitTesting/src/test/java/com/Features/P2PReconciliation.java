package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.ReconciliationSubCategories;
import com.pageobjects.networkadminpages.reconciliation.P2Preconciliationpage1;
import com.pageobjects.networkadminpages.reconciliation.P2Preconciliationpage2;
import com.pageobjects.networkadminpages.reconciliation.P2Preconciliationpage3;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

public class P2PReconciliation {
	
	public WebDriver driver;
	String MasterSheetPath;
	NetworkAdminHomePage homePage;
	Login login;
	P2Preconciliationpage1 p2Preconciliationpage1;
	P2Preconciliationpage2 p2Preconciliationpage2;
	P2Preconciliationpage3 p2Preconciliationpage3;
	String[] result;
	SelectNetworkPage selectNetworkPage;
	Map<String, String> userAccessMap;
	NetworkAdminHomePage networkAdminHomePage;
	ReconciliationSubCategories reconciliationSubCategories;
	
	public P2PReconciliation(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		
		p2Preconciliationpage1 = new P2Preconciliationpage1(driver);
		p2Preconciliationpage2 = new P2Preconciliationpage2(driver);
		p2Preconciliationpage3 = new P2Preconciliationpage3(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
		userAccessMap = new HashMap<String, String>();
		networkAdminHomePage = new NetworkAdminHomePage(driver);
		reconciliationSubCategories = new ReconciliationSubCategories(driver);
	}
	
	
	public String p2PreconciliationLink(String serviceType) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -29);
		String toDate = currDate;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.EntertoDate(toDate);
		p2Preconciliationpage1.SelectserviceType(serviceType);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = p2Preconciliationpage1.getActualMsg();
		return uiMessage;
	}
	
	public String p2PReconciliationlink_fromDateNull() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String fromDate = "";
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = p2Preconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String p2PReconciliationlink_toDateNull() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -29);
		String toDate = "";
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.EntertoDate(toDate);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = p2Preconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String p2PReconciliationlink_serviceNameNull() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -29);
		String toDate = currDate;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.EntertoDate(toDate);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = p2Preconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String p2PReconciliationlink_dateDiffMoreThan30Days(String serviceType) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -40);
		String toDate = currDate;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.EntertoDate(toDate);
		p2Preconciliationpage1.SelectserviceType(serviceType);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = p2Preconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String p2PReconciliationlink_invalidFromDateFormat() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String fromDate = null;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = p2Preconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String p2PReconciliationlink_invalidToDateFormat() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -40);
		String toDate = null;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.EntertoDate(toDate);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = p2Preconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String p2PReconciliationlink_fromDateGreaterThanCurrDate() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 40);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 40);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.EntertoDate(toDate);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = p2Preconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String p2PReconciliationlink_toDateGreaterThanCurrDate() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -2);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 40);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.EntertoDate(toDate);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = p2Preconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String p2Preconciliationpage1Reconciliationlink_toDateLessThanForDate() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -40);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.EntertoDate(toDate);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = p2Preconciliationpage1.getActualMsg();
		return uiMessage;

	}
	

	public boolean[] p2PReconciliationlink_checkAmbigious(String serviceType, String validationValue, String selectorType) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		boolean[] result = new boolean[2];
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate,-30);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickP2PReconciliation();
		p2Preconciliationpage1.EnterfromDate(fromDate);
		p2Preconciliationpage1.EntertoDate(toDate);
		p2Preconciliationpage1.SelectserviceType(serviceType);
		p2Preconciliationpage1.ClickOnbtnSubmit();
		boolean dBResult = DBHandler.AccessHandler.checkAmbiguousTransactionsforP2P(fromDate, toDate, selectorType);
		boolean uiResult = p2Preconciliationpage2.checkTransaction(validationValue);	
		result[0] = dBResult;
		result[1] = uiResult;
		return result;

	}
	
	//Success
		public String p2PReconciliationlink_Success(String serviceType, String selectorType) {

			userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			// User Access module ends.
			String currDate = networkAdminHomePage.getDate();
			String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -30);
			String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickReconciliation();
			reconciliationSubCategories.clickP2PReconciliation();
			p2Preconciliationpage1.EnterfromDate(fromDate);
			p2Preconciliationpage1.EntertoDate(toDate);
			p2Preconciliationpage1.SelectserviceType(serviceType);
			p2Preconciliationpage1.ClickOnbtnSubmit();
			boolean dBResult = DBHandler.AccessHandler.checkAmbiguousTransactionsforP2P(fromDate, toDate, selectorType);
			String uiMessage = null;
			if(!dBResult){
			String transactionID = DBHandler.AccessHandler.fetchAmbiguousTransactionsforP2P(fromDate, toDate, selectorType);
			p2Preconciliationpage2.clickonradioButton(transactionID);
			p2Preconciliationpage2.ClickOnbtnSubmit();
			p2Preconciliationpage3.ClickOnbtnSubmit();
			driver.switchTo().alert().accept();
		    //driver.switchTo().defaultContent();
		    uiMessage  = new AddChannelUserDetailsPage(driver).getActualMessage();//p2Preconciliationpage1.getActualMsg();
			}
			else{
			uiMessage = p2Preconciliationpage1.getnoAmbiguiousMessage();
			}
			
			return uiMessage;

		}
		
		//DB_Verification
		public String p2PReconciliationlink_dbVerification(String serviceType, String selectorType) {

			userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			// User Access module ends.
			String result = null;
			String currDate = networkAdminHomePage.getDate();
			String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -30);
			String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickReconciliation();
			reconciliationSubCategories.clickP2PReconciliation();
			p2Preconciliationpage1.EnterfromDate(fromDate);
			p2Preconciliationpage1.EntertoDate(toDate);
			p2Preconciliationpage1.SelectserviceType(serviceType);
			p2Preconciliationpage1.ClickOnbtnSubmit();
			String transferStatus = null;
			boolean dBResult = DBHandler.AccessHandler.checkAmbiguousTransactionsforP2P(fromDate, toDate, selectorType);
			if(!dBResult){
				String transactionID = DBHandler.AccessHandler.fetchAmbiguousTransactionsforP2P(fromDate, toDate, selectorType);
				p2Preconciliationpage2.clickonradioButton(transactionID);
				p2Preconciliationpage2.ClickOnbtnSubmit();
				p2Preconciliationpage3.ClickOnbtnSubmit();
				driver.switchTo().alert().accept();
			    driver.switchTo().defaultContent();
			    transferStatus = DBHandler.AccessHandler.fetchTransferStatusforP2P(transactionID);
			    result = transferStatus;
				}
			else{
				result = p2Preconciliationpage1.getnoAmbiguiousMessage();
			}
			return result;
			
		}
		
		//Fail
				public String p2PReconciliationlink_Fail(String serviceType, String selectorType) {

					userAccessMap = UserAccess.getUserWithAccess(RolesI.P2P_RECONCILIATION);
					login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
					// User Access module ends.
					String currDate = networkAdminHomePage.getDate();
					String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -30);
					String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
					selectNetworkPage.selectNetwork();
					networkAdminHomePage.clickReconciliation();
					reconciliationSubCategories.clickP2PReconciliation();
					p2Preconciliationpage1.EnterfromDate(fromDate);
					p2Preconciliationpage1.EntertoDate(toDate);
					p2Preconciliationpage1.SelectserviceType(serviceType);
					p2Preconciliationpage1.ClickOnbtnSubmit();
					boolean dBResult = DBHandler.AccessHandler.checkAmbiguousTransactionsforP2P(fromDate, toDate, selectorType);
					String uiMessage = null;
					if(!dBResult){
					String transactionID = DBHandler.AccessHandler.fetchAmbiguousTransactionsforP2P(fromDate, toDate, selectorType);
					p2Preconciliationpage2.clickonradioButton(transactionID);
					p2Preconciliationpage2.ClickOnbtnSubmit();
					p2Preconciliationpage3.ClickOnbtnFail();
					driver.switchTo().alert().accept();
				    driver.switchTo().defaultContent();
				    uiMessage  = p2Preconciliationpage1.getActualMsg();
					}
					else{
					uiMessage = p2Preconciliationpage1.getnoAmbiguiousMessage();
					}
					
					return uiMessage;

				}
	

}
