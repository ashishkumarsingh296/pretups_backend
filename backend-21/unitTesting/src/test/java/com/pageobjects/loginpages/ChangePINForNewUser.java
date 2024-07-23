/*
 * 
 */
package com.pageobjects.loginpages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 * This class is created to Change First Time Pin of Users.
 */
public class ChangePINForNewUser {

	@FindBy(name="searchLoginId")
	private WebElement chngPINLoginID;
	
	@FindBy(name="searchMsisdn")
	private WebElement chngPINMsisdn;
	
	@FindBy(id="eventRemarks")
	private WebElement eventRemarks;
	
	@FindBy(name="submit1")
	private WebElement submitBtn;
	
	@FindBy(name="msisdnListIndexed[0].multiBox")
	private WebElement checkBox;
	
	@FindBy(xpath="//input[@name[contains(.,'oldSmsPin')]]")
	private WebElement oldPIN;
	
	@FindBy(xpath="//input[@name[contains(.,'showSmsPin')]]")
	private WebElement newPIN;
	
	@FindBy(xpath="//input[@name[contains(.,'confirmSmsPin')]]")
	private WebElement confirmPIN;
	
	@FindBy(name="changePin")
	private WebElement submitChangePINBtn;
	
	@FindBy(name="changeSmsPin")
	private WebElement confirmBtn;
	
	WebDriver driver=null;
	
	public ChangePINForNewUser(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterLoginIDandRemarks(String LoginID){
		Log.info("Trying to enter LoginID for which PIN is to be changed.");
		chngPINLoginID.sendKeys(LoginID);
		Log.info("LoginID entered successfully.["+LoginID+"]");
		Log.info("Trying to enter Remarks");
		eventRemarks.sendKeys("Remarks entered");
		Log.info("Remarks entered successfully.");
		Log.info("Trying to click submit button");
		submitBtn.click();
		Log.info("Submit button clicked successfully");
	}
	
	public void enterMSISDNandRemarks(String MSISDN){
		Log.info("Trying to enter MSISDN for which PIN is to be changed.");
		chngPINMsisdn.sendKeys(MSISDN);
		Log.info("MSISDN entered successfully.["+MSISDN+"]");
		Log.info("Trying to enter Remarks");
		eventRemarks.sendKeys("Remarks entered");
		Log.info("Remarks entered successfully.");
		Log.info("Trying to click submit button");
		submitBtn.click();
		Log.info("Submit button clicked successfully");
	}
	
	
	public void changePIN(String Pin, String NewPin, String ConfirmPin){
		//try catch removed from this method to get actual failure
		
		Log.info("Trying to enter Old Pin");	
		oldPIN.sendKeys(Pin);
		Log.info("Old Pin Entered successfully.["+Pin+"]");
		Log.info("Trying to enter New Pin");
		newPIN.sendKeys(NewPin);
		Log.info("New Pin Entered successfully.["+NewPin+"]");
		Log.info("Trying to enter Confirm Pin");
		confirmPIN.sendKeys(ConfirmPin);
		Log.info("Confirm Pin Entered successfully.["+ConfirmPin+"]");
		Log.info("Trying to click CheckBox");
		checkBox.click();
		Log.info("CheckBox clicked successfully.");
		
		Log.info("Trying to enter Remarks");
		eventRemarks.sendKeys("Remarks entered for Change PIN");
		Log.info("Remarks entered successfully.");
		
		Log.info("Trying to click Submit Button");
		submitChangePINBtn.click();
		Log.info("Submit Button clicked successfully.");
		Log.info("Trying to click Confirm Button");
		confirmBtn.click();
		Log.info("Confirm Button clicked successfully.");
		
	}
	
	
}
