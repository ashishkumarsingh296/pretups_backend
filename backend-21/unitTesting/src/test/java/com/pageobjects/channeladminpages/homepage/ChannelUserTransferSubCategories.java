package com.pageobjects.channeladminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ChannelUserTransferSubCategories {
	
	@FindBy(id = "CUSRSUSPO1USERTRF")
    private WebElement SuspendChannelUserHierarchy;

	@FindBy(id = "CUSRRESP01USERTRF")
	private WebElement ResumeChannelUserHierarchy;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=CUSRTRF001')]]")
	private WebElement TransferChannelUser;

	
	WebDriver driver = null;

	public ChannelUserTransferSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickSuspendChannelUserHierarchy() {
		Log.info("Trying to click Suspend User Hierarchy Link");
		SuspendChannelUserHierarchy.click();
		Log.info("Suspend User Hierarchy link clicked successfully");
	}

	public void clickResumeChannelUserHierarchy() {
		Log.info("Trying to click Resume User Hierarchy Link");
		ResumeChannelUserHierarchy.click();
		Log.info("Resume User Hierarchy link clicked successfully");
	}
	
	public void clickTransferChannelUserHierarchy() {
		Log.info("Trying to click Transfer User Link");
		TransferChannelUser.click();
		Log.info("Transfer User link clicked successfully");
	}

	
}
