package com.pageobjects.channeladminpages.autoO2CTransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class Approval3AutoO2CTransferPage3 {
	
	@ FindBy(name = "approve")
	private WebElement approveButton;
	
	@ FindBy(name = "reject")
	private WebElement rejectButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
    WebDriver driver= null;
	
	public Approval3AutoO2CTransferPage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
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
	
	public void clickApproveButton() {
		approveButton.click();
		Log.info("User clicked Submit button");
	}
	
	public void clickResetButton() {
		rejectButton.click();
		Log.info("User clicked Submit button");
	}
	
	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}

}
