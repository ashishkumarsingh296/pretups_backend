package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewActiveProfile {

	WebDriver driver = null;
	public ViewActiveProfile(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "applicableFrom" )
	private WebElement applicableON;
	
//	@FindBy(name = "viewActiveSbmt" )
//	private WebElement submit;
	
	@FindBy ( xpath ="//input[@name='viewActiveSbmt' and @type='submit']")
	private WebElement submit;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public String getSuccessMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage() {
		String errormessage =null;
		Log.info("Trying to fetch Message");
		try {
			errormessage =errorMessage.getText();
			Log.info("Error Message fetched successfully as: " + errormessage);
		}
		catch(Exception e){
			Log.info("Error Message not found");
		}
		
		return errormessage;
	}
	
	public void clickSubmit() {
		Log.info("Trying to click on Submit button ");
		submit.click();
		Log.info("Clicked on Submit successfully");
	}
	
	public void enterApplicableOn(String value) {
		Log.info("Trying to Enter Applicable Date");
		try {
			applicableON.clear();
			applicableON.sendKeys(value);
		}
		catch(Exception e) {
			Log.info("Applicable date not entered");
		}
		Log.info("Applicable date entered successfully as: "+value);
	}
}
