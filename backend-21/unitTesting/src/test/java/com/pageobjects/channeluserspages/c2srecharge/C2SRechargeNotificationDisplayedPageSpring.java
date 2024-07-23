package com.pageobjects.channeluserspages.c2srecharge;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2SRechargeNotificationDisplayedPageSpring {

	@FindBy(xpath = "//a[@href='process-backc2srecharge.form']")
	private WebElement backButton;

	@FindBy(id = "btnPrint")
	private WebElement printButton;

	WebDriver driver = null;

	public C2SRechargeNotificationDisplayedPageSpring(WebDriver driver) {
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
