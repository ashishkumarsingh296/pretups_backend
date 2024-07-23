package com.pageobjects.channeladminpages.autoO2CTransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateAutoO2CTransferPageIDEA2 {
	
	@ FindBy(name = "msisdn")
	private WebElement MSISDN;
	
	@ FindBy(name = "submit1")
	private WebElement AddModify;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	WebDriver driver= null;
	
	public InitiateAutoO2CTransferPageIDEA2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterMSISDN(String msisdn) throws InterruptedException {
		MSISDN.sendKeys(msisdn);
		Log.info("User entered MSISDN: "+msisdn);
	}
	
	public void clickAddModifyButton() {
		AddModify.click();
		Log.info("User clicked Add/Modify button");
	}
	
	public String getMessage(){
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
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully");
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
	
	

}
