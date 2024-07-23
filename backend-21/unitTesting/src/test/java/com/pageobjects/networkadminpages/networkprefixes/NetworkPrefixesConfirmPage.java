package com.pageobjects.networkadminpages.networkprefixes;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class NetworkPrefixesConfirmPage {
	WebDriver driver;
	public NetworkPrefixesConfirmPage(WebDriver driver) {
	this.driver = driver;
	PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "confirm" )
	private WebElement confirm;

	@FindBy(name = "cancel" )
	private WebElement cancel;
	
	@FindBy(name = "back" )
	private WebElement back;
	
	public void clickOnConfirmButton(){
		Log.info("Trying to click on Confirm Button");
		try{
		confirm.click();
		}catch(Exception e){
			Log.info("Confirm Button not found"+e);
		}
		Log.info("Clicked on Confirm successfully");
		}
	
	public void clickOnCancelButton(){
		Log.info("Trying to click on Cancel Button");
		cancel.click();
		Log.info("Clicked on cancel successfully");
		}
	
	public void clickOnBackButton(){
		Log.info("Trying to click on Back Button");
		back.click();
		Log.info("Clicked on Back successfully");
		}
}
