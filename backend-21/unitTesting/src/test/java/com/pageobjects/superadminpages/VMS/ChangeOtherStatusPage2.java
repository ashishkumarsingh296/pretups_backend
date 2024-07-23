package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ChangeOtherStatusPage2 {

	WebDriver driver = null;
	public ChangeOtherStatusPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "changeotherStatus" )
	private WebElement changeotherStatus;
	
	@FindBy(name = "cancelOther" )
	private WebElement cancelOther;
	
	@FindBy(name = "backOther" )
	private WebElement backOther;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void ClickonConfirm(){
		Log.info("Trying to click on Confirm Button");
		changeotherStatus.click();
		Log.info("Clicked on Confirm Button successfully");
		}
	
	public void ClickonCancel(){
		Log.info("Trying to click on Cancel Button");
		cancelOther.click();
		Log.info("Clicked on Cancel Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		backOther.click();
		Log.info("Clicked on Back Button successfully");
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
		Log.info("Error Message fetched successfully as:"+ Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
}
