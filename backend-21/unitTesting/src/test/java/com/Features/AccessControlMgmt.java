package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeladminpages.homepage.AccessControlMgmtSubCategories;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.loginpages.ChangePINForNewUser;
import com.pageobjects.loginpages.ChangePasswordForNewUser;
import com.pageobjects.networkadminpages.accesscontrolmgmt.UserpasswordMgmtpage1;
import com.pageobjects.networkadminpages.accesscontrolmgmt.UserpasswordMgmtpage2;
import com.pageobjects.superadminpages.addoperatoruser.AddOperatorUserDetailsPage;
import com.pageobjects.superadminpages.addoperatoruser.AddOperatorUserPage;
import com.pageobjects.superadminpages.addoperatoruser.ApproveOperatorUsersPage;
import com.pageobjects.superadminpages.homepage.OperatorUsersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.Log;
import com.utils.RandomGeneration;

/**
 * @author lokesh.kontey
 *
 */
public class AccessControlMgmt extends BaseTest {
	
	SuperAdminHomePage homePage;
	AddOperatorUserPage addOptrUserPage;
	AddOperatorUserDetailsPage addOptrUserDetailsPage;
	SelectNetworkPage networkPage;
	Login login;
	RandomGeneration randStr;
	ApproveOperatorUsersPage approveOperatorUser;
	ChangePasswordForNewUser changenewpwd;
	OperatorUsersSubCategories operatorSubLink;
	ChannelUsersSubCategories channelUserSubCategories;
	ChannelAdminHomePage chnlhomepage;
	ChangePINForNewUser changeUsrPIN;
	AddChannelUserDetailsPage addChrUserDetailsPage;
	AccessControlMgmtSubCategories accessControlMgmt;
	UserpasswordMgmtpage1 userPwdPage1;
	UserpasswordMgmtpage2 userPwdPage2;
	public String LoginID;
	public String autoPassword = null;
	public int RowNum;
	public String NEWPASSWORD;
	HashMap<String, String> optresultMap;
	Map<String, String> userAccessMap;
	static String NewPin;
	public String autoPIN = null;
	WebDriver driver=null;
	
	public AccessControlMgmt(WebDriver driver) {
		this.driver=driver;
		homePage = new SuperAdminHomePage(driver);
		addOptrUserPage = new AddOperatorUserPage(driver);
		addOptrUserDetailsPage = new AddOperatorUserDetailsPage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		randStr = new RandomGeneration();
		approveOperatorUser = new ApproveOperatorUsersPage(driver);
		changenewpwd = new ChangePasswordForNewUser(driver);
		operatorSubLink = new OperatorUsersSubCategories(driver);
		optresultMap = new HashMap<String, String>();
		chnlhomepage= new ChannelAdminHomePage(driver);
		channelUserSubCategories = new ChannelUsersSubCategories(driver);
		changeUsrPIN = new ChangePINForNewUser(driver);
		addChrUserDetailsPage= new AddChannelUserDetailsPage(driver);
		userAccessMap= new HashMap<String, String>();
		accessControlMgmt= new AccessControlMgmtSubCategories(driver);
		userPwdPage1= new UserpasswordMgmtpage1(driver);
		userPwdPage2= new UserpasswordMgmtpage2(driver);
	}

	public HashMap<String, String> userPwdMgmt_sendPassword(String parameter, String loginidOrmsisdn, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.USER_PIN_PASSWORDMGMT_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		homePage.clickAccessControlMgmt();
		accessControlMgmt.clickUserPasswordMgmtLink();
		
		if (parameter.equals("loginid")) {
			userPwdPage1.EnterloginID(loginidOrmsisdn);
		} else if (parameter.equals("msisdn")) {
			userPwdPage1.Entermsisdn(loginidOrmsisdn);
		}
		userPwdPage1.Enterremarks(Remarks);
		userPwdPage1.ClickOnbtnSubmit();
		
		userPwdPage2.ClickOnbtnSendPassword();
		driver.switchTo().alert().accept();
		
		optresultMap.put("sendPasswordMsg", addChrUserDetailsPage.getActualMessage());
		return optresultMap;
	}
	
	public HashMap<String, String> userPwdMgmt_resetPassword(String parameter, String loginidOrmsisdn, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.USER_PIN_PASSWORDMGMT_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		homePage.clickAccessControlMgmt();
		accessControlMgmt.clickUserPasswordMgmtLink();
		
		if (parameter.equals("loginid")) {
			userPwdPage1.EnterloginID(loginidOrmsisdn);
		} else if (parameter.equals("msisdn")) {
			userPwdPage1.Entermsisdn(loginidOrmsisdn);
		}
		userPwdPage1.Enterremarks(Remarks);
		userPwdPage1.ClickOnbtnSubmit();
		
		userPwdPage2.ClickOnbtnResetPassword();
		driver.switchTo().alert().accept();
		
		optresultMap.put("resetPasswordMsg", addChrUserDetailsPage.getActualMessage());
		return optresultMap;
	}
	
	public HashMap<String, String> userPwdMgmt_unblockPassword(String parameter, String loginidOrmsisdn, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.USER_PIN_PASSWORDMGMT_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		homePage.clickAccessControlMgmt();
		accessControlMgmt.clickUserPasswordMgmtLink();
		
		if (parameter.equals("loginid")) {
			userPwdPage1.EnterloginID(loginidOrmsisdn);
		} else if (parameter.equals("msisdn")) {
			userPwdPage1.Entermsisdn(loginidOrmsisdn);
		}
		userPwdPage1.Enterremarks(Remarks);
		userPwdPage1.ClickOnbtnSubmit();
		
		userPwdPage2.ClickOnbtnUnblockPassword();
		driver.switchTo().alert().accept();
		
		optresultMap.put("unblockPasswordMsg", addChrUserDetailsPage.getActualMessage());
		return optresultMap;
		
	}
	
	public HashMap<String, String> userPwdMgmt_unblockandSendPassword(String parameter, String loginidOrmsisdn, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.USER_PIN_PASSWORDMGMT_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		homePage.clickAccessControlMgmt();
		accessControlMgmt.clickUserPasswordMgmtLink();
		
		if (parameter.equals("loginid")) {
			userPwdPage1.EnterloginID(loginidOrmsisdn);
		} else if (parameter.equals("msisdn")) {
			userPwdPage1.Entermsisdn(loginidOrmsisdn);
		}
		userPwdPage1.Enterremarks(Remarks);
		userPwdPage1.ClickOnbtnSubmit();
		
		userPwdPage2.ClickOnbtnUnblockSendPassword();
		driver.switchTo().alert().accept();
		
		optresultMap.put("unblocksendPasswordMsg", addChrUserDetailsPage.getActualMessage());
		return optresultMap;
	}
	
	public void userPwdMgmt_back(String parameter, String loginidOrmsisdn, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.USER_PIN_PASSWORDMGMT_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		homePage.clickAccessControlMgmt();
		accessControlMgmt.clickUserPasswordMgmtLink();
		
		if (parameter.equals("loginid")) {
			userPwdPage1.EnterloginID(loginidOrmsisdn);
		} else if (parameter.equals("msisdn")) {
			userPwdPage1.Entermsisdn(loginidOrmsisdn);
		}
		userPwdPage1.Enterremarks(Remarks);
		userPwdPage1.ClickOnbtnSubmit();
		
		userPwdPage2.ClickOnbtnBack();
		userPwdPage1.Enterremarks(Remarks);
	}
	
	public void userPwdMgmt_cancel(String parameter, String loginidOrmsisdn, String Remarks){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.USER_PIN_PASSWORDMGMT_ROLECODE); //Getting User with Access to Add Channel Users
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		homePage.clickAccessControlMgmt();
		accessControlMgmt.clickUserPasswordMgmtLink();
		
		if (parameter.equals("loginid")) {
			userPwdPage1.EnterloginID(loginidOrmsisdn);
		} else if (parameter.equals("msisdn")) {
			userPwdPage1.Entermsisdn(loginidOrmsisdn);
		}
		userPwdPage1.Enterremarks(Remarks);
		userPwdPage1.ClickOnbtnSubmit();
		
		userPwdPage2.ClickOnbtnCancel();
		userPwdPage1.Enterremarks(Remarks);
	}
	
	public String blockPassword(String LoginID, String CategoryCode){
		String errorMessage = null;
		int count=DBHandler.AccessHandler.maxPasswordBlockCount(CategoryCode);
		for(int i=0; i<count; i++) {
			Log.info("Invalid attempt: "+ (i+1));
			errorMessage = login.LoginAsUser(driver,LoginID, "*invalidpwd*");
		}
		return errorMessage;
	}
}
