package com.pageobjects.channeladminpages.channelenquiries;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class UserBalances {

	WebDriver driver = null;
	
	@FindBy(name = "searchLoginId")
	private WebElement LoginID;
	
	@FindBy(name = "searchMsisdn")
	private WebElement MSISDN;
	
	@FindBy(name = "submitBalance")
	private WebElement SubmitButton;
	
	public UserBalances(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterLoginID(String login) {
		Log.info("Trying to enter Login ID");
		LoginID.sendKeys(login);
		Log.info("Login ID entered successfully as " + login);
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		SubmitButton.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public void enterMSISDN(String _msisdn) {
		Log.info("Trying to enter MSISDN");
		MSISDN.sendKeys(_msisdn);
		Log.info("MSISDN Entered successfully as: " + _msisdn);
	}
}
