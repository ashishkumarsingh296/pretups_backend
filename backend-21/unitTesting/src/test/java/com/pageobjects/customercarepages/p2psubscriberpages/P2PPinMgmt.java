package com.pageobjects.customercarepages.p2psubscriberpages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class P2PPinMgmt {

	@FindBy(name="msisdn")
	private WebElement msisdn;
	
	@FindBy(name="btnSubmit")
	private WebElement submitBtn;
	
	@FindBy(name="btnSendPin")
	private WebElement sendPinBtn;
	
	@FindBy(name="btnResetPin")
	private WebElement resetPinBtn;
	
	WebDriver driver =null;
	
	public P2PPinMgmt(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterMSISDN(String subsMsisdn){
		Log.info("Trying to enter MSISDN: "+subsMsisdn);
		msisdn.sendKeys(subsMsisdn);
		Log.info("MSISDN entered successfully.");
	}
	
	public void clickSendPinBtn(){
		Log.info("Trying to click sendPIN button.");
		sendPinBtn.click();
		Log.info("SendPIN button clicked successfully.");
	}
	
	public void clicksubmitBtn(){
		Log.info("Trying to click submit button.");
		submitBtn.click();
		Log.info("Submit button clicked successfully");
	}
	
	public void clickResetPinBtn(){
		Log.info("Trying to click resetPIN button.");
		resetPinBtn.click();
		Log.info("ResetPIN button clicked successfully.");
	}
}
