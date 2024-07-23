package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.pageobjects.superadminpages.UserStatusConfiguration.UserStatusConfigurationPage;
import com.pageobjects.superadminpages.UserStatusConfiguration.UserStatusConfirmPage;
import com.pageobjects.superadminpages.UserStatusConfiguration.UserStatusDetailsPage;
import com.pageobjects.superadminpages.UserStatusConfiguration.ViewUserStatus;
import com.pageobjects.superadminpages.homepage.MastersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.RandomGeneration;

public class UserStatusConfiguration {
	
	WebDriver driver = null;
	Login login;
	RandomGeneration randomNum;
	SuperAdminHomePage SuperAdminHomePage;
	MastersSubCategories MastersSubCategories;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	SelectNetworkPage networkPage;
	UserStatusConfigurationPage UserStatusConfigurationPage;
	UserStatusConfirmPage UserStatusConfirmPage;
	UserStatusDetailsPage UserStatusDetailsPage;
	ViewUserStatus ViewUserStatus;
	
	
	public UserStatusConfiguration(WebDriver driver){
		this.driver = driver;	
		login = new Login();
		randomNum = new RandomGeneration();
		SuperAdminHomePage = new SuperAdminHomePage(driver);
		MastersSubCategories = new MastersSubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		UserStatusConfigurationPage = new UserStatusConfigurationPage(driver);
		UserStatusConfirmPage = new UserStatusConfirmPage(driver);
		UserStatusDetailsPage = new UserStatusDetailsPage(driver);
		ViewUserStatus = new ViewUserStatus(driver);
		
	}
	
	public String AddUserStatusConfiguration(String domain , String catCode){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.USERSTATUS); //Getting User with Access to Add USERSTATUS
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickUserStatusConfiguration();
		UserStatusConfigurationPage.SelectGateway(PretupsI.GATEWAY_TYPE_WEB);
		UserStatusConfigurationPage.SelectUserType(PretupsI.CHANNEL_USER_TYPE);
		UserStatusConfigurationPage.SelectDomain(domain);
		UserStatusConfigurationPage.SelectCategory(catCode);
		UserStatusConfigurationPage.clickAdd();
		UserStatusDetailsPage.SelectUserSenderAllowedActiveCheckbox();
		UserStatusDetailsPage.SelectUserSenderDeniedPACheckbox();
		UserStatusDetailsPage.SelectUserReceiverAllowedActiveCheckbox();
		UserStatusDetailsPage.SelectUserReceiverDeniedExpiredCheckbox();
		UserStatusDetailsPage.SelectWebLoginAllowedActiveCheckbox();
		UserStatusDetailsPage.SelectWebLoginDeniedExpiredCheckbox();
		UserStatusDetailsPage.clickSubmit();
		UserStatusConfirmPage.clickConfirm();
		String message = UserStatusConfigurationPage.getMsg();
		
		return message;
		
	}
	
	
	
	public String ModifyUserStatusConfiguration(String domain , String catCode){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.USERSTATUS); //Getting User with Access to Add USERSTATUS
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickUserStatusConfiguration();
		UserStatusConfigurationPage.SelectGateway(PretupsI.GATEWAY_TYPE_WEB);
		UserStatusConfigurationPage.SelectUserType(PretupsI.CHANNEL_USER_TYPE);
		UserStatusConfigurationPage.SelectDomain(domain);
		UserStatusConfigurationPage.SelectCategory(catCode);
		UserStatusConfigurationPage.clickModify();
		UserStatusDetailsPage.SelectWebLoginDeniedPACheckbox();
		UserStatusDetailsPage.clickSubmit();
		UserStatusConfirmPage.clickConfirm();
		String message = UserStatusConfigurationPage.getMsg();
		
		return message;
		
	}
	
	
	public String ViewUserStatusConfiguration(String domain , String catCode){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.USERSTATUS); //Getting User with Access to Add USERSTATUS
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickUserStatusConfiguration();
		UserStatusConfigurationPage.SelectGateway(PretupsI.GATEWAY_TYPE_WEB);
		UserStatusConfigurationPage.SelectUserType(PretupsI.CHANNEL_USER_TYPE);
		UserStatusConfigurationPage.SelectDomain(domain);
		UserStatusConfigurationPage.SelectCategory(catCode);
		UserStatusConfigurationPage.clickView();
	
		String message = ViewUserStatus.getMessage();
		
		return message;
		
	}

}
