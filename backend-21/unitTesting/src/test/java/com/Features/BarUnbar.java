package com.Features;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.barunbar.BarUserPage;
import com.pageobjects.channeladminpages.barunbar.UnBarUserPage;
import com.pageobjects.channeladminpages.barunbar.UnBarUserPage2;
import com.pageobjects.channeladminpages.barunbar.ViewBarredListPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.MastersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class BarUnbar {

	WebDriver driver;

	Login login;
	ChannelAdminHomePage cAHomePage;
	MastersSubCategories mastersSubCat;
	BarUserPage barUserPage;
	UnBarUserPage unBarUserPage;
	UnBarUserPage2 unBarUserPage2;
	ChannelUser channelUser;
	String msisdn;
	SelectNetworkPage networkPage;
	ViewBarredListPage viewBarList;


	public BarUnbar(WebDriver driver){
		this.driver= driver;
		login = new Login();
		cAHomePage = new ChannelAdminHomePage(driver);
		mastersSubCat = new MastersSubCategories(driver);
		barUserPage = new BarUserPage(driver);
		unBarUserPage = new UnBarUserPage(driver);
		unBarUserPage2 = new UnBarUserPage2(driver);
		channelUser = new ChannelUser(driver);
		networkPage = new SelectNetworkPage(driver);
		viewBarList = new ViewBarredListPage(driver);
	}


	public void barringUser(String module, String userType, String msisdn){
		Log.startTestCase("Barring User Test case.");

		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.BAR_USER);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		networkPage.selectNetwork();
		cAHomePage.clickMasters();

		mastersSubCat.clickBarUser();

		//Fill mandatory details.

		barUserPage.selectModule(module);
		barUserPage.selectUserType(userType);
		barUserPage.selectBarringType();

		barUserPage.enterMobileNumber(msisdn);
		barUserPage.enterBarredReason("Automation Barring the user.");
		barUserPage.clickSubmitBtn();
		barUserPage.clickConfirmBtn();
	}

	public void unBarringUser(String module, String userType, String msisdn){
		Log.startTestCase("Unbarring User Test case.");
		
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.UNBAR_USER);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));
		networkPage.selectNetwork();

		cAHomePage.clickMasters();
		mastersSubCat.clickUnBarUser();

		unBarUserPage.selectModule(module);
		unBarUserPage.selectUserType(userType);
		unBarUserPage.enterMobileNumber(msisdn);
		unBarUserPage.enterBarredReason("Automation Un-barring the user.");

		unBarUserPage.clickSubmitBtn();

		unBarUserPage2.checkUnBarALLUser();
		unBarUserPage2.clickSubmitBtn();
		unBarUserPage2.clickConfirmBtn();
		
	}
	
	public void viewBarredList(String module, String userType, String msisdn, boolean exist){
		Log.startTestCase("View Barred List Test case.");
		
		Map<String, String> userInfo= UserAccess.getUserWithAccess(RolesI.VIEWBARREDLIST);
		login.LoginAsUser(driver, userInfo.get("LOGIN_ID"), userInfo.get("PASSWORD"));

		cAHomePage.clickMasters();
		mastersSubCat.clickViewBarredlist();

		viewBarList.enterMobileNumber(msisdn);
		viewBarList.clickSubmitBtn();
		viewBarList.checkMsisdnExistinList(msisdn,exist);
	}
}
