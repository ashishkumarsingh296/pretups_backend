package com.pageobjects.channeluserspages.homepages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ChannelReportsC2SSubLinksPage {

	@FindBy(xpath = "//a[@href [contains(.,'pageCode=RPTADCS001')]]")
	private WebElement addCommSummaryRpt;

	
	
	@FindBy(linkText = "Logout")
	private WebElement logout;

	WebDriver driver = null;

	public ChannelReportsC2SSubLinksPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickAddCommSmryRptSpring() {
		Log.info("Trying to click Additional Commission Summary link.");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.info("Error: "+e);
		}
		addCommSummaryRpt.click();
		Log.info("User clicked Additional Commission Summary link.");
	}

	public void clickAddCommSmryRptStruts() {
		Log.info("Trying to click Additional Commission Summary link.");
		addCommSummaryRpt.click();
		Log.info("User clicked Additional Commission Summary link.");
	}
	

	public void clickLogout() {
		logout.click();
		Log.info("User clicked logout button");
	}

}
