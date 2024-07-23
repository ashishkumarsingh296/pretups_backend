package com.pageobjects.channeladminpages.channelenquiries;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewChannelUser {

	WebDriver driver = null;
	
	@FindBy(name = "searchMsisdn")
	private WebElement MSISDN;
	
	@FindBy(name = "submitView")
	private WebElement SubmitButton;
	
	public ViewChannelUser(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterMSISDN(String MobileNo) {
		Log.info("Trying to enter Mobile No.");
		MSISDN.sendKeys(MobileNo);
		Log.info("Mobile Number entered successfully as: " + MobileNo);
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		SubmitButton.click();
		Log.info("Submit Button clicked successfully");
	}
}
