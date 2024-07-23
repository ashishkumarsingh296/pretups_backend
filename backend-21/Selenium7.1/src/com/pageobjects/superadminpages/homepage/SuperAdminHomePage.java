package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class SuperAdminHomePage {

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=MASTER')]]")
	private WebElement masters;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CACHE001')]]")
	private WebElement updateCache;
	
	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=PREFERENCE')]]")
	private WebElement preferences;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=CHANGENET')]]")
	private WebElement changeNetwork;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=CHNLDOMAIN')]]")
	private WebElement channelDomain;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=PROFILES')]]")
	private WebElement profileManagement;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=OUSERS')]]")
	private WebElement operatorUsers;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=GATEWAY')]]")
	private WebElement messageGateway;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=ACCESSCTRL')]]")
	private WebElement accessControlMgmt;

	@ FindBy(xpath = "//a[@href='/pretups/changePasswordAction.do?method=showChangePassword&page=0']")
	private WebElement changePassword;

	@ FindBy(linkText = "Logout")
	private WebElement logout ;
	
	WebDriver driver= null;
	
	public SuperAdminHomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickMasters() {
		Log.info("Trying to click Masters link");
		masters.click();
		Log.info("Master Link clicked successfully");
	}
	
	public void clickUpdateCache() {
		updateCache.click();
		Log.info("User clicked Update Cache Page");
	}

	public void clickPreferences() {
		preferences.click();
		Log.info("User clicked Preferences.");
	}

	public void clickChangeNetwork() {
		changeNetwork.click();
		Log.info("User clicked Change Network.");
	}

	public void clickChannelDomain() {
		Log.info("Trying to click Channel Domain Link");
		channelDomain.click();
		Log.info("User clicked Channel Domain.");
	}

	public void clickProfileManagement() {
		profileManagement.click();
		Log.info("User clicked Profile Management.");
	}

	public void clickOperatorUsers() {
		operatorUsers.click();
		Log.info("User clicked Operator Users.");
	}

	public void clickMessageGateway() {
		messageGateway.click();
		Log.info("User clicked Message Gateway.");
	}

	public void clickAccessControlMgmt() {
		accessControlMgmt.click();
		Log.info("User clicked Access Control Mgmt.");
	}

	public void clickChangePassword() {
		changePassword.click();
		Log.info("User clicked Change Password.");
	}

	public void clickLogout() {
		logout.click();
		Log.info("User clicked Logout.");
	}
}
