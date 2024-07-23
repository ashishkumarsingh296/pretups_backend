package com.pageobjects.channeluserspages.homepages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CTransferSubLink {

	@FindBy(xpath = "//a[@href[contains(.,'pageCode=O2CTRF006')]]")
	private WebElement initiateTransferLink;

	@FindBy(xpath = "//a[@href[contains(.,'pageCode=O2CRET101')]]")
	private WebElement returnLink;

	WebDriver driver = null;

	public O2CTransferSubLink(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickInitiateTransfer() {
		Log.info("Trying to click Initiate Transfer link");
		initiateTransferLink.click();
		Log.info("User clicked C2S Transfer Link.");
	}

	public void clickReturnLink() {
		Log.info("Trying to click O2C Return Link");
		returnLink.click();
		Log.info("Return link clicked successfully");
	}

}
