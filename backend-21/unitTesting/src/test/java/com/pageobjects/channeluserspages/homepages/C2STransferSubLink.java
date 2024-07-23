package com.pageobjects.channeluserspages.homepages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2STransferSubLink {

	@FindBy(xpath = "//a[@href[contains(.,'pageCode=RPTTRCS001')]]")
	private WebElement initiateTransferLink;

	@FindBy(xpath = "//a[@href[contains(.,'pageCode=RPTTRCS001')]]")
	private WebElement returnLink;

	WebDriver driver = null;

	public C2STransferSubLink(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickInitiateTransfer() {
		Log.info("Trying to click Initiate Transfer link");
		initiateTransferLink.click();
		Log.info("User clicked C2S Transfer Link.");
	}

	public void clickReturnLink() {
		Log.info("Trying to click C2S Transfer Return Link");
		returnLink.click();
		Log.info("Return link clicked successfully");
	}
}
