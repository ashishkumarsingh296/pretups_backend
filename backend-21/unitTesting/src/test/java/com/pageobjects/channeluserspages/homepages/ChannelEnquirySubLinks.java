package com.pageobjects.channeluserspages.homepages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ChannelEnquirySubLinks {
	
	WebDriver driver;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=CUSRBALV03')]]")
	private WebElement selfBalance;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=VIEWCUSS01')]]")
	private WebElement viewSelfDetails;
	
	public ChannelEnquirySubLinks(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickSelfBalance() {
		Log.info("Trying to click Self Balance Link");
		selfBalance.click();
		Log.info("Self Balance Link clicked successfully");
	}
	
	public void clickViewSelfDetails() {
		Log.info("Trying to click View Self Details Link");
		viewSelfDetails.click();
		Log.info("View Self Details link clicked successfully");
	}
}
