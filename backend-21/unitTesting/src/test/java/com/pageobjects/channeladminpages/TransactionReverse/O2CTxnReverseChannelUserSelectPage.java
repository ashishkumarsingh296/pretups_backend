package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CTxnReverseChannelUserSelectPage {
	
	@ FindBy(name = "submitButton")
	private WebElement submitButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	@FindBy(name = "channelCategoryUserName")
	private WebElement channelCategoryUserName;
	
	@FindBy(name = "channelOwnerCategoryUserName")
	private WebElement channelOwnerCategoryUserName;
	
	
	WebDriver driver= null;

	public O2CTxnReverseChannelUserSelectPage(WebDriver driver) {
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
	
	boolean w1=false;
	public void EnterOwneruserName(String userName){
		try {
			Log.info("Trying to check if Channel Owner User Search field exists");
			w1 = channelOwnerCategoryUserName.isDisplayed();
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Channel Owner User search field not found");
		}
		if(w1){	
			Log.info("Trying to enter Channel Owner Name.");
			channelOwnerCategoryUserName.sendKeys(userName);
			Log.info("Channel Owner Name entered as :" +userName);}


	}
}
