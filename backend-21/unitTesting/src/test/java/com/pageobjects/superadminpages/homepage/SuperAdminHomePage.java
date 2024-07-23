package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;
import com.utils._masterVO;

public class SuperAdminHomePage {

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=MASTER')]]")
	private WebElement masters;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CACHE001')]]")
	private WebElement updateCache;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=RCACHE001')]]")
	private WebElement redisupdateCache;
	
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
	
	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=VOMSDENOM')]]")
	private WebElement voucherDenomination;
	
	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=VOMSPROFIL')]]")
	private WebElement voucherProfile;

	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=VOMSGEN')]]")
	private WebElement voucherGeneration;
	
	@ FindBy(xpath = "//a[@href[contains(.,'moduleCode=VOMSREPORT')]]")
	private WebElement voucherReports;

	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=VOMSEXPDT')]]")
	private WebElement voucherExpiry;
	
	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=VOUBUNDLE')]]")
	private WebElement voucherBundleManagement;
	
	@ FindBy(linkText = "Logout")
	private WebElement logout ;
	
	WebDriver driver= null;
	
	public SuperAdminHomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickMasters() {
		Log.info("Trying to click Masters link");
		WebDriverWait wait = new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(masters));
		masters.click();
		Log.info("Master Link clicked successfully");
	}
	
	public void clickUpdateCache() {
		if(_masterVO.getClientDetail("UPDATECACHE_VER").equalsIgnoreCase("2"))
		redisupdateCache.click();
		else
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

	public void clickVoucherDenomination() {
		Log.info("Trying to click Voucher Denomination link");
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(voucherDenomination));
		voucherDenomination.click();
		Log.info("Voucher Denomination Link clicked successfully");
	}
	
	public void clickVoucherProfile() {
		Log.info("Trying to click Voucher Profile link");
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(voucherProfile));
		voucherProfile.click();
		Log.info("Voucher Profile Link clicked successfully");
	}
	
	public void clickVoucherGeneration() {
		Log.info("Trying to click Voucher Generation link");
		voucherGeneration.click();
		Log.info("Voucher Generation Link clicked successfully");
	}
	
	public void clickVoucherReports() {
		Log.info("Trying to click Voucher Reports link");
		voucherReports.click();
		Log.info("Voucher Reports Link clicked successfully");
	}
	
	public void clickLogout() {
		logout.click();
		Log.info("User clicked Logout.");
	}
	
	public void clickVoucherExpiry() {
		Log.info("Trying to click Voucher Expiry link");
		voucherExpiry.click();
		Log.info("Voucher Expiry Link clicked successfully");
	}
	
	public void clickVoucherBundleManagement() {
		Log.info("Trying to click Voucher Bundle Management Link");
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(voucherBundleManagement));
		voucherBundleManagement.click();
		Log.info("Voucher Bundle Management link clicked successfully");
	}
}
