package com.pageobjects.networkadminpages.networkprefixes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class NetworkPrefixesPage1 {
	
	WebDriver driver;
	public NetworkPrefixesPage1(WebDriver driver) {
	this.driver = driver;
	PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "save" )
	private WebElement save;

	@FindBy(name = "reset" )
	private WebElement reset;
	
	@FindBy(name = "confirm" )
	private WebElement Confirm;

	@FindBy(name = "prepaidSeries" )
	private WebElement prepaidSeries;

	@FindBy(name = "postpaidSeries" )
	private WebElement postpaidSeries;

	@FindBy(name = "otherSeries" )
	private WebElement otherSeries;
	
	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	WebElement errorMessage;
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
		}catch(Exception e){
			Log.info("No error Message found: "+e);
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message found: "+e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}

	public void ClickOnsave(){
	Log.info("Trying to click on button  Save ");
	save.click();
	Log.info("Clicked on  Save successfully");
	}

	public void ClickOnConfirm(){
		Log.info("Trying to click on button  Confirm ");
		Confirm.click();
		Log.info("Clicked on Confirm successfully");
		}

	public void ClickOnreset(){
	Log.info("Trying to click on button  Reset ");
	reset.click();
	Log.info("Clicked on  Reset successfully");
	}

	public void EnterprepaidSeries(String value){
	Log.info("Trying to enter  value in prepaidSeries ");
	prepaidSeries.sendKeys(value);
	Log.info("Value entered: "+value);
	}

	public void EnterpostpaidSeries(String value){
	Log.info("Trying to enter  value in postpaid Series ");
	postpaidSeries.sendKeys(value);
	Log.info("Value entered: "+value);
	}

	public void EnterOtherSeries(String value){
	Log.info("Trying to enter  value in otherSeries ");
	otherSeries.sendKeys(value);
	Log.info("Value entered: "+value);
	}
}
