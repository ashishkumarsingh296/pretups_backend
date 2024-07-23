package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class TxnRvrseO2CPage2 {
	
	@FindBy (name = "channelCategoryUserName")
	private WebElement channelCategoryUserName;
	
	@FindBy(name = "submitButton")
	private WebElement submitButton;


	@FindBy(name = "backbutton")
	private WebElement backButton;

	
	WebDriver driver = null;

	public TxnRvrseO2CPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void EnterChannelUserName(String UserName){
		channelCategoryUserName.sendKeys(UserName);
		
		Log.info("User entered channel user name as :" +UserName);
		
		
	}
	
	public void clickSubmit(){
		Log.info("User is trying to click submit button");
		submitButton.click();
		Log.info("User click submit button");
	}
	
	public void clickBack(){
		backButton.click();
		Log.info("User clicked back button");
	}
}
