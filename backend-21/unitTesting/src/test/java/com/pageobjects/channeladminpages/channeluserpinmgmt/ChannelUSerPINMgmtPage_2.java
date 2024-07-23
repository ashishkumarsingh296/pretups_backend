package com.pageobjects.channeladminpages.channeluserpinmgmt;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ChannelUSerPINMgmtPage_2 {
	
	@FindBy(name="btnSendPin")
	private WebElement sendPIN;
	
	@FindBy(name="btnResetPin")
	private WebElement resetPIN;
	
	@FindBy(name="btnCancel")
	private WebElement cancelBtn;
	
	@FindBy(name="btnBack")
	private WebElement backBtn;
	
	WebDriver driver = null;
	

	public ChannelUSerPINMgmtPage_2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickSendPinButton(){
		Log.info("Trying to click SendPIN button.");
		sendPIN.click();
		Log.info("Send PIN button clicked successfuly.");
	}
	
	public void clickResetPinButton(){
		Log.info("Trying to click ResetPIN button.");
		resetPIN.click();
		Log.info("Reset PIN button clicked successfuly.");
	}
	
	public void clickBackButton(){
		Log.info("Trying to click back button.");
		backBtn.click();
		Log.info("Back button clicked successfuly.");
	}
	
	public void clickCancelButton(){
		Log.info("Trying to click confirm button.");
		cancelBtn.click();
		Log.info("Cancel button clicked successfuly.");
	}
}
