package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CTxnReverseChannelUserSelectPage {
	
	@ FindBy(name = "submitButton")
	private WebElement submitButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	@FindBy(name = "channelCategoryUserName")
	private WebElement channelCategoryUserName;
	
	
	
	
	WebDriver driver= null;

	public C2CTxnReverseChannelUserSelectPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	
	
	
	public void clickSubmit(){
		Log.info("User is trying to click submit button");
		submitButton.click();
		Log.info("User click submit button");
	}
	
	public void clickBack(){
		Log.info("User is trying to click back button");
		backButton.click();
		Log.info("User click back button");
	}
	
	
	public void EnterChanneluserName(String userName){
		channelCategoryUserName.sendKeys(userName);
		
		Log.info("User entered channelCategoryUserName as :" +userName);
		
		
	}

}
