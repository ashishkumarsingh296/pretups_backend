package com.pageobjects.networkadminpages.accesscontrolmgmt;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class UserpasswordMgmtpage2 {
	WebDriver driver;

	public UserpasswordMgmtpage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "btnUnblockPassword")
	private WebElement btnUnblockPassword;

	@FindBy(name = "btnUnblockSendPassword")
	private WebElement btnUnblockSendPassword;

	@FindBy(name = "btnSendPassword")
	private WebElement btnSendPassword;

	@FindBy(name = "btnResetPassword")
	private WebElement btnResetPassword;

	@FindBy(name = "btnCancel")
	private WebElement btnCancel;

	@FindBy(name = "btnBack")
	private WebElement btnBack;

	public void ClickOnbtnUnblockPassword() {
		Log.info("Trying to click on button  Unblock password ");
		btnUnblockPassword.click();
		Log.info("Clicked on  Unblock password successfully");
	}

	public void ClickOnbtnUnblockSendPassword() {
		Log.info("Trying to click on button  Unblock & send password ");
		btnUnblockSendPassword.click();
		Log.info("Clicked on  Unblock & send password successfully");
	}

	public void ClickOnbtnSendPassword() {
		Log.info("Trying to click on button  Send password ");
		btnSendPassword.click();
		Log.info("Clicked on  Send password successfully");
	}

	public void ClickOnbtnResetPassword() {
		Log.info("Trying to click on button  Reset password ");
		btnResetPassword.click();
		Log.info("Clicked on  Reset password successfully");
	}

	public void ClickOnbtnCancel() {
		Log.info("Trying to click on button  Cancel ");
		btnCancel.click();
		Log.info("Clicked on  Cancel successfully");
	}

	public void ClickOnbtnBack() {
		Log.info("Trying to click on button  Back ");
		btnBack.click();
		Log.info("Clicked on  Back successfully");
	}
}
