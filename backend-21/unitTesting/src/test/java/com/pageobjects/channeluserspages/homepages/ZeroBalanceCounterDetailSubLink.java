package com.pageobjects.channeluserspages.homepages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ZeroBalanceCounterDetailSubLink {

	@FindBy(xpath = "//a[@href[contains(.,'pageCode=ZBALDET001')]]")
	private WebElement initiateTransferLink;

	@FindBy(xpath = "//a[@href[contains(.,'pageCode=ZBALDET001')]]")
	private WebElement returnLink;

	WebDriver driver = null;

	public ZeroBalanceCounterDetailSubLink(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickInitiateTransfer() {
		Log.info("Trying to click Initiate Transfer link");
		initiateTransferLink.click();
		Log.info("User clicked Zero Balance Counter Details Link.");
	}

	public void clickReturnLink() {
		Log.info("Trying to click Zero Balance Counter Details Return Link");
		returnLink.click();
		Log.info("Return link clicked successfully");
	}
}
