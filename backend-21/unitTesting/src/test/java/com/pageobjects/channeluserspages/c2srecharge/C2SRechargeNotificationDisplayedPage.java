package com.pageobjects.channeluserspages.c2srecharge;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2SRechargeNotificationDisplayedPage {

	@FindBy(name = "btnBack")
	private WebElement backButton;

	@FindBy(name = "btnPrint")
	private WebElement printButton;

	WebDriver driver = null;

	public C2SRechargeNotificationDisplayedPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked submit button");
	}

	public void clickPrintButton() {
		printButton.click();
		Log.info("User clicked print button");
	}
}
