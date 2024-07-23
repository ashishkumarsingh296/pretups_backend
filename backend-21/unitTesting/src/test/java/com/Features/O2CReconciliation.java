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
import com.pageobjects.networkadminpages.reconciliation.O2Creconciliationpage1;
import com.pageobjects.networkadminpages.reconciliation.O2Creconciliationpage2;
import com.pageobjects.networkadminpages.reconciliation.O2Creconciliationpage3;
import com.pageobjects.networkadminpages.reconciliation.O2Creconciliationpage4;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.BTSLDateUtil;

public class O2CReconciliation {
	public WebDriver driver;
	String MasterSheetPath;
	NetworkAdminHomePage homePage;
	Login login;
	SelectNetworkPage selectNetworkPage;
	Map<String, String> userAccessMap;
	NetworkAdminHomePage networkAdminHomePage;
	ReconciliationSubCategories reconciliationSubCategories;
	String[] result;
	O2Creconciliationpage1 o2Creconciliationpage1;
	O2Creconciliationpage2 o2Creconciliationpage2;
	O2Creconciliationpage3 o2Creconciliationpage3;
	O2Creconciliationpage4 o2Creconciliationpage4;
	
	public O2CReconciliation(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		selectNetworkPage = new SelectNetworkPage(driver);
		userAccessMap = new HashMap<String, String>();
		networkAdminHomePage = new NetworkAdminHomePage(driver);
		reconciliationSubCategories = new ReconciliationSubCategories(driver);
		o2Creconciliationpage1 = new O2Creconciliationpage1(driver);
		o2Creconciliationpage2 = new O2Creconciliationpage2(driver);
		o2Creconciliationpage3 = new O2Creconciliationpage3(driver);
		o2Creconciliationpage4 = new O2Creconciliationpage4(driver);
	}
	
	//Success
		public String[] o2CReconciliationlink_Success() {

			userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			// User Access module ends.
			String currDate = networkAdminHomePage.getDate();
			String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -30);
			String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickReconciliation();
			reconciliationSubCategories.clickO2CReconciliation();
			o2Creconciliationpage1.EnterfromDate(fromDate);
			o2Creconciliationpage1.EntertoDate(toDate);
			o2Creconciliationpage1.ClickOnbtnSubmit();
			boolean dBResult = DBHandler.AccessHandler.checkAmbiguousO2CPendingTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate));
			String[] arr={null,null};
			String transactionID=null;
			if(!dBResult){
			transactionID = DBHandler.AccessHandler.fetchAmbiguousO2CPendingTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate));
			o2Creconciliationpage2.clickonradioButton(transactionID);
			o2Creconciliationpage2.ClickOnbtnSubmit();
		    String str=	o2Creconciliationpage1.getActualMsg();
		    if(str==null || str.equals(" "))
		    {	
			o2Creconciliationpage3.ClickOnbtnSubmit();
			o2Creconciliationpage4.ClickOnbtnConfirm();
		    arr[0]  = o2Creconciliationpage1.getActualMsg();
		    }
		    else
		    {
		    	arr[0]  = o2Creconciliationpage1.getActualMsg();
		    }
			}
			else{
		    arr[0] = o2Creconciliationpage1.getActualMsg();
			}
			arr[1]=transactionID;
			return arr;

		}
		
		//Fail
		
		public String[] o2CReconciliationlink_Failure() {

			userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			// User Access module ends.
			String currDate = networkAdminHomePage.getDate();
			String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -30);
			String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickReconciliation();
			reconciliationSubCategories.clickO2CReconciliation();
			o2Creconciliationpage1.EnterfromDate(fromDate);
			o2Creconciliationpage1.EntertoDate(toDate);
			o2Creconciliationpage1.ClickOnbtnSubmit();
			boolean dBResult = DBHandler.AccessHandler.checkAmbiguousO2CPendingTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate));
			String[] arr={null,null};
			String transactionID=null;
			if(!dBResult){
			transactionID = DBHandler.AccessHandler.fetchAmbiguousO2CPendingTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate));
			o2Creconciliationpage2.clickonradioButton(transactionID);
			o2Creconciliationpage2.ClickOnbtnSubmit();
			 String str=	o2Creconciliationpage1.getActualMsg();
			    if(str==null || str.equals(" "))
			    {	
			    	o2Creconciliationpage3.ClickOnbtnFail();
				o2Creconciliationpage4.ClickOnbtnConfirm();
			    arr[0]  = o2Creconciliationpage1.getActualMsg();
			    }
			    else
			    {
			    	arr[0]  = o2Creconciliationpage1.getActualMsg();
			    }
			}
			else{
			arr[0] = o2Creconciliationpage1.getActualMsg();
			}
		arr[1]=transactionID;
			return arr;

		}
		
		public String o2CReconciliationFromDateNull()
		{
			userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			// User Access module ends.
			String currDate = networkAdminHomePage.getDate();
			String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -29);
			String fromDate = "";
			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickReconciliation();
			reconciliationSubCategories.clickO2CReconciliation();
			o2Creconciliationpage1.EnterfromDate(fromDate);
			o2Creconciliationpage1.EntertoDate(toDate);
			o2Creconciliationpage1.ClickOnbtnSubmit();
			return o2Creconciliationpage1.getActualMsg();
		}
		public String o2CReconciliationToDateNull()
		{
			userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			// User Access module ends.
			String currDate = networkAdminHomePage.getDate();
			String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -29);
			String toDate = "";
			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickReconciliation();
			reconciliationSubCategories.clickO2CReconciliation();
			o2Creconciliationpage1.EnterfromDate(fromDate);
			o2Creconciliationpage1.EntertoDate(toDate);
			o2Creconciliationpage1.ClickOnbtnSubmit();
			return o2Creconciliationpage1.getActualMsg();
		}
		
	public String 	o2CReconciliationlink_dateDiffMoreThan30Days()
	{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -40);
		String toDate = currDate;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickO2CReconciliation();
		o2Creconciliationpage1.EnterfromDate(fromDate);
		o2Creconciliationpage1.EntertoDate(toDate);
		o2Creconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = o2Creconciliationpage1.getActualMsg();
		return uiMessage;
	}	
		
	public String o2CReconciliationlink_fromDateGreaterThanCurrDate() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 40);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 40);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickO2CReconciliation();
		o2Creconciliationpage1.EnterfromDate(fromDate);
		o2Creconciliationpage1.EntertoDate(toDate);
		o2Creconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = o2Creconciliationpage1.getActualMsg();
		return uiMessage;

	}
		
	public String o2CReconciliationlink_toDateGreaterThanCurrDate() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -2);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, 40);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickO2CReconciliation();
		o2Creconciliationpage1.EnterfromDate(fromDate);
		o2Creconciliationpage1.EntertoDate(toDate);
		o2Creconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = o2Creconciliationpage1.getActualMsg();
		return uiMessage;

	}	
	public String o2CReconciliationlink_toDateLessThanForDate() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -23);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickO2CReconciliation();
		o2Creconciliationpage1.EnterfromDate(fromDate);
		o2Creconciliationpage1.EntertoDate(toDate);
		o2Creconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = o2Creconciliationpage1.getActualMsg();
		return uiMessage;

	}
	public String o2CReconciliationlink_invalidFromDateFormat() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String fromDate = null;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickO2CReconciliation();
		o2Creconciliationpage1.EnterfromDate(fromDate);
		o2Creconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = o2Creconciliationpage1.getActualMsg();
		return uiMessage;

	}
	
	public String o2CReconciliationlink_invalidToDateFormat() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -40);
		String toDate = null;
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickO2CReconciliation();
		o2Creconciliationpage1.EnterfromDate(fromDate);
		o2Creconciliationpage1.EntertoDate(toDate);
		o2Creconciliationpage1.ClickOnbtnSubmit();
		String uiMessage = o2Creconciliationpage1.getActualMsg();
		return uiMessage;

	}
	public boolean[] o2CReconciliationlink_checkAmbigious(String validationValue) {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		boolean[] result = new boolean[2];
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate,-30);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickO2CReconciliation();
		o2Creconciliationpage1.EnterfromDate(fromDate);
		o2Creconciliationpage1.EntertoDate(toDate);
		o2Creconciliationpage1.ClickOnbtnSubmit();
		boolean dBResult = DBHandler.AccessHandler.checkAmbiguousO2CPendingTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate));
		boolean uiResult = o2Creconciliationpage2.checkTransaction(validationValue);	
		result[0] = !dBResult;
		result[1] = uiResult;
		return result;
	}
	//DB_Verification 
	public String o2CReconciliationlink_dbVerification() {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		// User Access module ends.
		String result = null;
		String currDate = networkAdminHomePage.getDate();
		String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -30);
		String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
		selectNetworkPage.selectNetwork();
		networkAdminHomePage.clickReconciliation();
		reconciliationSubCategories.clickO2CReconciliation();
		o2Creconciliationpage1.EnterfromDate(fromDate);
		o2Creconciliationpage1.EntertoDate(toDate);
		o2Creconciliationpage1.ClickOnbtnSubmit();
		String transferStatus = null;
		boolean dBResult = DBHandler.AccessHandler.checkAmbiguousO2CPendingTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate));
		if(!dBResult){
			String transactionID = DBHandler.AccessHandler.fetchAmbiguousO2CPendingTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate));
			o2Creconciliationpage2.clickonradioButton(transactionID);
			o2Creconciliationpage2.ClickOnbtnSubmit();
			String str=	o2Creconciliationpage1.getActualMsg();
		    if(str==null || str.equals(" "))
		    {	
		    	o2Creconciliationpage3.ClickOnbtnSubmit();
				o2Creconciliationpage4.ClickOnbtnConfirm();
			    transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
			    result = transferStatus;
		    }
		    else
		    {
		    	result=str;
		    }
			
			}
		else{
			result = o2Creconciliationpage1.getActualMsg();
		}
		return result;
		
	}
	//DB_Verification CANCEL
		public String o2CReconciliationlink_dbVerification1() 
		{
			userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_RECONCILIATION);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
			// User Access module ends.
			String result = null;
			String currDate = networkAdminHomePage.getDate();
			String fromDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -30);
			String toDate = networkAdminHomePage.addDaysToCurrentDate(currDate, -1);
			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickReconciliation();
			reconciliationSubCategories.clickO2CReconciliation();
			o2Creconciliationpage1.EnterfromDate(fromDate);
			o2Creconciliationpage1.EntertoDate(toDate);
			o2Creconciliationpage1.ClickOnbtnSubmit();
			String transferStatus = null;
			boolean dBResult = DBHandler.AccessHandler.checkAmbiguousO2CPendingTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate));
			if(!dBResult){
				String transactionID = DBHandler.AccessHandler.fetchAmbiguousO2CPendingTransactions(BTSLDateUtil.getGregorianDateInString(fromDate), BTSLDateUtil.getGregorianDateInString(toDate));
				o2Creconciliationpage2.clickonradioButton(transactionID);
				o2Creconciliationpage2.ClickOnbtnSubmit();
				 String str=	o2Creconciliationpage1.getActualMsg();
				    if(str==null || str.equals(" "))
				    {	
				    	o2Creconciliationpage3.ClickOnbtnFail();
						o2Creconciliationpage4.ClickOnbtnConfirm();
					    transferStatus = DBHandler.AccessHandler.fetchTransferStatusO2C(transactionID);
					    result = transferStatus;
				    }
				    else
				    {
				    	result=str;
				    }
				
				}
			else{
				result = o2Creconciliationpage1.getActualMsg();
			}
			return result;
			
		}
		
}
