package com.pageobjects.channeluserspages.c2srecharge;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2SRechargeConfirmPage {

	@FindBy(name = "btnSubmit")
	private WebElement submit;

	@FindBy(name = "btnCancel")
	private WebElement cancelButton;

	@FindBy(name = "btnBack")
	private WebElement backButton;

	WebDriver driver = null;

	public C2SRechargeConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickSubmitButton() {
		submit.click();
		Log.info("User clicked submit button");
	}

	public void clickCancelButton() {
		cancelButton.click();
		Log.info("User clicked Cancel button");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}

}
