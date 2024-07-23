package com.pageobjects.channeladminpages.channeluserpinmgmt;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ChannelUSerPINMgmtPage_1 {
	
	@FindBy(name="msisdn")
	private WebElement msisdn;
	
	@FindBy(name="remarks")
	private WebElement remarks;
	
	@FindBy(name="btnSubmit")
	private WebElement btnSubmit;
	
	WebDriver driver = null;
	

	public ChannelUSerPINMgmtPage_1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterMSISDN(String mobileNumber){
		Log.info("Trying to enter MSISDN.");
		msisdn.sendKeys(mobileNumber);
		Log.info("Entered MSISDN is :: "+mobileNumber);
	}
	
	public void enterRemarks(String Remarks){
		Log.info("Trying to enter Remarks.");
		remarks.sendKeys(Remarks);
		Log.info("Entered remarks :: "+Remarks);
	}
	
	public void clickSubmitButton(){
		Log.info("Trying to click submit button");
		btnSubmit.click();
		Log.info("Submit button clicked successfuly");
	}
}
